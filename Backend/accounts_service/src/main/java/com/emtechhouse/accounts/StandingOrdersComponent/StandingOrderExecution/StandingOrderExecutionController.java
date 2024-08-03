package com.emtechhouse.accounts.StandingOrdersComponent.StandingOrderExecution;

import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@Api(value = "/Standing Orders API", tags = "Standing Order API")
@RequestMapping("/api/v1/sto-execution")
public class StandingOrderExecutionController {
    @Autowired
    StandingOrderExecutionRepo standingOrderExecutionRepo;

    @GetMapping("/all")
    public EntityResponse findAll(){
                try {
        EntityResponse response = new EntityResponse();
        if (standingOrderExecutionRepo.findAll().size()==0 || standingOrderExecutionRepo.findAll().isEmpty()){
            response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
        }else{
            response.setMessage(HttpStatus.FOUND.getReasonPhrase());
            response.setStatusCode(HttpStatus.FOUND.value());
            response.setEntity(standingOrderExecutionRepo.findAll());
        }
        return response;
    } catch (Exception e) {
        log.info("Catched Error {} " + e);
        return null;
    }
    }
}
