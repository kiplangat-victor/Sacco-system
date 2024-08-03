package com.emtechhouse.CustomerService.CustomerStatistics;

import com.emtechhouse.CustomerService.RetailMember.RetailcustomerRepo;
import com.emtechhouse.Utils.EntityResponse;
import com.emtechhouse.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.Utils.HttpInterceptor.UserRequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
//@CrossOrigin
@RequestMapping("api/v1/customer/retail/statistics")
public class CustomerStatisticsController {

    @Autowired
    private RetailcustomerRepo retailcustomerRepo;

    @GetMapping("customer-onboarding-statictics-years")
    public ResponseEntity<?> fetchCustomerOnboardingYearsStatistics(){
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
                    return new ResponseEntity<>(retailcustomerRepo.findOnboardingYears(), HttpStatus.OK);
                }
            }

        }catch (Exception exc){
            log.info(exc.getLocalizedMessage());
            return null;
        }
    }

    @GetMapping("customer-onboarding-statictics-year-wise")
    public ResponseEntity<?> fetchCustomerOnboardingYearWiseStatistics(){
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
                    return new ResponseEntity<>(retailcustomerRepo.findOnboardedCustomersYearWise(), HttpStatus.OK);
                }
            }

        }catch (Exception exc){
            log.info(exc.getLocalizedMessage());
            return null;
        }
    }

    @GetMapping("customer-onboarding-statictics-month-wise")
    public ResponseEntity<?> fetchCustomerOnboardingMonthWiseStatistics(@RequestParam String year){
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
                    return new ResponseEntity<>(retailcustomerRepo.findOnboardedCustomersMonthWise(year), HttpStatus.OK);
                }
            }

        }catch (Exception exc){
            log.info(exc.getLocalizedMessage());
            return null;
        }
    }

    @GetMapping("customer-onboarding-statictics-day-wise")
    public ResponseEntity<?> fetchCustomerOnboardingDayWiseStatistics(@RequestParam String year, @RequestParam String monthname){
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
                    return new ResponseEntity<>(retailcustomerRepo.findOnboardedCustomersDayWise(year, monthname), HttpStatus.OK);
                }
            }

        }catch (Exception exc){
            log.info(exc.getLocalizedMessage());
            return null;
        }
    }


}
