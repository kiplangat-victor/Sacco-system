package com.emtechhouse.accounts.Models.Accounts.AccountServices;

import com.emtechhouse.accounts.Models.Accounts.Account;
import com.emtechhouse.accounts.Models.Accounts.AccountDtos.AccountStatementDto;
import com.emtechhouse.accounts.Models.Accounts.AccountRepository;
import com.emtechhouse.accounts.Models.Accounts.Loans.Loan;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanRepository;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ProductFees.FeeDto;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ProductFees.ProductFeeAmount;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ProductFees.ProductFeesService;
import com.emtechhouse.accounts.Models.Accounts.TransactionDtos.OutgoingTransaction.OutgoingTransactionDetails;
import com.emtechhouse.accounts.Models.Accounts.TransactionDtos.RecievedTransactions.ReceivedTransactionHolder;
import com.emtechhouse.accounts.Models.Accounts.TransactionDtos.RecievedTransactions.RecievedTransactionDetails;
import com.emtechhouse.accounts.Models.Accounts.Validators.ValidatorService;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans.PartTran;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TransactionHeader;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TransactionsController;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@EnableTransactionManagement
public class AccountTransactionService {
    @Autowired
    private LoanRepository loanRepository;

    @Value("${POST_TRANSACTION}")
    String post_transaction_url;

    @Autowired
    private ValidatorService validatorsService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionsController transactionsController;
    @Autowired
    private ProductFeesService productFeesService;


