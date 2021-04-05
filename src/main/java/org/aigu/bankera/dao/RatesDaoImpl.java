package org.aigu.bankera.dao;

import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Repository
public class RatesDaoImpl implements RatesDao {

    private Map<String, BigDecimal> rates = new HashMap<>();

    @Override
    public BigDecimal get(String currency) {
        return rates.get(currency.toUpperCase());
    }

    @Override
    public void add(String currency, BigDecimal rate) {
        rates.put(currency.toUpperCase(), rate);
    }

    @Override
    public Map<String, BigDecimal> getAll() {
        return Collections.unmodifiableMap(rates);
    }
}
