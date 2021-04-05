package org.aigu.bankera.dao

import spock.lang.Shared
import spock.lang.Specification

class RatesDaoSpec extends Specification {

    @Shared
    def ratesDao = new RatesDaoImpl()

    def setupSpec() {
        ratesDao.rates = new HashMap<String, BigDecimal>() {
            {
                put("USD", BigDecimal.valueOf(0.23423445));
                put("EUR", BigDecimal.valueOf(1));
                put("GBP", BigDecimal.valueOf(1.2345));
            }
        }
    }

    def "Get"() {
        expect:
        ratesDao.get("USD") == BigDecimal.valueOf(0.23423445)
        ratesDao.get("uSD") == BigDecimal.valueOf(0.23423445)
        ratesDao.get("usd") == BigDecimal.valueOf(0.23423445)

        ratesDao.get("EUR") == BigDecimal.valueOf(1)
        ratesDao.get("GBP") == BigDecimal.valueOf(1.2345)
        ratesDao.get("BTC") == null
    }

    def "Add single"() {
        when:
        ratesDao.add("USD", BigDecimal.valueOf(0.183366))

        then:
        ratesDao.get("USD") == BigDecimal.valueOf(0.183366)
    }

    def "Add lowercase"() {
        when:
        ratesDao.add("btc", BigDecimal.valueOf(0.23423445))

        then:
        ratesDao.get("BTC") == BigDecimal.valueOf(0.23423445)
        ratesDao.get("btc") == BigDecimal.valueOf(0.23423445)
    }

    def "Add multiple"() {
        when:
        ratesDao.add("USD", BigDecimal.valueOf(0.23423445))
        ratesDao.add("USD", BigDecimal.valueOf(0.88))

        then:
        ratesDao.get("USD") == BigDecimal.valueOf(0.88)
    }

    def "GetAll"() {
        setup:
        def map = new HashMap<String, BigDecimal>() {
            {
                put("USD", BigDecimal.valueOf(0.23423445));
                put("EUR", BigDecimal.valueOf(1));
                put("GBP", BigDecimal.valueOf(1.2345));
            }
        }
        ratesDao.rates = new HashMap<>(map)

        expect:
        ratesDao.getAll() == map

        when:
        ratesDao.add("BTC", BigDecimal.valueOf(3333.2345))
        then:
        ratesDao.getAll() != map
    }
}
