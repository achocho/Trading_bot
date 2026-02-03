package com.adrian.tradingbot.persistence;

import java.math.BigDecimal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PortfolioRepository {
    private final JdbcTemplate jdbc;

    public PortfolioRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public BigDecimal getCash() {
        return jdbc.queryForObject("select cash from portfolio where id=1", BigDecimal.class);
    }

    public void setCash(BigDecimal newCash) {
        jdbc.update("update portfolio set cash=? where id=1", newCash);
    }
}
