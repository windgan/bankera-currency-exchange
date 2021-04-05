package org.aigu.bankera.service

import org.aigu.bankera.dao.RatesDao
import spock.lang.Shared
import spock.lang.Specification

import java.math.RoundingMode

class RatesServiceSpec extends Specification {

    @Shared
    def ratesDao = Mock(RatesDao)

    @Shared
    def ratesService = new RatesServiceImpl();

    @Shared
    def map = new HashMap<String, BigDecimal>() {
        {
            put("USD", BigDecimal.valueOf(0.23423445));
            put("EUR", BigDecimal.valueOf(1));
            put("GBP", BigDecimal.valueOf(1.2345));
        }
    }

    def setupSpec() {
        ratesDao.get("USD") >> BigDecimal.valueOf(0.23423445)
        ratesDao.get("EUR") >> BigDecimal.valueOf(1)
        ratesDao.get("GBP") >> BigDecimal.valueOf(1.2345)
        ratesDao.getAll() >> map

        ratesService.setRatesDao(ratesDao)
    }

    def "Get"() {
        when:
        def result = ratesService.get("USD")
        then:
        result.get() == BigDecimal.valueOf(0.23423445)

        when:
        result = ratesService.get("BTC")
        then:
        result == Optional.empty()
    }

    def "GetAll"() {
        expect:
        ratesService.getAll() == map
    }

    def "ConvertCurrencies"() {
        where:
        quantity | fromCurrency | toCurrency | result
        12       | null         | "USD"      | null
        12       | "USD"        | null       | null
        11       | "USD"        | "EUR"      | quantity * BigDecimal.valueOf(0.23423445).divide(BigDecimal.valueOf(1), 20, RoundingMode.HALF_UP)
        11       | "RUB"        | "EUR"      | null
        11       | "EUR"        | "RUB"      | null
        113.45   | "USD"        | "GBP"      | quantity * BigDecimal.valueOf(0.23423445).divide(BigDecimal.valueOf(1.2345), 20, RoundingMode.HALF_UP)
    }
}
