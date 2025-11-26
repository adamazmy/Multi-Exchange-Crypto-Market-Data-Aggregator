package com.adamazmy.cryptoaggregator.exchange.binance;

import com.adamazmy.cryptoaggregator.model.dto.PriceUpdateDto;
import com.adamazmy.cryptoaggregator.service.AggregatorService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Random;

/**
 * Mock Binance price stream client.
 *
 * <p>This component simulates a live Binance WebSocket stream by periodically
 * generating random BTCUSDT prices and pushing them into the {@link AggregatorService}.
 * It is used to validate the wiring and aggregation logic before integrating
 * with the real Binance API.</p>
 */
@Component
public class BinancePriceStreamClient {

    private static final Logger log = LoggerFactory.getLogger(BinancePriceStreamClient.class);

    private static final String EXCHANGE_NAME = "BINANCE";
    private static final String SYMBOL = "BTCUSDT";

    private final AggregatorService aggregatorService;
    private final Random random = new Random();

    public BinancePriceStreamClient(AggregatorService aggregatorService) {
        this.aggregatorService = aggregatorService;
    }

    /**
     * Starts a background thread that periodically generates mock price updates.
     *
     * <p>In a later iteration this method will be replaced with a real WebSocket
     * connection to Binance's market data API.</p>
     */
    @PostConstruct
    public void startMockStream() {
        Thread thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // Generate a pseudo-random price around a base value.
                    BigDecimal price = BigDecimal.valueOf(65000 + random.nextInt(2000));

                    PriceUpdateDto update = new PriceUpdateDto(
                            EXCHANGE_NAME,
                            SYMBOL,
                            price,
                            Instant.now()
                    );

                    aggregatorService.updatePrice(update);
                    log.info("Mock Binance update: {}", update);

                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("Binance mock stream interrupted, stopping thread", e);
                } catch (Exception e) {
                    log.error("Error in Binance mock stream", e);
                }
            }
        }, "binance-mock-stream");

        thread.setDaemon(true);
        thread.start();
    }
}
