package com.emtechhouse.accounts.TransactionService.ChequeComponent;


import com.emtechhouse.accounts.TransactionService.Responses.ChequeInfo;
import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@Api(value = "/Cheque API", tags = "Cheque API")
@RequestMapping("/api/v1/chequebook")
public class ChequebookController {
    private final ChequebookRepo chequebookRepo;
    private final ChequebookService chequebookService;
    public ChequebookController(ChequebookRepo chequebookRepo, ChequebookService chequebookService) {
        this.chequebookRepo = chequebookRepo;
        this.chequebookService = chequebookService;
    }
    @PostMapping("/add")
//    @PreAuthorize("hasRole('ROLE_SUPERUSER')")
    public ResponseEntity<?> addChequebook(@RequestBody Chequebook chequebook) {
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
//                    Check if code exist
                    Optional<Chequebook> chequebook1 = chequebookRepo.findByEntityIdAndMicrCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), chequebook.getMicrCode(), 'N');
                    if (chequebook1.isPresent()){
                        EntityResponse response = new EntityResponse();
                        response.setMessage("Code Exists");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }else{
                        if (chequebook.getStartSerialNo()>chequebook.getEndSerialNo()){
                            EntityResponse response = new EntityResponse();
                            response.setMessage("Beginning Num Can not be greater than End Number");
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            response.setEntity("");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        }else{
                            //Check if Range Exist
                            Integer begin = chequebook.getStartSerialNo();
                            Integer end = chequebook.getEndSerialNo();
                            Integer chqpages = end - begin;
                            String chqLvsStat = StringUtils.repeat("u", chqpages);
                            chequebook.setChqLvsStat(chqLvsStat);
                            chequebook.setNoOfLeafs(chqpages);
                            chequebook.setPostedBy(UserRequestContext.getCurrentUser());
                            chequebook.setEntityId(EntityRequestContext.getCurrentEntityId());
                            chequebook.setPostedFlag('Y');
                            chequebook.setPostedTime(new Date());

                            Chequebook newChequebook = chequebookService.addChequebook(chequebook);
                            EntityResponse response = new EntityResponse();
                            response.setMessage(HttpStatus.CREATED.getReasonPhrase());
                            response.setStatusCode(HttpStatus.CREATED.value());
                            response.setEntity(newChequebook);
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
    public ResponseEntity<?> getAllChequebooks() {
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
                    List<Chequebook> chequebook = chequebookRepo.findByEntityIdAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), 'N');
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(chequebook);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    @GetMapping("/find/{id}")
    public ResponseEntity<?> getChequebookById(@PathVariable("id") Long id) {
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
                    Chequebook chequebook = chequebookService.findById(id);
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(chequebook);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("/modify")
    public ResponseEntity<?> updateChequebook(@RequestBody Chequebook chequebook) {
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
                    chequebook.setModifiedBy(UserRequestContext.getCurrentUser());
                    chequebook.setEntityId(EntityRequestContext.getCurrentEntityId());
                    Optional<Chequebook> chequebook1 = chequebookRepo.findById(chequebook.getId());
                    if (chequebook1.isPresent()) {
                        chequebook.setPostedTime(chequebook1.get().getPostedTime());
                        chequebook.setPostedFlag('Y');
                        chequebook.setPostedBy(chequebook1.get().getPostedBy());
                        chequebook.setModifiedFlag('Y');
                        chequebook.setVerifiedFlag('N');
                        chequebook.setModifiedTime(new Date());
                        chequebook.setModifiedBy(chequebook.getModifiedBy());
                        chequebookService.updateChequebook(chequebook);
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(chequebook);
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
                    log.info("Checking if checque exist...");
                    Optional<Chequebook> chequebook1 = chequebookRepo.findById(Long.parseLong(id));
                    if (chequebook1.isPresent()) {
                        log.info("Checking found.");
                        Chequebook chequebook = chequebook1.get();
                        //                    Check Maker Checker
                        System.out.println("chequebook :: "+chequebook);
                        if (chequebook.getPostedBy().equalsIgnoreCase(UserRequestContext.getCurrentUser())){
                            EntityResponse response = new EntityResponse();
                            response.setMessage("You Can Not Verify What you initiated");
                            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                            response.setEntity("");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        }else{
                            chequebook.setVerifiedFlag('Y');
                            chequebook.setVerifiedTime(new Date());
                            chequebook.setVerifiedBy(UserRequestContext.getCurrentUser());
                            chequebookRepo.save(chequebook);
                            EntityResponse response = new EntityResponse();
                            response.setMessage(HttpStatus.OK.getReasonPhrase());
                            response.setStatusCode(HttpStatus.OK.value());
                            response.setEntity(chequebook);
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        }
                    } else {
                        log.info("Checking Not found.");
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e.getMessage());
            return null;
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteChequebook(@PathVariable String id) {
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
                    Optional<Chequebook> chequebook1 = chequebookRepo.findById(Long.parseLong(id));
                    if (chequebook1.isPresent()) {
                        Chequebook chequebook = chequebook1.get();
                        chequebook.setDeletedFlag('Y');
                        chequebook.setDeletedTime(new Date());
                        chequebook.setDeletedBy(UserRequestContext.getCurrentUser());
                        chequebookRepo.save(chequebook);
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(chequebook);
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
    @GetMapping("cheque/{chequeNo}")
    public ResponseEntity<?> getAccountBalance(@PathVariable String chequeNo) {
        EntityResponse response = new EntityResponse<>();
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                log.info("User Name not present in the Request Header");
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return new ResponseEntity<>(response, HttpStatus.OK);

            } else if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                log.info("Entity not present in the Request Header");
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return new ResponseEntity<>(response, HttpStatus.OK);

            } else {

                Integer isExist = chequebookRepo.checkChequeExistance(chequeNo);
                if (isExist == 1) {
                    log.info("Cheque found ");
                    log.info("Fetching Cheque information...");
                    response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                    response.setStatusCode(HttpStatus.FOUND.value());
                    ChequeInfo chequebookInfo = chequebookRepo.findChequebookInfo(chequeNo);
                    response.setEntity(chequebookInfo);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    log.info("Cheque not found...");
                    response.setMessage("Cheque does not exist");
                    response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error ", e.getMessage());
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.EXPECTATION_FAILED.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

    }
}