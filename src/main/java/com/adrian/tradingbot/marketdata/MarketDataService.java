package com.adrian.tradingbot.marketdata;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.adrian.tradingbot.model.Candle;

@Service
public class MarketDataService {
    private final WebClient webClient;

    public MarketDataService(WebClient binanceWebClient) {
        this.webClient = binanceWebClient;
    }

    @SuppressWarnings("unchecked")
    public List<Candle> getRecentCandles(String symbol, String interval, int limit) {
        List<List<Object>> raw = webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/v3/klines")
                .queryParam("symbol", symbol)
                .queryParam("interval", interval)
                .queryParam("limit", limit)
                .build())
            .retrieve()
            .bodyToMono(List.class)
            .block();

        return parseKlines(raw);
    }

    private List<Candle> parseKlines(List<List<Object>> raw) {
        if (raw == null) return List.of();

        List<Candle> candles = new ArrayList<>(raw.size());
        for (List<Object> k : raw) {
            long openTimeMs = ((Number) k.get(0)).longValue();
            BigDecimal open  = new BigDecimal((String) k.get(1));
            BigDecimal high  = new BigDecimal((String) k.get(2));
            BigDecimal low   = new BigDecimal((String) k.get(3));
            BigDecimal close = new BigDecimal((String) k.get(4));
            BigDecimal vol   = new BigDecimal((String) k.get(5));
            candles.add(new Candle(Instant.ofEpochMilli(openTimeMs), open, high, low, close, vol));
        }
        return candles;
    }
}
