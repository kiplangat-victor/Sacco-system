package com.emtechhouse.accounts.StandingOrdersComponent;

import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@Api(value = "/Standing Orders API", tags = "Standing Order API")
@RequestMapping("/api/v1/standingorder")
public class StandingorderheaderController {
    @Autowired
    private  StandingorderheaderRepo standingorderheaderRepo;
    @Autowired
    private  StandingorderheaderService standingorderheaderService;

    @Autowired
    private StandingorderExecutor standingorderExecutor;

    public StandingorderheaderController() {
    }
    @PostMapping("/add")
    public EntityResponse addStandingorderheader(@RequestBody Standingorderheader standingorderheader) {
        String username = UserRequestContext.getCurrentUser();
        String entityid = EntityRequestContext.getCurrentEntityId();
        standingorderheader.setPostedBy(username);
        standingorderheader.setEntityId(entityid);
        standingorderheader.setPostedTime(new Date());
        return standingorderheaderService.add(standingorderheader);
    }
    @GetMapping("/all")
    public EntityResponse  findAll() {
        return standingorderheaderService.findAll();
    }
    @GetMapping("/find/{id}")
    public EntityResponse findById(@PathVariable("id") Long id) {
        return standingorderheaderService.findById(id);
    }

    @PutMapping("/update")
    public EntityResponse updateStandingorderheader(@RequestBody Standingorderheader standingorderheader) {
        return standingorderheaderService.update(standingorderheader);
    }
    @DeleteMapping("/delete/{id}")
    public EntityResponse<?> deleteStandingorderheader(@PathVariable("id") Long id) {
        return  standingorderheaderService.delete(id);
    }

    @PutMapping("/verify/{id}")
    public EntityResponse<?> verifyStandingorderheader(@PathVariable("id") Long id) {

        return standingorderheaderService.verify(id);
    }

    @PostMapping("/execute/{id}")
    public ResponseEntity<?> executeStandingorderheader(@PathVariable("id") Long id) {
        standingorderExecutor.executeNew(id);
        return new ResponseEntity<>("Done", HttpStatus.OK);
    }
    @PostMapping("/execute/by/code/{code}")
    public ResponseEntity<?> executeStandingorderheader(@PathVariable("code") String code) {
        standingorderExecutor.executeNew(code);
        return new ResponseEntity<>("Done", HttpStatus.OK);
    }
    @GetMapping("/all/for/approvals")
    public EntityResponse<?>  findAllForApproval() {
        return standingorderheaderService.findAllForApproval();
    }
}