package emt.sacco.middleware.SecurityImpl.Tellersaccounts;

import emt.sacco.middleware.SecurityImpl.Sec.SwitchUsers;
import emt.sacco.middleware.Utils.Config.UserRequestContext;
import emt.sacco.middleware.Utils.EntityRequestContext;
import emt.sacco.middleware.Utils.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/telleraccount")

@Slf4j
public class TelleraccountController {
    private final TelleraccountRepo telleraccountRepo;
    private final TelleraccountService telleraccountService;

    public TelleraccountController(TelleraccountRepo telleraccountRepo, TelleraccountService telleraccountService) {
        this.telleraccountRepo = telleraccountRepo;
        this.telleraccountService = telleraccountService;
    }

    @PostMapping("/attach")
    public ResponseEntity<?> attach(@RequestBody Telleraccount telleraccount) {
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
//                    Check if Exist
                    Optional<Telleraccount> tellerdata = telleraccountRepo.findByEntityIdAndTellerIdAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), telleraccount.getTellerId(), 'N');
                    Optional<Telleraccount> tellerdata2 = telleraccountRepo.findByEntityIdAndTellerAcAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), telleraccount.getTellerAc(), 'N');
                    if (tellerdata.isPresent()) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("Teller with ID: " + telleraccount.getTellerId() + " Already Mapped to Account: " + tellerdata.get().getTellerAc());
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity(telleraccount);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        if (tellerdata2.isPresent()) {
                            EntityResponse response = new EntityResponse();
                            response.setMessage("Teller with Account : " + tellerdata.get().getTellerAc() + " Already Mapped to Teller ID: " + telleraccount.getTellerId());
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            response.setEntity(telleraccount);
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        } else {
                            telleraccount.setPostedBy(UserRequestContext.getCurrentUser());
                            telleraccount.setEntityId(EntityRequestContext.getCurrentEntityId());
                            telleraccount.setPostedFlag('Y');
                            telleraccount.setPostedTime(new Date());
                            Telleraccount tellerAccount = telleraccountRepo.save(telleraccount);
                            SwitchUsers switchUsers = new SwitchUsers();
                            switchUsers.setTellerAccount(tellerAccount.getTellerAc());
                            EntityResponse response = new EntityResponse();
                            response.setMessage("Teller Account: " + tellerAccount.getTellerAc() + " With Teller ID: " + tellerAccount.getTellerId() + " Mapped Successfully to User:- " + telleraccount.getTellerUserName());
                            response.setStatusCode(HttpStatus.CREATED.value());
                            response.setEntity(tellerAccount);
                            return new ResponseEntity<>(response, HttpStatus.OK);
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
    public ResponseEntity<?> getAllAccessgroups() {
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
                    List<Telleraccount> telleraccount = telleraccountRepo.findByEntityIdAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), 'N');
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(telleraccount);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<?> getAccessgroupById(@PathVariable("id") Long id) {
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
                    Telleraccount telleraccount = telleraccountService.findById(id);
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(telleraccount);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/find/tellerUserName/{tellerUserName}")
    public ResponseEntity<?> getTellerByUsername(@PathVariable("tellerUserName") String tellerUserName) {
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
                    Optional<Telleraccount> searchUsername = telleraccountRepo.findByEntityIdAndDeletedFlagAndTellerUserName(EntityRequestContext.getCurrentEntityId(), 'N', tellerUserName);
                    if (searchUsername.isPresent()){
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(searchUsername);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("Teller with username " + searchUsername.get().getTellerUserName() + " Not Registered");
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity(searchUsername);
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
    public ResponseEntity<?> updateAccessgroup(@RequestBody Telleraccount telleraccountChange) {
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
                    telleraccountChange.setModifiedBy(UserRequestContext.getCurrentUser());
                    telleraccountChange.setEntityId(EntityRequestContext.getCurrentEntityId());
                    Optional<Telleraccount> telleraccount1 = telleraccountRepo.findById(telleraccountChange.getId());
                    if (telleraccount1.isPresent()) {
                        Telleraccount telleraccount = telleraccount1.get();
                        telleraccount.setExcessAc(telleraccountChange.getExcessAc());
                        telleraccount.setShortageAc(telleraccountChange.getShortageAc());
                        telleraccount.setTellerAc(telleraccountChange.getTellerAc());
                        telleraccount.setModifiedFlag('Y');
                        telleraccount.setVerifiedFlag('N');
                        telleraccount.setModifiedTime(new Date());
                        telleraccount.setModifiedBy(UserRequestContext.getCurrentUser());
                        telleraccountRepo.save(telleraccount);
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(telleraccount);
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
                    Optional<Telleraccount> telleraccount1 = telleraccountRepo.findById(id);
                    if (telleraccount1.isPresent()) {
                        Telleraccount telleraccount = telleraccount1.get();
                        //                    Check Maker Checker
                        if (telleraccount.getPostedBy().equalsIgnoreCase(UserRequestContext.getCurrentUser())) {
                            EntityResponse response = new EntityResponse();
                            response.setMessage("You Can Not Verify What you initiated");
                            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                            response.setEntity("");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        } else {
                            telleraccount.setVerifiedFlag('Y');
                            telleraccount.setVerifiedTime(new Date());
                            telleraccount.setVerifiedBy(UserRequestContext.getCurrentUser());
                            telleraccountRepo.save(telleraccount);
                            EntityResponse response = new EntityResponse();
                            response.setMessage(HttpStatus.OK.getReasonPhrase());
                            response.setStatusCode(HttpStatus.OK.value());
                            response.setEntity(telleraccount);
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
    public ResponseEntity<?> deleteAccessgroup(@PathVariable Long id) {
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
                    Optional<Telleraccount> telleraccount = telleraccountRepo.findById(id);
                    if (telleraccount.isPresent()) {
                        telleraccountRepo.deleteById(id);
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(telleraccount);
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
