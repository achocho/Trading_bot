package com.adrian.tradingbot.bot;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class BotStateService {

    private final JdbcTemplate jdbc;

    public BotStateService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public String getMode() {
        return jdbc.queryForObject("select mode from bot_state where id=1", String.class);
    }

    public boolean isRunning() {
        Boolean running = jdbc.queryForObject("select running from bot_state where id=1", Boolean.class);
        return running != null && running;
    }

    public void setRunning(boolean running) {
        jdbc.update("update bot_state set running=? where id=1", running);
    }

    public void setMode(String mode) { // "TRAINING" or "TRADING"
        jdbc.update("update bot_state set mode=? where id=1", mode);
    }
}
