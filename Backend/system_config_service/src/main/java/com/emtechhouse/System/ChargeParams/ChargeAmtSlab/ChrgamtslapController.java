package com.emtechhouse.System.ChargeParams.ChargeAmtSlab;

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

@RestController
@Slf4j
@CrossOrigin
@Api(tags = "Charge Amount Slap API")
@RequestMapping("/api/v1/parameters/configurations/charge/params/amount/slap")
public class ChrgamtslapController {
    @Autowired
    private ChrgamtslapRepo chrgamtslapRepo;
    @Autowired
    private ChrgamtslapService chrgamtslapService;

    @PostMapping("/add")
    public ResponseEntity<?> addChrgamtslap(@RequestBody Chrgamtslab chrgamtslab){
        try{
           Optional<Chrgamtslab> chrgamtslapcheck = chrgamtslapRepo.findBySlapCode(chrgamtslab.getSlap_code());
           if (!chrgamtslapcheck.isPresent()){
                chrgamtslapRepo.save(chrgamtslab);
                return new ResponseEntity<>(new MessageResponse("Charge Amount Slap added successfully"), HttpStatus.CREATED);
           }else {
               return new ResponseEntity<>(new MessageResponse("Charge amount slap with code " + chrgamtslab.getSlap_code() + " already exists"), HttpStatus.CONFLICT);
           }
        } catch (Exception e) {
            log.info("Error {} "+e);
            return null;
        }
    }
    @GetMapping("/all")
    public ResponseEntity<List<Chrgamtslab>> getAllChrgamtslaps () {
        try{
            List<Chrgamtslab> chrgamtslabs = chrgamtslapService.findAllChrgamtslaps();
            return  new ResponseEntity<>(chrgamtslabs, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Error {} "+e);
            return null;
        }
    }
    @GetMapping("/find/{id}")
    public ResponseEntity<Chrgamtslab> getChrgamtslapById (@PathVariable("id") Long id){
        try{
            Chrgamtslab chrgamtslab = chrgamtslapService.findChrgamtslapById(id);
            return new ResponseEntity<>(chrgamtslab, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Error {} "+e);
            return null;
        }
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<Chrgamtslab> updateChrgamtslap(@PathVariable("id") long id, @RequestBody Chrgamtslab chrgamtslab) {
        try{
            Optional<Chrgamtslab> chrgamtslapData = chrgamtslapRepo.findById(id);
            if (chrgamtslapData.isPresent()) {
                Chrgamtslab _chrgamtslab = chrgamtslapData.get();
                _chrgamtslab.setSlap_code(chrgamtslab.getSlap_code());
                _chrgamtslab.setAmt_from(chrgamtslab.getAmt_from());
                _chrgamtslab.setAmt_to(chrgamtslab.getAmt_to());
                _chrgamtslab.setAmt_charge(chrgamtslab.getAmt_charge());
                _chrgamtslab.setModifiedTime(new Date());
                return new ResponseEntity<>(chrgamtslapRepo.save(_chrgamtslab), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.info("Error {} "+e);
            return null;
        }
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Chrgamtslab> deleteChrgamtslap(@PathVariable("id") Long id){

        try{
            chrgamtslapRepo.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.info("Caught Error {}"+e);
            return null;
        }
    }

    @PutMapping("/modify")
    public ResponseEntity<?> modifychargeamountslap(@RequestParam Long id, @RequestParam String modifiedby){
        try {
            Optional<Chrgamtslab> chrgamtslapcheck = chrgamtslapRepo.findById(id);
            if (chrgamtslapcheck.isPresent()){
                Chrgamtslab chrgamtslab = chrgamtslapcheck.get();
                chrgamtslab.setModifiedFlag('Y');
                chrgamtslab.setModifiedBy(modifiedby);
                chrgamtslab.setModifiedTime(new Date());

                chrgamtslapRepo.save(chrgamtslab);
                return new ResponseEntity<>(new MessageResponse("Charge amount slap modified successfully"), HttpStatus.OK);
            }else {
                return new ResponseEntity<>(new MessageResponse("No Data Found"), HttpStatus.NOT_FOUND);
            }
        }catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse(e.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/verify")
    public ResponseEntity<?> verifychargeamountslap(@RequestParam Long id, @RequestParam String verifiedby){
        try {
            Optional<Chrgamtslab> chrgamtslapcheck = chrgamtslapRepo.findById(id);
            if (chrgamtslapcheck.isPresent()){
                Chrgamtslab chrgamtslab = chrgamtslapcheck.get();
                chrgamtslab.setVerifiedFlag('Y');
                chrgamtslab.setVerifiedBy(verifiedby);
                chrgamtslab.setVerifiedTime(new Date());

                chrgamtslapRepo.save(chrgamtslab);
                return new ResponseEntity<>(new MessageResponse("Charge amount slap verified successfully"), HttpStatus.OK);
            }else {
                return new ResponseEntity<>(new MessageResponse("No Data Found"), HttpStatus.NOT_FOUND);
            }
        }catch (Exception e){
            return new ResponseEntity<>(new MessageResponse(e.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/delete")
    public ResponseEntity<?> deletechargeamountslap(@RequestParam Long id, @RequestParam String deletedby){
        try {
            Optional<Chrgamtslab> chrgamtslapcheck = chrgamtslapRepo.findById(id);
            if (chrgamtslapcheck.isPresent()){
                Chrgamtslab chrgamtslab = chrgamtslapcheck.get();
                chrgamtslab.setDeletedFlag('Y');
                chrgamtslab.setDeletedBy(deletedby);
                chrgamtslab.setDeletedTime(new Date());

                chrgamtslapRepo.save(chrgamtslab);
                return new ResponseEntity<>(new MessageResponse("Charge amount slap deleted successfully"), HttpStatus.OK);
            }else {
                return new ResponseEntity<>(new MessageResponse("No Data Found"), HttpStatus.NOT_FOUND);
            }
        }catch (Exception e){
            return new ResponseEntity<>(new MessageResponse(e.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/fetchall/deleted")
    public ResponseEntity<?> fetchalldeletedchargeamountslap(){
        try {
            List<Chrgamtslab> chrgamtslabList = chrgamtslapRepo.findalldeletedchargeamountslap();
            if (chrgamtslabList.size() > 0){
                return new ResponseEntity<>(chrgamtslabList, HttpStatus.OK);
            }else {
                return new ResponseEntity<>(new MessageResponse("No Data Found"), HttpStatus.NOT_FOUND);
            }
        }catch (Exception e){
            return new ResponseEntity<>(new MessageResponse(e.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/fetchall/undeleted")
    public ResponseEntity<?> fetchallundeletedchargeamountslap(){
        try {
            List<Chrgamtslab> chrgamtslabList = chrgamtslapRepo.findallundeletedchargeamountslap();
            if (chrgamtslabList.size() > 0){
                return new ResponseEntity<>(chrgamtslabList, HttpStatus.OK);
            }else {
                return new ResponseEntity<>(new MessageResponse("No Data Found"), HttpStatus.NOT_FOUND);
            }
        }catch (Exception e){
            return new ResponseEntity<>(new MessageResponse(e.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
        }
    }

}
