package com.adrian.tradingbot.bot;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TradingScheduler {

    private final BotStateService botState;
    private final TradingService tradeService;

    // hard-coded defaults for demo (front-end can change later)
    private volatile String symbol = "BTCUSDT";
    private volatile String interval = "1m";
    private volatile int limit = 200;

    public TradingScheduler(BotStateService botState, TradingService tradeService) {
        this.botState = botState;
        this.tradeService = tradeService;
    }

    public void setParams(String symbol, String interval, int limit) {
        this.symbol = symbol;
        this.interval = interval;
        this.limit = limit;
    }

    @Scheduled(fixedDelay = 5000)
    public void tick() {
        if (!botState.isRunning()) return;

        try {
            // For now we run the same engine for both modes.
            // "TRAINING" can be implemented as a separate endpoint that iterates historical candles.
            tradeService.executeTrade(symbol, interval, limit);
        } catch (Exception ex) {
            System.out.println("Scheduled trade tick failed: " + ex.getMessage());
        }
    }
}
