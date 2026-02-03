package com.adrian.tradingbot.bot;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.adrian.tradingbot.api.TrainingResult;
import com.adrian.tradingbot.marketdata.MarketDataService;
import com.adrian.tradingbot.model.Candle;
import com.adrian.tradingbot.model.Holding;
import com.adrian.tradingbot.model.Trade;
import com.adrian.tradingbot.persistence.HoldingsRepository;
import com.adrian.tradingbot.persistence.PortfolioRepository;
import com.adrian.tradingbot.persistence.TradeRepository;

@Service
public class TradingService {

    private final SignalService signalService;
    private final MarketDataService marketDataService;
    private final PortfolioRepository portfolioRepository;
    private final HoldingsRepository holdingsRepository;
    private final TradeRepository tradeRepository;

    private final BigDecimal feeRate = new BigDecimal("0.001"); // 0.1%

    public TradingService(SignalService signalService,
                        MarketDataService marketDataService,
                        PortfolioRepository portfolioRepository,
                        HoldingsRepository holdingsRepository,
                        TradeRepository tradeRepository) {
        this.signalService = signalService;
        this.marketDataService = marketDataService;
        this.portfolioRepository = portfolioRepository;
        this.holdingsRepository = holdingsRepository;
        this.tradeRepository = tradeRepository;
    }

    @Transactional
    public void executeTrade(String symbol, String interval, int limit) {

        String decision = signalService.decideBuyOrSell(symbol, interval, limit);
        if ("HOLD".equals(decision)) return;

        List<Candle> candles = marketDataService.getRecentCandles(symbol, interval, limit);
        if (candles.size() < 3) return;

        BigDecimal price = candles.get(candles.size() - 2).getClose(); // last closed candle close

        Optional<Holding> holdingOpt = holdingsRepository.findBySymbol(symbol);
        boolean holds = holdingOpt.isPresent() && holdingOpt.get().getQty().compareTo(BigDecimal.ZERO) > 0;

        if ("BUY".equals(decision)) {
            if (holds) return;

            BigDecimal cash = portfolioRepository.getCash();
            BigDecimal spend = cash.multiply(new BigDecimal("0.10"));
            if (spend.compareTo(new BigDecimal("1")) < 0) return; // too small

            BigDecimal fee = spend.multiply(feeRate).setScale(8, RoundingMode.HALF_UP);
            BigDecimal net = spend.subtract(fee);

            BigDecimal qty = net.divide(price, 8, RoundingMode.HALF_UP);
            if (qty.compareTo(BigDecimal.ZERO) <= 0) return;

            portfolioRepository.setCash(cash.subtract(spend).setScale(8, RoundingMode.HALF_UP));
            holdingsRepository.upsert(symbol, qty, price.setScale(8, RoundingMode.HALF_UP));

            tradeRepository.insert(new Trade(Instant.now(), symbol, "BUY", qty, price, fee, BigDecimal.ZERO));
            return;
        }

        if ("SELL".equals(decision)) {
            if (!holds) return;

            Holding h = holdingOpt.get();
            BigDecimal qty = h.getQty();

            BigDecimal gross = qty.multiply(price).setScale(8, RoundingMode.HALF_UP);
            BigDecimal fee = gross.multiply(feeRate).setScale(8, RoundingMode.HALF_UP);
            BigDecimal net = gross.subtract(fee);

            BigDecimal pnl = price.subtract(h.getAvgPrice()).multiply(qty).subtract(fee).setScale(8, RoundingMode.HALF_UP);

            BigDecimal cash = portfolioRepository.getCash();
            portfolioRepository.setCash(cash.add(net).setScale(8, RoundingMode.HALF_UP));
            holdingsRepository.delete(symbol);

            tradeRepository.insert(new Trade(Instant.now(), symbol, "SELL", qty, price, fee, pnl));
        }
    }

    @Transactional
    public void reset() {
        // wipe trades & holdings, reset cash
        tradeRepository.deleteAll();
        holdingsRepository.findAll().forEach(h -> holdingsRepository.delete(h.getSymbol()));
        portfolioRepository.setCash(new BigDecimal("100000.00000000"));
    }
    
