package com.emtechhouse.accounts.TransactionService.SalaryUploads;


import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TranHeaderRepository;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
//@CrossOrigin
@RequestMapping("/api/v1/salaryuploads")
public class SalaryuploadController {
    private final SalaryuploadRepo salaryuploadRepo;
    private final SalaryuploadService salaryuploadService;
    private final TranHeaderRepository tranHeaderrepo;

    public SalaryuploadController(SalaryuploadRepo salaryuploadRepo, SalaryuploadService salaryuploadService, TranHeaderRepository tranHeaderrepo) {
        this.salaryuploadRepo = salaryuploadRepo;
        this.salaryuploadService = salaryuploadService;
        this.tranHeaderrepo = tranHeaderrepo;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addSalaryupload(@RequestBody Salaryupload salaryupload) {
        try {
            salaryupload.setStatus(CONSTANTS.ENTERED);
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
                    String validation = salaryuploadService.validateSalary(salaryupload);

                    System.out.println(salaryupload);

                    if (validation.trim().length() > 0) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage(validation);
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }

                    salaryupload.setEntityId(EntityRequestContext.getCurrentEntityId());
                    salaryupload.setEnteredFlag('Y');
                    salaryupload.setDeletedFlag('N');
                    salaryupload.setEnteredTime(new Date());
                    salaryupload.setEnteredBy(UserRequestContext.getCurrentUser());
                    Salaryupload newSalaryupload = salaryuploadService.addSalaryupload(salaryupload);
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Saved, salary number " + salaryupload.getSalaryUploadCode() + ". Kindly verify");
                    response.setStatusCode(HttpStatus.CREATED.value());
                    response.setEntity(newSalaryupload);

                    if (newSalaryupload == null) {
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
    public ResponseEntity<?> getAllSalaryuploads(@RequestParam String fromDate, String toDate) {
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
                    List<Salaryupload> salaryupload = salaryuploadRepo.fetchByDate(EntityRequestContext.getCurrentEntityId(), fromDate, toDate, 'N');
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(salaryupload);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<?> getSalaryuploadById(@PathVariable("id") Long id) {
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
                    Salaryupload salaryupload = salaryuploadService.findById(id);
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(salaryupload);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/find/by/code/{salaryUploadCode}")
    public ResponseEntity<?> getSalaryuploadById(@PathVariable("salaryUploadCode") String salaryUploadCode) {
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
                    Optional<Salaryupload> salaryupload1 = salaryuploadRepo.findByEntityIdAndSalaryUploadCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), salaryUploadCode, 'N');
                    if (salaryupload1.isPresent()) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(salaryupload1.get());
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
    public ResponseEntity<?> updateSalaryupload(@RequestBody Salaryupload salaryupload) {
        try {
            salaryupload.setStatus(CONSTANTS.MODIFIED);
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
                    if (salaryupload.getPostedFlag() == 'Y')
                    {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("Salary is already posted");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }
                    salaryupload.setModifiedBy(UserRequestContext.getCurrentUser());
                    salaryupload.setEntityId(EntityRequestContext.getCurrentEntityId());
                    Optional<Salaryupload> salaryupload1 = salaryuploadRepo.findById(salaryupload.getId());
                    if (salaryupload1.isPresent()) {
                        salaryupload.setStatus(CONSTANTS.ENTERED);
                        salaryupload.setEnteredFlag('Y');
                        salaryupload.setEnteredBy(salaryupload1.get().getEnteredBy());
                        salaryupload.setEnteredTime(salaryupload1.get().getEnteredTime());
                        salaryupload.setEnteredBy(salaryupload1.get().getEnteredBy());
                        salaryupload.setPostedTime(salaryupload1.get().getPostedTime());
                        salaryupload.setPostedBy(salaryupload1.get().getPostedBy());
                        salaryupload.setModifiedFlag('Y');
                        salaryupload.setVerifiedFlag('N');
                        salaryupload.setVerifiedFlag_2('N');
                        salaryupload.setModifiedTime(new Date());
                        salaryupload.setModifiedBy(salaryupload.getModifiedBy());
                        salaryuploadRepo.save(salaryupload);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("Salary modified successfully");
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(salaryupload);
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
    public ResponseEntity<?> deleteSalaryupload(@RequestParam String id) {
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
                    Optional<Salaryupload> salaryupload1 = salaryuploadRepo.findById(Long.parseLong(id));
                    if (salaryupload1.isPresent()) {
                        Salaryupload salaryupload = salaryupload1.get();
                        salaryupload.setDeletedFlag('Y');
                        salaryupload.setStatus(CONSTANTS.DELETED);
                        salaryupload.setDeletedTime(new Date());
                        salaryupload.setDeletedBy(UserRequestContext.getCurrentUser());
                        salaryuploadRepo.save(salaryupload);
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(salaryupload);
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

    @GetMapping("salaryuploads")
    public ResponseEntity<?> fetchSalaryuploads(@RequestParam String fromDate,
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
                log.info("Entity not present in the Request Header");
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return new ResponseEntity<>(response, HttpStatus.OK);

            } else {
                List<Salaryupload> salaryuploads = salaryuploadService.filterBydate(fromDate, toDate, EntityRequestContext.getCurrentEntityId(), action);
                if (salaryuploads.size() > 0) {
                    response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(salaryuploads);
                    return new ResponseEntity<>(response, HttpStatus.OK);

                } else if (salaryuploads.isEmpty()) {
                    response.setEntity(salaryuploads);
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
    public ResponseEntity<?> fetchSalaryuploads() {
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
                List<Salaryupload> salaryuploadList = salaryuploadRepo.getApprovalList();
                if (salaryuploadList.size() > 0) {
                    response.setMessage("Welcome " + UserRequestContext.getCurrentUser() + ", There are " + salaryuploadList.size() + " Salary Upload(s) For Approval.");
                    response.setStatusCode(HttpStatus.FOUND.value());
                    response.setEntity(salaryuploadList);
                } else {
                    response.setMessage("Welcome " + UserRequestContext.getCurrentUser() + ", There are " + salaryuploadList.size() + " Salary Upload(s) For Approval.");
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setEntity("Total Salary Upload " + salaryuploadList.size());
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

    @PutMapping("verify/salary-upload/{salaryCode}")
    public ResponseEntity<?> verifyUpload(@PathVariable("salaryCode") String salaryCode) {
        try {
            EntityResponse response = salaryuploadService.verify(salaryCode);
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

    @PostMapping("post/salary-upload/{salaryCode}")
    public ResponseEntity<?> post(@PathVariable("salaryCode")  String salaryUploadCode) {
        try {
            EntityResponse response = salaryuploadService.post(salaryUploadCode);
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
    @GetMapping("total/salary/uploads/count")
    public ResponseEntity<EntityResponse<?>> salaryUploadsCount() {
        try {
            return ResponseEntity.ok().body(salaryuploadService.salaryUploadsCount());
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    @PutMapping("reject/salary/upload/{salaryUploadCode}")
    public ResponseEntity<?> rejectSalaryUpload(@PathVariable("salaryUploadCode") String salaryUploadCode) {
        try {
            EntityResponse response = salaryuploadService.rejectSalaryUpload(salaryUploadCode);
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