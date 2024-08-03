package com.emtechhouse.accounts.Models.Accounts.Loans.LoanPenalties;

import com.emtechhouse.accounts.Exception.ApiRequestException;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("loan/penalty")
@RestController
public class LoanPenaltyController {
    @Autowired
    private LoanPenaltyService loanPenaltyService;

    @PostMapping("/create/penalty/{loanAcid}")
    public ResponseEntity<?> createPenalty(@PathVariable("loanAcid") String loanAcid, @RequestBody LoanPenalty loanPenalty){
        try {
            EntityResponse res= loanPenaltyService.createLoanPenalty(loanAcid,loanPenalty);
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        }catch (Exception e){
            log.info("Catched Error {} " + e);
//            return null;
            throw new ApiRequestException("failed");
        }
    }

    @PutMapping("/verify/penalty/{id}")
    public ResponseEntity<?> verifyPenalty(@PathVariable("id") Long id){
        try{
            EntityResponse res= loanPenaltyService.verifyPenalty(id);
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        }catch (Exception e){
            log.info("Catched Error {} " + e);
//            return null;
            throw new ApiRequestException("failed");
        }
    }

    @PutMapping("/modify/penalty")
    public ResponseEntity<?> modifyPenalty(@RequestBody LoanPenalty loanPenalty){
        try{
            EntityResponse res= loanPenaltyService.modifyLoanPenalty(loanPenalty);
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            throw new ApiRequestException("failed");
        }
    }

    @PutMapping("/delete/penalty/{id}")
    public ResponseEntity<?> deletePenalty(@PathVariable("id") Long id){
        try{
            EntityResponse res= loanPenaltyService.deleteLoanPenalty(id);
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        }catch (Exception e){
            log.info("Catched Error {} " + e);
//            return null;
            throw new ApiRequestException("failed");
        }
    }

    @GetMapping("/get/penalty/by/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") Long id){
        try{
            EntityResponse res= loanPenaltyService.findById(id);
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            throw new ApiRequestException("failed");
        }
    }

    @GetMapping("/get/penalty/by/loan/{acid}")
    public ResponseEntity<?> getByLoanAcid(@PathVariable("acid") String acid){
        try{
            EntityResponse res= loanPenaltyService.findByLoanAcid(acid);
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            throw new ApiRequestException("failed");
        }
    }
    @GetMapping("/get/all/loan/penalties")
    public ResponseEntity<?> getAllLoanPenalties(){
        try{
            EntityResponse res= loanPenaltyService.getAllUnverifiedLoanPenalties();
            return ResponseEntity.status(HttpStatus.OK).body(res);
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            throw new ApiRequestException("failed");
        }
    }
    @GetMapping("/get/all/unverified/penalties")
    public ResponseEntity<?> getAllUnverifiedPenalties() {
        try{
            EntityResponse res= loanPenaltyService.getAllUnverifiedLoanPenalties();
            return ResponseEntity.status(HttpStatus.OK).body(res);
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            throw new ApiRequestException("failed");
        }
    }
}