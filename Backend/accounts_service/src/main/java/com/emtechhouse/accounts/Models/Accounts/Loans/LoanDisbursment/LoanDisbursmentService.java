package com.emtechhouse.accounts.Models.Accounts.Loans.LoanDisbursment;

import com.emtechhouse.accounts.Models.Accounts.Account;
import com.emtechhouse.accounts.Models.Accounts.AccountDtos.SmsDto;
import com.emtechhouse.accounts.Models.Accounts.AccountRepository;
import com.emtechhouse.accounts.Models.Accounts.AccountServices.AccountTransactionService;
import com.emtechhouse.accounts.Models.Accounts.Loans.Loan;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanCalculatorService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanFees.LoanFees;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanRepository;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ProductFees.ProductFeeAmount;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ProductFees.ProductFeesService;
import com.emtechhouse.accounts.Models.Accounts.Validators.ValidatorService;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans.PartTran;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TransactionHeader;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TransactionsController;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeaderHistory.PartTransHistory.PartTranHIstoryRepository;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.DatesCalculator;
import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import com.emtechhouse.accounts.Utils.ServiceCaller;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

;

@Service
@Slf4j
public class LoanDisbursmentService {
    @Autowired
    private PartTranHIstoryRepository partTranHIstoryRepository;
    @Autowired
    private ValidatorService validatorsService;

    @Autowired
    private LoanCalculatorService loanCalculatorService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private AccountTransactionService accountTransactionService;
    @Autowired
    private ProductFeesService productFeesService;
    @Autowired
    private DatesCalculator datesCalculator;

    @Autowired
    private LoanDisbursmentInfoRepo loanDisbursmentInfoRepo;
    @Autowired
    private ServiceCaller serviceCaller;

    @Autowired
    TransactionsController transactionsController;

