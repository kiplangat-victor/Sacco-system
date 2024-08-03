package com.emtechhouse.System.SalaryCharges;

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
@RequestMapping("salarycharges")
public class SalaryChargeController {
    private final SalaryChargeService salaryChargeService;
    private final SalaryChargeRepo salaryChargeRepo;

    public SalaryChargeController(SalaryChargeService salaryChargeService, SalaryChargeRepo salaryChargeRepo) {
        this.salaryChargeService = salaryChargeService;
        this.salaryChargeRepo = salaryChargeRepo;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addSalaryCharge(@RequestBody SalaryCharge salaryCharge) {
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
                    Optional<SalaryCharge> salaryCharge1 = salaryChargeRepo.findBySalaryChargeCode(salaryCharge.getSalaryChargeCode());
                    if (salaryCharge1.isPresent()) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("SALARY CHARGES WITH CODE " + salaryCharge.getSalaryChargeCode() + " ALREADY REGISTERED! ENTER UNIQUE CODE TO CONTINUE");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        salaryCharge.setPostedBy(UserRequestContext.getCurrentUser());
                        salaryCharge.setEntityId(EntityRequestContext.getCurrentEntityId());
                        salaryCharge.setPostedFlag('Y');
                        salaryCharge.setPostedTime(new Date());
                        SalaryCharge addSalaryCharge = salaryChargeService.addSalaryCharge(salaryCharge);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("SALARY CHARGES WITH CODE " + salaryCharge.getSalaryChargeCode() + " CREATED SUCCESSFULLY AT " + salaryCharge.getPostedTime());
                        response.setStatusCode(HttpStatus.CREATED.value());
                        response.setEntity(addSalaryCharge);
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
    public ResponseEntity<?> getAllSalaryCharges() {
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
                    List<SalaryCharge> salaryCharge = salaryChargeRepo.findByEntityIdAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), 'N');
                    if (salaryCharge.size() > 0) {
                        response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(salaryCharge);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity(salaryCharge);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/check/salary_charge/{salary_charge_code}")
    public ResponseEntity<?> findsalary_charge(@PathVariable("salary_charge_code") String salary_charge_code) {
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
                    Optional<SalaryCharge> searchsalary_charge = salaryChargeRepo.findBySalaryChargeCode(salary_charge_code);
                    if (!searchsalary_charge.isPresent()) {
                        response.setMessage("SALARY CHARGES WITH CODE " + salary_charge_code + " AVAILABLE FOR REGISTRATION");
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity("");

                    } else {
                        SalaryCharge salaryCharge = salaryChargeService.findsalary_charge(salary_charge_code);
                        response.setMessage("SALARY CHARGES WITH CODE " + salary_charge_code + " ALREADY REGISTERED");
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(salaryCharge);
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
    public ResponseEntity<?> getSalaryChargeCodeById(@PathVariable("id") Long id) {
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

                    Optional<SalaryCharge> searchSalaryChargeId = salaryChargeRepo.findById(id);
                    if (!searchSalaryChargeId.isPresent()) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("SALARY CHARGES DATA DOES NOT EXIST!! PLEASE PROVIDE A VALID REGISTERED SALARY CHARGES CODE");
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);

                    } else {
                        SalaryCharge salaryCharge = salaryChargeService.findById(id);
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(salaryCharge);
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
    public ResponseEntity<?> updateSalaryCharge(@RequestBody SalaryCharge salaryCharge) {
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
                    salaryCharge.setModifiedBy(UserRequestContext.getCurrentUser());
                    salaryCharge.setEntityId(EntityRequestContext.getCurrentEntityId());
                    Optional<SalaryCharge> salaryCharge1 = salaryChargeRepo.findById(salaryCharge.getId());
                    if (salaryCharge1.isPresent()) {
                        salaryCharge.setPostedTime(salaryCharge1.get().getPostedTime());
                        salaryCharge.setPostedFlag(salaryCharge1.get().getPostedFlag());
                        salaryCharge.setPostedBy(salaryCharge1.get().getPostedBy());
                        salaryCharge.setVerifiedFlag(salaryCharge1.get().getVerifiedFlag());
                        salaryCharge.setModifiedFlag('Y');
                        salaryCharge.setModifiedTime(new Date());
                        salaryCharge.setModifiedBy(salaryCharge.getModifiedBy());
                        salaryChargeService.updateSalaryCharge(salaryCharge);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("SALARY CHARGES WITH CODE " + salaryCharge.getSalaryChargeCode() + "MODIFIED SUCCESSFULLY AT " + salaryCharge.getModifiedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(salaryCharge);
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
    public ResponseEntity<?> verify(@PathVariable Long id) {
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
                    Optional<SalaryCharge> salaryCharge1 = salaryChargeRepo.findById(id);
                    if (salaryCharge1.isPresent()) {
                        SalaryCharge salaryCharge = salaryCharge1.get();
                        if (salaryCharge.getPostedBy().equalsIgnoreCase(UserRequestContext.getCurrentUser())) {
                            EntityResponse response = new EntityResponse();
                            response.setMessage("You Can Not Verify What you initiated");
                            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                            response.setEntity("");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        } else {
                            if (salaryCharge1.get().getVerifiedFlag().equals('Y')) {
                                EntityResponse response = new EntityResponse();
                                response.setMessage("SALARY CHARGES WITH CODE " + salaryCharge1.get().getSalaryChargeCode() + " ALREADY VERIFIED ON " + salaryCharge1.get().getVerifiedTime());
                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                response.setEntity("");
                                return new ResponseEntity<>(response, HttpStatus.OK);
                            } else {
                                salaryCharge.setVerifiedFlag('Y');
                                salaryCharge.setVerifiedTime(new Date());
                                salaryCharge.setVerifiedBy(UserRequestContext.getCurrentUser());
                                salaryChargeRepo.save(salaryCharge);
                                EntityResponse response = new EntityResponse();
                                response.setMessage("SALARY CHARGE WITH CODE " + salaryCharge.getSalaryChargeCode() + " VERIFIED SUCCESSFULLY AT " + salaryCharge.getVerifiedTime());
                                response.setStatusCode(HttpStatus.OK.value());
                                response.setEntity(salaryCharge);
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
    public ResponseEntity<?> deleteSalaryCharge(@PathVariable String id) {
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
                    Optional<SalaryCharge> salaryCharge1 = salaryChargeRepo.findById(Long.parseLong(id));
                    if (salaryCharge1.isPresent()) {
                        SalaryCharge salaryCharge = salaryCharge1.get();
                        salaryCharge.setDeletedFlag('Y');
                        salaryCharge.setDeletedTime(new Date());
                        salaryCharge.setDeletedBy(UserRequestContext.getCurrentUser());
                        salaryChargeRepo.save(salaryCharge);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("SALARY CHARGES WITH REF CODE " + salaryCharge.getSalaryChargeCode() + " DELETED SUCCESSFULLY AT " + salaryCharge.getDeletedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(salaryCharge);
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
