package emt.sacco.middleware.AgencyBanking.AgencyOnboarding.Controller;

import emt.sacco.middleware.AgencyBanking.AgencyOnboarding.Model.AgencyPayload;
import emt.sacco.middleware.AgencyBanking.AgencyOnboarding.Service.AgentService;
import emt.sacco.middleware.Utils.EntityRequestContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("api/v1")
public class AgentController {
    @Autowired
    private AgentService service;

    @PostMapping(value = "/add",produces = "application/json")
    public ResponseEntity<?> addAgent( @RequestBody AgencyPayload agencyPayload){
        String entityId= EntityRequestContext.getCurrentEntityId();
//        String entityId= EntityRequestContext.getCurrentEntityId();
        try{
            return service.addAgent(agencyPayload,entityId);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }      
    }
    @GetMapping(value = "/getAgency",produces = "application/json")
    public ResponseEntity<?> getAllAgents(){
        String entityId= EntityRequestContext.getCurrentEntityId();
        try{
            return service.getAllAgents(entityId);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping(value = "/ModifyAgency",produces = "application/json")
    public ResponseEntity<?> modifyAgency(@RequestParam String entityId,  AgencyPayload agencyPayload){
        try{
            return service.modifyAgency(agencyPayload);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping(value = "/DeleteAgency/{Id}",produces = "application/json")
    public ResponseEntity<?> deleteAgent(@RequestParam String entityId, Long Id){
        try{
            return  service.deleteAgent(entityId,Id);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

@PutMapping(value = "/verify/{Id}",produces = "application/json")
    public ResponseEntity<?> verify(@RequestParam String entityId , Long Id){
        try{
            return service.verify(Id);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        }
}

    @GetMapping("/ListOfUnverifiedAgency/all")
    public ResponseEntity<?> getAllUnverifiedAgency(@RequestParam String entityId) {
    try{
        return  service.getAllUnverifiedAgency(entityId);

    }catch(Exception e){
        log.info("Catch Error {}"+ e);
                return null;
        }

}
    @GetMapping("/allAgentsStatus")
    public ResponseEntity<?> getAllAgentsStatus(@RequestParam(value = "status") String status) {
        try {
            return service.getAllUnverifiedAgency(status);

        } catch (Exception e) {
            log.info("Catch Error {}" + e);
            return null;
        }

    }
}


