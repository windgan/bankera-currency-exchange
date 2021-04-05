package org.aigu.bankera.service;

import org.aigu.bankera.dao.RatesDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Optional;

@Service
public class RatesServiceImpl implements RatesService {

    private RatesDao ratesDao;

    @Autowired
    public void setRatesDao(RatesDao ratesDao) {
        this.ratesDao = ratesDao;
    }

    @Override
    public Optional<BigDecimal> get(String currency) {
        BigDecimal result = ratesDao.get(currency);
        return result != null ? Optional.of(result) : Optional.empty();
    }

    @Override
    public Map<String, BigDecimal> getAll() {
        return ratesDao.getAll();
    }

    @Override
    public BigDecimal convertCurrencies(BigDecimal quantity, String fromCurrency, String toCurrency) {
        BigDecimal fromRate = ratesDao.get(fromCurrency);
        BigDecimal toRate = ratesDao.get(toCurrency);
        if (fromRate == null || toRate == null) {
            return null;
        }
        return fromRate.multiply(quantity ).divide(toRate, 20, RoundingMode.HALF_UP);
    }
}
