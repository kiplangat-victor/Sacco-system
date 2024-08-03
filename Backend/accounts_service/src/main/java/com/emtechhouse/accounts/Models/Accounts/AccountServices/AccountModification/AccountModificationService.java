package com.emtechhouse.accounts.Models.Accounts.AccountServices.AccountModification;

import com.emtechhouse.accounts.Models.Accounts.Account;
import com.emtechhouse.accounts.Models.Accounts.AccountRepository;
import com.emtechhouse.accounts.Models.Accounts.Loans.Loan;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanCalculatorService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanCollateral.LoanCollateralService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDisbursment.LoanDisbursmentService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanFees.LoanFeesService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanGuarantor.LoanGuarantor;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanGuarantor.LoanGuarantorService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanSchedule.LoanScheduleService;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ProductInterestDetails.ProductInterestDetails;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ProductInterestDetails.ProductInterestService;
import com.emtechhouse.accounts.Models.Accounts.RelatedParties.RelatedParties;
import com.emtechhouse.accounts.Models.Accounts.TermDeposit.TermDeposit;
import com.emtechhouse.accounts.Models.Accounts.Validators.ValidatorService;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.DatesCalculator;
import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.accounts.Utils.RESPONSEMESSAGES;
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
public class AccountModificationService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ValidatorService validatorsService;
    @Autowired
    private LoanCalculatorService loanCalculatorService;
    @Autowired
    private DatesCalculator datesCalculator;
    @Autowired
    private LoanDisbursmentService loanDisbursmentService;

    @Autowired
    private ProductInterestService productInterestService;
    @Autowired
    private LoanScheduleService loanScheduleService;
    @Autowired
    private LoanFeesService loanFeesService;

    @Autowired
    private LoanGuarantorService loanGuarantorService;

    @Autowired
    private LoanCollateralService loanCollateralService;

    public EntityResponse<Account> modifyAccount(Account account){
        try{
            String acid=account.getAcid();
            Optional<Account> account1=accountRepository.findByAcid(acid);
            if(account1.isPresent()){
                Account existingAccount= account1.get();
                if(existingAccount.getAccountStatus().equals(CONSTANTS.ACTIVE)){
                    account.setAccountBalance(existingAccount.getAccountBalance());
                    account.setLienAmount(existingAccount.getLienAmount());
                    account.setOpeningDate(existingAccount.getOpeningDate());
                    account.setAccountStatus(existingAccount.getAccountStatus());
                    account.setAccountType(existingAccount.getAccountType());
                    account.setCreditLiens(existingAccount.getCreditLiens());
                    account.setDebitLiens(existingAccount.getDebitLiens());

                    account.setId(existingAccount.getId());
                    account.setPostedBy(existingAccount.getPostedBy());
                    account.setPostedFlag(existingAccount.getPostedFlag());
                    account.setPostedTime(existingAccount.getPostedTime());
                    account.setDeleteFlag(existingAccount.getDeleteFlag());
                    account.setVerifiedFlag(CONSTANTS.NO);
                    if(account.getAccountType().equals(CONSTANTS.LOAN_ACCOUNT)){
                        return modifyLoanAccount(account);
                    }else {
                        return updateAccount(account);
                    }
                }else {
                    EntityResponse res= new EntityResponse<>();
                    res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    res.setMessage("Account must be active");
                    return res;
                }

            }else {
                EntityResponse response = new EntityResponse();
                response.setMessage(RESPONSEMESSAGES.RECORD_NOT_FOUND);
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setEntity("");
                return response;
            }


        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse modifyLoanAccount(Account account){
        try{
            //check if loan is disbursed

            Loan existingLoan=accountRepository.findByAcid(account.getAcid()).get().getLoan();
            Loan loan=account.getLoan();
            loan.setLoanFees(new ArrayList<>());
            loan.setLoanSchedules(new ArrayList<>());
            loan.setSn(existingLoan.getSn());
            loan.setDisbursementAmount(0.0);
            loan.setFeesAmount(0.0);
            loan.setDisbursementVerifiedFlag(CONSTANTS.NO);
            loan.setDisbursementFlag(CONSTANTS.NO);
            account.setLoan(loan);

            if(loan.getDisbursementVerifiedFlag().equals(CONSTANTS.YES)){
                //throw an error
                EntityResponse response = new EntityResponse();
                response.setMessage("INVALID! A DISBURSED LOAN ACCOUNT CANNOT BE EDITTED");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;
            }else {
                return updateAccount(account);
            }

        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }


    public EntityResponse<Account> updateAccount(Account account){
        try{
            EntityResponse userValidator= validatorsService.userValidator();
            if(userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                return userValidator;
            }else {
                String user =UserRequestContext.getCurrentUser();
                /**Account type validator
                 * Confirming if the submitted account detail are of the selected account type
                 * **/
                EntityResponse accountTypeValidator =validatorsService.accountTypeValidator(account);
                if(accountTypeValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                    return accountTypeValidator;
                }else {
                    /**get account type /**/
                    String accountType=account.getAccountType();
                    /**-------------------------office account------------------------------------------------**/
                    if(accountType.equals(CONSTANTS.OFFICE_ACCOUNT)){
                        return stepTwoValidation(account);

                    }else {
                        /** check if product code and product type exists**/
                        String productCode= account.getProductCode();
                        EntityResponse productValidator=validatorsService.productValidator(productCode);
                        if(productValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                            return productValidator;
                        }else {
//                            return stepTwoValidation(account);
//                            /** Confirm whether CIF ID is created IE Customer Code**/
                            String customerCode = account.getCustomerCode();
                            EntityResponse retailCustomerValidator= validatorsService.retailCustomerValidator(customerCode);
                            if(retailCustomerValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                                return retailCustomerValidator;
                            }else {
                                /** checking if the account has related parties and each related party is registered
                                 * in the crm module IE has customer code
                                 */
                                if(validatorsService.hasRelatedParties(account)){
                                    List<RelatedParties> relatedPartiesList=account.getRelatedParties();
                                    EntityResponse relatedPartiesValidator = validatorsService.relatedPartiesValidator(relatedPartiesList);
                                    if(relatedPartiesValidator.getStatusCode().equals(HttpStatus.BAD_REQUEST.value())){
                                        return relatedPartiesValidator;
                                    }else {
                                        return stepTwoValidation(account);
                                    }
                                }else {
                                    return stepTwoValidation(account);
                                }
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }

    }
    public EntityResponse<Account> stepTwoValidation(Account account){
        try{
            //confirm account type
            if(account.getAccountType().equals(CONSTANTS.TERM_DEPOSIT)){
                TermDeposit termDeposit= account.getTermDeposit();
                String interestCrAccountId= termDeposit.getInterestCrAccountId();
                //validate interest account
                EntityResponse acidValidator =validatorsService.acidValidator(interestCrAccountId,
                        "Attached interest credit");
                if(acidValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                    return acidValidator;
                }else {
                    TermDeposit newTermDeposit = termDepositSetter(termDeposit);
                    account.setTermDeposit(newTermDeposit);
                    account.setTermDeposit(newTermDeposit);
                    return postAccount(account);
                }

            }else if(account.getAccountType().equals(CONSTANTS.LOAN_ACCOUNT)){
                return postLoanAccount(account);

            } else if (account.getAccountType().equals(CONSTANTS.SAVINGS_ACCOUNT)) {
                return postAccount(account);
            } else{
                return postAccount(account);
            }
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    //TODO: TERM DEPOSIT ACCOUNT SETTERS
    public TermDeposit termDepositSetter(TermDeposit termDeposit){
        try{
//            termDeposit.setAccruedInterest(0.00);
//            termDeposit.setMaturityAmount(100.00);
//            termDeposit.setMaturityDate(LocalDate.now());
            return termDeposit;
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse postLoanAccount(Account account){
        try {
            Loan loan=account.getLoan();
            Double principalAmt=loan.getPrincipalAmount();
            String customerCode=account.getCustomerCode();
            //validate all the guarantors
            List<LoanGuarantor> loanGuarantors=loan.getLoanGuarantors();
            EntityResponse loanLimitValidator= validatorsService.validateLoanLimit(customerCode,principalAmt);
            if(loanLimitValidator.getStatusCode().equals(HttpStatus.OK.value())){
                // get account interest rate if not found throw an error
                String prodCode= account.getProductCode();
                Double principalAmount= account.getLoan().getPrincipalAmount();
                //get interest details
                EntityResponse loanInterestResponse= productInterestService.getLoanInterest(prodCode, principalAmount);
                if(loanInterestResponse.getStatusCode().equals(HttpStatus.NOT_FOUND.value())){
                    return loanInterestResponse;
                }else {
                    ProductInterestDetails productInterestDetails= (ProductInterestDetails) loanInterestResponse.getEntity();
                    Double productLoanInterestRate=productInterestDetails.getInterestRate();
//                    Double productLoanInterestRate=12.0;
                    String productInterestCalculationMethod=productInterestDetails.getInterestCalculationMethod();
//                    String productInterestCalculationMethod="reducing_balance";
                    String productInterestPeriod=productInterestDetails.getInterestPeriod();
//                    String productInterestPeriod="p.a";

                    String loanInterestCalculationMethod = null;
                    Double loanAnnualInterestRate = null;
                    if(productInterestCalculationMethod.equalsIgnoreCase("flat_rate")){
                        loanInterestCalculationMethod=CONSTANTS.FLAT_RATE;
                    } else if (productInterestCalculationMethod.equalsIgnoreCase("reducing_balance")) {
                        loanInterestCalculationMethod=CONSTANTS.REDUCING_BALANCE;
                    }
                    if(productInterestPeriod.equalsIgnoreCase("p.m")){
                        loanAnnualInterestRate=(productLoanInterestRate*12);
                    } else if (productInterestPeriod.equalsIgnoreCase("p.a")) {
                        loanAnnualInterestRate=productLoanInterestRate;
                    }

                    //get customer preferential interest
                    //by default should be equal to zero
                    Double interestPreferential =loan.getInterestPreferential();
                    loanAnnualInterestRate=roundOff(loanAnnualInterestRate);


                    interestPreferential=(1*interestPreferential);
                    if(interestPreferential != null || !interestPreferential.equals(null)){
                        if(interestPreferential>0){
                            loanAnnualInterestRate=loanAnnualInterestRate+interestPreferential;
                        }else {
                            loanAnnualInterestRate=(loanAnnualInterestRate-(interestPreferential*-1));
                        }

                    }
                    String operativeAccountId= loan.getOperativeAcountId();
                    String productCode =loan.getAccount().getProductCode();
                    Double loanPrincipalAmount= loan.getPrincipalAmount();
                    loan.setInterestCalculationStartDate(loanCalculatorService.getInterestCalculationStartDateDate(loan));
                    Long loanPeriodInMonths=loanCalculatorService.calculateLoanPeriodMonths(loan);
                    loan.setLoanPeriodMonths(loanPeriodInMonths);
                    //validate interest
                    EntityResponse principalAmountAndPeriodValidator=validatorsService.loanAmountAndPeriodValidator(loan.getPrincipalAmount(),loanPeriodInMonths,loan.getAccount().getProductCode());
                    if(principalAmountAndPeriodValidator.getStatusCode().equals(HttpStatus.NOT_FOUND.value()) || principalAmountAndPeriodValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                        return principalAmountAndPeriodValidator;
                    }else {
                        //operative account validation step one
                        EntityResponse operativeAcidValidator =validatorsService.acidValidator(operativeAccountId, "Operative");
                        if(operativeAcidValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                            return operativeAcidValidator;
                        }else {
                            // operative account validation step two
                            EntityResponse acidWithdrawalValidator =validatorsService.acidWithdrawalValidator(operativeAccountId);
//
                            Double interestToBePaid=0.0;
                            Double totalLoanBalance= 0.0;
                            principalAmount=loan.getPrincipalAmount();
                            loan.setInterestRate(loanAnnualInterestRate);
                            loan.setInterestCalculationMethod(loanInterestCalculationMethod);
                            loan.setMaturityDate(loanCalculatorService.calculateMaturityDate(loan));
                            totalLoanBalance=principalAmount;

                            Date installmentsStartDate =loan.getInstallmentStartDate();
                            loan.setInstallmentStartDate(datesCalculator.convertDateTimeStamp(installmentsStartDate));
                            loan.setNetAmount(loanCalculatorService.calculateNetAmount(loan));
                            loan.setInterestDemandAmount(loanCalculatorService.calculateInterestDemandAmount(loan));
                            loan.setPrincipalDemandAmount(loanCalculatorService.calculatePrincipalDemandAmount(loan));
                            loan.setSumPrincipalDemand(0.00);
                            loan.setSumMonthlyFeeDemand(0.00);
                            loan.setOverFlowAmount(0.0);
                            loan.setNextRepaymentDate(datesCalculator.convertDateTimeStamp(installmentsStartDate));
                            loan.setDisbursementAmount(0.0);
                            loan.setOutStandingPrincipal(loan.getPrincipalAmount());
                            //flat rate and reducing balance
                            loan.setInterestAmount(interestToBePaid);
                            loan.setOutStandingInterest(interestToBePaid);
                            loan.setTotalLoanBalance(totalLoanBalance);
                            loan.setLoanPeriodDays(loanCalculatorService.calculateLoanPeriodInDays(loan));
                            loan.setDailyInterestAmount(loanCalculatorService.calculateDailyInterestToBePaid(loan));
                            loan.setInstallmentAmount(loanCalculatorService.calculateInstallmentAmount(loan));
                            loan.setAccrualLastDate(loanCalculatorService.getInterestCalculationStartDateDate(loan));
                            loan.setBookingLastDate(loanCalculatorService.getInterestCalculationStartDateDate(loan));
                            loan.setLoanSchedules(loanCalculatorService.generateLoanSchedules(loan));

                                loan.setLoanFees(loanDisbursmentService.getLoanFees(productCode,loanPrincipalAmount,loan));

                            loan.setLoanStatus(CONSTANTS.NEW);
                            loan.setDisbursementFlag(CONSTANTS.NO);
                            loan.setDisbursementVerifiedFlag(CONSTANTS.NO);
                            account.setLoan(loan);
                            account.getLoan().setDemandCarryForward(0);
                            return postAccount(account);
//                            }
                        }
                    }
                }
            }else {
                return loanLimitValidator;
            }

        }catch (Exception e){
            log.info("Catched Error {} " + e);
            EntityResponse response = new EntityResponse();
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity(e.getCause());
            return response;
        }
    }

    //TODO: ADDS A VERIFIED ACCOUNT TO THE DATABASE
    public EntityResponse<Account> postAccount(Account account){
        try {
            if(account.getAccountType().equals(CONSTANTS.LOAN_ACCOUNT)){
                Loan existingLoan=accountRepository.findByAcid(account.getAcid()).get().getLoan();

                //drop all the schedules, and create other ones
                log.info("Deleting loan schedules----->" + account.getLoan().getLoanSchedules().size());
                loanScheduleService.deleteLoanSchedules(existingLoan.getLoanSchedules());
                //drop all the loan fees
                loanFeesService.deleteLoanFees(existingLoan.getLoanFees());
                loanGuarantorService.deleteGuarantors(existingLoan.getLoanGuarantors());
                loanCollateralService.deleteLoanCollaterals(existingLoan.getLoanCollaterals());
            }
            Account newAccount = setAccountGeneralProperties(account);

            Account sAccount = accountRepository.save(newAccount);
            EntityResponse response = new EntityResponse();
            response.setMessage("ACCOUNT MODIFIED SUCCESSFULLY: ACCOUNT NUMBER: "+sAccount.getAcid());
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(sAccount);
            return response;
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public Double roundOff(Double nubmber){
        try{
            nubmber=Math.round(nubmber * 100.0) / 100.0;
            return nubmber;
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }

    public Account setAccountGeneralProperties(Account account){
        try{
            String modifiedBy =UserRequestContext.getCurrentUser();

//            account.setPostedBy(postedBy);
            account.setModifiedTime(new Date());
            account.setModifiedBy(modifiedBy);
            account.setDeleteFlag(CONSTANTS.NO);
            account.setVerifiedFlag(CONSTANTS.NO);
            account.setEntityId(EntityRequestContext.getCurrentEntityId());

            return account;
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }
}
