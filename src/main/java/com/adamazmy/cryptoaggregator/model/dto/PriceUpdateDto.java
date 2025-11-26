package com.adamazmy.cryptoaggregator.model.dto;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Data transfer object representing a normalised price update from an exchange.
 *
 * <p>All exchange-specific payloads are converted into this canonical shape so the
 * rest of the system can work with a consistent model.</p>
 *
 * @param exchange  the source exchange identifier (e.g. "BINANCE", "COINBASE")
 * @param symbol    the normalised trading symbol (e.g. "BTCUSDT")
 * @param price     the latest traded or quoted price for the symbol on the exchange
 * @param timestamp the time at which this price was observed or processed
 */

public record PriceUpdateDto(
        String exchange,
        String symbol,
        BigDecimal price,
        Instant timestamp
) {}
