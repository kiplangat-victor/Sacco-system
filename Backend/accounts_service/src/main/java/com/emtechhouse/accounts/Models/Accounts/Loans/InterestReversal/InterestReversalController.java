package com.emtechhouse.accounts.Models.Accounts.Loans.InterestReversal;


import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TranHeaderRepository;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
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
@RequestMapping("/api/v1/interestreversal")
public class InterestReversalController {
    private final InterestReversalRepo interestReversalRepo;
    private final InterestReversalService interestReversalService;
    private final TranHeaderRepository tranHeaderrepo;

    public InterestReversalController(InterestReversalRepo interestReversalRepo, InterestReversalService interestReversalService, TranHeaderRepository tranHeaderrepo) {
        this.interestReversalRepo = interestReversalRepo;
        this.interestReversalService = interestReversalService;
        this.tranHeaderrepo = tranHeaderrepo;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addInterestReversalupload(@RequestBody InterestReversal interestReversal) {
        try {
            interestReversal.setStatus(CONSTANTS.ENTERED);
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
                    String validation = interestReversalService.validateInterestReversal(interestReversal);

                    System.out.println(interestReversal);

                    if (validation.trim().length() > 0) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage(validation);
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }

                    interestReversal.setEntityId(EntityRequestContext.getCurrentEntityId());
                    interestReversal.setEnteredFlag('Y');
                    interestReversal.setDeletedFlag('N');
                    interestReversal.setEnteredTime(new Date());
                    interestReversal.setEnteredBy(UserRequestContext.getCurrentUser());
                    InterestReversal newInterestReversal = interestReversalService.addInterestReversalupload(interestReversal);
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Saved, interestReversal number " + interestReversal.getInterestReversalCode() + ". Kindly verify");
                    response.setStatusCode(HttpStatus.CREATED.value());
                    response.setEntity(newInterestReversal);

                    if (newInterestReversal == null) {
                        response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                        response.setMessage("Error: Could not save salaries");
                    }
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllInterestReversaluploads(@RequestParam String fromDate, String toDate) {
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
                    List<InterestReversal> interestReversal = interestReversalRepo.fetchByDate(EntityRequestContext.getCurrentEntityId(), fromDate, toDate, 'N');
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(interestReversal);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<?> getInterestReversaluploadById(@PathVariable("id") Long id) {
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
                    InterestReversal interestReversal = interestReversalService.findById(id);
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(interestReversal);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/find/by/code/{interestReversalUploadCode}")
    public ResponseEntity<?> getInterestReversaluploadById(@PathVariable("interestReversalUploadCode") String interestReversalUploadCode) {
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
                    Optional<InterestReversal> interestReversalupload1 = interestReversalRepo.findByEntityIdAndInterestReversalUploadCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), interestReversalUploadCode, 'N');
                    if (interestReversalupload1.isPresent()) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(interestReversalupload1.get());
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


    @PutMapping("/modify")
    public ResponseEntity<?> updateInterestReversalupload(@RequestBody InterestReversal interestReversal) {
        try {
            interestReversal.setStatus(CONSTANTS.MODIFIED);
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
                    if (interestReversal.getPostedFlag() == 'Y')
                    {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("InterestReversal is already posted");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }
                    interestReversal.setModifiedBy(UserRequestContext.getCurrentUser());
                    interestReversal.setEntityId(EntityRequestContext.getCurrentEntityId());
                    Optional<InterestReversal> interestReversalupload1 = interestReversalRepo.findById(interestReversal.getId());
                    if (interestReversalupload1.isPresent()) {
                        interestReversal.setStatus(CONSTANTS.ENTERED);
                        interestReversal.setEnteredFlag('Y');
                        interestReversal.setEnteredBy(interestReversalupload1.get().getEnteredBy());
                        interestReversal.setEnteredTime(interestReversalupload1.get().getEnteredTime());
                        interestReversal.setEnteredBy(interestReversalupload1.get().getEnteredBy());
                        interestReversal.setPostedTime(interestReversalupload1.get().getPostedTime());
                        interestReversal.setPostedBy(interestReversalupload1.get().getPostedBy());
                        interestReversal.setModifiedFlag('Y');
                        interestReversal.setVerifiedFlag('N');
                        interestReversal.setVerifiedFlag_2('N');
                        interestReversal.setModifiedTime(new Date());
                        interestReversal.setModifiedBy(interestReversal.getModifiedBy());
                        interestReversalRepo.save(interestReversal);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("InterestReversal modified successfully");
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(interestReversal);
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

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteInterestReversalupload(@RequestParam String id) {
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
                    Optional<InterestReversal> interestReversalupload1 = interestReversalRepo.findById(Long.parseLong(id));
                    if (interestReversalupload1.isPresent()) {
                        InterestReversal interestReversal = interestReversalupload1.get();
                        interestReversal.setDeletedFlag('Y');
                        interestReversal.setStatus(CONSTANTS.DELETED);
                        interestReversal.setDeletedTime(new Date());
                        interestReversal.setDeletedBy(UserRequestContext.getCurrentUser());
                        interestReversalRepo.save(interestReversal);
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(interestReversal);
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

    @GetMapping("interestReversaluploads")
    public ResponseEntity<?> fetchInterestReversaluploads(@RequestParam String fromDate,
                                                @RequestParam String toDate) {
        try {
            EntityResponse response = new EntityResponse<>();
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
                List<InterestReversal> interestReversals = interestReversalService.filterBydate(fromDate, toDate, EntityRequestContext.getCurrentEntityId());
                if (interestReversals.size() > 0) {
                    response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(interestReversals);
                    return new ResponseEntity<>(response, HttpStatus.OK);

                } else if (interestReversals.isEmpty()) {
                    response.setEntity(interestReversals);
                    response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                    response.setStatusCode(HttpStatus.NO_CONTENT.value());
                }

            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error ", e.getMessage());
            EntityResponse response = new EntityResponse<>();
            response.setMessage(e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity("");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("approvelist")
    public ResponseEntity<?> fetchInterestReversaluploads() {
        try {
            EntityResponse response = new EntityResponse<>();
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                List<InterestReversal> interestReversalList = interestReversalRepo.getApprovalList();
                if (interestReversalList.size() > 0) {
                    response.setMessage("Welcome " + UserRequestContext.getCurrentUser() + ", There are " + interestReversalList.size() + " InterestReversal Upload(s) For Approval.");
                    response.setStatusCode(HttpStatus.FOUND.value());
                    response.setEntity(interestReversalList);
                } else {
                    response.setMessage("Welcome " + UserRequestContext.getCurrentUser() + ", There are " + interestReversalList.size() + " InterestReversal Upload(s) For Approval.");
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setEntity("Total InterestReversal Upload " + interestReversalList.size());
                }
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error ", e.getMessage());
            EntityResponse response = new EntityResponse<>();
            response.setMessage(e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity("");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("verify/interestReversal-upload/{interestReversalCode}")
    public ResponseEntity<?> verifyUpload(@PathVariable("interestReversalCode") String interestReversalCode) {
        try {
            EntityResponse response = interestReversalService.verify(interestReversalCode);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error ", e.getMessage());
            EntityResponse response = new EntityResponse<>();
            response.setMessage(e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity("");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("post/interestReversal-upload/{interestReversalCode}")
    public ResponseEntity<?> post(@PathVariable("interestReversalCode")  String interestReversalUploadCode) {
        try {
            EntityResponse response = interestReversalService.post(interestReversalUploadCode);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error ", e.getMessage());
            EntityResponse response = new EntityResponse<>();
            response.setMessage(e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity("");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("total/interestReversal/uploads/count")
    public ResponseEntity<EntityResponse<?>> interestReversalUploadsCount() {
        try {
            return ResponseEntity.ok().body(interestReversalService.interestReversalUploadsCount());
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    @PutMapping("reject/interestReversal/upload/{interestReversalUploadCode}")
    public ResponseEntity<?> rejectInterestReversalUpload(@PathVariable("interestReversalUploadCode") String interestReversalUploadCode) {
        try {
            EntityResponse response = interestReversalService.rejectInterestReversalUpload(interestReversalUploadCode);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error ", e.getMessage());
            EntityResponse response = new EntityResponse<>();
            response.setMessage(e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity("");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}