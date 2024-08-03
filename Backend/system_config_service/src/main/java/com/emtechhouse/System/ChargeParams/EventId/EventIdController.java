package com.emtechhouse.System.ChargeParams.EventId;


import com.emtechhouse.System.ChargeParams.EventId.TieredCharges.Tieredcharges;
import com.emtechhouse.System.ChargeParams.EventId.TieredCharges.TieredchargesRepo;
import com.emtechhouse.System.DTO.EventCodeDto;
import com.emtechhouse.System.DTO.IncomingChargeCollectionReq;
import com.emtechhouse.System.DTO.PartTran;
import com.emtechhouse.System.Utils.EntityResponse;
import com.emtechhouse.System.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.System.Utils.HttpInterceptor.UserRequestContext;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

//@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@Slf4j
@Api(value = "/Event ID API", tags = "Event ID API")
@RequestMapping("/api/v1/parameters/configurations/event_id/")
//@PreAuthorize("hasAnyRole( 'ROLE_ADMIN','ROLE_DIRECTOR','ROLE_HR','ROLE_SUPERVISOR','ROLE_USER',)")
public class EventIdController {
    @Autowired
    private EventIdRepo eventIdRepo;
    @Autowired
    private EventIdService eventIdService;
    @Autowired
    private TieredchargesRepo tieredchargesRepo;

