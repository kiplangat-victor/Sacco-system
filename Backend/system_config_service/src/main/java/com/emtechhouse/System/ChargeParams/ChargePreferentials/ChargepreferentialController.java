package com.emtechhouse.System.ChargeParams.ChargePreferentials;

import com.emtechhouse.System.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.System.Utils.Responses.MessageResponse;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

//@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@Slf4j
@Api(value = "/Charge Preferential API", tags = "Charge Preferential API")
@RequestMapping("/api/v1/system/configurations/charge/params/preferentials/")
public class ChargepreferentialController {
    @Autowired
    private ChargepreferentialRepo chargepreferentialRepo;
    @Autowired
    private ChargepreferentialService chargepreferentialService;

    @GetMapping("/check/charge/preferential/")
    public ResponseEntity<?>  checkActualChargePreferential(@RequestParam String function_type, @RequestParam String chrg_preferential, @RequestParam String  event_type, @RequestParam String event_id, @RequestParam String account_id, @RequestParam String cif_id, @RequestParam String organization_id){
        try{
//            check the intentions
            if (function_type.matches("A-Add")){
                //            Customer Level
                if (chrg_preferential.matches("Customer Level")){
                    Optional<Chargepreferential> chargepreferential = chargepreferentialRepo.checkCustomerActualChargePreferential(chrg_preferential, cif_id);
                    if (chargepreferential.isPresent()){
                        System.out.println("got called for Customer");
                        return ResponseEntity
                                .badRequest()
                                .body(new MessageResponse("Error: Data already exists! You may modify"));
                    }else {
                        return new ResponseEntity<>(chargepreferential, HttpStatus.OK);
                    }
                }
//            Account Level
                else if (chrg_preferential.matches("Account Level")){
                    System.out.println("Called here");
                    Optional<Chargepreferential> chargepreferential = chargepreferentialRepo.checkAccountActualChargePreferential(chrg_preferential, account_id);
                    if (chargepreferential.isPresent()){
                        return ResponseEntity
                                .badRequest()
                                .body(new MessageResponse("Error: Data already exists! You may modify"));
                    }else {
                        return new ResponseEntity<>(chargepreferential, HttpStatus.OK);
                    }
                }
//            Charge Level
                else if (chrg_preferential.matches("Charge Level")){
                    Optional<Chargepreferential> chargepreferential = chargepreferentialRepo.checkChargeActualChargePreferential(chrg_preferential, event_type, event_id);
                    if (chargepreferential.isPresent()){
                        return ResponseEntity
                                .badRequest()
                                .body(new MessageResponse("Error: Data already exists! You may modify"));
                    }else {
                        return new ResponseEntity<>(chargepreferential, HttpStatus.OK);
                    }
                }
//            Contract Level
                else if (chrg_preferential.matches("Contract Level")){
                    Optional<Chargepreferential> chargepreferential = chargepreferentialRepo.checkContractActualChargePreferential(chrg_preferential, organization_id);
                    if (chargepreferential.isPresent()){
                        return ResponseEntity
                                .badRequest()
                                .body(new MessageResponse("Error: Data already exists! You may modify"));
                    }else {
                        return new ResponseEntity<>(chargepreferential, HttpStatus.OK);
                    }
                }else {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }

            }else {
                //            Customer Level
                if (chrg_preferential.matches("Customer Level")){
                    System.out.println("got called for Customer outside");

                    Optional<Chargepreferential> chargepreferential = chargepreferentialRepo.checkCustomerActualChargePreferential(chrg_preferential, cif_id);
                    if (!chargepreferential.isPresent()){
                        System.out.println("got called for Customer");
                        return ResponseEntity
                                .badRequest()
                                .body(new MessageResponse("Error: Data doesn't exists! You may add"));
                    }else {
                        return new ResponseEntity<>(chargepreferential, HttpStatus.OK);
                    }
                }
//            Account Level
                else if (chrg_preferential.matches("Account Level")){
                    System.out.println("Called here");
                    Optional<Chargepreferential> chargepreferential = chargepreferentialRepo.checkAccountActualChargePreferential(chrg_preferential, account_id);
                    if (!chargepreferential.isPresent()){
                        return ResponseEntity
                                .badRequest()
                                .body(new MessageResponse("Error: Data doesn't exists! You may add"));
                    }else {
                        return new ResponseEntity<>(chargepreferential, HttpStatus.OK);
                    }
                }
//            Charge Level
                else if (chrg_preferential.matches("Charge Level")){
                    Optional<Chargepreferential> chargepreferential = chargepreferentialRepo.checkChargeActualChargePreferential(chrg_preferential, event_type, event_id);
                    if (!chargepreferential.isPresent()){
                        return ResponseEntity
                                .badRequest()
                                .body(new MessageResponse("Error: Data doestn't exists! You may add"));
                    }else {
                        return new ResponseEntity<>(chargepreferential, HttpStatus.OK);
                    }
                }
//            Contract Level
                else if (chrg_preferential.matches("Contract Level")){
                    System.out.println("***************got called **********************");
//                    chrg_preferential, event_type, event_id, organization_id;
                    System.out.println(chrg_preferential);
                    System.out.println(event_type);
                    System.out.println(event_id);
                    System.out.println(organization_id);
                    Optional<Chargepreferential> chargepreferential = chargepreferentialRepo.checkContractActualChargePreferential(chrg_preferential, organization_id);
                    if (!chargepreferential.isPresent()){
                        return ResponseEntity
                                .badRequest()
                                .body(new MessageResponse("Error: Data doesn't exists! You may add"));
                    }else {
                        System.out.println("***************data responds ***************");
                        System.out.println(chargepreferential);
                        return new ResponseEntity<>(chargepreferential, HttpStatus.OK);
                    }
                }else {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            }
        } catch (Exception e) {
            log.info("Error {} "+e);
            return null;
        }
    }
    @PostMapping("/add")
    public ResponseEntity<?> addChargepreferential(@RequestBody Chargepreferential chargepreferential){
        try{
            if (chargepreferential.getPercentage_val() < 0 || chargepreferential.getPercentage_val() >  100){
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: Percentage out of range:0-100!"));
            }else{
                Chargepreferential newChargepreferential = chargepreferentialService.addChargepreferential(chargepreferential);
                return  new ResponseEntity<>(newChargepreferential, HttpStatus.CREATED);
            }
        } catch (Exception e) {
            log.info("Error {} "+e);
            return null;
        }
    }
    @GetMapping("/all")
    public ResponseEntity<List<Chargepreferential>> getAllChargepreferentials () {
        try{
            List<Chargepreferential> chargepreferentials = chargepreferentialRepo.findAllByEntityIdAndDeletedFlagOrderByIdDesc(EntityRequestContext.getCurrentEntityId(), 'N');;
            return  new ResponseEntity<>(chargepreferentials, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Error {} "+e);
            return null;
        }
    }
    @GetMapping("/find/actual/charge/preferential/")
    public ResponseEntity<Chargepreferential>  findActualChargePreferential(@RequestParam String chrg_preferential, @RequestParam String  event_type, @RequestParam String event_id, @RequestParam String account_id, @RequestParam String cif, @RequestParam String organization_id){
        try{
            Chargepreferential chargepreferential = chargepreferentialRepo. findActualChargePreferential(chrg_preferential,event_type,event_id,account_id, cif,organization_id);
            return new ResponseEntity<>(chargepreferential, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Error {} "+e);
            return null;
        }
    }
//    @RequestParam String int_tbl_code



    @GetMapping("/find/{id}")
    public ResponseEntity<Chargepreferential> getChargepreferentialById (@PathVariable("id") Long id){
        try{
            Chargepreferential chargepreferential = chargepreferentialService.findChargepreferentialById(id);
            return new ResponseEntity<>(chargepreferential, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Error {} "+e);
            return null;
        }
    }
//    @GetMapping("/find/by/event_id/{event_id}")
//    public ResponseEntity<Chargepreferential> findByEventId(@PathVariable("event_id") String event_id){
//        try{
//            Chargepreferential chargepreferential = chargepreferentialRepo.findByEventId(event_id);
//            return new ResponseEntity<>(chargepreferential, HttpStatus.OK);
//        } catch (Exception e) {
//            log.info("Error {} "+e);
//            return null;
//        }
//    }
    @PutMapping("/update/{id}")
    public ResponseEntity<Chargepreferential> updateChargepreferential(@PathVariable("id") long id, @RequestBody Chargepreferential chargepreferential){
        try{
            Optional<Chargepreferential> chargepreferentialData = chargepreferentialRepo.findById(id);
            if (chargepreferentialData.isPresent()) {
                Chargepreferential _chargepreferential = chargepreferentialData.get();
                _chargepreferential.setStart_date(chargepreferential.getStart_date());
                _chargepreferential.setEnd_date(chargepreferential.getEnd_date());
                _chargepreferential.setChrg_derivation(chargepreferential.getChrg_derivation());
                _chargepreferential.setPercentage_val(chargepreferential.getPercentage_val());
                _chargepreferential.setFixed_amt(chargepreferential.getFixed_amt());
                _chargepreferential.setMin_amt_ccy(chargepreferential.getMin_amt_ccy());
                _chargepreferential.setMin_amt(chargepreferential.getMin_amt());
                _chargepreferential.setMax_amt_ccy(chargepreferential.getMax_amt_ccy());
                _chargepreferential.setMax_amt(chargepreferential.getMax_amt());
                _chargepreferential.setVerifiedFlag(chargepreferential.getVerifiedFlag());
                _chargepreferential.setDeletedFlag(chargepreferential.getDeletedFlag());
                _chargepreferential.setModifiedTime(new Date());
                return new ResponseEntity<>(chargepreferentialRepo.save(_chargepreferential), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.info("Error {} "+e);
            return null;
        }
    }
    @DeleteMapping("/deletepermanently/{id}")
    public ResponseEntity<Chargepreferential> deleteChargepreferentialpermanently(@PathVariable("id") Long id){
        try{
            chargepreferentialRepo.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.info("Caught Error {}"+e);
            return null;
        }
    }

    @PutMapping("/delete")
    public ResponseEntity<?> deleteChargepreferential(@RequestParam Long id, @RequestParam String deletedby){
        try {
            Optional<Chargepreferential> chargepreferentialcheck = chargepreferentialRepo.findById(id);
            if (chargepreferentialcheck.isPresent()){
                Chargepreferential chargepreferential = chargepreferentialcheck.get();
                chargepreferential.setDeletedTime(new Date());
                chargepreferential.setDeletedBy(deletedby);
                chargepreferential.setDeletedFlag('Y');

                chargepreferentialRepo.save(chargepreferential);
                return new ResponseEntity<>(new MessageResponse("Charge preferential deleted successfully"), HttpStatus.OK);
            }else {
                return new ResponseEntity<>(new MessageResponse("No Data Found"), HttpStatus.BAD_REQUEST);
            }
        }catch (Exception e){
            return new ResponseEntity<>(new MessageResponse(e.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/verify")
    public ResponseEntity<?> verifyChargepreferential(@RequestParam Long id, @RequestParam String verifiedby){
        try {
            Optional<Chargepreferential> chargepreferentialcheck = chargepreferentialRepo.findById(id);
            if (chargepreferentialcheck.isPresent()){
                Chargepreferential chargepreferential = chargepreferentialcheck.get();
                chargepreferential.setVerifiedTime(new Date());
                chargepreferential.setVerifiedBy(verifiedby);
                chargepreferential.setVerifiedFlag('Y');

                chargepreferentialRepo.save(chargepreferential);
                return new ResponseEntity<>(new MessageResponse("Charge preferential verified successfully"), HttpStatus.OK);
            }else {
                return new ResponseEntity<>(new MessageResponse("No Data Found"), HttpStatus.BAD_REQUEST);
            }
        }catch (Exception e){
            return new ResponseEntity<>(new MessageResponse(e.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/modify")
    public ResponseEntity<?> modifyChargepreferential(@RequestParam Long id, @RequestParam String modifiedby){
        try {
            Optional<Chargepreferential> chargepreferentialcheck = chargepreferentialRepo.findById(id);
            if (chargepreferentialcheck.isPresent()){
                Chargepreferential chargepreferential = chargepreferentialcheck.get();
                chargepreferential.setModifiedTime(new Date());
                chargepreferential.setModifiedBy(modifiedby);
                chargepreferential.setModifiedFlag('Y');

                chargepreferentialRepo.save(chargepreferential);
                return new ResponseEntity<>(new MessageResponse("Charge preferential modified successfully"), HttpStatus.OK);
            }else {
                return new ResponseEntity<>(new MessageResponse("No Data Found"), HttpStatus.BAD_REQUEST);
            }
        }catch (Exception e){
            return new ResponseEntity<>(new MessageResponse(e.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/fetchall/deleted")
    public ResponseEntity<?> fetchalldeletedChargepreferential(){
        try {
            List<Chargepreferential> chargepreferentialList = chargepreferentialRepo.findalldeletedChargepreferential();
            if (chargepreferentialList.size() > 0){
                return new ResponseEntity<>(chargepreferentialList, HttpStatus.OK);
            }else {
                return new ResponseEntity<>(new MessageResponse("No Data Found"), HttpStatus.NOT_FOUND);
            }
        }catch (Exception e){
            return new ResponseEntity<>(new MessageResponse(e.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/fetchall/undeleted")
    public ResponseEntity<?> fetchallundeletedChargepreferential(){
        try {
            List<Chargepreferential> chargepreferentialList = chargepreferentialRepo.findallundeletedChargepreferential();
            if (chargepreferentialList.size() > 0){
                return new ResponseEntity<>(chargepreferentialList, HttpStatus.OK);
            }else {
                return new ResponseEntity<>(new MessageResponse("No Data Found"), HttpStatus.NOT_FOUND);
            }
        }catch (Exception e){
            return new ResponseEntity<>(new MessageResponse(e.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
        }
    }


}
