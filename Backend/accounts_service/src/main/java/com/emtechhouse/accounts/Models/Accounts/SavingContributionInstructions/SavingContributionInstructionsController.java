package com.emtechhouse.accounts.Models.Accounts.SavingContributionInstructions;

import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
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
@RequestMapping("saving-instructions")
public class SavingContributionInstructionsController {
    @Autowired
    private  SavingContributionInstructionsService savingService;
    @Autowired
    private SavingContributionInstructionsRepo savingRepo;

    public SavingContributionInstructionsController() {

    }

    @PostMapping("/add")
    public ResponseEntity<?> addSavingContributionInstructions(@RequestBody SavingContributionInstructions saving) {
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
                    Optional<SavingContributionInstructions> saving1 = savingRepo.findMaxByCustomerCode(saving.getCustomerCode());
                    if (saving1.isPresent()) {
                        SavingContributionInstructions instructions = saving1.get();
                        String customerCode = instructions.getCustomerCode();
                        String savingCode = instructions.getSavingCode();
                        saving.setSavingCode(customerCode+"-"+(Integer.parseInt(savingCode.substring(savingCode.length() - 1))+1));
                    } else {
                        saving.setSavingCode(saving.getCustomerCode()+"-1");
                    }
                    saving.setStatus(CONSTANTS.ACTIVE);
                    saving.setPostedFlag('Y');
                    saving.setPostedBy(UserRequestContext.getCurrentUser());
                    saving.setPostedTime(new Date());
                    SavingContributionInstructions addSavingContributionInstructions = savingRepo.save(saving);
                    EntityResponse response = new EntityResponse();
                    response.setMessage("SAVING INSTRUCTIONS WITH CODE " + saving.getSavingCode() + " CREATED SUCCESSFULLY AT " + saving.getPostedTime());
                    response.setStatusCode(HttpStatus.CREATED.value());
                    response.setEntity(addSavingContributionInstructions);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllSavingContributionInstructionss() {
        try {
            EntityResponse response = new EntityResponse();
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    List<SavingContributionInstructions> saving = savingRepo.findByDeletedFlag('N');
                    if (saving.size() > 0) {
                        response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.FOUND.value());
                    } else {
                        response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    }
                    response.setEntity(saving);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    @GetMapping("/all/unverified")
    public ResponseEntity<?> getAllUnverifiedSavingContributionInstructionss() {
        try {
            EntityResponse response = new EntityResponse();
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    List<SavingContributionInstructions> saving = savingRepo.findByDeletedFlagAndVerifiedFlag('N', 'N');
                    if (saving.size() > 0) {
                        response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.FOUND.value());
                    } else {
                        response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    }
                    response.setEntity(saving);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    @GetMapping("/all/due")
    public ResponseEntity<?> getAllDueSavingContributionInstructionss() {
        try {
            EntityResponse response = new EntityResponse();
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    List<SavingContributionInstructions> saving = savingRepo.findAllDue();
                    if (saving.size() > 0) {
                        response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.FOUND.value());
                    } else {
                        response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    }
                    response.setEntity(saving);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    @GetMapping("/execute/for/customer")
    public ResponseEntity<?> executeSavingContributionInstructions(@RequestParam String customerCode) {
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
                    Optional<SavingContributionInstructions> saving = savingRepo.findDueByCustomerCode(customerCode, 15);
                    if (saving.isPresent() ) {
                        response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.FOUND.value());
                        savingService.executeOne(saving.get());
                        response.setEntity(saving.get());
                    } else {
                        response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    }
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    @GetMapping("/all/due/for/customer")
    public ResponseEntity<?> getDueSavingContributionInstructions(@RequestParam String customerCode) {
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
                    System.out.println("Before call");
                    Optional<SavingContributionInstructions> saving = savingRepo.findDueByCustomerCode(customerCode, 15);
//                    System.out.println(saving..size());
                    if (saving.isPresent() ) {
                        response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(saving.get());
                    } else {
                        response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    }
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/check/saving_charge/{saving_charge_code}")
    public ResponseEntity<?> findsaving_charge(@PathVariable("saving_charge_code") String saving_charge_code) {
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
                    Optional<SavingContributionInstructions> searchsaving_charge = savingRepo.findBySavingCode(saving_charge_code);
                    if (!searchsaving_charge.isPresent()) {
                        response.setMessage("SAVING INSTRUCTIONS WITH CODE " + saving_charge_code + " AVAILABLE FOR REGISTRATION");
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity("");

                    } else {
                        response.setMessage("SAVING INSTRUCTIONS WITH CODE " + saving_charge_code + " ALREADY REGISTERED");
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(searchsaving_charge.get());
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
    public ResponseEntity<?> getSavingCodeById(@PathVariable("id") Long id) {
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

                    Optional<SavingContributionInstructions> searchSavingContributionInstructionsId = savingRepo.findById(id);
                    if (!searchSavingContributionInstructionsId.isPresent()) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("SAVING INSTRUCTIONS DATA DOES NOT EXIST!! PLEASE PROVIDE A VALID REGISTERED SAVING INSTRUCTIONS CODE");
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);

                    } else {
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(searchSavingContributionInstructionsId.get());
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
    public ResponseEntity<?> updateSavingContributionInstructions(@RequestBody SavingContributionInstructions saving) {
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
                    saving.setModifiedBy(UserRequestContext.getCurrentUser());
                    Optional<SavingContributionInstructions> saving1 = savingRepo.findById(saving.getId());
                    if (saving1.isPresent()) {
                        saving.setPostedTime(saving1.get().getPostedTime());
                        saving.setPostedFlag(saving1.get().getPostedFlag());
                        saving.setPostedBy(saving1.get().getPostedBy());
                        saving.setVerifiedFlag(saving1.get().getVerifiedFlag());
                        saving.setModifiedFlag('Y');
                        saving.setModifiedTime(new Date());
                        saving.setModifiedBy(saving.getModifiedBy());
                        savingRepo.save(saving);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("SAVING INSTRUCTIONS WITH CODE " + saving.getSavingCode() + "MODIFIED SUCCESSFULLY AT " + saving.getModifiedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(saving);
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
    public ResponseEntity<?> verify(@PathVariable("id") Long id) {
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
                    Optional<SavingContributionInstructions> saving1 = savingRepo.findById(id);
                    if (saving1.isPresent()) {
                        SavingContributionInstructions saving = saving1.get();
                        if (saving.getPostedBy().equalsIgnoreCase(UserRequestContext.getCurrentUser())) {
                            EntityResponse response = new EntityResponse();
                            response.setMessage("You Can Not Verify What you initiated");
                            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                            response.setEntity("");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        } else {
                            if (saving1.get().getVerifiedFlag().equals('Y')) {
                                EntityResponse response = new EntityResponse();
                                response.setMessage("SAVING INSTRUCTIONS WITH CODE " + saving1.get().getSavingCode() + " ALREADY VERIFIED ON " + saving1.get().getVerifiedTime());
                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                response.setEntity("");
                                return new ResponseEntity<>(response, HttpStatus.OK);
                            } else {
                                saving.setVerifiedFlag('Y');
                                saving.setVerifiedTime(new Date());
                                saving.setVerifiedBy(UserRequestContext.getCurrentUser());
                                savingRepo.save(saving);
                                EntityResponse response = new EntityResponse();
                                response.setMessage("SAVING CHARGE WITH CODE " + saving.getSavingCode() + " VERIFIED SUCCESSFULLY AT " + saving.getVerifiedTime());
                                response.setStatusCode(HttpStatus.OK.value());
                                response.setEntity(saving);
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


    @PutMapping("/execute/{id}")
    public ResponseEntity<?> execute(@PathVariable("id") Long id) {
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
                    Optional<SavingContributionInstructions> saving1 = savingRepo.findByIdDue(id, 15);
                    if (saving1.isPresent()) {
                        System.out.println("Found instruction");
                        SavingContributionInstructions saving = saving1.get();
                        return new ResponseEntity<>(savingService.executeOne(saving), HttpStatus.OK);
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
    public ResponseEntity<?> deleteSavingContributionInstructions(@PathVariable String id) {
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
                    Optional<SavingContributionInstructions> saving1 = savingRepo.findById(Long.parseLong(id));
                    if (saving1.isPresent()) {
                        SavingContributionInstructions saving = saving1.get();
                        saving.setDeletedFlag('Y');
                        saving.setDeletedTime(new Date());
                        saving.setDeletedBy(UserRequestContext.getCurrentUser());
                        savingRepo.save(saving);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("SAVING INSTRUCTIONS WITH REF CODE " + saving.getSavingCode() + " DELETED SUCCESSFULLY AT " + saving.getDeletedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(saving);
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
    @GetMapping("/all/unverified/instructions/due")
    public EntityResponse<?> getAllUnVerifiedInstructionsDue() {
        try {
            EntityResponse response = new EntityResponse();
            String currentUser =UserRequestContext.getCurrentUser();
            String currentEntityId = EntityRequestContext.getCurrentEntityId();
            if (currentUser.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            } else {
                if(currentEntityId.isEmpty()) {
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                } else {
                    List<SavingContributionInstructions> savings = savingRepo.findAllUnVerifiedInstructions();
                    if (savings.size() > 0) {
                        response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(savings);
                    } else {
                        response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    }
                }
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    @GetMapping("total/savings/contribution/instructions")
    public ResponseEntity<EntityResponse<?>> totalSavingContributionInstructions() {
        try {
            return ResponseEntity.ok().body(savingService.totalSavingContributionInstructions());
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("reject/savings/contribution/instructions/{id}")
    public ResponseEntity<EntityResponse<?>> rejectSavingContributionInstructions(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok().body(savingService.rejectSavingContributionInstructions(id));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
}