    //loan disbursment
    public EntityResponse disburseLoan(String acid) {
        try {
            EntityResponse userValidator = validatorsService.userValidator();
            if (userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                return userValidator;
            } else {
                //verify if loan account is verified
                EntityResponse acidValidator = validatorsService.acidValidator(acid, "Loan account");
                if (acidValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                    return acidValidator;
                } else {
                    Account account = accountRepository.findByAcid(acid).orElse(null);
                    String accountType = account.getAccountType();
                    if (accountType.equals(CONSTANTS.LOAN_ACCOUNT)) {
                        String user = UserRequestContext.getCurrentUser();
                        Loan loan = account.getLoan();
                        if (loan.getDisbursementFlag().equals(CONSTANTS.NO)) {
                            //check if loan has attached fees
                            Double totalFeeAmount = 0.0;
                            Double principalAmount = loan.getPrincipalAmount();
                            List<LoanFees> loanFees = loan.getLoanFees();
                            if (loanFees.size() > 0) {
                                for (Integer i = 0; i < loanFees.size(); i++) {
                                    totalFeeAmount = totalFeeAmount + loanFees.get(i).getInitialAmt();
                                }
                            } else {
                                totalFeeAmount = 0.0;
                            }
//                            Double totalDisbursedAmount= principalAmount-totalFeeAmount;

                            Double totalDisbursedAmount = principalAmount;

                            loanRepository.updateDisbursementAmount(totalDisbursedAmount, loan.getSn());

                            loanRepository.updateFeesAmount(totalFeeAmount, loan.getSn());

                            loanRepository.updateDisbursementFlag(CONSTANTS.YES, loan.getSn());

                            loanRepository.updateDisbursementDate(new Date(), loan.getSn());

                            loanRepository.updateDisbursementBy(user, loan.getSn());
                            loanRepository.updateLoanStatus(CONSTANTS.APPROVED, loan.getSn());

                            EntityResponse response = new EntityResponse<>();
                            response.setMessage("LOAN DISBURSED SUCCESSFULLY! WAITING VERIFICATION");
                            response.setStatusCode(HttpStatus.OK.value());
                            return response;
                        } else {
                            EntityResponse response = new EntityResponse<>();
                            response.setMessage("INVALID! LOAN WAS DISBURSED ON: " + loan.getDisbursementDate().toString());
                            response.setStatusCode(HttpStatus.OK.value());
                            return response;
                        }
                    } else {
                        return validatorsService.invalidAccountTypeMessage();
                    }
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    //disburse loan transaction

    public List<EntityResponse> verifyLoanDisbursment(String acid) {
        try {
            List<EntityResponse> entityResponseList = new ArrayList<>();
            EntityResponse userValidator = validatorsService.userValidator();
            if (userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                entityResponseList.add(userValidator);

            } else {
                Account loanAccount = accountRepository.findByAcid(acid).orElse(null);
                String accountType = loanAccount.getAccountType();
                if (accountType.equals(CONSTANTS.LOAN_ACCOUNT)) {
                    if (loanAccount.getLoan().getDisbursementFlag().equals(CONSTANTS.YES)) {
                        if (loanAccount.getLoan().getDisbursementVerifiedFlag().equals(CONSTANTS.NO)) {
                            //disbursed by
                            String disbursedBy = loanAccount.getLoan().getDisbursedBy();
                            String user = UserRequestContext.getCurrentUser();
                            if (!user.equals(disbursedBy)) {
                                //TRANSACTION
                                //CREDIT CUSTOMER OPERTIVE ACCOUNT
                                //DEBIT LOAN ACCOUNT
                                Loan loan = loanAccount.getLoan();
                                String loanDisbursmentAccount = loanAccount.getLoan().getDisbursmentAccount();
                                String loanCurrency = loanAccount.getCurrency();
                                Double disbursmentAmount = loan.getDisbursementAmount();
                                String drAc = loanAccount.getAcid();

                                //validate operative account
                                EntityResponse operativeAcValidator = validatorsService.acidValidator(loanDisbursmentAccount, "Customer disbursement account");
                                if (operativeAcValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                                    entityResponseList.add(operativeAcValidator);
                                } else {
                                    //validate all fee collection accounts
                                    EntityResponse feeCollectionAccountsValidator = validateAllFeeCollectionAccounts(loan.getLoanFees());
                                    if (!feeCollectionAccountsValidator.getStatusCode().equals(HttpStatus.OK.value())) {
                                        entityResponseList.add(feeCollectionAccountsValidator);
                                    } else {
                                        String transactionDescription = "LOAN DISBURSEMENT FOR ACID:  " + drAc;

                                        TransactionHeader tranHeader = createTransactionHeader(loanCurrency,
                                                transactionDescription,
                                                disbursmentAmount,
                                                drAc,
                                                loanDisbursmentAccount, getFeePartTrans(loan));
                                        EntityResponse transactRes = transactionsController.systemTransaction1(tranHeader).getBody();
                                        if (!transactRes.getStatusCode().equals(HttpStatus.OK.value())) {
                                            ///failed transaction
                                            //save failed transaction in disbursment report
                                            saveLoanDisbursmentInfo(loanDisbursmentAccount, drAc, transactionDescription, disbursmentAmount,
                                                    CONSTANTS.FAILED, CONSTANTS.NOT_FOUND, loan);
                                            EntityResponse response = new EntityResponse();
                                            response.setMessage("TRANSACTION ERROR! COULD NOT PERFORM DISBURSEMENT TRANSACTION");
                                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                            response.setEntity(tranHeader);
                                            entityResponseList.add(response);
                                        } else {
                                            String transactionCode = (String) transactRes.getEntity();
                                            saveLoanDisbursmentInfo(loanDisbursmentAccount, drAc, transactionDescription, disbursmentAmount,
                                                    CONSTANTS.SUCCUSSEFUL, transactionCode, loan);
                                            // TODO: 5/8/2023 collect fees

                                            loanRepository.updateDisbursementVerifiedFlag(CONSTANTS.YES, loan.getSn());

                                            loanRepository.updateDisbursmentVerifiedOn(new Date(), loan.getSn());

                                            loan.setDisbursmentVerifiedBy(user);
                                            loanRepository.updateDisbursmentVerifiedBy(user, loan.getSn());

                                            loanRepository.updateLoanStatus(CONSTANTS.DISBURSED, loan.getSn());

                                            // TODO: 5/22/2023  send a disbursement message
                                            String customerCode = loanAccount.getCustomerCode();
                                            String userName = UserRequestContext.getCurrentUser();
                                            String entityId = EntityRequestContext.getCurrentEntityId();
                                            EntityResponse response = serviceCaller.getCustomerPhone_and_Email(customerCode, userName, entityId);
                                            if (response.getStatusCode() == HttpStatus.OK.value()) {
                                                JSONObject jo = new JSONObject(response);
                                                JSONObject ety = jo.getJSONObject("entity");
                                                String phoneNumber = ety.getString("phoneNumber");
                                                if (phoneNumber != null && phoneNumber.length() == 12) {
//
//                                                    String message = "Dear " + loanAccount.getAccountName() + ", your loan of Ksh " + disbursmentAmount + " has been disbursed to your account " + loanAccount.getLoan().getOperativeAcountId() + " ON " + new Date();
                                                    String name = loanAccount.getAccountName();
                                                    name = name.contains(" ") ? name.split(" ")[0] : name;
                                                    String message = "Dear " + name + ", your loan of Ksh " + disbursmentAmount + " has been disbursed to your account  " + loanAccount.getLoan().getOperativeAcountId() + " ON " + datesCalculator.dateFormat(new Date());
                                                    SmsDto smsDto = new SmsDto();
                                                    smsDto.setMsisdn(phoneNumber);
                                                    smsDto.setText(message);
                                                    serviceCaller.sendSmsEmt(smsDto);
                                                } else {

                                                }
                                            }

                                            response = new EntityResponse();
                                            response.setMessage("SUCCESSFULLY DISBURSED " + disbursmentAmount.toString() + " TO ACCOUNT ID: " + loanDisbursmentAccount);
                                            response.setStatusCode(HttpStatus.CREATED.value());
                                            response.setEntity(tranHeader);
                                            entityResponseList.add(response);
                                        }
                                    }
                                }
                            } else {
                                EntityResponse response = new EntityResponse();
                                response.setMessage("VERIFICATION SHOULD BE DONE BY ANOTHER USER");
                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                entityResponseList.add(response);
                            }
                        } else {
                            EntityResponse response = new EntityResponse();
                            response.setMessage("FAILED! LOAN DISBURSEMENT WAS VERIFIED ON " + loanAccount.getLoan().getDisbursmentVerifiedOn().toString());
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            entityResponseList.add(response);
                        }

                    } else {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("LOAN MUST BE DISBURSED BEFORE VERIFICATION");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        entityResponseList.add(response);
                    }


                } else {
                    entityResponseList.add(validatorsService.invalidAccountTypeMessage());
                }
            }
            return entityResponseList;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }



    public List<EntityResponse> verifyMobileLoanDisbursment(String acid) {
        try {
            System.out.println("Starting to verify");
            List<EntityResponse> entityResponseList = new ArrayList<>();
            EntityResponse userValidator = validatorsService.userValidator();
            if (userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                entityResponseList.add(userValidator);

            } else {
                Account loanAccount = accountRepository.findByAcid(acid).orElse(null);
                String accountType = loanAccount.getAccountType();
                log.info("Account Type: "+accountType);
                if (accountType.equals(CONSTANTS.LOAN_ACCOUNT)) {
                    if (loanAccount.getLoan().getDisbursementFlag().equals(CONSTANTS.YES)) {
                        if (loanAccount.getLoan().getDisbursementVerifiedFlag().equals(CONSTANTS.NO)) {
                            //disbursed by
                            String disbursedBy = loanAccount.getLoan().getDisbursedBy();
                            String user = UserRequestContext.getCurrentUser();
                            if (!user.equals(disbursedBy)) {
                                //TRANSACTION
                                //CREDIT CUSTOMER OPERTIVE ACCOUNT
//                                DEBIT LOAN ACCOUNT
                                Loan loan = loanAccount.getLoan();

                                String loanDisbursmentAccount = loanAccount.getLoan().getDisbursmentAccount();
                                String loanCurrency = loanAccount.getCurrency();
                                Double disbursmentAmount = loan.getDisbursementAmount();
                                String drAc = loanAccount.getAcid();

                                //validate operative account
                                EntityResponse operativeAcValidator = validatorsService.acidValidator(loanDisbursmentAccount, "Customer disbursement account");
                                if (operativeAcValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                                    entityResponseList.add(operativeAcValidator);
                                } else {
                                    //validate all fee collection accounts
                                    EntityResponse feeCollectionAccountsValidator = validateAllFeeCollectionAccounts(loan.getLoanFees());
                                    if (!feeCollectionAccountsValidator.getStatusCode().equals(HttpStatus.OK.value())) {
                                        entityResponseList.add(feeCollectionAccountsValidator);
                                    } else {
                                        String transactionDescription = "LOAN DISBURSEMENT FOR ACID:  " + drAc;
                                        log.info("debit Acc: "+drAc);

                                        TransactionHeader tranHeader = createTransactionHeader(loanCurrency,
                                                transactionDescription,
                                                disbursmentAmount,
                                                drAc,
                                                loanDisbursmentAccount, getFeePartTrans(loan));
                                        log.info("Disbursement Amount: "+disbursmentAmount);
//                                        log.info("Partran Json: "+getFeePartTrans(loan));
                                        EntityResponse transactRes = transactionsController.systemTransaction1(tranHeader).getBody();
                                        if (!transactRes.getStatusCode().equals(HttpStatus.OK.value())) {
                                            ///failed transaction
                                            //save failed transaction in disbursment report
                                            saveLoanDisbursmentInfo(loanDisbursmentAccount, drAc, transactionDescription, disbursmentAmount,
                                                    CONSTANTS.FAILED, CONSTANTS.NOT_FOUND, loan);
                                            EntityResponse response = new EntityResponse();
                                            response.setMessage("TRANSACTION ERROR! COULD NOT PERFORM DISBURSEMENT TRANSACTION");
                                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                            response.setEntity(tranHeader);
                                            entityResponseList.add(response);
                                        } else {
                                            String transactionCode = (String) transactRes.getEntity();
                                            saveLoanDisbursmentInfo(loanDisbursmentAccount, drAc, transactionDescription, disbursmentAmount,
                                                    CONSTANTS.SUCCUSSEFUL, transactionCode, loan);
                                            // TODO: 5/8/2023 collect fees

                                            loanRepository.updateDisbursementVerifiedFlag(CONSTANTS.YES, loan.getSn());

                                            loanRepository.updateDisbursmentVerifiedOn(new Date(), loan.getSn());

                                            loan.setDisbursmentVerifiedBy(user);
                                            loanRepository.updateDisbursmentVerifiedBy(user, loan.getSn());

                                            loanRepository.updateLoanStatus(CONSTANTS.DISBURSED, loan.getSn());

                                            // TODO: 5/22/2023  send a disbursement message
                                            String customerCode = loanAccount.getCustomerCode();
                                            String userName = UserRequestContext.getCurrentUser();
                                            String entityId = EntityRequestContext.getCurrentEntityId();
                                            EntityResponse response = serviceCaller.getCustomerPhone_and_Email(customerCode, userName, entityId);
                                            if (response.getStatusCode() == HttpStatus.OK.value()) {
                                                JSONObject jo = new JSONObject(response);
                                                JSONObject ety = jo.getJSONObject("entity");
                                                String phoneNumber = ety.getString("phoneNumber");
                                                if (phoneNumber != null && phoneNumber.length() == 12) {
//
//                                                    String message = "Dear " + loanAccount.getAccountName() + ", your loan of Ksh " + disbursmentAmount + " has been disbursed to your account " + loanAccount.getLoan().getOperativeAcountId() + " ON " + new Date();
                                                    String name = loanAccount.getAccountName();
                                                    name = name.contains(" ") ? name.split(" ")[0] : name;
                                                    String message = "Dear " + name + ", your loan of Ksh " + disbursmentAmount + " has been disbursed to your account  " + loanAccount.getLoan().getOperativeAcountId() + " ON " + datesCalculator.dateFormat(new Date());
                                                    SmsDto smsDto = new SmsDto();
                                                    smsDto.setMsisdn(phoneNumber);
                                                    smsDto.setText(message);
                                                    serviceCaller.sendSmsEmt(smsDto);
                                                } else {

                                                }
                                            }

                                            response = new EntityResponse();
                                            response.setMessage("SUCCESSFULLY DISBURSED " + disbursmentAmount.toString() + " TO ACCOUNT ID: " + loanDisbursmentAccount);
                                            response.setStatusCode(HttpStatus.CREATED.value());
                                            response.setEntity(tranHeader);
                                            entityResponseList.add(response);
                                        }
                                    }
                                }
                            } else {
                                EntityResponse response = new EntityResponse();
                                response.setMessage("VERIFICATION SHOULD BE DONE BY ANOTHER USER");
                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                entityResponseList.add(response);
                            }
                        } else {
                            EntityResponse response = new EntityResponse();
                            response.setMessage("FAILED! LOAN DISBURSEMENT WAS VERIFIED ON " + loanAccount.getLoan().getDisbursmentVerifiedOn().toString());
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            entityResponseList.add(response);
                        }

                    } else {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("LOAN MUST BE DISBURSED BEFORE VERIFICATION");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        entityResponseList.add(response);
                    }


                } else {
                    entityResponseList.add(validatorsService.invalidAccountTypeMessage());
                }
            }
            return entityResponseList;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }





    public EntityResponse rejectLoanDisbursement(String acid) {
        try {
            EntityResponse response = new EntityResponse();
            EntityResponse userValidator = validatorsService.userValidator();

            if (userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                response.setEntity(userValidator);
            } else {
                Account loanAccount = accountRepository.findByAcid(acid).orElse(null);
                String accountType = loanAccount.getAccountType();

                if (accountType.equals(CONSTANTS.LOAN_ACCOUNT)) {
                    if (loanAccount.getLoan().getDisbursementFlag().equals(CONSTANTS.YES)) {
                        if (loanAccount.getLoan().getDisbursementVerifiedFlag().equals(CONSTANTS.NO)) {
                            // Only allow rejection if the verification is not done by the same user who disbursed
                            String disbursedBy = loanAccount.getLoan().getDisbursedBy();
                            String user = UserRequestContext.getCurrentUser();

                            if (!user.equals(disbursedBy)) {
                                if (loanAccount.getRejectedFlag().equals(CONSTANTS.REJECTED)) {
                                    response.setMessage("Account " + loanAccount.getAcid() + " Already REJECTED at " + loanAccount.getRejectedTime());
                                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                    response.setEntity("");
                                } else {
                                    loanAccount.setRejectedBy(UserRequestContext.getCurrentUser());
                                    loanAccount.setRejectedTime(new Date());
                                    loanAccount.setRejectedFlag(CONSTANTS.YES);
                                    loanAccount.setAccountStatus("REJECTED");

                                    // Update the loan status to REJECTED
                                    loanRepository.updateLoanStatus(CONSTANTS.REJECTED, loanAccount.getLoan().getSn());
                                    response.setMessage("Loan disbursement has been rejected");
                                    response.setStatusCode(HttpStatus.OK.value());
                                }
                            } else {
                                response.setMessage("Rejection should be done by another user");
                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            }
                        } else {
                            response.setMessage("Failed! Loan disbursement was already verified");
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        }
                    } else {
                        response.setMessage("Loan must be disbursed before rejection");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    }
                } else {
                    response.setEntity(validatorsService.invalidAccountTypeMessage());
                }
            }
            return response;
        } catch (Exception e) {
            log.info("Caught Error {}", e);
            return null;
        }
    }




    public List<PartTran> getFeePartTrans(Loan loan) {
        String operativeAccount = loan.getDisbursmentAccount();
        List<LoanFees> loanFees = loan.getLoanFees();
        List<PartTran> partTranList = new ArrayList<>();
        Double totalFees = 0.0;
        for (Integer i = 0; i < loanFees.size(); i++) {
            String feeCollectionAccount = loanFees.get(i).getChargeCollectionAccount();
            Double feeAmount = loanFees.get(i).getInitialAmt();
            String feeDescription = loanFees.get(i).getEventTypeDesc();
            totalFees += feeAmount;

            PartTran crPart = createPartTranModel("Credit", feeAmount, feeCollectionAccount, "KES", " " + feeDescription);
            partTranList.add(crPart);
        }

        PartTran drPart = createPartTranModel("Debit", totalFees, operativeAccount, "KES", " Total loan application fees");
        partTranList.add(drPart);

        return partTranList;
    }

    public EntityResponse validateAllFeeCollectionAccounts(List<LoanFees> loanFees) {

        try {
            EntityResponse res = new EntityResponse<>();
            res.setStatusCode(HttpStatus.OK.value());
            res.setMessage(HttpStatus.OK.getReasonPhrase());
            List<String> feeCollectionAccounts = loanFees.stream().map(l -> l.getChargeCollectionAccount()).collect(Collectors.toList());
            for (Integer i = 0; i < feeCollectionAccounts.size(); i++) {
                EntityResponse acidValidator = validatorsService.acidValidator(feeCollectionAccounts.get(i), "Fee collection account");
                if (!acidValidator.getStatusCode().equals(200)) {
                    res = acidValidator;
                    break;
                } else {
                    continue;
                }
            }

            return res;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    //
    public List<LoanFees> getLoanFees(String prodCode, Double amount, Loan loan) {
        try {
            EntityResponse feeEntityResponse = productFeesService.getProductFees(prodCode, amount);
            List<LoanFees> loanFees = new ArrayList<>();
            if (feeEntityResponse.getStatusCode().equals(HttpStatus.OK.value())) {
                if (feeEntityResponse.getEntity() != null) {
                    List<ProductFeeAmount> productFees = (List<ProductFeeAmount>) feeEntityResponse.getEntity();

                    Date interestCalculationStartDate = loan.getInterestCalculationStartDate();
                    Date nextCollectionDate = datesCalculator.addDate(interestCalculationStartDate, 1, "MONTHS");
                    productFees.forEach(productFeeAmount -> {
                        if (productFeeAmount != null) {
                            LoanFees loanFee = new LoanFees();
                            loanFee.setInitialAmt(productFeeAmount.getInitialAmt());
                            loanFee.setMonthlyAmount(productFeeAmount.getMonthlyAmt());
                            loanFee.setLoan(loan);
                            loanFee.setEventIdCode(productFeeAmount.getEventIdCode());
                            loanFee.setEventTypeDesc(productFeeAmount.getEventTypeDesc());
                            loanFee.setChargeCollectionAccount(productFeeAmount.getChargeCollectionAccount());
                            loanFee.setNextCollectionDate(nextCollectionDate);
                            loanFees.add(loanFee);
                        }
                    });

                }
                return loanFees;
            } else {
                return loanFees;
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public TransactionHeader createTransactionHeader(String loanCurrency,
                                                     String transDesc,
                                                     Double totalAmount,
                                                     String drAc,
                                                     String crAc, List<PartTran> feePartTrans) {
        TransactionHeader transactionHeader = new TransactionHeader();
        transactionHeader.setTransactionType(CONSTANTS.SYSTEM_USERNAME);
        transactionHeader.setCurrency(loanCurrency);
        transactionHeader.setTransactionDate(new Date());
        transactionHeader.setEntityId(CONSTANTS.SYSTEM_ENTITY);
        transactionHeader.setTotalAmount(totalAmount);

        PartTran drPartTran = new PartTran();
        drPartTran.setPartTranType(CONSTANTS.DEBITSTRING);
        log.info("Amount in Header1: "+totalAmount);
        drPartTran.setTransactionAmount(totalAmount);
        drPartTran.setAcid(drAc);
        drPartTran.setCurrency(loanCurrency);
        drPartTran.setExchangeRate("");
        drPartTran.setTransactionDate(new Date());
        drPartTran.setTransactionParticulars(transDesc);
        drPartTran.setIsoFlag(CONSTANTS.YES);

        PartTran crPartTran = new PartTran();
        crPartTran.setPartTranType(CONSTANTS.CREDITSTRING);
        log.info("Amount in Header2: "+totalAmount);
        crPartTran.setTransactionAmount(totalAmount);
        crPartTran.setAcid(crAc);
        crPartTran.setCurrency(loanCurrency);
        crPartTran.setExchangeRate("");
        crPartTran.setTransactionDate(new Date());
        crPartTran.setTransactionParticulars(transDesc);
        crPartTran.setIsoFlag(CONSTANTS.YES);

        List<PartTran> partTranList = new ArrayList<>();
        partTranList.add(drPartTran);
        partTranList.add(crPartTran);
        partTranList.addAll(feePartTrans);

        transactionHeader.setPartTrans(partTranList);
        return transactionHeader;
    }

    public TransactionHeader createTransactionHeader2(String loanCurrency,
                                                      Double totalAmount,
                                                      List<PartTran> partTranList) {
        TransactionHeader transactionHeader = new TransactionHeader();
        transactionHeader.setTransactionType(CONSTANTS.SYSTEM_USERNAME);
        transactionHeader.setCurrency(loanCurrency);
        transactionHeader.setTransactionDate(new Date());
        transactionHeader.setEntityId(CONSTANTS.SYSTEM_ENTITY);
        transactionHeader.setTotalAmount(totalAmount);
        transactionHeader.setPartTrans(partTranList);
        return transactionHeader;
    }

    public PartTran createPartTranModel(String type, Double amount, String acid, String currency, String desc) {
        PartTran partTran = new PartTran();
        partTran.setPartTranType(type);
        partTran.setTransactionAmount(amount);
        partTran.setAcid(acid);
        partTran.setCurrency(currency);
        partTran.setExchangeRate("");
        partTran.setTransactionDate(new Date());
        partTran.setTransactionParticulars(desc);
        partTran.setIsoFlag(CONSTANTS.YES);

        return partTran;
    }

    public void saveLoanDisbursmentInfo(String crAccount, String drAccount, String transcDesc, Double amt,
                                        String transcStatus, String transCode, Loan loan) {
        try {
            LoanDisbursmentInfo loanDisbursmentInfo = new LoanDisbursmentInfo();
            loanDisbursmentInfo.setCreditAccount(crAccount);
            loanDisbursmentInfo.setDebitAccount(drAccount);
            loanDisbursmentInfo.setTransactionDescription(transcDesc);
            loanDisbursmentInfo.setAmount(amt);
            loanDisbursmentInfo.setTransactionStatus(transcStatus);
            loanDisbursmentInfo.setTransactionCode(transCode);
            loanDisbursmentInfo.setLoan(loan);
            loanDisbursmentInfoRepo.save(loanDisbursmentInfo);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
    }

    public Double roundOff(Double nubmber) {
        try {
            nubmber = Math.round(nubmber * 100.0) / 100.0;
            return nubmber;
        } catch (Exception e) {
            log.info("Caught Error {}" + e);
            return null;
        }
    }


    //get loan disbursement info per acid
    public EntityResponse<List<LoanDisbursmentInfo>> getLoanDisbursementReportByAcid(String acid) {
        try {
            List<LoanDisbursmentInfo> loanDisbursmentInfos = loanDisbursmentInfoRepo.getLoanDisbursmentInfoByAcid(acid);
            EntityResponse listChecker = validatorsService.listLengthChecker(loanDisbursmentInfos);
            return listChecker;
        } catch (Exception e) {
            log.info("Caught Error {}" + e);
            return null;
        }
    }

    public EntityResponse<?> loanDisbursementCount() {
        try {
            EntityResponse response = new EntityResponse();
            EntityResponse userValidator = validatorsService.userValidator();
            if (userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                return userValidator;
            } else {
                Integer loanDisbursementList = loanRepository.countAllLoanDisbursement();
                if (loanDisbursementList > 0) {
                    response.setMessage("Total Unverified Loan Disbursement are:- " + loanDisbursementList);
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(loanDisbursementList);
                } else {
                    response.setMessage("Total Unverified Loan Disbursement are:- " + loanDisbursementList);
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setEntity(loanDisbursementList);
                }
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<?> findAllUnVerifiedLoanDisbursement() {
        try {
            EntityResponse response = new EntityResponse();
            EntityResponse userValidator = validatorsService.userValidator();
            if (userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                return userValidator;
            } else {
                List<LoanRepository.DisbursementDetails> loanDisbursementList = loanRepository.findAllUnVerifiedDisbursement();
                if (loanDisbursementList.size() > 0) {
                    response.setMessage("Total Unverified Loan Disbursement are:- " + loanDisbursementList.size());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(loanDisbursementList);
                } else {
                    response.setMessage("Total Unverified Loan Disbursement are:- " + loanDisbursementList.size());
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setEntity(loanDisbursementList);
                }
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
}



