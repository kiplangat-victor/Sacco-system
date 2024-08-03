package com.emtechhouse.accounts.TransactionService.BatchJobs;

import com.emtechhouse.accounts.TransactionService.Requests.RepaymentAcounts;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.Benevolent.BenevolentFunds;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.Benevolent.BenevolentFundsRepo;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.NoneWithdrawDeposit.NoneWithdrawableDepositsRepo;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.NoneWithdrawDeposit.NonwithdrawableDeposits;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans.PartTran;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TranHeaderRepository;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TranHeaderService;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TransactionHeader;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TransactionsController;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Configuration
//@EnableScheduling
@Slf4j
public class SchedulerConfig {
    @Autowired
    private TranHeaderRepository tranrepo;
    @Autowired
    private NoneWithdrawableDepositsRepo nwrepo;
    @Autowired
    private BenevolentFundsRepo bfrepo;
    @Autowired
    private TranHeaderService tranHeaderService;
    @Autowired
    private TransactionsController controller;
    //    @Scheduled(cron = "00 */5 * * * *")
    public boolean collectDeposit() {
        Date today = new Date();

        Date tomorrow = new Date(today.getTime() + (1000 * 60 * 60 * 24));

        String pattern = "yyyy-MM-dd";//2020-07-18
        SimpleDateFormat formater = new SimpleDateFormat(pattern);
        LocalDate currentDate = LocalDate.parse(formater.format(tomorrow));
        System.out.println("currentDate :" + currentDate);

        Integer day = currentDate.getDayOfMonth();

        System.out.println("DayOfMonth :: " + day);
        if (day == 20) {
            System.out.println("Tomorrow is First day of the month, " + day);
            System.out.println(" --TRUE--");
            try {
                NonWithdrawalDepCollector();
                benevolentCollector();
            } catch (Exception e) {
                log.info("ERROR " + e.getMessage());
            }
            return true;

        } else {
            System.out.println("Tomorrow is date " + day);
            System.out.println("--FALSE--");
            return false;
        }


    }


    public void NonWithdrawalDepCollector() {

        System.out.println("Do the given job now.....");
        System.out.println("Fetching repayment accounts.....");
        List<RepaymentAcounts> list = tranrepo.fetchRepaymentsAccounts("001");
        System.out.println("None withdrawal deposit members size" + list.size());
        ArrayList<NonwithdrawableDeposits> nwdepositsList = new ArrayList<>();

        for (RepaymentAcounts r : list) {
            NonwithdrawableDeposits nwdeposit = new NonwithdrawableDeposits();
            String acid = r.getAcid();
            Double balance = r.getAccountBalance();
            System.out.println(acid + " - " + balance);
            //save to model
            nwdeposit.setAcid(r.getAcid());
            nwdeposit.setAmount(1000.00);
            nwdeposit.setAccountName(r.getAccountName());
            nwdeposit.setCollectionDate(new Date());
//            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat month = new SimpleDateFormat("MMMMMMMMMMM");
//            String month=calendar.get(Calendar.MONTH);//january
            String currentMonth = month.format(new Date());
            nwdeposit.setMonth(currentMonth);
            nwdeposit.setEntityId("001");
            System.out.println("Month : " + currentMonth);
            log.info("Saving none widthdrawal deposit collections...");
            nwrepo.save(nwdeposit);
//            nwdepositsList.add(nwdeposit);

        }
//        log.info("Widthdrawal deposit collections  list to be saved :: "+ nwdepositsList.size());
//        nwrepo.saveAll(nwdepositsList);
//        log.info("Saved!");
    }

    public void benevolentCollector() {

        System.out.println("Do the given job now.....");
        System.out.println("Collecting benevolent funds from repayment accounts.....");
        List<RepaymentAcounts> list = tranrepo.fetchRepaymentsAccounts(EntityRequestContext.getCurrentEntityId());
        System.out.println(list.size());
        log.info("Benevolent funds members size " + list.size());

        ArrayList<BenevolentFunds> bflist = new ArrayList<>();

        for (RepaymentAcounts r : list) {
            BenevolentFunds bf = new BenevolentFunds();
            String acid = r.getAcid();
            Double balance = r.getAccountBalance();
            System.out.println(acid + " - " + balance);
            //create & save to model
            bf.setAcid(r.getAcid());
            bf.setAmount(300.00);
            bf.setEntityId("001");
            bf.setAccountName(r.getAccountName());
            bf.setCollectionDate(new Date());
            SimpleDateFormat month = new SimpleDateFormat("MMMMMMMMMMM");
            String currentMonth = month.format(new Date());
            bf.setMonth(currentMonth);
            System.out.println("Month : " + currentMonth);
            log.info("Saving benevolent collections...");
            bfrepo.save(bf);
//            bflist.add(bf);

        }
//        log.info("Benevolent list to e saved :: "+bflist.size());
//        bfrepo.saveAll(bflist);
        log.info("Saved!");
    }

