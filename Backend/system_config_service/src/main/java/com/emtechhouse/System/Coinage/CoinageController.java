package com.emtechhouse.System.Coinage;

import com.emtechhouse.System.Utils.EntityResponse;
import com.emtechhouse.System.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.System.Utils.HttpInterceptor.UserRequestContext;
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
//@CrossOrigin
@RequestMapping("coinage")
public class CoinageController {
    @Autowired
    private CoinageService coinageService ;
    @Autowired
    private CoinageRepo coinageRepo ;

    @PostMapping("/add")
    public ResponseEntity<?> addCoinage(@RequestBody Coinage coinage) {
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
                    Optional<Coinage> coinage1 = coinageRepo.findCoinageByCoinageCodeAndDeletedFlag(coinage.getCoinageCode(),'N');
                    if (coinage1.isPresent()){
                        EntityResponse response = new EntityResponse();
                        response.setMessage("COINAGE WITH CODE " + coinage.getCoinageCode() +" ALREADY CREATED!! TRY ANOTHER CODE");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }else{
                        coinage.setPostedBy(UserRequestContext.getCurrentUser());
                        coinage.setEntityId(EntityRequestContext.getCurrentEntityId());
                        coinage.setPostedFlag('Y');
                        coinage.setPostedTime(new Date());
                        Coinage newCoinage = coinageService.addCoinage(coinage);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("COINAGE WITH CODE " + coinage.getCoinageCode() + " CREATED SUCCESSFULLY AT " + coinage.getPostedTime());
                        response.setStatusCode(HttpStatus.CREATED.value());
                        response.setEntity(newCoinage);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    @GetMapping("/all/unverified")
    public ResponseEntity<?> getAllCoinages() {
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
                    List<Coinage> coinage = coinageRepo.findAllByEntityIdAndDeletedFlagOrderByIdDesc(EntityRequestContext.getCurrentEntityId(),'N');
                    if (coinage.size()>0){
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(coinage);
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

    @GetMapping("/all")
    public ResponseEntity<?> getAllVerifiedCoinages() {
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
                    List<Coinage> coinage = coinageRepo.findAll();
                    if (coinage.size()>0){
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(coinage);
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
    @GetMapping("/check/coinageCode/{coinageCode}")
    public  ResponseEntity<?> findCoinageCode(@PathVariable("coinageCode") String coinageCode){
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
                    Optional<Coinage> searchCoinageCode = coinageRepo.findCoinageByCoinageCode(coinageCode);
                    if(!searchCoinageCode.isPresent()){
                        response.setMessage("COINAGE WITH CODE " + coinageCode + " AVAILABLE FOR REGISTRATION");
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        Coinage coinage = coinageService.findCoinageCode(coinageCode);
                        response.setMessage("COINAGE WITH CODE " + coinageCode + " ALREADY REGISTERED");
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(coinage);
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
    public ResponseEntity<?> getCoinageById(@PathVariable("id") Long id) {
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
                    Optional<Coinage> searchCoinageId = coinageRepo.findById(id);
                    if(!searchCoinageId.isPresent()){
                        EntityResponse response = new EntityResponse();
                        response.setMessage("COINAGE CODE DOES NOT EXIST!! PLEASE PROVIDE A VALID REGISTERED COINAGE CODE");
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);

                    } else {
                        Coinage coinage = coinageService.findById(id);
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(coinage);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }

                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("/modify")
    public ResponseEntity<?> updateCoinage(@RequestBody Coinage coinage) {
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
                    coinage.setModifiedBy(UserRequestContext.getCurrentUser());
                    coinage.setEntityId(EntityRequestContext.getCurrentEntityId());
                    Optional<Coinage> coinage1 = coinageRepo.findById(coinage.getId());
                    if (coinage1.isPresent()) {
                        coinage.setPostedTime(coinage1.get().getPostedTime());
                        coinage.setPostedFlag(coinage1.get().getPostedFlag());
                        coinage.setPostedBy(coinage1.get().getPostedBy());
                        coinage.setModifiedFlag('Y');
                        coinage.setVerifiedFlag(coinage1.get().getVerifiedFlag());
                        coinage.setModifiedTime(new Date());
                        coinage.setModifiedBy(coinage.getModifiedBy());
                        coinageService.updateCoinage(coinage);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("COINAGE WITH CODE" + " " + coinage.getCoinageCode() + " " + "MODIFIED SUCCESSFULLY" + " AT " + coinage.getModifiedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(coinage);
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
                    Optional<Coinage> coinage1 = coinageRepo.findById(Long.parseLong(id));
                    if (coinage1.isPresent()) {
                        Coinage coinage = coinage1.get();
                        if (coinage.getPostedBy().equalsIgnoreCase(UserRequestContext.getCurrentUser())){
                            EntityResponse response = new EntityResponse();
                            response.setMessage("You Can Not Verify What you initiated");
                            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                            response.setEntity("");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        }else{
                            if (coinage.getVerifiedFlag().equals('Y')){
                                EntityResponse response = new EntityResponse();
                                response.setMessage("COINAGE WITH CODE " + coinage.getCoinageCode() + " ALREADY VERIFIED");
                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                response.setEntity(coinage);
                                return new ResponseEntity<>(response, HttpStatus.OK);
                            } else {
                                coinage.setVerifiedFlag('Y');
                                coinage.setVerifiedTime(new Date());
                                coinage.setVerifiedBy(UserRequestContext.getCurrentUser());
                                coinageRepo.save(coinage);
                                EntityResponse response = new EntityResponse();
                                response.setMessage("COINAGE WITH CODE " + coinage.getCoinageCode() + " VERIFIED SUCCESSFULLY AT "  + coinage.getVerifiedTime());
                                response.setStatusCode(HttpStatus.OK.value());
                                response.setEntity(coinage);
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
    public ResponseEntity<?> deleteCoinage(@PathVariable String id) {
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
                    Optional<Coinage> coinage1 = coinageRepo.findById(Long.parseLong(id));
                    if (coinage1.isPresent()) {
                        Coinage coinage = coinage1.get();
                        coinage.setDeletedFlag('Y');
                        coinage.setDeletedTime(new Date());
                        coinage.setDeletedBy(UserRequestContext.getCurrentUser());
                        coinageRepo.save(coinage);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("COINAGE WITH REF CODE" + " " + coinage.getCoinageCode() + " " + "DELETED SUCCESSFULLY" + " AT " + coinage.getDeletedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(coinage);
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

