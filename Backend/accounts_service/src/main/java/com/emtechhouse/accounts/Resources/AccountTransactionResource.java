package com.emtechhouse.accounts.Resources;

import com.emtechhouse.accounts.Models.Accounts.AccountDtos.AccountStatementDto;
import com.emtechhouse.accounts.Models.Accounts.AccountServices.AccountTransactionService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanAccrual.LoanAccrualService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanBooking.LoanBookingService;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ProductFees.ProductFeesService;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ProductInterestDetails.ProductInterestService;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ItemServiceCaller;
import com.emtechhouse.accounts.Models.Accounts.TransactionDtos.RecievedTransactions.ReceivedTransactionHolder;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
@RequestMapping("accounts/transaction")
@RestController
public class AccountTransactionResource {
    @Autowired
    private AccountTransactionService accountTransactionService;

    @Autowired
    private LoanAccrualService loanAccrualService;
    @Autowired
    private LoanBookingService loanBookingService;
    @Autowired
    private ItemServiceCaller itemServiceCaller;
    @Autowired
    private ProductFeesService productFeesService;
    @Autowired
    private ProductInterestService productInterestService;
//    @PutMapping("credit")
//    public ResponseEntity<?> creditAccount(String acid, Double amount){
//        try{
//            return ResponseEntity.status(HttpStatus.CREATED).body(accountTransactionService.creditAccount(acid,amount));
//        }catch (Exception e){
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }

//    @PutMapping("debit")
//    public ResponseEntity<?> debitAccount(String acid, Double amount){
//        try{
//            return ResponseEntity.status(HttpStatus.CREATED).body(accountTransactionService.debitAccount(acid,amount));
//        }catch (Exception e){
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }
    @PostMapping("full/transaction")
    public ResponseEntity<?> fullTransaction(@RequestBody ReceivedTransactionHolder receivedTransactionHolder){
        try{
            EntityResponse res =accountTransactionService.incomingFullTransaction(receivedTransactionHolder);
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            EntityResponse res = new EntityResponse<>();
            res.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        }
    }

    @PostMapping("account/statement/transaction")
    public ResponseEntity<?> fullTransaction(@RequestBody AccountStatementDto accountStatementDto){
        try{
            EntityResponse res =accountTransactionService.statementTransaction(accountStatementDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            EntityResponse res = new EntityResponse<>();
            res.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        }
    }

//    @GetMapping("prodItem")
//    public ResponseEntity<?> GeneralProductDetails(@RequestParam String event_id_code,@RequestParam Double amt){
//        try{
//            return ResponseEntity.status(HttpStatus.CREATED).body(productFeesService.getFeeAmount(event_id_code, amt));
//        }catch (Exception e){
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }

//    @PutMapping("post/transaction")
//    public ResponseEntity<?> transact(@RequestBody OutgoingTransactionDetails outgoingTransactionDetails){
//        try{
//            return ResponseEntity.status(HttpStatus.CREATED).body(accountTransactionService.createTransaction1(outgoingTransactionDetails));
//        }catch (Exception e){
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }

}
