package com.emtechhouse.accounts.TransactionService.EODProcess.PushToDB;

import com.emtechhouse.accounts.Models.Accounts.Account;
import com.emtechhouse.accounts.Models.Accounts.AccountRepository;
import com.emtechhouse.accounts.TransactionService.EODProcess.SystemCalendar.SystemCalendarRepository;
import com.emtechhouse.accounts.TransactionService.EODProcess.performEOD.DailyTransactionsResponse;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans.PartTran;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans.PartTranRepository;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TranHeaderRepository;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TransactionHeader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class EODStatusReportService {
    @Autowired
    private EODStatusReportRepository eodStatusReportRepository;
    @Autowired
    private TranHeaderRepository tranHeaderRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private SystemCalendarRepository repository;

    @Autowired
    PartTranRepository partTranRepository;

    @Autowired
    private EndOfDayBalanceRepo endOfDayBalanceRepo;

    //Add
    public void saveEODStatusData(EODStatusReport eod) {
        if (!eodStatusReportRepository.findByEntityIdAndPrevSystemDate(eod.getEntityId(), eod.getPrevSystemDate()).isPresent()) {
            eodStatusReportRepository.save(eod);
        }
    }

    //Find By EntityId and System Date
    public EODStatusReport findByEntityIdAndSysDate(String entityId, String sDate) {
        Date systemDate = null;
        try {
            systemDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sDate);
        } catch (Exception e) {
        }
        if (eodStatusReportRepository.findByEntityIdAndPrevSystemDate(entityId, systemDate).isPresent()) {
            EODStatusReport report = eodStatusReportRepository.findByEntityIdAndPrevSystemDate(entityId, systemDate).get();
            return report;
        } else {
            return null;
        }
    }

    //Find By Entity id
    public List<EODStatusReport> findByEntityId(String entityId) {
        if (eodStatusReportRepository.findByEntityId(entityId).isEmpty()) {
            return null;
        } else {
            return eodStatusReportRepository.findByEntityId(entityId);
        }
    }


    public void calculateAndSaveEOD(String tranDate) {
        System.out.println("Logged here to tell us");

        List<Object[]> resultList = new ArrayList<>();
        EndOfDayBalance[] eodList = new EndOfDayBalance[0];
        for (EndOfDayBalance eod : eodList) {
            resultList.add(new Object[] { eod.getAcid(), eod.getAccountBalance(), eod.getDate() });
        }
        Iterable<? extends EndOfDayBalance> eod;
//        endOfDayBalanceRepo.saveAll(resultList);

    }


//    public void fetchTodayTransactions(String date) {
//        try {
//            System.out.println("Starting to fetch transactions");
//            List<com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans.PartTran> allTransactions = tranHeaderRepository.getTodaytransaction(date);
//            System.out.println("Andika kakitu hapa: "+allTransactions.toString());
//            List<String> acids = new ArrayList<>();
//            List<Date> dates = new ArrayList<>();
//            List<BigDecimal> amounts = new ArrayList<>();
//
//            for (com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans.PartTran t : allTransactions) {
//                acids.add(t.getAcid());
//                dates.add(t.getTransactionDate());
//                amounts.add(BigDecimal.valueOf(t.getTransactionAmount()));
//            }
//
//            List<Account> balances = accountRepository.fetchAccountBalances(acids.toString());
//            System.out.println("Headed to fetch account balances");
//            List<EndOfDayBalance> transactions = new ArrayList<>();
//
//            for (int i = 0; i < acids.size(); i++) {
//                EndOfDayBalance transaction = new EndOfDayBalance();
//                transaction.setAcid(acids.get(i));
//                transaction.setDate(dates.get(i));
//                transaction.setTransactionAmount(amounts.get(i));
//                transaction.setAccountBalance(BigDecimal.valueOf(balances.get(i).getAccountBalance()));
//                transactions.add(transaction);
//            }
//
//            endOfDayBalanceRepo.saveAll(transactions);
//            System.out.println("End of DAY Accounts Balances Saved Successfully");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }



    public void fetchTodayTransactions(String date) {
        try {
            List<PartTran> oldAmount = partTranRepository.getTransactionDate(date);

            if (oldAmount.isEmpty()){
                log.info("Failed to get Parttran data");

            }
            else{
                log.info("Found parttran");
                for (PartTran transaction : oldAmount) {

                    EndOfDayBalance newEmpty = new EndOfDayBalance();

                    newEmpty.setAcid(transaction.getAcid());
                    newEmpty.setTransactionAmount(BigDecimal.valueOf(transaction.getTransactionAmount()));
                    newEmpty.setAccountBalance(BigDecimal.valueOf(transaction.getAccountBalance()));
                    newEmpty.setDate(transaction.getTransactionDate());
                    newEmpty.setEndOfDayDate(new Date());


                    Long sn = transaction.getSn();
                    log.info("SN: "+sn.toString());



                    Optional<TransactionHeader> existingTransactionOptional = tranHeaderRepository.findBySn(sn);
                    if (existingTransactionOptional.isPresent()) {
                        TransactionHeader existingTransaction = existingTransactionOptional.get();
                        // Update existingTransaction or do any other processing
                        existingTransaction.setEodStatus("Y");
                        tranHeaderRepository.save(existingTransaction);
                        log.info("Transaction with SN " + transaction.getSn() + " found and updated");
                    } else {
                        // Handle case where transaction is not found
                        log.warn("Transaction with SN " + transaction.getSn() + " not found");
                    }


                    endOfDayBalanceRepo.save(newEmpty);

                    log.info(transaction.toString());

                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }





}

