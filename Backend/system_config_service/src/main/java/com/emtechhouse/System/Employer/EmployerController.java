package com.emtechhouse.System.Employer;


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
//@CrossOrigin
@RequestMapping("employer")
public class EmployerController {
    private final EmployerService employerService;
    private final EmployerRepo employerRepo;

    public EmployerController(EmployerService employerService, EmployerRepo employerRepo) {
        this.employerService = employerService;
        this.employerRepo = employerRepo;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addEmployer(@RequestBody Employer employer) {
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
                    Optional<Employer> employer1 = employerRepo.findByCode(employer.getEmployerCode());
                    if (employer1.isPresent()) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("Employer WITH TYPE CODE " + employer.getEmployerCode() + " ALREADY REGISTERED! ENTER UNIQUE TYPE CODE TO CONTINUE");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        employer.setPostedBy(UserRequestContext.getCurrentUser());
                        employer.setEntityId(EntityRequestContext.getCurrentEntityId());
                        employer.setPostedFlag('Y');
                        employer.setPostedTime(new Date());
                        Employer addEmployer = employerService.addEmployer(employer);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("Employer TYPE WITH CODE " + employer.getEmployerCode() + " AND TYPE " + employer.getEmployerCode() + " CREATED SUCCESSFULLY " + employer.getPostedTime());
                        response.setStatusCode(HttpStatus.CREATED.value());
                        response.setEntity(addEmployer);
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
    public ResponseEntity<?> getAllEmployers() {
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
                    List<Employer> employer = employerRepo.findAllByEntityIdAndDeletedFlagOrderByIdDesc(EntityRequestContext.getCurrentEntityId(), 'N');
                    if (employer.size() > 0) {
                        response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(employer);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity(employer);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/check/{employerCode}")
    public ResponseEntity<?> findByCode(@PathVariable("employerCode") String employerCode) {
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
                    Optional<Employer> searchemployerCode = employerRepo.findByCode(employerCode);
                    if (!searchemployerCode.isPresent()) {
                        response.setMessage("Employer TYPE WITH CODE " + employerCode + " AVAILABLE FOR REGISTRATION");
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity("");

                    } else {
                        Employer employer = employerService.findByCode(employerCode);
                        response.setMessage("Employer TYPE WITH CODE " + employerCode + " ALREADY REGISTERED");
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(employer);
                    }
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<?> getEmployerCodeById(@PathVariable("id") Long id) {
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

                    Optional<Employer> searchEmployerId = employerRepo.findById(id);
                    if (!searchEmployerId.isPresent()) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("Employer TYPE CODE DOES NOT EXIST!! PLEASE PROVIDE A VALID REGISTERED Employer TYPE CODE");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);

                    } else {
                        Employer employer = employerService.findById(id);
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(employer);
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
    public ResponseEntity<?> updateEmployer(@RequestBody Employer employer) {
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
                    employer.setModifiedBy(UserRequestContext.getCurrentUser());
                    employer.setEntityId(EntityRequestContext.getCurrentEntityId());
                    Optional<Employer> employer1 = employerRepo.findById(employer.getId());
                    if (employer1.isPresent()) {
                        employer.setPostedTime(employer1.get().getPostedTime());
                        employer.setPostedFlag(employer1.get().getPostedFlag());
                        employer.setPostedBy(employer1.get().getPostedBy());
                        employer.setVerifiedFlag(employer1.get().getVerifiedFlag());
                        employer.setModifiedFlag('Y');
                        employer.setModifiedTime(new Date());
                        employer.setModifiedBy(employer.getModifiedBy());
                        employerService.updateEmployer(employer);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("Employer TYPE WITH CODE" + " " + employer.getEmployerCode() + " " + "MODIFIED SUCCESSFULLY" + " AT " + employer.getModifiedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(employer);
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
                    Optional<Employer> employer1 = employerRepo.findById(Long.parseLong(id));
                    if (employer1.isPresent()) {
                        Employer employer = employer1.get();
                        if (employer.getPostedBy().equalsIgnoreCase(UserRequestContext.getCurrentUser())) {
                            EntityResponse response = new EntityResponse();
                            response.setMessage("You Can Not Verify What you initiated");
                            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                            response.setEntity("");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        } else {
                            if (employer1.get().getVerifiedFlag().equals('Y')){
                                EntityResponse response = new EntityResponse();
                                response.setMessage("Employer TYPE WITH CODE " + employer.getEmployerCode() + " ALREADY VERIFIED ON " + employer1.get().getVerifiedTime());
                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                response.setEntity(employer);
                                return new ResponseEntity<>(response, HttpStatus.OK);
                            } else {
                                employer.setVerifiedFlag('Y');
                                employer.setVerifiedTime(new Date());
                                employer.setVerifiedBy(UserRequestContext.getCurrentUser());
                                employerRepo.save(employer);
                                EntityResponse response = new EntityResponse();
                                response.setMessage("Employer TYPE WITH CODE" + " " + employer.getEmployerCode() + " " + "VERIFIED SUCCESSFULLY" + " AT " + employer.getVerifiedTime());
                                response.setStatusCode(HttpStatus.OK.value());
                                response.setEntity(employer);
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
    public ResponseEntity<?> deleteEmployer(@PathVariable String id) {
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
                    Optional<Employer> employer1 = employerRepo.findById(Long.parseLong(id));
                    if (employer1.isPresent()) {
                        Employer employer = employer1.get();
                        employer.setDeletedFlag('Y');
                        employer.setDeletedTime(new Date());
                        employer.setDeletedBy(UserRequestContext.getCurrentUser());
                        employerRepo.save(employer);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("Employer TYPE WITH REF CODE " + employer.getEmployerCode() + " DELETED SUCCESSFULLY AT "+ employer.getDeletedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(employer);
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
