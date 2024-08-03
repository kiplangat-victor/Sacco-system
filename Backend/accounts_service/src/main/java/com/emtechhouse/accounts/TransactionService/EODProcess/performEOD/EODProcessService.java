package com.emtechhouse.accounts.TransactionService.EODProcess.performEOD;


import com.emtechhouse.accounts.TransactionService.EODProcess.DataBackUp.DataBackUpService;
import com.emtechhouse.accounts.TransactionService.EODProcess.PushToDB.EODStatusReport;
import com.emtechhouse.accounts.TransactionService.EODProcess.PushToDB.EODStatusReportRepository;
import com.emtechhouse.accounts.TransactionService.EODProcess.PushToDB.EODStatusReportService;
import com.emtechhouse.accounts.TransactionService.EODProcess.SystemCalendar.SystemCalendar;
import com.emtechhouse.accounts.TransactionService.EODProcess.SystemCalendar.SystemCalendarRepository;
import com.emtechhouse.accounts.TransactionService.EODProcess.performEOD.EODResponses.EODRes;
import com.emtechhouse.accounts.TransactionService.EODProcess.performEOD.EODResponses.EODResponse;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans.PartTran;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans.PartTranRepository;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TranHeaderRepository;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TransactionHeader;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeaderHistory.TranHeaderHistory;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeaderHistory.TranHeaderHistoryRepository;
import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.sql.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.From;
import javax.swing.text.html.parser.Entity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext.entityId;
import static com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext.getCurrentEntityId;

@Service
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class EODProcessService {

    @Autowired
    private SystemCalendarRepository repository;

    @Autowired
    private TranHeaderRepository tranHeaderRepository;

    @Autowired
    private TranHeaderHistoryRepository tranHeaderHistoryRepository;

    @Autowired
    private PartTranRepository partTranRepository;

    @Autowired
    private DataBackUpService bkp;

    @Value("${spring.datasource.dbname}")
    private String db;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

    @Autowired
    private EODStatusReportService eodStatusReportService;

    @Autowired
    private EODStatusReportRepository eodStatusReportRepository;

    //EOD PROCESS
    public EODResponse performEOD() {
        EODResponse eodResponse = new EODResponse();
        EODRes eodRes = new EODRes();

        List<EODRes> resList = new ArrayList<>();
        List<SystemCalendar> systemCalendars = repository.findByEntityIdAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), 'N');
        String systemDate = "";
        if (systemCalendars.isEmpty()) {
            systemDate = new Date().toString();
        } else {
            systemDate = systemCalendars.get(0).getSystemDate().toString();
        }
        System.out.println("System date is: "+systemDate);

        //Initialize Data in EOD Status Report Table
        EODStatusReport eodStatusReport = new EODStatusReport();
        eodStatusReport.setEodTime(new Date());
        eodStatusReport.setEodDoneBy(UserRequestContext.getCurrentUser());
        eodStatusReport.setEntityId(EntityRequestContext.getCurrentEntityId());
        Date prevSystemDate = null;
        try {
            prevSystemDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(systemDate);
        } catch (Exception e) {
        }
        eodStatusReport.setPrevSystemDate(prevSystemDate);
        eodStatusReportService.saveEODStatusData(eodStatusReport);
        System.out.println("End of day status report: "+eodStatusReport);

        //Back Up HTD data to a File
        eodRes = bkp.writeHTDDataToFile(tranHeaderHistoryRepository.findAll());
        resList.add(eodRes);

        //Update HTD BKP Status in DB
        eodStatusReport = eodStatusReportService.findByEntityIdAndSysDate(EntityRequestContext.getCurrentEntityId(), systemDate);
        eodStatusReport.setHtdDataBackup(eodRes.isStatus());
        eodStatusReportRepository.save(eodStatusReport);
