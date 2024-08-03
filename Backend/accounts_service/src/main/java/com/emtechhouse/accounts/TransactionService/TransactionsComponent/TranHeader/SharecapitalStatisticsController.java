package com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader;



import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans.PartTranRepository;
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

@Slf4j
@RequestMapping("sharecapital/amount")
@RestController
public class SharecapitalStatisticsController {

    @Autowired
    private PartTranRepository partTranRepo;

    @GetMapping("share-capital-statistics-years")
    public ResponseEntity<?> fetchShareCapitalYearsStatistics(){
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
                    return new ResponseEntity<>(partTranRepo.findhareCapitalYears(), HttpStatus.OK);
                }
            }

        }catch (Exception exc){
            log.info(exc.getLocalizedMessage());
            return null;
        }
    }

    @GetMapping("share-capital-statistics-year-wise")
    public ResponseEntity<?> fetchShareCapitalYearWiseStatistics(@RequestParam String productCode){
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
                    return new ResponseEntity<>(partTranRepo.findShareCapitalYearWise(productCode), HttpStatus.OK);
                }
            }

        }catch (Exception exc){
            log.info(exc.getLocalizedMessage());
            return null;
        }
    }

    @GetMapping("share-capital-statistics-month-wise")
    public ResponseEntity<?> fetchShareCapitalMonthWiseStatistics(@RequestParam String year, @RequestParam String productCode){
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
                    return new ResponseEntity<>(partTranRepo.findShareCapitalMonthWise(productCode, year), HttpStatus.OK);
                }
            }

        }catch (Exception exc){
            log.info(exc.getLocalizedMessage());
            return null;
        }
    }

    @GetMapping("share-capital-statistics-day-wise")
    public ResponseEntity<?> fetchShareCapitalDayWiseStatistics(@RequestParam String year, @RequestParam String monthname, @RequestParam String productCode){
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
                    return new ResponseEntity<>(partTranRepo.findShareCapitalDayWise(productCode, year, monthname), HttpStatus.OK);
                }
            }

        }catch (Exception exc){
            log.info(exc.getLocalizedMessage());
            return null;
        }
    }

}
