package com.adrian.tradingbot.bot;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;

import com.adrian.tradingbot.marketdata.MarketDataService;
import com.adrian.tradingbot.model.Candle;

@Service
public class SignalService {

    private final MarketDataService marketData;

    public SignalService(MarketDataService marketData) {
        this.marketData = marketData;
    }

    public String decideBuyOrSell(String symbol, String interval, int limit) {
        List<Candle> candles = marketData.getRecentCandles(symbol, interval, limit);
        if (candles.size() < 35) return "HOLD";

        int fast = 10;
        int slow = 30;

        int lastClosed = candles.size() - 2;
        int prevClosed = candles.size() - 3;

        BigDecimal pFast = ma(candles, prevClosed, fast);
        BigDecimal pSlow = ma(candles, prevClosed, slow);
        BigDecimal cFast = ma(candles, lastClosed, fast);
        BigDecimal cSlow = ma(candles, lastClosed, slow);

        if (pFast.compareTo(pSlow) <= 0 && cFast.compareTo(cSlow) > 0) return "BUY";
        if (pFast.compareTo(pSlow) >= 0 && cFast.compareTo(cSlow) < 0) return "SELL";
        return "HOLD";
    }

    private BigDecimal ma(List<Candle> candles, int endIndex, int period) {
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = endIndex; i > endIndex - period; i--) {
            sum = sum.add(candles.get(i).getClose());
        }
        return sum.divide(BigDecimal.valueOf(period), 8, RoundingMode.HALF_UP);
    }
}
