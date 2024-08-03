package co.ke.emtechhouse.AmountSlabs;


import co.ke.emtechhouse.Utils.Responses.MessageResponse;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/amountslab")
@Slf4j
@Api(tags = "Amount slabs API")
public class AmountSlabController {

    @Autowired
    private AmountSlabService amountSlabService;
    @Autowired
    private AmountSlabRepository amountSlabRepository;

    @PostMapping("/add")
    public ResponseEntity<?> addamountslab(@RequestBody AmountSlab amountSlab){
        try {
            amountSlabRepository.save(amountSlab);
            return new ResponseEntity<>(new MessageResponse("Amount slab added successfully"), HttpStatus.CREATED);
        }catch (Exception e){
            return new ResponseEntity<>(new MessageResponse(e.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> fetchallamountslabs(){
        try {
            return new ResponseEntity<>(amountSlabRepository.findAll(), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(new MessageResponse(e.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateamountslabs(@RequestBody AmountSlab amountSlab){
        try {
            amountSlabRepository.save(amountSlab);
            return new ResponseEntity<>(new MessageResponse("Amount slab updated successfully"), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(new MessageResponse(e.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("fetchbyid")
    public ResponseEntity<?> fetchamountslabbyid(@RequestParam Long id){
        try {
            return new ResponseEntity<>(amountSlabRepository.findById(id), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(new MessageResponse(e.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/permanent/delete")
    public ResponseEntity<?> deleteamountslabpermanent(@RequestParam Long id){
        try {
            amountSlabRepository.deleteById(id);
            return new ResponseEntity<>(new MessageResponse("Amount slab deleted successfully"), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/delete")
    public ResponseEntity<?> deleteAmountSlab(@RequestParam Long id, @RequestParam String deletedby){
        try {
            Optional<AmountSlab> amountSlabcheck = amountSlabRepository.findById(id);
            if (amountSlabcheck.isPresent()){
                AmountSlab amountSlab = amountSlabcheck.get();
                amountSlab.setDeletedTime(new Date());
                amountSlab.setDeletedBy(deletedby);
                amountSlab.setDeletedFlag('Y');

                amountSlabRepository.save(amountSlab);
                return new ResponseEntity<>(new MessageResponse("amountSlab interest code: " + amountSlab.getInterestCode() + " deleted successfully"), HttpStatus.OK);
            }else {
                return new ResponseEntity<>(new MessageResponse("No Data Found"), HttpStatus.BAD_REQUEST);
            }
        }catch (Exception e){
            return new ResponseEntity<>(new MessageResponse(e.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/verify")
    public ResponseEntity<?> verifyAmountSlab(@RequestParam Long id, @RequestParam String verifiedby){
        try {
            Optional<AmountSlab> amountSlabcheck = amountSlabRepository.findById(id);
            if (amountSlabcheck.isPresent()){
                AmountSlab amountSlab = amountSlabcheck.get();
                amountSlab.setVerifiedTime(new Date());
                amountSlab.setVerifiedBy(verifiedby);
                amountSlab.setVerifiedFlag('Y');

                amountSlabRepository.save(amountSlab);
                return new ResponseEntity<>(new MessageResponse("amountSlab interest code: " + amountSlab.getInterestCode() + " verified successfully"), HttpStatus.OK);
            }else {
                return new ResponseEntity<>(new MessageResponse("No Data Found"), HttpStatus.BAD_REQUEST);
            }
        }catch (Exception e){
            return new ResponseEntity<>(new MessageResponse(e.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/modify")
    public ResponseEntity<?> modifyAmountSlab(@RequestParam Long id, @RequestParam String modifiedby){
        try {
            Optional<AmountSlab> amountSlabcheck = amountSlabRepository.findById(id);
            if (amountSlabcheck.isPresent()){
                AmountSlab amountSlab = amountSlabcheck.get();
                amountSlab.setModifiedTime(new Date());
                amountSlab.setModifiedBy(modifiedby);
                amountSlab.setModifiedFlag('Y');

                amountSlabRepository.save(amountSlab);
                return new ResponseEntity<>(new MessageResponse("amountSlab interest code: " + amountSlab.getInterestCode() + " modified successfully"), HttpStatus.OK);
            }else {
                return new ResponseEntity<>(new MessageResponse("No Data Found"), HttpStatus.BAD_REQUEST);
            }
        }catch (Exception e){
            return new ResponseEntity<>(new MessageResponse(e.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/fetchall/deleted")
    public ResponseEntity<?> fetchalldeletedAmountSlabs(){
        try {
            List<AmountSlab> amountSlabList = amountSlabRepository.findalldeletedAmountSlabs();
            if (amountSlabList.size() > 0){
                return new ResponseEntity<>(amountSlabList, HttpStatus.OK);
            }else {
                return new ResponseEntity<>(new MessageResponse("No Data Found"), HttpStatus.NOT_FOUND);
            }
        }catch (Exception e){
            return new ResponseEntity<>(new MessageResponse(e.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/fetchall/undeleted")
    public ResponseEntity<?> fetchallundeletedAmountSlabs(){
        try {
            List<AmountSlab> amountSlabList = amountSlabRepository.findallundeletedAmountSlabs();
            if (amountSlabList.size() > 0){
                return new ResponseEntity<>(amountSlabList, HttpStatus.OK);
            }else {
                return new ResponseEntity<>(new MessageResponse("No Data Found"), HttpStatus.NOT_FOUND);
            }
        }catch (Exception e){
            return new ResponseEntity<>(new MessageResponse(e.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
        }
    }

}
