package com.emtechhouse.System.HolidayParams;

import com.emtechhouse.System.Utils.EntityResponse;
import com.emtechhouse.System.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.System.Utils.HttpInterceptor.UserRequestContext;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@Api(value = "/Holiday API", tags = "Holiday API")
@RequestMapping("/api/v1/holiday/params")
public class HolidayparamsController {
    private final HolidayparamsRepo holidayparamsRepo;
    private final HolidayparamsService holidayparamsService;

    public HolidayparamsController(HolidayparamsRepo holidayparamsRepo, HolidayparamsService holidayparamsService) {
        this.holidayparamsRepo = holidayparamsRepo;
        this.holidayparamsService = holidayparamsService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addHolidayparams(@RequestBody Holidayparams holidayparams) {
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
                    Optional<Holidayparams> holidayparams1 = holidayparamsRepo.findByEntityIdAndHolidayCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), holidayparams.getHolidayCode(), 'N');
                    if (holidayparams1.isPresent()) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("HOLIDAY WITH CODE " + holidayparams1.get().getHolidayCode() + " ALREADY REGISTERED ON " + holidayparams1.get().getPostedTime());
                        response.setEntity("");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        holidayparams.setPostedBy(UserRequestContext.getCurrentUser());
                        holidayparams.setEntityId(EntityRequestContext.getCurrentEntityId());
                        holidayparams.setPostedFlag('Y');
                        holidayparams.setPostedTime(new Date());
                        Holidayparams newHolidayparams = holidayparamsService.addHolidayparams(holidayparams);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("HOLIDAY WITH CODE " + holidayparams.getHolidayCode() + " AND NAME " + holidayparams.getHolidayName() + " CREATED SUCCESSFULLY AT " + holidayparams.getPostedTime());
                        response.setStatusCode(HttpStatus.CREATED.value());
                        response.setEntity(newHolidayparams);
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
    public ResponseEntity<?> getAllHolidayparamss() {
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
                    List<Holidayparams> holidayparams = holidayparamsRepo.findByEntityIdAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), 'N');
                    if (holidayparams.size() > 0) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(holidayparams);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity(holidayparams);
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
    public ResponseEntity<?> getHolidayparamsById(@PathVariable("id") Long id) {
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
                    Holidayparams holidayparams = holidayparamsService.findById(id);
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(holidayparams);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/find/holiday/code/{code}")
    public ResponseEntity<?> getMissubsectorByCode(@PathVariable("code") String holidayCode) {
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

                    Optional<Holidayparams> searchCode = holidayparamsRepo.findByEntityIdAndHolidayCode(EntityRequestContext.getCurrentEntityId(), holidayCode);
                    if (searchCode.isPresent()) {
                        EntityResponse response = new EntityResponse();
                        Optional<Holidayparams> holidayparams = holidayparamsRepo.findByHolidayCode(holidayCode);
                        response.setMessage("HOLIDAY WITH CODE " + holidayCode + " ALREADY REGISTERED");
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(holidayparams);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("HOLIDAY WITH CODE " + holidayCode + " AVAILABLE FOR REGISTRATION");
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

    @PutMapping("/modify")
    public ResponseEntity<?> updateHolidayparams(@RequestBody Holidayparams holidayparams) {
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
                    holidayparams.setModifiedBy(UserRequestContext.getCurrentUser());
                    holidayparams.setEntityId(EntityRequestContext.getCurrentEntityId());
                    Optional<Holidayparams> holidayparams1 = holidayparamsRepo.findById(holidayparams.getId());
                    if (holidayparams1.isPresent()) {
                        holidayparams.setPostedTime(holidayparams1.get().getPostedTime());
                        holidayparams.setPostedFlag(holidayparams1.get().getPostedFlag());
                        holidayparams.setPostedBy(holidayparams1.get().getPostedBy());
                        holidayparams.setModifiedFlag('Y');
                        holidayparams.setVerifiedFlag(holidayparams1.get().getVerifiedFlag());
                        holidayparams.setModifiedTime(new Date());
                        holidayparams.setModifiedBy(holidayparams.getModifiedBy());
                        holidayparamsService.updateHolidayparams(holidayparams);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("HOLIDAY WITH CODE " + holidayparams.getHolidayCode() + " AND NAME " + holidayparams.getHolidayName() + " MODIFIED SUCCESSFULLY AT " + holidayparams.getModifiedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(holidayparams);
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
                    Optional<Holidayparams> holidayparams1 = holidayparamsRepo.findById(Long.parseLong(id));
                    if (holidayparams1.isPresent()) {
                        Holidayparams holidayparams = holidayparams1.get();
                        if (holidayparams.getPostedBy().equalsIgnoreCase(UserRequestContext.getCurrentUser())) {
                            EntityResponse response = new EntityResponse();
                            response.setMessage("You Can Not Verify What you initiated");
                            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                            response.setEntity("");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        } else {
                            if (holidayparams.getVerifiedFlag().equals('Y')) {
                                EntityResponse response = new EntityResponse();
                                response.setMessage("HOLIDAY WITH CODE " + holidayparams.getHolidayCode() + " AND NAME " + holidayparams.getHolidayName() + " ALREADY VERIFIED SUCCESSFULLY AT");
                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                response.setEntity("");
                                return new ResponseEntity<>(response, HttpStatus.OK);
                            } else {
                                holidayparams.setVerifiedFlag('Y');
                                holidayparams.setVerifiedTime(new Date());
                                holidayparams.setVerifiedBy(UserRequestContext.getCurrentUser());
                                holidayparamsRepo.save(holidayparams);
                                EntityResponse response = new EntityResponse();
                                response.setMessage("HOLIDAY WITH CODE " + holidayparams.getHolidayCode() + " AND NAME " + holidayparams.getHolidayName() + " VERIFIED SUCCESSFULLY AT " + holidayparams.getVerifiedTime());
                                response.setStatusCode(HttpStatus.OK.value());
                                response.setEntity(holidayparams);
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
    public ResponseEntity<?> deleteHolidayparams(@PathVariable String id) {
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
                    Optional<Holidayparams> holidayparams1 = holidayparamsRepo.findById(Long.parseLong(id));
                    if (holidayparams1.isPresent()) {
                        Holidayparams holidayparams = holidayparams1.get();
                        holidayparams.setDeletedFlag('Y');
                        holidayparams.setDeletedTime(new Date());
                        holidayparams.setDeletedBy(UserRequestContext.getCurrentUser());
                        holidayparamsRepo.save(holidayparams);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("HOLIDAY WITH CODE " + holidayparams.getHolidayCode() + " AND NAME " + holidayparams.getHolidayName() + " DELETED SUCCESSFULLY AT " + holidayparams.getDeletedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(holidayparams);
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