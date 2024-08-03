package co.ke.emtechhouse.InterestMaintenance;

import co.ke.emtechhouse.Utils.Responses.EntityResponse;
import co.ke.emtechhouse.Utils.Responses.MessageResponse;
import co.ke.emtechhouse.Utils.CONSTANTS;
import co.ke.emtechhouse.Utils.RESPONSEMESSAGES;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

//@CrossOrigin
@RequestMapping("interest")
@RestController
@CrossOrigin
@Api(tags = "Interests API")
public class InterestResource {
    @Autowired
    private InterestService interestService;
    @Autowired
    private InterestRepository interestRepository;

    @PostMapping("add")
    public ResponseEntity<?> saveInterest(@RequestBody Interest interest, HttpServletRequest request){
//        if(!Objects.isNull(interest.getSn())){
//            return ResponseEntity.ok().body(new EntityResponse(RESPONSEMESSAGES.BAD_METHOD, interest, HttpStatus.BAD_REQUEST.value()));
//        }
        interest.setPostedTime(new Date());
        interest.setPostedFlag(CONSTANTS.YES);
        interest.setDeletedFlag(CONSTANTS.NO);
        interest.setVerifiedFlag(CONSTANTS.NO);
        interest.setModifiedTime(new Date());
        interest.getAmountSlabs().forEach(amountSlab -> {
            amountSlab.setPostedFlag(CONSTANTS.YES);
            amountSlab.setPostedTime(new Date());
            amountSlab.setVersion(1);
        });
        Interest sInterest = interestService.saveInterest(interest);
        return ResponseEntity.status(HttpStatus.CREATED).body(new EntityResponse(RESPONSEMESSAGES.INTEREST_ADDITION_SUCCESSFUL+sInterest.getInterestCode(), null, HttpStatus.CREATED.value()));
    }

    @GetMapping("{code}")
    public ResponseEntity<?> retrieveInterest(@PathVariable("code") String interestCode, HttpServletRequest request){
        Interest rInterest = interestService.retrieveInterest(interestCode);
        if(Objects.isNull(rInterest)){
            return ResponseEntity.status(HttpStatus.OK).body(new EntityResponse(RESPONSEMESSAGES.RECORD_NOT_FOUND, null, HttpStatus.NOT_FOUND.value()));
        }
        return ResponseEntity.ok().body(rInterest);
    }

    @PutMapping("update")
    public ResponseEntity<?> updateInterest(@RequestBody Interest interest, HttpServletRequest request){
        if(Objects.isNull(interest.getId())){
            return ResponseEntity.ok().body(new EntityResponse<>(RESPONSEMESSAGES.BAD_METHOD, interest, HttpStatus.BAD_REQUEST.value()));
        }
        interest.getAmountSlabs().forEach(amountSlab -> {
            amountSlab.setModifiedTime(new Date());
            amountSlab.setVersion(interestService.recentVersion(interest.getInterestCode()).getAmountSlabs().get(0).getVersion()+1);
        });
        Interest uInterest = interestService.updateInterest(interest);
        return ResponseEntity.ok().body(new EntityResponse<>(RESPONSEMESSAGES.INTEREST_UPDATED_SUCCESSFULLY, uInterest, HttpStatus.OK.value()));
    }

    @GetMapping("all")
    public ResponseEntity<?> allInterestCodes(){
        return ResponseEntity.ok().body(new EntityResponse<>(RESPONSEMESSAGES.RECORD_FOUND, interestService.retrieveInterests(), HttpStatus.OK.value()));
    }

    @GetMapping("recent/{code}")
    public ResponseEntity<?> recentInterestRates(@PathVariable("code") String code){
        Interest interest = interestService.recentVersion(code);
        if(Objects.isNull(interest)){
            return ResponseEntity.ok().body(new EntityResponse<>(RESPONSEMESSAGES.RECORD_NOT_FOUND, interest, HttpStatus.NOT_FOUND.value()));
        }
        return ResponseEntity.ok().body(new EntityResponse<>(RESPONSEMESSAGES.RECORD_FOUND, interest, HttpStatus.OK.value()));
    }

