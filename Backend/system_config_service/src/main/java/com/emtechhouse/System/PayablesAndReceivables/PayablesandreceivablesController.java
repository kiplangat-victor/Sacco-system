package com.emtechhouse.System.PayablesAndReceivables;

import com.emtechhouse.System.Utils.EntityResponse;
import com.emtechhouse.System.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.System.Utils.HttpInterceptor.UserRequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/payablesandreceivables")
@Slf4j
public class PayablesandreceivablesController {
    private final PayablesandreceivablesRepo payablesandreceivablesRepo;
    private final PayablesandreceivablesService payablesandreceivablesService;

    public PayablesandreceivablesController(PayablesandreceivablesRepo payablesandreceivablesRepo, PayablesandreceivablesService payablesandreceivablesService) {
        this.payablesandreceivablesRepo = payablesandreceivablesRepo;
        this.payablesandreceivablesService = payablesandreceivablesService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addPayablesandreceivables(@RequestBody Payablesandreceivables payablesandreceivables) {
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

                    payablesandreceivables.setPostedBy(UserRequestContext.getCurrentUser());
                    payablesandreceivables.setEntityId(EntityRequestContext.getCurrentEntityId());
                    payablesandreceivables.setPostedFlag('Y');
                    payablesandreceivables.setPostedTime(new Date());

                    Payablesandreceivables newPayablesandreceivables = payablesandreceivablesService.addPayablesandreceivables(payablesandreceivables);
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.CREATED.getReasonPhrase());
                    response.setStatusCode(HttpStatus.CREATED.value());
                    response.setEntity(newPayablesandreceivables);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllPayablesandreceivabless() {
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
                    List<Payablesandreceivables> payablesandreceivables = payablesandreceivablesRepo.findByEntityIdAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), 'N');
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(payablesandreceivables);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<?> getPayablesandreceivablesById(@PathVariable("id") Long id) {
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
                    Payablesandreceivables payablesandreceivables = payablesandreceivablesService.findById(id);
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(payablesandreceivables);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("/modify")
    public ResponseEntity<?> updatePayablesandreceivables(@RequestBody Payablesandreceivables payablesandreceivables) {
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
                    payablesandreceivables.setModifiedBy(UserRequestContext.getCurrentUser());
                    payablesandreceivables.setEntityId(EntityRequestContext.getCurrentEntityId());
                    Optional<Payablesandreceivables> payablesandreceivables1 = payablesandreceivablesRepo.findById(payablesandreceivables.getId());
                    if (payablesandreceivables1.isPresent()) {
                        payablesandreceivables.setPostedTime(payablesandreceivables1.get().getPostedTime());
                        payablesandreceivables.setPostedFlag(payablesandreceivables1.get().getPostedFlag());
                        payablesandreceivables.setPostedBy(payablesandreceivables1.get().getPostedBy());
                        payablesandreceivables.setModifiedFlag('Y');
                        payablesandreceivables.setVerifiedFlag(payablesandreceivables1.get().getVerifiedFlag());
                        payablesandreceivables.setModifiedTime(new Date());
                        payablesandreceivables.setModifiedBy(payablesandreceivables.getModifiedBy());
                        payablesandreceivablesService.updatePayablesandreceivables(payablesandreceivables);
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(payablesandreceivables);
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
                    Optional<Payablesandreceivables> payablesandreceivables1 = payablesandreceivablesRepo.findById(Long.parseLong(id));
                    if (payablesandreceivables1.isPresent()) {
                        Payablesandreceivables payablesandreceivables = payablesandreceivables1.get();
                        //                    Check Maker Checker
                        if (payablesandreceivables.getPostedBy().equalsIgnoreCase(UserRequestContext.getCurrentUser())){
                            EntityResponse response = new EntityResponse();
                            response.setMessage("You Can Not Verify What you initiated");
                            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                            response.setEntity("");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        }else{
                            payablesandreceivables.setVerifiedFlag('Y');
                            payablesandreceivables.setVerifiedTime(new Date());
                            payablesandreceivables.setVerifiedBy(UserRequestContext.getCurrentUser());
                            payablesandreceivablesRepo.save(payablesandreceivables);
                            EntityResponse response = new EntityResponse();
                            response.setMessage(HttpStatus.OK.getReasonPhrase());
                            response.setStatusCode(HttpStatus.OK.value());
                            response.setEntity(payablesandreceivables);
                            return new ResponseEntity<>(response, HttpStatus.OK);
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
    public ResponseEntity<?> deletePayablesandreceivables(@PathVariable String id) {
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
                    Optional<Payablesandreceivables> payablesandreceivables1 = payablesandreceivablesRepo.findById(Long.parseLong(id));
                    if (payablesandreceivables1.isPresent()) {
                        Payablesandreceivables payablesandreceivables = payablesandreceivables1.get();
                        payablesandreceivables.setDeletedFlag('Y');
                        payablesandreceivables.setDeletedTime(new Date());
                        payablesandreceivables.setDeletedBy(UserRequestContext.getCurrentUser());
                        payablesandreceivablesRepo.save(payablesandreceivables);
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(payablesandreceivables);
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
