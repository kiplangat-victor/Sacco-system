package com.emtechhouse.System.MisSector;

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

//@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@Slf4j
@Api(value = "/Mis Sector API", tags = "Mis Sector API")
@RequestMapping("/api/v1/mis/sector")
public class MissectorController {
    private final MissectorService missectorService;

    private final MissectorRepo missectorRepo;

    public MissectorController(MissectorService missectorService, MissectorRepo missectorRepo) {
        this.missectorService = missectorService;
        this.missectorRepo = missectorRepo;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addMissector(@RequestBody Missector missector) {
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
                    Optional<Missector> checkMissector = missectorRepo.findByEntityIdAndMisCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), missector.getMisCode(), 'N');
                    if (checkMissector.isPresent()) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("MIS SECTOR WITH CODE " + checkMissector.get().getMisCode() + " AND NAME " + checkMissector.get().getMisSector() + " ALREADY CREATED ON " + checkMissector.get().getPostedTime());
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        missector.setPostedBy(UserRequestContext.getCurrentUser());
                        missector.setEntityId(EntityRequestContext.getCurrentEntityId());
                        missector.setPostedFlag('Y');
                        missector.setPostedTime(new Date());
                        Missector newMissector = missectorService.addMissector(missector);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("MIS SECTOR WITH CODE " + missector.getMisCode() + " AND NAME " + missector.getMisSector() + " CREATED SUCCESSFULLY AT " + missector.getPostedTime());
                        response.setStatusCode(HttpStatus.CREATED.value());
                        response.setEntity(newMissector);
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
    public ResponseEntity<?> getAllMissectors() {
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
                    List<Missector> missector = missectorRepo.findAllByEntityIdAndDeletedFlagOrderByIdDesc(EntityRequestContext.getCurrentEntityId(), 'N');
                    if (missector.size() > 0) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(missector);
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
    public ResponseEntity<?> getMissectorById(@PathVariable("id") Long id) {
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
                    Missector missector = missectorService.findById(id);
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(missector);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/find/by/missector/{code}")
    public ResponseEntity<?> getMissectorByCode(@PathVariable("code") String misCode) {
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
                    Optional<Missector> searchCode = missectorRepo.findByEntityIdAndMisCode(EntityRequestContext.getCurrentEntityId(), misCode);
                    if (searchCode.isPresent()) {
                        EntityResponse response = new EntityResponse();
                        Optional<Missector> missector = missectorRepo.findByMisCode(misCode);
                        response.setMessage("MIS SECTOR WITH CODE " + misCode + " ALREADY REGISTERED");
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(missector);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("MIS SECTOR WITH CODE " + misCode + " AVAILABLE FOR REGISTRATION");
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
    public ResponseEntity<?> updateMissector(@RequestBody Missector missector) {
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
                    missector.setModifiedBy(UserRequestContext.getCurrentUser());
                    missector.setEntityId(EntityRequestContext.getCurrentEntityId());
                    Optional<Missector> missector1 = missectorRepo.findById(missector.getId());
                    if (missector1.isPresent()) {
                        missector.setPostedTime(missector1.get().getPostedTime());
                        missector.setPostedFlag(missector1.get().getPostedFlag());
                        missector.setPostedBy(missector1.get().getPostedBy());
                        missector.setModifiedFlag('Y');
                        missector.setVerifiedFlag(missector1.get().getVerifiedFlag());
                        missector.setModifiedTime(new Date());
                        missector.setModifiedBy(missector.getModifiedBy());
                        missectorService.updateMissector(missector);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("MIS SECTOR WITH CODE " + missector.getMisCode() + " AND NAME " + missector.getMisSector() + "MODIFIED SUCCESSFULLY AT " + missector.getModifiedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(missector);
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
                    Optional<Missector> missector1 = missectorRepo.findById(Long.parseLong(id));
                    if (missector1.isPresent()) {
                        Missector missector = missector1.get();
                        if (missector.getPostedBy().equalsIgnoreCase(UserRequestContext.getCurrentUser())) {
                            EntityResponse response = new EntityResponse();
                            response.setMessage("You Can Not Verify What you initiated");
                            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                            response.setEntity("");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        } else {
                            if (missector.getVerifiedFlag().equals('Y')) {
                                EntityResponse response = new EntityResponse();
                                response.setMessage("MIS SECTOR WITH CODE " + missector.getMisCode() + " AND NAME " + missector.getMisSector() + " ALREADY VERIFIED");
                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                response.setEntity("");
                                return new ResponseEntity<>(response, HttpStatus.OK);
                            } else {
                                if (missector.getVerifiedFlag().equals('Y')) {
                                    EntityResponse response = new EntityResponse();
                                    response.setMessage("MIS SECTOR WITH CODE " + missector.getMisCode() + " AND NAME " + missector.getMisSector() + " ALREADY VERIFIED");
                                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                    response.setEntity("");
                                    return new ResponseEntity<>(response, HttpStatus.OK);
                                } else {
                                    missector.setVerifiedFlag('Y');
                                    missector.setVerifiedTime(new Date());
                                    missector.setVerifiedBy(UserRequestContext.getCurrentUser());
                                    missectorRepo.save(missector);
                                    EntityResponse response = new EntityResponse();
                                    response.setMessage("MIS SECTOR WITH CODE " + missector.getMisCode() + " AND NAME " + missector.getMisSector() + "VERIFIED SUCCESSFULLY AT " + missector.getVerifiedTime());
                                    response.setStatusCode(HttpStatus.OK.value());
                                    response.setEntity(missector);
                                    return new ResponseEntity<>(response, HttpStatus.OK);
                                }

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
    public ResponseEntity<?> deleteMissector(@PathVariable Long id) {
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
                    Optional<Missector> missector1 = missectorRepo.findById(id);
                    if (missector1.isPresent()) {
                        Missector missector = missector1.get();
                        missector.setDeletedFlag('Y');
                        missector.setDeletedTime(new Date());
                        missector.setDeletedBy(UserRequestContext.getCurrentUser());
                        missectorRepo.save(missector);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("MIS SECTOR WITH CODE " + missector.getMisCode() + " AND NAME " + missector.getMisSector() + " DELETED SUCCESSFULLY AT " + missector.getDeletedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(missector);
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