    @DeleteMapping("/permanent/delete/{id}")
    public ResponseEntity<?> deleteInterestpermanently(@PathVariable("id") Long id){
        try{
            interestRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse(e.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/delete")
    public ResponseEntity<?> deleteInterest(@RequestParam Long id, @RequestParam String deletedby){
        try {
            Optional<Interest> interestcheck = interestRepository.findById(id);
            if (interestcheck.isPresent()){
                Interest interest = interestcheck.get();
                interest.setDeletedTime(new Date());
                interest.setDeletedBy(deletedby);
                interest.setDeletedFlag('Y');

                interestRepository.save(interest);
                return new ResponseEntity<>(new MessageResponse("Interest code: " + interest.getInterestCode() + " deleted successfully"), HttpStatus.OK);
            }else {
                return new ResponseEntity<>(new MessageResponse("No Data Found"), HttpStatus.BAD_REQUEST);
            }
        }catch (Exception e){
            return new ResponseEntity<>(new MessageResponse(e.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/verify")
    public ResponseEntity<?> verifyInterest(@RequestParam Long id, @RequestParam String verifiedby){
        try {
            Optional<Interest> interestcheck = interestRepository.findById(id);
            if (interestcheck.isPresent()){
                Interest interest = interestcheck.get();
                interest.setVerifiedTime(new Date());
                interest.setVerifiedBy(verifiedby);
                interest.setVerifiedFlag('Y');

                interestRepository.save(interest);
                return new ResponseEntity<>(new MessageResponse("Interest code: " + interest.getInterestCode() + " verified successfully"), HttpStatus.OK);
            }else {
                return new ResponseEntity<>(new MessageResponse("No Data Found"), HttpStatus.BAD_REQUEST);
            }
        }catch (Exception e){
            return new ResponseEntity<>(new MessageResponse(e.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/modify")
    public ResponseEntity<?> modifyInterest(@RequestParam Long id, @RequestParam String modifiedby){
        try {
            Optional<Interest> interestcheck = interestRepository.findById(id);
            if (interestcheck.isPresent()){
                Interest interest = interestcheck.get();
                interest.setModifiedTime(new Date());
                interest.setModifiedBy(modifiedby);
                interest.setModifiedFlag('Y');

                interestRepository.save(interest);
                return new ResponseEntity<>(new MessageResponse("Interest code: " + interest.getInterestCode() + " modified successfully"), HttpStatus.OK);
            }else {
                return new ResponseEntity<>(new MessageResponse("No Data Found"), HttpStatus.BAD_REQUEST);
            }
        }catch (Exception e){
            return new ResponseEntity<>(new MessageResponse(e.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/fetchall/deleted")
    public ResponseEntity<?> fetchalldeletedInterests(){
        try {
            List<Interest> interestList = interestRepository.findalldeletedInterests();
            if (interestList.size() > 0){
                return new ResponseEntity<>(interestList, HttpStatus.OK);
            }else {
                return new ResponseEntity<>(new MessageResponse("No Data Found"), HttpStatus.NOT_FOUND);
            }
        }catch (Exception e){
            return new ResponseEntity<>(new MessageResponse(e.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/fetchall/undeleted")
    public ResponseEntity<?> fetchallundeletedInterests(){
        try {
            List<Interest> interestList = interestRepository.findallundeletedInterests();
            if (interestList.size() > 0){
                return new ResponseEntity<>(interestList, HttpStatus.OK);
            }else {
                return new ResponseEntity<>(new MessageResponse("No Data Found"), HttpStatus.NOT_FOUND);
            }
        }catch (Exception e){
            return new ResponseEntity<>(new MessageResponse(e.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
        }
    }

}
