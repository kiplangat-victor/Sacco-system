package com.emtechhouse.accounts.TransactionService.ChequeProcessing;


import com.emtechhouse.accounts.Models.Accounts.AccountRepository;
import com.emtechhouse.accounts.Responses.BatchSalaryCheques;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.accounts.Utils.MoneyWordConverter;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@Api(value = "/Cheque Processing API", tags = "Cheque Processin  API")
@RequestMapping("/api/v1/chequeprocessing")

public class ChequeProcessingController {
    @Autowired
    private AccountRepository accountRepository;
    @Value("${spring.organisation.reports_absolute_path}")
    private String files_path;
    //    @Value("${logolink}")
    private String logolink;
    @Value("${spring.datasource.url}")
    private String db;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;
    //    spring.organisation.
    @Value("${spring.organisation.organizationName}")
    private String organizationName;
    @Value("${spring.organisation.organizationAddress}")
    private String organizationAddress;
    @Value("${spring.organisation.organizationPhone}")
    private String organizationPhone;
    @Value("${spring.organisation.organizationDescription}")
    private String organizationDescription;
    @Value("${spring.organisation.company_logo_path}")
    private String organizationLogo;

    @Autowired
    private MoneyWordConverter moneyWordConverter;
    private final ChequeProcessingService chequeProcessingService;
    private final ChequeProcessingRepo chequeProcessingRepo;

    public ChequeProcessingController(ChequeProcessingService chequeProcessingService, ChequeProcessingRepo chequeProcessingRepo) {
        this.chequeProcessingService = chequeProcessingService;
        this.chequeProcessingRepo = chequeProcessingRepo;
    }

