package com.emtechhouse.System.LinkedOrganization;

import com.emtechhouse.System.LinkedOrganization.OrganizationCharges.OrganizationeventidRepo;
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

@RestController
@Slf4j
@Api(value = "/Linked Organization API", tags = "Linked Organization API")
@RequestMapping("/api/v1/organization/")
public class LinkedorganizationController {
    @Autowired
    private OrganizationeventidRepo organizationeventidRepo;
    @Autowired
    private LinkedorganizationRepo linkedorganizationRepo;
    @Autowired
    private LinkedorganizationService linkedorganizationService;

    @PostMapping("/add")
    public ResponseEntity<?> addLinkedorganization(@RequestBody Linkedorganization linkedorganization) {
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
                    Optional<Linkedorganization> checkLinkedorganization = linkedorganizationRepo.findByEntityIdAndLinkedOrganizationCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), linkedorganization.getLinkedOrganizationCode(), 'N');
                    if (checkLinkedorganization.isPresent()) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("ORGANIZATION WITH CODE " + checkLinkedorganization.get().getLinkedOrganizationCode() + " AND NAME " + checkLinkedorganization.get().getOrganization_name() + " ALREADY REGISTERED");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        linkedorganization.setPostedBy(UserRequestContext.getCurrentUser());
                        linkedorganization.setEntityId(EntityRequestContext.getCurrentEntityId());
                        linkedorganization.setPostedFlag('Y');
                        linkedorganization.setPostedTime(new Date());

                        Linkedorganization newLinkedorganization = linkedorganizationService.addLinkedorganization(linkedorganization);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("ORGANIZATION WITH CODE " + linkedorganization.getLinkedOrganizationCode() + " AND NAME " + linkedorganization.getOrganization_name() + " CREATED SUCCESSFULLY AT " + linkedorganization.getPostedTime());
                        response.setStatusCode(HttpStatus.CREATED.value());
                        response.setEntity(newLinkedorganization);
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
    public ResponseEntity<?> getAllLinkedorganizations() {
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
                    List<Linkedorganization> linkedorganizations = linkedorganizationRepo.findByEntityIdAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), 'N');
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.FOUND.value());
                    response.setEntity(linkedorganizations);
                    return new ResponseEntity<>(response, HttpStatus.OK);

                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<?> getLinkedorganizationById(@PathVariable("id") Long id) {
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
                    Optional<Linkedorganization> searchOrg = linkedorganizationRepo.findByIdAndDeletedFlag(id, 'N');
                    if (searchOrg.isPresent()) {
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(searchOrg);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
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

    @GetMapping("/find/organization/code/{code}")
    public ResponseEntity<?> getLinkedorganizationByCode(@PathVariable("code") String linkedOrganizationCode) {
        try {
            EntityResponse response = new EntityResponse();
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                Optional<Linkedorganization> searchCode = linkedorganizationRepo.findByLinkedOrganizationCode(linkedOrganizationCode);
                if (!searchCode.isPresent()) {
                    response.setMessage("ORGANIZATION WITH CODE " + linkedOrganizationCode + " AVAILABLE FOR REGISTRATION");
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setEntity("");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    Linkedorganization linkedorganization = linkedorganizationService.findByLinkedOrganizationCode(linkedOrganizationCode);
                    response.setMessage("ORGANIZATION WITH CODE " + linkedOrganizationCode + " ALREADY REGISTERED");
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(linkedorganization);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("/modify")
    public ResponseEntity<?> updateLinkedorganization(@RequestBody Linkedorganization linkedorganization) {
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
                    linkedorganization.setModifiedBy(UserRequestContext.getCurrentUser());
                    linkedorganization.setEntityId(EntityRequestContext.getCurrentEntityId());
                    Optional<Linkedorganization> linkedorganization1 = linkedorganizationRepo.findById(linkedorganization.getId());
                    if (linkedorganization1.isPresent()) {
                        linkedorganization.setPostedTime(linkedorganization1.get().getPostedTime());
                        linkedorganization.setPostedFlag(linkedorganization1.get().getPostedFlag());
                        linkedorganization.setPostedBy(linkedorganization1.get().getPostedBy());
                        linkedorganization.setModifiedFlag('Y');
                        linkedorganization.setVerifiedFlag(linkedorganization1.get().getVerifiedFlag());
                        linkedorganization.setModifiedTime(new Date());
                        linkedorganization.setModifiedBy(linkedorganization.getModifiedBy());
                        linkedorganizationService.updateLinkedorganization(linkedorganization);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("ORGANIZATION WITH CODE " + linkedorganization.getLinkedOrganizationCode() + " AND NAME " + linkedorganization.getOrganization_name() + " MODIFIED SUCCESSFULLY AT " + linkedorganization.getModifiedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(linkedorganization);
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
                    Optional<Linkedorganization> linkedorganization1 = linkedorganizationRepo.findById(Long.parseLong(id));
                    if (linkedorganization1.isPresent()) {
                        Linkedorganization linkedorganization = linkedorganization1.get();
                        if (linkedorganization.getPostedBy().equalsIgnoreCase(UserRequestContext.getCurrentUser())) {
                            EntityResponse response = new EntityResponse();
                            response.setMessage("You Can Not Verify What you initiated");
                            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                            response.setEntity("");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        } else {
                            if (linkedorganization.getVerifiedFlag().equals('Y')) {
                                EntityResponse response = new EntityResponse();
                                response.setMessage("ORGANIZATION WITH CODE " + linkedorganization.getLinkedOrganizationCode() + " AND NAME " + linkedorganization.getOrganization_name() + " ALREADY VERIFIED SUCCESSFULLY");
                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                response.setEntity("");
                                return new ResponseEntity<>(response, HttpStatus.OK);
                            } else {
                                linkedorganization.setVerifiedFlag('Y');
                                linkedorganization.setVerifiedTime(new Date());
                                linkedorganization.setVerifiedBy(UserRequestContext.getCurrentUser());
                                linkedorganizationRepo.save(linkedorganization);
                                EntityResponse response = new EntityResponse();
                                response.setMessage("ORGANIZATION WITH CODE " + linkedorganization.getLinkedOrganizationCode() + " AND NAME " + linkedorganization.getOrganization_name() + " VERIFIED SUCCESSFULLY AT " + linkedorganization.getVerifiedTime());
                                response.setStatusCode(HttpStatus.OK.value());
                                response.setEntity(linkedorganization);
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

    @DeleteMapping("/delete{id}")
    public ResponseEntity<?> deleteLinkedorganization(@PathVariable String id) {
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
                    Optional<Linkedorganization> linkedorganization1 = linkedorganizationRepo.findById(Long.parseLong(id));
                    if (linkedorganization1.isPresent()) {
                        Linkedorganization linkedorganization = linkedorganization1.get();
                        linkedorganization.setDeletedFlag('Y');
                        linkedorganization.setDeletedTime(new Date());
                        linkedorganization.setDeletedBy(UserRequestContext.getCurrentUser());
                        linkedorganizationRepo.save(linkedorganization);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("ORGANIZATION WITH CODE " + linkedorganization.getLinkedOrganizationCode() + " AND NAME " + linkedorganization.getOrganization_name() + " DELETED SUCCESSFULLY AT " + linkedorganization.getDeletedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(linkedorganization);
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
