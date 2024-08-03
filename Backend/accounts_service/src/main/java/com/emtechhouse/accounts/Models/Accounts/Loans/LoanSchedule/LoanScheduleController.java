package com.emtechhouse.accounts.Models.Accounts.Loans.LoanSchedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
@RequestMapping("loan/schedule")
@RestController
public class LoanScheduleController {
    @Autowired
    LoanScheduleService loanScheduleService;

    @GetMapping("all")
    public ResponseEntity<List<?>> loanSchedule(){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(loanScheduleService.all());
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }
}
