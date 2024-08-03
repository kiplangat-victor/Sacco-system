
package com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader;

import com.emtechhouse.accounts.DTO.ChargeCollectionReq;
import com.emtechhouse.accounts.Models.Accounts.Account;
import com.emtechhouse.accounts.Models.Accounts.AccountRepository;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans.PartTran;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import com.emtechhouse.accounts.Utils.ServiceCaller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TransactionProcessing {
    @Value("${spring.application.fee.withdrawal_code}")
    private String withdrawal_code;
    @Value("${spring.application.fee.paybill_withdrawal_code}")
    private String paybill_withdrawal_code;
    @Value("${spring.application.fee.account_activation_code}")
    private String account_activation_code;
    @Value("${spring.application.fee.cheque_charge_code}")
    private String cheque_charge_code;
    @Value("${spring.application.paybill_account}")
    private String paybill_account;
    @Value("${spring.application.fee.money_transfer_code}")
    private String money_transfer_code;

    @Value("${spring.application.fee.balance_enquiry_code}")
    private String balance_enquiry_code;

    @Value("${spring.application.fee.sms_charges_code}")
    private String sms_charges_code;


    private final AccountRepository accountRepository;
    private final ServiceCaller serviceCaller;

   @Autowired
   NewTransactionService newTransactionService;

    public TransactionProcessing(AccountRepository accountRepository, ServiceCaller serviceCaller) {
        this.accountRepository = accountRepository;
        this.serviceCaller = serviceCaller;
    }
    //    TODO: Validate Parttrans
    public EntityResponse validateParttrans(TransactionHeader transactionHeader) {
        try {
            EntityResponse response = new EntityResponse();
            response.setMessage("Valid Transaction");
            response.setStatusCode(HttpStatus.OK.value());

            if (transactionHeader.getTransactionType() == null || transactionHeader.getTransactionType().isEmpty() || transactionHeader.getTransactionType().trim().isEmpty()){
                response.setMessage("Transaction Type can not be null or empty");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            }
            else if (transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.TRANSFER)) {
                if (transactionHeader.getPartTrans().size()<2) {
                    response.setMessage("Invalid transaction min two account has to be affected for transfer i.e Debit and Credit!");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                }else {
//                    check if any office account involved
                    List<PartTran> partTrans = transactionHeader.getPartTrans();
                    for (int i=0; i<partTrans.size(); i++) {
                        Optional<AccountRepository.AcTransactionDetails> checkAccount = accountRepository.getAccountTransactionDetails(partTrans.get(i).getAcid());
                        if (checkAccount.isPresent()){
                            if (checkAccount.get().getAccountType().equalsIgnoreCase(CONSTANTS.OFFICE_ACCOUNT)){
                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                response.setMessage("Invalid transaction! You can not use office account i.e "+partTrans.get(i).getAcid()+ " Only customer's account should be involved in normal transfer. Hint: Consult Senior Teller or manager for transaction on office accounts");
                            }
                        }
                    }
                    response = response;
                }
            }
            else if (transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.BATCH_UPLOAD)){
                if (transactionHeader.getPartTrans().size()<2){
                    response.setMessage("Invalid transaction min two account has to be affected for transfer i.e Debit and Credit!");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                }else {
//                    successful
                }
            }
            else if (transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.POST_EXPENSE)
                    || transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.POST_OFFICE_JOURNALS)
            ) {
                if (transactionHeader.getPartTrans().size()<2){
                    response.setMessage("Invalid transaction min two account has to be affected for transfer i.e Debit and Credit!");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                }else {
//                    check if any office account involved
                    List<PartTran> partTrans = transactionHeader.getPartTrans();
                    for (int i=0; i<partTrans.size(); i++){
                        Optional<AccountRepository.AcTransactionDetails> checkAccount = accountRepository.getAccountTransactionDetails(partTrans.get(i).getAcid());
                        if (checkAccount.isPresent()){
                            if (!checkAccount.get().getAccountType().equalsIgnoreCase(CONSTANTS.OFFICE_ACCOUNT)){
                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                response.setMessage("Invalid transaction! You can not use customer account i.e "+partTrans.get(i).getAcid()+ " Only office account should be involved in " + transactionHeader.getTransactionType()+ ". Hint: Consult Senior Teller or manager for transaction on office accounts");
                            }
                        }
                    }
                    response = response;
                }
            }
            else if(transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.CASH_WITHDRAWAL)) {
                if (transactionHeader.getTellerAccount() == null || transactionHeader.getTellerAccount().isEmpty() || transactionHeader.getTellerAccount().trim().isEmpty()){
                    response.setMessage("Invalid transaction! You must have a teller office account to do a cash-withdrawal transaction");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                }else {
                    if (transactionHeader.getPartTrans().size()>1){
                        response.setMessage("Invalid transaction! You can not withdraw from more than one account at once! hint: need only single parttran");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    }else {
                        if (transactionHeader.getPartTrans().get(0).getPartTranType().equalsIgnoreCase(CONSTANTS.Credit)){
                            response.setMessage("Invalid transaction! You can not credit a customer account when doing a withdraw transaction");
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        }else {
                            Optional<AccountRepository.AcTransactionDetails> tellerAc = accountRepository.getAccountTransactionDetails(transactionHeader.getTellerAccount());
                            if (tellerAc.isPresent()){
                                Double accountBalance = 0 - tellerAc.get().getAccountBalance();
                                if(accountBalance<transactionHeader.getPartTrans().get(0).getTransactionAmount()){
                                    response.setMessage("You've insufficient cash to perform this transaction. You available balance is " +accountBalance+ ".Hint: Consult your super teller");
                                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                }else {
                                    response = response;
                                }
                            }else {
                                response.setMessage("Teller account not found");
                                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                            }
                        }
                    }
                }
            }
            else if(transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.PAYBILL_WITHDRAWAL)) {
                {
                    if (transactionHeader.getPartTrans().size()>1){
                        response.setMessage("Invalid transaction! You can not withdraw from more than one account at once! hint: need only single parttran");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    }else {
                        if (transactionHeader.getPartTrans().get(0).getPartTranType().equalsIgnoreCase(CONSTANTS.Credit)) {
                            response.setMessage("Invalid transaction! You can not credit a customer account when doing a withdraw transaction");
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        }else {
                            Optional<AccountRepository.AcTransactionDetails> tellerAc = accountRepository.getAccountTransactionDetails(paybill_account);
                            if (tellerAc.isPresent()){
                                Double accountBalance = 0 - tellerAc.get().getAccountBalance();
                                if(accountBalance<transactionHeader.getPartTrans().get(0).getTransactionAmount()) {
                                    response.setMessage("Paybill account have insufficient cash to perform this transaction. You available balance is " +accountBalance+ ".Hint: Consult your super teller");
                                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                }else {
                                    response = response;
                                }
                            }else {
                                response.setMessage("Teller account not found");
                                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                            }
                        }
                    }
                }
            }
            else if(transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.PETTY_CASH)){
                if (transactionHeader.getTellerAccount() == null || transactionHeader.getTellerAccount().isEmpty() || transactionHeader.getTellerAccount().trim().isEmpty()){
                    response.setMessage("Invalid Transaction! You must have a TELLER office account to do a "+transactionHeader.getTransactionType()+" Transaction");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                }else {
                    if (transactionHeader.getPartTrans().size()<1){
                        response.setMessage("Invalid Transaction! You can not do a "+transactionHeader.getTransactionType()+" On Less than one account at once! hint: need only single Part Tran");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    }else {
                        if (transactionHeader.getPartTrans().get(0).getPartTranType().equalsIgnoreCase(CONSTANTS.Credit)){
                            response.setMessage("Invalid Transaction! You can not credit a petty cash account when doing a "+transactionHeader.getTransactionType()+" transaction");
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        }else {
                            Optional<AccountRepository.AcTransactionDetails> tellerAc = accountRepository.getAccountTransactionDetails(transactionHeader.getTellerAccount());
                            if (tellerAc.isPresent()){
                                Double accountBalance = 0 - tellerAc.get().getAccountBalance();
                                if(accountBalance<transactionHeader.getPartTrans().get(0).getTransactionAmount()){
                                    response.setMessage("You've INSUFFICIENT cash to Perform this Transaction. You available Balance is " +accountBalance+ ".Hint: Consult your super teller");
                                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                }else {
//                                    must be an office account
                                    for (int i = 0; i<transactionHeader.getPartTrans().size(); i++){
                                        PartTran partTran = transactionHeader.getPartTrans().get(i);
                                        Optional<AccountRepository.AcTransactionDetails> accCheck = accountRepository.getAccountTransactionDetails(partTran.getAcid());
                                        if (accCheck.isPresent()){
                                            if (accCheck.get().getAccountType().equalsIgnoreCase(CONSTANTS.OFFICE_ACCOUNT)){
                                                if (accCheck.get().getTellerAccount() == true){
                                                    response = response;
                                                }else if (partTran.getPartTranType().equalsIgnoreCase("Credit")) {
                                                    response.setMessage("Account involved in "+transactionHeader.getTransactionType()+" must be an Office Teller a/c. Check this account state " +partTran.getAcid());
                                                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                                }
                                            }else {
                                                response.setMessage("Account provided "+partTran.getAcid()+" is Not an office account. Hint: When doing "+transactionHeader.getTransactionType()+ " Petty Cash account normally is an office account.");
                                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                            }
                                        }else {
                                            response.setMessage("Account Not Found");
                                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                        }
                                    }
                                }
                            }else {
                                response.setMessage("Teller account not found");
                                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                            }
                        }
                    }
                }
            }
            else if (transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.CASH_DEPOSIT)){
                if (transactionHeader.getTellerAccount() == null || transactionHeader.getTellerAccount().isEmpty() || transactionHeader.getTellerAccount().trim().isEmpty()){
                    response.setMessage("Invalid transaction! You must have a teller office account to do a cash-deposit transaction");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                }else {
                    if (transactionHeader.getPartTrans().size()>1){
                        response.setMessage("Invalid transaction! You can not Deposit to more than one account at once! hint: need only single parttran");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    }else {
                        if (transactionHeader.getPartTrans().get(0).getPartTranType().equalsIgnoreCase(CONSTANTS.Debit)){
                            response.setMessage("Invalid transaction! You can not Debit a customer account when doing a cash deposit transaction");
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        }else {
                            response = response;
                        }
                    }
                }
            }
            else if (transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.FUND_TELLER)
                    || transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.COLLECT_TELLER_FUND)
                    || transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.PETTY_CASH)) {
                if (transactionHeader.getPartTrans().size()>1 || transactionHeader.getPartTrans().size()<1) {
                    response.setMessage("Invalid transaction! You must have a single partran to do a "+transactionHeader.getTransactionType()+"! hint: need only one partrans for transfer");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                }else {
                    for (int i = 0; i<transactionHeader.getPartTrans().size(); i++){
                        PartTran partTran = transactionHeader.getPartTrans().get(i);
                        Optional<AccountRepository.AcTransactionDetails> accCheck = accountRepository.getAccountTransactionDetails(partTran.getAcid());
                        if (accCheck.isPresent()){
                            if (accCheck.get().getAccountType().equalsIgnoreCase(CONSTANTS.OFFICE_ACCOUNT)) {
                                if (accCheck.get().getTellerAccount() == true) {
                                    if(transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.FUND_TELLER) && transactionHeader.getPartTrans().get(0).getPartTranType().equalsIgnoreCase(CONSTANTS.Credit)){
                                        response.setMessage("Invalid transaction! You can not Credit a Teller account when doing a FUND TELLER transaction");
                                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                    }
                                    else if (transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.COLLECT_TELLER_FUND) && transactionHeader.getPartTrans().get(0).getPartTranType().equalsIgnoreCase(CONSTANTS.Debit)){
                                        response.setMessage("Invalid transaction! You can not Debit a Teller account when doing a COLLECT TELLER FUND transaction");
                                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                    }else {
                                        response = response;
                                    }

                                }else {
                                    response.setMessage("Account involved in "+transactionHeader.getTransactionType()+" must be an Office Teller a/c. Check this account state " +partTran.getAcid());
                                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                }
                            }else {
                                response.setMessage("Account Not an office account");
                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            }
                        }else {
                            response.setMessage("Account Not Found");
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        }
                    }
                }
            }
            else if (transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.AGENCY_NET_WITHDRAWAL)
                    || transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.AGENCY_NET_DEPOSIT)) {
                System.out.println("Inside for agency transaction validation");
                if (transactionHeader.getPartTrans().size() != 1) {
                    response.setMessage("Invalid transaction! You must have a single partran to do a "+transactionHeader.getTransactionType()+"! hint: need only one partrans for transfer");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                }else {
                    for (int i = 0; i<transactionHeader.getPartTrans().size(); i++) {
                        PartTran partTran = transactionHeader.getPartTrans().get(i);
                        Optional<AccountRepository.AcTransactionDetails> accCheck = accountRepository.getAccountTransactionDetails(partTran.getAcid());
                        if (accCheck.isPresent()){
                            if (accCheck.get().getAccountType().equalsIgnoreCase(CONSTANTS.OFFICE_ACCOUNT)) {
                                System.out.println("Hello final");
                                System.out.println(transactionHeader.getTransactionType());
                                System.out.println(transactionHeader.getPartTrans().get(0).getPartTranType());

                                if (accCheck.get().getAgencyAccount() == true) {
                                    if(transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.AGENCY_NET_WITHDRAWAL) && transactionHeader.getPartTrans().get(0).getPartTranType().equalsIgnoreCase(CONSTANTS.Debit)){
                                        response.setMessage("Invalid transaction! You can not Credit a Agency account when doing a FUND TELLER transaction");
                                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                    }
                                    else if (transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.AGENCY_NET_DEPOSIT) && transactionHeader.getPartTrans().get(0).getPartTranType().equalsIgnoreCase(CONSTANTS.Credit)){
                                        response.setMessage("Invalid transaction! You can not Debit a Agency account when doing a COLLECT TELLER FUND transaction");
                                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                    } else {
                                        response = response;
                                    }

                                }else {
                                    response.setMessage("Account involved in "+transactionHeader.getTransactionType()+" must be an Office Agency a/c. Check this account state " +partTran.getAcid());
                                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                }
                            } else {
                                response.setMessage("Account Not an office account");
                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            }
                        }else {
                            response.setMessage("Account Not Found");
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        }
                    }
                }
            }
            else if (transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.RECONCILE_ACCOUNTS) || transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.CHEQUE_CLEARENCE) || transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.CHEQUE_BOUNCE)){
                if (transactionHeader.getPartTrans().size()<2){
                    response.setMessage("Invalid transaction min two account has to be affected for transfer i.e Debit and Credit!");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                }else {
                    response = response;
                }
            }
            else if (transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.REVERSE_TRANSACTIONS)) {
                if (transactionHeader.getTransactionCode().isEmpty()) {
                    response.setMessage("Invalid transaction! You must have a transaction code for previouse transaction to reverse!");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                } else {
                    response = response;
                }
            }
            else if (transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.SALARY_UPLOAD)) {
                log.info("Validating salary upload :: ");
                log.info("transactioncode :: "+transactionHeader.getTransactionCode());
                if (transactionHeader.getTransactionCode().isEmpty()){
                    log.info("Transaction code is empty");
                    response.setMessage("Invalid transaction! You must have a transaction code for previouse transaction to reverse!");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                } else {
                    log.info("Ok");
                    response = response;
                }
            }
            else {
                response.setMessage("Invalid transaction!");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            }
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public TransactionHeader attachTellerPartrans(TransactionHeader transactionHeader,
                                                  Optional<AccountRepository.AcTransactionDetails> tellerAc, String tranType) {
        List<PartTran> partTrans = new ArrayList<>();
//                Building Debit Parttran
        List<PartTran> inputparttran = transactionHeader.getPartTrans();
//                call charges collection
        AtomicReference<Double> sumCredits = new AtomicReference<>(0.0);
        AtomicReference<Double> sumDebits = new AtomicReference<>(0.0);
        log.info("transaction balance checking in progress ...");

        List<PartTran> debits = inputparttran.stream().filter(d->d.getPartTranType().equalsIgnoreCase(CONSTANTS.Debit)).collect(Collectors.toList());
        Double debitsAmount  = debits.stream().collect(Collectors.summingDouble(d->d.getTransactionAmount()));

        List<PartTran> credits = inputparttran.stream().filter(d->d.getPartTranType().equalsIgnoreCase(CONSTANTS.Credit)).collect(Collectors.toList());
        Double creditsAmount  = credits.stream().collect(Collectors.summingDouble(d->d.getTransactionAmount()));
//                Double creditToTeller = debitsAmount - creditsAmount;
        PartTran partTran = new PartTran();
        partTran.setAcid(tellerAc.get().getAcid());
        partTran.setPartTranType(tranType);
        partTran.setParttranIdentity(CONSTANTS.Normal);
        partTran.setTransactionAmount(inputparttran.get(0).getTransactionAmount());
        partTran.setTransactionCode(transactionHeader.getTransactionCode());
        partTran.setCurrency(tellerAc.get().getCurrency());
        partTran.setExchangeRate(inputparttran.get(0).getExchangeRate());
        partTran.setTransactionDate(inputparttran.get(0).getTransactionDate());
        partTran.setTransactionParticulars("Cr.Teller a/c for "+transactionHeader.getTransactionType());
        partTran.setIsoFlag('Y');
        partTran.setAccountBalance(tellerAc.get().getAccountBalance());
        partTran.setAccountType(tellerAc.get().getAccountType());
        partTran.setBatchCode(inputparttran.get(0).getBatchCode());
        partTran.setIsWelfare(inputparttran.get(0).getIsWelfare());
        partTran.setWelfareCode(inputparttran.get(0).getWelfareCode());
        partTran.setWelfareAction(inputparttran.get(0).getWelfareAction());
        partTran.setWelfareMemberCode(inputparttran.get(0).getWelfareMemberCode());
        partTrans.add(partTran);
        partTrans.addAll(inputparttran);
        transactionHeader.setPartTrans(partTrans);
        return transactionHeader;
    }


    //    TODO: Attach teller parttran a/c
    public TransactionHeader attachTellerPartrans(TransactionHeader transactionHeader) {
        try {
            log.info("Checking the transaction type inorder to attach teller partrans for "+transactionHeader.getTransactionType()+" teller account is :"+transactionHeader.getTellerAccount());
            TransactionHeader transactionHeaderData = new TransactionHeader();
            if (
                    transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.TRANSFER) ||
                            transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.RECONCILE_ACCOUNTS) ||
                            transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.CHEQUE_CLEARENCE) ||
                            transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.CHEQUE_BOUNCE) ||
                            transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.POST_EXPENSE) ||
                            transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.POST_OFFICE_JOURNALS)
            ) {
                transactionHeaderData = transactionHeader;
            } else if (
                    transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.CASH_DEPOSIT) ||
                            transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.FUND_TELLER) ||
                            transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.CASH_WITHDRAWAL) ||
                            transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.COLLECT_TELLER_FUND) ||
                            transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.AGENCY_NET_WITHDRAWAL) ||
                            transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.AGENCY_NET_DEPOSIT) ||
                            transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.PETTY_CASH)
            ) {
                System.out.println("Teller account: "+transactionHeader.getTellerAccount());
                Optional<AccountRepository.AcTransactionDetails> tellerAc = accountRepository.getAccountTransactionDetails(transactionHeader.getTellerAccount());
//                List<PartTran> partTrans = new ArrayList<>();
//                Building Debit Parttran
                System.out.println("Teller account response: "+tellerAc.get());
                PartTran partTran = new PartTran();
                PartTran inputparttran = transactionHeader.getPartTrans().get(0);

                partTran.setAcid(transactionHeader.getTellerAccount());
                if (transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.CASH_WITHDRAWAL)
                        || transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.FUND_TELLER)
                        || transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.PETTY_CASH)) {
                    partTran.setPartTranType(CONSTANTS.Credit);
                    System.out.println("Part_tran is: "+partTran);
                }
                else if (transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.AGENCY_NET_DEPOSIT)) {
                    partTran.setPartTranType(CONSTANTS.Debit);
                    System.out.println("Part_tran is: "+partTran);
                }
                else if (transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.AGENCY_NET_WITHDRAWAL)) {
                    partTran.setPartTranType(CONSTANTS.Credit);
                    System.out.println("Part_tran is: "+partTran);
                }
                else {
                    partTran.setPartTranType(CONSTANTS.Debit);
                }

                partTran.setTransactionAmount(inputparttran.getTransactionAmount());
                partTran.setTransactionCode(transactionHeader.getTransactionCode());
                partTran.setCurrency(tellerAc.get().getCurrency());
                partTran.setExchangeRate(inputparttran.getExchangeRate());
                partTran.setTransactionDate(inputparttran.getTransactionDate());
                partTran.setTransactionParticulars(inputparttran.getTransactionParticulars());
                partTran.setIsoFlag('Y');
                partTran.setAccountBalance(tellerAc.get().getAccountBalance());
                partTran.setAccountType("OAB");
                partTran.setBatchCode(inputparttran.getBatchCode());
                partTran.setIsWelfare(inputparttran.getIsWelfare());
                partTran.setWelfareCode(inputparttran.getWelfareCode());
                partTran.setWelfareAction(inputparttran.getWelfareAction());
                partTran.setWelfareMemberCode(inputparttran.getWelfareMemberCode());
