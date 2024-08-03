package com.emtechhouse.accounts.TransactionService.EODProcess.performEOD;

import com.emtechhouse.accounts.TransactionService.EODProcess.performEOD.EODResponses.EODResponse;
import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("api/v1/eod")
public class EODProcessController {

    @Autowired
    private EODProcessService eop;

    EntityResponse response = new EntityResponse<>();

    @PostMapping("/initiateEOD")
    public ResponseEntity<?> intitateEOD()
    {
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
                EODResponse res =  eop.performEOD();
                response.setMessage("SUCCESS");
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(res);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }
    }
}
