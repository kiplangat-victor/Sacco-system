package com.emtechhouse.accounts.Models.Accounts.TermDeposit.Accrual;
import com.emtechhouse.accounts.Models.Accounts.Account;
import com.emtechhouse.accounts.Models.Accounts.AccountRepository;
import com.emtechhouse.accounts.Models.Accounts.TermDeposit.TermDeposit;
import com.emtechhouse.accounts.Models.Accounts.TermDeposit.TermDepositRepo;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.DatesCalculator;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TermDepositAccrualService {

    @Autowired
    private DatesCalculator datesCalculator;
    @Autowired
    private TermDepositAccrualRepo termDepositAccrualRepo;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TermDepositRepo termDepositRepo;


    public EntityResponse accrue(String acid){
        try {
            log.info("accrual has started");
            EntityResponse res = new EntityResponse();
            Optional<Account> account= accountRepository.findByAcidAndDeleteFlag(acid, CONSTANTS.NO);
            if(account.isPresent()){
                log.info("account found");
                TermDeposit termDeposit= account.get().getTermDeposit();
                return accrueInterest(termDeposit);
            }else {
                res.setMessage("Account not found");
                res.setStatusCode(HttpStatus.NOT_FOUND.value());
            }
            return res;
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public List<EntityResponse> accrualForAllTda(){
        try{
            log.info("accrual for all accounts in progress");
            List<EntityResponse> responses= new ArrayList<>();
            List<TermDeposit> activeTdas =termDepositRepo.getAccountsToAccrue();
            if(activeTdas.size()>0){
                activeTdas.forEach(activeTda->{
                    EntityResponse entityResponse= accrueInterest(activeTda);
                    responses.add(entityResponse);
                });
            }else {
                EntityResponse response = new EntityResponse();
                response.setMessage("THERE ARE NO ACCounts LOANS");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                responses.add(response);
            }
            return responses;
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    public EntityResponse accrueInterest(TermDeposit termDeposit){
        try {
            log.info("entered accrual function");
            EntityResponse res = new EntityResponse();

            if(termDeposit.getTermDepositStatus().equals(CONSTANTS.APPROVED)){
                DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                Date today = new Date();
                Date now = formatter.parse(formatter.format(today));

                Date valueDate=formatter.parse(formatter.format(termDeposit.getValueDate()));
                Date maturityDate= formatter.parse(formatter.format(termDeposit.getMaturityDate()));
                Date accrualLastDate= formatter.parse(formatter.format(termDeposit.getAccrualLastDate()));
                Double sumAccrued= termDeposit.getSumAccruedAmount();

                Date accrualStartingDate;
                Long affectedDays=0L;

                String acid=termDeposit.getAccount().getAcid();

                //todo: calculate daily int
                Double intAmt= termDeposit.getInterestAmount();
                Long daysDiff= termDeposit.getPeriodInDays();
                Double dailyInt= (intAmt/daysDiff);
                log.info("daily int "+dailyInt);
                //todo: check last accrual date
                //todo: get today's date
                //todo: check todays date > maturity date
                //todo: if not
                if(accrualLastDate.compareTo(now)!=0 || (accrualLastDate.equals(valueDate)) && sumAccrued<1){
                    if(accrualLastDate.before(maturityDate)){
                        log.info("accrual date okay");
                        if(accrualLastDate.equals(valueDate)){
                            accrualStartingDate=accrualLastDate;
                        }else {
                            accrualStartingDate=datesCalculator.addDate(accrualLastDate,1,"DAYS");
                        }

                        if(maturityDate.compareTo(now)<0){
                            log.info("now, "+now.toString());
                            log.info("Maturity date, "+maturityDate.toString());
                            affectedDays=datesCalculator.getDaysDifference(accrualStartingDate,maturityDate)+1;
                            now=maturityDate;
                        }else {
                            affectedDays=datesCalculator.getDaysDifference(accrualStartingDate,now)+1;
                        }

                        log.info("affected days "+affectedDays);

                        Double interestToAccrue= (dailyInt*affectedDays);

                        TermDepositAccrual tda= new TermDepositAccrual();
                        tda.setAmountAccrued(interestToAccrue);
                        tda.setAcid(acid);
                        tda.setFromDate(accrualStartingDate);
                        tda.setToDate(now);
                        tda.setAccrualFrequency("F");
                        tda.setTermDeposit(termDeposit);
                        tda.setInterestType(CONSTANTS.NORMAL_INTEREST);
                        tda.setAccruedOn(today);
                        TermDepositAccrual sTda=termDepositAccrualRepo.save(tda);

                        //todo: update accrual last date
                        termDepositRepo.updateLastAccrualDate(now, termDeposit.getId());
                        //todo: update sum accrued amount
                        termDepositRepo.updateTermDepositSumAccruedAmount(interestToAccrue, termDeposit.getId());


                        res.setMessage(HttpStatus.OK.getReasonPhrase());
                        res.setStatusCode(HttpStatus.OK.value());
                        res.setEntity(sTda);

                        return res;
                    }else {
                        log.info("accrual date not okay");
                        EntityResponse response = new EntityResponse();
                        response.setMessage("ACCRUED UP TO MATURITY DATE" + "accrual last date :: "+accrualLastDate.toString());
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        return response;
                    }

                }else{
                    log.info("accrual date not okay");
                    EntityResponse response = new EntityResponse();
                    response.setMessage("ACCRUED UP TO DATE" + "accrual last date:==>"+accrualLastDate.toString()+ " now: "+LocalDate.now().toString());
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    return response;
                }
            }else {
                res.setMessage("Seems like account is not verified");
                res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return res;
            }
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }
}
