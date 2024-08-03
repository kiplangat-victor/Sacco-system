package com.emtechhouse.accounts.TransactionService.BatchTransaction;

import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TranHeaderRepository;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
//@CrossOrigin
@RequestMapping("/api/v1/batchtransaction")

public class BatchtransactionController {
    private final BatchtransactionRepository batchtransactionRepo;
    private final BatchtransactionService batchtransactionService;
    private final TranHeaderRepository tranHeaderrepo;

    public BatchtransactionController(BatchtransactionRepository batchtransactionRepo, BatchtransactionService batchtransactionService, TranHeaderRepository tranHeaderrepo) {
        this.batchtransactionRepo = batchtransactionRepo;
        this.batchtransactionService = batchtransactionService;
        this.tranHeaderrepo = tranHeaderrepo;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addBatchtransaction(@RequestBody Batchtransaction batchtransaction) {
        try {
            batchtransaction.setStatus(CONSTANTS.ENTERED);
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
                    String validation = batchtransactionService.validateBatch(batchtransaction);

                    if (validation.trim().length() > 0) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage(validation);
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }

                    batchtransaction.setEntityId(EntityRequestContext.getCurrentEntityId());
                    batchtransaction.setEnteredFlag('Y');
                    batchtransaction.setDeletedFlag('N');
                    batchtransaction.setEnteredTime(new Date());
                    batchtransaction.setEnteredBy(UserRequestContext.getCurrentUser());
                    Batchtransaction newBatchtransaction = batchtransactionService.addBatchtransaction(batchtransaction);
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Saved, batch number " + batchtransaction.getBatchUploadCode() + ". Kindly request for verification");
                    response.setStatusCode(HttpStatus.CREATED.value());
                    response.setEntity(newBatchtransaction);

                    if (newBatchtransaction == null) {
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
    public ResponseEntity<?> getAllBatchtransactions(@RequestParam String fromDate, String toDate) {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                EntityResponse response = new EntityResponse();
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    List<Batchtransaction> batchtransaction = batchtransactionRepo.fetchByDate(EntityRequestContext.getCurrentEntityId(), fromDate, toDate, 'N');
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(batchtransaction);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<?> getBatchtransactionById(@PathVariable("id") Long id) {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                EntityResponse response = new EntityResponse();
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    Batchtransaction batchtransaction = batchtransactionService.findById(id);
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(batchtransaction);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/find/by/code/{batchUploadCode}")
    public ResponseEntity<?> getBatchtransactionById(@PathVariable("batchUploadCode") String batchUploadCode) {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                EntityResponse response = new EntityResponse();
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    Optional<Batchtransaction> batchtransaction1 = batchtransactionRepo.findByEntityIdAndBatchUploadCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), batchUploadCode, 'N');
                    if (batchtransaction1.isPresent()) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(batchtransaction1.get());
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
    public ResponseEntity<?> updateBatchtransaction(@RequestBody Batchtransaction batchtransaction) {
        try {
            batchtransaction.setStatus(CONSTANTS.MODIFIED);
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
                    batchtransaction.setModifiedBy(UserRequestContext.getCurrentUser());
                    batchtransaction.setEntityId(EntityRequestContext.getCurrentEntityId());
                    Optional<Batchtransaction> batchtransaction1 = batchtransactionRepo.findById(batchtransaction.getId());
                    if (batchtransaction1.isPresent()) {
                        batchtransaction.setPostedTime(batchtransaction1.get().getPostedTime());
                        batchtransaction.setPostedFlag('Y');
                        batchtransaction.setPostedBy(batchtransaction1.get().getPostedBy());
                        batchtransaction.setModifiedFlag('Y');
                        batchtransaction.setVerifiedFlag('N');
                        batchtransaction.setModifiedTime(new Date());
                        batchtransaction.setModifiedBy(batchtransaction.getModifiedBy());
                        batchtransactionService.updateBatchtransaction(batchtransaction);
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(batchtransaction);
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
    public ResponseEntity<?> deleteBatchtransaction(@RequestParam String id) {
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
                    Optional<Batchtransaction> batchtransaction1 = batchtransactionRepo.findById(Long.parseLong(id));
                    if (batchtransaction1.isPresent()) {
                        Batchtransaction batchtransaction = batchtransaction1.get();
                        batchtransaction.setDeletedFlag('Y');
                        batchtransaction.setStatus(CONSTANTS.DELETED);
                        batchtransaction.setDeletedTime(new Date());
                        batchtransaction.setDeletedBy(UserRequestContext.getCurrentUser());
                        batchtransactionRepo.save(batchtransaction);
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(batchtransaction);
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

    @GetMapping("batchtransactions")
    public ResponseEntity<?> fetchBatchtransactions(@RequestParam String fromDate,
                                                    @RequestParam String toDate,
                                                    @RequestParam String action) {
        try {
            EntityResponse response = new EntityResponse<>();
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                log.info("User Name not present in the Request Header");
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                List<Batchtransaction> batchtransactions = batchtransactionService.filterBydate(fromDate, toDate, EntityRequestContext.getCurrentEntityId(), action);
                if (batchtransactions.size() > 0) {
                    response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(batchtransactions);
                    return new ResponseEntity<>(response, HttpStatus.OK);

                } else if (batchtransactions.isEmpty()) {
                    response.setEntity(batchtransactions);
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
    public ResponseEntity<?> fetchBatchtransactions() {
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
                List<Batchtransaction> batchtransactions = batchtransactionRepo.getApprovalList();
                if (batchtransactions.size() > 0) {
                    response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(batchtransactions);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else if (batchtransactions.isEmpty()) {
                    response.setEntity(batchtransactions);
                    response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                    response.setStatusCode(HttpStatus.NO_CONTENT.value());
                }
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            EntityResponse response = new EntityResponse<>();
            response.setMessage(e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("verify/batch-upload/{batchCode}")
    public ResponseEntity<?> verifyUpload(@PathVariable("batchCode") String batchCode) {
        try {
            EntityResponse response = batchtransactionService.verify(batchCode);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            EntityResponse response = new EntityResponse<>();
            response.setMessage(e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("post/batch-upload/{batchCode}")
    public ResponseEntity<?> post(@PathVariable("batchCode") String batchUploadCode) {
        try {
            EntityResponse response = batchtransactionService.post(batchUploadCode);
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

    @GetMapping("all/unverified/batch/transactions/list")
    public ResponseEntity<?> findAllUnVerifiedBatchTransactions(
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate
    ) throws IOException {
        try {
            EntityResponse response = new EntityResponse();
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
            } else if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            } else {
                if (fromDate == null || toDate == null) {
                    response.setMessage("You must provide a date range !");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                } else {
                    List<Batchtransaction> batchtransactionList = batchtransactionRepo.findAllBatchTransactionsByEntityIdAndDateRange(EntityRequestContext.getCurrentEntityId(), fromDate, toDate);
                    if (batchtransactionList.size() > 0) {
                        response.setMessage("Welcome " + UserRequestContext.getCurrentUser() + ", There are " + batchtransactionList.size() + " Batch Transaction(s) For Approval.");
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(batchtransactionList);
                    } else {
                        response.setMessage("Welcome " + UserRequestContext.getCurrentUser() + ", There are " + batchtransactionList.size() + " Batch Transaction(s) For Approval.");
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity(batchtransactionList.size());
                    }
                }

            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            EntityResponse response = new EntityResponse();
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("total/batch/transactions/count")
    public ResponseEntity<EntityResponse<?>> batchTransactionsCount() {
        try {
            return ResponseEntity.ok().body(batchtransactionService.batchTransactionsCount());
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    @PutMapping("reject/batch/upload/{batchUploadCode}")
    public ResponseEntity<?> rejectBatchUpload(@PathVariable("batchUploadCode") String batchUploadCode) {
        try {
            EntityResponse response = batchtransactionService.rejectBatchUpload(batchUploadCode);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            EntityResponse response = new EntityResponse<>();
            response.setMessage(e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
