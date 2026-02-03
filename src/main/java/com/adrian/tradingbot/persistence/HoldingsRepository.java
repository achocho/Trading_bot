package com.adrian.tradingbot.persistence;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.adrian.tradingbot.model.Holding;

@Repository
public class HoldingsRepository {
    private final JdbcTemplate jdbc;

    public HoldingsRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Holding> findAll() {
        return jdbc.query(
            "select symbol, qty, avg_price from holdings order by symbol asc",
            (rs, n) -> new Holding(
                rs.getString("symbol"),
                rs.getBigDecimal("qty"),
                rs.getBigDecimal("avg_price")
            )
        );
    }

    public Optional<Holding> findBySymbol(String symbol) {
        List<Holding> rows = jdbc.query(
            "select symbol, qty, avg_price from holdings where symbol=?",
            (rs, n) -> new Holding(
                rs.getString("symbol"),
                rs.getBigDecimal("qty"),
                rs.getBigDecimal("avg_price")
            ),
            symbol
        );
        return rows.isEmpty() ? Optional.empty() : Optional.of(rows.get(0));
    }

    public void upsert(String symbol, BigDecimal qty, BigDecimal avgPrice) {
        jdbc.update("""
            insert into holdings(symbol, qty, avg_price)
            values (?, ?, ?)
            on conflict (symbol) do update set qty=excluded.qty, avg_price=excluded.avg_price
        """, symbol, qty, avgPrice);
    }

    public void delete(String symbol) {
        jdbc.update("delete from holdings where symbol=?", symbol);
    }
    
    public void deleteAll() {
        jdbc.update("delete from holdings");
    }

}
