package com.emtechhouse.System.ExceptionCode;

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
@Api(value = "/Exceptioncode API", tags = "Exceptioncode API")
@RequestMapping("/api/v1/parameters/configurations/static/params/exceptioncode/")
public class ExceptioncodeController {
    @Autowired
    private ExceptioncodeRepo exceptioncodeRepo;
    @Autowired
    private ExceptioncodeService exceptioncodeService;

    @PostMapping("/add")
    public ResponseEntity<?> addExceptioncode(@RequestBody Exceptioncode exceptioncode) {
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
                    Optional<Exceptioncode> checkExceptioncode = exceptioncodeRepo.findByEntityIdAndExceptionCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), exceptioncode.getExceptionCode(), 'N');
                    if (checkExceptioncode.isPresent()) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("Code already exists!");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        exceptioncode.setPostedBy(UserRequestContext.getCurrentUser());
                        exceptioncode.setEntityId(EntityRequestContext.getCurrentEntityId());
                        exceptioncode.setPostedFlag('Y');
                        exceptioncode.setPostedTime(new Date());
                        exceptioncodeService.addExceptioncode(exceptioncode);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("EXCEPTION WITH CODE " + exceptioncode.getExceptionCode() + " AND CODE TYPE " + exceptioncode.getExce_code_type() + " CREATED SUCCESSFULLY AT " + exceptioncode.getPostedTime());
                        response.setStatusCode(HttpStatus.CREATED.value());
                        response.setEntity(exceptioncode);
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
    public ResponseEntity<?> getAllExceptioncodes() {
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
                    List<Exceptioncode> exceptioncode = exceptioncodeRepo.findAllByEntityIdAndDeletedFlagOrderByIdDesc(EntityRequestContext.getCurrentEntityId(), 'N');
                    if (exceptioncode.size() > 0) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(exceptioncode);
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

    @GetMapping("/find/{id}")
    public ResponseEntity<?> getExceptioncodeById(@PathVariable("id") Long id) {
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
                    Exceptioncode exceptioncode = exceptioncodeService.findById(id);
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(exceptioncode);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/find/exception/code/{code}")
    public ResponseEntity<?> findExeptionByCode(@PathVariable("code") String exceptionCode) {
        try {
            EntityResponse response = new EntityResponse();
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                Optional<Exceptioncode> searchCode = exceptioncodeRepo.findByEntityIdAndExceptionCode(EntityRequestContext.getCurrentEntityId(), exceptionCode);
                if (searchCode.isPresent()) {
                    Optional<Exceptioncode> exception = exceptioncodeRepo.findByExceptionCode(exceptionCode);
                    response.setMessage("EXCEPTION WITH CODE " + exceptionCode + " ALREADY REGISTERED");
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(exception);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    response.setMessage("EXCEPTION WITH CODE " + exceptionCode + " AVAILABLE FOR REGISTRATION");
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
    public ResponseEntity<?> updateExceptioncode(@RequestBody Exceptioncode exceptioncode) {
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
                    exceptioncode.setModifiedBy(UserRequestContext.getCurrentUser());
                    exceptioncode.setEntityId(EntityRequestContext.getCurrentEntityId());
                    Optional<Exceptioncode> exceptioncode1 = exceptioncodeRepo.findById(exceptioncode.getId());
                    if (exceptioncode1.isPresent()) {
                        exceptioncode.setPostedTime(exceptioncode1.get().getPostedTime());
                        exceptioncode.setPostedFlag(exceptioncode1.get().getPostedFlag());
                        exceptioncode.setPostedBy(exceptioncode1.get().getPostedBy());
                        exceptioncode.setModifiedFlag('Y');
                        exceptioncode.setVerifiedFlag(exceptioncode1.get().getVerifiedFlag());
                        exceptioncode.setModifiedTime(new Date());
                        exceptioncode.setModifiedBy(exceptioncode.getModifiedBy());
                        exceptioncodeService.updateExceptioncode(exceptioncode);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("EXCEPTION WITH CODE " + exceptioncode.getExceptionCode() + " AND CODE TYPE " + exceptioncode.getExce_code_type() + " MODIFIED SUCCESSFULLY AT " + exceptioncode.getModifiedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(exceptioncode);
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
                    Optional<Exceptioncode> exceptioncode1 = exceptioncodeRepo.findById(Long.parseLong(id));
                    if (exceptioncode1.isPresent()) {
                        Exceptioncode exceptioncode = exceptioncode1.get();
                        if (exceptioncode.getPostedBy().equalsIgnoreCase(UserRequestContext.getCurrentUser())) {
                            EntityResponse response = new EntityResponse();
                            response.setMessage("You Can Not Verify What you initiated");
                            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                            response.setEntity("");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        } else {
                            if (exceptioncode.getVerifiedFlag().equals('Y')) {
                                EntityResponse response = new EntityResponse();
                                response.setEntity("EXCEPTION WITH CODE " + exceptioncode1.get().getExceptionCode() + " AND CODE TYPE " + exceptioncode1.get().getExce_code_type() + " ALREADY VERIFIED ON " + exceptioncode1.get().getVerifiedTime());
                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                response.setEntity("");
                                return new ResponseEntity<>(response, HttpStatus.OK);
                            } else {
                                exceptioncode.setVerifiedFlag('Y');
                                exceptioncode.setVerifiedTime(new Date());
                                exceptioncode.setVerifiedBy(UserRequestContext.getCurrentUser());
                                exceptioncodeRepo.save(exceptioncode);
                                EntityResponse response = new EntityResponse();
                                response.setMessage("EXCEPTION WITH CODE " + exceptioncode.getExceptionCode() + " AND CODE TYPE " + exceptioncode.getExce_code_type() + " VERIFIED SUCCESSFULLY AT " + exceptioncode.getVerifiedTime());
                                response.setStatusCode(HttpStatus.OK.value());
                                response.setEntity(exceptioncode);
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
    public ResponseEntity<?> deleteExceptioncode(@PathVariable Long id) {
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
                    Optional<Exceptioncode> exceptioncode1 = exceptioncodeRepo.findById(id);
                    if (exceptioncode1.isPresent()) {
                        Exceptioncode exceptioncode = exceptioncode1.get();
                        exceptioncode.setDeletedFlag('Y');
                        exceptioncode.setDeletedTime(new Date());
                        exceptioncode.setDeletedBy(UserRequestContext.getCurrentUser());
                        exceptioncodeRepo.save(exceptioncode);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("EXCEPTION WITH CODE " + exceptioncode.getExceptionCode() + " AND CODE TYPE " + exceptioncode.getExce_code_type() + " DELETED SUCCESSFULLY ON " + exceptioncode.getDeletedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(exceptioncode);
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
