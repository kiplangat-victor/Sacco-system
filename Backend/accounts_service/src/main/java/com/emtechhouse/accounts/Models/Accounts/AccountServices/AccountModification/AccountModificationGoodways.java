package com.emtechhouse.accounts.Models.Accounts.AccountServices.AccountModification;

import com.emtechhouse.accounts.Models.Accounts.Account;
import com.emtechhouse.accounts.Models.Accounts.AccountRepository;
import com.emtechhouse.accounts.Models.Accounts.Loans.Loan;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanCalculatorService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanCollateral.LoanCollateral;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanCollateral.LoanCollateralService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDisbursment.LoanDisbursmentService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanFees.LoanFeesService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanGuarantor.LoanGuarantor;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanGuarantor.LoanGuarantorService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanSchedule.LoanScheduleService;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ProductInterestDetails.ProductInterestDetails;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ProductInterestDetails.ProductInterestService;
import com.emtechhouse.accounts.Models.Accounts.RelatedParties.RelatedParties;
import com.emtechhouse.accounts.Models.Accounts.TermDeposit.Schedule.TermDepositScheduleService;
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
public class AccountModificationGoodways {
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
    @Autowired
    private TermDepositScheduleService termDepositScheduleService;

    public EntityResponse<Account> modifyAccount(Account account) {
        try{
            String acid=account.getAcid();
            Optional<Account> account1=accountRepository.findByAcid(acid);
            if(account1.isPresent()){
                Account existingAccount= account1.get();
                if(existingAccount.getAccountStatus().equals(CONSTANTS.ACTIVE)
                || existingAccount.getAccountStatus().equals(CONSTANTS.REJECTED)) {
                    account.setAccountBalance(existingAccount.getAccountBalance());
                    account.setLienAmount(existingAccount.getLienAmount());
                    account.setOpeningDate(existingAccount.getOpeningDate());
                    account.setAccountStatus(existingAccount.getAccountStatus());
                    account.setAccountType(existingAccount.getAccountType());
                    account.setCreditLiens(existingAccount.getCreditLiens());
                    account.setDebitLiens(existingAccount.getDebitLiens());
                    account.setAccountStatus(CONSTANTS.ACTIVE);
                    account.setId(existingAccount.getId());
                    account.setPostedBy(existingAccount.getPostedBy());
                    account.setPostedFlag(existingAccount.getPostedFlag());
                    account.setPostedTime(existingAccount.getPostedTime());
                    account.setDeleteFlag(existingAccount.getDeleteFlag());
                    account.setVerifiedFlag(CONSTANTS.NO);
                    if(account.getAccountType().equalsIgnoreCase(CONSTANTS.LOAN_ACCOUNT)){
                        return modifyLoanAccount(account);
                    } else if (account.getAccountType().equals(CONSTANTS.TERM_DEPOSIT)) {
                        return modifyTermDeposit(account);
                    } else {
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

    public EntityResponse modifyLoanAccount(Account account) {
        try {
            Loan existingLoan=accountRepository.findByAcid(account.getAcid()).get().getLoan();
            Loan loan=account.getLoan();
            System.out.println("Beefore check verified");
            System.out.println(existingLoan.getSn());
            if(existingLoan.getDisbursementVerifiedFlag() != null)
            if(loan.getDisbursementVerifiedFlag().equals(CONSTANTS.YES)) {
                //throw an error
                EntityResponse response = new EntityResponse();
                response.setMessage("INVALID! A DISBURSED LOAN ACCOUNT CANNOT BE EDITED");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;
            }

            log.info("Modifying loan account");
            //check if loan is disbursed
            log.info("Loan fees size is :: "+ loan.getLoanFees().size());
//            loan.setLoanFees(new ArrayList<>());
            loan.setLoanSchedules(new ArrayList<>());
            loan.setSn(existingLoan.getSn());
            loan.setDisbursementAmount(0.0);
            loan.setFeesAmount(0.0);
            loan.setDisbursementVerifiedFlag(CONSTANTS.NO);
            loan.setDisbursementFlag(CONSTANTS.NO);
            account.setLoan(loan);
            {
                return updateAccount(account);
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse modifyTermDeposit(Account account){
        try{
            //check if loan is disbursed
            Character vFlag=accountRepository.findByAcid(account.getAcid()).get().getVerifiedFlag();
            TermDeposit t= account.getTermDeposit();

            if(vFlag.equals(CONSTANTS.YES)){
                //throw an error
                EntityResponse response = new EntityResponse();
                response.setMessage("INVALID! A VERIFIED TERM DEPOSIT ACCOUNT CANNOT BE EDITED");
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
//    public EntityResponse<Account> stepTwoValidation(Account account){
//        try{
//            //confirm account type
//            if(account.getAccountType().equals(CONSTANTS.TERM_DEPOSIT)){
//                TermDeposit termDeposit= account.getTermDeposit();
//                String interestCrAccountId= termDeposit.getInterestCrAccountId();
//                //validate interest account
//                EntityResponse acidValidator =validatorsService.acidValidator(interestCrAccountId,
//                        "Attached interest credit");
//                if(acidValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
//                    return acidValidator;
//                }else {
//                    TermDeposit newTermDeposit = termDepositSetter(termDeposit);
//                    account.setTermDeposit(newTermDeposit);
//                    account.setTermDeposit(newTermDeposit);
//                    return postAccount(account);
//                }
//
//            }else if(account.getAccountType().equals(CONSTANTS.LOAN_ACCOUNT)){
//                return postLoanAccount(account);
//
//            } else if (account.getAccountType().equals(CONSTANTS.SAVINGS_ACCOUNT)) {
//                return postAccount(account);
//            } else{
//                return postAccount(account);
//            }
//        }catch (Exception e){
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }

    public EntityResponse stepTwoValidation(Account account){
        try{
            log.info("step two validation");
            //confirm account type
            switch (account.getAccountType()) {
                case CONSTANTS.TERM_DEPOSIT:
                    return termDepositGeneralValidation(account);
                case CONSTANTS.LOAN_ACCOUNT:
                    return postLoanAccount(account);
                default:
                    return postAccount(account);
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

    public EntityResponse termDepositGeneralValidation(Account account){
        try {
            EntityResponse res = new EntityResponse<>();
            TermDeposit termDeposit = account.getTermDeposit();
            String interestCrAccountId = termDeposit.getInterestCrAccountId();
            String principalDebit = termDeposit.getPrincipalDrAccountId();
            String principalCredit = termDeposit.getPrincipalCrAccountId();
            Double principalAmt = termDeposit.getTermDepositAmount();
            //validate interest account
            //todo: validate interest credit account
            res = validatorsService.acidValidator(interestCrAccountId,
                    "Attached interest credit");
            if (res.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                return res;
            } else {
                //todo: validate principal dr
                res = validatorsService.acidValidator(principalDebit,
                        "Principal Debit");
                if(res.getStatusCode() ==HttpStatus.OK.value()){
                    //todo: check whether ac bal > = principal amt
                    Double principalDebitBal=accountRepository.getAccountBalance(principalDebit);
                    // todo: validate principal credit acid
                    if(principalDebitBal>=principalAmt){
                        //todo: validate principal credit account
                        res = validatorsService.acidValidator(principalCredit,
                                "Principal credit account");
                        if(res.getStatusCode() ==HttpStatus.OK.value()){
                            return setTermDepositInterest(account);
                        }else {
                            return res;
                        }
                    }else {
                        res.setMessage("Insufficient funds in the principal Debit account");
                        res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        res.setEntity(null);
                        return res;
                    }
                }else {
                    return res;
                }
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
    //TODO: GET TERM DEPOSIT INTEREST
    public EntityResponse setTermDepositInterest(Account account){
        try {
            log.info("Setting term deposit interest");
            String prodCode= account.getProductCode();
            TermDeposit t= account.getTermDeposit();
            Double principalAmount=  account.getTermDeposit().getTermDepositAmount();;
            EntityResponse res= productInterestService.getLoanInterest(prodCode,
                    principalAmount);

            if(res.getStatusCode()==HttpStatus.OK.value()){
                ProductInterestDetails productInterestDetails= (ProductInterestDetails) res.getEntity();
                Double productInterestRate=productInterestDetails.getInterestRate();
                Double interestRate = null;
                //check if its p.a or p.a
                String productInterestPeriod=productInterestDetails.getInterestPeriod();

                if(productInterestPeriod.equalsIgnoreCase("p.m")){
                    interestRate=(productInterestRate*12);
                } else if (productInterestPeriod.equalsIgnoreCase("p.a")) {
                    interestRate=productInterestRate;
                }
                t.setInterestRate(interestRate);

                TermDeposit newTermDeposit = termDepositSetter(t);
                account.setTermDeposit(newTermDeposit);
                account.setTermDeposit(newTermDeposit);
                return postTermDepositAccount(account);
            }else {
                return res;
            }
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    //TODO: TERM DEPOSIT ACCOUNT SETTERS
    public TermDeposit termDepositSetter(TermDeposit termDeposit){
        try{
            log.info("Setting term deposit details");
            //todo: calculate term int (product int + int preferential)
            Double interestRate= termDeposit.getInterestRate()+termDeposit.getInterestPreferential();
            termDeposit.setInterestRate(interestRate);
            //todo: calc total interest to be paid
            Double intAmt=termDepositScheduleService.calculateInterestToBePaid(
                    interestRate,
                    termDeposit.getTermDepositAmount(),
                    termDeposit.getPeriodInMonths()
            );
            termDeposit.setInterestAmount(intAmt);
            //todo: calculate maturity value
            Double mValue= termDepositScheduleService.calcMaturityValue(
                    termDeposit.getInterestRate(),
                    termDeposit.getTermDepositAmount(),
                    termDeposit.getPeriodInMonths());
            termDeposit.setMaturityValue(mValue);
            //todo: calculate maturity date
            Date mDate=termDepositScheduleService.calculateMaturityDate(
                    termDeposit.getPeriodInMonths(),
                    termDeposit.getValueDate());
            termDeposit.setMaturityDate(mDate);
            //todo: calculate loan period in days
            Long loanPeriodInDays=termDepositScheduleService.calculateTermDepositPeriodInDays(
                    termDeposit.getValueDate(),
                    mDate
            );
            termDeposit.setPeriodInDays(loanPeriodInDays);
            //todo: set Accrual last date = value date
            log.info("Setting accrual last date :: "+termDeposit.getValueDate());
            termDeposit.setAccrualLastDate(termDeposit.getValueDate());
            //todo: set status to new
            termDeposit.setTermDepositStatus(CONSTANTS.NEW);
            termDeposit.setSumAccruedAmount(0.0);
            return termDeposit;
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    //TODO: ALL TERM DEPOSIT VALIDATIONS
    public EntityResponse postTermDepositAccount(Account account){
        try{
            Double principalAmt= account.getTermDeposit().getTermDepositAmount();
            Integer period=account.getTermDeposit().getPeriodInMonths();
            String prodCode= account.getProductCode();
            EntityResponse res = new EntityResponse<>();
            res=validatorsService.termDepositValidation(
                    principalAmt,
                    period,
                    prodCode
            );
            if(res.getStatusCode()== HttpStatus.OK.value()){
                return postAccount(account);
            }else {
                return res;
            }
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse postLoanAccount(Account account){
        try {

            EntityResponse res = new EntityResponse<>();
            Loan loan=account.getLoan();
            Double principalAmt=loan.getPrincipalAmount();
            String customerCode=account.getCustomerCode();
            //validate all the guarantors
            List<LoanGuarantor> loanGuarantors=loan.getLoanGuarantors();
            // get account interest rate if not found throw an error
            String prodCode= account.getProductCode();
            Double principalAmount= account.getLoan().getPrincipalAmount();

            //todo: get interest rate from the product
            res= productInterestService.getLoanInterest(prodCode,
                    principalAmount);
            if(res.getStatusCode().equals(HttpStatus.NOT_FOUND.value())){
                log.error("Product interest not found");
                return res;
            }else {
                ProductInterestDetails productInterestDetails= (ProductInterestDetails) res.getEntity();
                Double productLoanInterestRate=productInterestDetails.getInterestRate();
                String productInterestCalculationMethod=productInterestDetails.getInterestCalculationMethod();
                String productInterestPeriod=productInterestDetails.getInterestPeriod();
                System.out.println("Getting interest details");
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

                System.out.println("Interest calculation method :"+loanInterestCalculationMethod);
                String operativeAccountId= loan.getOperativeAcountId();
                loan.setInterestCalculationStartDate(loanCalculatorService.getInterestCalculationStartDateDate(loan));
                Long loanPeriodInMonths=loanCalculatorService.calculateLoanPeriodMonths(loan);
                loan.setLoanPeriodMonths(loanPeriodInMonths);
                //todo: loan amount validation and period
                res=validatorsService.loanAmountAndPeriodValidator(loan.getPrincipalAmount(),
                        loanPeriodInMonths,
                        loan.getAccount().getProductCode());
                if(res.getStatusCode().equals(
                        HttpStatus.NOT_FOUND.value()) ||
                        res.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                    return res;
                }else {
                    //todo: individual loan loan limit validation
                    res= validatorsService.validateIndividualLoanLimit(account.getCustomerCode(),
                            loan.getPrincipalAmount(),
                            account.getProductCode());
                    if(res.getStatusCode().equals(HttpStatus.OK.value())){
                        //todo: operative account validation step one
                        res =validatorsService.acidValidator(operativeAccountId,
                                "Operative");
                        if(res.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                            return res;
                        }else {

                            //todo: loan guarantors validation- number of guarantors
                            res= loanGuarantorService.validateNumberOfGuarantors(account.getProductCode(),
                                    loan.getLoanGuarantors());
                            log.info("Guarntors res status code :: "+res.toString());
                            if(res.getStatusCode().equals(HttpStatus.OK.value())){
                                //todo: loan guarantors validation- amount guaranteed
                                res= loanGuarantorService.validateTotalGuaranteedAmtAndLoanAmount(loan.getPrincipalAmount(),
                                        loan.getLoanGuarantors());
                                if(res.getStatusCode().equals(HttpStatus.OK.value())){
                                    // TODO: 3/20/2023  validate collaterals if there are any
                                    log.info("validation collaterals :: "+loan.getLoanCollaterals());
                                    res= loanCollateralService.validateCollateral(loan.getLoanCollaterals());
                                    if(res.getStatusCode() == HttpStatus.OK.value()){

                                        // TODO: 3/22/2023 validate all loan fee attached
                                        res=loanFeesService.validateAllFeeCollectionAccounts(loan.getLoanFees());
                                        if(res.getStatusCode()==HttpStatus.OK.value()){
                                            log.info("Status code okay :: "+ res.getStatusCode());
                                            log.info("Status  entity :: "+ res.getEntity());

                                            if(res.getEntity() != null){
                                                List<LoanCollateral> ls= (List<LoanCollateral>) res.getEntity();
                                                log.info("Collaterals size is :: "+ls.size());
                                                loan.setLoanCollaterals(ls);
                                            }
                                            Double interestToBePaid=0.0;
                                            Double totalLoanBalance= 0.0;
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

//                                        loan.setLoanFees(loanDisbursmentService.getLoanFees(prodCode,principalAmt,loan));

                                            loan.setLoanStatus(CONSTANTS.NEW);
                                            loan.setDisbursementFlag(CONSTANTS.NO);
                                            loan.setDisbursementVerifiedFlag(CONSTANTS.NO);
                                            loan.setPayOffFlag(CONSTANTS.NO);
                                            loan.setAssetClassification(CONSTANTS.NOT_CLASSIFIED);
                                            account.setLoan(loan);
                                            account.getLoan().setDemandCarryForward(0);
                                            return postAccount(account);
                                        }else {
                                            return res;
                                        }
                                    }else {
                                        log.info("Status code not 200 :: "+ res.getStatusCode());
                                        return res;
                                    }

                                }else {
                                    return res;
                                }
                            }else {
                                return res;
                            }
                        }
                    }else {
                        return res;
                    }
                }
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
