package org.aigu.bankera.util

import org.aigu.bankera.dao.RatesDaoImpl
import spock.lang.Specification

class RatesLoaderSpec extends Specification {

    def ratesLoader = new RatesLoader()
    def ratesDao = new RatesDaoImpl()

    def setup() {
        ratesLoader.setRatesDao(ratesDao)
    }

    def "test with no file"() {
        when:
        ratesLoader.run()

        then:
        def e = thrown(RuntimeException)
        e.getMessage() == "Error while loading currency rates from file"
    }

    def "test incorrect file"() {
        setup:
        ratesLoader.ratesFile = fileName

        when:
        ratesLoader.run()

        then:
        def e = thrown(RuntimeException)
        e.getMessage().contains("Incorrect rate:")

        where:
        fileName << ["more_2_strings_in_row.csv", "empty_currency.csv", "empty_rate.csv"]
    }

    def "test negative rate"() {
        setup:
        ratesLoader.ratesFile = "negative_rate.csv"

        when:
        ratesLoader.run()

        then:
        def e = thrown(RuntimeException)
        e.getMessage().contains("Currency rate is negative:")
    }

    def "test success"() {
        setup:
        ratesLoader.ratesFile = "success.csv"

        def map = new HashMap<String, BigDecimal>() {
            {
                put("USD", BigDecimal.valueOf(0.809552722111111222336));
                put("EUR", BigDecimal.valueOf(1));
                put("GBP", BigDecimal.valueOf(1.126695849));
                put("BTC", BigDecimal.valueOf(6977.089657));
                put("ETH", BigDecimal.valueOf(685.2944747));
                put("FKE", BigDecimal.valueOf(0.025));
            }
        }

        when:
        ratesLoader.run()

        then:
        noExceptionThrown()
        ratesDao.get("GBP") == BigDecimal.valueOf(1.126695849)
        ratesDao.get("RUB") == null
    }
}
