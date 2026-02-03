package com.adrian.tradingbot.model;

import java.math.BigDecimal;

public class Holding {
    private final String symbol;
    private final BigDecimal qty;
    private final BigDecimal avgPrice;

    public Holding(String symbol, BigDecimal qty, BigDecimal avgPrice) {
        this.symbol = symbol;
        this.qty = qty;
        this.avgPrice = avgPrice;
    }

    public String getSymbol() { return symbol; }
    public BigDecimal getQty() { return qty; }
    public BigDecimal getAvgPrice() { return avgPrice; }
}