    @PostMapping("/add")
    public ResponseEntity<?> addEventId(@RequestBody EventId eventId) {
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
                    Optional<EventId> checkEventId = eventIdRepo.findByEventIdCodeAndDeletedFlag( eventId.getEventIdCode(), 'N');
                    if (checkEventId.isPresent()) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("Code already exists!");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                            if (eventId.getHas_exercise_duty().equalsIgnoreCase("Y") && eventId.getExciseDutyCollAc() == null){
                                EntityResponse response = new EntityResponse();
                                response.setMessage("You must supply excise  duty account!");
                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                response.setEntity("");
                                return new ResponseEntity<>(response, HttpStatus.OK);
                            }else {
                                eventId.setPostedBy(UserRequestContext.getCurrentUser());
                                eventId.setEntityId(EntityRequestContext.getCurrentEntityId());
                                eventId.setPostedFlag('Y');
                                eventId.setPostedTime(new Date());
                                EventId newEventId = eventIdService.addEventId(eventId);
                                EntityResponse response = new EntityResponse();
                                response.setMessage("EVENT ID  WITH CODE " + eventId.getEventIdCode() + " CREATED SUCCESSFULLY AT " + eventId.getPostedTime());
                                response.setStatusCode(HttpStatus.CREATED.value());
                                response.setEntity(newEventId);
                                return new ResponseEntity<>(response, HttpStatus.OK);
                            }
                    }
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllEventIds() {
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
                    List<EventId> eventId = eventIdRepo.findAllByEntityIdAndDeletedFlagOrderByIdDesc(EntityRequestContext.getCurrentEntityId(), 'N');
                    if (eventId.size() > 0) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(eventId);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity(eventId);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/find/by/code/{eventIdCode}")
    public ResponseEntity<?> getEventIdById(@PathVariable("eventIdCode") String eventIdCode) {
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
                    Optional<EventId> eventId = eventIdRepo.findByEventIdCodeAndDeletedFlag(eventIdCode, 'N');
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(eventId);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("/find/by/codes")
    public ResponseEntity<?> getEventIdByIds(@RequestBody List<EventCodeDto> evts) {
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
                    List<EventId> eventIdList= new ArrayList<>();

                    evts.forEach(evt->{
                        Optional<EventId> eventId = eventIdRepo.findByEventIdCodeAndDeletedFlag(evt.getEventIdCode(), 'N');
                        if(eventId.isPresent()){
                            eventIdList.add(eventId.get());
                        }
                    });
                    EntityResponse response = new EntityResponse();
                    if(eventIdList.size()>0){
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(eventIdList);
                    }else {
                        response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity(null);
                    }

                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PostMapping("/collect/charges/")
    public ResponseEntity<?> collectCharges(@RequestBody List<IncomingChargeCollectionReq> incomingChargeCollectionReqs) {
        try {
            EntityResponse response = eventIdService.collectCharges(incomingChargeCollectionReqs);
            System.out.println("**************************************");
            System.out.println(response);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PostMapping("/collect/account/activation/charges/")
    public ResponseEntity<?> collectAccountsActivationCharges(@RequestBody List<IncomingChargeCollectionReq> incomingChargeCollectionReqs) {
        try {
            EntityResponse response = eventIdService.collectAccountActivationFees(incomingChargeCollectionReqs);
            System.out.println("**************************************");
            System.out.println(response);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PostMapping("/collect/charges/money/transfer/")
    public ResponseEntity<?> collectMoneyTransferCharges(@RequestBody List<IncomingChargeCollectionReq> incomingChargeCollectionReqs) {
        try {
            EntityResponse response = eventIdService.collectMoneyTransferCharges(incomingChargeCollectionReqs);
            System.out.println("**************************************");
            System.out.println(response);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }


    @PostMapping("/collect/charges/balance/enquiry/")
    public ResponseEntity<?> balanceEnquiryCharges(@RequestBody List<IncomingChargeCollectionReq> incomingChargeCollectionReqs) {
        try {
            EntityResponse response = eventIdService.balanceEnquiryCharges(incomingChargeCollectionReqs);
            System.out.println("**************************************");
            System.out.println(response);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }



    @PostMapping("/collect/charges/sms/charges/")
    public ResponseEntity<?> smsCharges(@RequestBody List<IncomingChargeCollectionReq> incomingChargeCollectionReqs) {
        try {
            EntityResponse response = eventIdService.smsCharges(incomingChargeCollectionReqs);
            System.out.println("*****************sms*********************");
            System.out.println(response);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }


    @GetMapping("/find/by/chargetype/{chargeType}/and/amount/{amount}")
    public ResponseEntity<?> getChargeType(@PathVariable("chargeType") String chargeType, @PathVariable("amount") Double amount) {
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
                    PartTran partTran = new PartTran();
                    Optional<EventId> eventId = eventIdRepo.findByEntityIdAndAndChargeTypeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), chargeType, 'N');
                    if (eventId.isPresent()) {
                        String amt_derivation_type = eventId.get().getAmt_derivation_type();
                        if (amt_derivation_type.equalsIgnoreCase("FIXED")) {
                            partTran.setPartTranType("Credit");
                            partTran.setTransactionAmount(eventId.get().getAmt());
                            partTran.setAcid(eventId.get().getAc_placeholder());
                            partTran.setMonthlyFee(eventId.get().getMonthlyFee());
                            partTran.setMonthlyFee(eventId.get().getMonthlyFee());
                            partTran.setCurrency(eventId.get().getChrg_coll_crncy());
                            partTran.setTransactionParticulars(eventId.get().getTran_particulars());
                        } else if (amt_derivation_type.equalsIgnoreCase("PCNT")) {
                            Double chrgAmount = (eventId.get().getPercentage() / 100) * amount;
                            if (chrgAmount < eventId.get().getMin_amt()) {
                                chrgAmount = eventId.get().getMin_amt();
                            } else if (chrgAmount > eventId.get().getMax_amt()) {
                                chrgAmount = eventId.get().getMax_amt();
                            }
                            partTran.setPartTranType("Credit");
                            partTran.setTransactionAmount(chrgAmount);
                            partTran.setMonthlyFee(eventId.get().getMonthlyFee());
                            partTran.setAcid(eventId.get().getAc_placeholder());
                            partTran.setCurrency(eventId.get().getChrg_coll_crncy());
                            partTran.setTransactionParticulars(eventId.get().getTran_particulars());
                        } else if (amt_derivation_type.equalsIgnoreCase("TC")) {
                            Optional<Tieredcharges> tieredchargesCheck = tieredchargesRepo.findBetweenRange(String.valueOf(amount), eventId.get().getId());
                            if (tieredchargesCheck.isPresent()){
                                Tieredcharges tieredcharges = tieredchargesCheck.get();
                                if (tieredcharges.getUsePercentage() == 'Y') {
                                    Double chrg = (tieredcharges.getPercentage() / 100) * amount;
                                    partTran.setPartTranType("Credit");
                                    partTran.setTransactionAmount(chrg);
                                    partTran.setMonthlyFee(eventId.get().getMonthlyFee());
                                    partTran.setAcid(eventId.get().getAc_placeholder());
                                    partTran.setCurrency(eventId.get().getChrg_coll_crncy());
                                    partTran.setTransactionParticulars(eventId.get().getTran_particulars());
                                } else {
                                    partTran.setPartTranType("Credit");
                                    partTran.setTransactionAmount(tieredcharges.getChargeAmount());
                                    partTran.setMonthlyFee(eventId.get().getMonthlyFee());
                                    partTran.setAcid(eventId.get().getAc_placeholder());
                                    partTran.setCurrency(eventId.get().getChrg_coll_crncy());
                                    partTran.setTransactionParticulars(eventId.get().getTran_particulars());
                                }
                            }else {
                                Optional<Tieredcharges> tieredchargesMax = tieredchargesRepo.findMaxByEventId(eventId.get().getId());
                                if (tieredchargesMax.isPresent()){
                                    partTran.setPartTranType("Credit");
                                    partTran.setTransactionAmount(tieredchargesCheck.get().getChargeAmount());
                                    partTran.setMonthlyFee(eventId.get().getMonthlyFee());
                                    partTran.setAcid(eventId.get().getAc_placeholder());
                                    partTran.setCurrency(eventId.get().getChrg_coll_crncy());
                                    partTran.setTransactionParticulars(eventId.get().getTran_particulars());
                                }else {
                                    partTran.setPartTranType("Credit");
                                    partTran.setTransactionAmount(0.00);
                                    partTran.setMonthlyFee(eventId.get().getMonthlyFee());
                                    partTran.setAcid(eventId.get().getAc_placeholder());
                                    partTran.setCurrency(eventId.get().getChrg_coll_crncy());
                                    partTran.setTransactionParticulars(eventId.get().getTran_particulars());
                                }

                            }

                        }
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(partTran);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("No Data Present!");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
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

    @GetMapping("/find/{id}")
    public ResponseEntity<?> getEventIdById(@PathVariable("id") Long id) {
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
                    EventId eventId = eventIdService.findById(id);
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(eventId);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("find/eventId/code/{code}")
    public ResponseEntity<?> findEventIdByCode(@PathVariable("code") String eventIdCode) {
        try {
            EntityResponse response = new EntityResponse();
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                Optional<EventId> searchCode = eventIdRepo.findByEntityIdAndEventIdCode(EntityRequestContext.getCurrentEntityId(), eventIdCode);
                if (searchCode.isPresent()) {
                    Optional<EventId> eventId = eventIdRepo.findByEventIdCode(eventIdCode);
                    response.setMessage("EVENT ID WITH CODE " + eventIdCode + " ALREADY REGISTERED");
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(eventId);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    response.setMessage("EVENT ID WITH CODE " + eventIdCode + " AVAILABLE FOR REGISTRATION");
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

    @PutMapping("/modify")
    public ResponseEntity<?> updateEventId(@RequestBody EventId eventId) {
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
                    eventId.setModifiedBy(UserRequestContext.getCurrentUser());
                    eventId.setEntityId(EntityRequestContext.getCurrentEntityId());
                    Optional<EventId> eventId1 = eventIdRepo.findById(eventId.getId());
                    if (eventId1.isPresent()) {
                        eventId.setPostedTime(eventId1.get().getPostedTime());
                        eventId.setPostedFlag(eventId1.get().getPostedFlag());
                        eventId.setPostedBy(eventId1.get().getPostedBy());
                        eventId.setModifiedFlag('Y');
                        eventId.setVerifiedFlag(eventId1.get().getVerifiedFlag());
                        eventId.setModifiedTime(new Date());
                        eventId.setModifiedBy(eventId.getModifiedBy());
                        eventIdService.updateEventId(eventId);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("EVENT ID  WITH CODE " + eventId.getEventIdCode() + " MODIFIED SUCCESSFULLY AT " + eventId.getModifiedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(eventId);
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
                    Optional<EventId> eventId1 = eventIdRepo.findById(Long.parseLong(id));
                    if (eventId1.isPresent()) {
                        EventId eventId = eventId1.get();
                        if (eventId.getPostedBy().equalsIgnoreCase(UserRequestContext.getCurrentUser())) {
                            EntityResponse response = new EntityResponse();
                            response.setMessage("You Can Not Verify What you initiated");
                            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                            response.setEntity("");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        } else {
                            if (eventId.getVerifiedFlag().equals('Y')) {
                                EntityResponse response = new EntityResponse();
                                response.setMessage("EVENT ID  WITH CODE " + eventId.getEventIdCode() + " ALREADY VERIFIED");
                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                response.setEntity("");
                                return new ResponseEntity<>(response, HttpStatus.OK);
                            } else {
                                eventId.setVerifiedFlag('Y');
                                eventId.setVerifiedTime(new Date());
                                eventId.setVerifiedBy(UserRequestContext.getCurrentUser());
                                eventIdRepo.save(eventId);
                                EntityResponse response = new EntityResponse();
                                response.setMessage("EVENT ID  WITH CODE " + eventId.getEventIdCode() + " VERIFIED SUCCESSFULLY AT " + eventId.getVerifiedTime());
                                response.setStatusCode(HttpStatus.OK.value());
                                response.setEntity(eventId);
                                return new ResponseEntity<>(response, HttpStatus.OK);
                            }
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
    public ResponseEntity<?> deleteEventId(@PathVariable Long id) {
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
                    Optional<EventId> eventId1 = eventIdRepo.findById(id);
                    if (eventId1.isPresent()) {
                        EventId eventId = eventId1.get();
                        eventId.setDeletedFlag('Y');
                        eventId.setDeletedTime(new Date());
                        eventId.setDeletedBy(UserRequestContext.getCurrentUser());
                        eventIdRepo.save(eventId);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("EVENT ID  WITH CODE " + eventId.getEventIdCode() + " DELETED SUCCESSFULLY AT " + eventId.getDeletedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(eventId);
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
