package com.emtechhouse.accounts.Models.Accounts.AccountServices;

import com.emtechhouse.accounts.Models.Accounts.Account;
import com.emtechhouse.accounts.Models.Accounts.AccountDtos.SearchRequestDto;
import com.emtechhouse.accounts.Models.Accounts.AccountRepository;
import com.emtechhouse.accounts.Models.Accounts.AccountsLookupInterfaces.AccountLookUpInterface;
import com.emtechhouse.accounts.Models.Accounts.AccountsLookupInterfaces.AccountLookUpInterfaceASIS;
import com.emtechhouse.accounts.Models.Accounts.AccountsLookupInterfaces.LoanVerificationInterface;
import com.emtechhouse.accounts.Models.Accounts.FiltersSpecification;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanLimit.LoanLimit;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanLimit.LoanLimitRepo;
import com.emtechhouse.accounts.Models.Accounts.OfficeAccount.OfficeAccount;
import com.emtechhouse.accounts.Models.Accounts.OfficeAccount.OfficeAccountRepository;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ItemServiceCaller;
import com.emtechhouse.accounts.Models.Accounts.TermDeposit.TermDepositService;
import com.emtechhouse.accounts.Models.Accounts.Validators.ValidatorService;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.DatesCalculator;
import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.accounts.Utils.RESPONSEMESSAGES;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    LoanLimitRepo loanLimitRepo;

    @Autowired
    private ValidatorService validatorsService;
    @Autowired
    private OfficeAccountRepository officeAccountRepository;
    @Autowired
    private FiltersSpecification filtersSpecification;

    @Autowired
    private TermDepositService termDepositService;

    @Autowired
    private DatesCalculator datesCalculator;
    @Autowired
    private ItemServiceCaller itemServiceCaller;

    @Autowired
    private com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans.PartTranRepository partTranRepository;

    public EntityResponse<List<Account>> listAllAccounts() {
        try {
            EntityResponse userValidator = validatorsService.userValidator();
            if (userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                return userValidator;
            } else {
                List<Account> accounts = accountRepository.findAll();
                EntityResponse listChecker = validatorsService.listLengthChecker(accounts);
                return listChecker;
            }

        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<List<Account>> listAllUndeletedAccounts() {
        try {
            EntityResponse userValidator = validatorsService.userValidator();
            if (userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                return userValidator;
            } else {
                List<Account> accounts = accountRepository.findByDeleteFlag(CONSTANTS.NO);
                EntityResponse listChecker = validatorsService.listLengthChecker(accounts);
                return listChecker;
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }



    public EntityResponse<Account> getLoanLimit(String acid) {
        try {
            log.info("step 1");
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                EntityResponse response = new EntityResponse();
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return response;
            } else {
                log.info("step 2");
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return response;
                } else {
                    log.info("step 3");
                    Optional<Account> accountBalance = accountRepository.findAccountBalance(acid);
                    log.info("continuing");
                    if (accountBalance.isPresent()) {
                        log.info("query failed");
                        Account existingAccount = accountBalance.get();
                        log.info("Attempting to get balance");
                        Double existingBalance = existingAccount.getAccountBalance();
                        log.info(existingBalance.toString());
                        if (existingBalance < 0 ) {
                            EntityResponse response = new EntityResponse();
                            response.setMessage("YOU HAVE INSUFFICIENCT BALANCE IN YOUR ACCOUNT TO QUALIFY FOR LOAN");
                            response.setStatusCode(HttpStatus.FOUND.value());
                            response.setEntity('0');
                            return response;
                        } else {
                            log.info("step 4");
                            // Get today's date
                            Date formattedDate = new Date(System.currentTimeMillis());
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            String todayDate = dateFormat.format(formattedDate);

                            // Calculate the date 6 months ago
                            Calendar calendar = Calendar.getInstance();
                            calendar.add(Calendar.MONTH, -6);
                            Date sixMonthsAgo = calendar.getTime();
                            String sixMonthsTime = dateFormat.format(sixMonthsAgo);
                            log.info("Six months ago date {}",sixMonthsTime);

                            // Call the repository method with the specified parameters
                            log.info("step 5");
                            List<Account> accountSum = accountRepository.findAccountSum(acid, todayDate, sixMonthsTime);
//                            log.info("Active account "+accountSum.toString().);
                            if (accountSum.isEmpty()){
                                EntityResponse response = new EntityResponse();
                                response.setMessage("YOUR ACCOUNT HAS BEEN INACTIVE FOR THE LAST 6 MONTHS");
                                response.setStatusCode(HttpStatus.NO_CONTENT.value());
                                response.setEntity("0");
                                return response;
                            }else{
                                log.info("step 6");
                                Optional<com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans.PartTran> dDay = partTranRepository.findLastPartTranDate(acid);
                                Date lastDateEntry = dDay.get().getTransactionDate();
                                log.info("The last date entry {}", lastDateEntry);
                                Calendar calendar1 = Calendar.getInstance();
                                calendar1.setTime(lastDateEntry);
                                calendar1.add(Calendar.MONTH, -6);
                                Date siixMonthsAgo = calendar1.getTime();
                                String siixMonthsTime = dateFormat.format(siixMonthsAgo);
                                String newDate = dateFormat.format(lastDateEntry);
                                log.info("This is the new date {}",newDate);
                                log.info("This is the six MonthsTime {}",siixMonthsTime);
                                log.info("Attempting query");
                                String older = "2024-01-01";
                                String newer = "2024-03-05";
                                Optional<Double> loanLimits= accountRepository.findLoanLimit(acid, siixMonthsTime, newDate);
                                if (loanLimits.isPresent()){
                                    Double loan = loanLimits.get();
                                    log.info("Loan: "+loan);
                                    log.info("didnt fail");
                                    Double amount = loan * 0.3;
                                    if (loan < 500) {
                                        EntityResponse response = new EntityResponse();
                                        response.setMessage("YOUR TRANSACTION ARE BELOW 500 THEREFORE YOU DONT QUALIFY FOR LOAN");
                                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                        response.setEntity('0');
                                        return response;
                                    }

                                    // Get OD balance

                                    String customerCode = accountRepository.getTheCustomerCode(acid);
                                    String OdAcid = accountRepository.getOdAcid(customerCode);
                                    Double loanBlAccounts = accountRepository.sumTotalLoansByProductCode(customerCode);
                                    Double interestAmount = accountRepository.getMobileLoanInterestAmount(customerCode)*-1;
                                    log.info("The Loan amount: "+loanBlAccounts +" And interest Amount: "+interestAmount);
                                    System.out.println("Total Loan balance is: "+(loanBlAccounts + interestAmount));

                                    Double limitAmount = (amount + (loanBlAccounts + interestAmount));
                                    log.info("Limit amount: "+limitAmount);
                                    EntityResponse response = new EntityResponse();
                                    Optional<LoanLimit> qwerty = loanLimitRepo.findbyAcid(acid);
                                    if (qwerty.isPresent()){
                                        LoanLimit qwerty2 = qwerty.get();
                                        qwerty2.setLoanLimit(limitAmount);
                                        qwerty2.setDate(LocalDateTime.now());
                                        loanLimitRepo.save(qwerty2);
                                        log.info("CONGRATULATION 1, YOU QUALIFY FOR A LOAN: "+limitAmount);
                                        response.setMessage("CONGRATULATION, YOU QUALIFY FOR A LOAN");
                                        response.setStatusCode(HttpStatus.FOUND.value());
                                        response.setEntity(qwerty2);
                                        return response;
                                    }else {
                                        LoanLimit newloan = new LoanLimit();
                                        newloan.setLoanLimit(limitAmount);
                                        newloan.setAcid(acid);
                                        newloan.setDate(LocalDateTime.now());
                                        newloan.setEntityId(EntityRequestContext.getCurrentEntityId());
                                        loanLimitRepo.save(newloan);
                                        log.info("CONGRATULATION 2, YOU QUALIFY FOR A LOAN: "+limitAmount);
                                        response.setMessage("CONGRATULATION, YOU QUALIFY FOR A LOAN");
                                        response.setStatusCode(HttpStatus.FOUND.value());
                                        response.setEntity(newloan);
                                        return response;
                                    }
                                }

                            }

                        }

                    } else {
                        EntityResponse response = new EntityResponse();
                        response.setMessage(RESPONSEMESSAGES.RECORD_NOT_FOUND);
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity("");
                        return response;
                    }
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
        return null;

    }



    //CHANGE LATER
    public EntityResponse<Account> retrieveAccount(String acid) {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                EntityResponse response = new EntityResponse();
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return response;
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return response;
                } else {
                    Optional<Account> account = accountRepository.findByAcidAndDeleteFlag(acid, CONSTANTS.NO);
                    if (account.isPresent()) {
                        Account existingAccount = account.get();
                        EntityResponse response = new EntityResponse();
                        response.setMessage(RESPONSEMESSAGES.SUCCESS);
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(existingAccount);
                        return response;
                    } else {
                        EntityResponse response = new EntityResponse();
                        response.setMessage(RESPONSEMESSAGES.RECORD_NOT_FOUND);
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity("");
                        return response;
                    }
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }


    public EntityResponse<Account> retrieveAccount(Long id) {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                EntityResponse response = new EntityResponse();
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return response;
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return response;
                } else {
                    Optional<Account> account = accountRepository.findById(id);
                    if (account.isPresent()) {
                        Account existingAccount = account.get();
                        EntityResponse response = new EntityResponse();
                        response.setMessage(RESPONSEMESSAGES.SUCCESS);
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(existingAccount);
                        return response;
                    } else {
                        EntityResponse response = new EntityResponse();
                        response.setMessage(RESPONSEMESSAGES.RECORD_NOT_FOUND);
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity("");
                        return response;
                    }
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }


    //TODO: ALL RETAIL ACCOUNTS
    public EntityResponse<List<Account>> findAllRetailAccounts() {
        try {
            EntityResponse userValidator = validatorsService.userValidator();
            if (userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                return userValidator;
            } else {
                List<Account> retailAccounts = accountRepository.findByCustomerTypeAndDeleteFlag(CONSTANTS.RETAIL_CUSTOMER, CONSTANTS.NO);
                EntityResponse listChecker = validatorsService.listLengthChecker(retailAccounts);
                return listChecker;
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    //TODO: FIND ALL UNVERIFIED ACCOUNTS
    public EntityResponse<List<Account>> findAllUnverifiedAccounts() {
        try {
            EntityResponse userValidator = validatorsService.userValidator();
            if (userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                return userValidator;
            } else {
                List<Account> retailAccounts = accountRepository.findByVerifiedFlag(CONSTANTS.NO);
                EntityResponse listChecker = validatorsService.listLengthChecker(retailAccounts);
                return listChecker;
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }


    public EntityResponse<List<AccountRepository.AccountsByAccountType>> fetchByAccountType(String accountType) {
        try {
            EntityResponse userValidator = validatorsService.userValidator();
            if (userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                return userValidator;
            } else {
                List<AccountRepository.AccountsByAccountType> accounts = accountRepository.findByAccountTypeAndDeleteFlag(accountType, CONSTANTS.NO);
                Integer accSize = accounts.size();
                EntityResponse listChecker = validatorsService.listLengthChecker(accounts);
                return listChecker;
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }


    public EntityResponse<List<AccountRepository.AccountsByAccountType>> fetchByGlCode(String glCode) {
        try {
            EntityResponse userValidator = validatorsService.userValidator();
            if (userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                return userValidator;
            } else {
                List<AccountRepository.AccountsByAccountType> accounts = accountRepository.findByGlCodeAndDeleteFlag(glCode, CONSTANTS.NO);
                Integer accSize = accounts.size();
                EntityResponse listChecker = validatorsService.listLengthChecker(accounts);
                return listChecker;
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<List<Account>> fetchTellersAccounts() {
        try {
            EntityResponse userValidator = validatorsService.userValidator();
            if (userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                return userValidator;
            } else {
                List<Account> accounts = accountRepository.findByAccountTypeAndTellerAccountAndDeleteFlag(CONSTANTS.OFFICE_ACCOUNT, true, 'N');
                Integer accSize = accounts.size();
                EntityResponse listChecker = validatorsService.listLengthChecker(accounts);
                return listChecker;
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }


    public EntityResponse<List<?>> fetchByAccountTypeForLookup(String accountType) {
        try {
            EntityResponse userValidator = validatorsService.userValidator();
            if (userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                return userValidator;
            } else {
                List<AccountRepository.AccountsByAccountType> accounts = accountRepository.findByAccountTypeAndDeleteFlagLookUp(CONSTANTS.NO, accountType);
                Integer accSize = accounts.size();
                EntityResponse listChecker = validatorsService.listLengthChecker(accounts);
                return listChecker;
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<List<?>> fetchAllRepaymentAccountsForLookUp() {
        try {
            EntityResponse userValidator = validatorsService.userValidator();
            if (userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                return userValidator;
            } else {
                List<AccountRepository.AccountsByAccountType> accounts = accountRepository.findByProductCodeAndDeleteFlagLookUp(CONSTANTS.NO, CONSTANTS.REPAYMENT_ACCOUNT_PROD);
                Integer accSize = accounts.size();
                EntityResponse listChecker = validatorsService.listLengthChecker(accounts);
                return listChecker;
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<List<AccountRepository.AccountsByAccountType>> loanAccounts() {
        try {
            EntityResponse userValidator = validatorsService.userValidator();
            if (userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                return userValidator;
            } else {
                List<AccountRepository.AccountsByAccountType> accounts = accountRepository.findByAccountTypeAndDeleteFlag(CONSTANTS.LOAN_ACCOUNT, CONSTANTS.NO);
                EntityResponse listChecker = validatorsService.listLengthChecker(accounts);
                return listChecker;
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<List<AccountRepository.AccountsByAccountType>> savingAccounts() {
        try {
            EntityResponse userValidator = validatorsService.userValidator();
            if (userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                return userValidator;
            } else {
                List<AccountRepository.AccountsByAccountType> accounts = accountRepository.findByAccountTypeAndDeleteFlag(CONSTANTS.SAVINGS_ACCOUNT, CONSTANTS.NO);
                EntityResponse listChecker = validatorsService.listLengthChecker(accounts);
                return listChecker;
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<List<Account>> getByCustomerCode(String customerCode) {
        try {
            EntityResponse userValidator = validatorsService.userValidator();
            if (userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                return userValidator;
            } else {
                List<Account> accounts = accountRepository.findByCustomerCodeAndDeleteFlag(customerCode, CONSTANTS.NO);
                EntityResponse listChecker = validatorsService.listLengthChecker(accounts);
                return listChecker;

            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<List<Account>> getByCustomerCodeAndProductCode(String customerCode, String productCode) {
        try {
            EntityResponse userValidator = validatorsService.userValidator();
            if (userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                return userValidator;
            } else {
                List<Account> accounts = accountRepository.findByCustomerCodeAndProductCodeAndDeleteFlag(customerCode, productCode, CONSTANTS.NO);
                EntityResponse listChecker = validatorsService.listLengthChecker(accounts);
                return listChecker;

            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<List<Account>> findAllVerifidAccounts() {
        try {
            List<Account> accounts = accountRepository.findByVerifiedFlag(CONSTANTS.YES);
            if (accounts.size() > 0) {
                return new EntityResponse<>(RESPONSEMESSAGES.RECORD_FOUND, accounts, HttpStatus.OK.value());
            } else {
                return new EntityResponse<>(RESPONSEMESSAGES.RECORDS_NOT_FOUND, null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.info("Caught Error {}" + e);
            return null;
        }
    }

    //TODO: VERIFY
    public EntityResponse<?> verifyCreatedAccount(String acid) {
        try {
            EntityResponse userValidator = validatorsService.userValidator();
            if (userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                return userValidator;
            } else {
                Optional<Account> account = accountRepository.findByAcidAndDeleteFlag(acid, CONSTANTS.NO);
                if (account.isPresent()) {
                    Account existingAccount = account.get();

                    if (existingAccount.getVerifiedFlag().equals(CONSTANTS.YES)) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage(RESPONSEMESSAGES.ACCOUNT_ALREADY_VERIFIED);
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                        return response;
                    } else {
                        if (existingAccount.getPostedBy().equalsIgnoreCase(UserRequestContext.getCurrentUser())) {
                            EntityResponse response = new EntityResponse();
                            response.setMessage(RESPONSEMESSAGES.VERIFICATION_ERROR);
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            response.setEntity("");
                            return response;
                        } else {
                            String verifiedBy = UserRequestContext.getCurrentUser();
                            if (existingAccount.getAccountType().equals(CONSTANTS.TERM_DEPOSIT)) {
                                System.out.println("Before call verifyTermDepositAccount");
                                return termDepositService.verifyTermDepositAccount(
                                        existingAccount,
                                        verifiedBy);
                            } else {
                                Date d = new Date();
                                Date feeDate = datesCalculator.addDate(d, 1, "MONTHS");
//                                existingAccount.setLedgerFeesColectionDate(feeDate);
                                existingAccount.setVerifiedBy(verifiedBy);
                                existingAccount.setVerifiedTime(LocalDate.now().toDate());
                                existingAccount.setVerifiedFlag(CONSTANTS.YES);
                                accountRepository.save(existingAccount);

                                EntityResponse response = new EntityResponse();
                                response.setMessage("ACCOUNT VERIFIED SUCCESSFULLY");
                                response.setStatusCode(HttpStatus.OK.value());
                                response.setEntity("");
                                return response;
                            }

                        }
                    }
                } else {
                    EntityResponse response = new EntityResponse();
                    response.setMessage(RESPONSEMESSAGES.RECORDS_NOT_FOUND);
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setEntity("");
                    return response;
                }
            }
        } catch (Exception e) {
            log.info("Caught Error {}" + e);
            return null;
        }
    }

    //TODO: REJECT
    public EntityResponse<?> rejectAccount(String acid) {
        System.out.println("HERE IN REJECT");
        try {
            EntityResponse userValidator = validatorsService.userValidator();
            if (userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                return userValidator;
            } else {
                EntityResponse response = new EntityResponse();
                Optional<Account> searchAcid = accountRepository.findByAcidAndDeleteFlag(acid, CONSTANTS.NO);
                if (searchAcid.isPresent()) {
                    Account account = searchAcid.get();
                    if (account.getVerifiedFlag().equals('Y')) {
                        response.setMessage("Account " + searchAcid.get().getAcid() + " Already Verified. You Can Not REJECT an Account That has been Verified: !!");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                    } else {
                        if (!account.getPostedBy().equalsIgnoreCase(UserRequestContext.getCurrentUser())) {
                            response.setMessage("Just Any Message");
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            response.setEntity("");
                        } else {
                            if (searchAcid.get().getRejectedFlag().equals('Y')) {
                                response.setMessage("Account " + searchAcid.get().getAcid() + " Already REJECTED at " + searchAcid.get().getRejectedTime());
                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                response.setEntity("");
                            } else {
                                account.setRejectedBy(UserRequestContext.getCurrentUser());
                                account.setRejectedTime(new Date());
                                account.setRejectedFlag(CONSTANTS.YES);
                                account.setAccountStatus("REJECTED");
                                account.setVerifiedFlag('J');
                                Account rejectAccount = accountRepository.save(account);
                                response.setMessage("Account " + searchAcid.get().getAcid() + " REJECTED Successfully at " + new Date());
                                response.setStatusCode(HttpStatus.OK.value());
                                response.setEntity("");
                                return response;
                            }
                        }
                    }
                } else {
                    response.setMessage("Account " + acid + " Not Found: !!");
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setEntity("");
                }
                return response;
            }
        } catch (Exception e) {
            log.info("Caught Error {}" + e);
            return null;
        }
    }

    //TODO: ACTIVATE
    public EntityResponse<?> activateSavingAccount(String acid) {
        try {
            EntityResponse userValidator = validatorsService.userValidator();
            if (userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                return userValidator;
            } else {
                Optional<Account> account = accountRepository.findByAcidAndDeleteFlag(acid, CONSTANTS.NO);
                if (account.isPresent()) {
                    Account existingAccount = account.get();

                    if (existingAccount.getAccountStatus().equalsIgnoreCase(CONSTANTS.ACTIVE)) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("Account is already active");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                        return response;
                    } else {
                        {
                            String verifiedBy = UserRequestContext.getCurrentUser();
                            if (existingAccount.getAccountType().equals(CONSTANTS.SAVINGS_ACCOUNT)) {
                                Date d = new Date();
                                Date feeDate = datesCalculator.addDate(d, 1, "MONTHS");
//                                existingAccount.setLedgerFeesColectionDate(feeDate);
                                existingAccount.setVerifiedBy(verifiedBy);
                                existingAccount.setVerifiedTime(LocalDate.now().toDate());
                                existingAccount.setVerifiedFlag(CONSTANTS.YES);

                                existingAccount.setAccountStatus(CONSTANTS.ACTIVE);

                                accountRepository.save(existingAccount);
                                EntityResponse response = new EntityResponse();
                                response.setMessage("ACCOUNT ACTIVATED SUCCESSFULLY");
                                response.setStatusCode(HttpStatus.OK.value());
                                response.setEntity("");
                                return response;
                            }

                        }
                    }
                } else {
                    EntityResponse response = new EntityResponse();
                    response.setMessage(RESPONSEMESSAGES.RECORDS_NOT_FOUND);
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setEntity("");
                    return response;
                }
            }
        } catch (Exception e) {
            log.info("Caught Error {}" + e);
            return null;
        }
        return null;
    }


    public EntityResponse<List<Account>> fetchByAccountTypeAndCustomerTypeAndSolCodeAndAccountStatus(String accountType,
                                                                                                     String customerType,
                                                                                                     String solCode,
                                                                                                     String accountStatus) {
        try {
            EntityResponse userValidator = validatorsService.userValidator();
            if (userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                return userValidator;
            } else {
                List<Account> accounts = accountRepository.findByAccountTypeAndCustomerTypeAndSolCodeAndAccountStatusAndDeleteFlag(accountType, customerType, solCode, accountStatus, CONSTANTS.NO);
                EntityResponse listChecker = validatorsService.listLengthChecker(accounts);
                return listChecker;
            }
        } catch (Exception e) {
            log.info("Caught Error {}" + e);
            return null;
        }
    }

    public EntityResponse<List<?>> generalAccountsLookUp(String accountType,
                                                               String customerType,
                                                               String solCode,
                                                               String accountStatus,
                                                               String customerCode,
                                                               String nationalId,
                                                               String acid,
                                                               String accountName) {
        EntityResponse res = new EntityResponse();
        List<Account> accountList = new ArrayList<>();
        res.setStatusCode(HttpStatus.NOT_FOUND.value());
        res.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
        try {
            if (nationalId != null && !nationalId.trim().isEmpty()) {
                List<Account> accounts = accountRepository.findByNationalIdAndDeleteFlag(nationalId.trim(), CONSTANTS.NO);
                res = validatorsService.listLengthChecker(accounts);

            } else {
                List<SearchRequestDto> searchRequestDto = new ArrayList<>();
                boolean isAccountTypeOnly = true;
                //dynamic query
//                String start="FROM Account WHERE ";
                if (acid != null) {
                    if (!acid.trim().isEmpty()) {
                        SearchRequestDto sd = new SearchRequestDto();
                        sd.setColumnName("acid");
                        sd.setValue(acid.trim());
                        searchRequestDto.add(sd);
                        isAccountTypeOnly = false;
//                    start=start+"acid='"+acid+"' AND ";
                    }

                }
                if (customerCode != null) {
                    if (!customerCode.trim().isEmpty()) {
                        SearchRequestDto sd = new SearchRequestDto();
                        sd.setColumnName("customerCode");
                        sd.setValue(customerCode.trim());
                        searchRequestDto.add(sd);
                        isAccountTypeOnly = false;
//                    start=start+"customer_code='"+customerCode+"' AND ";
                    }
                }
                if (accountType != null) {
                    if (!accountType.trim().isEmpty()) {
                        SearchRequestDto sd = new SearchRequestDto();
                        sd.setColumnName("accountType");
                        sd.setValue(accountType.trim());
                        searchRequestDto.add(sd);
//                    start=start+"account_type='"+accountType+"' AND ";
                    }

                }
                if (customerType != null) {
                    if (!customerType.trim().isEmpty()) {
                        SearchRequestDto sd = new SearchRequestDto();
                        sd.setColumnName("customerType");
                        sd.setValue(customerType.trim());
                        searchRequestDto.add(sd);
                        isAccountTypeOnly = false;
//                    start=start +"customer_type='"+customerType+"' AND ";
                    }
                }
                if (solCode != null) {
                    if (!solCode.trim().isEmpty()) {
                        SearchRequestDto sd = new SearchRequestDto();
                        sd.setColumnName("solCode");
                        sd.setValue(solCode.trim());
                        searchRequestDto.add(sd);
                        //      start=start+"sol_code='"+solCode+"' AND ";
                    }
                }
                if (accountStatus != null) {
                    if (!accountStatus.trim().isEmpty()) {
                        SearchRequestDto sd = new SearchRequestDto();
                        sd.setColumnName("accountStatus");
                        sd.setValue(accountStatus.trim());
                        searchRequestDto.add(sd);
                        isAccountTypeOnly = false;
//                    start=start+"account_status='"+accountStatus+"' AND ";
                    }
                }

                if (accountName != null) {
                    if (!accountName.trim().isEmpty()) {
                        SearchRequestDto sd = new SearchRequestDto();
                        sd.setColumnName("accountName");
                        sd.setValue(accountName.trim());
                        searchRequestDto.add(sd);
                        isAccountTypeOnly = false;
//                    start=start+"account_status='"+accountStatus+"' AND ";
                    }
                }

//                start=start.substring(0, start.length() - 4);

//                System.out.println("The query is "+start);
                if (!isAccountTypeOnly) {
                    Specification<Account> specifications = filtersSpecification.getSearchSpecification(searchRequestDto);
                    System.out.println(specifications.toString());
                    List<Account> accounts = accountRepository.findAll(specifications);
//                List<Account> accounts= accountRepositoryImpl.findUsingNativeQuery(start);
                    res = validatorsService.listLengthChecker(accounts);
                } else {
                    System.out.println("Find by account type");
                    System.out.println(accountType.trim());
                    List<AccountLookUpInterfaceASIS> accounts = accountRepository.findByAccountType(accountType.trim());
//                List<Account> accounts= accountRepositoryImpl.findUsingNativeQuery(start);
                    res = validatorsService.listLengthChecker(accounts);
                }
            }

            return res;
        } catch (Exception e) {
            log.info("Caught Error {}" + e);
            return null;
        }
    }

    //TODO: GENERAL ACCOUNTS LOOKUP

    public EntityResponse<List<AccountLookUpInterface>> generalAccountDetailsLookUp(
            String accountType, String customerType, String solCode, String accountStatus,
            String customerCode, String nationalId, String acid, String fromDate,
            String toDate) {
        try {
            EntityResponse userValidator = validatorsService.userValidator();
            if (userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                return userValidator;
            } else {
                List<AccountLookUpInterface> accountLookUpInterfaceArrayList = new ArrayList<>();
                if (!nationalId.equals("") && nationalId != null) {
                    accountLookUpInterfaceArrayList = accountRepository.findAccountByEntityIdAndNationalId(EntityRequestContext.getCurrentEntityId(), nationalId);
                } else if (!accountType.equals("") && accountType != null) {
                    accountLookUpInterfaceArrayList = accountRepository.findAllByEntityIdAndAccountType(EntityRequestContext.getCurrentEntityId(), accountType);
                } else if (!customerType.equals("") && customerType != null) {
                    accountLookUpInterfaceArrayList = accountRepository.findAllByEntityIdAndCustomerType(EntityRequestContext.getCurrentEntityId(), customerType);
                } else if (!solCode.equals("") && solCode != null) {
                    accountLookUpInterfaceArrayList = accountRepository.findAllByEntityIdAndSolCode(EntityRequestContext.getCurrentEntityId(), solCode);
                } else if (!accountStatus.equals("") && accountStatus != null) {
                    accountLookUpInterfaceArrayList = accountRepository.findAllByEntityIdAndAccountStatus(EntityRequestContext.getCurrentEntityId(), accountStatus, accountType);
                } else if (!acid.equals("") && acid != null) {
                    accountLookUpInterfaceArrayList = accountRepository.findAllByEntityIdAndAcid(EntityRequestContext.getCurrentEntityId(), acid);
                } else if (!customerCode.equals("") && customerCode != null) {
                    accountLookUpInterfaceArrayList = accountRepository.findAllByEntityIdAndCustomerCode(EntityRequestContext.getCurrentEntityId(), customerCode);
                } else if (!fromDate.equals(null) || !toDate.equals(null) && !fromDate.equals("") || !toDate.equals("")) {
                    accountLookUpInterfaceArrayList = accountRepository.findAllByEntityIdAndDateRange(EntityRequestContext.getCurrentEntityId(), fromDate, toDate);
                }
                EntityResponse listChecker = validatorsService.listLengthChecker(accountLookUpInterfaceArrayList);
                return listChecker;
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    //TODO: FETCH IMAGES BY ACID
    public EntityResponse<List<Account>> getAccountImagesByAccountId(Long account_id_fk) {
        try {
            EntityResponse userValidator = validatorsService.userValidator();
            if (userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                return userValidator;
            } else {
                List<AccountRepository.accountDocuments> accountDocumentsList = accountRepository.findByAccountDocuments(account_id_fk);
                EntityResponse listChecker = validatorsService.listLengthChecker(accountDocumentsList);
                return listChecker;
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<List<Account>> fetchByAccountTypeAndCustomerTypeAndSolCodeAndAccountStatusAndIsWithdawalAllowed(String accountType, String customerType, String solCode, String accountStatus) {
        try {
            EntityResponse userValidator = validatorsService.userValidator();
            if (userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                return userValidator;
            } else {
                List<Account> accounts = accountRepository.findByAccountTypeAndCustomerTypeAndSolCodeAndAccountStatusAndDeleteFlagAndIsWithdrawalAllowed(accountType, customerType, solCode, accountStatus, CONSTANTS.NO, true);
                EntityResponse listChecker = validatorsService.listLengthChecker(accounts);
                return listChecker;
            }
        } catch (Exception e) {
            log.info("Caught Error {}" + e);
            return null;
        }
    }

    //TODO: ALL RETAIL ACCOUNTS
    public EntityResponse<List<Account>> findBySolCodeAndAccountType(String solCode, String accountType) {
        try {
            EntityResponse userValidator = validatorsService.userValidator();
            if (userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                return userValidator;
            } else {
                List<Account> retailAccounts = accountRepository.findBySolCodeAndAccountTypeAndVerifiedFlagAndDeleteFlag(solCode, accountType, CONSTANTS.YES, CONSTANTS.NO);
                EntityResponse listChecker = validatorsService.listLengthChecker(retailAccounts);
                return listChecker;
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    //TODO: ALL BY PRODUCT CODE
    public EntityResponse<List<Account>> findByProductCode(String productCode) {
        try {
            EntityResponse userValidator = validatorsService.userValidator();
            if (userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                return userValidator;
            } else {
                List<Account> retailAccounts = accountRepository.findByProductCodeAndDeleteFlag(productCode, CONSTANTS.NO);
                EntityResponse listChecker = validatorsService.listLengthChecker(retailAccounts);
                return listChecker;
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<?> checkIfAccountExists(String acid) {
        try {
            EntityResponse userValidator = validatorsService.userValidator();
            if (userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                return userValidator;
            } else {
                Optional<Account> account = accountRepository.findByAcidAndDeleteFlag(acid, CONSTANTS.NO);
                if (account.isPresent()) {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("true");
                    response.setStatusCode(HttpStatus.OK.value());
                    return response;
                } else {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("false");
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    return response;
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<?> accountsCount() {
        try {
            EntityResponse response = new EntityResponse();
            EntityResponse userValidator = validatorsService.userValidator();
            if (userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                return userValidator;
            } else {
                Integer accountList = accountRepository.countAllAccountsByAcid();
                if (accountList > 0) {
                    response.setMessage("Total Unverified Accounts are " + accountList);
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(accountList);
                } else {
                    response.setMessage("Total Unverified Accounts are " + accountList);
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                }
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<?> permanetDelete(String acid) {
        try {
            EntityResponse userValidator = validatorsService.userValidator();
            if (userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                return userValidator;
            } else {
                Optional<Account> account = accountRepository.findByAcid(acid);
                if (account.isPresent()) {
                    Account existingAccount = account.get();
                    accountRepository.delete(existingAccount);

                    EntityResponse response = new EntityResponse();
                    response.setMessage("Deleted successfully");
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity("");
                    return response;
                } else {
                    EntityResponse response = new EntityResponse();
                    response.setMessage(RESPONSEMESSAGES.RECORDS_NOT_FOUND);
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setEntity("");
                    return response;
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public List<EntityResponse> deleteAllLoanAccountsForMigration() {
        try {
            List<EntityResponse> ls = new ArrayList<>();


            List<AccountRepository.LoanAcid> acidLists = accountRepository.getLoanAcids();
            log.info(" Count records :: " + acidLists.size());
            if (acidLists.size() > 0) {
                acidLists.forEach(acidList -> {
                    ls.add(permanetDelete(acidList.getacid()));
                });
            }
            return ls;

        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public List<EntityResponse> deleteAllLoanAccountsForMigrationFr() {
        try {
            List<EntityResponse> ls = new ArrayList<>();


            List<AccountRepository.LoanAcid> acidLists = accountRepository.getLoanAcids2();
            log.info(" Count records :: " + acidLists.size());
            if (acidLists.size() > 0) {
                acidLists.forEach(acidList -> {
                    ls.add(permanetDelete(acidList.getacid()));
                });
            }
            return ls;

        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }


    public EntityResponse<?> permanetDeleteAcType(String acType) {
        try {
            EntityResponse userValidator = validatorsService.userValidator();
            if (userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                return userValidator;
            } else {
//                List<Account> accountList = accountRepository.findByAccountType(acType);
                List<Account> accountList = new ArrayList<>();
                log.info("****size--" + accountList.size());
                if (accountList.size() > 0) {
                    for (Integer i = 0; i < accountList.size(); i++) {
                        log.info("****deleting");
                        Account account = accountList.get(i);
                        accountRepository.delete(account);
                        log.info("****deleted");
                    }

                    EntityResponse response = new EntityResponse();
                    response.setMessage("Deleted successfully");
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity("");
                    return response;
                } else {
                    EntityResponse response = new EntityResponse();
                    response.setMessage(RESPONSEMESSAGES.RECORDS_NOT_FOUND);
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setEntity("");
                    return response;
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<List<AccountLookUpInterface>> getAccountForLookUp(String acid, String nationalId, String customerCode) {
        try {
            EntityResponse userValidator = validatorsService.userValidator();
            if (userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                return userValidator;
            } else {
                List<AccountLookUpInterface> accountList = new ArrayList<>();
                if (!acid.equals("") && acid != null) {
                    accountList = accountRepository.getAccountLookUpByAccountNumber(acid);
                } else if (!nationalId.equals("") && nationalId != null) {
                    accountList = accountRepository.getAccountLookUpByNationalId(nationalId);
                } else if (!customerCode.equals("") && customerCode != null) {
                    accountList = accountRepository.getAccountLookUpByCustomerCode(customerCode);
                }

                EntityResponse listChecker = validatorsService.listLengthChecker(accountList);
                return listChecker;
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<List<AccountLookUpInterface>> getAccountForLookUpByAccountType(String acid, String nationalId, String customerCode, String accountType, String fromDate, String toDate) {
        try {
            EntityResponse userValidator = validatorsService.userValidator();
            if (userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                return userValidator;
            } else {
                List<AccountLookUpInterface> accountList = new ArrayList<>();
                if (!acid.equals("") && acid != null) {
                    accountList = accountRepository.getAccountLookUpByAccountNumberAndAccountType(acid, accountType);
                } else if (!nationalId.equals("") && nationalId != null) {
                    accountList = accountRepository.getAccountLookUpByNationalIdAndAccountType(nationalId, accountType);
                } else if (!customerCode.equals("") && customerCode != null) {

//                    accountList = accountRepository.findAllByEntityIdAndAccountTypeAndCustomerCode(EntityRequestContext.getCurrentEntityId(), customerCode, accountType);

                }else if (!fromDate.equals("")  && fromDate != null || !toDate.equals("")  && toDate != null)  {
                    accountList = accountRepository.getAccountLookUpByAccountTypeAndDateRange(EntityRequestContext.getCurrentEntityId(), accountType, fromDate, toDate);
                }
                EntityResponse listChecker = validatorsService.listLengthChecker(accountList);
                return listChecker;
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    //get office account specific details
    public EntityResponse<OfficeAccount> getOfficeAccountSpecificDetails(String acid) {
        try {
            EntityResponse userValidator = validatorsService.userValidator();
            if (userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                return userValidator;
            } else {
                Optional<OfficeAccount> officeAccount = officeAccountRepository.getOfficeAccountSpecificDetails(acid);
                if (officeAccount.isPresent()) {
                    OfficeAccount officeAccount1 = officeAccount.get();
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                    response.setStatusCode(HttpStatus.FOUND.value());
                    response.setEntity(officeAccount1);
                    return response;
                } else {
                    EntityResponse response = new EntityResponse();
                    response.setMessage(RESPONSEMESSAGES.RECORDS_NOT_FOUND);
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setEntity("");
                    return response;
                }
            }

        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

//    public SavingsAccountProduct getSavingProdDetails(String productCode) {
//        try {
//            SavingsAccountProduct s = null;
//            EntityResponse res = itemServiceCaller.getSavingsAccountProductDetails(productCode);
//            if (res.getStatusCode() == HttpStatus.OK.value()) {
//                s = (SavingsAccountProduct) res.getEntity();
//            }
//            System.out.println("sss" + s.toString());
//            log.info("The product code is :::: " + productCode);
//            return s;
//        } catch (Exception e) {
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }

    public EntityResponse requestAcidApproval(String acid, String approver) {
        try {
            EntityResponse response = new EntityResponse();
            String user = UserRequestContext.getCurrentUser();
            String entityId = EntityRequestContext.getCurrentEntityId();
            if (user.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return response;
            } else if (entityId.isEmpty()) {
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;
            } else if (user.equalsIgnoreCase(approver)) {
                response.setMessage("You cannot send approval request to yourself");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;
            } else {
                Optional<Account> searchAcid = accountRepository.findByAcid(acid);
                if (searchAcid.isPresent()) {
                    if (searchAcid.get().getVerifiedFlag() == 'Y') {
                        response.setMessage("Already approved");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        return response;
                    } else {
                        Account account = searchAcid.get();
                        account.setApprovalSentBy(user);
                        account.setApprovalSentTo(approver);
                        account.setApprovalSentFlag('Y');
                        account.setApprovalSentTime(new Date());
                        Account saveApproval = accountRepository.save(account);
                        response.setMessage("Account Approval Request Sent Successfully To: " + approver + " At " + new Date());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(saveApproval);
                    }
                } else {
                    response.setMessage("Account With Acid  " + acid + " Not Registered With our System: !!");
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

    public EntityResponse<List<AccountLookUpInterface>> getApprovalList(String accountType) {
        EntityResponse res = new EntityResponse();
        List<Account> accountList = new ArrayList<>();
        res.setStatusCode(HttpStatus.NOT_FOUND.value());
        res.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
        try {
            List<AccountLookUpInterface> accounts = accountRepository.getApprovalList(accountType);
            res = validatorsService.listLengthChecker(accounts);

            return res;
        } catch (Exception e) {
            log.info("Caught Error {}" + e);
            return null;
        }
    }
    public EntityResponse<List<AccountLookUpInterface>> getNonLoanApprovalList() {
        EntityResponse res = new EntityResponse();
        List<Account> accountList = new ArrayList<>();
        res.setStatusCode(HttpStatus.NOT_FOUND.value());
        res.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
        try {
            List<AccountLookUpInterface> accounts = accountRepository.getNonLoanApprovalList();
            res = validatorsService.listLengthChecker(accounts);

            return res;
        } catch (Exception e) {
            log.info("Caught Error {}" + e);
            return null;
        }
    }
    public EntityResponse<List<AccountLookUpInterface>> getApprovalListLoan() {
        EntityResponse res = new EntityResponse();
        res.setStatusCode(HttpStatus.NOT_FOUND.value());
        res.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
        try {
            List<LoanVerificationInterface> applications = accountRepository.getUnverifiedLoanApplications();
            List<LoanVerificationInterface> disbursements = accountRepository.getUnverifiedLoanDisbursments();
            disbursements.addAll(applications);
            res = validatorsService.listLengthChecker(disbursements);
            return res;
        } catch (Exception e) {
            log.info("Caught Error {}" + e);
            return null;
        }
    }

    public EntityResponse<List<AccountLookUpInterface>> lookUpNew(String nationalId,
                                                                  String account_type,
                                                                  String account_name,
                                                                  String customer_code,
                                                                  String acid,
                                                                  Date fromDate,
                                                                  Date toDate) {
        try {
            System.out.println(nationalId);
            System.out.println(account_type);
            System.out.println(account_name);
            System.out.println(customer_code);
            System.out.println(acid);
            System.out.println(fromDate);
            System.out.println(toDate);

            EntityResponse response= new EntityResponse<>();
            List<AccountLookUpInterface> accountLookUpInterfaces= new ArrayList<>();
                if (!nationalId.trim().isEmpty()) {
                    if (account_type != null && !account_type.isEmpty()) {
                        accountLookUpInterfaces=accountRepository.accountLookUpByNationalIdAndAccountType(nationalId, account_type);
                    }else {
                        accountLookUpInterfaces=accountRepository.accountLookUpByNationalId(nationalId);
                    }
                }else if(!acid.trim().isEmpty()){
                    accountLookUpInterfaces=accountRepository.accountLookUpByAcid(acid);
                }else if(!customer_code.trim().isEmpty()){
                    if (account_type != null && !account_type.isEmpty()){
                        accountLookUpInterfaces=accountRepository.accountLookUpByCustomerCodeAndAccountType(customer_code, account_type);
                    }else {
                        accountLookUpInterfaces=accountRepository.accountLookUpByCustomerCode(customer_code);
                    }
                } else  {
                    accountLookUpInterfaces=accountRepository.accountLookUp( nationalId, account_type, account_name, customer_code, acid, fromDate, toDate);
                }

                if (accountLookUpInterfaces.size()>0) {
                    response.setStatusCode(HttpStatus.FOUND.value());
                    response.setEntity(accountLookUpInterfaces);
                    response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                }else {
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                }
                return response;
        } catch (Exception e) {
            log.info("Caught Error {}" + e);
            return null;
        }
    }

    public EntityResponse<List<AccountLookUpInterface>> lookUpNew1(String nationalId,
                                                                  String accountType,
                                                                  String accountName,
                                                                  String customerCode,
                                                                  String acid,
                                                                   String customerType,
                                                                   String solCode,
                                                                   String accountStatus,
                                                                  Date fromDate,
                                                                  Date toDate) {
        try {
            EntityResponse response= new EntityResponse<>();
            List<AccountLookUpInterface> accountLookUpInterfaces= new ArrayList<>();
            if (!nationalId.trim().isEmpty()) {
                accountLookUpInterfaces=accountRepository.accountLookUpByNationalId(nationalId);
            }else if(!acid.trim().isEmpty()){
                accountLookUpInterfaces=accountRepository.accountLookUpByAcid(acid);
            }else if(!customerCode.trim().isEmpty()){
                accountLookUpInterfaces=accountRepository.accountLookUpByCustomerCode(customerCode);
            } else  {
                log.info("using defalt query");
                accountLookUpInterfaces=accountRepository.accountLookUp(nationalId,accountType,accountName,customerCode,acid, fromDate, toDate, customerType, solCode, accountStatus);
            }

            if(accountLookUpInterfaces.size()>0){
                response.setStatusCode(HttpStatus.FOUND.value());
                response.setEntity(accountLookUpInterfaces);
                response.setMessage(HttpStatus.FOUND.getReasonPhrase());
            }else {
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
            }
            return response;
        } catch (Exception e) {
            log.info("Caught Error {}" + e);
            return null;
        }
    }


    public EntityResponse<Account> findAccountByPhone(String accountType, String phone) {
        Optional<Account>  accountOptional = accountRepository.getAccountByAccountTypeAndPhone(accountType, phone);

        EntityResponse response= new EntityResponse<>();
        if (accountOptional.isPresent()) {
            response.setStatusCode(HttpStatus.FOUND.value());
            response.setEntity(accountOptional.get());
            response.setMessage("Found " +accountType+ " Account for "+phone);
        } else {
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setEntity(0);
            response.setMessage(accountType+" Account for "+phone+ " not found");
        }
        return response;
    }


    public EntityResponse<Account> retrieveAccount(String nationalId, String customerCode) {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                EntityResponse response = new EntityResponse();
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return response;
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return response;
                } else {
                    Optional<String> acid = accountRepository.getAccountByCustomerCodeAndNationalId(customerCode, nationalId);
                    if (acid.isPresent()) {
                        Optional<Account> account = accountRepository.findByAccountId(acid.get());
                        if (account.isPresent()) {
                            Account existingAccount = account.get();
                            EntityResponse response = new EntityResponse();
                            response.setMessage(RESPONSEMESSAGES.SUCCESS);
                            response.setStatusCode(HttpStatus.FOUND.value());
                            response.setEntity(existingAccount);
                            return response;
                        } else {
                            EntityResponse response = new EntityResponse();
                            response.setMessage(RESPONSEMESSAGES.RECORD_NOT_FOUND);
                            response.setStatusCode(HttpStatus.NOT_FOUND.value());
                            response.setEntity("");
                            return response;
                        }
                    }else  {
                        EntityResponse response = new EntityResponse();
                        response.setMessage(RESPONSEMESSAGES.RECORD_NOT_FOUND);
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity("");
                        return response;
                    }
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
}