//                partTrans.add(partTran);
//                partTrans.add(inputparttran);
                transactionHeader.getPartTrans().add(partTran);
                transactionHeaderData = transactionHeader;
            }
//            else if(transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.CASH_WITHDRAWAL)
//            ) {
//                Optional<AccountRepository.AcTransactionDetails> tellerAc = accountRepository.getAccountTransactionDetails(transactionHeader.getTellerAccount());
//
//                transactionHeaderData = attachTellerPartrans(transactionHeader, tellerAc, CONSTANTS.Credit);
//            }
            else if(transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.PAYBILL_WITHDRAWAL)
            ){
                System.out.println("Paybill account "+paybill_account);
                Optional<AccountRepository.AcTransactionDetails> tellerAc = accountRepository.getAccountTransactionDetails(paybill_account);

                transactionHeaderData = attachTellerPartrans(transactionHeader, tellerAc, CONSTANTS.Credit);

//                System.out.println(transactionHeaderData);
            }

            else {
                transactionHeaderData = transactionHeader;
            }
            return transactionHeaderData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    //    TODO: Attach currencies to all a/c
    public List<PartTran> attachCurrencies(List<PartTran> partTrans){
        try {
            List<PartTran> partTranList = new ArrayList<>();
            for (int i=0; i<partTrans.size(); i++){
                String currency = "Ksh";
                PartTran p = partTrans.get(i);
                Optional<AccountRepository.Ccy> currencyData = accountRepository.getCurrencyByAcid(p.getAcid());
                if (currencyData.isPresent()){
                    currency = currencyData.get().getCurrency();
                }
                p.setCurrency(currency);
                if (p.getTransactionAmount() !=0 ){
                    partTranList.add(p);
                }
            }
            return partTranList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public EntityResponse collectWithdrawalCharges(TransactionHeader transactionHeader) {
        System.out.println("Collecting withdrawal charge");
        return collectWithdrawalCharges(transactionHeader, withdrawal_code);
    }

    public EntityResponse collectMoneyTransferCharges(TransactionHeader transactionHeader) {
        System.out.println("Collecting money transfer charge");
        return collectMoneyTransferCharges(transactionHeader, money_transfer_code);
    }

    public EntityResponse collectAccountActivationCharges(TransactionHeader transactionHeader) {
        System.out.println("Collecting account activation charge");
        return collectAccountActivationCharge(transactionHeader, account_activation_code);
    }

    public EntityResponse collectBalanceEnquiryCharges(TransactionHeader transactionHeader) {
        System.out.println("Collecting balance Enquiry charge");
        return collectBalanceEnquiryCharges(transactionHeader, balance_enquiry_code);
    }

    public EntityResponse collectSMSCharges(TransactionHeader transactionHeader) {
        System.out.println("Collecting SMS Enquiry charge");
        return collectSMSCharges(transactionHeader, sms_charges_code);
    }


    public EntityResponse collectPaybillWithdrawalCharges(TransactionHeader transactionHeader) {
        System.out.println("Collect paybill charges");
        return collectWithdrawalCharges(transactionHeader, paybill_withdrawal_code);
    }

    public EntityResponse collectWithdrawalCharges(TransactionHeader transactionHeader, String chargeCode) {
        System.out.println("collectWithdrawalCharges");
        System.out.println(chargeCode);
        EntityResponse response = new EntityResponse();
        List<ChargeCollectionReq> chargeCollectionReqs = new ArrayList<>();
        List<PartTran> partTrans = transactionHeader.getPartTrans();
        System.out.println("After Get part trans");
        if (transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.CASH_WITHDRAWAL)
                || transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.PAYBILL_WITHDRAWAL)
        ) {
            System.out.println("In if ");
            List<PartTran> debits = partTrans.stream().filter(d->d.getPartTranType().equalsIgnoreCase(CONSTANTS.Debit)).collect(Collectors.toList());
            for (int i=0; i<debits.size(); i++){
                System.out.println("Looping");
                PartTran partTran  = debits.get(i);
                System.out.println("After get debits");
                System.out.println("acid"+partTran.getAcid());
                Optional<AccountRepository.AcTransactionDetails> acDetails = accountRepository.getAccountTransactionDetails(partTran.getAcid());
                if (acDetails.get().getAccountType().equalsIgnoreCase(CONSTANTS.OFFICE_ACCOUNT)) {
                    log.info("Office account transaction error");
//                no charges to attach to office account
                    response.setMessage("You can not do a "+transactionHeader.getTransactionType()+" on an office account!");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    System.out.println(response);
                }else {
                    log.info("Before charge collection");
                    ChargeCollectionReq chargeCollectionReq = new ChargeCollectionReq();
                    chargeCollectionReq.setDebitAc(partTran.getAcid());
//                    chargeCollectionReq.setParttranIdentity();
                    chargeCollectionReq.setTransactionAmount(partTran.getTransactionAmount());
                    chargeCollectionReq.setTransParticulars(partTran.getTransactionParticulars());
                    chargeCollectionReq.setChargeCode(chargeCode);
                    chargeCollectionReqs.add(chargeCollectionReq);
                    response  =  serviceCaller.getWithdrawalCharges(chargeCollectionReqs);
                    System.out.println("----------------------------");

                    System.out.println(response);

                    log.info("After charge collection");
                }
            }
        }else {
            System.out.println("out of caller 1");
            return response;
        }
        System.out.println("out of caller 2");
        return response;
    }

    public EntityResponse collectAccountActivationCharge(TransactionHeader transactionHeader, String chargeCode) {
        System.out.println("collectActivationCharges");
        System.out.println(chargeCode);
        EntityResponse response = new EntityResponse();
        List<ChargeCollectionReq> chargeCollectionReqs = new ArrayList<>();
        List<PartTran> partTrans = transactionHeader.getPartTrans();
        System.out.println("After Get part trans");
        if (transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.ACCOUNT_ACTIVATION)
        ) {
            System.out.println("In if ");
            List<PartTran> debits = partTrans.stream().filter(d->d.getPartTranType().equalsIgnoreCase(CONSTANTS.Debit)).collect(Collectors.toList());
            for (int i=0; i<debits.size(); i++){
                System.out.println("Looping");
                PartTran partTran  = debits.get(i);
                System.out.println("After get debits");
                System.out.println("acid "+partTran.getAcid());
                Optional<AccountRepository.AcTransactionDetails> acDetails = accountRepository.getAccountTransactionDetails(partTran.getAcid());
                if (acDetails.get().getAccountType().equalsIgnoreCase(CONSTANTS.OFFICE_ACCOUNT)) {
                    log.info("Office account transaction error");
//                no charges to attach to office account
                    response.setMessage("You can not do a "+transactionHeader.getTransactionType()+" on an office account!");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    System.out.println(response);
                }else {
                    log.info("Before charge collection");
                    ChargeCollectionReq chargeCollectionReq = new ChargeCollectionReq();
                    chargeCollectionReq.setDebitAc(partTran.getAcid());
//                    chargeCollectionReq.setCurrency("KSH");
                    chargeCollectionReq.setTransactionAmount(partTran.getTransactionAmount());
                    chargeCollectionReq.setTransParticulars(partTran.getTransactionParticulars());
                    chargeCollectionReq.setChargeCode(chargeCode);
                    chargeCollectionReqs.add(chargeCollectionReq);
                    response  =  serviceCaller.getActivationCharges (chargeCollectionReqs);
                    System.out.println("----------------------------");

                    System.out.println(response);

                    log.info("After charge collection");
                }
            }
        }else {
            System.out.println("out of caller 1");
            return response;
        }
        System.out.println("out of caller 2");
        return response;
    }


    public EntityResponse collectMoneyTransferCharges(TransactionHeader transactionHeader, String chargeCode) {
        System.out.println("collect Money Transfer Charges");
        System.out.println(chargeCode);
        EntityResponse response = new EntityResponse();
        List<ChargeCollectionReq> chargeCollectionReqs = new ArrayList<>();
        List<PartTran> partTrans = transactionHeader.getPartTrans();
        System.out.println("After Get part trans");
        if (transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.TRANSFER)
        ) {
            System.out.println("In if ");
            List<PartTran> debits = partTrans.stream().filter(d->d.getPartTranType().equalsIgnoreCase(CONSTANTS.Debit)).collect(Collectors.toList());
            for (int i=0; i<debits.size(); i++){
                System.out.println("Looping");
                PartTran partTran  = debits.get(i);
                System.out.println("After get debits");
                System.out.println("acid "+partTran.getAcid());
                Optional<AccountRepository.AcTransactionDetails> acDetails = accountRepository.getAccountTransactionDetails(partTran.getAcid());
                if (acDetails.get().getAccountType().equalsIgnoreCase(CONSTANTS.OFFICE_ACCOUNT)) {
                    log.info("Office account transaction error");
//                no charges to attach to office account
                    response.setMessage("You can not do a "+transactionHeader.getTransactionType()+" on an office account!");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    System.out.println(response);
                }else {
                    log.info("Before charge collection");
                    ChargeCollectionReq chargeCollectionReq = new ChargeCollectionReq();
                    chargeCollectionReq.setDebitAc(partTran.getAcid());
//                    chargeCollectionReq.setCurrency("KSH");
                    chargeCollectionReq.setTransactionAmount(partTran.getTransactionAmount());
                    chargeCollectionReq.setTransParticulars(partTran.getTransactionParticulars());
                    chargeCollectionReq.setChargeCode(chargeCode);
                    chargeCollectionReqs.add(chargeCollectionReq);
                    response  =  serviceCaller.getMoneyTransferCharges (chargeCollectionReqs);
                    System.out.println("----------------------------");

                    System.out.println(response);

                    log.info("After charge collection");
                }
            }
        }else {
            System.out.println("out of caller 1");
            return response;
        }
        System.out.println("out of caller 2");
        return response;
    }



    public EntityResponse collectBalanceEnquiryCharges(TransactionHeader transactionHeader, String chargeCode) {
        System.out.println("collect Balance Enquiry Charges");
        System.out.println(chargeCode);
        EntityResponse response = new EntityResponse();
        List<ChargeCollectionReq> chargeCollectionReqs = new ArrayList<>();
        List<PartTran> partTrans = transactionHeader.getPartTrans();
        System.out.println("After Get part trans");
        if (transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.BALANCE_ENQUIRY)
        ) {
            System.out.println("In if ");
            List<PartTran> debits = partTrans.stream().filter(d->d.getPartTranType().equalsIgnoreCase(CONSTANTS.Debit)).collect(Collectors.toList());
            for (int i=0; i<debits.size(); i++){
                System.out.println("Looping");
                PartTran partTran  = debits.get(i);
                System.out.println("After get debits");
                System.out.println("acid "+partTran.getAcid());
                Optional<AccountRepository.AcTransactionDetails> acDetails = accountRepository.getAccountTransactionDetails(partTran.getAcid());
                if (acDetails.get().getAccountType().equalsIgnoreCase(CONSTANTS.OFFICE_ACCOUNT)) {
                    log.info("Office account transaction error");
//                no charges to attach to office account
                    response.setMessage("You can not do a "+transactionHeader.getTransactionType()+" on an office account!");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    System.out.println(response);
                }else {
                    log.info("Before charge collection");
                    ChargeCollectionReq chargeCollectionReq = new ChargeCollectionReq();
                    chargeCollectionReq.setDebitAc(partTran.getAcid());
//                    chargeCollectionReq.setCurrency("KSH");
                    chargeCollectionReq.setTransactionAmount(partTran.getTransactionAmount());
                    chargeCollectionReq.setTransParticulars(partTran.getTransactionParticulars());
                    chargeCollectionReq.setChargeCode(chargeCode);
                    chargeCollectionReqs.add(chargeCollectionReq);
                    response  =  serviceCaller.getBalanceEnquiryCharges (chargeCollectionReqs);
                    System.out.println("-------------Headed to get charges---------------");

                    System.out.println(response);

                    log.info("After charge collection");
                }
            }
        }else {
            System.out.println("out of caller 1");
            return response;
        }
        System.out.println("out of caller 2");
        return response;
    }


    public EntityResponse collectSMSCharges(TransactionHeader transactionHeader, String chargeCode) {
        System.out.println("collect SMS Enquiry Charges");
        System.out.println(chargeCode);
        EntityResponse response = new EntityResponse();
        List<ChargeCollectionReq> chargeCollectionReqs = new ArrayList<>();
        List<PartTran> partTrans = transactionHeader.getPartTrans();
        System.out.println("After Get part trans");
        if (transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.SMS_CHARGES)
        ) {
            System.out.println("In if ");
            List<PartTran> debits = partTrans.stream().filter(d->d.getPartTranType().equalsIgnoreCase(CONSTANTS.Debit)).collect(Collectors.toList());
            for (int i=0; i<debits.size(); i++){
                System.out.println("Looping");
                PartTran partTran  = debits.get(i);
                System.out.println("After get debits");
                System.out.println("acid "+partTran.getAcid());
                Optional<AccountRepository.AcTransactionDetails> acDetails = accountRepository.getAccountTransactionDetails(partTran.getAcid());
                if (acDetails.get().getAccountType().equalsIgnoreCase(CONSTANTS.OFFICE_ACCOUNT)) {
                    log.info("Office account transaction error");
//                no charges to attach to office account
                    response.setMessage("You can not do a "+transactionHeader.getTransactionType()+" on an office account!");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    System.out.println(response);
                }else {
                    log.info("Before charge collection");
                    ChargeCollectionReq chargeCollectionReq = new ChargeCollectionReq();
                    chargeCollectionReq.setDebitAc(partTran.getAcid());
                    chargeCollectionReq.setTransactionAmount(partTran.getTransactionAmount());
                    chargeCollectionReq.setTransParticulars(partTran.getTransactionParticulars());
                    chargeCollectionReq.setChargeCode(chargeCode);
                    chargeCollectionReqs.add(chargeCollectionReq);
                    response  =  serviceCaller.getSMSCharges (chargeCollectionReqs);
                    System.out.println("-------------Headed to get charges---------------");

                    System.out.println(response);

                    log.info("After charge collection");
                }
            }
        }else {
            System.out.println("out of caller 1");
            return response;
        }
        System.out.println("out of caller 2");
        return response;
    }




    public EntityResponse collectChequeCharges(TransactionHeader transactionHeader){
        EntityResponse response = new EntityResponse();
        List<ChargeCollectionReq> chargeCollectionReqs = new ArrayList<>();
        List<PartTran> partTrans = transactionHeader.getPartTrans();
        if (transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.CHEQUE_CLEARENCE) || transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.CHEQUE_BOUNCE)){
            List<PartTran> credits = partTrans.stream().filter(d->d.getPartTranType().equalsIgnoreCase(CONSTANTS.Credit)).collect(Collectors.toList());
            for (int i=0; i<credits.size(); i++){
                PartTran partTran  = credits.get(i);
                Optional<AccountRepository.AcTransactionDetails> acDetails = accountRepository.getAccountTransactionDetails(partTran.getAcid());
                if (acDetails.get().getAccountType().equalsIgnoreCase(CONSTANTS.OFFICE_ACCOUNT)){
//                no charges to attach to office account
                    response.setMessage("You can not do a "+transactionHeader.getTransactionType()+" on an office account!");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                }else {
                    ChargeCollectionReq chargeCollectionReq = new ChargeCollectionReq();
                    chargeCollectionReq.setDebitAc(partTran.getAcid());
                    chargeCollectionReq.setTransactionAmount(partTran.getTransactionAmount());
                    chargeCollectionReq.setTransParticulars(partTran.getTransactionParticulars());
                    System.out.println("Event ID: "+transactionHeader.getChargeEventId());
                    chargeCollectionReq.setChargeCode(transactionHeader.getChargeEventId());
                    chargeCollectionReqs.add(chargeCollectionReq);
                    response  =  serviceCaller.getWithdrawalCharges(chargeCollectionReqs);
                    System.out.println("----------------------------");
                    System.out.println(response);
                }
            }
        }else {
            return response;
        }
        return response;
    }


    public EntityResponse collectSalaryCharges(TransactionHeader transactionHeader,
                                               String chargeCode,
                                               String chargeAccount){

        log.info("Collecting charges");

        EntityResponse response = new EntityResponse();
        List<ChargeCollectionReq> chargeCollectionReqs = new ArrayList<>();
        List<PartTran> partTrans = transactionHeader.getPartTrans();
        if (transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.BATCH_UPLOAD) ||
                transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.SALARY_UPLOAD) ||
                transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.CHEQUE_BOUNCE)){
            List<PartTran> credits = partTrans.stream().filter(d->d.getPartTranType().equalsIgnoreCase(CONSTANTS.Credit)).collect(Collectors.toList());
            if(chargeAccount.equalsIgnoreCase(CONSTANTS.Debit)){
                credits = partTrans.stream().filter(d->d.getPartTranType().equalsIgnoreCase(CONSTANTS.Debit)).collect(Collectors.toList());
            }else {
                credits = partTrans.stream().filter(d->d.getPartTranType().equalsIgnoreCase(CONSTANTS.Credit)).collect(Collectors.toList());
            }
            for (int i=0; i<credits.size(); i++){
                log.info("loop one :: ");
                PartTran partTran  = credits.get(i);
                Optional<AccountRepository.AcTransactionDetails> acDetails = accountRepository.getAccountTransactionDetails(partTran.getAcid());
                if (acDetails.get().getAccountType().equalsIgnoreCase(CONSTANTS.OFFICE_ACCOUNT)){
//                no charges to attach to office account
                    response.setMessage("You can not do a "+transactionHeader.getTransactionType()+" on an office account!");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                } else {
                    ChargeCollectionReq chargeCollectionReq = new ChargeCollectionReq();
                    chargeCollectionReq.setDebitAc(partTran.getAcid());
                    chargeCollectionReq.setTransactionAmount(partTran.getTransactionAmount());
                    chargeCollectionReq.setTransParticulars(partTran.getTransactionParticulars());
                    chargeCollectionReq.setChargeCode(chargeCode);
                    chargeCollectionReqs.add(chargeCollectionReq);
                    response  =  serviceCaller.getWithdrawalCharges(chargeCollectionReqs);
                    System.out.println("----------------------------");
                    System.out.println(response);
                }
            }
        }else {
            return response;
        }
        return response;
    }
    //    TODO: Attach currencies to all a/c
    public void updateAccountBalances(List<PartTran> partTrans){
        try {
            List<PartTran> debits = partTrans.stream().filter(d->d.getPartTranType().equalsIgnoreCase(CONSTANTS.Debit)).collect(Collectors.toList());
            Map<String, Double> minifiedDebits  = debits.stream().collect(Collectors.groupingBy(d->d.getAcid(),Collectors.summingDouble(d->d.getTransactionAmount())));
            for (String key : minifiedDebits.keySet()) {
                Double debitBal = minifiedDebits.get(key);
                String acid = key;
                Optional<Account> account = accountRepository.findByAcid(acid);
                if (account.isPresent()){
                    Double balance = account.get().getAccountBalance()  - debitBal;
                    account.get().setAccountBalance(balance);
                }
            }
            List<PartTran> credits = partTrans.stream().filter(d->d.getPartTranType().equalsIgnoreCase(CONSTANTS.Credit)).collect(Collectors.toList());
            Map<String, Double> minifiedCredits  = credits.stream().collect(Collectors.groupingBy(d->d.getAcid(),Collectors.summingDouble(d->d.getTransactionAmount())));
            for (String key : minifiedCredits.keySet()) {
                Double creditBal = minifiedCredits.get(key);
                String acid = key;
                Optional<Account> account = accountRepository.findByAcid(acid);
                if (account.isPresent()){
                    Double balance = account.get().getAccountBalance()  + creditBal;
                    account.get().setAccountBalance(balance);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public EntityResponse isTransactionWithinTellerLimits(TransactionHeader transactionHeader) {
        try {
            EntityResponse response = new EntityResponse();
            response.setStatusCode(HttpStatus.OK.value());
            response.setMessage("Transaction can be verified");
            Double transactionAmount = transactionHeader.getPartTrans().get(0).getTransactionAmount();
            if (
                    transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.TRANSFER) ||
                            transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.BATCH_UPLOAD) ||
                            transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.FUND_TELLER) ||
                            transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.COLLECT_TELLER_FUND) ||
                            transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.RECONCILE_ACCOUNTS) ||
                            transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.POST_OFFICE_JOURNALS) ||
                            transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.POST_EXPENSE) ||
                            transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.REVERSE_TRANSACTIONS) ||
                            transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.CHEQUE_CLEARENCE) ||
                            transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.CHEQUE_BOUNCE)

