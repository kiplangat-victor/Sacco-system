package com.emtechhouse.accounts.Models.Accounts.Loans.LoanGuarantor;

import com.emtechhouse.accounts.Models.Accounts.Account;
import com.emtechhouse.accounts.Models.Accounts.AccountRepository;
import com.emtechhouse.accounts.Models.Accounts.Loans.Loan;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanCollateral.LoanCollateral;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.DemandSatisfaction.LoanDemandSatisfactionRepo;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanRepository;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ItemServiceCaller;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.SpecificProductDetails.LoanAccountProduct;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LoanGuarantorService {
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private LoanGuarantorRepository loanGuarantorRepository;

    @Autowired
    private ItemServiceCaller itemServiceCaller;
    @Autowired
    private LoanDemandSatisfactionRepo loanDemandSatisfactionRepo;

    public EntityResponse getGuarantorAvailableAmount(String customerCode) {
        try{
            //get customer 'guarantable' amount
            //get the total amount guarantor has guaranteed
            // b balance on S02 account
            Double availableAmountToGuarantee=0.0;
            Double totalUsedToGuarantee=0.0;
            Double shareCapitalAcBal=0.0;
            //get balance on customer S02 account
            Optional<LoanGuarantorRepository.GuarantorSBABalance> guarantorShareCapitalAccountDetails= loanGuarantorRepository.getGuarantorShareCapitalAccountDetails(customerCode,
                    CONSTANTS.NO);
            if(guarantorShareCapitalAccountDetails.isPresent()){
                log.info("Guarantor customer code is :: "+customerCode);
                log.info("Guarantor is present :: "+customerCode);

                LoanGuarantorRepository.GuarantorSBABalance guarantorSBABalance =guarantorShareCapitalAccountDetails.get();
                shareCapitalAcBal= guarantorSBABalance.getaccount_balance();
                LoanGuarantorRepository.TotalGivenAsGuarantee totalAmountUsedAsGuarantee=loanGuarantorRepository.getTotalAmountGivenToGuarantees(customerCode,
                        CONSTANTS.DISBURSED).orElse(null);
                if(totalAmountUsedAsGuarantee.gettotalGuaranteeAmount() !=null) {
                    totalUsedToGuarantee=totalAmountUsedAsGuarantee.gettotalGuaranteeAmount();
                }else{
                    totalUsedToGuarantee=0.0;
                }
                log.info("shareCapitalAcBal: "+shareCapitalAcBal);
                log.info("totalUsedToGuarantee: "+totalUsedToGuarantee);
                availableAmountToGuarantee=shareCapitalAcBal-totalUsedToGuarantee;

                EntityResponse response = new EntityResponse();
                response.setMessage(HttpStatus.OK.getReasonPhrase());
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(availableAmountToGuarantee);
                return response;
            }else {
                //throw an error
                EntityResponse response = new EntityResponse();
                response.setMessage("Invalid! Guarantor: "+customerCode+" does not have a share capital account");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;
            }
        }catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    // TODO: 3/20/2023 to be used by fronted
    public EntityResponse getGuarantorAvailableAmount(String customerCode, Double amt) {
        try{
            //get customer 'guarantable' amount
            //get the total amount guarantor has guaranteed
            // b balance on S02 account
            Double availableAmountToGuarantee=0.0;
            Double totalUsedToGuarantee=0.0;
            Double shareCapitalAcBal=0.0;
            //get balance on customer S02 account
            Optional<LoanGuarantorRepository.GuarantorSBABalance> guarantorShareCapitalAccountDetails= loanGuarantorRepository.getGuarantorShareCapitalAccountDetails(customerCode,
                    CONSTANTS.NO);
            if(guarantorShareCapitalAccountDetails.isPresent()){
                log.info("Guarantor customer code is :: "+customerCode);
                log.info("Guarantor is present :: "+customerCode);

                LoanGuarantorRepository.GuarantorSBABalance guarantorSBABalance =guarantorShareCapitalAccountDetails.get();
                shareCapitalAcBal= guarantorSBABalance.getaccount_balance();
                LoanGuarantorRepository.TotalGivenAsGuarantee totalAmountUsedAsGuarantee=loanGuarantorRepository.getTotalAmountGivenToGuarantees(customerCode,
                        CONSTANTS.DISBURSED).orElse(null);
                if(totalAmountUsedAsGuarantee.gettotalGuaranteeAmount() !=null) {
                    totalUsedToGuarantee=totalAmountUsedAsGuarantee.gettotalGuaranteeAmount();
                }else{
                    totalUsedToGuarantee=0.0;
                }
                log.info("shareCapitalAcBal: "+shareCapitalAcBal);
                log.info("totalUsedToGuarantee: "+totalUsedToGuarantee);
                availableAmountToGuarantee=shareCapitalAcBal-totalUsedToGuarantee;

                EntityResponse response = new EntityResponse();
                if(availableAmountToGuarantee>=amt){
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                }else {
                    response.setMessage("The available to guarantee is :: "+availableAmountToGuarantee);
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                }
                response.setEntity(availableAmountToGuarantee);
                return response;


            }else {
                //throw an error
                EntityResponse response = new EntityResponse();
                response.setMessage("Invalid! Guarantor: "+customerCode+" does not have a share capital account");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;
            }
        }catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    //validate all guarantors
    public EntityResponse getTotalGuranteedAmount(List<LoanGuarantor> loanGuarantorList){
        if (loanGuarantorList == null){
            EntityResponse response = new EntityResponse();
            response.setMessage(HttpStatus.OK.getReasonPhrase());
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(0);
            return response;
        }
        try{
            sumAllGuarantorsAmount(loanGuarantorList);
            Double totalGuaranteedAmt=0.00;
            EntityResponse entityResponse=new EntityResponse<>();
            entityResponse.setEntity(totalGuaranteedAmt);
            entityResponse.setStatusCode(HttpStatus.OK.value());
            for(Integer i=0;i<loanGuarantorList.size();i++) {

                if(loanGuarantorList.get(i).getGuarantorType().equalsIgnoreCase(CONSTANTS.SALARY)){
                    //okay
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    entityResponse=response;
                }else {
                    String guarantorCustomerCode=loanGuarantorList.get(i).getGuarantorCustomerCode();
                    System.out.println("Guarantor verify");
                    EntityResponse guarantorEntityResponse=getGuarantorAvailableAmount(guarantorCustomerCode);
                    log.info("guarantor ety response :: "+guarantorEntityResponse.getStatusCode());
                    if(guarantorEntityResponse.getStatusCode().equals(HttpStatus.OK.value())){

                        Double availableAmountToGuarantee= (Double) guarantorEntityResponse.getEntity();

                        if(availableAmountToGuarantee>= loanGuarantorList.get(i).getGuaranteeAmount()){
                            totalGuaranteedAmt=totalGuaranteedAmt+loanGuarantorList.get(i).getGuaranteeAmount();

                            EntityResponse response = new EntityResponse();
                            response.setMessage(HttpStatus.OK.getReasonPhrase());
                            response.setStatusCode(HttpStatus.OK.value());
                            entityResponse=response;
                        }else {
                            EntityResponse response = new EntityResponse();
                            response.setMessage("Guarantor of :"+ loanGuarantorList.get(i).getGuarantorCustomerCode()+" can only " +
                                    "guarntee up to Ksh "+availableAmountToGuarantee);
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            entityResponse=response;
                            break;
                        }

                    }else {
                        entityResponse=guarantorEntityResponse;
                        break;
                    }
                }
            }
            System.out.println();
            return entityResponse;
        }catch (Exception e) {
            System.out.println("Error in getTotalGuranteedAmount");
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse validateTotalGuaranteedAmtAndLoanAmount(Double loanAmt,
                                                                  List<LoanGuarantor> loanGuarantorList){
        try{
            log.info("Validating total guaranteed amount");
            //check if there are duplicates in the loan guarantors lis
            Boolean checkDuplicates = checkDuplicates(loanGuarantorList);
            EntityResponse response = new EntityResponse();
            if(checkDuplicates.equals(false)){
                EntityResponse loanTotalAmtGuaranteedEtyRes= getTotalGuranteedAmount(loanGuarantorList);
                if(loanTotalAmtGuaranteedEtyRes.getStatusCode().equals(HttpStatus.OK.value())){
//                    Double totalLoanAmtGuaranteed= (Double) loanTotalAmtGuaranteedEtyRes.getEntity();
                    //check if guaranteed amount can cover the loan

                    //not applicabe since collaterals also can be used to guarantee
//                    if(totalLoanAmtGuaranteed>=loanAmt){
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.OK.value());
//                    }else {
//                        response.setMessage("Invalid! Total Guaranteed Amount should be not less than loan amount:"+ loanAmt);
//                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                    }
                    response.setEntity(null);
                    return response;
                }else {
                    return loanTotalAmtGuaranteedEtyRes;
                }
            }else {
                response.setMessage("Failed! Duplicates guarantors found");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;
            }
        }catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse validateNumberOfGuarantors(String productCode,
                                                     List<LoanGuarantor> loanGuarantors){
        try{
            Boolean checkDuplicates = checkDuplicates(loanGuarantors);
            if(checkDuplicates.equals(true)){
                log.info("there are duplicates");
            }else {
                log.info("there are no duplicates");
            }
            log.info("Validating the number of guarantors");
            EntityResponse res = new EntityResponse();
            Integer guarantorsNo= loanGuarantors.size();
            log.info("The guarantors size is :: "+guarantorsNo);
            EntityResponse laaDetails= itemServiceCaller.getLoanAccountProductDetails(productCode);
            System.out.println(laaDetails);
            log.info("response status code is :: "+laaDetails.getStatusCode());
            if(laaDetails.getStatusCode().equals(HttpStatus.OK.value())){
                LoanAccountProduct loanAccountProduct= (LoanAccountProduct) laaDetails.getEntity();

                Integer minGuarantorCount = loanAccountProduct.getMinGuarantorCount();
                log.info("product min no is "+ minGuarantorCount);
                Integer maxGuarantorCount = loanAccountProduct.getMaxGuarantorCount();
                log.info("product max no is "+ maxGuarantorCount);

                if(minGuarantorCount != null && maxGuarantorCount !=null){
                    log.info("Guarantor size is not null");
                    if(minGuarantorCount == (int)minGuarantorCount && maxGuarantorCount == (int)maxGuarantorCount){
                        log.info("Guarantor is int");
                        if(guarantorsNo < minGuarantorCount){
                            log.warn("Minimum number of guarantors should be: "+minGuarantorCount);
                            res.setMessage(" Minimum number of guarantors should be: "+minGuarantorCount);
                            res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        }else {
                            if(guarantorsNo> maxGuarantorCount){
                                log.warn(" Maximum number of guarantors should be: "+minGuarantorCount);
                                res.setMessage(" Maximum number of guarantors should be: "+minGuarantorCount);
                                res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            }else {
                                log.warn("Guarantor validation successful ");
                                res.setMessage(" Ok ");
                                res.setStatusCode(HttpStatus.OK.value());
                            }
                        }
                    }
                }else {
                    res.setMessage(" Ok ");
                    res.setStatusCode(HttpStatus.OK.value());
                }
                return res;
            }else{
                return laaDetails;
            }
        }catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse addGuarantorToALoan(LoanGuarantor loanGuarantor, String acid){
        try {
            EntityResponse res = new EntityResponse();
            Optional<Account> account= accountRepository.findByAcidAndDeleteFlag(acid, CONSTANTS.NO);
            if(account.isPresent()){
                Account pAc= account.get();
                if(pAc.getAccountType().equals(CONSTANTS.LOAN_ACCOUNT)){
                    Loan loan= pAc.getLoan();
//                    if(loanGuarantorRepository.existsByGuarantorCustomerCodeAndLoan(
//                            loanGuarantor.getGuarantorCustomerCode(),
//                            loan
//                    )){
//                        res.setMessage(" Guarantor already in the system");
//                        res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                    }else {
                        loanGuarantor.setLoan(loan);
                        LoanGuarantor sLoanGuarantor=loanGuarantorRepository.save(loanGuarantor);

                        res.setMessage(" Ok ");
                        res.setEntity(sLoanGuarantor);
                        res.setStatusCode(HttpStatus.OK.value());
//                    }

                }else {
                    res.setMessage(" Account not a loan account "+acid);
                    res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                }
            }else {
                res.setMessage("Account not found "+acid);
                res.setStatusCode(HttpStatus.NOT_FOUND.value());
            }

            return res;
        }catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }


    public void deleteGuarantors(List<LoanGuarantor> loanGuarantors){
        try{
            log.info("Guarantors size: "+loanGuarantors.size());
            loanGuarantors.forEach(l -> {
                log.info("deleting guarantors");
                l.setLoan(null);
                loanGuarantorRepository.deleteById(l.getId());
            });
        }catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
    }

    public void deleteGuarantorsInAnAccount(String acid){
        try{
            Optional<Account> a =accountRepository.findByAccountId(acid);
            if(a.isPresent()){
                Account pa =a.get();
                if(pa.getAccountType().equals(CONSTANTS.LOAN_ACCOUNT)){
                    List<LoanGuarantor> loanGuarantors =pa.getLoan().getLoanGuarantors();
                    if(loanGuarantors.size()>0){
                        deleteGuarantors(loanGuarantors);
                    }
                }
            }
        }catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
    }

    public Double sumAllGuarantorsAmount(List<LoanGuarantor> l){
        Double amt=0.0;
        try{
            if(l !=null){
                if(l.size()>0){
                    Double sum =l.stream().mapToDouble(LoanGuarantor :: getGuaranteeAmount).sum();
                    amt=sum;
                    log.info("sum guaranteed amount is :: "+sum);
                    log.info("The sum is :: "+sum);
                }
            }
            return amt;
        }catch (Exception e){
            log.error("Catched exception {} "+e);
            return null;
        }
    }

    // TODO: 3/21/2023  Sum all collaterals

    public Double sumAllCollateralAmount(List<LoanCollateral> l) {
        Double amt=0.0;
        try{
            if(l !=null){
                if(l.size()>0){
                    Double sum =l.stream().mapToDouble(LoanCollateral :: getCollateralValue).sum();
                    amt=sum;
                    log.info("sum guaranteed amount is :: "+sum);
                    log.info("The sum is :: "+sum);
                }
            }
            return amt;
        }catch (Exception e){
            log.error("Catched exception {} "+e);
            return null;
        }
    }

    public Boolean checkDuplicates(List<LoanGuarantor> l){
        List<String> ls = l.stream().map(LoanGuarantor :: getGuarantorCustomerCode).collect(Collectors.toList());
//        Set<LoanGuarantor> set = new HashSet<LoanGuarantor>(l);
        Set<String> set1 = new HashSet<String>(ls);
        if(set1.size()<ls.size()){
            return true;
        }else {
            return false;
        }
    }


    // TODO: 3/24/2023 Validate total loan security i.e guarantors+ collaterals
    public EntityResponse validateTotalLoanSecurity(Loan loan){
        try {

            log.info("Validating sum security amount");
            EntityResponse response=new EntityResponse<>();
            List<LoanGuarantor> lg=loan.getLoanGuarantors();
            List<LoanCollateral> lc=loan.getLoanCollaterals();

            Double guarantorsAmt=sumAllGuarantorsAmount(lg);
            Double collateralsAmount= sumAllCollateralAmount(lc);

            Double securityAmount =guarantorsAmt+collateralsAmount;
            log.info("sum security amount is :: "+securityAmount);

            if(loan.getPrincipalAmount()<=securityAmount){
                response.setMessage(HttpStatus.OK.getReasonPhrase());
                response.setStatusCode(HttpStatus.OK.value());
            }else {
                response.setMessage("The total loan security amount should be greater than "+loan.getPrincipalAmount());
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            }
            return response;
        }catch (Exception e){
            log.error("Catched exception {} "+e);
            return null;
        }
    }

    public EntityResponse updateGuaranteedAmt(String acid){
        try {
            EntityResponse res= new EntityResponse<>();
            Optional<Account> account= accountRepository.findByAcidAndDeleteFlag(acid, CONSTANTS.NO);
            if(account.isPresent()){
                Account pAc= account.get();
                if(pAc.getAccountType().equals(CONSTANTS.LOAN_ACCOUNT)){
                    Loan loan =pAc.getLoan();

                    Double principalPaid=loanDemandSatisfactionRepo.getTotalPrincipalPaid(acid);

                    Double savingBal = accountRepository.getSavingNonWithAcBal(pAc.getCustomerCode());
                    log.info("savingBal :: "+savingBal);

                    log.info("principalPaid :: "+principalPaid);
                    Double initialPrincipal =loan.getPrincipalAmount();
                    log.info("initialPrincipal :: "+initialPrincipal);

                    Double sumInitialGuaranteedAmt=loanRepository.getSumInitialGuaranteed(acid);

                    log.info("sumInitialGuaranteedAmt :: "+sumInitialGuaranteedAmt);

                    Double now =initialPrincipal-principalPaid-savingBal;
                    log.info(now+" :: now");

                    Double initial =sumInitialGuaranteedAmt;
                    Double diff=initial-now;
                    log.info(diff+" :: diff");

                    log.info("principalPaid new:: "+principalPaid);

//                    Double diff = initialPrincipal-principalPaid;
                    Double diffPercent= ((diff/initial)*100);


                    //update loan guarantors current amount
                    List<LoanGuarantor> lgs= loan.getLoanGuarantors();
                    if(lgs.size()>0){
                        final Integer[] i = {0};
                        lgs.forEach(lg->{
                            i[0] = i[0] +1;
                            log.info("count "+ i[0]);
                            Double guaranteedAmt =lg.getInitialAmt();
                            log.info("guaranteed amt :: "+guaranteedAmt);
                            Double newAmt= guaranteedAmt-(guaranteedAmt*(diffPercent/100));
                            newAmt=roundOff(newAmt);
                            //update new amt
                            loanGuarantorRepository.updateGuaranteedAmt(newAmt,lg.getId());
                            log.info("newAmt "+ newAmt);
                        });
                    }

                    res.setMessage(" Ok "+acid);
                    res.setEntity(diffPercent);
                    res.setStatusCode(HttpStatus.OK.value());
//                    }

                }else {
                    res.setMessage(" Account not a loan account "+acid);
                    res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                }
            }else {
                res.setMessage("Account not found "+acid);
                res.setStatusCode(HttpStatus.NOT_FOUND.value());
            }
            return res;
        }catch (Exception e){
            log.error("Catched exception {} "+e);
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
}
