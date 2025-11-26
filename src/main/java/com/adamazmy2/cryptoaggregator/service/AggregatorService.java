package com.adamazmy2.cryptoaggregator.service;

import org.springframework.stereotype.Service;
import com.adamazmy2.cryptoaggregator.model.dto.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Aggregates and stores the latest price updates received from multiple exchanges.
 *
 * <p>This service maintains an in-memory, thread-safe structure that maps:
 * <pre>
 *   symbol -> (exchange -> latest PriceUpdateDto)
 * </pre>
 *
 * It allows different exchange connector components (e.g., Binance, Coinbase)
 * to push real-time price updates, while API layers (REST/WebSocket) can fetch
 * the current state for any symbol.
 *
 * <p>Design characteristics:
 * <ul>
 *   <li>Backed by {@link ConcurrentHashMap} for safe concurrent read/write access.</li>
 *   <li>Stores <b>only</b> the latest price per exchange—not historical data.</li>
 *   <li>Acts as the "in-memory database" for the real-time aggregator.</li>
 * </ul>
 *
 * <p>This service is central to the system and functions similarly to an internal
 * market-data cache found in trading or low-latency systems.
 */

@SuppressWarnings("ALL")
@Service
public class AggregatorService{

    /**
     * Stores the latest price per symbol and exchange.
     *
     * Structure:
     * <pre>
     *   BTCUSDT -> { BINANCE -> PriceUpdateDto, COINBASE -> PriceUpdateDto }
     *   ETHUSDT -> { BINANCE -> PriceUpdateDto }
     * </pre>
     *
     * Using ConcurrentHashMap ensures safe concurrent updates from multiple
     * exchange streams and concurrent reads from REST/WebSocket endpoints.
     */
    private final Map<String, Map<String, PriceUpdateDto>> latestPrices = new ConcurrentHashMap<>();

    /**
     * Updates the in-memory store with a new price update.
     *
     * <p>If this is the first time seeing the symbol, a new exchange map is created.
     * If the symbol already exists, the update overwrites the previous price for
     * that specific exchange.
     *
     * @param update the structured price update received from an exchange connector
     */
    public void updatePrice(PriceUpdateDto update) {
        latestPrices
                .computeIfAbsent(update.symbol(), s -> new ConcurrentHashMap<>())
                .put(update.exchange(), update);
    }

    /**
     * Retrieves the latest price snapshot for all exchanges for a given symbol.
     *
     * @param symbol the trading pair symbol (e.g., "BTCUSDT")
     * @return a map of exchange -> PriceUpdateDto, or an empty map if symbol is unknown
     */
    public Map<String, PriceUpdateDto> getLatestForSymbol(String symbol) {
        return latestPrices.getOrDefault(symbol, Map.of());
    }

}