//            CHEQUE CLEARENCE
            ){
                response.setMessage("Transfer Transaction");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            }else {

                Optional<AccountRepository.OfficeAccountLimits> check = accountRepository.getOfficeAccountLimitsByAcid(transactionHeader.getTellerAccount());
                Double CashCreditLImit = check.get().getCashCreditLImit();
                Double CashLimitDr = check.get().getCashLimitDr();
                if (check.isPresent()){
                    if (transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.CASH_WITHDRAWAL)
                            || transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.AGENCY_NET_WITHDRAWAL)
                            || transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.PAYBILL_WITHDRAWAL)
                    ) {
                        if (transactionAmount<=CashLimitDr){
                        }else {
                            response.setMessage("Transaction amount is beyond your account transaction limits. Kindly request the responsible senior teller or manager to verify the transaction.Transaction code " +transactionHeader.getTransactionCode());
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        }
                    }
                    else if (transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.CASH_DEPOSIT)
                            || transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.AGENCY_NET_DEPOSIT)
                    ){
                        if (transactionAmount<=CashCreditLImit){
                        }else {
                            response.setMessage("Transaction amount is beyond your account transaction limits. Kindly request the responsible senior teller or manager to verify the transaction.Transaction code " +transactionHeader.getTransactionCode());
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        }
                    }else {
                        response.setMessage("The transaction policy does not allow you to verify a transfer transaction that you initiated " +transactionHeader.getTransactionCode());
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    }
                } else {
                    response = response;
                }
            }
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public EntityResponse staffTransactionRestriction(TransactionHeader transactionHeader) {
        System.out.println("staffTransactionRestriction");
        try {
            EntityResponse response = new EntityResponse();
            List<ValidationError> validationErrors = new ArrayList<>();
            if (transactionHeader.getStaffCustomerCode() == null
                    || transactionHeader.getStaffCustomerCode().isEmpty()) {
            } else {
//                check if either of a/c involved in parttrans belongs to the staff
//                findByCustomerCodeAndDeleteFlag(String customerCode, Character deleteFlag); getAccountTransactionDetails
                for (int i=0; i<transactionHeader.getPartTrans().size(); i++){
                    PartTran partTran = transactionHeader.getPartTrans().get(i);
                    Optional<AccountRepository.AcTransactionDetails> acTransactionDetails = accountRepository.getAccountTransactionDetails(partTran.getAcid());
                    if (acTransactionDetails.isPresent()){
                        if (acTransactionDetails.get().getCustomercode().equalsIgnoreCase(transactionHeader.getStaffCustomerCode())){
                            ValidationError validationError = new ValidationError();
                            validationError.setMessage("You can not do transaction on to your own account as a staff! Account No "+partTran.getAcid());
                            validationError.setValidationError(true);
                            validationErrors.add(validationError);
                        }
                    }
                }
            }
            //            Check if validation error has any error transaction should stop and return all the errors
            if (validationErrors.size()>0) {
                response.setMessage("Account Errors!");
                response.setEntity(validationErrors);
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            }else {
                response.setMessage("All accounts are valid");
                response.setStatusCode(HttpStatus.ACCEPTED.value());
            }
            log.info("--------All account are valid?--------" +response.getStatusCode());
            System.out.println(response);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    //TODO:Transaction Validation point
    public EntityResponse validateTransaction(TransactionHeader transactionHeader) {
        EntityResponse res = new EntityResponse<>();
        try {
            res = staffTransactionRestriction(transactionHeader);
            if (res.getStatusCode().equals(HttpStatus.ACCEPTED.value())){
                res = accountValidation(transactionHeader);
                if (res.getStatusCode().equals(HttpStatus.ACCEPTED.value())){
                    res = accountDebitBalanceValidation(transactionHeader);
                    if (res.getStatusCode().equals(HttpStatus.ACCEPTED.value())){
                        if((transactionHeader.getTransactionType() == "AGENCY_NET_DEPOSIT") || (transactionHeader.getTransactionType() == "AGENCY_NET_WITHDRAWAL")) {
                            res = res;
                        } else {
                            if (res.getStatusCode().equals(HttpStatus.ACCEPTED.value())){
                                res = res;
                            }else {
                                res = res;
                            }
                        }
                    }else {
                        res = res;
                    }
                }else {
                    res = res;
                }
            }else {
                res = res;
            }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            res.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            res.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return res;
        }
    }
    //TODO: Check if account is valid
    public EntityResponse accountValidation(TransactionHeader transactionHeader) {
        System.out.println("accountValidation in Processing");
        EntityResponse res = new EntityResponse<>();
        try {
            List<ValidationError> validationErrors = new ArrayList<>();
            List<PartTran> partTrans = transactionHeader.getPartTrans();
            for (int i = 0; i < partTrans.size(); i++) {
                System.out.println(partTrans.get(i).getAcid());
                Optional<AccountRepository.AcTransactionDetails> acDetails = accountRepository.getAccountTransactionDetails(partTrans.get(i).getAcid());
                if (acDetails.isPresent()) {
                    AccountRepository.AcTransactionDetails account = acDetails.get();

                    // Check if account is active (only for debit account)
                    if (partTrans.get(i).getPartTranType().equalsIgnoreCase(CONSTANTS.Debit) &&
                            !transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.SALARY_UPLOAD)
                            &&
                            !transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.CHEQUE_CLEARENCE)){
                        if (account.getAccountStatus().equalsIgnoreCase(CONSTANTS.ACTIVE)) {
                            // Additional checks for the debit account can be added here if needed
                        } else {
                            ValidationError validationError = new ValidationError();
                            validationError.setMessage("Debit account " + partTrans.get(i).getAcid() + " not active");
                            validationError.setValidationError(true);
                            validationErrors.add(validationError);
                            continue; // Skip the rest of the checks for inactive debit accounts
                        }
                    }

                    // CHECK IF ACCOUNT IS VERIFIED
                    if (account.getVerifiedFlag() == 'Y') {
                        // CHECK IF ACCOUNT IS LOAN ACCOUNT
                        if (account.getAccountType().equalsIgnoreCase(CONSTANTS.LOAN_ACCOUNT)
                                && !transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.RECONCILE_ACCOUNTS)) {
                            ValidationError validationError = new ValidationError();
                            validationError.setMessage("You cannot do a transaction on a loan account! Account No " + partTrans.get(i).getAcid() + " Hint: Loan accounts only receive transactions from system init transactions.");
                            validationError.setValidationError(true);
                            validationErrors.add(validationError);
                        } else {
                            // CHECK IF ACCOUNT HAS CURRENCY
                            if (account.getCurrency() != null) {
                                // Check if account allows debit
                                if (partTrans.get(i).getPartTranType().equalsIgnoreCase(CONSTANTS.Debit)) {
                                    // CHECK transaction if withdrawal, check debit if allowed withdrawal else proceed
                                    if (transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.CASH_WITHDRAWAL)) {
                                        // Check if account allows withdrawal
                                        if (account.getIsWithdrawalAllowed()) {
                                            // Additional checks for debit account can be added here if needed
                                        } else {
                                            ValidationError validationError = new ValidationError();
                                            validationError.setMessage("Account does not allow cash withdrawal " + partTrans.get(i).getAcid());
                                            validationError.setValidationError(true);
                                            validationErrors.add(validationError);
                                        }
                                    }
                                }
                            } else {
                                ValidationError validationError = new ValidationError();
                                validationError.setMessage("Account " + partTrans.get(i).getAcid() + " has no currency");
                                validationError.setValidationError(true);
                                validationErrors.add(validationError);
                            }
                        }
                    } else {
                        ValidationError validationError = new ValidationError();
                        validationError.setMessage("Account " + partTrans.get(i).getAcid() + " not verified");
                        validationError.setValidationError(true);
                        validationErrors.add(validationError);
                    }
                } else {
                    ValidationError validationError = new ValidationError();
                    validationError.setMessage("Account " + partTrans.get(i).getAcid() + " not found");
                    validationError.setValidationError(true);
                    validationErrors.add(validationError);
                }
            }

            // Check if validation error has any error; if so, stop and return all the errors
            if (validationErrors.size() > 0) {
                res.setMessage("Account Errors! " + joinValidationErrors(validationErrors));
                res.setEntity(validationErrors);
                res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            } else {
                res.setMessage("All accounts are valid");
                res.setStatusCode(HttpStatus.ACCEPTED.value());
            }

            log.info("--------All accounts are valid?--------" + res.getStatusCode());
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            res.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            res.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return res;
        }
    }

    private String joinValidationErrors(List<ValidationError> validationErrors) {
        String error = "";
        if (validationErrors != null) {
            for (ValidationError validationError: validationErrors) {
                error += validationError.getMessage();
            }
        }
        return error;
    }

    public EntityResponse accountValidator(String acid, String  tranType) {
        System.out.println(acid);
        EntityResponse res = new EntityResponse();
        try {
            List<ValidationError> validationErrors = new ArrayList<>();
            Optional<AccountRepository.AcTransactionDetails> acDetails = accountRepository.getAccountTransactionDetails(acid);
            if (acDetails.isPresent()){
                AccountRepository.AcTransactionDetails account = acDetails.get();
                System.out.println("Found "+account.getAcid());
                log.info("ACCOUNTS DETAILS ==>|| " + account);
//                    check if ac is active
//                if (account.getAccountStatus().equalsIgnoreCase(CONSTANTS.ACTIVE) ){cheque

//                      CHECK IF ACCOUNT IS VERIFIED
                    if (account.getVerifiedFlag() == 'Y'){
//                            CHECK IF ACCOUNT IS LOAN ACCOUNT LAA
                        if(account.getAccountType().equalsIgnoreCase(CONSTANTS.LOAN_ACCOUNT)
                                && !tranType.equalsIgnoreCase(CONSTANTS.RECONCILE_ACCOUNTS))  {
                            ValidationError validationError = new ValidationError();
                            validationError.setMessage("You can not do transaction on a loan account! Account No "+acid+" Hint: Loan accounts only receive transactions from system init transactions.");
                            validationError.setValidationError(true);
                            validationErrors.add(validationError);
                        }else {
//                        CHECK IF ACCOUNT HAS CURRENCY
                            if (account.getCurrency() != null){
                                log.info("----------------------Account Valid --------------------");
                            }else {
                                ValidationError validationError = new ValidationError();
                                validationError.setMessage("Account "+acid+" has no currency " );
                                validationError.setValidationError(true);
                                validationErrors.add(validationError);
                            }
                        }
                    }else {
                        ValidationError validationError = new ValidationError();
                        validationError.setMessage("Account "+acid+" not verified " );
                        validationError.setValidationError(true);
                        validationErrors.add(validationError);
                    }
//                }
//                else {
//                    ValidationError validationError = new ValidationError();
//                    validationError.setMessage("Account "+acid +" not active " );
//                    validationError.setValidationError(true);
//                    validationErrors.add(validationError);
//                }
            }else {
                ValidationError validationError = new ValidationError();
                validationError.setMessage("Account "+acid+" not found " );
                validationError.setValidationError(true);
                validationErrors.add(validationError);
            }
            //            Check if validation error has any error transaction should stop and return all the errors
            if (validationErrors.size()>0){
                res.setMessage("Account Errors!");
                res.setEntity(validationErrors);
                res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            }else {
                res.setMessage("All accounts are valid");
                res.setStatusCode(HttpStatus.ACCEPTED.value());
            }
            log.info("--------All account are valid?--------" +res.getStatusCode());
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            res.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            res.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return res;
        }
    }
    //TODO: Check available balance less lien , less charges;
    public EntityResponse accountDebitBalanceValidation(TransactionHeader transactionHeader) {
        System.out.println("accountDebitBalanceValidation");
        try{
            EntityResponse response = new EntityResponse();
            List<ValidationError> validationErrors = new ArrayList<>();
            List<PartTran> debits = transactionHeader.getPartTrans().stream().filter(d->d.getPartTranType().equalsIgnoreCase(CONSTANTS.Debit)).collect(Collectors.toList());
            List<PartTran> credits = transactionHeader.getPartTrans().stream().filter(d->d.getPartTranType().equalsIgnoreCase(CONSTANTS.Credit)).collect(Collectors.toList());
            Map<String, Double> minifiedDebits  = debits.stream().collect(Collectors.groupingBy(PartTran::getAcid,Collectors.summingDouble(d->d.getTransactionAmount())));
            Map<String, Double> minifiedCredits  = credits.stream().collect(Collectors.groupingBy(d->d.getAcid(),Collectors.summingDouble(d->d.getTransactionAmount())));
            for (String key : minifiedDebits.keySet()) {
                Double debitBal = minifiedDebits.get(key);
                String acid = key;
                Optional<AccountRepository.AcTransactionDetails> acDetails = accountRepository.getAccountTransactionDetails(acid);
                if (acDetails.get().getAccountType().equalsIgnoreCase(CONSTANTS.OFFICE_ACCOUNT) || acDetails.get().getAccountType().equalsIgnoreCase(CONSTANTS.LOAN_ACCOUNT)) {
                    if (transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.TRANSFER)) {
//                        check if debit account is a teller account
                        if(acDetails.get().getTellerAccount()){
                            ValidationError validationError = new ValidationError();
                            validationError.setMessage("Transfer transaction does not allow debit or credit a teller account. The teller account is " +acid+ ".Hint: You can login as a teller that owns that account and do the transaction");
                            validationError.setValidationError(true);
                            validationErrors.add(validationError);
                        }
                    } else if (transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.CASH_WITHDRAWAL)) {
                        Double accountBalance = 0 - acDetails.get().getAccountBalance();
                        if (accountBalance<debitBal) {
                            ValidationError validationError = new ValidationError();
                            validationError.setMessage("You've insufficient cash to perfom this transaction. You available balance is " +accountBalance+ ".Hint: Consult your super teller");
                            validationError.setValidationError(true);
                            validationErrors.add(validationError);
                        }
                    }
                    else {
                        response.setMessage("All accounts are valid");
                        response.setStatusCode(HttpStatus.ACCEPTED.value());
                    }
                } else {
                    if (acDetails.isPresent()) {
                        Double actualBalance = acDetails.get().getAccountBalance() - accountRepository.getTotalLienAmount(acDetails.get().getAcid())-acDetails.get().getBookBalance();
                        if (actualBalance<debitBal) {
                            if (minifiedCredits.containsKey(acDetails.get().getAcid())) {
                                System.out.println("Found in credit");
                                System.out.println(minifiedCredits.get(acDetails.get().getAcid()));
                                System.out.println(debitBal);
                                debitBal = debitBal-minifiedCredits.get(acDetails.get().getAcid());
                                if (debitBal > 0 && actualBalance<debitBal) {
                                    System.out.println("Insufficeint");
                                    System.out.println(minifiedCredits.get(acDetails.get().getAcid()));
                                    System.out.println(debitBal);
                                    ValidationError validationError = new ValidationError();
                                    validationError.setMessage("Insufficient account balance for account " +acid+ " actual balance is "+actualBalance);
                                    validationError.setValidationError(true);
                                    validationErrors.add(validationError);
                                    System.out.println(validationError);
                                }
                            }else{
                                System.out.println("Not found in credit");
                                ValidationError validationError = new ValidationError();
                                validationError.setMessage("Insufficient account balance for account " +acid+ " available balance is "+actualBalance);
                                validationError.setValidationError(true);
                                validationErrors.add(validationError);
                            }
                        }
                    }
                    else {
                        ValidationError validationError = new ValidationError();
                        validationError.setMessage("Account "+acid+" not found ");
                        validationError.setValidationError(true);
                        validationErrors.add(validationError);
                    }
                }
            }
            System.out.println("Errors here in validational?");
            if (validationErrors.size()<1) {
                response.setMessage("All accounts are valid");
                response.setStatusCode(HttpStatus.ACCEPTED.value());
            }else {
                response.setMessage("Account Errors!");
                response.setEntity(validationErrors);
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            }
            log.info("--------All account are valid yesssr--------" +response.getStatusCode());
            return response;
        }  catch (Exception e) {
            EntityResponse response = new EntityResponse();
            e.printStackTrace();
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return response;
        }
    }
    //TODO: Check if transaction is balanced

    public EntityResponse validateAccountActivationTransaction(TransactionHeader transactionHeader) {
        EntityResponse res = new EntityResponse<>();
        try {
            res = staffTransactionRestriction(transactionHeader);
            if (res.getStatusCode().equals(HttpStatus.ACCEPTED.value())){
                res = accountDebitBalanceValidation(transactionHeader);
                if (res.getStatusCode().equals(HttpStatus.ACCEPTED.value())){
                    res = isTransactionBalanced(transactionHeader.getPartTrans());
                    if (res.getStatusCode().equals(HttpStatus.ACCEPTED.value())){
                        res = res;
                    }else {
                        res = res;
                    }
                }else {
                    res = res;
                }

            }else {
                res = res;
            }
            return res;
        } catch (Exception e) {
            log.error(e.getMessage());
            res.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            res.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return res;
        }
    }
    public EntityResponse isTransactionBalanced(List<PartTran> partTrans) {
        try {
            EntityResponse response = new EntityResponse();
            Boolean balanced = false;
            AtomicReference<Double> sumCredits = new AtomicReference<>(0.0);
            AtomicReference<Double> sumDebits = new AtomicReference<>(0.0);
            log.info("transaction balance checking in progress ...");
            for (PartTran p : partTrans) {
                if (p.getPartTranType().equalsIgnoreCase(CONSTANTS.Credit)) {
                    sumCredits.updateAndGet(v -> v + p.getTransactionAmount());
                } else if (p.getPartTranType().equalsIgnoreCase(CONSTANTS.Debit)) {
                    sumDebits.updateAndGet(v -> v + p.getTransactionAmount());
                }
            }
            System.out.println("Sum of Credits " + sumCredits.get());
            System.out.println("Sum of Debits " + sumDebits.get());
            if (Double.compare(round(sumCredits.get()), round(sumDebits.get()))  == 0) {
                balanced = true;
            }else{
                balanced = false;
            }
            if (balanced){
                response.setMessage("Transaction Balanced");
                response.setStatusCode(HttpStatus.ACCEPTED.value());
            }else{
                response.setMessage("Transaction Not Balanced");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            }
            log.info("--------Transaction Balanced?--------" +balanced);
            return response;
        } catch (Exception e) {
            EntityResponse res = new EntityResponse<>();
            e.printStackTrace();
            res.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            res.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return res;
        }
    }

    public EntityResponse feeCharges(String acid){
        try {
            System.out.println("In Tran Processing 3 service to collect sms charges");
            EntityResponse res= new EntityResponse<>();

            newTransactionService.feeCharges(acid);

        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
        return null;
    }




    public double round(double a){
        return Math.round(a * 100.0) / 100.0;
    }
}