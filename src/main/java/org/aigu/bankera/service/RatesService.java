package org.aigu.bankera.service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

public interface RatesService {
    Optional<BigDecimal> get(String currency);

    Map<String, BigDecimal> getAll();

    BigDecimal convertCurrencies(BigDecimal quantity, String fromCurrency, String toCurrency);
}
