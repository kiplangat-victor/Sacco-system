package com.emtechhouse.accounts.Resources;


import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.LoanDemandRepository;
import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/repayments/statistics/")
@Slf4j
public class LoanRepaymentsStatisticsController {


    @Autowired
    private LoanDemandRepository loanDemandRepo;

    @GetMapping("loan-repayments-statictics-years")
    public ResponseEntity<?> fetchLoanRepaymentsYearsStatistics(){
        try {

            if (UserRequestContext.getCurrentUser().isEmpty()){
                EntityResponse response = new EntityResponse();
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()){
                    EntityResponse response = new EntityResponse<>();
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }else {
                    return new ResponseEntity<>(loanDemandRepo.findRepaymentYears(), HttpStatus.OK);
                }
            }

        }catch (Exception exc){
            log.info(exc.getLocalizedMessage());
            return null;
        }
    }

    @GetMapping("loan-repayments-statictics-year-wise")
    public ResponseEntity<?> fetchLoanRepaymentsYearWiseStatistics(){
        try {

            if (UserRequestContext.getCurrentUser().isEmpty()){
                EntityResponse response = new EntityResponse();
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()){
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }else {
                    return new ResponseEntity<>(loanDemandRepo.findRepaymentYearWise(), HttpStatus.OK);
                }
            }

        }catch (Exception exc){
            log.info(exc.getLocalizedMessage());
            return null;
        }
    }

    @GetMapping("loan-repayments-statictics-month-wise")
    public ResponseEntity<?> fetchLoanRepaymentsMonthWiseStatistics(@RequestParam String year){
        try {

            if (UserRequestContext.getCurrentUser().isEmpty()){
                EntityResponse response = new EntityResponse();
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()){
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }else {
                    return new ResponseEntity<>(loanDemandRepo.findRepaymentMonthWise(year), HttpStatus.OK);
                }
            }

        }catch (Exception exc){
            log.info(exc.getLocalizedMessage());
            return null;
        }
    }

    @GetMapping("loan-repayments-statictics-day-wise")
    public ResponseEntity<?> fetchLoanRepaymentsDayWiseStatistics(@RequestParam String year, @RequestParam String monthname){
        try {

            if (UserRequestContext.getCurrentUser().isEmpty()){
                EntityResponse response = new EntityResponse();
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()){
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }else {
                    return new ResponseEntity<>(loanDemandRepo.findRepaymentDayWise(year, monthname), HttpStatus.OK);
                }
            }

        }catch (Exception exc){
            log.info(exc.getLocalizedMessage());
            return null;
        }
    }


}
