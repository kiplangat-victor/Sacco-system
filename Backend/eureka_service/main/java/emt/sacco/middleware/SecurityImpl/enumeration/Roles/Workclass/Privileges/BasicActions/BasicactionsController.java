package emt.sacco.middleware.SecurityImpl.enumeration.Roles.Workclass.Privileges.BasicActions;

import emt.sacco.middleware.Utils.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            List<SBasicactions> basicactions = basicActionRequest.getBasicactions();
            return new ResponseEntity<>(basicactionsService.addBasicactions(basicactions, privilegeId, workclassId), HttpStatus.OK);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllAccessgroups(@RequestParam Long privilegeId, @RequestParam Long workclassId) {
        try {
            return new ResponseEntity<>(basicactionsService.findAllBasicactionss(privilegeId,workclassId), HttpStatus.OK);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

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
            List<SBasicactions> basicactions = basicActionRequest.getBasicactions();
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