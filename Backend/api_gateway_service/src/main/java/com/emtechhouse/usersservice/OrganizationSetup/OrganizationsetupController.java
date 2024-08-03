package com.emtechhouse.usersservice.OrganizationSetup;

import com.emtechhouse.usersservice.utils.EntityResponse;
import com.emtechhouse.usersservice.utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.usersservice.utils.HttpInterceptor.UserRequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/v1/organizationsetup")
@Slf4j
public class OrganizationsetupController {
    private final OrganizationsetupRepo organizationsetupRepo;
    private final OrganizationService organizationService;

    public OrganizationsetupController(OrganizationsetupRepo organizationsetupRepo, OrganizationService organizationService) {
        this.organizationsetupRepo = organizationsetupRepo;
        this.organizationService = organizationService;
    }


    @PostMapping("/add")
    public ResponseEntity<?> addEntity(@RequestBody Organizationsetup organizationsetup) {
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
//                    Check if code exist
                    List<Organizationsetup> entity1 = organizationsetupRepo.findByDeletedFlag('N');
                    if (entity1.size()>0){
                        EntityResponse response = new EntityResponse();
                        response.setMessage("Code Exists");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }else{
                        organizationsetup.setPostedBy(UserRequestContext.getCurrentUser());
                        organizationsetup.setPostedFlag('Y');
                        organizationsetup.setPostedTime(new Date());

                        Organizationsetup newEntityGroup = organizationService.addEntity(organizationsetup);
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.CREATED.getReasonPhrase());
                        response.setStatusCode(HttpStatus.CREATED.value());
                        response.setEntity(newEntityGroup);
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
    public ResponseEntity<?> getAllEntitys() {
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
                    List<Organizationsetup> organizationsetup = organizationsetupRepo.findByDeletedFlag('N');
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(organizationsetup);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<?> getEntityById(@PathVariable("id") Long id) {
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
                    Organizationsetup organizationsetup = organizationService.findById(id);
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(organizationsetup);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("/modify")
    public ResponseEntity<?> updateEntity(@RequestBody Organizationsetup organizationsetup) {
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
                    organizationsetup.setModifiedBy(UserRequestContext.getCurrentUser());
                    Optional<Organizationsetup> entity1 = organizationsetupRepo.findById(organizationsetup.getId());
                    if (entity1.isPresent()) {
                        organizationsetup.setPostedTime(entity1.get().getPostedTime());
                        organizationsetup.setPostedFlag('Y');
                        organizationsetup.setPostedBy(entity1.get().getPostedBy());
                        organizationsetup.setModifiedFlag('Y');
                        organizationsetup.setVerifiedFlag('N');
                        organizationsetup.setModifiedTime(new Date());
                        organizationsetup.setModifiedBy(organizationsetup.getModifiedBy());
                        organizationService.updateEntity(organizationsetup);
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(organizationsetup);
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
                    Optional<Organizationsetup> entity1 = organizationsetupRepo.findById(Long.parseLong(id));
                    if (entity1.isPresent()) {
                        Organizationsetup organizationsetup = entity1.get();
                        //                    Check Maker Checker
                        if (organizationsetup.getPostedBy().equalsIgnoreCase(UserRequestContext.getCurrentUser())){
                            EntityResponse response = new EntityResponse();
                            response.setMessage("You Can Not Verify What you initiated");
                            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                            response.setEntity("");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        }else{
                            organizationsetup.setVerifiedFlag('Y');
                            organizationsetup.setVerifiedTime(new Date());
                            organizationsetup.setVerifiedBy(UserRequestContext.getCurrentUser());
                            organizationsetupRepo.save(organizationsetup);
                            EntityResponse response = new EntityResponse();
                            response.setMessage(HttpStatus.OK.getReasonPhrase());
                            response.setStatusCode(HttpStatus.OK.value());
                            response.setEntity(organizationsetup);
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
    public ResponseEntity<?> deleteEntity(@PathVariable String id) {
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
                    Optional<Organizationsetup> entity1 = organizationsetupRepo.findById(Long.parseLong(id));
                    if (entity1.isPresent()) {
                        Organizationsetup organizationsetup = entity1.get();
                        organizationsetup.setDeletedFlag('Y');
                        organizationsetup.setDeletedTime(new Date());
                        organizationsetup.setDeletedBy(UserRequestContext.getCurrentUser());
                        organizationsetupRepo.save(organizationsetup);
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(organizationsetup);
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
