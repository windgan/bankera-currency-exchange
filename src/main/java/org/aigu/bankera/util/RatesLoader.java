package org.aigu.bankera.util;

import lombok.extern.slf4j.Slf4j;
import org.aigu.bankera.dao.RatesDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.Arrays;

@Component
@Slf4j
public class RatesLoader implements ApplicationRunner {

    private final String COMMA_DELIMITER = ",";

    @Value("${rates.file}")
    private String ratesFile;

    private RatesDao ratesDao;

    @Autowired
    public void setRatesDao(RatesDao ratesDao) {
        this.ratesDao = ratesDao;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (ratesFile == null || ratesFile.isEmpty()) {
            throw new RuntimeException("Error while loading currency rates from file");
        }
        try (BufferedReader br = new BufferedReader(new FileReader(
                ResourceUtils.getFile("classpath:" + ratesFile)))) {
            String line;
            //skip the first line
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(COMMA_DELIMITER);
                if (values.length != 2 || values[0].isEmpty() || values[1].isEmpty()) {
                    throw new RuntimeException("Incorrect rate: " + Arrays.toString(values));
                }
                String currency = values[0];
                BigDecimal rate = new BigDecimal(values[1].trim());
                if (rate.compareTo(BigDecimal.ZERO) < 0) {
                    throw new RuntimeException("Currency rate is negative: " + currency);
                }
                BigDecimal storedRate = ratesDao.get(currency);
                if (storedRate != null) {
                    log.warn("Currency rate for {} is already stored: {}. New value will be used: {}",
                            currency, storedRate, rate);
                }
                ratesDao.add(currency, rate);
            }
        }
        log.trace("rates {}", ratesDao.getAll());
    }
}
