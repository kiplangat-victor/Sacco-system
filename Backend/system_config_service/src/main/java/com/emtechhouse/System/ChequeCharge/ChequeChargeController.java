package com.emtechhouse.System.ChequeCharge;

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
@Slf4j
//@CrossOrigin
@RequestMapping("chequecharges")
public class ChequeChargeController {
    private final ChequeChargeService chequeChargeService;
    private final ChequeChargeRepo chequeChargeRepo;

    public ChequeChargeController(ChequeChargeService chequeChargeService, ChequeChargeRepo chequeChargeRepo) {
        this.chequeChargeService = chequeChargeService;
        this.chequeChargeRepo = chequeChargeRepo;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addChequeCharge(@RequestBody ChequeCharge chequeCharge) {
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
                    Optional<ChequeCharge> chequeCharge1 = chequeChargeRepo.findByChargeCode(chequeCharge.getChargeCode());
                    if (chequeCharge1.isPresent()) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("CUSTOMER WITH TYPE CODE " + chequeCharge.getChargeCode() + " ALREADY REGISTERED! ENTER UNIQUE TYPE CODE TO CONTINUE");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        chequeCharge.setPostedBy(UserRequestContext.getCurrentUser());
                        chequeCharge.setEntityId(EntityRequestContext.getCurrentEntityId());
                        chequeCharge.setPostedFlag('Y');
                        chequeCharge.setPostedTime(new Date());
                        ChequeCharge addChequeCharge = chequeChargeService.addChequeCharge(chequeCharge);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("SALARY CHARGE WITH CODE " + chequeCharge.getChargeCode() + " AND TYPE " + chequeCharge.getChargeCode() + " CREATED SUCCESSFULLY " + chequeCharge.getPostedTime());
                        response.setStatusCode(HttpStatus.CREATED.value());
                        response.setEntity(addChequeCharge);
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
    public ResponseEntity<?> getAllChequeCharges() {
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
                    List<ChequeCharge> chequeCharge = chequeChargeRepo.findByEntityIdAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), 'N');
                    if (chequeCharge.size() > 0) {
                        response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(chequeCharge);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity(chequeCharge);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/check/cheque_charge/{cheque_charge_code}")
    public ResponseEntity<?> findsalary_charge(@PathVariable("cheque_charge_code") String cheque_charge_code) {
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
                    Optional<ChequeCharge> searchcheque_charge = chequeChargeRepo.findByChargeCode(cheque_charge_code);
                    if (!searchcheque_charge.isPresent()) {
                        response.setMessage("SALARY CHARGE WITH CODE " + cheque_charge_code + " AVAILABLE FOR REGISTRATION");
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity("");

                    } else {
                        ChequeCharge chequeCharge = chequeChargeService.findsalary_charge(cheque_charge_code);
                        response.setMessage("SALARY CHARGE WITH CODE " + cheque_charge_code + " ALREADY REGISTERED");
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(chequeCharge);
                    }
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<?> getChargeCodeById(@PathVariable("id") Long id) {
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

                    Optional<ChequeCharge> searchChequeChargeId = chequeChargeRepo.findById(id);
                    if (!searchChequeChargeId.isPresent()) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("SALARY CHARGE CODE DOES NOT EXIST!! PLEASE PROVIDE A VALID REGISTERED SALARY CHARGE CODE");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);

                    } else {
                        ChequeCharge chequeCharge = chequeChargeService.findById(id);
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(chequeCharge);
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
    public ResponseEntity<?> updateChequeCharge(@RequestBody ChequeCharge chequeCharge) {
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
                    chequeCharge.setModifiedBy(UserRequestContext.getCurrentUser());
                    chequeCharge.setEntityId(EntityRequestContext.getCurrentEntityId());
                    Optional<ChequeCharge> chequeCharge1 = chequeChargeRepo.findById(chequeCharge.getId());
                    if (chequeCharge1.isPresent()) {
                        chequeCharge.setPostedTime(chequeCharge1.get().getPostedTime());
                        chequeCharge.setPostedFlag(chequeCharge1.get().getPostedFlag());
                        chequeCharge.setPostedBy(chequeCharge1.get().getPostedBy());
                        chequeCharge.setVerifiedFlag(chequeCharge1.get().getVerifiedFlag());
                        chequeCharge.setModifiedFlag('Y');
                        chequeCharge.setModifiedTime(new Date());
                        chequeCharge.setModifiedBy(chequeCharge.getModifiedBy());
                        chequeChargeService.updateChequeCharge(chequeCharge);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("SALARY CHARGE WITH CODE" + " " + chequeCharge.getChargeCode() + " " + "MODIFIED SUCCESSFULLY" + " AT " + chequeCharge.getModifiedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(chequeCharge);
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
                    Optional<ChequeCharge> chequeCharge1 = chequeChargeRepo.findById(Long.parseLong(id));
                    if (chequeCharge1.isPresent()) {
                        ChequeCharge chequeCharge = chequeCharge1.get();
                        if (chequeCharge.getPostedBy().equalsIgnoreCase(UserRequestContext.getCurrentUser())) {
                            EntityResponse response = new EntityResponse();
                            response.setMessage("You Can Not Verify What you initiated");
                            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                            response.setEntity("");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        } else {
                            chequeCharge.setVerifiedFlag('Y');
                            chequeCharge.setVerifiedTime(new Date());
                            chequeCharge.setVerifiedBy(UserRequestContext.getCurrentUser());
                            chequeChargeRepo.save(chequeCharge);
                            EntityResponse response = new EntityResponse();
                            response.setMessage("SALARY CHARGE WITH CODE" + " " + chequeCharge.getChargeCode() + " " + "VERIFIED SUCCESSFULLY" + " AT " + chequeCharge.getVerifiedTime());
//                            response.setMessage(HttpStatus.OK.getReasonPhrase());
                            response.setStatusCode(HttpStatus.OK.value());
                            response.setEntity(chequeCharge);
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
    public ResponseEntity<?> deleteChequeCharge(@PathVariable String id) {
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
                    Optional<ChequeCharge> chequeCharge1 = chequeChargeRepo.findById(Long.parseLong(id));
                    if (chequeCharge1.isPresent()) {
                        ChequeCharge chequeCharge = chequeCharge1.get();
                        chequeCharge.setDeletedFlag('Y');
                        chequeCharge.setDeletedTime(new Date());
                        chequeCharge.setDeletedBy(UserRequestContext.getCurrentUser());
                        chequeChargeRepo.save(chequeCharge);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("SALARY CHARGE WITH REF CODE" + " " + chequeCharge.getChargeCode() + " " + "DELETED SUCCESSFULLY" + " AT " + chequeCharge.getDeletedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(chequeCharge);
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