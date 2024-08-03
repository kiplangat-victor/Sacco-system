package com.emtechhouse.accounts.Models.Accounts.AccountsRecurringFees;

import com.emtechhouse.accounts.Exception.ApiRequestException;
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
@RequestMapping("recurringfees")
@RestController
public class LedgerFeeController {

    @Autowired
    private AccountRecurringFeeService accountRecurringFeeService;

//    @PutMapping("aa/statement/details")
//    public ResponseEntity<?> ledgerFee(@RequestParam String acid) {
//        try {
//            List<EntityResponse> res = accountRecurringFeeService.createLedgerFee(acid);
//            return ResponseEntity.status(HttpStatus.CREATED).body(res);
//        } catch (Exception e) {
//            log.info("Catched Error {} " + e);
////            return null;
//            throw new ApiRequestException("failed");
//        }
//    }

//    @PutMapping("aa/statement/details/all")
//    public ResponseEntity<?> ledgerFeeAll() {
//        try {
//            List<EntityResponse> res = accountRecurringFeeService.collectLedgerFeeForAllAc();
//            return ResponseEntity.status(HttpStatus.CREATED).body(res);
//        } catch (Exception e) {
//            log.info("Catched Error {} " + e);
////            return null;
//            throw new ApiRequestException("failed");
//        }
//    }
}
