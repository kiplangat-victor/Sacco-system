package com.emtechhouse.accounts.Models.Accounts.Loans.LoanGuarantor;

import com.emtechhouse.accounts.Exception.ApiRequestException;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("guarantor")
@RestController
public class LoanGuarantorController {

    @Autowired
    private LoanGuarantorService loanGuarantorService;

    @PutMapping("add/guarantor/")
    public ResponseEntity<?> addLoanGuarantor(@RequestBody LoanGuarantor loanGuarantor, @RequestParam String acid) {
        try {
            EntityResponse res = loanGuarantorService.addGuarantorToALoan(loanGuarantor, acid);
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
//            return null;
            throw new ApiRequestException("failed");
        }
    }
}