    @Transactional
    public TrainingResult train(String symbol, String interval, int limit, int fast, int slow) {

        // reset DB state for backtest
        tradeRepository.deleteAll();
        holdingsRepository.deleteAll();
        portfolioRepository.setCash(new BigDecimal("100000.00000000"));

        BigDecimal startingCash = portfolioRepository.getCash();

        List<Candle> candles = marketDataService.getRecentCandles(symbol, interval, limit);
        if (candles.size() < slow + 5) {
            return new TrainingResult(0, startingCash, startingCash, BigDecimal.ZERO);
        }

        BigDecimal cash = startingCash;
        BigDecimal qty = BigDecimal.ZERO;
        BigDecimal avgPrice = BigDecimal.ZERO;

        int tradesCount = 0;

        // last closed candle index 
        int lastIndex = candles.size() - 2;

        // start where MA windows are possible
        int start = slow + 2;

        for (int i = start; i <= lastIndex; i++) {

            // crossover check uses previous closed vs current closed
            int prev = i - 1;

            BigDecimal pFast = ma(candles, prev, fast);
            BigDecimal pSlow = ma(candles, prev, slow);
            BigDecimal cFast = ma(candles, i, fast);
            BigDecimal cSlow = ma(candles, i, slow);

            String decision = "HOLD";
            if (pFast.compareTo(pSlow) <= 0 && cFast.compareTo(cSlow) > 0) decision = "BUY";
            if (pFast.compareTo(pSlow) >= 0 && cFast.compareTo(cSlow) < 0) decision = "SELL";

            BigDecimal price = candles.get(i).getClose();

            boolean holds = qty.compareTo(BigDecimal.ZERO) > 0;

            if ("BUY".equals(decision) && !holds) {

                BigDecimal spend = cash.multiply(new BigDecimal("0.10"));
                if (spend.compareTo(new BigDecimal("1")) < 0) continue;

                BigDecimal fee = spend.multiply(feeRate).setScale(8, RoundingMode.HALF_UP);
                BigDecimal net = spend.subtract(fee);

                BigDecimal boughtQty = net.divide(price, 8, RoundingMode.HALF_UP);

                cash = cash.subtract(spend).setScale(8, RoundingMode.HALF_UP);
                qty = boughtQty;
                avgPrice = price.setScale(8, RoundingMode.HALF_UP);

                tradeRepository.insert(new Trade(candles.get(i).getOpenTime(), symbol, "BUY", boughtQty, price, fee, BigDecimal.ZERO));
                tradesCount++;
            }

            if ("SELL".equals(decision) && holds) {

                BigDecimal gross = qty.multiply(price).setScale(8, RoundingMode.HALF_UP);
                BigDecimal fee = gross.multiply(feeRate).setScale(8, RoundingMode.HALF_UP);
                BigDecimal net = gross.subtract(fee);

                BigDecimal pnl = price.subtract(avgPrice).multiply(qty).subtract(fee).setScale(8, RoundingMode.HALF_UP);

                cash = cash.add(net).setScale(8, RoundingMode.HALF_UP);

                tradeRepository.insert(new Trade(candles.get(i).getOpenTime(), symbol, "SELL", qty, price, fee, pnl));
                tradesCount++;

                qty = BigDecimal.ZERO;
                avgPrice = BigDecimal.ZERO;
            }
        }

        // persist final state
        portfolioRepository.setCash(cash);
        if (qty.compareTo(BigDecimal.ZERO) > 0) {
            holdingsRepository.upsert(symbol, qty, avgPrice);
        }

        BigDecimal lastPrice = candles.get(lastIndex).getClose();
        BigDecimal equity = cash.add(qty.multiply(lastPrice)).setScale(8, RoundingMode.HALF_UP);

        BigDecimal retPct = equity.subtract(startingCash)
            .divide(startingCash, 8, RoundingMode.HALF_UP)
            .multiply(new BigDecimal("100"))
            .setScale(4, RoundingMode.HALF_UP);

        return new TrainingResult(tradesCount, cash, equity, retPct);
    }

    // helper MA for training 
    private BigDecimal ma(List<Candle> candles, int endIndex, int period) {
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = endIndex; i > endIndex - period; i--) {
            sum = sum.add(candles.get(i).getClose());
        }
        return sum.divide(BigDecimal.valueOf(period), 8, RoundingMode.HALF_UP);
    }
}
