package com.adrian.tradingbot.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adrian.tradingbot.portfolio.PortfolioService;
import com.adrian.tradingbot.portfolio.PortfolioSnapshot;

@RestController
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping("/api/portfolio")
    public PortfolioSnapshot portfolio() {
        return portfolioService.getCurrentPortfolio();
    }
}
