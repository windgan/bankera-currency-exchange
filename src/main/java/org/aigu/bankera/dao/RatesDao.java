package org.aigu.bankera.dao;

import java.math.BigDecimal;
import java.util.Map;

public interface RatesDao {

    BigDecimal get(String currency);

    void add(String currency, BigDecimal rate);

    Map<String, BigDecimal> getAll();
}
