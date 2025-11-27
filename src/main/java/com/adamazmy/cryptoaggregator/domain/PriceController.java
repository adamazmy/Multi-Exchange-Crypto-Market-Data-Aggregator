package com.adamazmy.cryptoaggregator.domain;

import com.adamazmy.cryptoaggregator.model.dto.PriceUpdateDto;
import com.adamazmy.cryptoaggregator.service.AggregatorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/prices") //Create a Resource Mapping
public class PriceController {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PriceController.class);

    private final AggregatorService aggregatorService;

    public PriceController(AggregatorService aggregatorService) {
        this.aggregatorService = aggregatorService;
    }

    /**
     * Returns the latest known prices for a given trading symbol across all exchanges.
     *
     * <p>The symbol is provided as a path variable and treated as a case-insensitive
     * identifier for a market instrument (e.g. BTCUSDT). The returned map contains
     * one {@link PriceUpdateDto} per exchange that has reported a price.</p>
     *
     * <p>Example request:
     * <pre>
     *   GET /api/prices/BTCUSDT/latest
     * </pre>
     * </p>
     *
     * @param symbol the trading symbol to query (e.g. "BTCUSDT")
     * @return a map of exchange identifier &rarr; latest {@link PriceUpdateDto}, or an empty map if no data exists
     */

    @GetMapping("/{symbol}/latest")
    public Map<String, PriceUpdateDto> getPrice(@PathVariable("symbol") String symbol) {
        if ((symbol == null) || symbol.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Symbol cannot be empty");
        }
        return aggregatorService.getLatestForSymbol(symbol);
    }
}
