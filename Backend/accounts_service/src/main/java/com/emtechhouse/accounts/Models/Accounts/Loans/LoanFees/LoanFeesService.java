package com.emtechhouse.accounts.Models.Accounts.Loans.LoanFees;

import com.emtechhouse.accounts.Models.Accounts.Loans.Loan;
import com.emtechhouse.accounts.Models.Accounts.Validators.ValidatorService;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LoanFeesService {
    @Autowired
    private LoanFeesRepo loanFeesRepo;
    @Autowired
    private ValidatorService validatorsService;

    public void deleteLoanFees(List<LoanFees> loanFees){
        try{
            for(Integer i=0;i<loanFees.size();i++){
                LoanFees loanFee= loanFees.get(i);
                loanFee.setLoan(null);
                log.info("deleted loan fee");
//                loanFeesRepo.delete(loanFee);
                loanFeesRepo.deleteById(loanFee.getId());
            }
        }catch (Exception e){
            log.info("Catched Error {} " + e);
        }
    }

    public EntityResponse validateAllFeeCollectionAccounts(List<LoanFees> loanFees){
        try {
            log.info("Validating loan fees");
            log.info(loanFees.size()+":: fees size");
            EntityResponse res= new EntityResponse<>();
            res.setStatusCode(HttpStatus.OK.value());
            res.setMessage(HttpStatus.OK.getReasonPhrase());

            if(loanFees !=null){
                if(loanFees.size()>0){
                    List<String> feeCollectionAccounts= loanFees.stream().map(l->l.getChargeCollectionAccount()).collect(Collectors.toList());
                    for(Integer i=0;i<loanFees.size();i++){
                        LoanFees f= loanFees.get(i);
                        if(f.getInitialAmt()>0 || f.getMonthlyAmount()>0){
                            EntityResponse acidValidator =validatorsService.acidValidator(f.getChargeCollectionAccount(), "Fee collection account");
                            if(!acidValidator.getStatusCode().equals(200)){
                                res=acidValidator;
                                break;
                            }else {
                                continue;
                            }
                        }else {
                            res.setMessage("Fee amount should be greater than zero, event id :: "+f.getEventIdCode());
                            res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            break;
                        }

                    }
                }
            }


            return res;
        }catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    // TODO: 3/22/2023 Removing loan fee
    public EntityResponse removeLoanFee(Long id){
        try {
            EntityResponse res= new EntityResponse<>();
            Optional<LoanFees> l= loanFeesRepo.findById(id);
            if(l.isPresent()){
                LoanFees loanFee= l.get();
                Loan loan =loanFee.getLoan();
                if(loan.getLoanStatus().equals(CONSTANTS.NEW)){

                    loanFeesRepo.deleteById(loanFee.getId());
                    res.setMessage("Fee removed successfully");
                    res.setStatusCode(HttpStatus.OK.value());
                }else {
                    res.setMessage("Failed ! Seems like loan has been disbursed");
                    res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                }
            }else {
                res.setMessage("Could not find fee with the provided id");
                res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            }
            return res;
        }catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    // TODO: 3/22/2023 verify there are no duplicate
    public Boolean checkDuplicates(List<LoanFees> l){
        List<String> ls = l.stream().map(LoanFees :: getEventIdCode).collect(Collectors.toList());
//        Set<LoanGuarantor> set = new HashSet<LoanGuarantor>(l);
        Set<String> set1 = new HashSet<String>(ls);
        if(set1.size()<ls.size()){
            return true;
        }else {
            return false;
        }
    }
}
