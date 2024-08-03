package com.emtechhouse.accounts.Models.Accounts.AccountsRecurringFees;

import com.emtechhouse.accounts.DTO.ChargeCollectionReq;
import com.emtechhouse.accounts.Models.Accounts.Account;
import com.emtechhouse.accounts.Models.Accounts.AccountRepository;
import com.emtechhouse.accounts.Models.Accounts.LienComponent.LienRepository;
import com.emtechhouse.accounts.Models.Accounts.Savings.SavingRepo;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.GeneralProductDetails;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ItemServiceCaller;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ProductFees.FeeDto;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ProductFees.ProductFeesService;
import com.emtechhouse.accounts.Models.Accounts.Validators.ValidatorService;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans.PartTran;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TransactionHeader;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TransactionProcessing;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TransactionsController;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.DatesCalculator;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import com.emtechhouse.accounts.Utils.ServiceCaller;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AccountRecurringFeeService {
    @Autowired
    private TransactionsController transactionsController;
    @Autowired
    private SavingRepo savingRepo;

    @Autowired
    private DatesCalculator datesCalculator;
    @Autowired
    private RecurringFeeDemandRepo recurringFeeDemandRepo;
    @Autowired
    private AccountRecurringFeeRepository accountRecurringFeeRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ValidatorService validatorService;
    @Autowired
    private ItemServiceCaller itemServiceCaller;
    @Autowired
    private ProductFeesService productFeesService;
    @Autowired
    private  ServiceCaller serviceCaller;

    @Autowired
    private LienRepository lienRepository;


    @Autowired
    private TransactionProcessing transactionProcessing;

    public void initiateRecurringFees() {
        List<RecurringFeeDemandRepo.ProductRecuringFee> productRecuringFees = recurringFeeDemandRepo.getProductRecuringFees();
        System.out.println(productRecuringFees.size());
        for (RecurringFeeDemandRepo.ProductRecuringFee recuringFee: productRecuringFees) {
            System.out.println(recuringFee.getEventCode()+" "+recuringFee.getProduct_code());
            List<String> newAccounts = accountRecurringFeeRepository.getNewAccountRecurringFees(recuringFee.getProduct_code(), recuringFee.getEventCode());
//            System.out.println("Count of new recuring fees");
//            System.out.println(recuringFee.getEventCode());
//            System.out.println(recuringFee.getProduct_code());
//            System.out.println(newAccounts.size());
            for (String acid: newAccounts) {
                Account account = accountRepository.findByAcid(acid).get();
                AccountRecurringFee accountRecurringFee = new AccountRecurringFee();
                accountRecurringFee.setAccount(account);
                accountRecurringFee.setEventCode(recuringFee.getEventCode());
                accountRecurringFee.setCreatedOn(new Date());
                accountRecurringFee.setEndDate(recuringFee.getEndDate());
                Date nextRecurringDate = recuringFee.getNextCollectionDate();
                while (datesCalculator.getDaysDifference(nextRecurringDate, account.getOpeningDate()) > 0) {
                    nextRecurringDate = datesCalculator.addDate(nextRecurringDate,1, "MONTHS");
                }
                accountRecurringFee.setNextCollectionDate(nextRecurringDate);
                System.out.println(accountRecurringFee);
                accountRecurringFeeRepository.save(accountRecurringFee);
            }
            System.out.println("-----------------------------------------------------------------------------------");
//            System.out.println(recuringFee);
        }
        System.out.println("Initiated recurring fees");
    }

    public void executeRecurringFees() {
        List<AccountRecurringFee> accountRecurringFees = accountRecurringFeeRepository.findAllDue();
        for (AccountRecurringFee accountRecurringFee: accountRecurringFees) {
//            System.out.println(accountRecurringFee);
            executeRecurringFees(accountRecurringFee);
        }
    }


    public void registerLastRunAttempt(AccountRecurringFee accountRecurringFee) {
        accountRecurringFee.setLastRunAttemptDate(new Date());
        if (accountRecurringFee.getCreatedOn() == null)
            accountRecurringFee.setCreatedOn(new Date());
        accountRecurringFeeRepository.updateLastRunAttemptDate(new Date(), accountRecurringFee.getId());
    }

    @Transactional
    public void executeRecurringFees(AccountRecurringFee accountRecurringFee) {
        System.out.println("--------------------------------------------------------------------------");
        Double availableBalance = accountRepository.getAvailableBalance(accountRecurringFee.getAccount().getAcid());
        if (accountRepository.getAvailableBalance(accountRecurringFee.getAccount().getAcid()) < 20 ) {
            System.out.println("No enough money in account");
            registerLastRunAttempt(accountRecurringFee);
            return;
        }



        System.out.println("Available balance: "+availableBalance);
        List<ChargeCollectionReq> chargeCollectionReqs = new ArrayList<>();
        ChargeCollectionReq chargeCollectionReq = new ChargeCollectionReq();
        chargeCollectionReq.setDebitAc(accountRecurringFee.getAccount().getAcid());
        chargeCollectionReq.setTransactionAmount(300.0);
        chargeCollectionReq.setTransParticulars("");
        chargeCollectionReq.setChargeCode(accountRecurringFee.getEventCode());
        chargeCollectionReqs.add(chargeCollectionReq);
//        System.out.println(chargeCollectionReq);
        EntityResponse response =  serviceCaller.getWithdrawalCharges(chargeCollectionReqs);
//        System.out.println(
//                response
//        );

//        Double totalDebits = 0.0;

        if (response.getStatusCode() == HttpStatus.FOUND.value()) {
            JSONObject obj = new JSONObject(response);
            JSONArray ja = obj.getJSONArray("entity");
            List<PartTran> allPartTrans = new ArrayList<>();
            for (Object o : ja) {
                JSONObject ob = new JSONObject(o.toString());
                String acid = ob.get("acid").toString();
                if (acid.trim().equalsIgnoreCase(accountRecurringFee.getAccount().getAcid().trim()))
                    continue;

                List<PartTran> partTrans = createPartTrans("KES",
                        ob.getString("transactionParticulars")+" - "+datesCalculator.dateFormat(accountRecurringFee.getNextCollectionDate()),
                        Math.abs(ob.getDouble("transactionAmount")), accountRecurringFee.getAccount().getAcid(),  accountRepository.findByAcid(acid).get().getAcid());
                allPartTrans.addAll(partTrans);
            }
            Map<String, Double> minifiedDebits  = allPartTrans.stream().collect(Collectors.groupingBy(PartTran::getAcid,Collectors.summingDouble(PartTran::getTransactionAmount)));
            if (minifiedDebits.get(accountRecurringFee.getAccount().getAcid()) > availableBalance) {
                System.out.println("Total deduct amount is less than available balance");
                registerLastRunAttempt(accountRecurringFee);
                return;
            }
            TransactionHeader transactionHeader = createTransactionHeader("KES", allPartTrans);
//            if (true)
//                return ;
            if (transactionHeader != null) {
                EntityResponse transactionRes = transactionsController.systemTransaction1(transactionHeader).getBody();
//            System.out.println(transactionRes);
                if (transactionRes.getStatusCode().equals(HttpStatus.OK.value())) {
                    Date previosDate = accountRecurringFee.getNextCollectionDate();
                    Date nextRepaymentDate = datesCalculator.addDate(accountRecurringFee.getNextCollectionDate(),1, "MONTHS");
                    accountRecurringFee.setNextCollectionDate(nextRepaymentDate);
//                    System.out.println(accountRecurringFee);
                    if (accountRecurringFee.getCreatedOn() == null)
                        accountRecurringFee.setCreatedOn(previosDate);
                    accountRecurringFee.setLastRunAttemptDate(new Date());
                    accountRecurringFeeRepository.updateNextRunDate(nextRepaymentDate, accountRecurringFee.getId());
                } else {
                    registerLastRunAttempt(accountRecurringFee);
                }
            } else {
                registerLastRunAttempt(accountRecurringFee);
                System.out.println("Saving transaction is null");
            }
        }
    }
//    public void executeRecurringFees(AccountRecurringFee accountRecurringFee) {
//        List<ChargeCollectionReq> chargeCollectionReqs = new ArrayList<>();
//        ChargeCollectionReq chargeCollectionReq = new ChargeCollectionReq();
//        chargeCollectionReq.setDebitAc(accountRecurringFee.getAccount().getAcid());
//        chargeCollectionReq.setTransactionAmount(300.0);
//        chargeCollectionReq.setTransParticulars("");
//        chargeCollectionReq.setChargeCode(accountRecurringFee.getEventCode());
//        chargeCollectionReqs.add(chargeCollectionReq);
//        System.out.println(chargeCollectionReq);
//        EntityResponse response =  serviceCaller.getWithdrawalCharges(chargeCollectionReqs);
////        System.out.println(
////                response
////        );
//
//        if (response.getStatusCode() == HttpStatus.FOUND.value()) {
//            JSONObject obj = new JSONObject(response);
//            JSONArray ja = obj.getJSONArray("entity");
//            for (Object o : ja) {
//                JSONObject ob = new JSONObject(o.toString());
//                String acid = ob.get("acid").toString();
//                if (acid.trim().equalsIgnoreCase(accountRecurringFee.getAccount().getAcid().trim()))
//                    continue;
//                Lien lien =  new Lien();
//                lien.setEffectiveFrom(new Date());
//                lien.setLienCode( getSaltString());
//                lien.setLienAmount(ob.getDouble("transactionAmount"));
//                lien.setSourceAccount(accountRecurringFee.getAccount());
//                lien.setStatus(CONSTANTS.L_ACTIVE);
//                lien.setLienType(ob.getString("parttranIdentity"));
//                lien.setLienDescription(ob.getString("transactionParticulars")+" - "+datesCalculator.dateFormat(accountRecurringFee.getNextCollectionDate()));
//                lien.setDestinationAccount(accountRepository.findByAcid(acid).get());
//                lienRepository.save(lien);
////                AccountRepository.AcTransactionDetails acDetailsDb = accountRepository.getAccountTransactionDetails(acid).get();
////                partTran1.setPartTranType(ob.getString("partTranType"));
////                partTran1.setParttranIdentity(ob.getString("parttranIdentity"));
////                partTran1.setCurrency(acDetailsDb.getCurrency());
////                partTran1.setTransactionParticulars(ob.getString("transactionParticulars"));
////                partTran1.setIsoFlag('N');
////                partTran1.setExchangeRate("1");
////                partTran1.setTransactionDate(partTranList.get(0).getTransactionDate());
////                partTran1.setAccountBalance(acDetailsDb.getAccountBalance());
////                partTran1.setAccountType(acDetailsDb.getAccountType());
////                partTran1.setIsWelfare(false);
//            }
//            Date previosDate = accountRecurringFee.getNextCollectionDate();
//            Date nextRepaymentDate = datesCalculator.addDate(accountRecurringFee.getNextCollectionDate(),1, "MONTHS");
//            accountRecurringFee.setNextCollectionDate(nextRepaymentDate);
//            System.out.println(accountRecurringFee);
//            if (accountRecurringFee.getCreatedOn() == null)
//                accountRecurringFee.setCreatedOn(previosDate);
//        }
//    }
    protected String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }




    public EntityResponse getFeeAmtInfo(String prodCode) {
        try {
            EntityResponse response =new EntityResponse<>();
            response=itemServiceCaller.getGeneralProductDetail1(prodCode,CONSTANTS.SAVINGS_ACCOUNT);
            if(response.getStatusCode()== HttpStatus.OK.value()) {
                GeneralProductDetails g = (GeneralProductDetails) response.getEntity();

                if(g.getCollectLedgerFee() != null) {
                    if(g.getCollectLedgerFee().equals(true)) {

                        String eventId= g.getLedgerFeeEventIdCode();

                        //get fee amount
                        EntityResponse feeRes= productFeesService.getProductFees2(eventId);
                        if(feeRes.getStatusCode()== HttpStatus.OK.value()) {
                            FeeDto f= (FeeDto) feeRes.getEntity();


                            AccountRecurringFee accountRecurringFee = new AccountRecurringFee();

                            Double availableBalance = accountRepository.getAvailableBalance(accountRecurringFee.getAccount().getAcid());

                            // Check if available balance is sufficient for ledger fee deduction
                            if (availableBalance < f.getAmt()) {
                                System.out.println("Not enough money in account");
                                registerLastRunAttempt(accountRecurringFee);


                            }else

                            response.setStatusCode(HttpStatus.OK.value());
                            response.setEntity(f);
                            response.setMessage(HttpStatus.OK.getReasonPhrase());
                        }

                    }else {
                        response.setMessage("Ledger fee not applicable for this product");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity(null);
                    }
                }else {
                    response.setMessage("Ledger fee not applicable for this product");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity(null);
                }

            }
            return response;
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }


    public List<PartTran> createPartTrans(String loanCurrency,
                                          String transDesc,
                                          Double totalAmount,
                                          String drAc,
                                          String crAc) {

        PartTran drPartTran = new PartTran();
        drPartTran.setPartTranType(CONSTANTS.DEBITSTRING);
        drPartTran.setTransactionAmount(totalAmount);
        drPartTran.setAcid(drAc);
        drPartTran.setCurrency(loanCurrency);
        drPartTran.setExchangeRate("");
        drPartTran.setParttranIdentity("NORMAL");
        drPartTran.setTransactionDate(new Date());
        drPartTran.setTransactionParticulars(transDesc);
        drPartTran.setIsoFlag(CONSTANTS.YES);

        PartTran crPartTran = new PartTran();
        crPartTran.setPartTranType(CONSTANTS.CREDITSTRING);
        crPartTran.setTransactionAmount(totalAmount);
        crPartTran.setAcid(crAc);
        crPartTran.setCurrency(loanCurrency);
        crPartTran.setExchangeRate("");
        crPartTran.setParttranIdentity("NORMAL");
        crPartTran.setTransactionDate(new Date());
        crPartTran.setTransactionParticulars(transDesc);
        crPartTran.setIsoFlag(CONSTANTS.YES);

        List<PartTran> partTranList =new ArrayList<>();
        partTranList.add(drPartTran);
        partTranList.add(crPartTran);

        return partTranList;
    }

    public TransactionHeader createTransactionHeader(String loanCurrency,
                                                     List<PartTran> partTrans) {
        Double totalAmount = 0.0;
        for (PartTran partTran:  partTrans)
            if (partTran.getPartTranType().equalsIgnoreCase("debit"))
                totalAmount += partTran.getTransactionAmount();
        TransactionHeader transactionHeader = new TransactionHeader();
        transactionHeader.setTransactionType(CONSTANTS.SYSTEM_USERNAME);
        transactionHeader.setCurrency(loanCurrency);
        transactionHeader.setTransactionDate(new Date());
        transactionHeader.setEntityId(CONSTANTS.SYSTEM_ENTITY);
        transactionHeader.setTotalAmount(totalAmount);


        transactionHeader.setPartTrans(partTrans);
        return transactionHeader;
    }
    public Double roundOff2(Double number, int scale) {
        try {
            BigDecimal bigDecimal= new BigDecimal(number).setScale(scale, RoundingMode.HALF_EVEN);
            return bigDecimal.doubleValue();
        } catch (Exception e) {
            log.info("Caught Error {}"+e);
            return null;
        }
    }
}