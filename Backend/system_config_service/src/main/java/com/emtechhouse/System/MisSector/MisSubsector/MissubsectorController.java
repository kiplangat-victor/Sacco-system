package com.emtechhouse.System.MisSector.MisSubsector;


import com.emtechhouse.System.MisSector.Missector;
import com.emtechhouse.System.MisSector.MissectorRepo;
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
@Api(value = "/Mis Sub Sector API", tags = "Mis Sub Sector API")
@RequestMapping("/api/v1/mis/subsector")
public class MissubsectorController {
    private final MissubsectorService missubsectorService;
    private final MissubsectorRepo missubsectorRepo;
    private final MissectorRepo missectorRepo;

    public MissubsectorController(MissubsectorService missubsectorService, MissubsectorRepo missubsectorRepo, MissectorRepo missectorRepo) {
        this.missubsectorService = missubsectorService;
        this.missubsectorRepo = missubsectorRepo;
        this.missectorRepo = missectorRepo;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addMissubsector(@RequestBody Missubsector missubsector, @RequestParam Long misSectorId) {
        System.out.println(missubsector);
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
                    Optional<Missubsector> checkMissubsector = missubsectorRepo.findByEntityIdAndMisSubcodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), missubsector.getMisSubcode(), 'N');
                    if (checkMissubsector.isPresent()) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("MIS SUB SECTOR WITH CODE " + checkMissubsector.get().getMisSubcode() + " AND NAME " + checkMissubsector.get().getMisSubcode() + " ALREADY CREATED ON  " + checkMissubsector.get().getPostedTime());
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        Optional<Missector> checkMisSectorId = missectorRepo.findById(misSectorId);
                        if (checkMisSectorId.isPresent()) {
                            Missector missector = checkMisSectorId.get();
                            missubsector.setMissector(missector);
                            missubsector.setPostedBy(UserRequestContext.getCurrentUser());
                            missubsector.setEntityId(EntityRequestContext.getCurrentEntityId());
                            missubsector.setPostedFlag('Y');
                            missubsector.setPostedTime(new Date());
                            Missubsector newMissubsector = missubsectorService.addMissubsector(missubsector);
                            EntityResponse response = new EntityResponse();
                            response.setMessage("MIS SUB SECTOR WITH CODE " + missubsector.getMisSubcode() + " AND NAME " + missubsector.getMisSubcode() + " CREATED SUCCESSFULLY AT " + missubsector.getPostedTime());
                            response.setStatusCode(HttpStatus.CREATED.value());
                            response.setEntity(newMissubsector);
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        } else {
                            EntityResponse response = new EntityResponse();
                            response.setMessage("MIS SECTOR CODE NOT FOUND");
                            response.setStatusCode(HttpStatus.NOT_FOUND.value());
                            response.setEntity("");
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
    public ResponseEntity<?> getAllMissubsectors() {
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
                    List<Missubsector> missubsector = missubsectorRepo.findByEntityIdAndDeletedFlagOrderByIdDesc(EntityRequestContext.getCurrentEntityId(), 'N');
                    if (missubsector.size() > 0) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(missubsector);
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
    public ResponseEntity<?> getMissubsectorById(@PathVariable("id") Long id) {
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
                    Missubsector missubsector = missubsectorService.findById(id);
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(missubsector);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }


    @GetMapping("/find/missubsector/code/{code}")
    public ResponseEntity<?> getMissubsectorByCode(@PathVariable("code") String misSubcode) {
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

                    Optional<Missubsector> searchCode = missubsectorRepo.findByEntityIdAndMisSubcode(EntityRequestContext.getCurrentEntityId(), misSubcode);
                    if (searchCode.isPresent()) {
                        EntityResponse response = new EntityResponse();
                        Missubsector missubsector = missubsectorService.findByMissubcode(misSubcode);
                        response.setMessage("MIS SUB SECTOR WITH CODE " + misSubcode + " ALREADY REGISTERED");
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(missubsector);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("MIS SUB SECTOR WITH CODE " + misSubcode + " AVAILABLE FOR REGISTRATION");
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
    public ResponseEntity<?> updateMissubsector(@RequestBody Missubsector missubsector) {
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
                    missubsector.setModifiedBy(UserRequestContext.getCurrentUser());
                    missubsector.setEntityId(EntityRequestContext.getCurrentEntityId());
                    Optional<Missubsector> missubsector1 = missubsectorRepo.findById(missubsector.getId());
                    if (missubsector1.isPresent()) {
                        missubsector.setPostedTime(missubsector1.get().getPostedTime());
                        missubsector.setPostedFlag(missubsector1.get().getPostedFlag());
                        missubsector.setPostedBy(missubsector1.get().getPostedBy());
                        missubsector.setModifiedFlag('Y');
                        missubsector.setVerifiedFlag(missubsector1.get().getVerifiedFlag());
                        missubsector.setModifiedTime(new Date());
                        missubsector.setModifiedBy(missubsector.getModifiedBy());
                        missubsectorService.updateMissubsector(missubsector);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("MIS SUB SECTOR WITH CODE " + missubsector.getMisSubcode() + " AND NAME " + missubsector.getMisSubcode() + " MODIFIED SUCCESSFULLY AT " + missubsector.getModifiedTime());
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(missubsector);
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
                    Optional<Missubsector> missubsector1 = missubsectorRepo.findById(Long.parseLong(id));
                    if (missubsector1.isPresent()) {
                        Missubsector missubsector = missubsector1.get();
                        if (missubsector.getPostedBy().equalsIgnoreCase(UserRequestContext.getCurrentUser())) {
                            EntityResponse response = new EntityResponse();
                            response.setMessage("You Can Not Verify What you initiated");
                            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                            response.setEntity("");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        } else {
                            if (missubsector.getVerifiedFlag().equals('N')) {
                                EntityResponse response = new EntityResponse();
                                response.setMessage("MIS SUB SECTOR WITH CODE " + missubsector.getMisSubcode() + " AND NAME " + missubsector.getMisSubcode() + " ALREADY VERIFIED");
                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                response.setEntity("");
                                return new ResponseEntity<>(response, HttpStatus.OK);
                            } else {
                                missubsector.setVerifiedFlag('Y');
                                missubsector.setVerifiedTime(new Date());
                                missubsector.setVerifiedBy(UserRequestContext.getCurrentUser());
                                missubsectorRepo.save(missubsector);
                                EntityResponse response = new EntityResponse();
                                response.setMessage("MIS SUB SECTOR WITH CODE " + missubsector.getMisSubcode() + " AND NAME " + missubsector.getMisSubcode() + " VERIFIED SUCCESSFULLY AT " + missubsector.getVerifiedTime());
                                response.setStatusCode(HttpStatus.OK.value());
                                response.setEntity(missubsector);
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
    public ResponseEntity<?> deleteMissubsector(@PathVariable Long id) {
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
                    Optional<Missubsector> missubsector1 = missubsectorRepo.findById(id);
                    if (missubsector1.isPresent()) {
                        Missubsector missubsector = missubsector1.get();
                        missubsector.setDeletedFlag('Y');
                        missubsector.setDeletedTime(new Date());
                        missubsector.setDeletedBy(UserRequestContext.getCurrentUser());
                        missubsectorRepo.save(missubsector);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("MIS SUB SECTOR WITH CODE " + missubsector.getMisSubcode() + " AND NAME " + missubsector.getMisSubcode() + " DELETED SUCCESSFULLY AT " + missubsector.getDeletedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(missubsector);
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
