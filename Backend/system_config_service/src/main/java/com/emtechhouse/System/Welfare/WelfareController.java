package com.emtechhouse.System.Welfare;

import com.emtechhouse.System.Utils.EntityResponse;
import com.emtechhouse.System.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.System.Utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.System.Welfare.WelfareAction.WelfareAction;
import com.emtechhouse.System.Welfare.WelfareAction.WelfareActionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RequestMapping("welfare")
@RestController
@Slf4j
public class WelfareController {
    private final WelfareService welfareService;
    private final WelfareRepository welfareRepository;
    private final WelfareActionRepository welfareActionRepository;

    public WelfareController(WelfareService welfareService, WelfareRepository welfareRepository, WelfareActionRepository welfareActionRepository) {
        this.welfareService = welfareService;
        this.welfareRepository = welfareRepository;
        this.welfareActionRepository = welfareActionRepository;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addWelfare(@RequestBody Welfare welfare) {
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
                    welfare.setPostedBy(UserRequestContext.getCurrentUser());
                    welfare.setEntityId(EntityRequestContext.getCurrentEntityId());
                    Optional<Welfare> welfare1 = welfareRepository.findByEntityIdAndWelfareCodeAndDeletedFlag(welfare.getEntityId(), welfare.getWelfareCode(), 'N');
                    if (welfare1.isPresent()) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("Welfare with Code " + welfare.getWelfareCode() + " Already Created On " + welfare1.get().getPostedTime());
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        welfare.setPostedFlag('Y');
                        welfare.setPostedTime(new Date());
                        Welfare newWelfare = welfareService.addWelfare(welfare);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("Welfare with Code " + welfare.getWelfareCode() + " Created Successfully " + welfare.getPostedTime());
                        response.setStatusCode(HttpStatus.CREATED.value());
                        response.setEntity(newWelfare);
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
    public ResponseEntity<?> getAllWelfare() {
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
                    List<Welfare> welfare = welfareRepository.findByEntityIdAndDeletedFlagOrderByIdDesc(EntityRequestContext.getCurrentEntityId(), 'N');
                    if (welfare.size() > 0) {
                        response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.TEMPORARY_REDIRECT.value());
                        response.setEntity(welfare);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
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

    @GetMapping("/all/verified/welfare")
    public ResponseEntity<?> getAllVerifiedWelfare() {
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
                    List<Welfare> welfare = welfareRepository.findByEntityIdAndDeletedFlagAndVerifiedFlagOrderByIdDesc(EntityRequestContext.getCurrentEntityId(), 'N', 'Y');
                    if (welfare.size() > 0) {
                        response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.TEMPORARY_REDIRECT.value());
                        response.setEntity(welfare);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
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
    public ResponseEntity<?> getWelfareById(@PathVariable("id") Long id) {
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
                    Welfare welfare = welfareService.findById(id);
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(welfare);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/code/find/{code}")
    public ResponseEntity<?> getWelfareByCode(@PathVariable("code") String welfareCode) {
        try {
            EntityResponse response = new EntityResponse();
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                Optional<Welfare> searchCode = welfareRepository.findByEntityIdAndWelfareCode(EntityRequestContext.getCurrentEntityId(), welfareCode);
                if (searchCode.isPresent()) {
                    Optional<Welfare> welfare = welfareRepository.findByWelfareCode(welfareCode);
                    response.setMessage("Welfare  WITH CODE " + welfareCode + " ALREADY REGISTERED");
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(welfare);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    response.setMessage("Welfare WITH CODE " + welfareCode + " AVAILABLE FOR REGISTRATION");
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
    public ResponseEntity<?> updateWelfare(@RequestBody Welfare welfare) {
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
                    welfare.setModifiedBy(UserRequestContext.getCurrentUser());
                    welfare.setEntityId(EntityRequestContext.getCurrentEntityId());
                    Optional<Welfare> welfare1 = welfareRepository.findByEntityIdAndWelfareCodeAndDeletedFlag(welfare.getEntityId(), welfare.getWelfareCode(), 'N');
                    if (welfare1.isPresent()) {
                        welfare.setPostedTime(welfare1.get().getPostedTime());
                        welfare.setPostedFlag('Y');
                        welfare.setPostedBy(welfare1.get().getPostedBy());
                        welfare.setModifiedFlag('Y');
                        welfare.setVerifiedFlag('N');
                        welfare.setModifiedTime(new Date());
                        welfare.setModifiedBy(welfare.getModifiedBy());
                        welfareService.updateWelfare(welfare);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("Welfare  WITH CODE " + welfare.getWelfareCode() + " MODIFIED SUCCESSFULLY AT " + welfare.getModifiedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(welfare);
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
    public ResponseEntity<?> verify(@PathVariable Long id) {
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
                    Optional<Welfare> welfare = welfareRepository.findById(id);
                    if (welfare.isPresent()) {
                        Welfare welfare1 = welfare.get();
                        if (welfare1.getPostedBy().equalsIgnoreCase(UserRequestContext.getCurrentUser())) {
                            EntityResponse response = new EntityResponse();
                            response.setMessage("You Can Not Verify What you initiated");
                            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                            response.setEntity("");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        } else {
                            if (welfare1.getVerifiedFlag().equals('Y')) {
                                EntityResponse response = new EntityResponse();
                                response.setMessage("Welfare with Code " + welfare.get().getWelfareCode() + " Already Verified On " + welfare.get().getVerifiedTime());
                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                response.setEntity("");
                                return new ResponseEntity<>(response, HttpStatus.OK);
                            } else {
                                welfare1.setVerifiedFlag('Y');
                                welfare1.setVerifiedTime(new Date());
                                welfare1.setVerifiedBy(UserRequestContext.getCurrentUser());
                                welfareRepository.save(welfare1);
                                EntityResponse response = new EntityResponse();
                                response.setMessage("Welfare with Code " + welfare.get().getWelfareCode() + " Verified Successfully At " + welfare1.getVerifiedTime());
                                response.setStatusCode(HttpStatus.OK.value());
                                response.setEntity(welfare1);
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
    public ResponseEntity<?> deleteWelfare(@PathVariable String id) {
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
                    Optional<Welfare> welfare = welfareRepository.findById(Long.parseLong(id));
                    if (welfare.isPresent()) {
                        Welfare welfare1 = welfare.get();
                        welfare1.setDeletedFlag('Y');
                        welfare1.setDeletedTime(new Date());
                        welfare1.setDeletedBy(UserRequestContext.getCurrentUser());
                        welfareRepository.save(welfare1);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("Welfare  WITH CODE " + welfare.get().getWelfareCode() + " DELETED SUCCESSFULLY AT " + welfare.get().getDeletedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(welfare1);
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
