package com.emtechhouse.accounts.Resources;

import com.emtechhouse.accounts.Exception.ApiRequestException;
import com.emtechhouse.accounts.Models.Accounts.TermDeposit.TermDepositService;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequestMapping("accounts/term-deposit")
@RestController
public class TermDepositResource {
    @Autowired
    private TermDepositService termDepositService;

    @PutMapping("pay/term-deposit")
    public ResponseEntity<?> payTermDeposit(@RequestParam String acid) {
        try {
            List<EntityResponse> res= termDepositService.initiateTermDepositPayment(acid);
            if (res.size() > 0) {
                return ResponseEntity.status(HttpStatus.CREATED).body(res.get(0));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        }catch (Exception e){
            log.info("Catched Error {} " + e);
//            return null;
            throw new ApiRequestException("failed");
        }
    }
}
