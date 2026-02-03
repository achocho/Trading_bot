package com.adrian.tradingbot.portfolio;

import java.math.BigDecimal;
import java.util.List;

import com.adrian.tradingbot.model.Holding;

public class PortfolioSnapshot {
    private final BigDecimal cash;
    private final List<Holding> holdings;

    public PortfolioSnapshot(BigDecimal cash, List<Holding> holdings) {
        this.cash = cash;
        this.holdings = holdings;
    }

    public BigDecimal getCash() { return cash; }
    public List<Holding> getHoldings() { return holdings; }
}
