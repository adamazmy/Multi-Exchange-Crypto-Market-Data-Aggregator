package com.adamazmy2.cryptoaggregator.model.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record PriceUpdateDto(
        String exchange,
        String symbol,
        BigDecimal price,
        Instant timestamp
) {}
