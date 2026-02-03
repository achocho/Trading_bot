package com.adrian.tradingbot.portfolio;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.adrian.tradingbot.model.Holding;
import com.adrian.tradingbot.persistence.HoldingsRepository;
import com.adrian.tradingbot.persistence.PortfolioRepository;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final HoldingsRepository holdingsRepository;

    public PortfolioService(PortfolioRepository portfolioRepository, HoldingsRepository holdingsRepository) {
        this.portfolioRepository = portfolioRepository;
        this.holdingsRepository = holdingsRepository;
    }

    public PortfolioSnapshot getCurrentPortfolio() {
        BigDecimal cash = portfolioRepository.getCash();
        List<Holding> holdings = holdingsRepository.findAll();
        return new PortfolioSnapshot(cash, holdings);
    }
}
