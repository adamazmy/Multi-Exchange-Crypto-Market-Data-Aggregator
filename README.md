# Multi-Exchange Crypto Market Data Aggregator

A Spring Boot service intended to collect live prices from several cryptocurrency exchanges and serve them through one API. It is at an early stage: the aggregation layer and a REST endpoint work, but they are currently fed by a mock Binance stream rather than real exchange data.

## Status: work in progress

**Works now**

- An in-memory, thread-safe price store (`AggregatorService`) that keeps the latest price per symbol per exchange (`symbol -> exchange -> price`), backed by `ConcurrentHashMap`.
- A normalised price model (`PriceUpdateDto`: exchange, symbol, price, timestamp) that all exchange connectors are meant to convert into.
- A mock Binance client that starts a background thread on startup and pushes a random BTCUSDT price (65,000 to 66,999) into the store every two seconds.
- A REST endpoint that returns the latest price for a symbol from each exchange that has reported one.

**Planned**

- Real WebSocket connections to exchange market-data APIs, starting with Binance and then others such as Coinbase, replacing the mock client.
- A unified streaming endpoint (WebSocket) that pushes updates to clients as they arrive.
- Tests beyond the default context-load test.

## Tech stack

- Java 17
- Spring Boot 4.0 (Web MVC; validation and WebSocket starters are included for the planned features)
- Gradle (wrapper, Gradle 9.2.1)

## Running locally

Requires JDK 17 or later.

```bash
./gradlew bootRun        # Windows: gradlew.bat bootRun
```

The service starts on port 8080. The mock Binance stream logs a new BTCUSDT price every two seconds.

> Note: `gradle/wrapper/gradle-wrapper.jar` is not yet committed, so on a fresh clone run `gradle wrapper` once (with a local Gradle installation) or use `gradle bootRun` directly.

## API

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/prices/{symbol}/latest` | Latest price for `symbol` from each exchange, as a map of exchange to price update. Returns `{}` if the symbol has no data. Symbols are case-sensitive (use `BTCUSDT`). |

Example:

```bash
curl http://localhost:8080/api/prices/BTCUSDT/latest
```

```json
{
  "BINANCE": {
    "exchange": "BINANCE",
    "symbol": "BTCUSDT",
    "price": 65842,
    "timestamp": "2025-01-01T12:00:00.000Z"
  }
}
```

## Project structure

```
src/main/java/com/adamazmy/cryptoaggregator/
├── RealtimeCryptoAggregatorApplication.java
├── domain/PriceController.java                       # REST endpoint
├── exchange/binance/BinancePriceStreamClient.java    # Mock Binance stream
├── model/dto/PriceUpdateDto.java                     # Normalised price update
└── service/AggregatorService.java                    # In-memory latest-price store
```

## Licence

[MIT](LICENSE)
