package com.emtechhouse.System.InterestCodeParams;

import com.emtechhouse.System.GLSubhead.GLSubhead;
import com.emtechhouse.System.InterestCodeParams.Interestcodeslabs.Interestcodeslabs;
import com.emtechhouse.System.InterestCodeParams.Interestcodeslabs.InterestcodeslabsRepo;
import com.emtechhouse.System.Utils.CONSTANTS;
import com.emtechhouse.System.Utils.EntityResponse;
import com.emtechhouse.System.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.System.Utils.HttpInterceptor.UserRequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("api/v1/interestcode/params")
public class InterestcodeparamsController {
    private final InterestcodeparamsService interestcodeparamsService;
    private final InterestcodeparamsRepo interestcodeparamsRepo;
    private final InterestcodeslabsRepo interestcodeslabsRepo;

    public InterestcodeparamsController(InterestcodeparamsService interestcodeparamsService, InterestcodeparamsRepo interestcodeparamsRepo, InterestcodeslabsRepo interestcodeslabsRepo) {
        this.interestcodeparamsService = interestcodeparamsService;
        this.interestcodeparamsRepo = interestcodeparamsRepo;
        this.interestcodeslabsRepo = interestcodeslabsRepo;
    }
    @PostMapping("/add")
    public ResponseEntity<?> addInterestcodeparams(@RequestBody Interestcodeparams interestcodeparams) {
        try {
            EntityResponse response = new EntityResponse();
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
//                    Check if code exist
                    Optional<Interestcodeparams> interestcodeparams1 = interestcodeparamsRepo.findByEntityIdAndInterestCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), interestcodeparams.getInterestCode(), 'N');
                    if (interestcodeparams1.isPresent()){
                        response.setMessage("Code Exists");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }else{
//                        check if percentage is within the range
                        if (interestcodeparams.getPenalIntBasedOn().equalsIgnoreCase(CONSTANTS.PERCENT) && interestcodeparams.getPenalInterest() <1 || interestcodeparams.getPenalInterest() >  100 ){
                            response.setMessage("Percentage of penal interest out or range. 1 - 100 values accepted. Your value is "+interestcodeparams.getPenalInterest());
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        }else {
                            interestcodeparams.setPostedBy(UserRequestContext.getCurrentUser());
                            interestcodeparams.setEntityId(EntityRequestContext.getCurrentEntityId());
                            interestcodeparams.setPostedFlag('Y');
                            interestcodeparams.setPostedTime(new Date());
                            Interestcodeparams newInterestcodeparams = interestcodeparamsService.addInterestcodeparams(interestcodeparams);
                            response.setMessage("INTERESTS PARAM WITH CODE " + interestcodeparams.getInterestCode() + " AND NAME " + interestcodeparams.getInterestName() + " CREATED SUCCESSFULLY AT " + interestcodeparams.getPostedTime());
                            response.setStatusCode(HttpStatus.CREATED.value());
                            response.setEntity(newInterestcodeparams);
                        }
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    @GetMapping("/all")
    public ResponseEntity<?> getAllInterestcodeparamss() {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                EntityResponse response = new EntityResponse();
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    List<Interestcodeparams> interestcodeparams = interestcodeparamsRepo.findByEntityIdAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), 'N');
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(interestcodeparams);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<?> getInterestcodeparamsById(@PathVariable("id") Long id) {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                EntityResponse response = new EntityResponse();
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    Interestcodeparams interestcodeparams = interestcodeparamsService.findById(id);
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(interestcodeparams);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }


