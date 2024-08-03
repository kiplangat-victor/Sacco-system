//package com.emtechhouse.accounts.Models.Accounts.Migration;//package com.emtechhouse.accounts.Models.Accounts.Migration;
//
//import com.emtechhouse.accounts.Models.Accounts.Account;
//import com.emtechhouse.accounts.Models.Accounts.AccountRepository;
//import com.emtechhouse.accounts.Models.Accounts.AccountServices.AccountCreation.AccountRegistrationService;
//import com.emtechhouse.accounts.Models.Accounts.AccountServices.AccountTransactionService;
//import com.emtechhouse.accounts.Models.Accounts.Loans.Loan;
//import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.DemandSatisfaction.AssetClassificationService;
//import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDisbursment.LoanDisbursmentService;
//import com.emtechhouse.accounts.Models.Accounts.Loans.LoanGuarantor.LoanGuarantor;
//import com.emtechhouse.accounts.Models.Accounts.Loans.LoanGuarantor.LoanGuarantorService;
//import com.emtechhouse.accounts.Models.Accounts.Loans.LoanPayOff.LoanPayOffService;
//import com.emtechhouse.accounts.Models.Accounts.Loans.LoanRepository;
//import com.emtechhouse.accounts.Utils.CONSTANTS;
//import com.emtechhouse.accounts.Utils.DatesCalculator;
//import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
//import io.swagger.models.auth.In;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.concurrent.atomic.AtomicReference;
//
//@Service
//@Slf4j
//public class MigrationService {
//    @Autowired
//    private AccountRegistrationService accountRegistrationService;
//    @Autowired
//    private MigrationRepo migrationRepo;
//    @Autowired
//    private LoanRepository loanRepository;
//    @Autowired
//    private AssetClassificationService assetClassificationService;
//    @Autowired
//    private LoanDisbursmentService loanDisbursmentService;
//    @Autowired
//    private DatesCalculator datesCalculator;
//
//    @Autowired
//    private LoanPayOffService loanPayOffService;
//
//    @Autowired
//    private AccountRepository accountRepository;
//
//    @Autowired
//    private AccountTransactionService accountTransactionService;
//    @Autowired
//    private LoanGuarantorService loanGuarantorService;
//
////
////
////
////    public List<EntityResponse> createAllCustomersAccount(){
////        try{
////            List<MigrationRepo.CustomerInterface> savingsModelList=migrationRepo.getCustomerInfo();
////            log.info("Sizeeeee=="+String.valueOf(savingsModelList.size()));
////            List<EntityResponse> entityResponseList=new ArrayList<>();
////            AtomicReference<Integer> i= new AtomicReference<>(0);
////            savingsModelList.forEach(savingsModel -> {
////                i.getAndSet(i.get() + 1);
////                EntityResponse res=addSavingsAccounts(savingsModel);
////                entityResponseList.add(res);
////                log.info("created:"+ i.get() );
////            });
////            return entityResponseList;
////        }catch (Exception e){
////            log.info("Catched Error {} " + e);
////            return null;
////        }
////    }
////    public List<EntityResponse> createAllGroupCustomersAccount(){
////        try{
////            List<MigrationRepo.CustomerInterface> savingsModelList=migrationRepo.getGroupCustomerInfo();
////            List<EntityResponse> entityResponseList=new ArrayList<>();
////            AtomicReference<Integer> i= new AtomicReference<>(0);
////            savingsModelList.forEach(savingsModel -> {
////                i.getAndSet(i.get() + 1);
////                EntityResponse res=addSavingsAccounts(savingsModel);
////                entityResponseList.add(res);
////                log.info("created:"+ i.get());
////            });
////            return entityResponseList;
////        }catch (Exception e){
////            log.info("Catched Error {} " + e);
////            return null;
////        }
////    }
////
//
//    // TODO: 4/3/2023 flat rate
//    public List<EntityResponse> createAllLoanAccounts() {
//        try{
//            List<LoanInterface> loanInterfaces=migrationRepo.loanAccountsInfo2();
//            System.out.println("Count: "+loanInterfaces.size());
//            List<EntityResponse> entityResponseList=new ArrayList<>();
//            if(loanInterfaces.size()>0){
//                for (Integer i =0; i<loanInterfaces.size();i++) {
//                    System.out.println(i);
//                    System.out.println(loanInterfaces.get(i).getcustomer_code());
//                    if (loanInterfaces.get(i).getcustomer_code().substring(0,2).equalsIgnoreCase("01")) {
//                        System.out.println("Is individual");
//                    }else {
//                        System.out.println("Is group or corporate");
//                    }
//                    EntityResponse entityResponse=addLoanAccount(loanInterfaces.get(i));
//                    entityResponseList.add(entityResponse);
//                }
//            }
//            return entityResponseList;
//        }catch (Exception e){
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }
////
//
//    // TODO: 4/3/2023 reducing balance
//    public List<EntityResponse> createAllLoanAccounts2() {
//        try{
//            List<LoanInterface> loanInterfaces=migrationRepo.loanAccountsInfo();
//            System.out.println("Count: "+loanInterfaces.size());
//            log.info("Count: "+loanInterfaces.size());
//            List<EntityResponse> entityResponseList=new ArrayList<>();
//            if(loanInterfaces.size()>0){
//                for (Integer i =0; i<loanInterfaces.size();i++) {
//                    System.out.println(i);
//                    System.out.println(loanInterfaces.get(i).getcustomer_code());
//                    if (loanInterfaces.get(i).getcustomer_code().substring(0,2).equalsIgnoreCase("01")) {
//                        System.out.println("Is individual");
//                    }else {
//                        System.out.println("Is group or corporate");
//                    }
//                    EntityResponse entityResponse=addLoanAccount2(loanInterfaces.get(i));
//                    entityResponseList.add(entityResponse);
//                }
//            }
//            return entityResponseList;
//        }catch (Exception e){
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }
////
//////    public List<EntityResponse> createGroupCustomersLoanAccounts(){
//////        try{
//////            List<LoanInterface> loanInterfaces=migrationRepo.grploanAccountsInfo();
//////            List<EntityResponse> entityResponseList=new ArrayList<>();
//////            if(loanInterfaces.size()>0){
//////                for (Integer i =0; i<loanInterfaces.size();i++){
//////                    EntityResponse entityResponse=addLoanAccount(loanInterfaces.get(i));
//////                    entityResponseList.add(entityResponse);
//////                }
//////            }
//////            return entityResponseList;
//////        }catch (Exception e){
//////            log.info("Catched Error {} " + e);
//////            return null;
//////        }
//////    }
////    public EntityResponse addSavingsAccounts(MigrationRepo.CustomerInterface savingsModel){
////        try{
////            Account account = new Account();
////            account.setEntityId("001");
////            account.setOpeningDate(new Date());
////            account.setCurrency("KES");
////            account.setProductCode(CONSTANTS.SAVINGS_NON_WITHDRAWABLE_PROD);
////            account.setAccountType(CONSTANTS.SAVINGS_ACCOUNT);
////            account.setAccountName(savingsModel.getAccountName());
////            account.setAccountManager("COLLINS");
////            account.setCustomerCode(savingsModel.getCustomerCode());
////            account.setManagerCode("001001");
////            account.setWithholdingTax(false);
////            account.setCustomerType("01");
////            account.setMisSectorCode("123");
////            account.setAccountOwnership("individual");
////            account.setMisSubSectorCode("1");
////            account.setAccountStatement(true);
////            account.setOperationMode("EITHER TO SIGN");
////            account.setCheckedJointAccount(false);
////            account.setTransferExceptionLimitDr("string");
////            account.setTransferExceptionLimitCr("String");
////            account.setSavings(createSavingModel());
////            return accountRegistrationService.postSavingsAccount(account);
////
////        }catch (Exception e){
////            log.info("Catched Error {} " + e);
////            return null;
////        }
////    }
////
////    public Saving createSavingModel(){
////        try{
////            Saving saving = new Saving();
////            saving.setSba_savingPeriod(1);
////            saving.setSba_maturedDate(new Date());
////            saving.setSba_maturedValue(0.0);
////            saving.setSba_monthlyValue(0.0);
////            saving.setSba_startDate(new Date());
////            return  saving;
////
////        }catch (Exception e){
////            log.info("Catched Error {} " + e);
////            return null;
////        }
////    }
////
//
//    // TODO: 4/3/2023 flat rate
//public Loan createLoanModel(LoanInterface loanInterface){
//    try {
//        Loan loan = new Loan();
//        loan.setLoanType("NORMAL");
//        loan.setPrincipalAmount(loanInterface.getprincipal_amount());
//        loan.setDisbursementFlag(CONSTANTS.NO);
//
//        Integer installmentsNumber=loanInterface.getnumber_of_installments();
//
//
//        loan.setDisbursementAmount(loanInterface.getdisbursement_amount());
//        loan.setFeesAmount(0.00);
//        loan.setDisbursementFlag(CONSTANTS.YES);
//        loan.setDisbursementDate(loanInterface.getdisbursement_date());
//        loan.setDisbursedBy(loanInterface.getApproved_by());
//        loan.setLoanStatus(CONSTANTS.APPROVED);
//        loan.setDisbursementVerifiedFlag(CONSTANTS.NO);
//        loan.setDisbursementAmount(loanInterface.getdisbursement_amount());
//        log.info("getdisbursement_amount: "+loanInterface.getdisbursement_amount());
//            loan.setDisbursementDate(loanInterface.getdisbursement_date());
//            loan.setDisbursmentVerifiedOn(loanInterface.getApproval_date());
//            loan.setDisbursedBy("SYSTEM");
//            loan.setDisbursementVerifiedFlag(CONSTANTS.YES);
//            loan.setDisbursmentVerifiedBy(loanInterface.getApproved_by());
//            loan.setDisbursmentVerifiedOn(loanInterface.getdisbursement_date());
//        loan.setInterestRate(loanInterface.getinterest_rate());
//        loan.setInterestPreferential(0.0);
//        loan.setOperativeAcountId(loanInterface.getoperative_acount_id());
//        loan.setDisbursmentAccount(loanInterface.getdisbursment_account());
//
//
//        loan.setTotalLoanBalance(loanInterface.getprincipal_amount());
//
//        log.info("total loan balance: "+loanInterface.getaccount_balance());
//
//        loan.setNumberOfInstallments(installmentsNumber);
//
//        loan.setFrequencyId(loanInterface.getfrequency_id());
//        loan.setFrequencyPeriod(loanInterface.getfrequency_period());
//        loan.setInstallmentStartDate(loanInterface.getinstallment_start_date());
//        loan.setLoanStatus(CONSTANTS.DISBURSED);
//        loan.setPayOffVerificationFlag(CONSTANTS.NO);
//
//        loan.setPausedDemandsFlag(CONSTANTS.YES);
//        loan.setPauseAccrualFlag(CONSTANTS.YES);
//        loan.setPauseBookingFlag(CONSTANTS.YES);
//
//        return loan;
//    }catch (Exception e){
//        log.info("Catched Error {} " + e);
//        return null;
//    }
//}
////
//// TODO: 4/3/2023 reducing balance
//    public Loan createLoanModel2(LoanInterface loanInterface){
//        try {
//            log.info("crating loan model");
//            Loan loan = new Loan();
//            loan.setLoanType("NORMAL");
//            Double principal=loanInterface.getprincipal_amount();
//
//            log.info("principal====>"+principal.toString());
//            loan.setPrincipalAmount(principal);
//            loan.setDisbursementFlag(CONSTANTS.NO);
//
//            Integer installmentsNumber=loanInterface.getnumber_of_installments();
//            log.info("Loan interface date :: "+loanInterface.getinstallment_start_date());
//            Date actualIntallmentStartDate= loanInterface.getinstallment_start_date();
//            log.info("actualIntallmentStartDate :: "+loanInterface.getinstallment_start_date());
//
//            Date installStartDate=loanInterface.getinstallment_start_date();
//            log.info("installment start date is :: "+installStartDate);
//            LocalDate givenDate=datesCalculator.convertDateToLocalDate(installStartDate);
//            log.info("given date :: "+givenDate);
//            LocalDate newStartDate= LocalDate.parse("2023-02-09");
////            LocalDate newStartDate= LocalDate.parse("2023-03-09");
//            log.info("converting date");
//            Date newInstallStartDate= datesCalculator.convertLocalDateToDate(newStartDate);
//            log.info("date converted, actual start date :: "+newStartDate);
//
//            if(installStartDate.compareTo(newInstallStartDate)<0){
//                log.info("getting fulfilled schedules");
//                EntityResponse res=getFulfilledSchedules(givenDate, newStartDate ,installmentsNumber);
//                installmentsNumber= (Integer) res.getEntity();
//                log.info("installment number :: "+installmentsNumber);
//                actualIntallmentStartDate=newInstallStartDate;
//            }
//
//            Double loanInterest= loanInterface.getinterest_rate();
//
//            Double interestPreferential = loanInterest-12;
//
//
//
//            if(installmentsNumber>0){
//                loan.setDisbursementAmount(principal);
//                loan.setFeesAmount(0.00);
//                loan.setDisbursementFlag(CONSTANTS.YES);
//                loan.setDisbursementDate(loanInterface.getdisbursement_date());
//                loan.setDisbursedBy(loanInterface.getApproved_by());
//                loan.setLoanStatus(CONSTANTS.APPROVED);
//                loan.setDisbursementVerifiedFlag(CONSTANTS.NO);
//                loan.setDisbursementAmount(loanInterface.getdisbursement_amount());
//                log.info("getdisbursement_amount: "+loanInterface.getdisbursement_amount());
//                loan.setDisbursementDate(loanInterface.getdisbursement_date());
//                log.info("check 1");
//                loan.setDisbursmentVerifiedOn(loanInterface.getApproval_date());
//                log.info("check 2");
//                loan.setDisbursedBy("SYSTEM");
//                log.info("check 3");
//                loan.setDisbursementVerifiedFlag(CONSTANTS.YES);
//                log.info("check 4");
//                loan.setDisbursmentVerifiedBy(loanInterface.getApproved_by());
//                log.info("check 5");
//                loan.setDisbursmentVerifiedOn(loanInterface.getdisbursement_date());
//                log.info("check 6");
//                loan.setInterestRate(12.0);
//                log.info("check 7");
//
//                loan.setInterestPreferential(interestPreferential);
//                log.info("check 8");
//                loan.setOperativeAcountId(loanInterface.getoperative_acount_id());
//                log.info("check 9");
//                loan.setDisbursmentAccount(loanInterface.getdisbursment_account());
//                log.info("check 10");
//
//
//
//                loan.setNumberOfInstallments(installmentsNumber);
//                log.info("check 11");
//                loan.setFrequencyId(loanInterface.getfrequency_id());
//                log.info("check 12");
//                loan.setFrequencyPeriod(loanInterface.getfrequency_period());
//                log.info("check 13");
//                loan.setInstallmentStartDate(actualIntallmentStartDate);
//                log.info("check 14");
//                loan.setLoanStatus(CONSTANTS.DISBURSED);
//                log.info("check 15");
//
//                return loan;
//            }else {
//                return null;
//            }
//
//        }catch (Exception e){
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }
//
//
//    // TODO: 4/3/2023 flat rate
//    public EntityResponse addLoanAccount(LoanInterface loanInterface){
//        try{
//            Account account = new Account();
//            account.setEntityId("001");
//            account.setOpeningDate(new Date());
//            account.setCurrency("KES");
//            account.setAccountType(CONSTANTS.LOAN_ACCOUNT);
//            account.setAccountName(loanInterface.getAccount_name());
//            account.setVerifiedBy(loanInterface.getApproved_by());
//            account.setPostedBy(loanInterface.getPosted_by());
//            account.setVerifiedTime(loanInterface.getApproval_date());
//            account.setAccountName(loanInterface.getAccount_name());
//            account.setAccountManager("SUPERUSER");
//            account.setCustomerCode(loanInterface.getcustomer_code());
//            account.setManagerCode("");
//            account.setWithholdingTax(false);
//            account.setCustomerType(loanInterface.getcustomer_type());
//            account.setProductCode(loanInterface.getproduct_code());
//            account.setMisSectorCode("123");
//            account.setAccountOwnership("individual");
//            account.setMisSubSectorCode("1");
//            account.setAccountStatement(true);
//            account.setOperationMode("EITHER TO SIGN");
//            account.setCheckedJointAccount(false);
//            account.setTransferExceptionLimitDr("string");
//            account.setTransferExceptionLimitCr("String");
//            account.setAcid(loanInterface.getacid());
//
//            account.setAccountBalance(loanInterface.getaccount_balance());
//
//            log.info("-ve ac bal+"+loanInterface.getaccount_balance());
//
////            account.setAccountBalance(0.0);
//            account.setLoan(createLoanModel(loanInterface));
//
//            account.setPostedFlag(CONSTANTS.YES);
//            account.setVerifiedFlag(CONSTANTS.YES);
//            account.setVerifiedBy("SYSTEM");
//            account.setPostedBy(loanInterface.getPosted_by());
//            account.setPostedTime(loanInterface.getApproval_date());
//            account.setOpeningDate(loanInterface.getApproval_date());
//
//            return accountRegistrationService.registerAccountMigration(account);
//
//        }catch (Exception e){
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }
////
//// TODO: 3/29/2023 this time round no acid generation
//// TODO: 4/3/2023 reducing balance
//    public EntityResponse addLoanAccount2(LoanInterface loanInterface){
//        try{
//            if(createLoanModel2(loanInterface)==null){
//                return null;
//            }else {
//                log.info("check 1");
//
////                String acid=generateAcid("LA30",loanInterface.getacid());
//                Account account = new Account();
//                account.setAcid(loanInterface.getacid());
//                account.setEntityId("001");
//                account.setOpeningDate(new Date());
//                account.setCurrency("KES");
//                account.setAccountType(CONSTANTS.LOAN_ACCOUNT);
//                account.setAccountName(loanInterface.getAccount_name());
//                account.setVerifiedBy(loanInterface.getApproved_by());
//                account.setPostedBy(loanInterface.getPosted_by());
//                account.setVerifiedTime(loanInterface.getApproval_date());
//                account.setAccountName(loanInterface.getAccount_name());
//                account.setAccountManager("SUPERUSER");
//                account.setCustomerCode(loanInterface.getcustomer_code());
//                account.setManagerCode("");
//                account.setWithholdingTax(false);
//                account.setCustomerType(loanInterface.getcustomer_type());
//                account.setProductCode("LA30");
//                account.setMisSectorCode("123");
//                account.setAccountOwnership("individual");
//                account.setMisSubSectorCode("1");
//                account.setAccountStatement(true);
//                account.setOperationMode("EITHER TO SIGN");
//                account.setCheckedJointAccount(false);
//                account.setTransferExceptionLimitDr("string");
//                account.setTransferExceptionLimitCr("String");
////                account.setAcid(loanInterface.getacid());
//                log.info("check 2");
//                account.setAccountBalance(loanInterface.getaccount_balance());
//                log.info("check 3");
//                account.setLoan(createLoanModel2(loanInterface));
//
//                account.setPostedFlag(CONSTANTS.YES);
//                account.setVerifiedFlag(CONSTANTS.YES);
//                account.setVerifiedBy("SYSTEM");
//                account.setPostedBy(loanInterface.getPosted_by());
//                account.setPostedTime(loanInterface.getApproval_date());
//                account.setOpeningDate(loanInterface.getApproval_date());
//
//                return accountRegistrationService.registerAccountMigration(account);
//            }
//        }catch (Exception e) {
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }
////
////    public List<EntityResponse> classifyAllLoans(){
////        try{
////            List<EntityResponse> entityResponseList= new ArrayList<>();
////            List<Loan> loans= loanRepository.findAll();
////            for(Integer i=0; i<loans.size(); i++){
////                Loan loan =loans.get(i);
////                String loanAcid= loans.get(i).getAccount().getAcid();
////                String assetClassification=assetClassificationService.getLoanAssetClassification(loanAcid);
////                loan.setAssetClassification(assetClassification);
////
////                Loan sLoan=loanRepository.save(loan);
////                EntityResponse entityResponse=new EntityResponse<>();
////                entityResponse.setStatusCode(200);
////                entityResponse.setMessage(sLoan.getAssetClassification());
////                entityResponseList.add(entityResponse);
////            }
////            return entityResponseList;
////        }catch (Exception e){
////            log.info("Catched Error {} " + e);
////            return null;
////        }
////    }
////
////
////    public List<EntityResponse> disburseAllMigratedLoans(){
////        try{
////            List<String> acids= migrationRepo.allMigratedLoanAcids();
////            List<EntityResponse> entityResponseList=new ArrayList<>();
////            acids.forEach( acid->{
////                List<EntityResponse> res=loanDisbursmentService.verifyLoanDisbursmentMigration(acid);
////                entityResponseList.addAll(res);
////            });
////
////            return entityResponseList;
////        }catch (Exception e){
////            log.info("Catched Error {} " + e);
////            return null;
////        }
////    }
////
////
//    public EntityResponse getFulfilledSchedules(LocalDate startDate, LocalDate endDate, Integer installments){
//        try {
//            Integer sNumber = 0;
////            startDate=datesCalculator.substractDate(startDate,1,"MONTHS");
//            while (startDate.compareTo(endDate) <= 0) {
//                log.info("start date: " + startDate.toString());
//                startDate = datesCalculator.addDate(startDate, 1, "MONTHS");
//                sNumber++;
//            }
//            Integer remInstallments = installments - sNumber + 1;
//            EntityResponse res = new EntityResponse<>();
//            res.setEntity(remInstallments);
//            return res;
//        } catch (Exception e) {
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }
//
//    public void updateIntFlags(){
//        try {
//            log.info("updating 1");
//            migrationRepo.updateLoanInterestFlags("LA30000510",0);
//            log.info("updating 2");
//            migrationRepo.updateLoanInterestFlags("LA30000551",48);
//            log.info("updating 3");
//            migrationRepo.updateLoanInterestFlags("LA30000554",0);
//            log.info("updating 4");
//            migrationRepo.updateLoanInterestFlags("LA30000559",48);
//            log.info("updating 5");
//            migrationRepo.updateLoanInterestFlags("LA30000573",24);
//            log.info("updating 6");
//            migrationRepo.updateLoanInterestFlags("LA30000508",24);
//            log.info("updating 7");
//            migrationRepo.updateLoanInterestFlags("LA30000573",24);
//            log.info("updating 8");
//            migrationRepo.updateLoanInterestFlags("LA30000573",24);
//        } catch (Exception e) {
//            log.info("Catched Error {} " + e);
//        }
//    }
//
//    public List<EntityResponse> payOffLoans(){
//        try {
//            EntityResponse res= new EntityResponse<>();
//            List<EntityResponse> listRes= new ArrayList<>();
//            List<LoanInterface> li=migrationRepo.loanAccountsInfo2();
//            for (Integer i =0; i<li.size();i++){
//                res=loanPayOffService.initiateLoanPayOff(li.get(i).getacid(), "130013");
//                listRes.add(res);
//            }
//            return listRes;
//        }catch (Exception e) {
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }
//
//    public List<EntityResponse> verifyPayOffLoans(){
//        try {
//            EntityResponse res= new EntityResponse<>();
//            List<EntityResponse> listRes= new ArrayList<>();
//            List<LoanInterface> li=migrationRepo.loanAccountsInfo2();
//            for (Integer i =0; i<li.size();i++){
//                res=loanPayOffService.verifyLoanPayOff1(li.get(i).getacid());
//                listRes.add(res);
//            }
//            return listRes;
//        }catch (Exception e) {
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }
//
//
////
////
////    public List<EntityResponse> transferForOverflowLonaBalances() {
////        System.out.println("In tranfer");
////        List<EntityResponse> responses = new ArrayList<>();
////        Double tranAmount;
////        List<Account> repaymentAccounts = accountRepository.findOverPaidRepaymentAccounts();
////        for (Account repaymentAccount: repaymentAccounts) {
////            System.out.println(repaymentAccount.getAcid());
////            Account loanAccount = accountRepository.getLoanAccountByRepaymentAccount(repaymentAccount.getAcid());
////            tranAmount = repaymentAccount.getAccountBalance();
////            if (tranAmount > Math.abs(loanAccount.getAccountBalance()))
////                tranAmount =  Math.abs(loanAccount.getAccountBalance());
////            OutgoingTransactionDetails outgoingTransactionDetails=  createMigrationTransactionModel("KES", "001", tranAmount, repaymentAccount.getAcid(),  loanAccount.getAcid(), "Migration - Transfer of prepayments for loan");
////            EntityResponse transactionRes=accountTransactionService.createTransaction1(outgoingTransactionDetails);
////
////            if(!transactionRes.getStatusCode().equals(HttpStatus.OK.value())) {
////                ///failed transactiom
////                EntityResponse response = new EntityResponse();
////                response.setMessage("Could not transact");
////                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
////
////            }
////
////            responses.add(transactionRes);
////        }
////        return responses;
////    }
////
////    public OutgoingTransactionDetails createMigrationTransactionModel(String loanCurrency,
////                                                                      String entity,
////                                                                      Double interestDemand,
////                                                                      String drAc, String crAc, String particulars) {
////        try {
////            //TODO: TRANSACTION MAIN DETAILS
////            OutgoingTransactionDetails outgoingTransactionDetails =new OutgoingTransactionDetails();
////            outgoingTransactionDetails.setTransactionType("SYSTEM");
////            outgoingTransactionDetails.setCurrency(loanCurrency);
////            outgoingTransactionDetails.setTransactionDate(new Date());
////            outgoingTransactionDetails.setEntityId(entity);
////            outgoingTransactionDetails.setTotalAmount(interestDemand);
////
////            //TODO: TRANSACTION DR DETAILS
////            OutgoingTransactionPartTrans drOutgoingTransactionPartTrans= new OutgoingTransactionPartTrans();
////            drOutgoingTransactionPartTrans.setPartTranType(CONSTANTS.DEBITSTRING);
////            drOutgoingTransactionPartTrans.setTransactionAmount(interestDemand);
////            drOutgoingTransactionPartTrans.setAcid(drAc);
////            drOutgoingTransactionPartTrans.setCurrency(loanCurrency);
////            drOutgoingTransactionPartTrans.setExchangeRate("");
////            drOutgoingTransactionPartTrans.setTransactionDate(new Date());
////            drOutgoingTransactionPartTrans.setTransactionParticulars(particulars);
////            drOutgoingTransactionPartTrans.setIsoFlag("Y");
////
////            //TODO: TRANSACTION CR DETAILS
////            OutgoingTransactionPartTrans crOutgoingTransactionPartTrans= new OutgoingTransactionPartTrans();
////            crOutgoingTransactionPartTrans.setPartTranType(CONSTANTS.CREDITSTRING);
////            crOutgoingTransactionPartTrans.setTransactionAmount(interestDemand);
////            crOutgoingTransactionPartTrans.setAcid(crAc);
////            crOutgoingTransactionPartTrans.setCurrency(loanCurrency);
////            crOutgoingTransactionPartTrans.setExchangeRate("");
////            crOutgoingTransactionPartTrans.setTransactionDate(new Date());
////            crOutgoingTransactionPartTrans.setTransactionParticulars(particulars);
////            crOutgoingTransactionPartTrans.setIsoFlag("Y");
////
////            List<OutgoingTransactionPartTrans> listOutgoingTransactionPartTrans= new ArrayList<>();
////            listOutgoingTransactionPartTrans.add(drOutgoingTransactionPartTrans);
////            listOutgoingTransactionPartTrans.add(crOutgoingTransactionPartTrans);
////            outgoingTransactionDetails.setPartTrans(listOutgoingTransactionPartTrans);
////            log.info("------MODEL CREATED -----"+outgoingTransactionDetails.toString());
////            return outgoingTransactionDetails;
////
////        }catch (Exception e){
////            log.info("Catched Error {} " + e);
////            return null;
////        }
////    }
////
////
//    public String generateAcid(String productCode, String laonAcid){
//        try{
//            String prod=productCode;
//            String lastSix=laonAcid.substring(laonAcid.length() - 6);
//            return prod+""+lastSix;
//
//        }catch (Exception e) {
//            log.info("Caught Error {}"+e);
//            return null;
//        }
//    }
////
////    public List<EntityResponse> payOffAllAccounts(String payOffAccount, LocalDate maturityDate){
////        try {
////            List<String> acids= migrationRepo.allMigratedLoanAcidsPayOff(maturityDate);
////            List<EntityResponse> entityResponseList=new ArrayList<>();
////            acids.forEach( acid->{
////                EntityResponse res=loanPayOffService.initiateLoanPayOff( acid, payOffAccount);
////                entityResponseList.add(res);
////            });
////            return entityResponseList;
////        }catch (Exception e) {
////            log.info("Caught Error {}"+e);
////            return null;
////        }
////    }
////
////    public List<EntityResponse> verifyPayOffAllAccounts(){
////        try {
////            List<String> acids= migrationRepo.allMigratedLoanAcids();
////            List<EntityResponse> entityResponseList=new ArrayList<>();
////            acids.forEach( acid->{
////                EntityResponse res=loanPayOffService.verifyLoanPayOff(acid);
////                entityResponseList.add(res);
////            });
////            return entityResponseList;
////        }catch (Exception e) {
////            log.info("Caught Error {}"+e);
////            return null;
////        }
////    }
//
//
//    public EntityResponse getGuarantors(){
//        try{
//            EntityResponse res = new EntityResponse<>();
//            List<LoanGuarantorInterface> loanGuarantorInterfaces=migrationRepo.getGuarantors();
//            if(loanGuarantorInterfaces.size()>0){
//                res.setMessage(HttpStatus.FOUND.getReasonPhrase());
//                res.setStatusCode(HttpStatus.FOUND.value());
//                res.setEntity(loanGuarantorInterfaces);
//            }else {
//                res.setMessage("Not Found");
//                res.setStatusCode(HttpStatus.NOT_FOUND.value());
//            }
//            return res;
//        }catch (Exception e) {
//            log.info("Caught Error {}"+e);
//            return null;
//        }
//    }
//
//    public List<EntityResponse> migrateGuarantors(){
//        try{
//            List<EntityResponse> resList = new ArrayList<>();
//            EntityResponse res = new EntityResponse<>();
//            List<LoanGuarantorInterface> loanGuarantorInterfaces=migrationRepo.getGuarantors();
//            if(loanGuarantorInterfaces.size()>0){
//                for(Integer i=0;i<loanGuarantorInterfaces.size();i++){
//                    LoanGuarantorInterface l= loanGuarantorInterfaces.get(i);
//                    LoanGuarantor lg= new LoanGuarantor();
//                    lg.setGuarantorType(l.getguarantor_type());
//                    lg.setGuarantorCustomerCode(l.getguarantor_customer_code());
//                    lg.setGuaranteeAmount(l.getguarantee_amount());
//                    lg.setLoanSeries(l.getloan_series());
//                    lg.setInitialAmt(l.getinitial_gee_amount());
//
//                    res=loanGuarantorService.addGuarantorToALoan(lg,l.getacid());
//                    resList.add(res);
//
//                }
//            }else {
//                res.setMessage("Not Found");
//                res.setStatusCode(HttpStatus.NOT_FOUND.value());
//                resList.add(res);
//            }
//            return resList;
//        }catch (Exception e) {
//            log.info("Caught Error {}"+e);
//            return null;
//        }
//    }
//
//    public EntityResponse updateGuarantorCurrentAmount(String acid){
//        try{
//            EntityResponse res = new EntityResponse<>();
//              res=loanGuarantorService.updateGuaranteedAmt(acid);
//            return res;
//        }catch (Exception e) {
//            log.info("Caught Error {}"+e);
//            return null;
//        }
//    }
//    public List<EntityResponse> updateGuarantorCurrentAmount(){
//        try{
//            EntityResponse res = new EntityResponse<>();
//            List<EntityResponse> resL =new ArrayList<>();
//            List<Account> acs= accountRepository.findByAccountTypeAndAccountStatus(CONSTANTS.LOAN_ACCOUNT,CONSTANTS.ACTIVE);
//            if(acs.size()>0){
//                for(Integer i=0;i<acs.size();i++){
//                    res=loanGuarantorService.updateGuaranteedAmt(acs.get(i).getAcid());
//                    resL.add(res);
//                }
//            }
//            return resL;
//        }catch (Exception e) {
//            log.info("Caught Error {}"+e);
//            return null;
//        }
//    }
//
//    public void deleteGuarantors(){
//        try {
//            List<LoanGuarantorInterface> loanGuarantorInterfaces=migrationRepo.getGuarantors();
//            if(loanGuarantorInterfaces.size()>0){
//                for(Integer i=0;i<loanGuarantorInterfaces.size();i++){
//                    LoanGuarantorInterface l= loanGuarantorInterfaces.get(i);
//
//                    loanGuarantorService.deleteGuarantorsInAnAccount(l.getacid());
//
//
//                }
//            }
//        }catch (Exception e) {
//            log.info("Caught Error {}"+e);
//        }
//    }
//}
