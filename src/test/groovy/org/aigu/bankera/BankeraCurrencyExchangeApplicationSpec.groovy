package org.aigu.bankera

import org.aigu.bankera.rest.RatesController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class BankeraCurrencyExchangeApplicationSpec extends Specification {

    @Autowired(required = false)
    private RatesController webController

    def "when context is loaded then all expected beans are created"() {
        expect: "the RatesController is created"
        webController
    }
}
