package com.emtechhouse.accounts.Models.Accounts.AccountClosure;

import com.emtechhouse.accounts.Models.Accounts.Account;
import com.emtechhouse.accounts.Models.Accounts.AccountRepository;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.NewTransactionService;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans.PartTran;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TransactionHeader;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class AccountClosureService {
//    @Autowired
    AccountClosureRepo accountClosureRepo;
    @Autowired
    AccountActivationRepo accountActivationRepo;
    @Autowired
    AccountRepository accountRepository;

    private final NewTransactionService newTransactionService;

    public AccountClosureService( NewTransactionService newTransactionService) {
        this.newTransactionService = newTransactionService;
    }

    public EntityResponse initiateAccountClosure(String acid, String closureReason){
        try {
            EntityResponse res= new EntityResponse<>();
            Optional<Account> account= accountRepository.findByAcidAndDeleteFlag(acid, CONSTANTS.NO);
            if(account.isPresent()){
                Account presentAccount = account.get();
                if(presentAccount.getAccountClosureInfo() == null){
                    if(!presentAccount.getAccountStatus().equals(CONSTANTS.CLOSED)){
                        if(!presentAccount.getAccountType().equals(CONSTANTS.LOAN_ACCOUNT)){
                            if(presentAccount.getAccountBalance() != null ){
                                AccountClosureInfo accountClosureInfo= new AccountClosureInfo();
                                accountClosureInfo.setAccount(presentAccount);
                                accountClosureInfo.setClosingDate(new Date());
                                accountClosureInfo.setClosedBy(UserRequestContext.getCurrentUser());
                                accountClosureInfo.setPostedFlag(CONSTANTS.YES);
                                accountClosureInfo.setVerifiedFlag(CONSTANTS.NO);
                                accountClosureInfo.setClosureReason(closureReason);

                                accountClosureRepo.save(accountClosureInfo);

                                res.setStatusCode(HttpStatus.CREATED.value());
                                res.setMessage("Account closer initiated successfully! waiting verification");
                            }else {
                                res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                res.setMessage("Failed! Account has no balance");
                            }
                        }else {
                            res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            res.setMessage("Failed! Only applicable for SBA accounts");
                        }
                    }else {
                        res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        res.setMessage("Failed! The  Account is already closed");
                    }
                }else {
                    res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    res.setMessage("Closure process for the account was already initiated by "+presentAccount.getAccountClosureInfo().getClosedBy()+" on "+presentAccount.getAccountClosureInfo().getClosingDate());
                }
            }else {
                res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                res.setMessage("Failed! Could not find the account");
            }
            return res;
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse verifyAccountClosure(String acid){
        try {
            EntityResponse res= new EntityResponse<>();
            Optional<Account> account= accountRepository.findByAcidAndDeleteFlag(acid, CONSTANTS.NO);
            if(account.isPresent()){
                Account presentAccount = account.get();
                if(!presentAccount.getAccountStatus().equals(CONSTANTS.CLOSED)){
                    if(!presentAccount.getAccountType().equals(CONSTANTS.LOAN_ACCOUNT)){
                        if(presentAccount.getAccountClosureInfo() != null){
                            String currentUser= UserRequestContext.getCurrentUser();
                            String initiatedBy= presentAccount.getAccountClosureInfo().getClosedBy();
                            if(!currentUser.equals(initiatedBy)){
                                AccountClosureInfo info=presentAccount.getAccountClosureInfo();
                                info.setVerifiedBy(currentUser);
                                info.setVerifiedFlag(CONSTANTS.YES);
                                info.setVerificationDate(new Date());

                                presentAccount.setAccountStatus(CONSTANTS.CLOSED);
                                presentAccount.setAccountClosureInfo(info);
                                Long accountId = presentAccount.getAccountClosureInfo().getId();
                                //Void deleteActivationInfo = accountClosureRepo.deleteAccountActivationInfo(accountId);
                                accountRepository.save(presentAccount);

                                res.setStatusCode(HttpStatus.OK.value());
                                res.setMessage("Account closure verified successfully");
                            }else {
                                res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                res.setMessage("Failed! You cannot verify what you initiated");
                            }
                        }else {
                            res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            res.setMessage("Failed! Account closure was not initiated");
                        }
                    }else {
                        res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        res.setMessage("Failed! Only applicable for SBA accounts");
                    }
                }else {
                    res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    res.setMessage("The Account was already closed by "+presentAccount.getAccountClosureInfo().getClosedBy()+ " on "+presentAccount.getAccountClosureInfo().getClosingDate());
                }
            }else {
                res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                res.setMessage("Failed! Could not find the account");
            }
            return res;
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse initiateAccountActivation(String acid, String closureReason){
        try {
            EntityResponse res= new EntityResponse<>();
            Optional<Account> account= accountRepository.findByAcidAndDeleteFlag(acid, CONSTANTS.NO);
            if(account.isPresent()){
                Account presentAccount = account.get();
                if(presentAccount.getAccountActivationInfo() == null){
                    if(!presentAccount.getAccountStatus().equals(CONSTANTS.ACTIVE)){
                        if(!presentAccount.getAccountType().equals(CONSTANTS.LOAN_ACCOUNT)){
                            AccountActivationInfo accountActivationInfo= new AccountActivationInfo();
                            accountActivationInfo.setAccount(presentAccount);
                            accountActivationInfo.setActivationDate(new Date());
                            accountActivationInfo.setActivatedBy(UserRequestContext.getCurrentUser());
                            accountActivationInfo.setPostedFlag(CONSTANTS.YES);
                            accountActivationInfo.setVerifiedFlag(CONSTANTS.NO);
                            accountActivationInfo.setActivationReason(closureReason);

                            accountActivationRepo.save(accountActivationInfo);

                            res.setStatusCode(HttpStatus.CREATED.value());
                            res.setMessage("Account activation for "+acid+" initiated, waiting for verification to be activated");

                        }else {
                            res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            res.setMessage("Failed! Only applicable for SBA accounts");
                        }
                    }else {
                        res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        res.setMessage("The account is already active!!");
                    }
                }else {
                    res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    res.setMessage("Activation process for the account was already initiated by "+presentAccount.getAccountActivationInfo().getActivatedBy()+" on "+presentAccount.getAccountActivationInfo().getActivationDate());
                }
            }else {
                res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                res.setMessage("Failed!! Could not find the account");
            }
            return res;
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse verifyAccountActivation(String acid){
        try {
            EntityResponse res= new EntityResponse<>();
            Optional<Account> account1= accountRepository.findByAcidAndDeleteFlag(acid, CONSTANTS.NO);
            if(account1.isPresent()){
                Account presentAccount1 = account1.get();
                if(!presentAccount1.getAccountStatus().equals(CONSTANTS.ACTIVE)){
                    if(!presentAccount1.getAccountType().equals(CONSTANTS.LOAN_ACCOUNT)){
                        if(presentAccount1.getAccountActivationInfo() != null){
                            String currentUser= UserRequestContext.getCurrentUser();
                            String entityId = EntityRequestContext.getCurrentEntityId();
                            String initiatedBy= presentAccount1.getAccountActivationInfo().getActivatedBy();
                            if(!currentUser.equals(initiatedBy)){
                                String transactionType = CONSTANTS.ACCOUNT_ACTIVATION;
                                res = enterActivationFeeTransaction(acid, "302020", 300.00, "Activation fees for account "+acid, transactionType, "ACT");
                                System.out.println("Account activation fee collection response: "+res);
                                if(res.getStatusCode() == HttpStatus.CREATED.value() || res.getStatusCode() == HttpStatus.OK.value()){
                                    String getActivationTransactionCode = accountActivationRepo.selectTransactionCode(acid);
                                    System.out.println("Account activation transaction code is: "+getActivationTransactionCode);
                                    res  = postActivationFeeTransaction(getActivationTransactionCode);
                                    System.out.println("Account activation post response: "+res);
                                    if(res.getStatusCode() == HttpStatus.OK.value()){
                                        AccountActivationInfo info=presentAccount1.getAccountActivationInfo();
                                        info.setVerifiedBy(currentUser);
                                        info.setVerifiedFlag(CONSTANTS.YES);
                                        info.setVerificationDate(new Date());

                                        presentAccount1.setAccountStatus(CONSTANTS.ACTIVE);
                                        presentAccount1.setAccountActivationInfo(info);
                                        accountRepository.save(presentAccount1);
                                        res.setStatusCode(HttpStatus.OK.value());
                                        res.setMessage("Account activated successfully");
                                    }else {
                                        res.setStatusCode(HttpStatus.NOT_FOUND.value());
                                        res.setMessage(res.getMessage());
                                    }

                                }else {
                                    res.setStatusCode(HttpStatus.NOT_FOUND.value());
                                    res.setMessage("Account activation failed, could not collect the activation fee");
                                }
                                return res;


                            }else {
                                res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                res.setMessage("Failed! You cannot verify what you initiated");
                            }
                        }else {
                            res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            res.setMessage("Failed! Account activation was not initiated");
                        }
                    }else {
                        res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        res.setMessage("Failed! Only applicable for SBA accounts");
                    }
                }else {
                    res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    res.setMessage("The Account is already active");
                }
            }else {
                res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                res.setMessage("Failed! Could not find the account");
            }
            return res;
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }


    public EntityResponse balanceEnquiryCharges(String acid){
        try {
            EntityResponse res= new EntityResponse<>();
            Optional<Account> account1= accountRepository.findByAcidAndDeleteFlag(acid, CONSTANTS.NO);
            if(account1.isPresent()){
                Account presentAccount1 = account1.get();
                            String currentUser= UserRequestContext.getCurrentUser();
                                String transactionType = CONSTANTS.BALANCE_ENQUIRY;
                System.out.println("The Acid: "+acid);
                                res = enterBalanceEnquiryFeeTransaction(acid, transactionType, "BLE");
                                String getBalanceTransactionCode = accountActivationRepo.selectBalanceTransactionCode(acid);
                                System.out.println("Balance Enquiry fee transaction code is: "+getBalanceTransactionCode);
                                res  = postBalanceFeeTransaction(getBalanceTransactionCode);
                                System.out.println("Balance Enquiry fee post response: "+res.getMessage());
                                if(res.getStatusCode() == HttpStatus.CREATED.value() || res.getStatusCode() == HttpStatus.OK.value()){
                                    if(res.getStatusCode() == HttpStatus.OK.value()){

                                        res.setStatusCode(HttpStatus.OK.value());
                                        res.setMessage("Balance fee added successfully");

                                        System.out.println("Balance fee added successfully");
                                    }else {
                                        res.setStatusCode(HttpStatus.NOT_FOUND.value());
                                        res.setMessage(res.getMessage());
                                    }

                                }else {
                                    res.setStatusCode(HttpStatus.NOT_FOUND.value());
                                    res.setMessage("could not collect the balance enquiry fee");
                                }
                                return res;

            }else {
                res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                res.setMessage("Failed! Could not find the account");
            }
            return res;
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }



    public EntityResponse smsCharges(String acid){
        try {
            System.out.println("In service to collect sms charges");
            EntityResponse res= new EntityResponse<>();
            Optional<Account> account1= accountRepository.findByAcidAndDeleteFlag(acid, CONSTANTS.NO);
            if(account1.isPresent()){
                Account presentAccount1 = account1.get();
                String currentUser= UserRequestContext.getCurrentUser();
                String transactionType = CONSTANTS.SMS_CHARGES;
                System.out.println("The Acid: "+acid);
                res = enterSMSFeeTransaction(acid, transactionType, "SMS");
                String getBalanceTransactionCode = accountActivationRepo.selectSmsTransactionCode(acid);
                System.out.println("SMS fee transaction code is: "+getBalanceTransactionCode);
                res  = postBalanceFeeTransaction(getBalanceTransactionCode);
                System.out.println("SMS fee post response: "+res.getMessage());
                if(res.getStatusCode() == HttpStatus.CREATED.value() || res.getStatusCode() == HttpStatus.OK.value()){
                    if(res.getStatusCode() == HttpStatus.OK.value()){

                        res.setStatusCode(HttpStatus.OK.value());
                        res.setMessage("SMS fee added successfully");

                        System.out.println("SMS fee added successfully");
                    }else {
                        res.setStatusCode(HttpStatus.NOT_FOUND.value());
                        res.setMessage(res.getMessage());
                    }

                }else {
                    res.setStatusCode(HttpStatus.NOT_FOUND.value());
                    res.setMessage("could not collect the balance enquiry fee");
                }
                return res;

            }else {
                res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                res.setMessage("Failed! Could not find the account");
            }
            return res;
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }





    public EntityResponse getAccountClosureDetails(String acid){
        try{
            Optional<Account> account= accountRepository.findByAcidAndDeleteFlag(acid, CONSTANTS.NO);
            EntityResponse res= new EntityResponse<>();
            if(account.isPresent()){
                log.info("Account is present");
                Account presentAccount = account.get();
                Optional<AccountClosureInfo> accountClosureInfo=accountClosureRepo.findByAccount(presentAccount);
                if(accountClosureInfo.isPresent()){
                    AccountClosureInfo info=accountClosureInfo.get();
                    log.info("Closure details present");
                    res.setStatusCode(HttpStatus.FOUND.value());
                    res.setMessage(HttpStatus.FOUND.getReasonPhrase());
                    res.setEntity(info);
                }else {
                    res.setStatusCode(HttpStatus.NOT_FOUND.value());
                    res.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                }
            }else {
                res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                res.setMessage("Failed! Could not find the account");
            }
            return res;
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse getAccountActivationDetails(String acid){
        try{
            Optional<Account> account= accountRepository.findByAcidAndDeleteFlag(acid, CONSTANTS.NO);
            EntityResponse res= new EntityResponse<>();
            if(account.isPresent()){
                log.info("Account is present");
                Account presentAccount = account.get();
                Optional<AccountActivationInfo> accountActivationInfo=accountActivationRepo.findByAccount(presentAccount);
                if(accountActivationInfo.isPresent()){
                    AccountActivationInfo info=accountActivationInfo.get();
                    log.info("Activation details present");
                    res.setStatusCode(HttpStatus.FOUND.value());
                    res.setMessage(HttpStatus.FOUND.getReasonPhrase());
                    res.setEntity(info);
                }else {
                    res.setStatusCode(HttpStatus.NOT_FOUND.value());
                    res.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                }
            }else {
                res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                res.setMessage("Failed! Could not find the account");
            }
            return res;
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse enterActivationFeeTransaction(String drAcid, String crAcid, Double amount, String transactipnParticulars, String transactionType, String chargeEventId) {
        try {
            System.out.println("Creating activation transaction Json.");
            EntityResponse response = new EntityResponse();
            String user = UserRequestContext.getCurrentUser();
            TransactionHeader transactionHeader = new TransactionHeader();
            transactionHeader.setChargeEventId(chargeEventId);
            transactionHeader.setVerifiedFlag('Y');
            transactionHeader.setVerifiedBy(user);
            transactionHeader.setVerifiedTime(new Date());
            transactionHeader.setVerifiedFlag_2('Y');
            transactionHeader.setVerifiedBy_2(user);
            transactionHeader.setVerifiedTime_2(new Date());
            transactionHeader.setTransactionType(transactionType);
            List<PartTran> partTranList = new ArrayList<>();
            PartTran partTranDr = new PartTran();
            partTranDr.setAcid(drAcid);
//            partTranDr.setIsoFlag('Y');
//            partTranDr.setExchangeRate("1");
//            partTranDr.setParttranIdentity(CONSTANTS.Fee);
            partTranDr.setPartTranType(CONSTANTS.Debit);
//            partTranDr.setTransactionAmount(amount);
            partTranDr.setTransactionDate(new Date());
            partTranDr.setCurrency("KSH");
//            partTranDr.setTransactionParticulars(CONSTANTS.Debit + " " + transactipnParticulars);
            partTranList.add(partTranDr);
//
//            PartTran partTranCr = new PartTran();
//            partTranCr.setAcid(crAcid);
//            partTranCr.setIsoFlag('Y');
//            partTranCr.setExchangeRate("1");
//            partTranDr.setParttranIdentity(CONSTANTS.Fee);
//            partTranCr.setPartTranType(CONSTANTS.Credit);
//            partTranCr.setTransactionAmount(amount);
//            partTranCr.setTransactionDate(new Date());
//            partTranCr.setTransactionParticulars(CONSTANTS.Credit + " " + transactipnParticulars);
//            partTranList.add(partTranCr);
            transactionHeader.setPartTrans(partTranList);
            response = newTransactionService.enterAccountActivationFee(transactionHeader);
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }


    public EntityResponse enterBalanceEnquiryFeeTransaction(String drAcid, String transactionType, String chargeEventId) {
        try {
            System.out.println("Creating balance enquiry transaction Json.");
            EntityResponse response = new EntityResponse();
            String user = UserRequestContext.getCurrentUser();
            TransactionHeader transactionHeader = new TransactionHeader();
            transactionHeader.setChargeEventId(chargeEventId);
            transactionHeader.setVerifiedFlag('Y');
            transactionHeader.setVerifiedBy(user);
            transactionHeader.setVerifiedTime(new Date());
            transactionHeader.setVerifiedFlag_2('Y');
            transactionHeader.setVerifiedBy_2(user);
            transactionHeader.setVerifiedTime_2(new Date());
            transactionHeader.setTransactionType(transactionType);
            List<PartTran> partTranList = new ArrayList<>();
            PartTran partTranDr = new PartTran();
            partTranDr.setAcid(drAcid);
            partTranDr.setPartTranType(CONSTANTS.Debit);
            partTranDr.setTransactionDate(new Date());
            partTranDr.setCurrency("KSH");
            partTranList.add(partTranDr);
            transactionHeader.setPartTrans(partTranList);
            response = newTransactionService.enterBalanceEnquiryFee(transactionHeader);
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }



    public EntityResponse enterSMSFeeTransaction(String drAcid, String transactionType, String chargeEventId) {
        try {
            System.out.println("Creating SMS fee transaction Json.");
            EntityResponse response = new EntityResponse();
            String user = UserRequestContext.getCurrentUser();
            TransactionHeader transactionHeader = new TransactionHeader();
            transactionHeader.setChargeEventId(chargeEventId);
            transactionHeader.setVerifiedFlag('Y');
            transactionHeader.setVerifiedBy(user);
            transactionHeader.setVerifiedTime(new Date());
            transactionHeader.setVerifiedFlag_2('Y');
            transactionHeader.setVerifiedBy_2(user);
            transactionHeader.setVerifiedTime_2(new Date());
            transactionHeader.setTransactionType(transactionType);
            List<PartTran> partTranList = new ArrayList<>();
            PartTran partTranDr = new PartTran();
            partTranDr.setAcid(drAcid);
            partTranDr.setPartTranType(CONSTANTS.Debit);
            partTranDr.setTransactionDate(new Date());
            partTranDr.setCurrency("KSH");
            partTranList.add(partTranDr);
            transactionHeader.setPartTrans(partTranList);
            response = newTransactionService.enterSMSFee(transactionHeader);
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }


    public EntityResponse postActivationFeeTransaction(String activationRandCode) {
        try {
            EntityResponse response = new EntityResponse();
            String user = UserRequestContext.getCurrentUser();
            String entityId = EntityRequestContext.getCurrentEntityId();
            if (user.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;
            } else if (entityId.isEmpty()) {
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;
            } else {
                String activationCode = accountActivationRepo.selectActivationCode(activationRandCode);
                if (activationCode != null) {
                    response = newTransactionService.post(activationCode);
                    System.out.println("post activation response: "+response);
                    return response;

                } else {
                    response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                }
            }
            return response;
        } catch (Exception e) {
            EntityResponse res = new EntityResponse<>();
            log.error(e.getMessage());
            res.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            res.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return res;
        }
    }



    public EntityResponse postBalanceFeeTransaction(String activationRandCode) {
        try {
            EntityResponse response = new EntityResponse();
            String user = UserRequestContext.getCurrentUser();
            String entityId = EntityRequestContext.getCurrentEntityId();
            if (user.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;
            } else if (entityId.isEmpty()) {
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;
            } else {
                String activationCode = accountActivationRepo.selectActivationCode(activationRandCode);
                if (activationCode != null) {
                    response = newTransactionService.post(activationCode);
                    System.out.println("post balance fee response: "+response.getMessage());
                    return response;

                } else {
                    response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                }
            }
            return response;
        } catch (Exception e) {
            EntityResponse res = new EntityResponse<>();
            log.error(e.getMessage());
            res.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            res.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return res;
        }
    }



}
