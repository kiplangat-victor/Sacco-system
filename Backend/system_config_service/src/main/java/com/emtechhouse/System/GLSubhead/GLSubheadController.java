package com.emtechhouse.System.GLSubhead;

import com.emtechhouse.System.GL.GL;
import com.emtechhouse.System.GL.GLRepository;
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

//@CrossOrigin
@RequestMapping("glsubhead")
@RestController
@Slf4j
public class GLSubheadController {
    private final GLSubheadService glsubheadSubheadService;
    private final GLSubheadRepo glsubheadSubheadRepo;
    private final GLRepository glRepository;

    public GLSubheadController(GLSubheadService glsubheadSubheadService, GLSubheadRepo glsubheadSubheadRepo, GLRepository glRepository) {
        this.glsubheadSubheadService = glsubheadSubheadService;
        this.glsubheadSubheadRepo = glsubheadSubheadRepo;
        this.glRepository = glRepository;
    }

    public boolean validateCode(String glCode, String glSubheadCode) {
        Boolean valid = false;
        Integer count = 0;
        String glCodestr = String.valueOf(glCode);
        String glSubheadCodestr = String.valueOf(glSubheadCode);
        for (int i = 0; i < glCodestr.length(); i++) {
            for (int j = 0; j < glSubheadCodestr.length(); i++) {
                if (glCodestr.charAt(i) == glSubheadCodestr.charAt(i)) {
                    count = count + 0;
                } else {
                    count = count + 1;
                }
            }
        }
        if (count.equals(0)) {
            valid = true;
        } else {
            valid = false;
        }
        return valid;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addGLSubhead(@RequestBody GLSubhead glsubhead) {
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
                    glsubhead.setPostedFlag('Y');
                    glsubhead.setPostedBy(UserRequestContext.getCurrentUser());
                    glsubhead.setEntityId(EntityRequestContext.getCurrentEntityId());
                    Optional<GLSubhead> searchGlSubHeadCode = glsubheadSubheadRepo.findByEntityIdAndGlSubheadCodeAndDeletedFlag(glsubhead.getEntityId(), glsubhead.getGlSubheadCode(), 'N');
                    Optional<GL> gl = glRepository.findByEntityIdAndGlCodeAndDeletedFlag(glsubhead.getEntityId(), glsubhead.getGlCode(), 'N');
                    if (searchGlSubHeadCode.isPresent()) {
                        EntityResponse entityResponse = new EntityResponse<>();
                        entityResponse.setMessage("GL SUBHEAD  WITH CODE " + searchGlSubHeadCode.get().getGlSubheadCode() + " ALREADY REGISTERED ON " + searchGlSubHeadCode.get().getPostedTime());
                        entityResponse.setEntity("");
                        entityResponse.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        return new ResponseEntity<>(entityResponse, HttpStatus.OK);
                    } else {
                        if (gl.isPresent()) {
                            glsubhead.setPostedFlag('Y');
                            glsubhead.setPostedTime(new Date());
                            glsubhead.setPostedFlag('Y');
                            glsubhead.setPostedTime(new Date());
                            GLSubhead newGLSubhead = glsubheadSubheadService.addGLSubhead(glsubhead);
                            EntityResponse response = new EntityResponse();
                            response.setMessage("GL SUBHEAD WITH CODE " + glsubhead.getGlSubheadCode() + " CREATED SUCCESSFULLY AT " + glsubhead.getPostedTime());
                            response.setStatusCode(HttpStatus.CREATED.value());
                            response.setEntity(newGLSubhead);
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        } else {
                            EntityResponse entityResponse = new EntityResponse<>();
                            entityResponse.setEntity("");
                            entityResponse.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                            entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                            return new ResponseEntity<>(entityResponse, HttpStatus.OK);
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
    public ResponseEntity<?> getAllGLSubheads() {
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
                    List<GLSubhead> glsubhead = glsubheadSubheadRepo.findAllByEntityIdAndDeletedFlagOrderByIdDesc(EntityRequestContext.getCurrentEntityId(), 'N');
                    if (glsubhead.size() > 0) {
                        response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(glsubhead);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity(glsubhead);
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
    public ResponseEntity<?> getGLSubheadById(@PathVariable("id") Long id) {
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
                    GLSubhead glsubhead = glsubheadSubheadService.findById(id);
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(glsubhead);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/code/find/{code}")
    public ResponseEntity<?> getGlSubheadByCode(@PathVariable("code") String glSubheadCode) {
        try {
            EntityResponse response = new EntityResponse();
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                Optional<GLSubhead> searchCode = glsubheadSubheadRepo.findByEntityIdAndGlSubheadCode(EntityRequestContext.getCurrentEntityId(), glSubheadCode);
                if (searchCode.isPresent()) {
                    Optional<GLSubhead> glSubhead = glsubheadSubheadRepo.findByGlSubheadCode(glSubheadCode);
                    response.setMessage("GL SUBHEAD  WITH CODE " + glSubheadCode + " ALREADY REGISTERED");
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(glSubhead);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    response.setMessage("GL SUBHEAD WITH CODE " + glSubheadCode + " AVAILABLE FOR REGISTRATION");
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
    public ResponseEntity<?> update(@RequestBody GLSubhead glsubhead) {
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
                    glsubhead.setModifiedBy(UserRequestContext.getCurrentUser());
                    glsubhead.setEntityId(EntityRequestContext.getCurrentEntityId());
                    Optional<GLSubhead> gls1 = glsubheadSubheadRepo.findByEntityIdAndGlSubheadCodeAndDeletedFlag(glsubhead.getEntityId(), glsubhead.getGlSubheadCode(), 'N');
                    if (gls1.isPresent()) {
                        glsubhead.setPostedTime(gls1.get().getPostedTime());
                        glsubhead.setPostedFlag(gls1.get().getPostedFlag());
                        glsubhead.setPostedBy(gls1.get().getPostedBy());
                        glsubhead.setModifiedFlag('Y');
                        glsubhead.setVerifiedFlag(gls1.get().getVerifiedFlag());
                        glsubhead.setModifiedTime(new Date());
                        glsubhead.setModifiedBy(glsubhead.getModifiedBy());
                        GLSubhead updateGlSubHead = glsubheadSubheadService.updateGLSubhead(glsubhead);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("GL SUBHEAD  WITH CODE " + glsubhead.getGlSubheadCode() + " MODIFIED SUCCESSFULLY AT " + glsubhead.getModifiedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(updateGlSubHead);
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
                    Optional<GLSubhead> glsubhead = glsubheadSubheadRepo.findById(Long.parseLong(id));
                    if (glsubhead.isPresent()) {
                        GLSubhead glsubhead1 = glsubhead.get();
                        if (glsubhead1.getPostedBy().equalsIgnoreCase(UserRequestContext.getCurrentUser())) {
                            EntityResponse response = new EntityResponse();
                            response.setMessage("You Can Not Verify What you initiated");
                            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                            response.setEntity("");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        } else {
                            if (glsubhead1.getVerifiedFlag().equals('Y')) {
                                EntityResponse response = new EntityResponse();
                                response.setMessage("GL SUBHEAD  WITH CODE " + glsubhead.get().getGlSubheadCode() + " ALREADY VERIFIED ON " + glsubhead1.getVerifiedTime());
                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                response.setEntity("");
                                return new ResponseEntity<>(response, HttpStatus.OK);
                            } else {
                                glsubhead1.setVerifiedFlag('Y');
                                glsubhead1.setVerifiedTime(new Date());
                                glsubhead1.setVerifiedBy(UserRequestContext.getCurrentUser());
                                glsubheadSubheadRepo.save(glsubhead1);
                                EntityResponse response = new EntityResponse();
                                response.setMessage("GL SUBHEAD  WITH CODE " + glsubhead.get().getGlSubheadCode() + " VERIFIED SUCCESSFULLY AT " + glsubhead.get().getVerifiedTime());
                                response.setStatusCode(HttpStatus.OK.value());
                                response.setEntity(glsubhead1);
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
    public ResponseEntity<?> deleteGLSubhead(@PathVariable String id) {
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
                    Optional<GLSubhead> glsubhead = glsubheadSubheadRepo.findById(Long.parseLong(id));
                    if (glsubhead.isPresent()) {
                        GLSubhead branch = glsubhead.get();
                        branch.setDeletedFlag('Y');
                        branch.setDeletedTime(new Date());
                        branch.setDeletedBy(UserRequestContext.getCurrentUser());
                        glsubheadSubheadRepo.save(branch);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("GL SUBHEAD  WITH CODE " + glsubhead.get().getGlSubheadCode() + " DELETED SUCCESSFULLY AT " + glsubhead.get().getDeletedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(branch);
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