    //    @Scheduled(cron = "00 */1 * * * *")
    public void createNWDTransaction() {
        log.info("Building transaction ...");

        List<NonwithdrawableDeposits> nwdlist = nwrepo.findByStatusAndEntityId("pending", "001");
        log.info("NONE withdrawal deposit list size " + nwdlist.size());
        ArrayList<TransactionHeader> nwdepositTransactions = new ArrayList<>();
        if (nwdlist.size() > 0) {
            for (NonwithdrawableDeposits d : nwdlist) {
                TransactionHeader t = new TransactionHeader();
                try {
                    log.info("initiating Building transaction ...");

                    t.setEntityId(d.getEntityId());
                    t.setTransactionDate(new Date());
                    t.setRcre(new Date());
                    t.setCurrency("KES");
                    t.setTransactionType("Transfer");
                    t.setEnteredBy("SYSTEM");
                    t.setEnteredFlag(CONSTANTS.YES);
                    t.setEnteredTime(new Date());
                    t.setTransactionCode(tranHeaderService.generatecSystemCode(6));

                    ArrayList<PartTran> partTrans = new ArrayList<>();
                    PartTran p1 = new PartTran();
                    p1.setCurrency("KES");
                    p1.setAcid(d.getAcid());
                    p1.setIsoFlag('S');
                    p1.setTransactionDate(new Date());
                    p1.setExchangeRate("1");
//              p1.setAccountBalance();
                    p1.setPartTranType("Debit");
                    p1.setTransactionAmount(Double.valueOf(d.getAmount()));
                    p1.setTransactionParticulars("None withdwawable Deposit Contribution");

                    PartTran p2 = new PartTran();
                    p2.setCurrency("KES");
                    p2.setAcid("SA01-010443");
                    p2.setIsoFlag('S');
                    p2.setTransactionDate(new Date());
                    p2.setExchangeRate("1");
//              p2.setAccountBalance();
                    p2.setPartTranType("Credit");
                    p2.setTransactionAmount(Double.valueOf(d.getAmount()));
                    p2.setTransactionParticulars("None withdwawable Deposit Contribution");

                    partTrans.add(p1);
                    partTrans.add(p2);

                    t.setPartTrans(partTrans);

                    tranrepo.save(t);
                    d.setStatus("initiated");
                    nwrepo.save(d);
                    nwdepositTransactions.add(t);


                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("Error ", e.getMessage());

                }

            }
            log.info("Posting transactions...");
            log.info("Transaction List size :: "+nwdepositTransactions.size());
            systemTransactions(nwdepositTransactions);


        } else {
            log.info("SYSTEM CHECK =<Monthly contributions> Record not found");
        }


    }

    //    @Scheduled(cron = "00 */1 * * * *")
    public void createBnTransaction() {
        log.info("Building transaction ...");
        List<BenevolentFunds> benevolentFunds = bfrepo.findByStatusAndEntityId("pending", "001");
        ArrayList<TransactionHeader> bfTransactions = new ArrayList<>();
        log.info("Benefaulent list found " + benevolentFunds.size());
        if (benevolentFunds.size() > 0) {
            for (BenevolentFunds b : benevolentFunds) {
                TransactionHeader t = new TransactionHeader();
                try {
                    log.info("Initiating Building transaction ...");

                    t.setEntityId(b.getEntityId());
                    t.setTransactionDate(new Date());
                    t.setRcre(new Date());
                    t.setCurrency("KES");
                    t.setTransactionType("Transfer");
                    t.setEnteredBy("SYSTEM");
                    t.setEnteredFlag(CONSTANTS.YES);
                    t.setEnteredTime(new Date());
                    t.setTransactionCode(tranHeaderService.generatecSystemCode(6));

                    ArrayList<PartTran> partTrans = new ArrayList<>();
                    PartTran p1 = new PartTran();
                    p1.setCurrency("KES");
                    p1.setAcid(b.getAcid());
                    p1.setIsoFlag('S');
                    p1.setTransactionDate(new Date());
                    p1.setExchangeRate("1");
//        p1.setAccountBalance();
                    p1.setPartTranType("Debit");
                    p1.setTransactionAmount(Double.valueOf(b.getAmount()));
                    p1.setTransactionParticulars("Benevolent Contribution");

                    PartTran p2 = new PartTran();
                    p2.setCurrency("KES");
                    p2.setAcid("SA02-010443");
                    p2.setIsoFlag('S');
                    p2.setTransactionDate(new Date());
                    p2.setExchangeRate("1");
//        p2.setAccountBalance();
                    p2.setPartTranType("Credit");
                    p2.setTransactionAmount(Double.valueOf(b.getAmount()));
                    p2.setTransactionParticulars("Benevolent Contribution");

                    partTrans.add(p1);
                    partTrans.add(p2);


                    t.setPartTrans(partTrans);

                    tranrepo.save(t);
                    b.setStatus("initiated");
                    bfrepo.save(b);
                    bfTransactions.add(t);


                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("Error ", e.getMessage());
                }

            }
            //post Transactions
            log.info("Posting transactions...");
            systemTransactions(bfTransactions);

        } else {
            log.info("SYSTEM CHECK =<Monthly contributions> Record not found");
        }


    }


    public void systemTransactions(ArrayList<TransactionHeader> transactionHeaders) {
        log.info("Initiating system transaction...");

        if (transactionHeaders.size() > 0) {
            for (TransactionHeader transaction : transactionHeaders) {

                try {
                    ResponseEntity<EntityResponse> response = controller.systemTransaction(transaction);
                    EntityResponse res = response.getBody();
                    log.info("Response Message ::"+res.getMessage());
                    log.info("Status code : " +res.getStatusCode());
                } catch (Exception e) {
                    log.error("Error " + e.getMessage());
                }
            }
        }else {
            log.info("No Transactions found...");
        }
    }


}
