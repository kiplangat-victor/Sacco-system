package com.emtechhouse.System.CurrencyParams;


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
@Api(value = "/Currency API", tags = "Currency API")
@RequestMapping("/currency")
public class CurrencyController {
    @Autowired
    private CurrencyRepo currencyRepo;
    @Autowired
    private CurrencyService currencyService;

    @PostMapping("/add")
    public ResponseEntity<?> addCurrency(@RequestBody Currency currency) {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                EntityResponse response = new EntityResponse();
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    Optional<Currency> checkCurrency = currencyRepo.findByEntityIdAndCurrencyCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), currency.getCcy(), 'N');
                    if (checkCurrency.isPresent()) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("Code already exists!");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        Optional<Currency> currency1 = currencyRepo.findByCcyName(currency.getCcyName());
                        if (currency1.isPresent()){
                            EntityResponse response = new EntityResponse();
                            response.setMessage("Currency with code " + currency1.get().getCurrencyCode() + " for " + currency.getCcyName() + " Already REGISTERED: !! " );
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        } else {
                            Optional<Currency> currency2 = currencyRepo.findCurrencyByCountry(currency.getCountry());
                            if (currency2.isPresent()){
                                EntityResponse response = new EntityResponse();
                                response.setMessage("Currency with code " + currency2.get().getCurrencyCode() + " for " + currency.getCountry() + " Already REGISTERED: !! " );
                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                return new ResponseEntity<>(response, HttpStatus.OK);
                            } else {
                                currency.setPostedBy(UserRequestContext.getCurrentUser());
                                currency.setEntityId(EntityRequestContext.getCurrentEntityId());
                                currency.setPostedFlag('Y');
                                currency.setPostedTime(new Date());
                                Currency newCurrency = currencyService.addCurrency(currency);
                                EntityResponse response = new EntityResponse();
                                response.setMessage("CURRENCY WITH CODE " + currency.getCurrencyCode() + " , " + currency.getCcy() + " , " + currency.getCcyName() + " CREATED SUCCESSFULLY AT " + currency.getPostedTime());
                                response.setStatusCode(HttpStatus.CREATED.value());
                                response.setEntity(newCurrency);
                                return new ResponseEntity<>(response, HttpStatus.OK);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    @GetMapping("/all")
    public ResponseEntity<?> getAllCurrencys() {
        try {
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
                    List<Currency> currency = currencyService.findAllCurrencys(EntityRequestContext.getCurrentEntityId(), 'N');
                    if (!currency.isEmpty()){
                        response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(currency);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity(currency);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<?> getCurrencyById(@PathVariable("id") Long id) {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                EntityResponse response = new EntityResponse();
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    Currency currency = currencyService.findById(id);
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(currency);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/code/find/{code}")
    public ResponseEntity<?> getCurrencyByCode(@PathVariable("code") String currencyCode) {
        try {
            EntityResponse response = new EntityResponse();
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                Optional<Currency> searchCode = currencyRepo.findByEntityIdAndCurrencyCode(EntityRequestContext.getCurrentEntityId(),currencyCode);
                if (searchCode.isPresent()){
                   Optional<Currency>  currency = currencyRepo.findByCurrencyCode(currencyCode);
                    response.setMessage("CURRENCY WITH CODE " + currencyCode + " ALREADY REGISTERED");
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(currency);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    response.setMessage("CURRENCY WITH CODE " + currencyCode + " AVAILABLE FOR REGISTRATION");
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setEntity("");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("/modify")
    public ResponseEntity<?> updateCurrency(@RequestBody Currency currency) {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                EntityResponse response = new EntityResponse();
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    currency.setModifiedBy(UserRequestContext.getCurrentUser());
                    currency.setEntityId(EntityRequestContext.getCurrentEntityId());
                    Optional<Currency> currency1 = currencyRepo.findById(currency.getId());
                    if (currency1.isPresent()) {
                        currency.setPostedTime(currency1.get().getPostedTime());
                        currency.setPostedFlag(currency1.get().getPostedFlag());
                        currency.setPostedBy(currency1.get().getPostedBy());
                        currency.setModifiedFlag('Y');
                        currency.setVerifiedFlag(currency1.get().getVerifiedFlag());
                        currency.setModifiedTime(new Date());
                        currency.setModifiedBy(currency.getModifiedBy());
                        currencyService.updateCurrency(currency);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("CURRENCY WITH CODE " + currency.getCurrencyCode() + " , " + currency.getCcy() + " , " + currency.getCcyName() + " MODIFIED SUCCESSFULLY AT " + currency.getModifiedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(currency);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("/verify/{id}")
    public ResponseEntity<?> verify(@PathVariable String id) {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                EntityResponse response = new EntityResponse();
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    Optional<Currency> currency1 = currencyRepo.findById(Long.parseLong(id));
                    if (currency1.isPresent()) {
                        Currency currency = currency1.get();
                        if (currency.getPostedBy().equalsIgnoreCase(UserRequestContext.getCurrentUser())) {
                            EntityResponse response = new EntityResponse();
                            response.setMessage("You Can Not Verify What you initiated");
                            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                            response.setEntity("");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        } else {
                            if (currency.getVerifiedFlag().equals('Y')){
                                EntityResponse response = new EntityResponse();
                                response.setMessage("CURRENCY WITH CODE " + currency.getCurrencyCode() + " , " + currency.getCcy() + " , " + currency.getCcyName() + " ALREADY VERIFIED SUCCESSFULLY");
                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                response.setEntity("");
                                return new ResponseEntity<>(response, HttpStatus.OK);
                            } else {
                                currency.setVerifiedFlag('Y');
                                currency.setVerifiedTime(new Date());
                                currency.setVerifiedBy(UserRequestContext.getCurrentUser());
                                currencyRepo.save(currency);
                                EntityResponse response = new EntityResponse();
                                response.setMessage("CURRENCY WITH CODE " + currency.getCurrencyCode() + " , " + currency.getCcy() + " , " + currency.getCcyName() + " VERIFIED SUCCESSFULLY AT " + currency.getVerifiedTime());
                                response.setStatusCode(HttpStatus.OK.value());
                                response.setEntity(currency);
                                return new ResponseEntity<>(response, HttpStatus.OK);
                            }
                        }
                    } else {
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCurrency(@PathVariable Long id) {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                EntityResponse response = new EntityResponse();
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    Optional<Currency> currency1 = currencyRepo.findById(id);
                    if (currency1.isPresent()) {
                        Currency currency = currency1.get();
                        currency.setDeletedFlag('Y');
                        currency.setDeletedTime(new Date());
                        currency.setDeletedBy(UserRequestContext.getCurrentUser());
                        currencyRepo.save(currency);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("CURRENCY WITH CODE " + currency.getCurrencyCode() + " , " + currency.getCcy() + " , " + currency.getCcyName() + " DELETED SUCCESSFULLY AT " + currency.getDeletedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(currency);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
}
