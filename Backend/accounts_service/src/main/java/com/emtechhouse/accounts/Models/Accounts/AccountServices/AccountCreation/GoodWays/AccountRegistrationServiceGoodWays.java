package com.emtechhouse.accounts.Models.Accounts.AccountServices.AccountCreation.GoodWays;

import com.emtechhouse.accounts.Exception.ApiRequestException;
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
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.AutoAddedAccountDto;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.GeneralProductDetails;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ProductInterestDetails.ProductInterestDetails;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ProductInterestDetails.ProductInterestService;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ItemServiceCaller;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.SpecificProductDetails.SavingsAccountProduct;
import com.emtechhouse.accounts.Models.Accounts.RelatedParties.RelatedParties;
import com.emtechhouse.accounts.Models.Accounts.TermDeposit.Schedule.TermDepositScheduleService;
import com.emtechhouse.accounts.Models.Accounts.TermDeposit.TermDeposit;
import com.emtechhouse.accounts.Models.Accounts.Validators.ValidatorInterfaces.RetailCustomerItem;
import com.emtechhouse.accounts.Models.Accounts.Validators.ValidatorService;
import com.emtechhouse.accounts.Models.Accounts.Validators.ValidatorsRepo;
import com.emtechhouse.accounts.Utils.AcidGenerator;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.DatesCalculator;
import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class AccountRegistrationServiceGoodWays{
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ValidatorsRepo validatorsRepo;
    @Autowired
    private ValidatorService validatorsService;
    @Autowired
    private LoanCalculatorService loanCalculatorService;
    @Autowired
    private AcidGenerator acidGenerator;
    @Autowired
    private DatesCalculator datesCalculator;
    @Autowired
    private LoanDisbursmentService loanDisbursmentService;
    @Autowired
    private ItemServiceCaller itemServiceCaller;

    @Autowired
    private ProductInterestService productInterestService;

    @Autowired
    private LoanGuarantorService loanGuarantorService;
    @Autowired
    private TermDepositScheduleService termDepositScheduleService;
    @Autowired
    private LoanCollateralService loanCollateralService;
    @Autowired
    private LoanFeesService loanFeesService;


    //list all validators


    public EntityResponse registerAccount(Account account) {
        System.out.println("Arrived to open account");
        try {
            EntityResponse userValidator= validatorsService.userValidator();
            if(userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                return userValidator;
            }else {
                /**Account type validator
                 * Confirming if the submitted account detail are of the selected account type
                 * **/
                EntityResponse accountTypeValidator =validatorsService.accountTypeValidator(account);
                if(accountTypeValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                    return accountTypeValidator;
                } else {
                    /**get account type /**/
                    String accountType=account.getAccountType();
                    /**-------------------------office account------------------------------------------------**/
                    if(accountType.equals(CONSTANTS.OFFICE_ACCOUNT)) {
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
                                if(validatorsService.hasRelatedParties(account)) {
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
        }catch (Exception e) {
            log.info("Catched Error {} " + e);
//            return null;
            EntityResponse response = new EntityResponse();
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity(e.getCause());
            return response;
        }
    }
    public EntityResponse stepTwoValidation(Account account){
        try{
            log.info("step two validation");
            //confirm account type
            switch (account.getAccountType()) {
                case CONSTANTS.TERM_DEPOSIT:
                    EntityResponse res= new EntityResponse<>();
                    return termDepositGeneralValidation(account);

                case CONSTANTS.LOAN_ACCOUNT:
                    return postLoanAccount(account);

                case CONSTANTS.SAVINGS_ACCOUNT:
                        return postSavingsAccount1(account);
//                    }
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

                                        // TODO: 3/24/2023 validate total loan security amount
                                        log.info("Loan Type: "+loan.getLoanType());
                                        res=loanGuarantorService.validateTotalLoanSecurity(loan);
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
                                        }
                                        else {
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

    //TODO: REGISTERING A SAVING ACCOUNT
    //SA01
    //SA02
    //SA06


    @Transactional
    public EntityResponse postSavingsAccount1(Account account){
        try{
            //check accounts auto creation
            String productCode= account.getProductCode();
            String acName= account.getAccountName();
            EntityResponse res = new EntityResponse<>();
            List<EntityResponse> lsRes= new ArrayList<>();
            List<AutoAddedAccountDto> ls= new ArrayList<>();
            String message ="";
            ls.add(new AutoAddedAccountDto(productCode));
            res= itemServiceCaller.getAutoCreatedAccountProducts(account.getProductCode());
            if(res.getStatusCode().equals(HttpStatus.OK.value())){
                List<AutoAddedAccountDto> newLs= (List<AutoAddedAccountDto>) res.getEntity();
                ls.addAll(newLs);
            }
            //todo: create new accounts
            for (Integer i = 0; i < ls.size(); i++) {
                res = createSavingsAccountGeneral(account, acName, ls.get(i).getProductCode());
                lsRes.add(res);
                message = message + " \n " + res.getMessage() + " \n ";

                // Save each auto-added account to the database
                if (res.getStatusCode().equals(HttpStatus.CREATED.value())) {
                    EntityResponse etyAutoAddedAccount = postAccount(account);
                    lsRes.add(etyAutoAddedAccount);
                    message = message + " \n " + etyAutoAddedAccount.getMessage() + " \n ";
                }
            }

            res.setMessage(message);
            res.setEntity(message);
            return res;

        }catch (Exception e){
            log.info("Catched Error {} " + e);
            List<EntityResponse> lsRes= new ArrayList<>();

            EntityResponse response = new EntityResponse();
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity(e.getCause());
            lsRes.add(response);
            return response;
        }
    }
    public EntityResponse<?> createSavingsAccountGeneral(Account account, String acName, String productCode){
        try {
            EntityResponse res= new EntityResponse<>();
            log.info("Creating a general savings account");
//            account= setAccountGeneralProperties(account);
            log.info("acName "+account.getAccountName());
            //get product details
            String customerCode=account.getCustomerCode();
            // product validator
            res= validatorsService.retailCustomerNumberOfAccountValidator(productCode,customerCode);
            if(res.getStatusCode()== HttpStatus.OK.value()){
                res= itemServiceCaller.getGeneralProductDetail1(productCode,CONSTANTS.SAVINGS_ACCOUNT);
                if(res.getStatusCode().equals(HttpStatus.OK.value())){

                    GeneralProductDetails generalProductDetails = (GeneralProductDetails) res.getEntity();
                    res= itemServiceCaller.getSavingsAccountProductDetails(productCode);
                    if(res.getStatusCode().equals(HttpStatus.OK.value())){

                        String productTwoName= generalProductDetails.getProductCodeDesc();
                        acName= acName;
                        account.setProductCode(productCode);
                        account.setAccountName(acName);

                        //todo: set savings account flag
                        String flag= flagChecker(account.getProductCode());
                        account.setSavingsAccountTypeFlag(flag);
                        //save account two
                        EntityResponse etyAcTwo=postAccount(account);
                        if(etyAcTwo.getStatusCode().equals(HttpStatus.CREATED.value())) {
                            Account account1= (Account) etyAcTwo.getEntity();
                            String acNo=account1.getAcid();

                            res.setMessage(productTwoName+" ACCOUNT CREATED SUCCESSFULLY: ACCOUNT NUMBER: "+acNo);
                            res.setStatusCode(HttpStatus.CREATED.value());
                        }else {

                            res.setMessage("FAILED! COULD NOT CREATE "+productTwoName+" ACCOUNT: "+etyAcTwo.getMessage());
                            res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        }
                        return res;
                    }else {
                        return res;
                    }

                }else {
                    return res;
                }
            }else {
                return res;
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

    public String flagChecker(String accountType){
        String flag= null;
        if(accountType.equals(CONSTANTS.ORDINARY_SAVINGS_ACCOUNT_PRODUCT_PERSONAL) ||
                accountType.equals(CONSTANTS.ORDINARY_SAVINGS_ACCOUNT_PRODUCT_BUSINESS)){
            flag=CONSTANTS.ORDINARY_SAVINGS_ACCOUNT_FLAG;
        } else if (accountType.equals(accountType.equals(CONSTANTS.SHARE_CAPITAL_PRODUCT))) {
            flag=CONSTANTS.SHARE_CAPITAL_ACCOUNT_FLAG;
        }else if (accountType.equals(accountType.equals(CONSTANTS.DEPOSIT_CONTRIBUTION_PRODUCT))){
            flag=CONSTANTS.DEPOSIT_CONTRIBUTION_ACCOUNT_FLAG;
        }
        return flag;
    }

    //TODO: NORMAL

    public EntityResponse<?> createSavingsAccount(Account account) {
        try {
            String productCode=account.getProductCode();
            //get product details
            String customerCode=account.getCustomerCode();
            EntityResponse numberOfAccountsValidator= validatorsService.retailCustomerNumberOfAccountValidator(productCode,customerCode);
            if(!numberOfAccountsValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {

                EntityResponse specificProduct= itemServiceCaller.getSavingsAccountProductDetails(productCode);
                if(specificProduct.getStatusCode().equals(HttpStatus.OK.value())){
                    // get account product type
                    if(productCode.equals(CONSTANTS.SHARE_CAPITAL_PRODUCT)){
                        account.setSavingsAccountTypeFlag(CONSTANTS.SHARE_CAPITAL_ACCOUNT_FLAG);
                    } else if (productCode.equals(CONSTANTS.DEPOSIT_CONTRIBUTION_PRODUCT)) {
                        account.setSavingsAccountTypeFlag(CONSTANTS.DEPOSIT_CONTRIBUTION_ACCOUNT_FLAG);
                    }else if(productCode.equals(CONSTANTS.ORDINARY_SAVINGS_ACCOUNT_PRODUCT_PERSONAL) || productCode.equals(CONSTANTS.ORDINARY_SAVINGS_ACCOUNT_PRODUCT_BUSINESS)){
                        account.setSavingsAccountTypeFlag(CONSTANTS.ORDINARY_SAVINGS_ACCOUNT_FLAG);
                    }
                    SavingsAccountProduct savingsAccountProduct= (SavingsAccountProduct) specificProduct.getEntity();

                    //save account two
                    return postAccount(account);
                }else {
                    return specificProduct;
                }
            }else {
                return numberOfAccountsValidator;
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

    public EntityResponse<Account> setAccountGeneralProperties(Account account){
        try{
            log.info("setting general properties");
            EntityResponse entityResponse=new EntityResponse<>();

            String postedBy =UserRequestContext.getCurrentUser();

            account.setPostedFlag(CONSTANTS.YES);
            account.setPostedTime(new Date());
            account.setOpeningDate(new Date());
            account.setPostedBy(postedBy);
            account.setDeleteFlag(CONSTANTS.NO);
            account.setVerifiedFlag(CONSTANTS.NO);
            account.setLienAmount(0.0);
            account.setAccountStatus(CONSTANTS.ACTIVE);
            account.setEntityId(EntityRequestContext.getCurrentEntityId());
            account.setOverdrawingPower(CONSTANTS.NO);


            String productCode=account.getProductCode();
            String customerCode = account.getCustomerCode();
            if(account.getAccountType().equals(CONSTANTS.OFFICE_ACCOUNT)){

                entityResponse.setMessage(HttpStatus.OK.getReasonPhrase());
                entityResponse.setStatusCode(HttpStatus.OK.value());
                entityResponse.setEntity(account);

            }else {
                Optional<ValidatorsRepo.ProductGlInterface> productGlInterface=validatorsRepo.getProductGlInfo(productCode);
                if(productGlInterface.isPresent()){
                    ValidatorsRepo.ProductGlInterface existingProductGlInterface=productGlInterface.get();
                    String glSubHead= existingProductGlInterface.getGl_subhead();
                    log.info("glSubhead:"+glSubHead);

                    Optional<RetailCustomerItem> retailCustomerItem=validatorsRepo.getRetailCustomerInfo(customerCode);
                    if(retailCustomerItem.isPresent()){
                        log.info("retailCustomerItem:"+retailCustomerItem.toString());
                        RetailCustomerItem exsitingRetailCustomerItem=retailCustomerItem.get();
                        String branchCode=exsitingRetailCustomerItem.getbranch_code();
                        String phoneNumber =exsitingRetailCustomerItem.getPhone_number();
                        log.info("branchCode:"+branchCode);

                        account.setSolCode(branchCode);
                        account.setTransactionPhone(phoneNumber);
                        account.setGlSubhead(glSubHead);
                        account.setAccountBalance(0.0);


                        entityResponse.setMessage(HttpStatus.OK.getReasonPhrase());
                        entityResponse.setStatusCode(HttpStatus.OK.value());
                        entityResponse.setEntity(account);

                        log.info("Completed setting account general info :"+retailCustomerItem.toString());

                    }else {
                        Optional<RetailCustomerItem> groupCustomer=validatorsRepo.getGroupMemberInfo(customerCode);
                        if(groupCustomer.isPresent()){
                            log.info("groupCustomer:"+groupCustomer.toString());
                            RetailCustomerItem exsitingGroupCustomerItem=groupCustomer.get();
                            String branchCode=exsitingGroupCustomerItem.getbranch_code();
                            String phoneNumber =exsitingGroupCustomerItem.getPhone_number();
                            log.info("branchCode:"+branchCode);

                            account.setSolCode(branchCode);
                            account.setTransactionPhone(phoneNumber);
                            account.setGlSubhead(glSubHead);
                            account.setAccountBalance(0.0);


                            entityResponse.setMessage(HttpStatus.OK.getReasonPhrase());
                            entityResponse.setStatusCode(HttpStatus.OK.value());
                            entityResponse.setEntity(account);
                        }else {
                            entityResponse.setMessage("COULD NOT FIND ATTACHED CUSTOMER CODE");
                            entityResponse.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        }
                    }
                }else {
                    entityResponse.setMessage("COULD NOT FIND GL SUBHEAD ATTACHED TO PRODUCT");
                    entityResponse.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                }
            }

            return entityResponse;
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
    public EntityResponse<Account> postAccount(Account account) {
        try {
            log.info("Saving account");
            EntityResponse accountGeneralDetailsSetter= setAccountGeneralProperties(account);
            if(accountGeneralDetailsSetter.getStatusCode().equals(HttpStatus.OK.value())){

                Account newAccount = (Account) accountGeneralDetailsSetter.getEntity();



                String prodCode= newAccount.getProductCode();
                String customerCode= newAccount.getCustomerCode();
                String glSubHead= newAccount.getGlSubhead();
                String branchCode= newAccount.getSolCode();
                String acType= newAccount.getAccountType();

                /** Generate acid**/
                String acid=null;

                if(newAccount.getAccountType().equals(CONSTANTS.OFFICE_ACCOUNT)){
                    acid=acidGenerator.generateAcidForOfficeAccount(glSubHead);
                    newAccount.setAcid(acid);
                    Account sAccount = accountRepository.save(newAccount);
                    EntityResponse response = new EntityResponse();
                    response.setMessage("ACCOUNT CREATED SUCCESSFULLY: ACCOUNT NUMBER: "+sAccount.getAcid());
                    response.setStatusCode(HttpStatus.CREATED.value());
                    response.setEntity(sAccount);
                    return response;
                }else {
                    log.info("Generating acid");
                    EntityResponse acidRes= acidGenerator.generateAcid1(prodCode,
                            acType,
                            branchCode,
                            glSubHead,
                            customerCode);
                    if(acidRes.getStatusCode().equals(HttpStatus.OK.value())) {
                        acid= (String) acidRes.getEntity();
                        newAccount.setAcid(acid);

                        Account sAccount = accountRepository.save(newAccount);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("ACCOUNT CREATED SUCCESSFULLY: ACCOUNT NUMBER: "+sAccount.getAcid());
                        response.setStatusCode(HttpStatus.CREATED.value());
                        response.setEntity(sAccount);
                        return response;
                    }else {
                        return acidRes;
                    }
                }

            }else {
                return accountGeneralDetailsSetter;
            }

        }catch (Exception e){
            log.info("Catched Error {} " + e);


            EntityResponse response = new EntityResponse();
            response.setMessage(e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity(e.getCause());
            return response;
//            return null;
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

    //TODO: GENERAL VALIDATION
    public List<EntityResponse> generalValidation(Account account){
        try{
            List<EntityResponse> errors=new ArrayList<>();
            EntityResponse res=new EntityResponse<>();
            //1. account type validator
            log.info("Validating account type");
            String accountType= account.getAccountType();
            res =validatorsService.accountTypeValidator(account);
            if(!res.getStatusCode().equals(HttpStatus.OK.value())){
                errors.add(res);
            }
            //2. Product code validator
            log.info("Validating product code");
            String productCode= account.getProductCode();
            res=validatorsService.productValidator(productCode);
            if(!res.getStatusCode().equals(HttpStatus.OK.value())){
                errors.add(res);
            }

            if(!account.getAccountType().equals(CONSTANTS.OFFICE_ACCOUNT)){
                //3. retail customer validator
                log.info("Validating customer code");
                String customerCode = account.getCustomerCode();
                res= validatorsService.retailCustomerValidator(customerCode);
                if(!res.getStatusCode().equals(HttpStatus.OK.value())){
                    errors.add(res);
                }
                //4. related parties validator
                log.info("Validating related parties");
                if(validatorsService.hasRelatedParties(account)){
                    List<RelatedParties> relatedPartiesList=account.getRelatedParties();
                    res = validatorsService.relatedPartiesValidator(relatedPartiesList);
                    if(!res.getStatusCode().equals(HttpStatus.OK.value())){
                        errors.add(res);
                    }
                }
            }
            return errors;

        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }
    public List<EntityResponse> validateSpecificDetails(Account account){
        try {
            String accountType= account.getAccountType();
            if(accountType.equals(CONSTANTS.LOAN_ACCOUNT)){
                return loanAccountValidation(account);
            }else if(accountType.equals(CONSTANTS.SAVINGS_ACCOUNT)){
                return savingsAccountValidator(account);
            }else {
                return generalValidation(account);
            }
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }

    public List<EntityResponse> loanAccountValidation(Account account){
        try{
            List<EntityResponse> errors=new ArrayList<>();
            EntityResponse res=new EntityResponse<>();
            Double principalAmt= account.getLoan().getPrincipalAmount();
            String productCode= account.getProductCode();
            Date interestCalcStartDate=loanCalculatorService.getInterestCalculationStartDateDate(account.getLoan());
            Date maturityDate= loanCalculatorService.calculateMaturityDate(interestCalcStartDate,
                    account.getLoan().getFrequencyPeriod(),
                    account.getLoan().getFrequencyId(),
                    account.getLoan().getNumberOfInstallments());
            Long loanPeriodInMonths=loanCalculatorService.calculateLoanPeriodMonths(interestCalcStartDate,  maturityDate);
            String operativeAccountId= account.getLoan().getOperativeAcountId();
            //1. Product interest validator
            log.info("<<Validating product interest details>>");
            res= productInterestService.getLoanInterest(productCode, principalAmt);
            if(!res.getStatusCode().equals(HttpStatus.OK.value())){
                errors.add(res);
            }
            //2. Principal amt and period validator
            log.info("<<Validating Loan amount and period validator>>");
            res=validatorsService.loanAmountAndPeriodValidator(principalAmt,
                    loanPeriodInMonths,
                    productCode);
            if(!res.getStatusCode().equals(HttpStatus.OK.value())){
                errors.add(res);
            }
            //3. Operative account validation
            res =validatorsService.acidValidator(operativeAccountId,
                    "Operative");
            if(!res.getStatusCode().equals(HttpStatus.OK.value())){
                errors.add(res);
            }
            //4. Operative account withdrawal validator
            return errors;

        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }

    public List<EntityResponse> savingsAccountValidator(Account account){
        try{
            //1. Product interest validator
            List<EntityResponse> errors=new ArrayList<>();
            EntityResponse res=new EntityResponse<>();
            res= validatorsService.retailCustomerNumberOfAccountValidator(account.getProductCode(),
                    account.getCustomerCode());

            if(!res.getStatusCode().equals(HttpStatus.OK.value())){
                errors.add(res);
            }
            return errors;

        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }




}
