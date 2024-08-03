package com.emtechhouse.usersservice.Roles.Workclass.Privileges.BasicActions;

import com.emtechhouse.usersservice.utils.EntityResponse;
import com.emtechhouse.usersservice.utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.usersservice.utils.HttpInterceptor.UserRequestContext;
import com.google.gson.JsonArray;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;



@RestController
@RequestMapping("/api/v1/auth/basicactions")
@Slf4j
public class BasicactionsController {
    private final BasicactionsService basicactionsService;

    public BasicactionsController(BasicactionsService basicactionsService) {
        this.basicactionsService = basicactionsService;
    }
    @PostMapping("/add")
    public ResponseEntity<?> addAccessgroup(@RequestBody BasicActionRequest basicActionRequest) {
        try {
            EntityResponse response = new EntityResponse();
            Long privilegeId = basicActionRequest.getPrivilegeId();
            Long workclassId = basicActionRequest.getWorkclassId();
            List<Basicactions> basicactions = basicActionRequest.getBasicactions();
            return new ResponseEntity<>(basicactionsService.addBasicactions(basicactions, privilegeId, workclassId), HttpStatus.OK);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    //check inquire
    @GetMapping("/all") //inquire
    public ResponseEntity<?> getAllAccessgroups(@RequestParam Long privilegeId, @RequestParam Long workclassId) {
        try {
            return new ResponseEntity<>(basicactionsService.findAllBasicactionss(privilegeId,workclassId), HttpStatus.OK);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

//    //added
//    //http://localhost:9002/api/v1/auth/basicactions/all?privilegeId=1622&workclassId=124
//    @GetMapping("/allRelated")
//    public ResponseEntity<?> getAllRelatedAccessgroups(@RequestParam Long privilegeId, @RequestParam Long workclassId) {
//        try {
//            return new ResponseEntity<>(basicactionsService.findAllRelatedBasicactions(privilegeId,workclassId), HttpStatus.OK);
////            return new ResponseEntity<>(basicactionsService.findAllBasicactionss(privilegeId,workclassId), HttpStatus.OK);
//        } catch (Exception e) {
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }

    @GetMapping("/find/{id}")
    public ResponseEntity<?> getAccessgroupById(@PathVariable("id") Long id) {
        try {
            return new ResponseEntity<>(basicactionsService.findById(id), HttpStatus.OK);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("/modify")
    public ResponseEntity<?> updateAccessgroup(@RequestBody BasicActionRequest basicActionRequest) {
        try {
            Long privilegeId = basicActionRequest.getPrivilegeId();
            Long workclassId = basicActionRequest.getWorkclassId();
            Long id = basicActionRequest.getId();
            List<Basicactions> basicactions = basicActionRequest.getBasicactions();
            return new ResponseEntity<>(basicactionsService.updateBasicactions(id, basicactions, privilegeId, workclassId), HttpStatus.OK);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    @PutMapping("/verify/{id}")
    public ResponseEntity<?> verify(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(basicactionsService.verify(id), HttpStatus.OK);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteAccessgroup(@RequestParam Long privilegeId, @RequestParam Long workclassId) {
        try {
            return new ResponseEntity<>(basicactionsService.deletePermanently(privilegeId,workclassId), HttpStatus.OK);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
}