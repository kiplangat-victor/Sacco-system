package com.emtechhouse.System.ExchangeRates;


import com.emtechhouse.System.Utils.CONSTANTS;
import com.emtechhouse.System.Utils.EntityResponse;
import com.emtechhouse.System.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.System.Utils.HttpInterceptor.UserRequestContext;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;


@RestController
@Slf4j
@Api(value = "/echangeRates API", tags = "ExchangeRates API")
@RequestMapping("/exchangerates/")
public class EchangeRateController {

    @Autowired
    private ExchangeRatesService exchangeRatesService;

    @Autowired
    private  ExchangeRatesRepo exchangeRatesRepo;

    @PostMapping("set")
    public ResponseEntity<EntityResponse> setBaseCurrency(@RequestBody BaseCurency baseCurency) {
        EntityResponse response = new EntityResponse();


        if (UserRequestContext.getCurrentUser().isEmpty()) {

            response.setMessage("User Name not present in the Request Header");
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            response.setEntity("");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                List<BaseCurency> baseCurencies = exchangeRatesService.getCurrency();
                if (baseCurencies.size() > 0) {
                    response.setMessage("Base currency is already set. You can only update the existing");
                    response.setStatusCode(HttpStatus.EXPECTATION_FAILED.value());
                } else {
                    exchangeRatesService.setbaseCurrency(baseCurency);
                    response.setEntity(baseCurency);
                    response.setStatusCode(HttpStatus.CREATED.value());

                    response.setMessage(HttpStatus.CREATED.getReasonPhrase());



                }

            }
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("base")
    public ResponseEntity<EntityResponse> getBaAseCurency() {

        EntityResponse response = new EntityResponse();


        if (UserRequestContext.getCurrentUser().isEmpty()) {

            response.setMessage("User Name not present in the Request Header");
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            response.setEntity("");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
//                Optional<Currency> checkCurrency = currencyRepo.findByEntityIdAndCurrencyCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), currency.getCcy(),'N');


                    List<BaseCurency> baseCurencies = exchangeRatesService.getCurrency();
                    response.setEntity(baseCurencies);
                    response.setStatusCode(HttpStatus.CREATED.value());

                    response.setMessage(HttpStatus.CREATED.getReasonPhrase());




            }
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("update")
    public ResponseEntity<EntityResponse> gupdateBaseCurency(@RequestBody BaseCurency baseCurency) {

        EntityResponse response = new EntityResponse();


        if (UserRequestContext.getCurrentUser().isEmpty()) {

            response.setMessage("User Name not present in the Request Header");
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            response.setEntity("");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
//                Optional<Currency> checkCurrency = currencyRepo.findByEntityIdAndCurrencyCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), currency.getCcy(),'N');
//                if(checkCurrency.isPresent()){
//                    EntityResponse response = new EntityResponse();
//                    response.setMessage("Code already exists!");
//                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                    response.setEntity("");
//                    return new ResponseEntity<>(response, HttpStatus.OK);
//                }else{
                baseCurency.setModifiedBy(UserRequestContext.getCurrentUser());
                baseCurency.setModifiedTime(new Date());
                exchangeRatesService.updateCurrency(baseCurency);
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage("Exchange rate updated");


            }
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("addExchangeRate")
    public ResponseEntity<EntityResponse> addexchangeRate(@RequestBody ExchangeRate exchangeRate) {
        EntityResponse response = new EntityResponse();


        if (UserRequestContext.getCurrentUser().isEmpty()) {

            response.setMessage("User Name not present in the Request Header");
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            response.setEntity("");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
//                Optional<Currency> checkCurrency = currencyRepo.findByEntityIdAndCurrencyCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), currency.getCcy(),'N');
//                if(checkCurrency.isPresent()){
//                    EntityResponse response = new EntityResponse();
//                    response.setMessage("Code already exists!");
//                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                    response.setEntity("");
//                    return new ResponseEntity<>(response, HttpStatus.OK);
//                }else{
                exchangeRate.setPostedBy(UserRequestContext.getCurrentUser());
                exchangeRate.setEntityId(EntityRequestContext.getCurrentEntityId());
                exchangeRate.setPostedTime(new Date());

                exchangeRatesService.addexchangerate(exchangeRate);
                response.setStatusCode(HttpStatus.CREATED.value());
                response.setMessage(HttpStatus.CREATED.getReasonPhrase());


            }
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("fetchExchangeRate")
    public ResponseEntity<EntityResponse> getexchangeRate() {
        EntityResponse response = new EntityResponse();


        if (UserRequestContext.getCurrentUser().isEmpty()) {

            response.setMessage("User Name not present in the Request Header");
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            response.setEntity("");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
//                Optional<Currency> checkCurrency = currencyRepo.findByEntityIdAndCurrencyCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), currency.getCcy(),'N');
//                if(checkCurrency.isPresent()){
//                    EntityResponse response = new EntityResponse();
//                    response.setMessage("Code already exists!");
//                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                    response.setEntity("");
//                    return new ResponseEntity<>(response, HttpStatus.OK);
//                }else{


                List<ExchangeRate> rate = exchangeRatesService.getexchangerate();
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(rate);
                response.setMessage(HttpStatus.OK.getReasonPhrase());


            }
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("updatexchangeRate")
    public ResponseEntity<EntityResponse> updateexchangeRate(@RequestBody ExchangeRate exchangeRate) {
        EntityResponse response = new EntityResponse();


        if (UserRequestContext.getCurrentUser().isEmpty()) {

            response.setMessage("User Name not present in the Request Header");
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            response.setEntity("");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
//                Optional<Currency> checkCurrency = currencyRepo.findByEntityIdAndCurrencyCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), currency.getCcy(),'N');
//                if(checkCurrency.isPresent()){
//                    EntityResponse response = new EntityResponse();
//                    response.setMessage("Code already exists!");
//                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                    response.setEntity("");
//                    return new ResponseEntity<>(response, HttpStatus.OK);
//                }else{

                exchangeRate.setModifiedFlag(CONSTANTS.YES);
                exchangeRate.setModifiedBy(UserRequestContext.getCurrentUser());
                exchangeRate.setModifiedTime(new Date());
                exchangeRatesService.updateExchangeRate(exchangeRate);
                response.setStatusCode(HttpStatus.CREATED.value());
                response.setMessage(HttpStatus.CREATED.getReasonPhrase());


            }
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("fetchyByCurency/{currency}")
    public ResponseEntity<EntityResponse> getByCurrency(@PathVariable String currency) {
        EntityResponse response = new EntityResponse();


        if (UserRequestContext.getCurrentUser().isEmpty()) {

            response.setMessage("User Name not present in the Request Header");
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            response.setEntity("");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                Optional<ExchangeRate> exchangeRate =exchangeRatesRepo.findByForeignCurrency(currency);
                if(exchangeRate.isPresent()){
                    response.setEntity(exchangeRate.get());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                }else {
                    response.setEntity(null);
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                }

            }
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }




}
