package com.emtechhouse.accounts.Models.Accounts.Loans.LoanPenalties;

import com.emtechhouse.accounts.Models.Accounts.Account;
import com.emtechhouse.accounts.Models.Accounts.AccountClosure.AccountClosureRepo;
import com.emtechhouse.accounts.Models.Accounts.AccountRepository;
import com.emtechhouse.accounts.Models.Accounts.AccountServices.AccountTransactionService;
import com.emtechhouse.accounts.Models.Accounts.LienComponent.LienRepository;
import com.emtechhouse.accounts.Models.Accounts.Loans.Loan;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanAccrual.LoanAccrualService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanBooking.LoanBookingService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.DemandGenerationService;
//import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.GenerateDemandsForOneLoan;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.LoanDemand;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.LoanDemandRepository;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanFees.LoanFeesRepo;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanRepository;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanSchedule.LoanScheduleRepo;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.GeneralProductDetails;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ItemServiceCaller;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ProductFees.ProductFeesService;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ProductInterestDetails.ProductInterestDetails;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ProductInterestDetails.ProductInterestService;
import com.emtechhouse.accounts.Models.Accounts.Validators.ValidatorService;
import com.emtechhouse.accounts.TransactionService.TransactionUtils;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TransactionHeader;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TransactionsController;
import com.emtechhouse.accounts.Utils.AcidGenerator;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.DatesCalculator;
import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class LoanPenaltyService {
    @Autowired
    private LienRepository lienRepository;
    @Autowired
    private DatesCalculator datesCalculator;
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private LoanDemandRepository loanDemandRepository;
    @Autowired
    private ItemServiceCaller itemServiceCaller;

    @Autowired
    private ValidatorService validatorsService;

    @Autowired
    TransactionUtils transactionUtils;

/*    @Autowired
    private GenerateDemandsForOneLoan generateDemandsForOneLoan; */
    @Autowired
    private AccountTransactionService accountTransactionService;
    @Autowired
    private LoanFeesRepo loanFeesRepo;
    @Autowired
    private ProductInterestService productInterestService;
    @Autowired
    private AcidGenerator acidGenerator;
    @Autowired
    private LoanAccrualService loanAccrualService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private LoanBookingService loanBookingService;
    @Autowired
    private LoanScheduleRepo loanScheduleRepo;

    @Autowired
    private TransactionsController transactionsController;

    @Autowired
    private ProductFeesService productFeesService;

    @Autowired
    private AccountClosureRepo accountClosureRepo;
    @Autowired
    private LoanPenaltyRepo loanPenaltyRepo;

    @Autowired
    private DemandGenerationService demandGenerationService;


    public EntityResponse createLoanPenalty(String loanAcid,LoanPenalty loanPenalty){
        try {
            EntityResponse res= new EntityResponse();
            Optional<Account> account= accountRepository.findByAcidAndDeleteFlag(loanAcid, CONSTANTS.NO);
            if(account.isPresent()){
                Account presentAccount =account.get();
                if(presentAccount.getAccountType().equalsIgnoreCase(CONSTANTS.LOAN_ACCOUNT)){
                    Loan loan= presentAccount.getLoan();
                    if(loan.getLoanStatus().equals(CONSTANTS.DISBURSED)){
                        if(loanPenalty.getPenaltyAmount()>0){
                            String postedBy = UserRequestContext.getCurrentUser();

                            loanPenalty.setLoan(loan);
                            loanPenalty.setLoanAcid(loanAcid);
                            loanPenalty.setLoanName(loan.getAccount().getAccountName());
                            loanPenalty.setVerifiedFlag(CONSTANTS.NO);
                            loanPenalty.setPostedBy(postedBy);
                            loanPenalty.setPostedTime(new Date());
                            loanPenalty.setPostedFlag(CONSTANTS.YES);

                            LoanPenalty sLoanPenalty=loanPenaltyRepo.save(loanPenalty);

                            res.setMessage("Loan penalty created successfully");
                            res.setStatusCode(HttpStatus.CREATED.value());
                            res.setEntity(sLoanPenalty);
                        }else {
                            res.setMessage("Penalty amount should be greater than zero");
                            res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        }
                    }else {
                        res.setMessage("Seems like the loan is not disbursed or has been fully paid");
                        res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    }
                }else {
                    res.setMessage("Could not find a loan account with account number "+loanAcid);
                    res.setStatusCode(HttpStatus.NOT_FOUND.value());
                }
            }else {
                res.setMessage("Could not find account with account number "+loanAcid);
                res.setStatusCode(HttpStatus.NOT_FOUND.value());
            }
            return res;
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse verifyPenalty(Long id) {
        try{
            EntityResponse res= new EntityResponse();
            Optional<LoanPenalty> loanPenalty= loanPenaltyRepo.findByIdAndDeletedFlag(id,CONSTANTS.NO);
            if(loanPenalty.isPresent()) {
                log.info("penalty present");
                LoanPenalty existingLoanPenalty = loanPenalty.get();
                if(existingLoanPenalty.getVerifiedFlag().equals(CONSTANTS.NO)){
                    String currentUser = UserRequestContext.getCurrentUser();
//                    if(!existingLoanPenalty.getPostedBy().equals(currentUser)){
                        Loan loan =existingLoanPenalty.getLoan();
                        System.out.println("1");
                        Double amt= existingLoanPenalty.getPenaltyAmount();
                        System.out.println("2");

                        res= createPenalInterestDemand(loan,amt, existingLoanPenalty.getPenaltyDescription());
                        if(res.getStatusCode()==HttpStatus.CREATED.value()){

                            //update loan balance
                            Double totalLoanBalnce= loanRepository.getLoanTotalBalance(loan.getSn());
                            Double newLoanBalance = totalLoanBalnce+amt;
                            loanRepository.updateLoanBalance(newLoanBalance, loan.getSn());

                            loanPenaltyRepo.updateVerifiedFlag(CONSTANTS.YES,id);
                            loanPenaltyRepo.updateVerifiedBy(currentUser, id);
                            loanPenaltyRepo.updateVerifiedTime(new Date(), id);
                            //update verified time
                            //update verified by

                            res.setMessage("Verification was successful");
                            res.setStatusCode(HttpStatus.OK.value());
                            //update verified flag to yes
                        }else {
                            return res;
                        }
//                    }else {
//                        res.setMessage("You cannot verify what you started");
//                        res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                    }

                }else {
                    res.setMessage("Seems like the penalty has already been verified");
                    res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                }
            }else {
                res.setMessage("Loan penalty is not found ");
                res.setStatusCode(HttpStatus.NOT_FOUND.value());
            }
            return res;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse modifyLoanPenalty(LoanPenalty loanPenalty) {
        try {

            String currentUser = UserRequestContext.getCurrentUser();
            Long id=loanPenalty.getId();
            EntityResponse res= new EntityResponse();
            Optional<LoanPenalty> loanPenalty1= loanPenaltyRepo.findByIdAndDeletedFlag(id,CONSTANTS.NO);
            if(loanPenalty1.isPresent()) {
                log.info("penalty present");
                LoanPenalty existingLoanPenalty = loanPenalty1.get();
                if(existingLoanPenalty.getVerifiedFlag().equals(CONSTANTS.NO)){
                    if(loanPenalty.getPenaltyAmount()>0){

                        loanPenalty.setPostedFlag(existingLoanPenalty.getPostedFlag());
                        loanPenalty.setPostedBy(existingLoanPenalty.getPostedBy());
                        loanPenalty.setPostedTime(existingLoanPenalty.getPostedTime());

                        loanPenalty.setModifiedBy(currentUser);
                        loanPenalty.setModifiedTime(new Date());
                        loanPenalty.setModifiedFlag(CONSTANTS.YES);
                        loanPenalty.setVerifiedFlag(CONSTANTS.NO);
                        LoanPenalty sLoanPenalty= loanPenaltyRepo.save(loanPenalty);

                        res.setMessage("Penalty updated successfully");
                        res.setStatusCode(HttpStatus.OK.value());
                        res.setEntity(sLoanPenalty);
                    }else {
                        res.setMessage("Penalty amount should be greater than zero");
                        res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    }
                }else {
                    res.setMessage("Modification cannot be done after verification");
                    res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                }
            }else {
                res.setMessage("Loan penalty is not found ");
                res.setStatusCode(HttpStatus.NOT_FOUND.value());
            }
            return res;
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse deleteLoanPenalty(Long id){
        try {

            String currentUser = UserRequestContext.getCurrentUser();
            EntityResponse res= new EntityResponse();
            Optional<LoanPenalty> loanPenalty1= loanPenaltyRepo.findByIdAndDeletedFlag(id,CONSTANTS.NO);
            if(loanPenalty1.isPresent()) {
                log.info("penalty present");
                LoanPenalty existingLoanPenalty = loanPenalty1.get();
                if(existingLoanPenalty.getVerifiedFlag().equals(CONSTANTS.NO)){
                    existingLoanPenalty.setDeletedFlag(CONSTANTS.YES);
                    existingLoanPenalty.setDeleteTime(new Date());
                    existingLoanPenalty.setDeletedBy(currentUser);
                    loanPenaltyRepo.save(existingLoanPenalty);

                    res.setMessage("Penalty deleted successfully");
                    res.setStatusCode(HttpStatus.OK.value());
                }else {
                    res.setMessage("Deletion cannot be done after verification");
                    res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                }
            }else {
                res.setMessage("Loan penalty is not found ");
                res.setStatusCode(HttpStatus.NOT_FOUND.value());
            }
            return res;
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse findByLoanAcid(String acid){
        try {
            String currentUser = UserRequestContext.getCurrentUser();
            EntityResponse res= new EntityResponse();

            Optional<Account> account= accountRepository.findByAcidAndDeleteFlag(acid,CONSTANTS.NO );
            if(account.isPresent()){
                Account pAccount = account.get();
                if(pAccount.getAccountType().equals(CONSTANTS.LOAN_ACCOUNT)){
                    Loan loan = pAccount.getLoan();
                    List<LoanPenalty> ls=loanPenaltyRepo.findByLoanAndDeletedFlag(loan, CONSTANTS.NO);
                    if(ls.size()>0){
                        res.setMessage(HttpStatus.FOUND.getReasonPhrase());
                        res.setStatusCode(HttpStatus.FOUND.value());
                        res.setEntity(ls);
                    }else {
                        res.setMessage("Could not find any penalties "+acid);
                        res.setStatusCode(HttpStatus.NOT_FOUND.value());
                    }
                }else {
                    res.setMessage("Could not find a loan account with account number "+acid);
                    res.setStatusCode(HttpStatus.NOT_FOUND.value());
                }
            }else {
                res.setMessage("Could not find account with account number "+acid);
                res.setStatusCode(HttpStatus.NOT_FOUND.value());
            }

            return res;
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse findById(Long id){
        try {
            EntityResponse res= new EntityResponse();
            Optional<LoanPenalty> loanPenalty= loanPenaltyRepo.findByIdAndDeletedFlag(id, CONSTANTS.NO);
            if(loanPenalty.isPresent()){
                res.setMessage(HttpStatus.FOUND.getReasonPhrase());
                res.setStatusCode(HttpStatus.FOUND.value());
                res.setEntity(loanPenalty.get());
            }else {
                res.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                res.setStatusCode(HttpStatus.NOT_FOUND.value());
            }
            return res;
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse getAllUnverifiedLoanPenalties() {
        try {
            EntityResponse res= new EntityResponse();
            List<LoanPenalty> loanPenaltyList= loanPenaltyRepo.findAllByDeletedFlagAndVerifiedFlag(CONSTANTS.NO, CONSTANTS.NO);
            if(loanPenaltyList.size()>0){
                res.setMessage(HttpStatus.FOUND.getReasonPhrase());
                res.setStatusCode(HttpStatus.FOUND.value());
                res.setEntity(loanPenaltyList);
            }else {
                res.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                res.setStatusCode(HttpStatus.NOT_FOUND.value());
            }
            return res;
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }



    public EntityResponse createPenalInterestDemand(Loan loan, Double penaltyAmount, String description) {
        try{
            String productCode= loan.getAccount().getProductCode();
            if(penaltyAmount>0){
                EntityResponse entityResponse= itemServiceCaller.getGeneralProductDetail1(productCode,
                        CONSTANTS.LOAN_ACCOUNT);
                if(entityResponse.getStatusCode().equals(HttpStatus.OK.value())) {
                    //penal interest receivable account
                    GeneralProductDetails newGeneralProductDetails = (GeneralProductDetails) entityResponse.getEntity();
                    String crAcid = newGeneralProductDetails.getPenal_int_receivable_ac();


                    String drAcid= loan.getAccount().getAcid();
                    String acid= drAcid;

                    log.info("Validating in loan account");
                    EntityResponse drAcValidator=validatorsService.acidValidator(drAcid, "Loan");
                    if(drAcValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                        return drAcValidator;
                    } else {
                        EntityResponse crAcValidator=validatorsService.acidValidator(crAcid, "Penalty Interest receivable");
                        if (crAcValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                            return crAcValidator;
                        } else {
                            //perform transaction;
                            String loanCurrency=loan.getAccount().getCurrency();
//                            String transactionDescription="PENALTY DEMAND FOR LOAN: "+acid;
                            TransactionHeader tranHeader= transactionUtils.createTransactionHeader(loanCurrency,
                                    description,
                                    penaltyAmount,
                                    drAcid,
                                    crAcid);
                            log.info("account bal before transaction is :: "+accountRepository.fetchAccountBalance(drAcid));
                            EntityResponse transactionRes= transactionsController.systemTransaction1(tranHeader).getBody();

                            if(!transactionRes.getStatusCode().equals(HttpStatus.OK.value())){
                                ///failed transactiom
                                EntityResponse response = new EntityResponse();
                                response.setMessage("TRANSACTION ERROR! COULD NOT PERFORM PENAL INTEREST DEMAND GENERATION TRANSACTION FOR LOAN ACCOUNT: "+acid);
                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                response.setEntity(tranHeader);
                                return response;

                            }else{
                                // accruel penal interest amount
                                loanAccrualService.accruePenalInterest( loan,  penaltyAmount);

                                String transactionCode= (String) transactionRes.getEntity();
                                return createPenalInterestDemandModel(loan, penaltyAmount,transactionCode);
                            }
                        }
                    }

                }else {
                    log.info("Product service response code NOT OK "+entityResponse.getStatusCode());
                    return entityResponse;
                }
            }else {
                EntityResponse res= new EntityResponse<>();
                res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                res.setMessage("PENAL INTEREST <0");
                return res;
            }
        }catch (Exception e){
            log.info("Catched Error {} " + e.getStackTrace()[0].getLineNumber()+" - "+e);
            return null;
        }
    }
    public EntityResponse<LoanDemand> createPenalInterestDemandModel(LoanDemand loanDemand1, Double penaltyAmount, String transactionCode){
        try{
            log.info("account bal before saving demand  is :: "+accountRepository.fetchAccountBalance(loanDemand1.getAcid()));
            log.info("crating penal interest demand");
            String demandCode =acidGenerator.generateDemandCode(CONSTANTS.PENAL_INTEREST_DEMAND);

            LoanDemand loanDemand = new LoanDemand();
            Double loanRemainingBalance= loanDemand1.getLoanRemainingBalance()+penaltyAmount;
            Loan loan=loanRepository.findByAcountId(loanDemand1.getAcid());
            loanDemand.setDemandCode(demandCode);
            loanDemand.setAcid(loanDemand1.getAcid());
            loanDemand.setDemandAmount(penaltyAmount);
            loanDemand.setInterestAmount(0.0);
            loanDemand.setPrincipalAmount(0.0);
            loanDemand.setDemandType(CONSTANTS.PENAL_INTEREST_DEMAND);
            loanDemand.setAdjustmentDate(null);
            loanDemand.setAdjustmentAmount(0.0);
            loanDemand.setDemandDate(new Date());
            loanDemand.setCreatedBy(CONSTANTS.SYSTEM_USERNAME);
            loanDemand.setTransactionCode(transactionCode);
            loanDemand.setDemandCarriedFowardFlag(CONSTANTS.NO);
            loanDemand.setLoan(loan);
            loanDemand.setCreatedOn(new Date());
            loanDemand.setLoanScheduleId(0L);
            loanDemand.setLoanRemainingBalance(loanRemainingBalance);
            loanDemand.setFeeAmount(0.0);
            loanDemand.setPenalInterestAmount(penaltyAmount);
            LoanDemand sLoanDemand=loanDemandRepository.save(loanDemand);

            log.info("account bal after saving demand  is :: "+accountRepository.fetchAccountBalance(loanDemand1.getAcid()));

            EntityResponse response = new EntityResponse();
            response.setMessage("OK");
            response.setStatusCode(HttpStatus.CREATED.value());
            response.setEntity(sLoanDemand);
            return response;

        }catch (Exception e){
            log.info("Catched Error {} " + e.getStackTrace()[0].getLineNumber()+" - "+e);
            return null;
        }
    }


    // TODO: 3/27/2023 overloaded used for mannually created penalties
    public EntityResponse<LoanDemand> createPenalInterestDemandModel(Loan loan, Double penaltyAmount, String transactionCode){
        try{
            log.info("crating penal interest demand");
            String demandCode =acidGenerator.generateDemandCode(CONSTANTS.PENAL_INTEREST_DEMAND);

            LoanDemand loanDemand = new LoanDemand();
            Double loanRemainingBalance= loan.getTotalLoanBalance()+penaltyAmount;
            loanDemand.setDemandCode(demandCode);
            loanDemand.setAcid(loan.getAccount().getAcid());
            loanDemand.setDemandAmount(penaltyAmount);
            loanDemand.setInterestAmount(0.0);
            loanDemand.setPrincipalAmount(0.0);
            loanDemand.setDemandType(CONSTANTS.PENAL_INTEREST_DEMAND);
            loanDemand.setAdjustmentDate(null);
            loanDemand.setAdjustmentAmount(0.0);
            loanDemand.setDemandDate(new Date());
            loanDemand.setCreatedBy(CONSTANTS.SYSTEM_USERNAME);
            loanDemand.setTransactionCode(transactionCode);
            loanDemand.setDemandCarriedFowardFlag(CONSTANTS.NO);
            loanDemand.setLoan(loan);
            loanDemand.setCreatedOn(new Date());
            loanDemand.setLoanScheduleId(0L);
            loanDemand.setLoanRemainingBalance(loanRemainingBalance);
            loanDemand.setFeeAmount(0.0);
            loanDemand.setPenalInterestAmount(penaltyAmount);
            LoanDemand sLoanDemand=loanDemandRepository.save(loanDemand);

            EntityResponse response = new EntityResponse();
            response.setMessage("OK");
            response.setStatusCode(HttpStatus.CREATED.value());
            response.setEntity(sLoanDemand);
            return response;

        }catch (Exception e){
            log.info("Catched Error {} " + e.getStackTrace()[0].getLineNumber()+" - "+e);
            return null;
        }
    }


    //----PENAL INTEREST DEMAND
    @CacheEvict(value = "first", allEntries = true)
    public EntityResponse createPenalInterestDemand(LoanDemand loanDemand) {
        try {
            String productCode= loanDemand.getLoan().getAccount().getProductCode();
            Double loanAmt= loanDemand.getLoan().getPrincipalAmount();
            //get penalInterest
            log.info("Calling product service to get penal interest amount");
            EntityResponse penalIntRes=productInterestService.getLoanInterest(productCode,
                    loanAmt);
            if(penalIntRes.getStatusCode().equals(HttpStatus.OK.value())) {
                ProductInterestDetails productInterestDetails= (ProductInterestDetails) penalIntRes.getEntity();
                Double penaltyAmount= productInterestDetails.getPenalInterestAmount();
                if(penaltyAmount>0){
                    EntityResponse entityResponse= itemServiceCaller.getGeneralProductDetail1(productCode,
                            CONSTANTS.LOAN_ACCOUNT);
                    if(entityResponse.getStatusCode().equals(HttpStatus.OK.value())) {
                        //penal interest receivable account
                        GeneralProductDetails newGeneralProductDetails = (GeneralProductDetails) entityResponse.getEntity();
                        String crAcid = newGeneralProductDetails.getPenal_int_receivable_ac();


                        String drAcid= loanDemand.getLoan().getAccount().getAcid();
                        String acid= loanDemand.getLoan().getAccount().getAcid();

                        log.info("Validating in loan account");
                        EntityResponse drAcValidator=validatorsService.acidValidator(drAcid, "Loan");
                        if(drAcValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                            return drAcValidator;
                        }else {
                            EntityResponse crAcValidator=validatorsService.acidValidator(crAcid, "Penal Interest recievable");
                            if(crAcValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                                return crAcValidator;
                            }else {
                                //perform transaction;
                                String loanCurrency=loanDemand.getLoan().getAccount().getCurrency();
                                String transactionDescription="PENALTY DEMAND LOAN : "+acid;
                                TransactionHeader tranHeader= transactionUtils.createTransactionHeader(loanCurrency,
                                        transactionDescription,
                                        penaltyAmount,
                                        drAcid,
                                        crAcid);
                                log.info("account bal before transaction is :: "+accountRepository.fetchAccountBalance(drAcid));
                                EntityResponse transactionRes= transactionsController.systemTransaction1(tranHeader).getBody();

                                if(!transactionRes.getStatusCode().equals(HttpStatus.OK.value())){
                                    ///failed transactiom
                                    EntityResponse response = new EntityResponse();
                                    response.setMessage("TRANSACTION ERROR! COULD NOT PERFORM PENAL INTEREST DEMAND GENERATION TRANSACTION FOR LOAN ACCOUNT: "+acid);
                                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                    response.setEntity(tranHeader);
                                    return response;

                                }else{
                                    // accruel penal interest amount
                                    loanAccrualService.accruePenalInterest( loanDemand.getLoan(),  penaltyAmount);

                                    String transactionCode= (String) transactionRes.getEntity();
                                    return createPenalInterestDemandModel(loanDemand, penaltyAmount,transactionCode);
                                }
                            }
                        }

                    }else {
                        log.info("Product service response code NOT OK "+entityResponse.getStatusCode());
                        return entityResponse;
                    }
                }else {
                    EntityResponse res= new EntityResponse<>();
                    res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    res.setMessage("PENAL INTEREST <0");
                    return res;
                }


            }else {
                log.info("Product service response code NOT OK "+penalIntRes.getStatusCode());
                return penalIntRes;
            }
        }catch (Exception e){
            log.info("Catched Error {} " + e.getStackTrace()[0].getLineNumber()+" - "+e);
            return null;
        }
    }

}
