package com.emtechhouse.accounts.Models.Accounts.TermDeposit.Schedule;

import com.emtechhouse.accounts.Models.Accounts.TermDeposit.Accrual.TermDepositAccrualService;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RequestMapping("term/")
@RestController
public class TermScheduleController {
    @Autowired
    private TermDepositScheduleService termDepositScheduleService;
    @Autowired
    private TermDepositAccrualService termDepositAccrualService;

    @PutMapping("accrue")
    public ResponseEntity<?> accrue(@RequestParam String acid) {
        try {
            EntityResponse res= termDepositAccrualService.accrue(acid);
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("accrue/all")
    public ResponseEntity<?> accrueAll() {
        try {
            List<EntityResponse> res= termDepositAccrualService.accrualForAllTda();
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
}
