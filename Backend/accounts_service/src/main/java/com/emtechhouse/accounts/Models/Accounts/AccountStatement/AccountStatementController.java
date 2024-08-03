package com.emtechhouse.accounts.Models.Accounts.AccountStatement;

import com.emtechhouse.accounts.Exception.ApiRequestException;
import com.emtechhouse.accounts.Models.Accounts.AccountDtos.AccountStatementDto;
import com.emtechhouse.accounts.Models.Accounts.AccountServices.AccountTransactionService;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("accounts")
@RestController
public class AccountStatementController {
    @Autowired
    private AccountStatementService accountStatementService;
    @Autowired
    private AccountTransactionService accountTransactionService;

    @PutMapping("/account/statement/")
    public ResponseEntity<?> getCollateralDetails(@RequestBody AccountStatementDto accountStatementDto){
        try{
            EntityResponse res= accountTransactionService.statementTransaction(accountStatementDto);

            return ResponseEntity.status(HttpStatus.OK).body(res);
        }catch (Exception e){
            log.info("Catched Error {} " + e);
//            return null;
            throw new ApiRequestException("failed");
        }
    }
}
