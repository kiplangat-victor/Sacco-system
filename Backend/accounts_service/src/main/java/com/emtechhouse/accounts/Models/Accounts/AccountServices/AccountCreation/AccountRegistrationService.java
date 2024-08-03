package com.emtechhouse.accounts.Models.Accounts.AccountServices.AccountCreation;

import com.emtechhouse.accounts.Exception.ApiRequestException;
import com.emtechhouse.accounts.Models.Accounts.Account;
import com.emtechhouse.accounts.Models.Accounts.AccountRepository;
import com.emtechhouse.accounts.Models.Accounts.Loans.Loan;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanCalculatorService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDisbursment.LoanDisbursmentService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanGuarantor.LoanGuarantor;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.GeneralProductDetails;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ProductInterestDetails.ProductInterestDetails;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ProductInterestDetails.ProductInterestService;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ItemServiceCaller;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.SpecificProductDetails.SavingsAccountProduct;
import com.emtechhouse.accounts.Models.Accounts.RelatedParties.RelatedParties;
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
public class AccountRegistrationService {
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

//    @Autowired
//    private LoanGuarantorService loanGuarantorService;


    //list all validators


    public EntityResponse registerAccount(Account account) {
        try {
            EntityResponse userValidator= validatorsService.userValidator();
            if(userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                return userValidator;
            } else {
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
                            }  else {
                                /** checking if the account has related parties and each related party is registered
                                 * in the crm module IE has customer code
                                 */
                                if(validatorsService.hasRelatedParties(account)) {
                                    List<RelatedParties> relatedPartiesList=account.getRelatedParties();
                                    EntityResponse relatedPartiesValidator = validatorsService.relatedPartiesValidator(relatedPartiesList);
                                    if(relatedPartiesValidator.getStatusCode().equals(HttpStatus.BAD_REQUEST.value())) {
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
                    TermDeposit termDeposit = account.getTermDeposit();
                    String interestCrAccountId = termDeposit.getInterestCrAccountId();
                    //validate interest account
                    EntityResponse acidValidator = validatorsService.acidValidator(interestCrAccountId, "Attached interest credit");
                    if (acidValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                        return acidValidator;
                    } else {
                        TermDeposit newTermDeposit = termDepositSetter(termDeposit);
                        account.setTermDeposit(newTermDeposit);
                        account.setTermDeposit(newTermDeposit);
                        return postAccount(account);
                    }

                case CONSTANTS.LOAN_ACCOUNT:
                    return postLoanAccount(account);

                case CONSTANTS.SAVINGS_ACCOUNT:
                    log.info("doing retail customer account validator");
                    String productCode = account.getProductCode();
                    String customerCode = account.getCustomerCode();
                    EntityResponse numberOfAccountsValidator = validatorsService.retailCustomerNumberOfAccountValidator(productCode, customerCode);
                    if (numberOfAccountsValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                        return numberOfAccountsValidator;
                    } else {
                        return postSavingsAccount(account);
                    }
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
            log.info("Posting loan account");
            Loan loan=account.getLoan();
            Double principalAmt=loan.getPrincipalAmount();
            String customerCode=account.getCustomerCode();
            //validate all the guarantors
            List<LoanGuarantor> loanGuarantors=loan.getLoanGuarantors();
            System.out.println("Validating loan limit from savings account");
            EntityResponse loanLimitValidator= validatorsService.validateLoanLimit(customerCode,principalAmt);
            if(loanLimitValidator.getStatusCode().equals(HttpStatus.OK.value())){
                System.out.println("Completed Validating loan limit savings account");
                // get account interest rate if not found throw an error
                String prodCode= account.getProductCode();
                System.out.println("Product code: "+ prodCode);
                Double principalAmount= account.getLoan().getPrincipalAmount();
                System.out.println("Principal amount: "+ principalAmount);
                //get interest details
                System.out.println("Getting loan interest from product");
                EntityResponse loanInterestResponse= productInterestService.getLoanInterest(prodCode, principalAmount);
                if(loanInterestResponse.getStatusCode().equals(HttpStatus.NOT_FOUND.value())){
                    log.error("Product interest not found");
                    return loanInterestResponse;
                }else {
                    ProductInterestDetails productInterestDetails= (ProductInterestDetails) loanInterestResponse.getEntity();
                    Double productLoanInterestRate=productInterestDetails.getInterestRate();
//                    Double productLoanInterestRate=12.0;
                    String productInterestCalculationMethod=productInterestDetails.getInterestCalculationMethod();
//                    String productInterestCalculationMethod="reducing_balance";
                    String productInterestPeriod=productInterestDetails.getInterestPeriod();
//                    String productInterestPeriod="p.a";
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
                    System.out.println("Annula interest rate :"+loanAnnualInterestRate);
                    String operativeAccountId= loan.getOperativeAcountId();
                    loan.setInterestCalculationStartDate(loanCalculatorService.getInterestCalculationStartDateDate(loan));
                    Long loanPeriodInMonths=loanCalculatorService.calculateLoanPeriodMonths(loan);
                    loan.setLoanPeriodMonths(loanPeriodInMonths);
                    //validate interest
                    EntityResponse principalAmountAndPeriodValidator=validatorsService.loanAmountAndPeriodValidator(loan.getPrincipalAmount(),
                            loanPeriodInMonths,
                            loan.getAccount().getProductCode());
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
//                            if(acidWithdrawalValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
//                                return acidWithdrawalValidator;
//                            }else {
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

                                loan.setLoanFees(loanDisbursmentService.getLoanFees(prodCode,principalAmt,loan));

                                loan.setLoanStatus(CONSTANTS.NEW);
                                loan.setDisbursementFlag(CONSTANTS.NO);
                                loan.setDisbursementVerifiedFlag(CONSTANTS.NO);
                                loan.setPayOffFlag(CONSTANTS.NO);
                                loan.setAssetClassification(CONSTANTS.NOT_CLASSIFIED);
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

        }catch (Exception e) {
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
    public EntityResponse postSavingsAccount(Account account){
        try{
            String accountName= account.getAccountName();
            List<String> messages =new ArrayList<>();
            EntityResponse entityResponses= new EntityResponse();

            String productCode= account.getProductCode();
            if(productCode.equals(CONSTANTS.SAVINGS_NON_WITHDRAWABLE_PROD)) {
                EntityResponse acOneEtRes=createSavingsNonWithdrawableAccount(account, accountName);
                EntityResponse acTwo=createShareCapitalAccount(account, accountName);
                EntityResponse acThree=createRepaymentAccount(account, accountName);

                String messageOne=acOneEtRes.getMessage();
                String messageTwo=acTwo.getMessage();
                String messageThree=acThree.getMessage();

                messages.add(acOneEtRes.getMessage());
                messages.add(acTwo.getMessage());
                messages.add(acThree.getMessage());
                entityResponses.setEntity(messages);
                entityResponses.setStatusCode(acOneEtRes.getStatusCode());
                entityResponses.setMessage(messageOne+",\n "+messageTwo+",\n "+messageThree);

                return entityResponses;
            }else if(productCode.equals(CONSTANTS.SHARE_CAPITAL_PROD)){
                EntityResponse acTwo=createShareCapitalAccount(account, accountName);
                return acTwo;
            } else if (productCode.equals(CONSTANTS.REPAYMENT_ACCOUNT_PROD)) {
                EntityResponse acThree=createRepaymentAccount(account, accountName);
                return acThree;
            }else {
                EntityResponse acThree=createSavingsNonWithdrawableAccount(account, accountName);
                return acThree;
            }

//            return createSavingsAccount(account);

        }catch (Exception e){
            log.info("Catched Error {} " + e);

            EntityResponse response = new EntityResponse();
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity(e.getCause());
            return response;
        }
    }
    public EntityResponse<?> createSavingsNonWithdrawableAccount(Account account, String acName){
        try {
            log.info("crating savings non withdrawable");
//            account= setAccountGeneralProperties(account);
            String productCode= account.getProductCode();
            String customerCode=account.getCustomerCode();
            EntityResponse numberOfAccountsValidator= validatorsService.retailCustomerNumberOfAccountValidator(productCode,customerCode);
            if(!numberOfAccountsValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                //get product details
                EntityResponse genProductDetailsEtyRes= itemServiceCaller.getGeneralProductDetail1(productCode,CONSTANTS.SAVINGS_ACCOUNT);
                if(genProductDetailsEtyRes.getStatusCode().equals(HttpStatus.OK.value())){
                    GeneralProductDetails generalProductDetails = (GeneralProductDetails) genProductDetailsEtyRes.getEntity();
                    //get specific product details
                    EntityResponse specificProduct= itemServiceCaller.getSavingsAccountProductDetails(productCode);
                    if(specificProduct.getStatusCode().equals(HttpStatus.OK.value())){
                        SavingsAccountProduct savingsAccountProduct= (SavingsAccountProduct) specificProduct.getEntity();
                        String productName= generalProductDetails.getProductCodeDesc();
                        acName= acName+" "+productName;
                        account.setAccountName(acName);
                        account.setId(0L);
                        account.setSavingsAccountTypeFlag(CONSTANTS.DEPOSIT_CONTRIBUTION_ACCOUNT_FLAG);
                        Boolean noWithdrawalAllowed=savingsAccountProduct.getWithdrawalsAllowed();
                        if(noWithdrawalAllowed){
                            account.setIsWithdrawalAllowed(false);
                        }else {
                            account.setIsWithdrawalAllowed(true);
                        }

                        //save account two
                        EntityResponse etyAcTwo=postAccount(account);
                        if(etyAcTwo.getStatusCode().equals(HttpStatus.CREATED.value())){
                            Account account1= (Account) etyAcTwo.getEntity();
                            String acNo=account1.getAcid();
                            EntityResponse response = new EntityResponse();
                            response.setMessage("SAVINGS NON WITHDRWABLE ACCOUNT: ACCOUNT NUMBER: "+acNo);
                            response.setStatusCode(HttpStatus.CREATED.value());
                            return response;
                        }else {
                            EntityResponse response = new EntityResponse();
                            response.setMessage("FAILED! COULD NOT CREATE SAVINGS NON WITHDRAWABLE ACCOUNT "+ etyAcTwo.getMessage());
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            return response;
                        }
                    }else {
                        return specificProduct;
                    }

                }else {
                    return genProductDetailsEtyRes;
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

    public EntityResponse<?> createRepaymentAccount(Account account, String acName){
        try {
            log.info("crating savings repayment account");
//            account= setAccountGeneralProperties(account);
            log.info("acName "+account.getAccountName());
            String productCode=CONSTANTS.REPAYMENT_ACCOUNT_PROD;
            String customerCode=account.getCustomerCode();
            EntityResponse numberOfAccountsValidator= validatorsService.retailCustomerNumberOfAccountValidator(productCode,customerCode);
            if(!numberOfAccountsValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                EntityResponse genProductDetailsEtyRes= itemServiceCaller.getGeneralProductDetail1(productCode,CONSTANTS.SAVINGS_ACCOUNT);
                if(genProductDetailsEtyRes.getStatusCode().equals(HttpStatus.OK.value())){
                    GeneralProductDetails generalProductDetails = (GeneralProductDetails) genProductDetailsEtyRes.getEntity();
                    //get specific product details
                    EntityResponse specificProduct= itemServiceCaller.getSavingsAccountProductDetails(productCode);
                    if(specificProduct.getStatusCode().equals(HttpStatus.OK.value())){
                        String productTwoName= generalProductDetails.getProductCodeDesc();
                        acName= acName+" "+productTwoName;
                        account.setProductCode(productCode);
                        account.setAccountName(acName);
                        account.setSavingsAccountTypeFlag(CONSTANTS.ORDINARY_SAVINGS_ACCOUNT_FLAG);
                        SavingsAccountProduct savingsAccountProduct= (SavingsAccountProduct) specificProduct.getEntity();
                        Boolean noWithdrawalAllowed=savingsAccountProduct.getWithdrawalsAllowed();
                        if(noWithdrawalAllowed){
                            account.setIsWithdrawalAllowed(false);
                        }else {
                            account.setIsWithdrawalAllowed(true);
                        }
                        //save account two
                        EntityResponse etyAcTwo=postAccount(account);
                        if(etyAcTwo.getStatusCode().equals(HttpStatus.CREATED.value())){
                            Account account1= (Account) etyAcTwo.getEntity();
                            String acNo=account1.getAcid();
                            EntityResponse response = new EntityResponse();
                            response.setMessage("DEPOSIT CONTRIBUTION ACCOUNT CREATED SUCCESSFULLY: ACCOUNT NUMBER: "+acNo);
                            response.setStatusCode(HttpStatus.CREATED.value());
                            return response;
                        }else {
                            EntityResponse response = new EntityResponse();
                            response.setMessage("FAILED! COULD NOT CREATE DEPOSIT CONTRIBUTION ACCOUNT: "+etyAcTwo.getMessage() );
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            return response;
                        }
                    }else {
                        return specificProduct;
                    }

                }else {
                    return genProductDetailsEtyRes;
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
    public EntityResponse<?> createShareCapitalAccount(Account account, String acName){
        try {
            log.info("crating share capital account");
//            account= setAccountGeneralProperties(account);
            log.info("acName "+account.getAccountName());
            String productCode=CONSTANTS.SHARE_CAPITAL_PROD;
            //get product details
            String customerCode=account.getCustomerCode();
            EntityResponse numberOfAccountsValidator= validatorsService.retailCustomerNumberOfAccountValidator(productCode,customerCode);
            if(!numberOfAccountsValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                EntityResponse genProductDetailsEtyRes= itemServiceCaller.getGeneralProductDetail1(productCode,CONSTANTS.SAVINGS_ACCOUNT);
                if(genProductDetailsEtyRes.getStatusCode().equals(HttpStatus.OK.value())){
                    GeneralProductDetails generalProductDetails = (GeneralProductDetails) genProductDetailsEtyRes.getEntity();
                    EntityResponse specificProduct= itemServiceCaller.getSavingsAccountProductDetails(productCode);
                    if(specificProduct.getStatusCode().equals(HttpStatus.OK.value())){
                        String productTwoName= generalProductDetails.getProductCodeDesc();
                        acName= acName+" "+productTwoName;
                        account.setProductCode(productCode);
                        account.setAccountName(acName);
                        account.setSavingsAccountTypeFlag(CONSTANTS.SHARE_CAPITAL_ACCOUNT_FLAG);
                        SavingsAccountProduct savingsAccountProduct= (SavingsAccountProduct) specificProduct.getEntity();
                        Boolean noWithdrawalAllowed=savingsAccountProduct.getWithdrawalsAllowed();
                        if(noWithdrawalAllowed){
                            account.setIsWithdrawalAllowed(false);
                        }else {
                            account.setIsWithdrawalAllowed(true);
                        }
                        //save account two
                        EntityResponse etyAcTwo=postAccount(account);
                        if(etyAcTwo.getStatusCode().equals(HttpStatus.CREATED.value())){
                            Account account1= (Account) etyAcTwo.getEntity();
                            String acNo=account1.getAcid();
                            EntityResponse response = new EntityResponse();
                            response.setMessage("SHARE CAPITAL ACCOUNT CREATED SUCCESSFULLY: ACCOUNT NUMBER: "+acNo);
                            response.setStatusCode(HttpStatus.CREATED.value());
                            return response;
                        }else {
                            EntityResponse response = new EntityResponse();
                            response.setMessage("FAILED! COULD NOT CREATE SHARE CAPITAL ACCOUNT: "+etyAcTwo.getMessage());
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            return response;
                        }
                    }else {
                        return specificProduct;
                    }

                }else {
                    return genProductDetailsEtyRes;
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

    //TODO: NORMAL

    public EntityResponse<?> createSavingsAccount(Account account){
        try {
            String productCode=account.getProductCode();
            //get product details
            String customerCode=account.getCustomerCode();
            EntityResponse numberOfAccountsValidator= validatorsService.retailCustomerNumberOfAccountValidator(productCode,customerCode);
            if(!numberOfAccountsValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){

                EntityResponse specificProduct= itemServiceCaller.getSavingsAccountProductDetails(productCode);
                if(specificProduct.getStatusCode().equals(HttpStatus.OK.value())){
                    // get account product type
                    if(productCode.equals(CONSTANTS.SHARE_CAPITAL_PROD)){
                        account.setSavingsAccountTypeFlag(CONSTANTS.SHARE_CAPITAL_ACCOUNT_FLAG);
                    } else if (productCode.equals(CONSTANTS.SAVINGS_NON_WITHDRAWABLE_PROD)) {
                        account.setSavingsAccountTypeFlag(CONSTANTS.DEPOSIT_CONTRIBUTION_ACCOUNT_FLAG);
                    }else if(productCode.equals(CONSTANTS.REPAYMENT_ACCOUNT_PROD)){
                        account.setSavingsAccountTypeFlag(CONSTANTS.ORDINARY_SAVINGS_ACCOUNT_FLAG);
                    }
                    SavingsAccountProduct savingsAccountProduct= (SavingsAccountProduct) specificProduct.getEntity();
                    Boolean noWithdrawalAllowed=savingsAccountProduct.getWithdrawalsAllowed();
                    if(noWithdrawalAllowed){
                        account.setIsWithdrawalAllowed(false);
                    }else {
                        account.setIsWithdrawalAllowed(true);
                    }
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
    public EntityResponse<Account> postAccount(Account account){
        try {
            EntityResponse accountGeneralDetailsSetter= setAccountGeneralProperties(account);
            if(accountGeneralDetailsSetter.getStatusCode().equals(HttpStatus.OK.value())){

                Account newAccount = (Account) accountGeneralDetailsSetter.getEntity();
                String prodCode=newAccount.getProductCode();
                String customerCode= account.getCustomerCode();
                String glSubHead=account.getGlSubhead();

                /** Generate acid**/
                String acid=null;
                if(account.getAccountType().equals(CONSTANTS.LOAN_ACCOUNT)){
                    acid=acidGenerator.generateAcidForLoanAccount(prodCode);
                }else if(account.getAccountType().equals(CONSTANTS.OFFICE_ACCOUNT)){
                    acid=acidGenerator.generateAcidForOfficeAccount(glSubHead);
                }else {
                    acid=acidGenerator.generateAcidForSavingsAccount(prodCode,customerCode);
                }
                account.setAcid(acid);

                Account sAccount = accountRepository.save(newAccount);
                EntityResponse response = new EntityResponse();
                response.setMessage("ACCOUNT CREATED SUCCESSFULLY: ACCOUNT NUMBER: "+sAccount.getAcid());
                response.setStatusCode(HttpStatus.CREATED.value());
                response.setEntity(sAccount);
                return response;
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
            String accountType= account.getAccountType();
            res =validatorsService.accountTypeValidator(account);
            if(!res.getStatusCode().equals(HttpStatus.OK.value())){
                errors.add(res);
            }
            //2. Product code validator
            String productCode= account.getProductCode();
            res=validatorsService.productValidator(productCode);
            if(!res.getStatusCode().equals(HttpStatus.OK.value())){
                errors.add(res);
            }

            if(!account.getAccountType().equals(CONSTANTS.OFFICE_ACCOUNT)){
                //3. retail customer validator
                String customerCode = account.getCustomerCode();
                res= validatorsService.retailCustomerValidator(customerCode);
                if(!res.getStatusCode().equals(HttpStatus.OK.value())){
                    errors.add(res);
                }
                //4. related parties validator
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


    //-------------------MIGRATION LOAN ACCOUNTS--------------------------------//

    public EntityResponse<Account> registerAccountMigration(Account account){
        try{
            System.out.println("registering account for migration");
            EntityResponse userValidator= validatorsService.userValidator();
            log.info("User validator");
            if(userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                return userValidator;
            }else {
                String user =UserRequestContext.getCurrentUser();
                /**Account type validator
                 * Confirming if the submitted account detail are of the selected account type
                 * **/
                EntityResponse accountTypeValidator =validatorsService.accountTypeValidator(account);
                log.info("ac type validator");
                if(accountTypeValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                    return accountTypeValidator;
                }else {
                    /**get account type /**/
                    String accountType=account.getAccountType();
                    /**-------------------------office account------------------------------------------------**/
                    if(accountType.equals(CONSTANTS.OFFICE_ACCOUNT)){
//                        /** check if product code and product type exists**/
//                        String productCode= account.getProductCode();
//                        EntityResponse productValidator=validatorsService.officeProductValidator(productCode);
//                        if(productValidator.getStatusCode().equals(404)){
//                            return productValidator;
//                        }else{
//                            return stepTwoValidation(account);
//                        }
                        return stepTwoValidationMigration(account);

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
                                        return stepTwoValidationMigration(account);
                                    }
                                }else {
                                    return stepTwoValidationMigration(account);
                                }
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            throw new ApiRequestException("failed");
//            return null;
        }

    }

    public EntityResponse<Account> stepTwoValidationMigration(Account account){
        try{
            log.info("step two validation");
            //confirm account type
            if(account.getAccountType().equals(CONSTANTS.TERM_DEPOSIT)){
                TermDeposit termDeposit= account.getTermDeposit();
                String interestCrAccountId= termDeposit.getInterestCrAccountId();
                //validate interest account
                EntityResponse acidValidator =validatorsService.acidValidator(interestCrAccountId, "Attached interest credit");
                if(acidValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                    return acidValidator;
                }else {
                    TermDeposit newTermDeposit = termDepositSetter(termDeposit);
                    account.setTermDeposit(newTermDeposit);
                    account.setTermDeposit(newTermDeposit);
                    return postAccount(account);
                }

            }else if(account.getAccountType().equals(CONSTANTS.LOAN_ACCOUNT)){
                return postLoanAccountMigration(account);

            } else if (account.getAccountType().equals(CONSTANTS.SAVINGS_ACCOUNT)) {
                log.info("doing retail customer account validator");
                String productCode =account.getProductCode();
                String customerCode =account.getCustomerCode();
                EntityResponse numberOfAccountsValidator= validatorsService.retailCustomerNumberOfAccountValidator(productCode,customerCode);
                if(numberOfAccountsValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                    return numberOfAccountsValidator;
                }else {
                    return postSavingsAccount(account);
                }
            } else{
                return postAccount(account);
            }
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse postLoanAccountMigration(Account account){
        try {
            log.info("posting migration loan account specifi details*******");
            Loan loan=account.getLoan();
            Double principalAmt=loan.getPrincipalAmount();
            String customerCode=account.getCustomerCode();
            // get account interest rate if not found throw an error
            String prodCode= account.getProductCode();
            Double principalAmount= account.getLoan().getPrincipalAmount();
            //get interest details
            Double productLoanInterestRate=loan.getInterestRate();
            String productInterestCalculationMethod="reducing_balance";
            String productInterestPeriod="p.a";

            if(account.getProductCode().equalsIgnoreCase("LA30")){
                productInterestCalculationMethod="reducing_balance";
                Double totalLoanBalance=principalAmount;
                loan.setTotalLoanBalance(totalLoanBalance);
            }else {
                productInterestCalculationMethod="flat_rate";
            }

            String loanInterestCalculationMethod = null;
            Double loanAnnualInterestRate = null;
            log.info("Getting interest calculation method*******");
            if(productInterestCalculationMethod.equalsIgnoreCase("flat_rate")){
                loanInterestCalculationMethod=CONSTANTS.FLAT_RATE;
            } else if (productInterestCalculationMethod.equalsIgnoreCase("reducing_balance")) {
                loanInterestCalculationMethod=CONSTANTS.REDUCING_BALANCE;
            }

            log.info("productInterestCalculationMethod******* :"+loanInterestCalculationMethod);

            if(productInterestPeriod.equalsIgnoreCase("p.m")){

                loanAnnualInterestRate=(productLoanInterestRate*12);

            } else if (productInterestPeriod.equalsIgnoreCase("p.a")) {

                loanAnnualInterestRate=productLoanInterestRate;
            }
            log.info("loanAnnualInterestRate******* :"+loanAnnualInterestRate);
            log.info("installment start date******* :"+loan.getInstallmentStartDate().toString());

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
            log.info("Interest preferential is :: "+interestPreferential);
            //set loan interest
//                loan.setInterestRate(intersetRate);

            String operativeAccountId= loan.getOperativeAcountId();
            String productCode =loan.getAccount().getProductCode();
            Double loanPrincipalAmount= loan.getPrincipalAmount();
            log.info("Getting loan interest calculation start date");
            loan.setInterestCalculationStartDate(loanCalculatorService.getInterestCalculationStartDateDate(loan));
            log.info("Getting loan period in months");
            Long loanPeriodInMonths=loanCalculatorService.calculateLoanPeriodMonths(loan);
            log.info("Loan period in months is :: "+loanPeriodInMonths);
            loan.setLoanPeriodMonths(loanPeriodInMonths);
            //validate interest
//            EntityResponse principalAmountAndPeriodValidator=validatorsService.loanAmountAndPeriodValidator(loan.getPrincipalAmount(),loanPeriodInMonths,loan.getAccount().getProductCode());
            //operative account validation step one
            log.info("Validating operative account");
            EntityResponse operativeAcidValidator =validatorsService.acidValidator(operativeAccountId, "Operative");
            if(operativeAcidValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                return operativeAcidValidator;
            }else {
                // operative account validation step two
                EntityResponse acidWithdrawalValidator =validatorsService.acidWithdrawalValidator(operativeAccountId);
//                            if(acidWithdrawalValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
//                                return acidWithdrawalValidator;
//                            }else {
                Double interestToBePaid=0.0;

                principalAmount=loan.getPrincipalAmount();
                loan.setInterestRate(loanAnnualInterestRate);
                loan.setInterestCalculationMethod(loanInterestCalculationMethod);
                loan.setMaturityDate(loanCalculatorService.calculateMaturityDate(loan));
                log.info("Maturity date set");

//                String interestCalculationMethod=loan.getInterestCalculationMethod();
//                if(interestCalculationMethod.equalsIgnoreCase(CONSTANTS.REDUCING_BALANCE)){
//                    interestToBePaid=loanCalculatorService.calculateTotalInterestToBePaidReducingBalance(loan);
//                    totalLoanBalance=principalAmount+interestToBePaid;
//                }else{
//                    interestToBePaid=loanCalculatorService.calculateInterestToBePaidForFlatRate(loan);
//                    totalLoanBalance=principalAmount+interestToBePaid;
//                }


                Date installmentsStartDate =loan.getInstallmentStartDate();
                log.info("Check 1");
                loan.setInstallmentStartDate(datesCalculator.convertDateTimeStamp(installmentsStartDate));
                log.info("Check 2");
                loan.setNetAmount(loanCalculatorService.calculateNetAmount(loan));
                log.info("Check 3");
                loan.setInterestDemandAmount(loanCalculatorService.calculateInterestDemandAmount(loan));
                log.info("Check 4");
                loan.setPrincipalDemandAmount(loanCalculatorService.calculatePrincipalDemandAmount(loan));
                log.info("Check 5");
                loan.setSumPrincipalDemand(0.00);
                loan.setSumMonthlyFeeDemand(0.00);
                loan.setOverFlowAmount(0.0);

                loan.setNextRepaymentDate(datesCalculator.convertDateTimeStamp(installmentsStartDate));
//                loan.setDisbursementAmount(0.0);
                loan.setOutStandingPrincipal(loan.getPrincipalAmount());

                //flat rate and reducing balance
                loan.setInterestAmount(interestToBePaid);

                loan.setOutStandingInterest(interestToBePaid);


                loan.setLoanPeriodDays(loanCalculatorService.calculateLoanPeriodInDays(loan));
                loan.setDailyInterestAmount(loanCalculatorService.calculateDailyInterestToBePaid(loan));
                loan.setInstallmentAmount(loanCalculatorService.calculateInstallmentAmount(loan));
                loan.setAccrualLastDate(loanCalculatorService.getInterestCalculationStartDateDate(loan));
                loan.setBookingLastDate(loanCalculatorService.getInterestCalculationStartDateDate(loan));
                loan.setLoanSchedules(loanCalculatorService.generateLoanSchedules(loan));
                log.info("Loan schedules generated");

//                loan.setLoanFees(loanDisbursmentService.getLoanFees(productCode,loanPrincipalAmount,loan));

//                loan.setLoanStatus(CONSTANTS.NEW);
//                loan.setDisbursementFlag(CONSTANTS.NO);
//                loan.setDisbursementVerifiedFlag(CONSTANTS.NO);
                account.setLoan(loan);
                account.getLoan().setDemandCarryForward(0);

                log.info("Migration loan Product code is :: "+account.getProductCode());
                if(account.getProductCode().equalsIgnoreCase("LA30")){
                    return postAccountLoanAccountMigration2(account);
                }else {
                    return postAccountLoanAccountMigration(account);
                }


//                            }
            }

        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<Account> setAccountGeneralPropertiesLoanAccountMigration(Account account){
        try{
            log.info("setting general properties for migrated loan acc");
            EntityResponse entityResponse=new EntityResponse<>();

            String postedBy =UserRequestContext.getCurrentUser();
            String EntityId=EntityRequestContext.getCurrentEntityId();

            account.setDeleteFlag(CONSTANTS.NO);
            account.setVerifiedFlag(CONSTANTS.YES);
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


                        entityResponse.setMessage(HttpStatus.OK.getReasonPhrase());
                        entityResponse.setStatusCode(HttpStatus.OK.value());
                        entityResponse.setEntity(account);

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
            return null;
        }
    }



    public EntityResponse<Account> postAccountLoanAccountMigration(Account account){
        try {
            log.info("posting migrated loan account");
            EntityResponse accountGeneralDetailsSetter= setAccountGeneralPropertiesLoanAccountMigration(account);
            if(accountGeneralDetailsSetter.getStatusCode().equals(HttpStatus.OK.value())){
                log.info("status code okay");
                Account newAccount = (Account) accountGeneralDetailsSetter.getEntity();
                log.info("general properties okay");
                String prodCode=newAccount.getProductCode();
//                String customerCode= account.getCustomerCode();
//                String glSubHead=account.getGlSubhead();


                /** Generate acid**/
//                String acid=null;
//                acid=acidGenerator.generateAcidForLoanAccount(prodCode);
//                newAccount.setAcid(acid);
//                if(account.getAccountType().equals(CONSTANTS.LOAN_ACCOUNT)){
//                    acid=acidGenerator.generateAcidForLoanAccount(prodCode);
//                }else if(account.getAccountType().equals(CONSTANTS.OFFICE_ACCOUNT)){
//                    acid=acidGenerator.generateAcidForOfficeAccount(glSubHead);
//                }else {
//                    acid=acidGenerator.generateAcidForSavingsAccount(prodCode,customerCode);
//                }
//                account.setAcid(acid.toString());

                Account sAccount = accountRepository.save(newAccount);
                log.info("saved account");
                EntityResponse response = new EntityResponse();
                response.setMessage("ACCOUNT CREATED SUCCESSFULLY: ACCOUNT NUMBER: "+sAccount.getAcid());
                response.setStatusCode(HttpStatus.CREATED.value());
                response.setEntity(sAccount);
                return response;
            }else {
                return accountGeneralDetailsSetter;
            }

        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<Account> postAccountLoanAccountMigration2(Account account){
        try {
            log.info("posting migrated loan account");
            EntityResponse accountGeneralDetailsSetter= setAccountGeneralPropertiesLoanAccountMigration(account);
            if(accountGeneralDetailsSetter.getStatusCode().equals(HttpStatus.OK.value())){
                log.info("status code okay");
                Account newAccount = (Account) accountGeneralDetailsSetter.getEntity();
                log.info("general properties okay");
                String prodCode=newAccount.getProductCode();
//                String customerCode= account.getCustomerCode();
//                String glSubHead=account.getGlSubhead();


                /** Generate acid**/
//                String acid=null;
//                acid=acidGenerator.generateAcidForLoanAccount(prodCode);
//                newAccount.setAcid(acid);
//                if(account.getAccountType().equals(CONSTANTS.LOAN_ACCOUNT)){
//                    acid=acidGenerator.generateAcidForLoanAccount(prodCode);
//                }else if(account.getAccountType().equals(CONSTANTS.OFFICE_ACCOUNT)){
//                    acid=acidGenerator.generateAcidForOfficeAccount(glSubHead);
//                }else {
//                    acid=acidGenerator.generateAcidForSavingsAccount(prodCode,customerCode);
//                }
//                account.setAcid(acid.toString());

                Account sAccount = accountRepository.save(newAccount);
                log.info("saved account");
                EntityResponse response = new EntityResponse();
                response.setMessage("ACCOUNT CREATED SUCCESSFULLY: ACCOUNT NUMBER: "+sAccount.getAcid());
                response.setStatusCode(HttpStatus.CREATED.value());
                response.setEntity(sAccount);
                return response;
            }else {
                return accountGeneralDetailsSetter;
            }

        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

}
