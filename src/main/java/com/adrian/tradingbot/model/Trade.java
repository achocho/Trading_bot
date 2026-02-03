package com.adrian.tradingbot.model;

import java.math.BigDecimal;
import java.time.Instant;

public class Trade {
    private final long id;
    private final Instant ts;
    private final String symbol;
    private final String side; 
    private final BigDecimal qty;
    private final BigDecimal price;
    private final BigDecimal fee;
    private final BigDecimal pnl;

    public Trade(long id, Instant ts, String symbol, String side,
                 BigDecimal qty, BigDecimal price, BigDecimal fee, BigDecimal pnl) {
        this.id = id;
        this.ts = ts;
        this.symbol = symbol;
        this.side = side;
        this.qty = qty;
        this.price = price;
        this.fee = fee;
        this.pnl = pnl;
    }

    
    public Trade(Instant ts, String symbol, String side,
                 BigDecimal qty, BigDecimal price, BigDecimal fee, BigDecimal pnl) {
        this(0L, ts, symbol, side, qty, price, fee, pnl);
    }

    public long getId() { return id; }
    public Instant getTs() { return ts; }
    public String getSymbol() { return symbol; }
    public String getSide() { return side; }
    public BigDecimal getQty() { return qty; }
    public BigDecimal getPrice() { return price; }
    public BigDecimal getFee() { return fee; }
    public BigDecimal getPnl() { return pnl; }
}
