package com.emtechhouse.accounts.Models.Accounts.AccountClosure;

import com.emtechhouse.accounts.Exception.ApiRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("accounts/closure")
@RestController
public class AccountClosureController {
    @Autowired
    private AccountClosureService accountClosureService;

    @PostMapping("close/account")
    public ResponseEntity<?> closeAccount(@RequestParam  String acid, @RequestParam  String closureReason){
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(accountClosureService.initiateAccountClosure(acid,closureReason));
        }catch (Exception e){
            log.info("Catched Error {} " + e);
//            return null;
            throw new ApiRequestException("failed");
        }
    }

    @PutMapping("verify/account/closure/{acid}")
    public ResponseEntity<?> verifyAccountClosure(@PathVariable("acid")  String acid){
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(accountClosureService.verifyAccountClosure(acid));
        }catch (Exception e){
            log.info("Catched Error {} " + e);
//            return null;
            throw new ApiRequestException("failed");
        }
    }

    @PostMapping("activate/account")
    public ResponseEntity<?> activateAccount(@RequestParam  String acid, @RequestParam  String closureReason){
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(accountClosureService.initiateAccountActivation(acid,closureReason));
        }catch (Exception e){
            log.info("Catched Error {} " + e);
//            return null;
            throw new ApiRequestException("failed");
        }
    }
    @PutMapping("verify/account/activation/{acid}")
    public ResponseEntity<?> verifyAccountActivation(@PathVariable("acid")  String acid){
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(accountClosureService.verifyAccountActivation(acid));
        }catch (Exception e){
            log.info("Catched Error {} " + e);
//            return null;
            throw new ApiRequestException("failed");
        }
    }

    @PutMapping("balance/enquiry/charges/{acid}")
    public ResponseEntity<?> balanceEnquiryCharges(@PathVariable("acid")  String acid){
        try{
            System.out.println("Arrived to enter balance enquiry fee");
            return ResponseEntity.status(HttpStatus.CREATED).body(accountClosureService.balanceEnquiryCharges(acid));
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            throw new ApiRequestException("failed");
        }
    }


    @PutMapping("sms/charges/{acid}")
    public ResponseEntity<?> smsCharges(@PathVariable("acid")  String acid){
        try{
            System.out.println("Arrived to enter notification charges");
            return ResponseEntity.status(HttpStatus.CREATED).body(accountClosureService.smsCharges(acid));
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            throw new ApiRequestException("failed");
        }
    }



    @GetMapping("get/account/closure/details/{acid}")
    public ResponseEntity<?> getAccountClosureDetails(@PathVariable("acid")  String acid){
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(accountClosureService.getAccountClosureDetails(acid));
        }catch (Exception e){
            log.info("Catched Error {} " + e);
//            return null;
            throw new ApiRequestException("failed");
        }
    }
    @GetMapping("get/account/activation/details/{acid}")
    public ResponseEntity<?> getAccountActivationDetails(@PathVariable("acid")  String acid){
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(accountClosureService.getAccountActivationDetails(acid));
        }catch (Exception e){
            log.info("Catched Error {} " + e);
//            return null;
            throw new ApiRequestException("failed");
        }
    }
}