    public EntityResponse<?> creditAccount(String acid, Double amount){
        try{
            EntityResponse acidValidator =validatorsService.acidValidator(acid, "Credit");
            if(acidValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                return acidValidator;
            }else {
                Account account = accountRepository.findByAcid(acid).orElse(null);
                //amount should go to overflow
                if(account.getAccountType().equals(CONSTANTS.LOAN_ACCOUNT)){
                    log.info("Crediting loan account");
                    Loan loan= account.getLoan();
                    Double loanOverFlowAmount= loan.getOverFlowAmount();
                    log.info("current overflow amt is :: "+loanOverFlowAmount);
                    log.info("transaction amount is :: "+amount);
                    loanRepository.updateLoanOverFlowAmount((loanOverFlowAmount+amount),loan.getSn());
//                    loan.setOverFlowAmount(loanOverFlowAmount+amount);
                    log.info("new over-flow is :: "+(loanOverFlowAmount+amount));
//                    account.setLoan(loan);
                }
//                Double accountBalance = account.getAccountBalance();
//                Double newAccountBalance =amount+accountBalance;
//                account.setAccountBalance(newAccountBalance);
//                Account savedAccount=accountRepository.save(account);
                accountRepository.updateAccountBalance(amount, acid);

                EntityResponse response = new EntityResponse();
                response.setMessage(amount.toString()+" was successfully credited to , Account Number: "+acid);
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity("");
                return response;
            }
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse debitAccount(String acid, Double amount){
        try{
            EntityResponse acidValidator =validatorsService.acidValidator(acid, "Debit");
            if(acidValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                return acidValidator;
            }else {
                Account account = accountRepository.findByAcid(acid).orElse(null);
                EntityResponse acidDebitValidator =validatorsService.acidDebitValidator(account,amount);
                if(acidDebitValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                    return acidDebitValidator;
                }else {
                    EntityResponse acidWithdrawalValidator =validatorsService.acidWithdrawalValidator(account);
                    if(acidWithdrawalValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                        return acidWithdrawalValidator;
                    }else {
                        Double accountBalance = account.getAccountBalance();
//                        Double newAccountBalance =accountBalance+amount;
//                        account.setAccountBalance(newAccountBalance);
//                        Account savedAccount=accountRepository.save(account);

                        accountRepository.updateAccountBalance(amount, acid);

                        EntityResponse response = new EntityResponse();
                        response.setMessage(amount.toString()+" was successfully debited to , Account Number: "+acid);
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity("");
                        return response;
                    }
                }
            }

        }catch (Exception e){
            log.info("Catched Error {} " + e);
            EntityResponse finalResponse= new EntityResponse();
            finalResponse.setMessage(e.getMessage());
            finalResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return finalResponse;
        }
    }


    public EntityResponse debitAccountLien(String acid, Double amount){
        try{
            Account account = accountRepository.findByAcid(acid).orElse(null);
            Double accountBalance = account.getAccountBalance();
            Double newAccountBalance =accountBalance+amount;
            account.setAccountBalance(newAccountBalance);
            Account savedAccount=accountRepository.save(account);

            EntityResponse response = new EntityResponse();
            response.setMessage(amount.toString()+" was successfully debited to , Account Number: "+acid);
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity("");
            return response;
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            EntityResponse finalResponse= new EntityResponse();
            finalResponse.setMessage(e.getMessage());
            finalResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return finalResponse;
        }
    }

    @Transactional
    public EntityResponse incomingFullTransaction(ReceivedTransactionHolder receivedTransactionHolder){
        log.info("initiating incomingFullTransaction");
        try{
            List<RecievedTransactionDetails> recievedTransactionDetails = receivedTransactionHolder.getTransactions();
            List<EntityResponse> responses =new ArrayList<>();

            final Integer[] successfull = {0};
            final Integer[] failed = {0};
            final Integer[] returnStatusCode = {HttpStatus.OK.value()};
            Integer acidStatusCode = HttpStatus.OK.value();

//            log.info("Looping through incoming transactions");

            for(Integer i=0;i<recievedTransactionDetails.size();i++){
//                log.info("Loop number "+ i);
                RecievedTransactionDetails recievedTransactionDetails1=recievedTransactionDetails.get(i);
                String acid = recievedTransactionDetails1.getAcid();
                Account account = accountRepository.findByAcid(acid).orElse(null);
                Double amount=recievedTransactionDetails1.getTransactionAmount();

                //basic account validation
                EntityResponse acidValidator =validatorsService.acidValidator(acid, "Debit");
                if(acidValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                    responses.add(acidValidator);
                    acidStatusCode= HttpStatus.NOT_ACCEPTABLE.value();
                    break;
                }

                //withdrawal validations -- withdrawal and debits validations
                if(recievedTransactionDetails1.getPartTranType().equals(CONSTANTS.INCOMINGDEBITSTRING)){
//                    log.info("Validation of debit transaction");
//                    log.info("Transaction description===>"+ recievedTransactionDetails1.getTransactionDesc());
                    EntityResponse acidDebitValidator =validatorsService.acidDebitValidator(account,amount);
                    EntityResponse acidWithdrawalValidator =validatorsService.acidWithdrawalValidator(account);
                    if(recievedTransactionDetails1.getTransactionDesc().equals("NORMAL")){
//                        log.info("normal transaction");
//                        log.info("The acid status code is :: 1 "+acidStatusCode);
                        if(acidDebitValidator.getStatusCode() == HttpStatus.NOT_ACCEPTABLE.value()){
//                            log.info("acidDebitValidator "+acidDebitValidator.getStatusCode());
                            responses.add(acidDebitValidator);
                            acidStatusCode= HttpStatus.NOT_ACCEPTABLE.value();
//                            log.info("The acid status code is 2 :: "+acidStatusCode);
                            break;
                        } else if (acidWithdrawalValidator.getStatusCode() == HttpStatus.NOT_ACCEPTABLE.value()) {
//                            log.info("acidWithdrawalValidator "+acidWithdrawalValidator.getStatusCode());
//                            log.info("acidWithdrawalValidator "+ acidWithdrawalValidator.getMessage());
                            responses.add(acidWithdrawalValidator);
                            acidStatusCode= HttpStatus.NOT_ACCEPTABLE.value();
//                            log.info("The acid status code is :: 3 "+acidStatusCode);
                            break;
                        }
                    } else if (recievedTransactionDetails1.getTransactionDesc().equals("LIEN")){
//                        log.info("lien transaction");
                        EntityResponse acidDebitLienValidator =validatorsService.acidDebitLienValidator(account,amount);
                        if(acidDebitLienValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                            responses.add(acidDebitLienValidator);
                            acidStatusCode= HttpStatus.NOT_ACCEPTABLE.value();
                            break;
                        } else if (acidWithdrawalValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                            responses.add(acidDebitLienValidator);
                            acidStatusCode= HttpStatus.NOT_ACCEPTABLE.value();
                            break;
                        }
                    }else {
                        if(acidDebitValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                            responses.add(acidDebitValidator);
                            acidStatusCode= HttpStatus.NOT_ACCEPTABLE.value();
                            break;
                        } else if (acidWithdrawalValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                            responses.add(acidDebitValidator);
                            acidStatusCode= HttpStatus.NOT_ACCEPTABLE.value();
                            break;
                        }
                    }

                }

            }

//            log.info("The acid status code is :: "+acidStatusCode);

            if(acidStatusCode.equals(HttpStatus.OK.value())){
//                log.info("Completed account validation");
                recievedTransactionDetails.forEach(recievedTransactionDetail -> {
                    String partTranType= recievedTransactionDetail.getPartTranType();
                    String acid = recievedTransactionDetail.getAcid();
                    Double amount = recievedTransactionDetail.getTransactionAmount();
                    if(partTranType.equals(CONSTANTS.INCOMINGCREDITSTRING)){
//                        log.info("Initializing credit transaction");
                        EntityResponse entityResponse=creditAccount(acid, amount);
                        responses.add(entityResponse);
                    }else if(partTranType.equals(CONSTANTS.INCOMINGDEBITSTRING)){
                        if(recievedTransactionDetail.getTransactionDesc().equals("LIEN")){
//                            log.info("Initializing debit transaction");
                            EntityResponse entityResponse=debitAccountLien(acid, amount);
                            responses.add(entityResponse);
                        }else {
//                            log.info("Initializing debit transaction");
                            EntityResponse entityResponse=debitAccount(acid, amount);
                            responses.add(entityResponse);
                        }
                    }else{
                        EntityResponse entityResponse = new EntityResponse();
                        entityResponse.setMessage("Expecting \""+CONSTANTS.INCOMINGDEBITSTRING+"\" for DEBIT OR \""+CONSTANTS.INCOMINGCREDITSTRING+"\" FOR CREDIT in partTranType");
                        entityResponse.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        entityResponse.setEntity("");
                        responses.add(entityResponse);
                    }
                });
            }
            //get successful and failed
//            log.info("responses size is "+responses.size());

            if(responses.size()>0){
                for(Integer i=0;i<responses.size();i++){
                    Integer statusCode=responses.get(i).getStatusCode();
//                    log.info("final response status code :: "+responses.get(i).getStatusCode());
//                    log.info("final response status code :: "+responses.get(i).getMessage());
                    if(statusCode.equals(HttpStatus.OK.value())){
                        successfull[0] = successfull[0] +1;
                    }else {
                        failed[0] = failed[0] +1;
                        returnStatusCode[0] = HttpStatus.BAD_REQUEST.value();
                    }
                }
            }else {
                returnStatusCode[0] = HttpStatus.BAD_REQUEST.value();
            }


            EntityResponse finalResponse= new EntityResponse();
            finalResponse.setMessage("SUCCESSFULL TRANSACTION: "+successfull[0]+", FAILED TRANSACTIONS: "+failed[0]);
            finalResponse.setEntity(responses);
            finalResponse.setStatusCode(returnStatusCode[0]);
//            log.info("responses :: "+finalResponse.getMessage());
//            log.info("responses :: "+finalResponse.getStatusCode());
            return finalResponse;


        }catch (Exception e){
//            log.info("Catched Error {} " + e.getMessage());
            EntityResponse finalResponse= new EntityResponse();
            finalResponse.setMessage(e.getMessage());
            finalResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return finalResponse;
        }
    }

    //fee transaction
    public EntityResponse statementTransaction(AccountStatementDto accountStatementDto) {
        try {

            // TODO: 3/31/2023 check debit account balance

            EntityResponse res= new EntityResponse<>();

            res= productFeesService.getProductFees2(
                    accountStatementDto.getEventId());

            if(res.getStatusCode()== HttpStatus.OK.value()) {
                FeeDto f= (FeeDto) res.getEntity();
                Double amt =f.getAmt();
                String crAcid= f.getAc_placeholder();

                Double totalAmt =amt *accountStatementDto.getPagesNumber();

                if(totalAmt>0){
                    TransactionHeader th = createTransactionHeader("KES",
                            accountStatementDto.getDescription(),
                            totalAmt,
                            accountStatementDto.getDrAccount(),
                            crAcid);

                    EntityResponse transactionRes= transactionsController.systemTransaction1(th).getBody();
                    if(!transactionRes.getStatusCode().equals(HttpStatus.OK.value())){
                        ///failed transactiom EntityResponse response = new EntityResponse();
                        res.setMessage("TRANSACTION ERROR! COULD NOT PERFORM FEE STATEMENT TRANSACTION");
                        res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        res.setEntity(th);

                    }else {
                        String transactionCode= (String) transactionRes.getEntity();

                        res.setStatusCode(HttpStatus.OK.value());
                        res.setMessage("Transaction was successful Of ksh "+totalAmt+" "+transactionCode);
                        res.setEntity(transactionCode);
                    }
                }else {
                    res.setMessage("TRANSACTION AMT SHOULD BE GREATER THAN ZERO");
                    res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    res.setEntity(null);
                }
            }
            return  res;
        }catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public TransactionHeader createTransactionHeader(String currency,
                                                     String transDesc,
                                                     Double totalAmount,
                                                     String drAc,
                                                     String crAc) {
        TransactionHeader transactionHeader = new TransactionHeader();
        transactionHeader.setTransactionType(CONSTANTS.SYSTEM_USERNAME);
        transactionHeader.setCurrency(currency);
        transactionHeader.setTransactionDate(new Date());
        transactionHeader.setEntityId(CONSTANTS.SYSTEM_ENTITY);
        transactionHeader.setTotalAmount(totalAmount);

        PartTran drPartTran = new PartTran();
        drPartTran.setPartTranType(CONSTANTS.DEBITSTRING);
        drPartTran.setTransactionAmount(totalAmount);
        drPartTran.setAcid(drAc);
        drPartTran.setCurrency(currency);
        drPartTran.setExchangeRate("");
        drPartTran.setTransactionDate(new Date());
        drPartTran.setTransactionParticulars(transDesc);
        drPartTran.setIsoFlag(CONSTANTS.YES);

        PartTran crPartTran = new PartTran();
        crPartTran.setPartTranType(CONSTANTS.CREDITSTRING);
        crPartTran.setTransactionAmount(totalAmount);
        crPartTran.setAcid(crAc);
        crPartTran.setCurrency(currency);
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


    public boolean isJSONValid(String json) {
        try {
            new JSONObject(json);
        } catch (JSONException e) {
            try {
                new JSONArray(json);
            } catch (JSONException ne) {
                return false;
            }
        }
        return true;
    }



}