//
        //Back Up DTD Data to a File
        eodRes = bkp.writeDTDDataToFile(tranHeaderRepository.getTodaytransactions(systemDate, EntityRequestContext.getCurrentEntityId()));
        resList.add(eodRes);

//        //Update DTD BKP Status in DB
        eodStatusReport = eodStatusReportService.findByEntityIdAndSysDate(EntityRequestContext.getCurrentEntityId(), systemDate);
        eodStatusReport.setDtdDataBackup(eodRes.isStatus());
        eodStatusReportRepository.save(eodStatusReport);

        log.info("*** System Date is " + systemDate + " ***");
        log.info("*** 3. Log Out All Users From the System - " + new Date() + " ***");

        //Log Out Users
        String entityId = EntityRequestContext.getCurrentEntityId();
        eodRes = disableAllUserAccounts(entityId);
        resList.add(eodRes);

        //Update Users Log Out Status in DB
        eodStatusReport = eodStatusReportService.findByEntityIdAndSysDate(EntityRequestContext.getCurrentEntityId(), systemDate);
        eodStatusReport.setDisableUserAccounts(eodRes.isStatus());
        eodStatusReportRepository.save(eodStatusReport);

        log.info("*** 4. Take Pre EOD Data Back UP - " + new Date() + " ***");

        //Pre EOD Back Up
        eodRes = bkp.preEODBackup(username, password, db, "");
        resList.add(eodRes);

        //Update Disable User Accounts
        eodStatusReport = eodStatusReportService.findByEntityIdAndSysDate(EntityRequestContext.getCurrentEntityId(), systemDate);
        eodStatusReport.setPreEodDbBackup(eodRes.isStatus());
        eodStatusReportRepository.save(eodStatusReport);

        //Start Verification
        //Check Transactions Not Posted
        log.info("*** 5. Check Transactions Not Posted - " + new Date() + " ***");
        eodRes = checkTransactionsNotPosted(systemDate, entityId);
        resList.add(eodRes);

        //Update Check Not Posted Status in DB
        eodStatusReport = eodStatusReportService.findByEntityIdAndSysDate(EntityRequestContext.getCurrentEntityId(), systemDate);
        eodStatusReport.setCheckNotPostedTrans(eodRes.isStatus());
        eodStatusReportRepository.save(eodStatusReport);

        log.info("*** 6. Check Transactions Not Verified - " + new Date() + " ***");
        eodRes = checkTransactionsNotVerified(systemDate, entityId);
        resList.add(eodRes);

        //Update Unverified Trans Status in DB
        eodStatusReport = eodStatusReportService.findByEntityIdAndSysDate(EntityRequestContext.getCurrentEntityId(), systemDate);
        eodStatusReport.setCheckUnverifiedTrans(eodRes.isStatus());
        eodStatusReportRepository.save(eodStatusReport);

        log.info("*** 7. Check Sum of Debits and Credits - " + new Date() + " ***");
        eodRes = checkSumOfDebitsAndCredits(systemDate, entityId);
        resList.add(eodRes);

        //Update Check Sum of Dr and Cr
        eodStatusReport = eodStatusReportService.findByEntityIdAndSysDate(EntityRequestContext.getCurrentEntityId(), systemDate);
        eodStatusReport.setCheckDrAndCrSum(eodRes.isStatus());
        eodStatusReportRepository.save(eodStatusReport);

        eodResponse.setEodRes(resList);

        //Check status of each operation
        for (int l = 0; l < eodResponse.getEodRes().size(); l++) {
            if (!eodResponse.getEodRes().get(l).isStatus()) {
                eodResponse.setEodStatus(false);
                break;
            } else {
                eodResponse.setEodStatus(true);
            }
        }

        if (eodResponse.isEodStatus()) {
            log.info("*** 8. Move Data from DTD to HTD - " + new Date() + " ***");
            eodRes = moveDataFromDtdToHtd(systemDate);
            resList.add(eodRes);

            //Update DTD to HTD BKP status in DB
            eodStatusReport = eodStatusReportService.findByEntityIdAndSysDate(EntityRequestContext.getCurrentEntityId(), systemDate);
            eodStatusReport.setDtdToHtdBackup(eodRes.isStatus());
            eodStatusReportRepository.save(eodStatusReport);

            if (eodRes.isStatus()) {
                log.info("*** 9. Move System Date to the Next Working Day - " + new Date() + " ***");
                eodRes = moveSystemDate(systemDate);
                resList.add(eodRes);

                //Update Move System Date status in DB
                eodStatusReport = eodStatusReportService.findByEntityIdAndSysDate(EntityRequestContext.getCurrentEntityId(), systemDate);
                eodStatusReport.setMoveSystemDate(eodRes.isStatus());
                eodStatusReportRepository.save(eodStatusReport);

                //Add new Date in Response
                if (eodRes.isStatus()) {
                    eodResponse.setNewSystemDate(eodRes.getIssues().toString());

                    //Back Up Data Post EOD
//                    if (eodResponse.isEodStatus()) {
//                        //Enable User Accounts
//                        log.info("*** 10. ENABLE USER ACCOUNTS AFTER EOD PROCESS -  ***");
//                        eodRes = enableAllUserAccounts(entityId);
//                        resList.add(eodRes);

                        //Update Enable User Accounts status in DB
                        eodStatusReport = eodStatusReportService.findByEntityIdAndSysDate(EntityRequestContext.getCurrentEntityId(), systemDate);
                        eodStatusReport.setEnableUserAccounts(eodRes.isStatus());
                        eodStatusReportRepository.save(eodStatusReport);

                        log.info("*** 11. POST EOD DATA BACK UP - " + new Date() + " ***");
                        eodRes = bkp.postEODBackup(username, password, db, "");
                        resList.add(eodRes);

                        //Update POST EOD Data Back Up Status in DB
                        eodStatusReport = eodStatusReportService.findByEntityIdAndSysDate(EntityRequestContext.getCurrentEntityId(), systemDate);
                        eodStatusReport.setPostEodDbBackup(eodRes.isStatus());
                        eodStatusReportRepository.save(eodStatusReport);
//                    }
                } else {
                    System.out.println("About to SET New Date");
                    eodResponse.setNewSystemDate(String.valueOf(new Date()));
                }

                log.info("*** 10. ENABLE USER ACCOUNTS AFTER EOD PROCESS -  ***");
                eodRes = enableAllUserAccounts(entityId);
                resList.add(eodRes);
            }
        }

        eodResponse.setEodRes(resList);

        //Check status of each operation Again
        for (int l = 0; l < eodResponse.getEodRes().size(); l++) {
            if (!eodResponse.getEodRes().get(l).isStatus()) {
                eodResponse.setEodStatus(false);
                break;
            } else {
                eodResponse.setEodStatus(true);
            }
        }

        if (eodResponse.isEodStatus()) {
            //Update EOD Status in DB
            //Update System Date in DB
            Date nextSystemDate = null;
            try {
                nextSystemDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(eodResponse.getNewSystemDate());
            } catch (Exception e) {
                e.printStackTrace();
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            eodStatusReport = eodStatusReportService.findByEntityIdAndSysDate(EntityRequestContext.getCurrentEntityId(), sdf.format(prevSystemDate));
            eodStatusReport.setEodStatus(true);
            eodStatusReport.setNextSystemDate(nextSystemDate);
            eodStatusReportRepository.save(eodStatusReport);
        }
        return eodResponse;
    }


    //Log Out All Users (Disable All Users Temporarily)
    private EODRes disableAllUserAccounts(String entityId) {
        EODRes eodRes = new EODRes();
        eodRes.setEodStep("SIGN OUT ALL USERS");
        try {
            repository.disableUserAccounts(UserRequestContext.getCurrentUser(), EntityRequestContext.getCurrentEntityId());
            eodRes.setIssues("NA");
            eodRes.setMessage("ALL USERS LOGGED OUT OF THE SYSTEM SUCCESSFULLY!");
            eodRes.setStatus(true);
        } catch (Exception e) {
            log.info("*** Error When Disabling User Accounts - " + e.getLocalizedMessage() + " ***");
            eodRes.setIssues("NA");
            eodRes.setMessage("LOGGING OUT USERS FROM THE SYSTEM FAILED!");
            eodRes.setStatus(false);
        }
        return eodRes;
    }

    //Log In All Users (Enable All User Accounts After Successful EOD Process)
    private EODRes enableAllUserAccounts(String entityId) {
        EODRes eodRes = new EODRes();
        eodRes.setEodStep("ENABLE USER ACCOUNTS AFTER SUCCESSFUL EOD PROCESS");
        try {
            repository.enableUserAccounts(EntityRequestContext.getCurrentEntityId());
            eodRes.setIssues("NA");
            eodRes.setMessage("ALL USER ACCOUNTS ENABLED SUCCESSFULLY!");
            eodRes.setStatus(true);
        } catch (Exception e) {
            log.info("*** Error When Enabling User Accounts - " + e.getLocalizedMessage() + " ***");
            eodRes.setIssues("NA");
            eodRes.setMessage("ENABLE USER ACCOUNTS AFTER EOD FAILED!");
            eodRes.setStatus(false);
        }
        return eodRes;
    }

    //Fetch All Daily Transactions (From Dtd)
    public DailyTransactionsResponse fetchAllTransactions(String date, String entityId) {
        DailyTransactionsResponse dtr = new DailyTransactionsResponse();
        try {
            System.out.println("INSIDE");
            List<TransactionHeader> allTransactions = tranHeaderRepository.getTodaytransactions(date, EntityRequestContext.getCurrentEntityId());
            System.out.println("All Tra: "+allTransactions);
            List<TransactionHeader> unVerifiedTransactions = new ArrayList<>();
            List<TransactionHeader> notPostedTransactions = new ArrayList<>();
            List<TransactionHeader> completeTransactions = new ArrayList<>();

            for (TransactionHeader t : allTransactions) {
                //Get unverified transactions
                if (t.getVerifiedFlag() == 'N') {
                    unVerifiedTransactions.add(t);
                }

                //Get Not Posted Transactions
                if (t.getPostedFlag() == 'N') {
                    notPostedTransactions.add(t);
                }

                if (t.getPostedFlag() == 'Y' && t.getVerifiedFlag() == 'Y') {
                    completeTransactions.add(t);
                }
            }
            dtr.setUnverifiedTransactions(unVerifiedTransactions);
            dtr.setCompletedTransactions(completeTransactions);
            dtr.setNotPostedTransactions(notPostedTransactions);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dtr;
    }


    //Check Verification and Post Status For transactions
    public EODRes checkTransactionsNotPosted(String systemDate, String entityId) {
        EODRes eodRes = new EODRes();
        eodRes.setEodStep("CHECK NOT POSTED TRANSACTIONS");

        DailyTransactionsResponse dtr = fetchAllTransactions(systemDate, entityId);
        if (dtr.getNotPostedTransactions().isEmpty()) {
            eodRes.setIssues(dtr.getNotPostedTransactions());
            eodRes.setMessage("ALL TRANSACTIONS ARE POSTED!");
            eodRes.setStatus(true);
        } else {
            eodRes.setIssues(dtr.getNotPostedTransactions());
            eodRes.setMessage(dtr.getNotPostedTransactions().size() + " TRANSACTIONS NOT POSTED FOUND!");
            eodRes.setStatus(false);
        }

        return eodRes;
    }

    //Check Transactions Verification Status
    public EODRes checkTransactionsNotVerified(String systemDate, String entityId) {
        EODRes eodRes = new EODRes();
        eodRes.setEodStep("CHECK UNVERIFIED TRANSACTIONS");

        DailyTransactionsResponse dtr = fetchAllTransactions(systemDate, entityId);
        if (dtr.getUnverifiedTransactions().isEmpty()) {
            eodRes.setIssues(dtr.getUnverifiedTransactions());
            eodRes.setMessage("ALL TRANSACTIONS ARE VERIFIED!");
            eodRes.setStatus(true);
        } else {
            eodRes.setIssues(dtr.getUnverifiedTransactions());
            eodRes.setMessage(dtr.getUnverifiedTransactions().size() + " UNVERIFIED TRANSACTIONS FOUND!");
            eodRes.setStatus(false);
        }

        return eodRes;
    }

    //Check Balances (Sum of Debits and Credits == 0)
    public EODRes checkSumOfDebitsAndCredits(String systemDate, String entityId) {
        EODRes eodRes = new EODRes();
        eodRes.setEodStep("CHECK SUM OF DEBITS & CREDITS IN DTD!");
        DailyTransactionsResponse dtr = fetchAllTransactions(systemDate, entityId);
        List<PartTran> debitPartTrans = new ArrayList<>();
        List<PartTran> creditPartTrans = new ArrayList<>();
        if (!dtr.getCompletedTransactions().isEmpty()) {
            for (TransactionHeader th : dtr.getCompletedTransactions()) {
                for (int i = 0; i < th.getPartTrans().size(); i++) {
                    if (th.getPartTrans().get(i).getPartTranType().equalsIgnoreCase("Debit")) {
                        debitPartTrans.add(th.getPartTrans().get(i));
                    } else if (th.getPartTrans().get(i).getPartTranType().equalsIgnoreCase("Credit")) {
                        creditPartTrans.add(th.getPartTrans().get(i));
                    }
                }
            }

            //Check Total Debits
            AtomicReference<Double> sumCredits = new AtomicReference<>(0.0);
            AtomicReference<Double> sumDebits = new AtomicReference<>(0.0);

            for (PartTran partTran : debitPartTrans) {
                sumDebits.updateAndGet(v -> v + partTran.getTransactionAmount());
            }

            //Check Total Credits
            for (PartTran partTran : creditPartTrans) {
                sumCredits.updateAndGet(v -> v + partTran.getTransactionAmount());
            }

            //Check difference Between Credits and Debits
            if (sumDebits.get().doubleValue() == sumCredits.get().doubleValue()) {
                eodRes.setIssues("NA");
                eodRes.setMessage("TOTAL CREDITS (" + sumCredits.get() + ") == TOTAL DEBITS (" + sumDebits.get() + ")!");
                eodRes.setStatus(true);
            } else if (sumDebits.get() < sumCredits.get()) {
                eodRes.setIssues(dtr.getCompletedTransactions());
                eodRes.setMessage("TOTAL CREDITS (" + sumCredits.get() + ") < TOTAL DEBITS (" + sumDebits.get() + ") BY AMOUNT OF " + (sumCredits.get() - sumDebits.get()) + "!");
                eodRes.setStatus(false);
            } else if (sumDebits.get() > sumCredits.get()) {
                eodRes.setIssues(dtr.getCompletedTransactions());
                eodRes.setMessage("TOTAL DEBITS (" + sumDebits.get() + ") > TOTAL CREDITS (" + sumCredits.get() + ") BY AMOUNT OF " + (sumDebits.get() - sumCredits.get()) + "!");
                eodRes.setStatus(false);
            }
        } else {
            eodRes.setIssues(dtr.getCompletedTransactions());
            eodRes.setMessage("NO TRANSACTIONS DONE TODAY!");
            eodRes.setStatus(true);
        }
        return eodRes;
    }


    //Move System Date to Next Working Day After EOD
    public EODRes moveSystemDate(String systemDdate) {
        EODRes eodRes = new EODRes();
        eodRes.setEodStep("MOVE SYSTEM DATE TO THE NEXT WORKING DAY!");
        String newDate = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar c = Calendar.getInstance();
            c.setTime(sdf.parse(systemDdate));
            newDate = sdf.format(c.getTime());

            if (c.get(Calendar.DAY_OF_WEEK) == 7) {
                c.add(Calendar.DATE, 2);
                newDate = sdf.format(c.getTime());
            } else if (c.get(Calendar.DAY_OF_WEEK) == 1) {
                c.add(Calendar.DATE, 1);
                newDate = sdf.format(c.getTime());
            } else if (c.get(Calendar.DAY_OF_WEEK) == 6) {
                c.add(Calendar.DATE, 3);
                newDate = sdf.format(c.getTime());
            } else {
                c.add(Calendar.DATE, 1);
                newDate = sdf.format(c.getTime());
            }

            //Update System Date in System Date Table
            SystemCalendar systemCalendar = repository.findByEntityIdAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), 'N').get(0);
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(newDate);
            systemCalendar.setSystemDate(date);
            repository.save(systemCalendar);

            eodRes.setIssues(newDate);
            eodRes.setMessage("SUCCESSFULLY MOVED SYSTEM DATE FROM " + systemDdate + " TO " + newDate);
            eodRes.setStatus(true);
            return eodRes;
        } catch (Exception e) {
            eodRes.setIssues(e.getMessage().toUpperCase());
            eodRes.setMessage("ERROR OCCURRED WHEN MOVING SYSTEM DATE FROM " + systemDdate + " TO NEXT DAY!");
            eodRes.setStatus(false);
            return eodRes;
        }
    }

    //Move Data From DTD to HTD
    public EODRes moveDataFromDtdToHtd(String systemDate) {
        EODRes eodRes = new EODRes();
        eodRes.setEodStep("MOVE DATA FROM DTD TABLE TO HTD!");

        String entityId = EntityRequestContext.getCurrentEntityId();
        System.out.println("ENTITY ID HERE: "+entityId);
//        eodStatusReportService.calculateAndSaveEOD(systemDate, entityId);

        System.out.println("SYSTEM DATE: "+systemDate);

        List<TransactionHeader> dtdTransactions = tranHeaderRepository.getTodaytransactions(systemDate, EntityRequestContext.getCurrentEntityId());
        List<TransactionHeader> htdTransactions = dtdTransactions;
        System.out.println("DTD TRANSACTIONS: "+dtdTransactions);
        TranHeaderHistory htd;

        Gson gson = new Gson();
        for (int i = 0; i < dtdTransactions.size(); i++) {
            htd = gson.fromJson(gson.toJson(dtdTransactions.get(i)), TranHeaderHistory.class);

            try {
                //Update EOD status in DTD
                dtdTransactions.get(i).setEodStatus("Y");
                tranHeaderRepository.save(dtdTransactions.get(i));

                //Move Record to HTD
                htd.setEodStatus("Y");
                tranHeaderHistoryRepository.save(htd);
            } catch (Exception e) {
                eodRes.setIssues(e.getMessage().toUpperCase());
                eodRes.setMessage("ERROR OCCURRED WHEN MOVING DATA FROM DTD TO HTD!");
                eodRes.setStatus(false);
                return eodRes;
            }
        }
        try {
//            Delete Data From DTD table After back up
            for (TransactionHeader th : tranHeaderRepository.AllBackedUp()) {
                tranHeaderRepository.deleteById(th.getSn());
            }

            eodRes.setIssues("NA");
            eodRes.setMessage("SUCCESSFULLY MOVED DATA FROM DTD TO HTD!");
            eodRes.setStatus(true);
            return eodRes;
        } catch (Exception e) {
            eodRes.setIssues(e.getMessage().toUpperCase());
            eodRes.setMessage("ERROR OCCURRED WHEN DELETING DATA IN DTD!");
            eodRes.setStatus(false);
            return eodRes;
        }
    }
}
