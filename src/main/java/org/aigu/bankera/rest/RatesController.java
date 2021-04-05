package org.aigu.bankera.rest;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.aigu.bankera.service.RatesService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@Api(value = "Currency Rates Controller")
@RestController
@RequestMapping(value = "/api/currency", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class RatesController {

    private RatesService ratesService;

    @Autowired
    public void setRatesService(RatesService ratesService) {
        this.ratesService = ratesService;
    }

    @ApiOperation(notes = "Convert Currencies", value = "Convert from one currency to another", nickname = "convertCurrencies")
    @ApiResponses({@ApiResponse(code = 200, message = "Currencies exchanged!", response = RestResponseVO.class)})
    @GetMapping("/exchange")
    public RestResponseVO convertCurrencies(@ApiParam(required = true, name = "quantity", example = "12.15") @RequestParam(value = "quantity", required = true) BigDecimal quantity,
                                            @ApiParam(required = true, name = "from", example = "USD") @RequestParam(value = "from", required = true) String fromCurrency,
                                            @ApiParam(required = true, name = "to", example = "EUR") @RequestParam(value = "to", required = true) String toCurrency) {
        log.debug("Entered convertCurrencies method - quantity:{}, from:{}, to:{}", quantity, fromCurrency, toCurrency);
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) < 0 || StringUtils.isAnyEmpty(fromCurrency, toCurrency)) {
            String msg = "Incorrect request";
            log.error(msg);
            return new RestResponseVO(false, msg);
        }
        BigDecimal result = ratesService.convertCurrencies(quantity, fromCurrency, toCurrency);
        if (result == null) {
            String msg = "Could not exchange currencies or one of currencies is not supported";
            log.error("Exiting convertCurrencies method. " + msg);
            return new RestResponseVO(false, msg);
        }
        log.debug("Exiting convertCurrencies method. Result: {}", result);
        return new RestResponseVO(true, result);
    }

    @ApiOperation(notes = "Get Rates", value = "Get All Currency Rates", nickname = "getAllRates")
    @ApiResponses({@ApiResponse(code = 200, message = "Currency rates listed!", response = RestResponseVO.class)})
    @GetMapping("/rates")
    public RestResponseVO getAllRates() {
        log.debug("Entered getAllRates method");
        Map<String, BigDecimal> rates = ratesService.getAll();
        log.debug("Exiting getAllRates method. Result is {}", rates);
        return new RestResponseVO(true, rates);
    }

    @ApiOperation(notes = "Get Rate", value = "Get Currency Rate", nickname = "getRate")
    @ApiResponses({@ApiResponse(code = 200, message = "Currency rates listed!", response = RestResponseVO.class)})
    @GetMapping("/rates/{currency}")
    public RestResponseVO getRate(@ApiParam(required = true, name = "currency", example = "USD") @PathVariable(value = "currency", required = true) String currency) {
        log.debug("Entered getRate method for currency: {}", currency);
        if (StringUtils.isEmpty(currency)) {
            return new RestResponseVO(false, "incorrect request");
        }
        Optional<BigDecimal> result = ratesService.get(currency);
        if (result.isPresent()) {
            BigDecimal value = result.get();
            log.debug("Exiting getRate method. Result is {}", value);
            return new RestResponseVO(true, value);
        } else {
            log.error("Exiting getRate method. Currency not found");
            return new RestResponseVO(false, "Currency not found");
        }
    }
}
