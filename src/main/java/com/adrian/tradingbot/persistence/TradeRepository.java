package com.adrian.tradingbot.persistence;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.adrian.tradingbot.model.Trade;

@Repository
public class TradeRepository {
    private final JdbcTemplate jdbc;

    public TradeRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Trade> findAll() {
        return jdbc.query("""
            select id, ts, symbol, side, qty, price, fee, pnl
            from trades
            order by ts asc
        """, (rs, n) -> new Trade(
            rs.getLong("id"),
            rs.getTimestamp("ts").toInstant(),
            rs.getString("symbol"),
            rs.getString("side"),
            rs.getBigDecimal("qty"),
            rs.getBigDecimal("price"),
            rs.getBigDecimal("fee"),
            rs.getBigDecimal("pnl")
        ));
    }

    public void insert(Trade trade) {
        jdbc.update("""
            insert into trades(ts, symbol, side, qty, price, fee, pnl)
            values (?, ?, ?, ?, ?, ?, ?)
        """,
            Timestamp.from(trade.getTs() == null ? Instant.now() : trade.getTs()),
            trade.getSymbol(),
            trade.getSide(),
            trade.getQty(),
            trade.getPrice(),
            trade.getFee(),
            trade.getPnl()
        );
    }

    public void deleteAll() {
        jdbc.update("delete from trades");
    }
}