    @PostMapping("enter")
    public ResponseEntity<?> enter(@RequestBody ChequeProcessing chequeProcessing) throws IOException {
        try {
            return new ResponseEntity<>(chequeProcessingService.enter(chequeProcessing), HttpStatus.OK);
        } catch (Exception e) {
            EntityResponse response = new EntityResponse();
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("modify")
    public ResponseEntity<?> modify(@RequestBody ChequeProcessing chequeProcessing) throws IOException {
        try {
            return new ResponseEntity<>(chequeProcessingService.modify(chequeProcessing), HttpStatus.OK);
        } catch (Exception e) {
            EntityResponse response = new EntityResponse();
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("verify")
    public ResponseEntity<?> verify(@RequestParam String chequeRandCode) throws IOException {
        try {
            return new ResponseEntity<>(chequeProcessingService.verify(chequeRandCode), HttpStatus.OK);
        } catch (Exception e) {
            EntityResponse response = new EntityResponse();
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("bounce")
    public ResponseEntity<?> bounce(@RequestParam String chequeRandCode, @RequestParam String penaltyCollAc, @RequestParam Double penaltyAmount) throws IOException {
        try {
            return new ResponseEntity<>(chequeProcessingService.bounce(chequeRandCode, penaltyCollAc, penaltyAmount), HttpStatus.OK);
        } catch (Exception e) {
            EntityResponse response = new EntityResponse();
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("clear")
    public ResponseEntity<?> clear(@RequestParam String chequeRandCode) throws IOException {
        try {
            return new ResponseEntity<>(chequeProcessingService.clear(chequeRandCode), HttpStatus.OK);
        } catch (Exception e) {
            EntityResponse response = new EntityResponse();
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("delete")
    public ResponseEntity<?> delete(@RequestParam String chequeRandCode) throws IOException {
        try {
            return new ResponseEntity<>(chequeProcessingService.delete(chequeRandCode), HttpStatus.OK);
        } catch (Exception e) {
            EntityResponse response = new EntityResponse();
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("all")
    public ResponseEntity<?> findAll() throws IOException {
        try {
            EntityResponse response = new EntityResponse();
            response = chequeProcessingService.fetchAll();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            EntityResponse response = new EntityResponse();
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("all/unverified/cheques")
    public ResponseEntity<?> findAllUnVerifiedCheques() throws IOException {
        try {
            EntityResponse response = new EntityResponse();
            response = chequeProcessingService.findAllUnVerifiedCheques();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            EntityResponse response = new EntityResponse();
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("all/filter")
    public ResponseEntity<?> findAll(@RequestParam(required = false) String fromDate, @RequestParam(required = false) String toDate, @RequestParam(required = false) String chequeRandCode, @RequestParam(required = false) String status) throws IOException {
        try {
            EntityResponse response = new EntityResponse();
            if (status == null || status.trim().isEmpty()) {
                status = CONSTANTS.ENTERED;
            }
            if (chequeRandCode == null || chequeRandCode.isEmpty()) {
                if (fromDate == null || toDate == null) {
                    response.setMessage("You must provide a date range or transaction code if you know!");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                } else {
                    Period period = Period.between(LocalDate.parse(fromDate), LocalDate.parse(toDate));
                    int months = period.getMonths();
                    if (months > 2) {
                        response.setMessage("You can only fetch data within a range of two months. Kindly adjust your dates accordingly!");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    } else {
                        List<ChequeProcessingRepo.CheckProcessingAll> transactions = chequeProcessingRepo.findAllDatabyDateRangeAndStatus(fromDate.toString(), toDate.toString(), status);
                        if (transactions.size() > 0) {
                            response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                            response.setStatusCode(HttpStatus.FOUND.value());
                            response.setEntity(transactions);
                        } else {
                            response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                            response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        }
                    }
                }
            } else {
//                call find by transaction id
                Optional<ChequeProcessing> data = chequeProcessingRepo.findByChequeRandCode(chequeRandCode);
                if (data.isPresent()) {
                    List<ChequeProcessing> chequeProcessing = new ArrayList<>();
                    chequeProcessing.add(data.get());
                    response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                    response.setStatusCode(HttpStatus.FOUND.value());
                    response.setEntity(chequeProcessing);
                } else {
                    response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
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

    @GetMapping("find/by/ChequeRandCode")
    public ResponseEntity<?> findByChequeRandCode(@RequestParam String chequeRandCode) throws IOException {
        try {
            EntityResponse response = new EntityResponse();
            response = chequeProcessingService.findByChequeRandCode(chequeRandCode);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            EntityResponse response = new EntityResponse();
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("post")
    public ResponseEntity<?> enterTransaction(@RequestParam String chequeRandCode) throws IOException {
        try {
            return new ResponseEntity<>(chequeProcessingService.post(chequeRandCode), HttpStatus.OK);
        } catch (Exception e) {
            EntityResponse response = new EntityResponse();
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("approvelist")
    public ResponseEntity<?> fetchCheques() {
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
                List<ChequeProcessingRepo.CheckProcessingAll> salaryuploads = chequeProcessingRepo.getApprovalList();
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
    @GetMapping("unposted")
    public ResponseEntity<?> unpostedCheques() {
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
                List<BatchSalaryCheques> allRoughCheque = chequeProcessingRepo.getRoughUnpostedList(UserRequestContext.getCurrentUser());
                if (!allRoughCheque.isEmpty()) {
                    response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(allRoughCheque);
                    return new ResponseEntity<>(response, HttpStatus.OK);

                } else {
                    response.setEntity(allRoughCheque);
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


    @GetMapping("unverified")
    public ResponseEntity<?> unVerifiedCheques() {
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
                List<BatchSalaryCheques> allRoughCheque = chequeProcessingRepo.getRoughUnpostedList(UserRequestContext.getCurrentUser());
                if (!allRoughCheque.isEmpty()) {
                    response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(allRoughCheque);
                    return new ResponseEntity<>(response, HttpStatus.OK);

                } else {
                    response.setEntity(allRoughCheque);
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


    @GetMapping("all/unVerified/check/processing/list")
    public ResponseEntity<?> findAllUnVerifiedCheckProcessing(
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
                    List<ChequeProcessingRepo.CheckProcessingAll> checkProcessingAllList = chequeProcessingRepo.findAllChequesProcessingByEntityIdDateRange(EntityRequestContext.getCurrentEntityId(), fromDate, toDate);
                    if (checkProcessingAllList.size() > 0) {
                        response.setMessage("Welcome " + UserRequestContext.getCurrentUser() + ", There are " + checkProcessingAllList.size() + " Cheques(s) Processing For Approval.");
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(checkProcessingAllList);
                    } else {
                        response.setMessage("Welcome " + UserRequestContext.getCurrentUser() + ", There are " + checkProcessingAllList.size() + " Cheques(s) Processing For Approval.");
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity(checkProcessingAllList);
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

    @GetMapping("/total/cheque/for/processing")
    public EntityResponse<?> getTotalChequeForProcessing() {
        try {
            EntityResponse response = new EntityResponse();
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                } else {
                    return ResponseEntity.ok().body(chequeProcessingService.getTotalChequeForProcessing()).getBody();
                }
            }
            return response;

        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("reject/{chequeRandCode}")
    public ResponseEntity<?> rejectChequeProcessing(@PathVariable("chequeRandCode") String chequeRandCode) {
        try {
            EntityResponse response = chequeProcessingService.rejectChequeProcessing(chequeRandCode);
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