package com.emtechhouse.accounts.AutomatedProcesses;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class AutomatedProcesses {
    @Autowired
    AutomatedService automatedService;

    //    @Scheduled(fixedRate = 100, initialDelay = 655000)
    public void satisfyLiens() {
        automatedService.satisfyLiens();
    }

//    @Scheduled (fixedRate = 5000000L, initialDelay = 0)
    public void endOfYearReconciliation(){
        automatedService.endOfYearReconciliation();
    }



    @Scheduled(fixedRate = 40000000L, initialDelay = 78000000L)
    public void generateDemands() {
        automatedService.generateDemands();
    }


    @Scheduled(fixedRate = 400000L, initialDelay = 5500000L)
    public void executeStandingOrders() {
        automatedService.executeStandingOrders();
    }

    @Scheduled(fixedRate = 40000000L, initialDelay = 6000000L)
    public void closeFixedDeposits() {
        automatedService.closeFixedDeposits();
    }

    @Scheduled(fixedRate = 100000000L, initialDelay =100000L)
    public void initiateRecurringFees() {
        automatedService.initiateRecurringFees();
    }
    @Scheduled(fixedRate =6000000L, initialDelay = 600000L)
    public void executeRecurringFees() {
        automatedService.executeRecurringFees();
    }

    //    @Scheduled(fixedRate = 600000000000000000L, initialDelay = 0)
    public void reverseSystemTransactions() {
        automatedService.reverseSystemTransactions();
    }

    @Scheduled(fixedRate = 4000000L, initialDelay = 6000000L)
    public void  satisfyDemands() {
        automatedService.satisfyDemands();
    }


    @Scheduled(fixedRate = 3000000L, initialDelay = 4500000L)
    public void  incomeInstructions() {
        automatedService.incomeInstructions();
    }


    @Scheduled(fixedRate = 36000000L, initialDelay = 740000000L)
    public void  classifyLoans() {
        automatedService.classifyLoans();
    }

    @Scheduled (fixedRate = 5000000L, initialDelay = 700000L)
    public void updateDormantAccounts(){
        automatedService.updateDormantAccounts();
    }

    @Scheduled (fixedRate = 8000000L, initialDelay = 1100000L)
    public void updateAccountsStatus(){
        automatedService.updateAccountsStatus();
    }

//    @Scheduled(fixedRate = 86400000L, initialDelay = 87870L)
//    public void saveEod() {
//        automatedService.saveEod();
//    }


    @Scheduled(cron = "0 45 23 * * *")
    public void saveEod() {
        automatedService.saveEod();
    }


}

