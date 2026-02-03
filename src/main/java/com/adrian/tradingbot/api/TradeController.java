package com.adrian.tradingbot.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adrian.tradingbot.model.Trade;
import com.adrian.tradingbot.persistence.TradeRepository;

@RestController
public class TradeController {

    private final TradeRepository tradeRepository;

    public TradeController(TradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    @GetMapping("/api/trades")
    public List<Trade> getTrades() {
        return tradeRepository.findAll();
    }
}
