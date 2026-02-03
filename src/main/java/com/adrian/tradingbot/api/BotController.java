package com.adrian.tradingbot.api;

import org.springframework.web.bind.annotation.*;

import com.adrian.tradingbot.api.TrainingResult;
import com.adrian.tradingbot.bot.BotStateService;
import com.adrian.tradingbot.bot.TradingService;
import com.adrian.tradingbot.bot.TradingScheduler;

@RestController
public class BotController {

    private final BotStateService botState;
    private final TradingService tradeService;
    private final TradingScheduler scheduler;

    public BotController(BotStateService botState, TradingService tradeService, TradingScheduler scheduler) {
        this.botState = botState;
        this.tradeService = tradeService;
        this.scheduler = scheduler;
    }

    @PostMapping("/api/bot/start")
    public String start() {
        botState.setRunning(true);
        return "OK";
    }

    @PostMapping("/api/bot/pause")
    public String pause() {
        botState.setRunning(false);
        return "OK";
    }

    @PostMapping("/api/bot/reset")
    public String reset() {
        botState.setRunning(false);
        tradeService.reset();
        return "OK";
    }

    @PostMapping("/api/bot/mode")
    public String mode(@RequestParam String mode) {
        // TRAINING / TRADING
        botState.setMode(mode);
        return "OK";
    }

    @PostMapping("/api/bot/params")
    public String params(@RequestParam String symbol,
                         @RequestParam String interval,
                         @RequestParam int limit) {
        scheduler.setParams(symbol, interval, limit);
        return "OK";
    }

    @GetMapping("/api/bot/status")
    public String status() {
        return "mode=" + botState.getMode() + ", running=" + botState.isRunning();
    }
    
    @GetMapping("/api/bot/train")
    public TrainingResult train(@RequestParam String symbol,
                                @RequestParam String interval,
                                @RequestParam int limit,
                                @RequestParam int fast,
                                @RequestParam int slow) {
        // Pause bot before training so scheduler doesn't fight DB
        botState.setRunning(false);
        return tradeService.train(symbol, interval, limit, fast, slow);
    }
}
