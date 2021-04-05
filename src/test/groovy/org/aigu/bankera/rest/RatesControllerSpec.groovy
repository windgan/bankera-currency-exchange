package org.aigu.bankera.rest

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import spock.lang.Shared
import spock.lang.Specification

import java.math.RoundingMode

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@AutoConfigureMockMvc
@SpringBootTest
class RatesControllerSpec extends Specification {

    @Autowired
    private MockMvc mvc

    def CURRENCY_API = "/api/currency";

    @Shared
    def reader = new ObjectMapper().reader()


    def "get all rates"() {
        when:
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get(CURRENCY_API + "/rates")
                .accept(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.success').value(true))
                .andReturn();
        JsonNode response = reader.readTree(result.getResponse().getContentAsString())

        then:
        response.at('/response/BTC').asText() == "6977.089657000000000000"
    }

    def "get present rate"() {
        expect:
        mvc.perform(MockMvcRequestBuilders.get(CURRENCY_API + "/rates/{currency}", "BTC")
                .accept(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.success').value(true))
                .andExpect(jsonPath('$.response').value("6977.089657000000000000"))
                .andReturn();
    }

    def "get non existing currency"() {
        expect:
        mvc.perform(MockMvcRequestBuilders.get(CURRENCY_API + "/rates/{currency}", "RUB")
                .accept(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.success').value(false))
                .andExpect(jsonPath('$.response').value("Currency not found"))
                .andReturn();
    }

    def "convertCurrencies"() {
        when:
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get(CURRENCY_API + "/exchange")
                .param("quantity", quantity)
                .param("from", from)
                .param("to", to)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.success').value(true))
                .andReturn();

        JsonNode response = reader.readTree(result.getResponse().getContentAsString())
        BigDecimal value = new BigDecimal(response.path("response").asText())

        then:
        value.scale() == 18
        value == expected

        where:
        quantity | from  | to    | expected
        "11"     | "USD" | "EUR" | new BigDecimal(quantity).multiply(new BigDecimal("0.809552722111111222336")).divide(BigDecimal.valueOf(1), 18, RoundingMode.HALF_UP)
        "326.45" | "BTC" | "USD" | new BigDecimal(quantity).multiply(new BigDecimal("6977.089657")).divide(new BigDecimal("0.809552722111111222336"), 18, RoundingMode.HALF_UP)
    }

    def "convertCurrencies incorrect request"() {
        expect:
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get(CURRENCY_API + "/exchange")
                .param("quantity", quantity)
                .param("from", from)
                .param("to", to)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.success').value(false))
                .andExpect(jsonPath('$.response').value("Incorrect request"))
                .andReturn();

        where:
        quantity | from  | to
        "-11"    | "USD" | "EUR"
        "326.45" | ""    | "USD"
        "326.45" | "USD" | ""
    }

    def "convertCurrencies for non existing currency"() {
        expect:
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get(CURRENCY_API + "/exchange")
                .param("quantity", quantity)
                .param("from", from)
                .param("to", to)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.success').value(false))
                .andExpect(jsonPath('$.response').value("Could not exchange currencies or one of currencies is not supported"))
                .andReturn();


        where:
        quantity | from  | to
        "326.45" | "RUB" | "USD"
    }
}