    @GetMapping("/find/charge/rate/by/code/{code}/and/amount/{amount}")
    public ResponseEntity<?> getInterestcodeparamsById(@PathVariable("code") String code, @PathVariable("amount") Double amount) {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                EntityResponse response = new EntityResponse();
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    Interestdetails interestdetails = new Interestdetails();
                    Optional<Interestcodeparams> interestcodeparams = interestcodeparamsRepo.findByEntityIdAndInterestCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), code, 'N');
                    if (interestcodeparams.isPresent()){
                        Interestcodeparams interestcodeparams1 = interestcodeparams.get();
//                    TODO: Check if Outdated
//                    TODO: Check if deleted
                        if (interestcodeparams1.getDeletedFlag() == 'Y'){
                            EntityResponse response = new EntityResponse();
                            response.setMessage("Interest Code is Deleted!");
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            response.setEntity("");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        }else{
//                    TODO: Check if not Verified
                            if (interestcodeparams1.getVerifiedFlag() == 'N'){
                                EntityResponse response = new EntityResponse();
                                response.setMessage("Interest Code not verified!");
                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                response.setEntity("");
                                return new ResponseEntity<>(response, HttpStatus.OK);
                            }else{
                                Double interestrate = 1.0;
                                if (interestcodeparams1.getInterestType().equalsIgnoreCase("FlatRate")){
                                    if (interestcodeparams1.getInterestrate() == null){
                                        interestrate = 1.0;
                                    }else{
                                        interestrate = interestcodeparams1.getInterestrate();
                                    }
                                }else{
                                    Optional<Interestcodeslabs> intslab = interestcodeslabsRepo.findAllByAmount("122.0");
                                    if (intslab.isPresent()){
//                                Check if percentage based
//                                interestrate =  Double.valueOf(intslab.get().getPercentage());
                                        Integer percentage = 1;
                                        if (intslab.get().getUsePercentage()=="Y"){
                                            if (intslab.get().getPercentage() == null){
                                                percentage = 1;
                                            }else{
                                                percentage = intslab.get().getPercentage();
                                            }
                                            interestrate = (percentage/100) * amount;
                                        }else {
                                            interestrate = intslab.get().getChargeAmount();
                                        }
                                    }else{
                                        interestrate = 1.0;
                                    }
                                }

                                //penal interest
                                Double interestAmount = 0.00;
                                if (interestcodeparams.get().getPenalIntBasedOn().equalsIgnoreCase(CONSTANTS.PERCENT)){
                                    interestAmount = (interestcodeparams.get().getPenalInterest()/100) * amount;
                                } else {
                                    interestAmount = interestcodeparams.get().getPenalInterest();
                                }
                                interestdetails.setRate(interestrate);
                                interestdetails.setPenalInterest(interestAmount);
                                interestdetails.setPenalInterestType(interestcodeparams.get().getInterestType());
                                interestdetails.setCalculationMethod(interestcodeparams.get().getCalculationMethod());
                                interestdetails.setInterestPeriod(interestcodeparams.get().getInterestPeriod());
                                EntityResponse response = new EntityResponse();
                                response.setMessage(HttpStatus.OK.getReasonPhrase());
                                response.setStatusCode(HttpStatus.OK.value());
                                response.setEntity(interestdetails);
                                return new ResponseEntity<>(response, HttpStatus.OK);
                            }
                        }
                    }else{
                        EntityResponse response = new EntityResponse();
                        response.setMessage("Interest code not found");
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    @GetMapping("/code/find/{interestCode}")
    public ResponseEntity<?> getInterestCodeParamByCode(@PathVariable("interestCode") String interestCode) {
        try {
            EntityResponse response = new EntityResponse();
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                Optional<Interestcodeparams> searchCode = interestcodeparamsRepo.findByEntityIdAndInterestCode(EntityRequestContext.getCurrentEntityId(), interestCode);
                if (searchCode.isPresent()) {
                    Interestcodeparams interestcodeparams =searchCode.get();
                    response.setMessage("INTERESTS PARAM WITH CODE " + interestcodeparams.getInterestCode() + " ALREADY REGISTERED");
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(interestcodeparams);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    response.setMessage("INTERESTS PARAM WITH CODE " + interestCode + " AVAILABLE FOR REGISTRATION");
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setEntity("");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

//    @GetMapping("/find/charge/rate/by/code/{code}/and/amount/{amount}")
//    public ResponseEntity<?> getInterestcodeparamsById(@PathVariable("code") String code, @PathVariable("amount") Double amount) {
//        try {
//            if (UserRequestContext.getCurrentUser().isEmpty()) {
//                EntityResponse response = new EntityResponse();
//                response.setMessage("User Name not present in the Request Header");
//                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                response.setEntity("");
//                return new ResponseEntity<>(response, HttpStatus.OK);
//            } else {
//                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
//                    EntityResponse response = new EntityResponse();
//                    response.setMessage("Entity not present in the Request Header");
//                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                    response.setEntity("");
//                    return new ResponseEntity<>(response, HttpStatus.OK);
//                } else {
//                    Interestdetails interestdetails = new Interestdetails();
//                    Optional<Interestcodeparams> interestcodeparams = interestcodeparamsRepo.findByEntityIdAndInterestCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), code, 'N');
//                    if (interestcodeparams.isPresent()){
//                        Interestcodeparams interestcodeparams1 = interestcodeparams.get();
////                    TODO: Check if Outdated
////                    TODO: Check if deleted
//                        if (interestcodeparams1.getDeletedFlag() == 'Y'){
//                            EntityResponse response = new EntityResponse();
//                            response.setMessage("Interest Code is Deleted!");
//                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                            response.setEntity("");
//                            return new ResponseEntity<>(response, HttpStatus.OK);
//                        }else{
////                    TODO: Check if not Verified
//                            if (interestcodeparams1.getVerifiedFlag() == 'N'){
//                                EntityResponse response = new EntityResponse();
//                                response.setMessage("Interest Code not verified!");
//                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                                response.setEntity("");
//                                return new ResponseEntity<>(response, HttpStatus.OK);
//                            }else{
//                                Double interestrate = 1.0;
//                                if (interestcodeparams1.getInterestType().equalsIgnoreCase("FlatRate")){
//                                    if (interestcodeparams1.getInterestrate() == null){
//                                        interestrate = 1.0;
//                                    }else{
//                                        interestrate = interestcodeparams1.getInterestrate();
//                                    }
//                                }else{
//                                    Optional<Interestcodeslabs> intslab = interestcodeslabsRepo.findAllByAmount("122.0");
//                                    if (intslab.isPresent()){
////                                Check if percentage based
////                                interestrate =  Double.valueOf(intslab.get().getPercentage());
//                                        Integer percentage = 1;
//                                        if (intslab.get().getUsePercentage()=="Y"){
//                                            if (intslab.get().getPercentage() == null){
//                                                percentage = 1;
//                                            }else{
//                                                percentage = intslab.get().getPercentage();
//                                            }
//                                            interestrate = (percentage/100) * amount;
//                                        }else {
//                                            interestrate = intslab.get().getChargeAmount();
//                                        }
//                                    }else{
//                                        interestrate = 1.0;
//                                    }
//                                }
//                                interestdetails.setRate(interestrate);
//                                interestdetails.setPenalInterest(interestcodeparams.get().getPenalInterest());
//                                interestdetails.setPenalInterestType(interestcodeparams.get().getInterestType());
//                                interestdetails.setCalculationMethod(interestcodeparams.get().getCalculationMethod());
//                                interestdetails.setInterestPeriod(interestcodeparams.get().getInterestPeriod());
//                                EntityResponse response = new EntityResponse();
//                                response.setMessage(HttpStatus.OK.getReasonPhrase());
//                                response.setStatusCode(HttpStatus.OK.value());
//                                response.setEntity(interestdetails);
//                                return new ResponseEntity<>(response, HttpStatus.OK);
//                            }
//                        }
//                    }else{
//                        EntityResponse response = new EntityResponse();
//                        response.setMessage("Interest code not found");
//                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
//                        response.setEntity("");
//                        return new ResponseEntity<>(response, HttpStatus.OK);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }

    @PutMapping("/modify")
    public ResponseEntity<?> updateInterestcodeparams(@RequestBody Interestcodeparams interestcodeparams) {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                EntityResponse response = new EntityResponse();
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    interestcodeparams.setModifiedBy(UserRequestContext.getCurrentUser());
                    interestcodeparams.setEntityId(EntityRequestContext.getCurrentEntityId());
                    Optional<Interestcodeparams> interestcodeparams1 = interestcodeparamsRepo.findById(interestcodeparams.getId());
                    if (interestcodeparams1.isPresent()) {
                        interestcodeparams.setPostedTime(interestcodeparams1.get().getPostedTime());
                        interestcodeparams.setPostedFlag(interestcodeparams1.get().getPostedFlag());
                        interestcodeparams.setPostedBy(interestcodeparams1.get().getPostedBy());
                        interestcodeparams.setModifiedFlag('Y');
                        interestcodeparams.setVerifiedFlag(interestcodeparams1.get().getVerifiedFlag());
                        interestcodeparams.setModifiedTime(new Date());
                        interestcodeparams.setModifiedBy(interestcodeparams.getModifiedBy());
                        interestcodeparams.setInterestCode(interestcodeparams1.get().getInterestCode());
                        interestcodeparamsService.updateInterestcodeparams(interestcodeparams);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("INTERESTS PARAM WITH CODE " + interestcodeparams.getInterestCode() + " AND NAME " + interestcodeparams.getInterestName() + " MODIFIED SUCCESSFULLY AT " + interestcodeparams.getModifiedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(interestcodeparams);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("/verify/{id}")
    public ResponseEntity<?> verify(@PathVariable String id) {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                EntityResponse response = new EntityResponse();
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    Optional<Interestcodeparams> interestcodeparams1 = interestcodeparamsRepo.findById(Long.parseLong(id));
                    if (interestcodeparams1.isPresent()) {
                        Interestcodeparams interestcodeparams = interestcodeparams1.get();
//                    Check Maker Checker
                        if (interestcodeparams.getPostedBy().equalsIgnoreCase(UserRequestContext.getCurrentUser())){
                            EntityResponse response = new EntityResponse();
                            response.setMessage("You Can Not Verify What you initiated");
                            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                            response.setEntity("");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        }else{
                            interestcodeparams.setVerifiedFlag('Y');
                            interestcodeparams.setVerifiedTime(new Date());
                            interestcodeparams.setVerifiedBy(UserRequestContext.getCurrentUser());
                            interestcodeparamsRepo.save(interestcodeparams);
                            EntityResponse response = new EntityResponse();
                            response.setMessage("INTERESTS PARAM WITH CODE " + interestcodeparams.getInterestCode() + " AND NAME " + interestcodeparams.getInterestName() + " VERIFIED SUCCESSFULLY AT " + interestcodeparams.getVerifiedTime());
                            response.setStatusCode(HttpStatus.OK.value());
                            response.setEntity(interestcodeparams);
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        }
                    } else {
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteInterestcodeparams(@PathVariable String id) {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                EntityResponse response = new EntityResponse();
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    Optional<Interestcodeparams> interestcodeparams1 = interestcodeparamsRepo.findById(Long.parseLong(id));
                    if (interestcodeparams1.isPresent()) {
                        Interestcodeparams interestcodeparams = interestcodeparams1.get();
                        interestcodeparams.setDeletedFlag('Y');
                        interestcodeparams.setDeletedTime(new Date());
                        interestcodeparams.setDeletedBy(UserRequestContext.getCurrentUser());
                        interestcodeparamsRepo.save(interestcodeparams);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("INTERESTS PARAM WITH CODE " + interestcodeparams.getInterestCode() + " AND NAME " + interestcodeparams.getInterestName() + " DELETED SUCCESSFULLY AT " + interestcodeparams.getDeletedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(interestcodeparams);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
}
