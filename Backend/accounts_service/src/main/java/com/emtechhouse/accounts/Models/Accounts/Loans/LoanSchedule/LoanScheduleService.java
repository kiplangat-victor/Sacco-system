package com.emtechhouse.accounts.Models.Accounts.Loans.LoanSchedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class LoanScheduleService {
    @Autowired
    private LoanScheduleRepo loanScheduleRepo;

    public List<LoanSchedule> all(){
        try{
            List<LoanSchedule> loanScheduleList= loanScheduleRepo.findAll();
            if(loanScheduleList.size()>0){
                return loanScheduleList;
            }else {
                return null;
            }
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public LoanSchedule updateLoanSchedule(LoanSchedule loanSchedule){
        try{
            Long id= loanSchedule.getId();
            Optional<LoanSchedule> loanSchedule1=loanScheduleRepo.findById(id);
            if(loanSchedule1.isPresent()){
                LoanSchedule sLoanSchedule=loanScheduleRepo.save(loanSchedule);
                return  sLoanSchedule;
            }else {
                return null;
            }
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    public void deleteLoanSchedules(List<LoanSchedule> loanSchedules){
        try{
            log.info("Loan schedule size == "+loanSchedules.size() );
            for(Integer i=0;i<loanSchedules.size();i++){
                log.info("deleting loan schedule "+i);

                LoanSchedule loanSchedule= loanSchedules.get(i);
                loanSchedule.setLoan(null);
                log.info("deleted loan schedule");
                loanScheduleRepo.deleteById(loanSchedule.getId());
            }
        }catch (Exception e){
            log.info("Catched Error {} " + e);
        }
    }

}
