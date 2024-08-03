package com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader;

import com.emtechhouse.accounts.Models.Accounts.AccountRepository;
import com.emtechhouse.accounts.NotificationComponent.SmsService.SMSService;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.accounts.Utils.MoneyWordConverter;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import com.emtechhouse.accounts.Utils.ServiceCaller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequestMapping("api/v1/transaction")
@RestController
public class NewTransactionController {
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

    @Autowired
    SMSService smsService;
    private final NewTransactionService newTransactionService;
    private final TranHeaderRepository tranHeaderRepository;
    private final AccountRepository accountRepository;
    private final TransactionProcessing transactionProcessing;
    @Autowired
    private ServiceCaller serviceCaller;

    public NewTransactionController(NewTransactionService newTransactionService, TranHeaderRepository tranHeaderRepository, AccountRepository accountRepository, TransactionProcessing transactionProcessing) {
        this.newTransactionService = newTransactionService;
        this.tranHeaderRepository = tranHeaderRepository;
        this.accountRepository = accountRepository;
        this.transactionProcessing = transactionProcessing;
    }
    @GetMapping("by/sn/{sn}")
    public ResponseEntity<?> retrieveTransaction(@PathVariable("sn") Long sn) {

        EntityResponse response = new EntityResponse<>();
        if (UserRequestContext.getCurrentUser().isEmpty()) {

            response.setMessage("User Name not present in the Request Header");
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            response.setEntity("");
            return new ResponseEntity<>(response, HttpStatus.OK);

        } else if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
            response.setMessage("Entity not present in the Request Header");
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            response.setEntity("");
            return new ResponseEntity<>(response, HttpStatus.OK);

        } else {

            Optional<TransactionHeader> transactionHeader = tranHeaderRepository.findById(sn);

            if (!transactionHeader.isPresent()) {
                response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                response.setEntity("");
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            } else {
                response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                response.setEntity(transactionHeader.get());
                response.setStatusCode(HttpStatus.OK.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }
    }
    @PostMapping("enter")
    public ResponseEntity<?> enterTransaction(@RequestBody TransactionHeader transactionHeader) throws IOException {
        try {
            return new ResponseEntity<>(newTransactionService.enter(transactionHeader), HttpStatus.OK);
        } catch (Exception e) {
            EntityResponse response = new EntityResponse();
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("mobile/enter")
    public ResponseEntity<?> enterMobileTransaction(@RequestBody TransactionHeader transactionHeader) throws IOException {
        try {
            return new ResponseEntity<>(newTransactionService.enterMobileTransaction(transactionHeader), HttpStatus.OK);
        } catch (Exception e) {
            EntityResponse response = new EntityResponse();
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PutMapping("reverse")
    public ResponseEntity<?> revserseTransaction(@RequestParam String transactionCode) throws IOException {
        try {
            return new ResponseEntity<>(newTransactionService.reverseTransaction(transactionCode), HttpStatus.OK);
        } catch (Exception e) {
            EntityResponse response = new EntityResponse();
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("system/reverse")
    public ResponseEntity<?> systemReverseTransaction(@RequestParam String transactionCode) throws IOException {
        try {
            String user = UserRequestContext.getCurrentUser();
            String entityId = EntityRequestContext.getCurrentEntityId();
            return new ResponseEntity<>(newTransactionService.systemReverseTransaction(transactionCode, user, entityId, true),
                    HttpStatus.OK);
        } catch (Exception e) {
            EntityResponse response = new EntityResponse();
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("verify")
    public ResponseEntity<?> verifyTransaction(@RequestParam String transactionCode) throws IOException {
        try {
            return new ResponseEntity<>(newTransactionService.verify(transactionCode), HttpStatus.OK);
        } catch (Exception e) {
            EntityResponse response = new EntityResponse();
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("request-approval")
    public ResponseEntity<?> requestApproval(@RequestParam String transactionCode, @RequestParam String approver) throws IOException {
        try {
            return new ResponseEntity<>(newTransactionService.requestApproval(transactionCode, approver), HttpStatus.OK);
        } catch (Exception e) {
            EntityResponse response = new EntityResponse();
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("rejectTransaction")
    public ResponseEntity<?> rejectTransaction(@RequestParam String transactionCode, @RequestParam String reason) throws IOException {
        try {
            return new ResponseEntity<>(newTransactionService.rejectTransaction(transactionCode, reason), HttpStatus.OK);
        } catch (Exception e) {
            EntityResponse response = new EntityResponse();
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("acknowledge")
    public ResponseEntity<?> acknowledgeTransaction(@RequestParam String transactionCode) throws IOException {
        try {
            return new ResponseEntity<>(newTransactionService.acknowledge(transactionCode), HttpStatus.OK);
        } catch (Exception e) {
            EntityResponse response = new EntityResponse();
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("post")
    public ResponseEntity<?> enterTransaction(@RequestParam String transactionCode) throws IOException {
        try {
            return new ResponseEntity<>(newTransactionService.post(transactionCode), HttpStatus.OK);
        } catch (Exception e) {
            EntityResponse response = new EntityResponse();
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }


    @PutMapping("post1")
    public ResponseEntity<?> enterTransaction1(@RequestParam String transactionCode) throws IOException {
        try {
            System.out.println("Inside Post 1");
            return new ResponseEntity<>(newTransactionService.post1(transactionCode), HttpStatus.OK);
        } catch (Exception e) {
            EntityResponse response = new EntityResponse();
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("paginated/all")
    public ResponseEntity<?> allTrans(@RequestParam int offset, @RequestParam int pageSize, @RequestParam String sortby) throws IOException {
        try {
            EntityResponse response = new EntityResponse();
            Page<TransactionHeader> transactionHeaders = newTransactionService.findTransactions(offset, pageSize, sortby);
//            findAllPaged
            response.setMessage(HttpStatus.FOUND.getReasonPhrase());
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(transactionHeaders);
            return new ResponseEntity<>(response, HttpStatus.OK);

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
            response = newTransactionService.fectAll();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            EntityResponse response = new EntityResponse();
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("all/filter")
    public ResponseEntity<?> findAll(@RequestParam(required = false) String transactionType, @RequestParam(required = false) String fromDate, @RequestParam(required = false) String toDate, @RequestParam(required = false) String transactionCode, @RequestParam(required = false) String status) throws IOException {
        try {
            EntityResponse response = new EntityResponse();
            if (status == null || status.trim().isEmpty()) {
                status = CONSTANTS.ENTERED;
            }
            if (transactionType == null || transactionType.isEmpty()) {
                response.setMessage("Transaction Type is required!");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            } else {
                if (transactionCode == null || transactionCode.isEmpty()) {
                    if (fromDate == null || toDate == null) {
                        response.setMessage("You must provide a date range or transaction code if you know!");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    } else {
                        List<TransactionHeader> transactions = tranHeaderRepository.findAllTransactionsbyTransactionTypeAndDateRangeAndStatus(transactionType, fromDate.toString(), toDate.toString(), status);
                        if (transactions.size() > 0) {
                            response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                            response.setStatusCode(HttpStatus.FOUND.value());
                            response.setEntity(transactions);
                        } else {
                            response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                            response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        }
                    }
                } else {
//                call find by transaction id
                    Optional<TransactionHeader> data = tranHeaderRepository.findByTransactionCode(transactionCode);
                    if (data.isPresent()) {
                        List<TransactionHeader> transactionHeaders = new ArrayList<>();
                        transactionHeaders.add(data.get());
                        response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(transactionHeaders);
                    } else {
                        response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
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

    @GetMapping("all/approvelist")
    public ResponseEntity<?> findForApproval() throws IOException {
        try {
            EntityResponse response = new EntityResponse();
            List<TransactionHeader> transactionHeaderList
                    = tranHeaderRepository.findAllForApproval(UserRequestContext.getCurrentUser());
            if (transactionHeaderList.size() > 0) {
                response.setMessage("Welcome " + UserRequestContext.getCurrentUser() + ", There are " + transactionHeaderList.size() + " Transactions(s) For Approval.");
                response.setStatusCode(HttpStatus.FOUND.value());
                response.setEntity(transactionHeaderList);
            } else {
                response.setMessage("Welcome " + UserRequestContext.getCurrentUser() + ", There are " + transactionHeaderList.size() + " Transactions(s) For Approval.");
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setEntity("Total Transactions " + transactionHeaderList.size());
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            EntityResponse response = new EntityResponse();
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("total/transactions/count")
    public ResponseEntity<EntityResponse<?>> transactionsCount() {
        try {
            return ResponseEntity.ok().body(newTransactionService.transactionsCount());
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
}
