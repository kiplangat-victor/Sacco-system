package com.emtechhouse.accounts.Resources;

import com.emtechhouse.accounts.Models.Accounts.Loans.LoanPayOff.LoanPayOffRepo;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanPayOff.LoanPayOffService;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("loan/payoff")
@RestController
public class LoanPayOffResource {
    @Autowired
    private LoanPayOffService loanPayOffService;
    @Autowired
    private LoanPayOffRepo loanPayOffRepo;

    @GetMapping("get/payoff/amount")
    public ResponseEntity<EntityResponse<?>> getPayOffAmount(@RequestParam  String acid){
        try {
            return ResponseEntity.ok().body(loanPayOffService.getPayOffAmount(acid));
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("initiate/loan/payoff")
    public ResponseEntity<EntityResponse<?>> initiateLoanPayOff(@RequestParam String loanAccountAcid,
                                                                @RequestParam String operativeAccountAcid){
        try {
            return ResponseEntity.ok().body(loanPayOffService.initiateLoanPayOff( loanAccountAcid,operativeAccountAcid));
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("verify/loan/payoff")
    public ResponseEntity<EntityResponse<?>> verifyLoanPayOff(@RequestParam String loanAccountAcid) {
        try {
            return ResponseEntity.ok().body(loanPayOffService.verifyLoanPayOff1( loanAccountAcid));
        }catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
}
