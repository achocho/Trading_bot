package com.adrian.tradingbot.api;

import java.math.BigDecimal;

public class TrainingResult {
    private final int trades;
    private final BigDecimal finalCash;
    private final BigDecimal finalEquity;
    private final BigDecimal returnPct;

    public TrainingResult(int trades, BigDecimal finalCash, BigDecimal finalEquity, BigDecimal returnPct) {
        this.trades = trades;
        this.finalCash = finalCash;
        this.finalEquity = finalEquity;
        this.returnPct = returnPct;
    }

    public int getTrades() { return trades; }
    public BigDecimal getFinalCash() { return finalCash; }
    public BigDecimal getFinalEquity() { return finalEquity; }
    public BigDecimal getReturnPct() { return returnPct; }
}
