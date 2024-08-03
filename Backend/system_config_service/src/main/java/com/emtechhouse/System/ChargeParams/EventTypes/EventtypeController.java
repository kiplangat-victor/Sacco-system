package com.emtechhouse.System.ChargeParams.EventTypes;

import com.emtechhouse.System.Utils.EntityResponse;
import com.emtechhouse.System.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.System.Utils.HttpInterceptor.UserRequestContext;
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
@Api(value = "/Event Type API", tags = "Event Type API")
@RequestMapping("/api/v1/parameters/configurations/event_type/")
public class EventtypeController {
    @Autowired
    private EventtypeRepo eventtypeRepo;
    @Autowired
    private EventtypeService eventtypeService;

    @PostMapping("/add")
    public ResponseEntity<?> addEventtype(@RequestBody Eventtype eventtype) {
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
                    Optional<Eventtype> checkEventtype = eventtypeRepo.findByEntityIdAndEventTypeCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), eventtype.getEventTypeCode(), 'N');
                    if (checkEventtype.isPresent()) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("Code already exists!");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        eventtype.setPostedBy(UserRequestContext.getCurrentUser());
                        eventtype.setEntityId(EntityRequestContext.getCurrentEntityId());
                        eventtype.setPostedFlag('Y');
                        eventtype.setPostedTime(new Date());

                        Eventtype newEventtype = eventtypeService.addEventtype(eventtype);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("EVENT TYPE WITH CODE " + eventtype.getEventTypeCode() + " AND NAME " + eventtype.getEventTypeName() + " CREATED SUCCESSFULLY AT " + eventtype.getPostedTime());
                        response.setStatusCode(HttpStatus.CREATED.value());
                        response.setEntity(newEventtype);
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
    public ResponseEntity<?> getAllEventtypes() {
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
                    List<Eventtype> eventtype = eventtypeRepo.findAllByEntityIdAndDeletedFlagOrderByIdDesc(EntityRequestContext.getCurrentEntityId(), 'N');
                    if (eventtype.size() > 0) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(eventtype);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity(eventtype);
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
    public ResponseEntity<?> getEventtypeById(@PathVariable("id") Long id) {
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
                    Eventtype eventtype = eventtypeService.findById(id);
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(eventtype);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("find/eventtype/code/{code}")
    public ResponseEntity<?> findEventTypeByCode(@PathVariable("code") String eventTypeCode) {
        try {
            EntityResponse response = new EntityResponse();
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                Optional<Eventtype> searchCode = eventtypeRepo.findByEntityIdAndEventTypeCode(EntityRequestContext.getCurrentEntityId(), eventTypeCode);
                if (searchCode.isPresent()) {
                    Optional<Eventtype> eventId = eventtypeRepo.findByEventTypeCode(eventTypeCode);
                    response.setMessage("EVENT TYPE WITH CODE " + eventTypeCode + " ALREADY REGISTERED");
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(eventId);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    response.setMessage("EVENT TYPE WITH CODE " + eventTypeCode + " AVAILABLE FOR REGISTRATION");
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
    public ResponseEntity<?> updateEventtype(@RequestBody Eventtype eventtype) {
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
                    eventtype.setModifiedBy(UserRequestContext.getCurrentUser());
                    eventtype.setEntityId(EntityRequestContext.getCurrentEntityId());
                    Optional<Eventtype> eventtype1 = eventtypeRepo.findById(eventtype.getId());
                    if (eventtype1.isPresent()) {
                        eventtype.setPostedTime(eventtype1.get().getPostedTime());
                        eventtype.setPostedFlag(eventtype1.get().getPostedFlag());
                        eventtype.setPostedBy(eventtype1.get().getPostedBy());
                        eventtype.setModifiedFlag('Y');
                        eventtype.setVerifiedFlag(eventtype1.get().getVerifiedFlag());
                        eventtype.setModifiedTime(new Date());
                        eventtype.setModifiedBy(eventtype.getModifiedBy());
                        eventtypeService.updateEventtype(eventtype);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("EVENT TYPE WITH CODE " + eventtype.getEventTypeCode() + " AND NAME " + eventtype.getEventTypeName() + " MODIFIED SUCCESSFULLY AT " + eventtype.getModifiedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(eventtype);
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
                    Optional<Eventtype> eventtype1 = eventtypeRepo.findById(Long.parseLong(id));
                    if (eventtype1.isPresent()) {
                        Eventtype eventtype = eventtype1.get();
                        if (eventtype.getPostedBy().equalsIgnoreCase(UserRequestContext.getCurrentUser())) {
                            EntityResponse response = new EntityResponse();
                            response.setMessage("You Can Not Verify What you initiated");
                            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                            response.setEntity("");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        } else {
                            if (eventtype.getVerifiedFlag().equals('Y')) {
                                EntityResponse response = new EntityResponse();
                                response.setMessage("EVENT TYPE WITH CODE " + eventtype.getEventTypeCode() + " AND NAME " + eventtype.getEventTypeName() + " ALREADY VERIFIED");
                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                response.setEntity("");
                                return new ResponseEntity<>(response, HttpStatus.OK);
                            } else {
                                eventtype.setVerifiedFlag('Y');
                                eventtype.setVerifiedTime(new Date());
                                eventtype.setVerifiedBy(UserRequestContext.getCurrentUser());
                                eventtypeRepo.save(eventtype);
                                EntityResponse response = new EntityResponse();
                                response.setMessage("EVENT TYPE WITH CODE " + eventtype.getEventTypeCode() + " AND NAME " + eventtype.getEventTypeName() + " VERIFIED SUCCESSFULLY AT " + eventtype.getVerifiedTime());
                                response.setStatusCode(HttpStatus.OK.value());
                                response.setEntity(eventtype);
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
    public ResponseEntity<?> deleteEventtype(@PathVariable Long id) {
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
                    Optional<Eventtype> eventtype1 = eventtypeRepo.findById(id);
                    if (eventtype1.isPresent()) {
                        Eventtype eventtype = eventtype1.get();
                        eventtype.setDeletedFlag('Y');
                        eventtype.setDeletedTime(new Date());
                        eventtype.setDeletedBy(UserRequestContext.getCurrentUser());
                        eventtypeRepo.save(eventtype);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("EVENT TYPE WITH CODE " + eventtype.getEventTypeCode() + " AND NAME " + eventtype.getEventTypeName() + " DELETED SUCCESSFULLY AT " + eventtype.getDeletedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(eventtype);
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
