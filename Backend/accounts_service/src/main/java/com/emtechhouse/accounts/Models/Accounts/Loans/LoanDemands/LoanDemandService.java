package com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands;//package com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands;
//
//import com.emtechhouse.accounts.Models.Accounts.Account;
//import com.emtechhouse.accounts.Models.Accounts.AccountRepository;
//import com.emtechhouse.accounts.Models.Accounts.AccountServices.AccountTransactionService;
//import com.emtechhouse.accounts.Models.Accounts.Loans.Loan;
//import com.emtechhouse.accounts.Models.Accounts.Loans.LoanCalculatorService;
//import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.DemandSatisfaction.LoanDemandSatisfaction;
//import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.DemandSatisfaction.LoanDemandSatisfactionRepo;
//import com.emtechhouse.accounts.Models.Accounts.Loans.LoanRepository;
//import com.emtechhouse.accounts.Models.Accounts.Loans.LoanSchedule.LoanSchedule;
//import com.emtechhouse.accounts.Models.Accounts.ProductDetails.GeneralProductDetails;
//import com.emtechhouse.accounts.Models.Accounts.ProductDetails.ProductItemService;
//import com.emtechhouse.accounts.Models.Accounts.TransactionInterfaces.OutgoingTransaction.OutgoingTransactionDetails;
//import com.emtechhouse.accounts.Models.Accounts.TransactionInterfaces.OutgoingTransaction.OutgoingTransactionPartTrans;
//import com.emtechhouse.accounts.Models.Accounts.Validators.ValidatorService;
//import com.emtechhouse.accounts.Utils.CONSTANTS;
//import com.emtechhouse.accounts.Utils.DatesCalculator;
//import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
//import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Optional;
//
//@Component
//@Slf4j
//public class LoanDemandService {
//    @Autowired
//    private LoanDemandRepository loanDemandRepository;
//    @Autowired
//    private DatesCalculator datesCalculator;
//    @Autowired
//    private LoanRepository loanRepository;
//    @Autowired
//    private ValidatorService validatorsService;
//    @Autowired
//    private AccountRepository accountRepository;
//    @Autowired
//    private ProductItemService productItemService;
//    @Autowired
//    private AccountTransactionService accountTransactionService;
//    @Autowired
//    private LoanCalculatorService loanCalculatorService;
//    @Autowired
//    LoanDemandSatisfactionRepo loanDemandSatisfactionRepo;
////    public void createDemand(Loan loan){
////        try{
////            Date today = new Date();
////            Date demandDate = loan.getNextRepaymentDate();
////            String acid = loan.getAccount().getAcid();
////            Double principalDemand =loan.getPrincipalDemandAmount();
////            Double interestDemand= loan.getInterestDemandAmount();
////            Double sumDemand =loan.getSumPrincipalDemand();
////            String installmentFreqId= loan.getFrequencyId();
////            Integer installmentFreqPeriod = loan.getFrequencyPeriod();
////
////            //----TRANSCTION-----
////            //DR LOAN ACCOUNT INTERES
////            //CR RECIEVABLE
////
////
////            LoanDemand loanDemand = new LoanDemand();
////            loanDemand.setDemandCode("CODE");
////            loanDemand.setAcid(acid);
////            loanDemand.setDemandAmount(principalDemand+interestDemand);
////            loanDemand.setDemandDate(demandDate);
////            loanDemand.setDemandType("FDEM");
////            loanDemand.setAdjustmentAmount(0.00);
////            loanDemand.setDeletedFlag('N');
////            loanDemand.setCreatedOn(today);
////            loanDemand.setLoan(loan);
////            loanDemand.setCreatedBy("SYSTEM");
////            loanDemandRepository.save(loanDemand);
////
////            //update loan
////            Date nextRepaymentDate = datesCalculator.addDate(demandDate,installmentFreqPeriod, installmentFreqId);
////            loan.setNextRepaymentDate(nextRepaymentDate);
////            loan.setSumPrincipalDemand(sumDemand+interestDemand+principalDemand);
////            loanRepository.save(loan);
////
////        }catch (Exception e){
////            log.info("Catched Error {} " + e);
//////            return null;
////        }
////    }
//
//    public EntityResponse createDemandByScheduleSlabs(Loan loan){
//        try{
//            Date today = new Date();
//            Date demandDate = loan.getNextRepaymentDate();
//            String acid = loan.getAccount().getAcid();
//            Double principalDemand =loan.getPrincipalDemandAmount();
//            Double interestDemand= loan.getInterestDemandAmount();
//            Double sumDemand =loan.getSumPrincipalDemand();
//            Double sumDemandedAmount =principalDemand+interestDemand;
//            String installmentFreqId= loan.getFrequencyId();
//            Integer installmentFreqPeriod = loan.getFrequencyPeriod();
//
//            String interestCalculationMethod=loan.getInterestCalculationMethod();
//
//            String productCode= loan.getAccount().getProductCode();
//
//            String entity = EntityRequestContext.getCurrentEntityId();
//
//            Integer numberOfInstallments=loan.getNumberOfInstallments();
//            String freqId=loan.getFrequencyId();
//            Integer freqPeriod=loan.getFrequencyPeriod();
//            Double principalAmount=loan.getPrincipalAmount();
//            Double interestRate=loan.getInterestRate();
//            LocalDate startDate=datesCalculator.convertDateToLocalDate(loan.getInstallmentStartDate());
//            //create loan shedule
//            //check all slabs to demand i.e date<today
//            List<LoanSchedule> schedules;
//            final LoanSchedule[] affectedSchedule = new LoanSchedule[1];
//
//            //get the interest calculation method
//            if(interestCalculationMethod.equalsIgnoreCase("reducing-balance")){
//                schedules=loanCalculatorService.reducingBalanceloanScheduleCalculator(numberOfInstallments, freqId, freqPeriod, principalAmount, interestRate, startDate,1.0);
//            }else {
//                schedules=loanCalculatorService.fixedRateLoanScheduleCalculator(numberOfInstallments, freqId, freqPeriod, principalAmount, interestRate, startDate);
//            }
//
//            //loan next repayment date
//            //get all the affected days in the slabs
//            //matches loan next repayment date and slab next repayment date
//            LocalDate loanNxtRepaymentDate= datesCalculator.convertDateToLocalDate(loan.getNextRepaymentDate());
//            schedules.forEach(schedule->{
//                LocalDate scheduleDemandDate= schedule.getDemandDate();
//                if(loanNxtRepaymentDate.compareTo(scheduleDemandDate)==0){
//                    affectedSchedule[0] =schedule;
//                }
//            });
//
//            if(!affectedSchedule[0].equals(null)){
//                log.info("kk");
//                log.info("affectedSchedule[0] "+affectedSchedule[0]);
//                //get interest to be paid
//                principalDemand =affectedSchedule[0].getPrincipleAmount();
//                interestDemand= affectedSchedule[0].getInterestAmount();
//
//                //----TRANSCTION-----
//                //DR LOAN ACCOUNT INTEREST ONLY
//                //CR RECIEVABLE
//
//                String drAc="";
//                String crAc="";
//
//                //------------transaction--------//
//                drAc=loan.getAccount().getAcid();
//                log.info("---------------Dr account-----> "+drAc);
//                log.info("---------------productcode----->"+productCode+"===");
//
//                //TODO----------GET CR ACC//
//                EntityResponse entityResponse=productItemService.getGeneralProductDetail1(productCode, CONSTANTS.LOAN_ACCOUNT);
//                if(entityResponse.getStatusCode().equals(HttpStatus.OK.value())){
////                crAc=productItemService.getGeneralProductDetail("100100").getInt_receivable_ac();
//                    GeneralProductDetails newGeneralProductDetails= (GeneralProductDetails) entityResponse.getEntity();
//                    crAc=newGeneralProductDetails.getInt_receivable_ac();
//                    log.info("---------------Cr account-----> "+crAc);
//                    String loanCurrency=loan.getAccount().getCurrency();
//
//
//                    //------Validate cr and debit accounts------//
//                    EntityResponse drAcValidator=validatorsService.acidValidator(drAc, "Loan");
//                    if(drAcValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
//                        //Error
//                        return drAcValidator;
//                    }else if(drAcValidator.getStatusCode().equals(200)){
//                        //validate crAc
//                        EntityResponse crAcValidator=validatorsService.acidValidator(crAc, "Interest recievable");
//                        if(crAcValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
//                            //Error
//                            return crAcValidator;
//                        }else {
//                            //perform transaction
//                            // Perform transaction to Dr receivable and Cr P&L account
//                            //TODO: TRANSACTION MAIN DETAILS
//                            OutgoingTransactionDetails outgoingTransactionDetails =new OutgoingTransactionDetails();
//                            outgoingTransactionDetails.setTransactionType("SYSTEM");
//                            outgoingTransactionDetails.setCurrency(loanCurrency);
//                            outgoingTransactionDetails.setTransactionDate(new Date());
//                            outgoingTransactionDetails.setEntityId(entity);
//                            outgoingTransactionDetails.setTotalAmount(interestDemand);
//
//                            //TODO: TRANSACTION DR DETAILS
//                            OutgoingTransactionPartTrans drOutgoingTransactionPartTrans= new OutgoingTransactionPartTrans();
//                            drOutgoingTransactionPartTrans.setPartTranType(CONSTANTS.DEBITSTRING);
//                            drOutgoingTransactionPartTrans.setTransactionAmount(interestDemand);
//                            drOutgoingTransactionPartTrans.setAcid(drAc);
//                            drOutgoingTransactionPartTrans.setCurrency(loanCurrency);
//                            drOutgoingTransactionPartTrans.setExchangeRate("");
//                            drOutgoingTransactionPartTrans.setTransactionDate(new Date());
//                            drOutgoingTransactionPartTrans.setTransactionParticulars("DEMAND GENERATION FOR LOAN ACCOUNT:  "+acid);
//                            drOutgoingTransactionPartTrans.setIsoFlag("Y");
//
//                            //TODO: TRANSACTION CR DETAILS
//                            OutgoingTransactionPartTrans crOutgoingTransactionPartTrans= new OutgoingTransactionPartTrans();
//                            crOutgoingTransactionPartTrans.setPartTranType(CONSTANTS.CREDITSTRING);
//                            crOutgoingTransactionPartTrans.setTransactionAmount(interestDemand);
//                            crOutgoingTransactionPartTrans.setAcid(crAc);
//                            crOutgoingTransactionPartTrans.setCurrency(loanCurrency);
//                            crOutgoingTransactionPartTrans.setExchangeRate("");
//                            crOutgoingTransactionPartTrans.setTransactionDate(new Date());
//                            crOutgoingTransactionPartTrans.setTransactionParticulars("DEMAND GENERATION FOR LOAN ACCOUNT: "+acid);
//                            crOutgoingTransactionPartTrans.setIsoFlag("Y");
//
//                            List<OutgoingTransactionPartTrans> listOutgoingTransactionPartTrans= new ArrayList<>();
//                            listOutgoingTransactionPartTrans.add(drOutgoingTransactionPartTrans);
//                            listOutgoingTransactionPartTrans.add(crOutgoingTransactionPartTrans);
//
//                            outgoingTransactionDetails.setPartTrans(listOutgoingTransactionPartTrans);
//                            String transactCode=accountTransactionService.createTransaction(outgoingTransactionDetails);
//                            log.info("transaction details-->"+ outgoingTransactionDetails.toString());
//                            if(transactCode.equals(CONSTANTS.FAILED)){
//                                ///failed transactiom
//                                EntityResponse response = new EntityResponse();
//                                response.setMessage("TRANSACTION ERROR! COULD NOT PERFORM DEMAND GENERATION TRANSACTION FOR LOAN ACCOUNT: "+acid);
//                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                                response.setEntity(outgoingTransactionDetails);
//                                return response;
//
//                            }else {
//
//                                //CREATE PRINCIPAL DEMAND
//                                LoanDemand principalLoanDemand = new LoanDemand();
//                                principalLoanDemand.setDemandCode(transactCode);
//                                principalLoanDemand.setAcid(acid);
//                                principalLoanDemand.setDemandAmount(principalDemand);
////                                principalLoanDemand.setInterestAmount(0.00);
////                                principalLoanDemand.setPrincipalAmount(0.00);
//                                principalLoanDemand.setDemandDate(demandDate);
//                                principalLoanDemand.setDemandType(CONSTANTS.PRINCIPAL_DEMAND);
//                                principalLoanDemand.setAdjustmentAmount(0.00);
//                                principalLoanDemand.setDeletedFlag('N');
//                                principalLoanDemand.setCreatedOn(today);
//                                principalLoanDemand.setLoan(loan);
//                                principalLoanDemand.setCreatedBy("SYSTEM");
//
//                                //CREATE INTEREST DEMAND
//                                LoanDemand interestLoanDemand = new LoanDemand();
//                                interestLoanDemand.setDemandCode(transactCode);
//                                interestLoanDemand.setAcid(acid);
//                                interestLoanDemand.setDemandAmount(interestDemand);
////                                interestLoanDemand.setInterestAmount(0.00);
////                                interestLoanDemand.setPrincipalAmount(0.00);
//                                interestLoanDemand.setDemandDate(demandDate);
//                                interestLoanDemand.setDemandType(CONSTANTS.INTEREST_DEMAND);
//                                interestLoanDemand.setAdjustmentAmount(0.00);
//                                interestLoanDemand.setDeletedFlag('N');
//                                interestLoanDemand.setCreatedOn(today);
//                                interestLoanDemand.setLoan(loan);
//                                interestLoanDemand.setCreatedBy("SYSTEM");
//
//                                List<LoanDemand> allDemands= new ArrayList<>();
//                                allDemands.add(principalLoanDemand);
//                                allDemands.add(interestLoanDemand);
//                                List<LoanDemand> savedDemands=loanDemandRepository.saveAll(allDemands);
//
//
//                                //update loan
//                                Date nextRepaymentDate = datesCalculator.addDate(demandDate,installmentFreqPeriod, installmentFreqId);
//                                loan.setNextRepaymentDate(nextRepaymentDate);
//                                loan.setSumPrincipalDemand(sumDemand+interestDemand+principalDemand);
//                                loanRepository.save(loan);
//
//                                EntityResponse response = new EntityResponse();
//                                response.setMessage("SUCCESSFULLY POSTED DEMAND GENERATION TRANSACTION FOR LOAN ACCOUNT: "+acid+ " FOR DEMAND DATE "+interestLoanDemand.getDemandDate().toString()+", TRANSACTION ID: "+transactCode);
//                                response.setStatusCode(HttpStatus.CREATED.value());
//                                response.setEntity(savedDemands);
//                                return response;
//                            }
//                        }
//                    }else {
//                        EntityResponse response = new EntityResponse();
//                        response.setMessage("ACCOUNT VERIFICATION ERROR! COULD NOT PERFORM DEMAND GENERATION TRANSACTION FOR LOAN ACCOUNT: "+acid);
//                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                        response.setEntity("");
//                        return response;
//                    }
//                }else {
//                    return entityResponse;
//                }
//            }else {
//                EntityResponse response = new EntityResponse();
//                response.setMessage("SCHEDULE ERROR");
//                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                response.setEntity("");
//                return response;
//            }
//
//
//
//
//        }catch (Exception e){
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//
//    }
//
////    public EntityResponse createDemand2(Loan loan){
////        try{
////            Date today = new Date();
////            Date demandDate = loan.getNextRepaymentDate();
////            String acid = loan.getAccount().getAcid();
////            Double principalDemand =loan.getPrincipalDemandAmount();
////            Double interestDemand= loan.getInterestDemandAmount();
////            Double sumDemand =loan.getSumPrincipalDemand();
////            Double sumDemandedAmount =principalDemand+interestDemand;
////            String installmentFreqId= loan.getFrequencyId();
////            Integer installmentFreqPeriod = loan.getFrequencyPeriod();
////
////            String productCode= loan.getAccount().getProductCode();
////
////            String entity = EntityRequestContext.getCurrentEntityId();
////
////            //----TRANSCTION-----
////            //DR LOAN ACCOUNT INTEREST ONLY
////            //CR RECIEVABLE
////
////            String drAc="";
////            String crAc="";
////
////            //------------transaction--------//
////            drAc=loan.getAccount().getAcid();
////            log.info("---------------Dr account-----> "+drAc);
////            log.info("---------------productcode----->"+productCode+"===");
////
////            //TODO----------GET CR ACC//
////            EntityResponse entityResponse=productItemService.getGeneralProductDetail1(productCode);
////            if(entityResponse.getStatusCode().equals(HttpStatus.OK.value())){
//////                crAc=productItemService.getGeneralProductDetail("100100").getInt_receivable_ac();
////                GeneralProductDetails newGeneralProductDetails= (GeneralProductDetails) entityResponse.getEntity();
////                crAc=newGeneralProductDetails.getInt_receivable_ac();
////                log.info("---------------Cr account-----> "+crAc);
////                String loanCurrency=loan.getAccount().getCurrency();
////
////
////                //------Validate cr and debit accounts------//
////                EntityResponse drAcValidator=validatorsService.acidValidator(drAc);
////                if(drAcValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
////                    //Error
////                    return drAcValidator;
////                }else if(drAcValidator.getStatusCode().equals(200)){
////                    //validate crAc
////                    EntityResponse crAcValidator=validatorsService.acidValidator(drAc);
////                    if(crAcValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
////                        //Error
////                        return crAcValidator;
////                    }else {
////                        //perform transaction
////                        // Perform transaction to Dr receivable and Cr P&L account
////                        //TODO: TRANSACTION MAIN DETAILS
////                        OutgoingTransactionDetails outgoingTransactionDetails =new OutgoingTransactionDetails();
////                        outgoingTransactionDetails.setTransactionType("SYSTEM");
////                        outgoingTransactionDetails.setCurrency(loanCurrency);
////                        outgoingTransactionDetails.setTransactionDate(new Date());
////                        outgoingTransactionDetails.setEntityId(entity);
////                        outgoingTransactionDetails.setTotalAmount(interestDemand);
////
////                        //TODO: TRANSACTION DR DETAILS
////                        OutgoingTransactionPartTrans drOutgoingTransactionPartTrans= new OutgoingTransactionPartTrans();
////                        drOutgoingTransactionPartTrans.setPartTranType(CONSTANTS.DEBITSTRING);
////                        drOutgoingTransactionPartTrans.setTransactionAmount(interestDemand);
////                        drOutgoingTransactionPartTrans.setAcid(drAc);
////                        drOutgoingTransactionPartTrans.setCurrency(loanCurrency);
////                        drOutgoingTransactionPartTrans.setExchangeRate("");
////                        drOutgoingTransactionPartTrans.setTransactionDate(new Date());
////                        drOutgoingTransactionPartTrans.setTransactionParticulars("DEMAND GENERATION FOR LOAN ACCOUNT:  "+acid);
////                        drOutgoingTransactionPartTrans.setIsoFlag("Y");
////
////                        //TODO: TRANSACTION CR DETAILS
////                        OutgoingTransactionPartTrans crOutgoingTransactionPartTrans= new OutgoingTransactionPartTrans();
////                        crOutgoingTransactionPartTrans.setPartTranType(CONSTANTS.CREDITSTRING);
////                        crOutgoingTransactionPartTrans.setTransactionAmount(interestDemand);
////                        crOutgoingTransactionPartTrans.setAcid(crAc);
////                        crOutgoingTransactionPartTrans.setCurrency(loanCurrency);
////                        crOutgoingTransactionPartTrans.setExchangeRate("");
////                        crOutgoingTransactionPartTrans.setTransactionDate(new Date());
////                        crOutgoingTransactionPartTrans.setTransactionParticulars("DEMAND GENERATION FOR LOAN ACCOUNT: "+acid);
////                        crOutgoingTransactionPartTrans.setIsoFlag("Y");
////
////                        List<OutgoingTransactionPartTrans> listOutgoingTransactionPartTrans= new ArrayList<>();
////                        listOutgoingTransactionPartTrans.add(drOutgoingTransactionPartTrans);
////                        listOutgoingTransactionPartTrans.add(crOutgoingTransactionPartTrans);
////
////                        outgoingTransactionDetails.setPartTrans(listOutgoingTransactionPartTrans);
////                        String transactCode=accountTransactionService.createTransaction(outgoingTransactionDetails);
////                        if(transactCode.equals(CONSTANTS.FAILED)){
////                            ///failed transactiom
////                            EntityResponse response = new EntityResponse();
////                            response.setMessage("TRANSACTION ERROR! COULD NOT PERFORM DEMAND GENERATION TRANSACTION FOR LOAN ACCOUNT: "+acid);
////                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
////                            response.setEntity(outgoingTransactionDetails);
////                            return response;
////
////                        }else {
////
////                            LoanDemand loanDemand = new LoanDemand();
////                            loanDemand.setDemandCode(transactCode);
////                            loanDemand.setAcid(acid);
////                            loanDemand.setDemandAmount(principalDemand+interestDemand);
////                            loanDemand.setInterestAmount(interestDemand);
////                            loanDemand.setPrincipalAmount(principalDemand);
////                            loanDemand.setDemandDate(demandDate);
////                            loanDemand.setDemandType("FDEM");
////                            loanDemand.setAdjustmentAmount(0.00);
////                            loanDemand.setDeletedFlag('N');
////                            loanDemand.setCreatedOn(today);
////                            loanDemand.setLoan(loan);
////                            loanDemand.setCreatedBy("SYSTEM");
////                            LoanDemand savedLoanDemand=loanDemandRepository.save(loanDemand);
////
////                            //update loan
////                            Date nextRepaymentDate = datesCalculator.addDate(demandDate,installmentFreqPeriod, installmentFreqId);
////                            loan.setNextRepaymentDate(nextRepaymentDate);
////                            loan.setSumPrincipalDemand(sumDemand+interestDemand+principalDemand);
////                            loanRepository.save(loan);
////
////                            EntityResponse response = new EntityResponse();
////                            response.setMessage("SUCCESSFULLY POSTED DEMAND GENERATION TRANSACTION FOR LOAN ACCOUNT: "+acid+ " FOR DEMAND DATE "+loanDemand.getDemandDate().toString()+", TRANSACTION ID: "+transactCode);
////                            response.setStatusCode(HttpStatus.CREATED.value());
////                            response.setEntity(savedLoanDemand);
////                            return response;
////                        }
////                    }
////                }else {
////                    EntityResponse response = new EntityResponse();
////                    response.setMessage("ACCOUNT VERIFICATION ERROR! COULD NOT PERFORM DEMAND GENERATION TRANSACTION FOR LOAN ACCOUNT: "+acid);
////                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
////                    response.setEntity("");
////                    return response;
////                }
////            }else {
////                return entityResponse;
////            }
////
////
////
////        }catch (Exception e){
////            log.info("Catched Error {} " + e);
////            return null;
////        }
////
////    }
//
////    public void demand(){
////        try{
////            //get all transactions whose annivasary date is today
////            Date demandDate = new Date();
////            demandDate=datesCalculator.convertDateTimeStamp(demandDate);
////            List<Loan> loansToDemand = loanRepository.findByNextRepaymentDate(demandDate);
////            log.info("Date--"+ demandDate);
////            log.info("length--"+ loansToDemand.size());
////            if(loansToDemand.size()>0){
////                loansToDemand.forEach(loanToDemand-> {
////                    if(loanToDemand.getSumPrincipalDemand()>=loanToDemand.getNetAmount()){
////                        log.info("All demands had been generated for account " + loanToDemand.getAccount().getAcid());
////                    }else {
//////                        createDemand(loanToDemand);
////                        Date nextRepaymentDate=loanToDemand.getNextRepaymentDate();
////                        String acid= loanToDemand.getAccount().getAcid();
////                        Loan loan=loanToDemand;
////                        while(nextRepaymentDate.compareTo(new Date ())<0){
////                            createDemand2(loan);
////                            loan = loanRepository.findByAcid(acid).get();
////                            nextRepaymentDate =loan.getNextRepaymentDate();
////                            if(loan.getSumPrincipalDemand()>=(loan.getNetAmount()-5)){
////                                log.info("All demands had been generated for account " + loan.getAccount().getAcid());
////                                break;
////                            }else {
////                                continue;
////                            }
////                        }
////                    }
////                });
////            }
////        }catch (Exception e){
////            log.info("Catched Error {} " + e);
//////            return null;
////        }
////    }
//
//
//
//public List<EntityResponse> demandManualForce(String acid){
//    try{
//        List<EntityResponse> entityResponses=new ArrayList<>();
//        Date demandDate = new Date();
//        Optional<Loan> loanToDemand = loanRepository.findByAcid(acid);
//        if(loanToDemand.isPresent()){
//            //check next repayment date
//            Loan loan = loanToDemand.get();
//            Double sumPrincipleDemand=loan.getSumPrincipalDemand();
//
//            if(loan.getSumPrincipalDemand()>=(loan.getNetAmount()-5)){
//                EntityResponse response = new EntityResponse();
//                response.setMessage("ALL DEMANDS FOR THIS ACCOUNT HAVE BEEN GENERATED, ACCOUNT ID " + loan.getAccount().getAcid());
//                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                response.setEntity("");
//                entityResponses.add(response);
//            }else {
//                Date nextRepaymentDate =loan.getNextRepaymentDate();
//
//                while(nextRepaymentDate.compareTo(new Date ())<0  && sumPrincipleDemand<(loan.getNetAmount())){
////                    EntityResponse demandCreationEntity= createDemand2(loan);
//                    EntityResponse demandCreationEntity= createDemandByScheduleSlabs(loan);
//
//                    EntityResponse demandCreationEntity2=demandCreationEntity;
//                    demandCreationEntity2.setEntity(null);
//                    entityResponses.add(demandCreationEntity2);//createDemandByScheduleSlabs
//
//
//                    Integer responseCode=demandCreationEntity.getStatusCode();
//                    if(responseCode.equals(HttpStatus.CREATED.value())){
//                        loan = loanRepository.findByAcid(acid).get();
//                        nextRepaymentDate =loan.getNextRepaymentDate();
//                        sumPrincipleDemand =loan.getSumPrincipalDemand();
//                        continue;
//                    }else {
//                        break;
//                    }
//                }
//            }
//
//
//
//        }else {
//            EntityResponse response = new EntityResponse();
//            response.setMessage("FAILED! LOAN ACCOUNT COULD NOT BE FOUND ACCOUNT ID: "+acid);
//            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//            response.setEntity("");
//            entityResponses.add(response);
//        }
//        return entityResponses;
//    }catch (Exception e){
//        log.info("Catched Error {} " + e);
//            return null;
//    }
//
//}
//
//    public List<EntityResponse> raiseDemandsForAllLoans(){
//        try{
//            LocalDate today = LocalDate.now();
//            Date now =datesCalculator.convertLocalDateToDate(today);
//            List<EntityResponse> responses = new ArrayList<>();
//            List<Loan> loansToDemand = loanRepository.findByNextRepaymentDate(now);
//            if(loansToDemand.size()>0){
//                for (Loan loanToDemand : loansToDemand) {
//                    String acid = loanToDemand.getAccount().getAcid();
//                    log.info(acid);
//                    List<EntityResponse> entityResponse = demandManualForce(acid);
//                    responses.addAll(entityResponse);
//                    log.info(entityResponse.toString());
//                }
//            }else {
//                EntityResponse response = new EntityResponse();
//                response.setMessage("THERE ARE NO LOANS TO DEMAND");
//                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                responses.add(response);
//            }
//            return responses;
//        }catch (Exception e){
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }
//
//
//    public EntityResponse<List<LoanDemand>> getDemandInfoPerAcid(String acid){
//        try{
//            EntityResponse userValidator= validatorsService.userValidator();
//            if(userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
//                return userValidator;
//            }else {
//                List<LoanDemand> retailAccounts= loanDemandRepository.findByAcid(acid);
//                EntityResponse listChecker = validatorsService.listLengthChecker(retailAccounts);
//                return listChecker;
//            }
//        }catch (Exception e){
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }
//
//    //------DEMAND SATISFACTION-----------------------------------//
//    public List<EntityResponse> satisfyDemandManualForce(String acid){
//        try {
//            List<EntityResponse> entityResponses=new ArrayList<>();
//
//            List<LoanDemand>loanDemands= loanDemandRepository.findByAcid(acid);
//
//            if(loanDemands.size()>0){
//                loanDemands.forEach(loanDemand -> {
//                    //check if the demand has been fully satisfied;
//                    Double adjustmentAmount=loanDemand.getAdjustmentAmount();
//                    Integer statusCode= HttpStatus.CREATED.value();
//                    Double operativeAccountBalance=0.00;
//                    while (!statusCode.equals(HttpStatus.NOT_ACCEPTABLE.value())){
//                        if(adjustmentAmount>=loanDemand.getDemandAmount()){
//                            EntityResponse response = new EntityResponse();
//                            response.setMessage("DEMAND FULLY SATISFIED");
//                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                            entityResponses.add(response);
//                            break;
//                        }else {
//                            Long loanDemandId= loanDemand.getSn();
//
//                            String operativeAcid=loanDemand.getLoan().getOperativeAcountId();
//                            operativeAccountBalance=accountRepository.getAccountBalance(operativeAcid);
//                            if(operativeAccountBalance.equals(null)){
//                                operativeAccountBalance=0.00;
//                            }
//                            EntityResponse entityResponse1=satisfyDemand(loanDemand, operativeAcid,operativeAccountBalance);
//                            entityResponses.add(entityResponse1);
//
//                            statusCode=entityResponse1.getStatusCode();
//                            LoanDemand newLoanDemand= loanDemandRepository.findById(loanDemandId).get();
//                            adjustmentAmount=newLoanDemand.getAdjustmentAmount();
//                            continue;
//                        }
//                    }
//
//                });
//            }else{
//                EntityResponse response = new EntityResponse();
//                response.setMessage("NO DEMANDS FOR THIS LOAN ACCOUNT");
//                response.setStatusCode(HttpStatus.CREATED.value());
//                response.setEntity("");
//                entityResponses.add(response);
//            }
//
//            return entityResponses;
//        }catch (Exception e){
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }
//    public  EntityResponse satisfyDemand(LoanDemand loanDemand, String operativeAcid, Double operativeAccountBalance){
//        try{
//            String entity = EntityRequestContext.getCurrentEntityId();
//            Loan loan =loanDemand.getLoan();
//            Double demandedAmount =(loanDemand.getDemandAmount()-loanDemand.getAdjustmentAmount());
//            String demandType= loanDemand.getDemandType();
//            String loanAcid=loanDemand.getAcid();
//            if(!loan.getOperativeAcountId().equals("") || !loan.getOperativeAcountId().equals(null)){
//
//                //---get operative accaount balance-----//
//                Optional<Account> account = accountRepository.findByAcid(operativeAcid);
//                if(account.isPresent()){
//                    Account existingAccount=account.get();
//
//                    String drAc="";
//                    String crAc="";
//
//                    //------------transaction--------//
//                    drAc=operativeAcid;
//                    log.info("---------------Dr account-----> "+drAc);
//                    //TODO----------//
//                    crAc=loanAcid;
//                    log.info("---------------Cr account-----> "+crAc);
//                    String loanCurrency=loanDemand.getLoan().getAccount().getCurrency();
//
//                    //------//
//                    Double totalAmountToBeDebited=0.0;
//                    Double principleToBeDebited=0.0;
//                    Double interestToBeDebited=0.0;
//                    //------------//
//                    if(demandedAmount>0){
//                        if(operativeAccountBalance>=demandedAmount){
//                            //satisfy Demand fully
//                            totalAmountToBeDebited=demandedAmount;
//                            if(demandType.equals(CONSTANTS.INTEREST_DEMAND)){
//                                interestToBeDebited=demandedAmount;
//                            }else if(demandType.equals(CONSTANTS.PRINCIPAL_DEMAND)){
//                                principleToBeDebited=demandedAmount;
//                            }
//                            log.info("principleToBeDebited"+principleToBeDebited.toString());
//                            return createDemandSatisfactionTransaction( loanDemand,  drAc,
//                                    crAc, loanAcid, loan,
//                                    entity, loanCurrency, totalAmountToBeDebited,
//                                    principleToBeDebited,  interestToBeDebited);
//
//
//                        }else if(operativeAccountBalance >0 && operativeAccountBalance<demandedAmount){
//                            // satisfy demand partially
//                            Double demandBalance=demandedAmount-operativeAccountBalance;
//                            totalAmountToBeDebited=operativeAccountBalance;
//
//                            if(demandType.equals(CONSTANTS.INTEREST_DEMAND)){
//                                interestToBeDebited=operativeAccountBalance;
//                            }else if(demandType.equals(CONSTANTS.PRINCIPAL_DEMAND)){
//                                principleToBeDebited=operativeAccountBalance;
//                            }
//                            //todo: lien demand balance
//                            //accountTransactionService.addLien(operativeAcid, demandBalance);
//                            //todo: creating a transaction
//                            return createDemandSatisfactionTransaction( loanDemand,  drAc,
//                                    crAc, loanAcid, loan,
//                                    entity, loanCurrency, totalAmountToBeDebited,
//                                    principleToBeDebited,  interestToBeDebited);
//
//
//                        }else {
//
//                            EntityResponse response = new EntityResponse();
//                            response.setMessage("DEMAND NOT SATISFIED DUE TO INSUFFICIENT FUNDS IN OPERATIVE ACCOUNT FOR LOAN ACCOUNT: "+loanAcid);
//                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                            response.setEntity("");
//                            //todo: lien demand amount
//                            //accountTransactionService.addLien(operativeAcid, demandedAmount);
//                            return response;
//                        }
//                    }else{
//                        EntityResponse response = new EntityResponse();
//                        response.setMessage("DEMAND SATISFIED ALREADY");
//                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                        response.setEntity("");
//                        return response;
//                    }
//                }else {
//                    EntityResponse response = new EntityResponse();
//                    response.setMessage("COULD NOT FIND OPERATIVE ACCOUNT ");
//                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                    response.setEntity("");
//                    return response;
//                }
//
//            }else {
//                EntityResponse response = new EntityResponse();
//                response.setMessage("OPERATIVE ACCOUNT CANNOT BE NULL ");
//                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                response.setEntity("");
//                return response;
//            }
//
//
//
//
//        }catch (Exception e){
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }
//
//    public OutgoingTransactionDetails createTransactionModel(String loanCurrency,
//                                                             String entity,
//                                                             Double totalAmountToBeDebited,
//                                                        String drAc,String crAc,String acid){
//        try{
//            log.info("---------------------CREATING TRANSACTION MODEL------------");
//            //TODO: TRANSACTION MAIN DETAILS
//            OutgoingTransactionDetails outgoingTransactionDetails =new OutgoingTransactionDetails();
//            outgoingTransactionDetails.setTransactionType("SYSTEM");
//            outgoingTransactionDetails.setCurrency(loanCurrency);
//            outgoingTransactionDetails.setTransactionDate(new Date());
//            outgoingTransactionDetails.setEntityId(entity);
//            outgoingTransactionDetails.setTotalAmount(totalAmountToBeDebited);
//
//            //TODO: TRANSACTION DR DETAILS
//            OutgoingTransactionPartTrans drOutgoingTransactionPartTrans= new OutgoingTransactionPartTrans();
//            drOutgoingTransactionPartTrans.setPartTranType(CONSTANTS.DEBITSTRING);
//            drOutgoingTransactionPartTrans.setTransactionAmount(totalAmountToBeDebited);
//            drOutgoingTransactionPartTrans.setAcid(drAc);
//            drOutgoingTransactionPartTrans.setCurrency(loanCurrency);
//            drOutgoingTransactionPartTrans.setExchangeRate("");
//            drOutgoingTransactionPartTrans.setTransactionDate(new Date());
//            drOutgoingTransactionPartTrans.setTransactionParticulars("DEMAND SATISFACTION FOR LOAN ACCOUNT:  "+acid);
//            drOutgoingTransactionPartTrans.setIsoFlag("Y");
//
//            //TODO: TRANSACTION CR DETAILS
//            OutgoingTransactionPartTrans crOutgoingTransactionPartTrans= new OutgoingTransactionPartTrans();
//            crOutgoingTransactionPartTrans.setPartTranType(CONSTANTS.CREDITSTRING);
//            crOutgoingTransactionPartTrans.setTransactionAmount(totalAmountToBeDebited);
//            crOutgoingTransactionPartTrans.setAcid(crAc);
//            crOutgoingTransactionPartTrans.setCurrency(loanCurrency);
//            crOutgoingTransactionPartTrans.setExchangeRate("");
//            crOutgoingTransactionPartTrans.setTransactionDate(new Date());
//            crOutgoingTransactionPartTrans.setTransactionParticulars("DEMAND SATISFACTION FOR LOAN ACCOUNT: "+acid);
//            crOutgoingTransactionPartTrans.setIsoFlag("Y");
//
//            List<OutgoingTransactionPartTrans> listOutgoingTransactionPartTrans= new ArrayList<>();
//            listOutgoingTransactionPartTrans.add(drOutgoingTransactionPartTrans);
//            listOutgoingTransactionPartTrans.add(crOutgoingTransactionPartTrans);
//
//            outgoingTransactionDetails.setPartTrans(listOutgoingTransactionPartTrans);
//
//            log.info("------MODEL CREATED -----"+outgoingTransactionDetails.toString());
//            return outgoingTransactionDetails;
//        }catch (Exception e){
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }
//
//    public EntityResponse createDemandSatisfactionTransaction(LoanDemand loanDemand, String drAc,
//                                                                 String crAc,String loanAcid,Loan loan,
//                                                                 String entity,String loanCurrency,Double totalAmountToBeDebited,
//                                                                 Double principleToBeDebited, Double interestToBeDebited){
//        try{
//            //----PERFORM  TRANSACTIONS---------------------//
//            //----DEDIT CUSTOMER OPERARIVE ACCOUNT-------------//
//            //----CREDIT LOAN ACCOUNT---------------------------//
//
//            log.info("---------------------ENTEREDED IN TRANSACTION FUNCTION-------------------");
//
//            //------Validate cr and debit accounts------//
//            EntityResponse drAcValidator=validatorsService.acidValidator(drAc, "Customer operative");
//            if(drAcValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
//                return drAcValidator;
//
//            }else if(drAcValidator.getStatusCode().equals(200)){
//                //validate crAc
//                EntityResponse crAcValidator=validatorsService.acidValidator(crAc, "Loan");
//                if(crAcValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
//                    //Error
//                    return crAcValidator;
//                }else {
//                    //perform transaction
//                    // Perform transaction to Dr receivable and Cr P&L account
//                    OutgoingTransactionDetails outgoingTransactionDetails=createTransactionModel(loanCurrency, entity, totalAmountToBeDebited,
//                            drAc, crAc, loanAcid);
//                    String transactCode=accountTransactionService.createTransaction(outgoingTransactionDetails);
//                    if(transactCode.equals(CONSTANTS.FAILED)){
//                        ///failed transactiom
//                        EntityResponse response = new EntityResponse();
//                        response.setMessage("TRANSACTION ERROR! COULD NOT PERFORM DEMAND SATISFACTION TRANSACTION FOR LOAN ACCOUNT: "+loanAcid);
//                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                        response.setEntity(outgoingTransactionDetails);
//                        return response;
//
//                    }else {
//                        log.info("---------------------TRANSACTION SUCCESSFFUL-------------------");
//
//                        //update loan demand
//                        Double currentAdjustmentAmount= loanDemand.getAdjustmentAmount();
//                        loanDemand.setAdjustmentAmount(totalAmountToBeDebited+currentAdjustmentAmount);
//                        loanDemand.setAdjustmentDate(new Date());
//                        loanDemandRepository.save(loanDemand);
//
//                        //update loan account
//
//                        loan.getTotalLoanBalance();
//
//                        Double outStandingPrincipal =(loan.getOutStandingPrincipal()-principleToBeDebited);
//                        log.info("outStandingPrincipal"+outStandingPrincipal);
//                        Double outStandingInterest= (loan.getOutStandingInterest()-interestToBeDebited);
//                        Double totalLoanBalance=(loan.getTotalLoanBalance()-totalAmountToBeDebited);
//
//                        loan.setTotalLoanBalance(totalLoanBalance);
//                        loan.setOutStandingInterest(outStandingInterest);
//                        loan.setOutStandingPrincipal(outStandingPrincipal);
//                        loanRepository.save(loan);
//
//                        LoanDemandSatisfaction loanDemandSatisfaction = new LoanDemandSatisfaction();
//                        loanDemandSatisfaction.setAcid(loanDemand.getAcid());
//                        loanDemandSatisfaction.setLoanDemand(loanDemand);
//                        loanDemandSatisfaction.setAmount(totalAmountToBeDebited);
//                        loanDemandSatisfaction.setDate(new Date());
//                        loanDemandSatisfactionRepo.save(loanDemandSatisfaction);
//
//                        EntityResponse response = new EntityResponse();
//                        response.setMessage("SUCCESSFULLY POSTED DEMAND SATISFACTION TRANSACTION FOR LOAN ACCOUNT: "+loanAcid+ "AMOUNT: ");
//                        response.setStatusCode(HttpStatus.CREATED.value());
//                        response.setEntity(outgoingTransactionDetails);
//                        return response;
//
//                    }
//                }
//            }else {
//                EntityResponse response = new EntityResponse();
//                response.setMessage("ACCOUNT VERIFICATION ERROR! COULD NOT PERFORM DEMAND GENERATION TRANSACTION FOR LOAN ACCOUNT: "+loanAcid);
//                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                response.setEntity("");
//                return response;
//            }
//        }catch (Exception e){
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }
//
//
//    public List<EntityResponse> satisfyAllDemands(){
//        try{
//            List<EntityResponse> responses = new ArrayList<>();
//
//            //fetch all unsatisfied demands
//            List<LoanDemand> loanDemands = loanDemandRepository.findUnsatisfiedLoanDemands();
//            if(loanDemands.size()>0){
//                for (LoanDemand loanToDemand : loanDemands) {
//                    String operativeAcid=loanToDemand.getAcid();
//                    List<EntityResponse> entityResponse= satisfyDemandManualForce(operativeAcid);
//                    responses.addAll(entityResponse);
//                }
//            }else {
//                EntityResponse response = new EntityResponse();
//                response.setMessage("THERE ARE NO DEMANDS TO SATISFY");
//                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                responses.add(response);
//            }
//            return responses;
//        }catch (Exception e){
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }
//
//
//
//}
