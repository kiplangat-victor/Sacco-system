package com.emtechhouse.accounts.TransactionService.EODProcess.PushToDB;


import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext.entityId;

@RestController
@Slf4j
@RequestMapping("api/v1/eodStatus")
public class EODStatusReportController {
    @Autowired
    private EODStatusReportService service;

    EntityResponse response = new EntityResponse<>();

    @GetMapping("/pastEodData")
    public ResponseEntity<?> fetchPastEOData()
    {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response,HttpStatus.OK);
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return new ResponseEntity<>(response,HttpStatus.OK);
                } else {
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(service.findByEntityId(EntityRequestContext.getCurrentEntityId()));
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            response.setMessage("ERROR WHEN FETCHING PAST EOD DATA :: "+e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            response.setEntity("");
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
    }
//    @PostMapping("/save")
//    public ResponseEntity<?> saveEndOfDayBalances(@RequestParam("entityId") String entityId) {
//        service.saveEndOfDayBalances(entityId);
//        // Return ResponseEntity with appropriate response if needed
//        return ResponseEntity.ok().build();
//    }
    @GetMapping("/currentEodData")
    public ResponseEntity<?> fetchPastEOData(@RequestParam("systemDate") String systemDate)
    {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response,HttpStatus.OK);
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return new ResponseEntity<>(response,HttpStatus.OK);
                } else {
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(service.findByEntityIdAndSysDate(EntityRequestContext.getCurrentEntityId(),systemDate));
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }

        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            response.setMessage("ERROR WHEN FETCHING PAST EOD DATA :: "+e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            response.setEntity("");
            return new ResponseEntity<>(response,HttpStatus.OK);
        }

    }
}
