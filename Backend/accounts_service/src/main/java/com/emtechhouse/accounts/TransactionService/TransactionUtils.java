package com.emtechhouse.accounts.TransactionService;

import com.emtechhouse.accounts.DTO.ChargeCollectionReq;
import com.emtechhouse.accounts.Models.Accounts.AccountRepository;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans.PartTran;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TransactionHeader;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import com.emtechhouse.accounts.Utils.ServiceCaller;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TransactionUtils {


    @Autowired
    private ServiceCaller serviceCaller;
    @Autowired
    private AccountRepository accountRepository;

    public TransactionHeader createTransactionHeader(String loanCurrency,
                                                     String transDesc,
                                                     Double totalAmount,
                                                     String drAc,
                                                     String crAc) {
        return createTransactionHeader(loanCurrency, transDesc, "NORMAL", totalAmount, drAc, crAc);
    }
    public TransactionHeader createTransactionHeader(String loanCurrency,
                                                     String transDesc,
                                                     String tranIdentity,
                                                     Double totalAmount,
                                                     String drAc,
                                                     String crAc) {
        TransactionHeader transactionHeader = new TransactionHeader();
        transactionHeader.setTransactionType(CONSTANTS.SYSTEM_USERNAME);
        transactionHeader.setCurrency(loanCurrency);
        transactionHeader.setTransactionDate(new Date());
        transactionHeader.setEntityId(CONSTANTS.SYSTEM_ENTITY);
        transactionHeader.setTotalAmount(totalAmount);

        PartTran drPartTran = new PartTran();
        drPartTran.setPartTranType(CONSTANTS.DEBITSTRING);
        drPartTran.setTransactionAmount(totalAmount);
        drPartTran.setAcid(drAc);
        drPartTran.setCurrency(loanCurrency);
        drPartTran.setExchangeRate("");
        drPartTran.setTransactionDate(new Date());
        drPartTran.setTransactionParticulars(transDesc);
        drPartTran.setIsoFlag(CONSTANTS.YES);

        PartTran crPartTran = new PartTran();
        crPartTran.setPartTranType(CONSTANTS.CREDITSTRING);
        crPartTran.setTransactionAmount(totalAmount);
        crPartTran.setAcid(crAc);
        crPartTran.setCurrency(loanCurrency);
        crPartTran.setExchangeRate("");
        crPartTran.setTransactionDate(new Date());
        crPartTran.setTransactionParticulars(transDesc);
        crPartTran.setIsoFlag(CONSTANTS.YES);

        List<PartTran> partTranList =new ArrayList<>();
        partTranList.add(drPartTran);
        partTranList.add(crPartTran);

        transactionHeader.setPartTrans(partTranList);
        return transactionHeader;
    }

    public EntityResponse chargesPartTrans(String accountToCharge, String chargeCode, Double amount) {
        System.out.println("collectWithdrawalCharges");
        System.out.println(chargeCode);
        EntityResponse response = new EntityResponse();
        List<ChargeCollectionReq> chargeCollectionReqs = new ArrayList<>();
        List<PartTran> partTrans = new ArrayList<>();
        System.out.println("After Get part trans");

            System.out.println("In if ");

                System.out.println("Looping");
//                PartTran partTran  = debits.get(i);
                System.out.println("After get debits");
                System.out.println("acid"+accountToCharge);
                Optional<AccountRepository.AcTransactionDetails> acDetails = accountRepository.getAccountTransactionDetails(accountToCharge);
                if (acDetails.get().getAccountType().equalsIgnoreCase(CONSTANTS.OFFICE_ACCOUNT)) {
                    log.info("Office account transaction error");
//                no charges to attach to office account
                    response.setMessage("You can not charge an office account!");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    System.out.println(response);
                } else {
                    log.info("Before charge collection");
                    ChargeCollectionReq chargeCollectionReq = new ChargeCollectionReq();
                    chargeCollectionReq.setDebitAc(accountToCharge);
//                    chargeCollectionReq.setParttranIdentity();
                    chargeCollectionReq.setTransactionAmount(amount);
                    chargeCollectionReq.setTransParticulars("");
                    chargeCollectionReq.setChargeCode(chargeCode);
                    chargeCollectionReqs.add(chargeCollectionReq);
                    response  =  serviceCaller.getWithdrawalCharges(chargeCollectionReqs);

                    if (response.getStatusCode() == HttpStatus.FOUND.value()) {
                        System.out.println("Charges found");
                        JSONObject obj = new JSONObject(response);
                        JSONArray ja = obj.getJSONArray("entity");
                        for (Object o : ja) {
                            JSONObject ob = new JSONObject(o.toString());
                            String acid = ob.get("acid").toString();
                            PartTran partTran1 = new PartTran();
                            AccountRepository.AcTransactionDetails acDetailsDb = accountRepository.getAccountTransactionDetails(acid).get();
                            partTran1.setPartTranType(ob.getString("partTranType"));
                            partTran1.setTransactionAmount(ob.getDouble("transactionAmount"));
                            partTran1.setAcid(acid);
                            partTran1.setParttranIdentity(ob.getString("parttranIdentity"));
                            partTran1.setCurrency(acDetailsDb.getCurrency());
                            partTran1.setTransactionParticulars(ob.getString("transactionParticulars"));
                            partTran1.setIsoFlag('N');
                            partTran1.setExchangeRate("1");
                            partTran1.setTransactionDate(new Date());
                            partTran1.setAccountBalance(acDetailsDb.getAccountBalance());
                            partTran1.setAccountType(acDetailsDb.getAccountType());
                            partTran1.setIsWelfare(false);
                            System.out.println(partTran1);
                            partTrans.add(partTran1);
                        }
                        response.setEntity(partTrans);
                        response.setStatusCode(HttpStatus.OK.value());
                    } else {
                        return response;
                    }

                    System.out.println("----------------------------");

                    System.out.println(response);

                    log.info("After charge collection");
                }

        System.out.println("out of caller 2");
        return response;
    }

    public TransactionHeader createTransactionHeader(String loanCurrency) {
        TransactionHeader transactionHeader = new TransactionHeader();
        transactionHeader.setTransactionType(CONSTANTS.SYSTEM_USERNAME);
        transactionHeader.setCurrency(loanCurrency);
        transactionHeader.setTransactionDate(new Date());
        transactionHeader.setEntityId(CONSTANTS.SYSTEM_ENTITY);
        transactionHeader.setTotalAmount(0.0);

        List<PartTran> partTranList =new ArrayList<>();

        transactionHeader.setPartTrans(partTranList);
        return transactionHeader;
    }

    public Double availableBalance(String acid) {
        Optional<AccountRepository.AcTransactionDetails> acDetails
                = accountRepository.getAccountTransactionDetails(acid);
        Double actualBalance = acDetails.get().getAccountBalance()
                - acDetails.get().getBookBalance() - accountRepository.getTotalLienAmount(acDetails.get().getAcid());
        return actualBalance;
    }

    public EntityResponse availableBalanceWithEntity(String acid) {
        EntityResponse response = new EntityResponse();
        response.setStatusCode(HttpStatus.OK.value());
        response.setEntity(availableBalance(acid));

        return response;
    }
}