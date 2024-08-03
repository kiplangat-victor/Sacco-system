
package com.emtechhouse.System.GL;

import com.emtechhouse.System.GLSubhead.GLSubheadRepo;
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
@RequestMapping("gl")
@RestController
@Slf4j
public class GLController {
    private final GLService glService;
    private final GLRepository glRepository;
    private final GLSubheadRepo glSubheadRepo;

    public GLController(GLService glService, GLRepository glRepository, GLSubheadRepo glSubheadRepo) {
        this.glService = glService;
        this.glRepository = glRepository;
        this.glSubheadRepo = glSubheadRepo;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addGL(@RequestBody GL gl) {
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
                    gl.setPostedBy(UserRequestContext.getCurrentUser());
                    gl.setEntityId(EntityRequestContext.getCurrentEntityId());
                    Optional<GL> gl1 = glRepository.findByEntityIdAndGlCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), gl.getGlCode(), 'N');
                    if (gl1.isPresent()) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("GL WITH CODE " + gl.getGlCode() + " ALREADY REGISTERED");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        gl.setPostedFlag('Y');
                        gl.setPostedTime(new Date());
                        GL newGL = glService.addGL(gl);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("GL WITH CODE " + gl.getGlCode() + " CREATED SUCCESSFULLY AT " + gl.getPostedTime());
                        response.setStatusCode(HttpStatus.CREATED.value());
                        response.setEntity(newGL);
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
    public ResponseEntity<?> getAllGLs() {
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
                    List<GL> gl = glRepository.findAllByEntityIdAndDeletedFlagOrderByIdDesc(EntityRequestContext.getCurrentEntityId(), 'N');
                    if (gl.size() > 0) {
                        response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(gl);
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
    public ResponseEntity<?> getGLById(@PathVariable("id") Long id) {
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
                    GL gl = glService.findById(id);
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(gl);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/code/find/{code}")
    public ResponseEntity<?> getGlByCode(@PathVariable("code") String glCode) {
        try {
            EntityResponse response = new EntityResponse();
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                Optional<GL> searchCode = glRepository.findByEntityIdAndGlCode(EntityRequestContext.getCurrentEntityId(), glCode);
                if (searchCode.isPresent()) {
                    Optional<GL> gl = glRepository.findByGlCode(glCode);
                    response.setMessage("GL WITH CODE " + glCode + " ALREADY REGISTERED");
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(gl);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    response.setMessage("GL WITH CODE " + glCode + " AVAILABLE FOR REGISTRATION");
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
    public ResponseEntity<?> updateGL(@RequestBody GL gl) {
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
                    gl.setModifiedBy(UserRequestContext.getCurrentUser());
                    gl.setEntityId(EntityRequestContext.getCurrentEntityId());
                    Optional<GL> searchId = glRepository.findById(gl.getId());
                    if (searchId.isPresent()) {
                        gl.setPostedTime(searchId.get().getPostedTime());
                        gl.setPostedFlag(searchId.get().getPostedFlag());
                        gl.setPostedBy(searchId.get().getPostedBy());
                        gl.setModifiedFlag('Y');
                        gl.setVerifiedFlag(searchId.get().getVerifiedFlag());
                        gl.setModifiedTime(new Date());
                        gl.setModifiedBy(gl.getModifiedBy());
                        GL updateGl = glService.updateGL(gl);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("GL WITH CODE " + gl.getGlCode() + " MODIFIED SUCCESSFULLY AT " + gl.getModifiedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(updateGl);
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
                    Optional<GL> searchId = glRepository.findById(id);
                    System.out.println("GEL ID" + searchId);
                    if (searchId.isPresent()) {
                        GL gl1 = searchId.get();
                        if (gl1.getPostedBy().equalsIgnoreCase(UserRequestContext.getCurrentUser())) {
                            EntityResponse response = new EntityResponse();
                            response.setMessage("You Can Not Verify What you initiated");
                            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                            response.setEntity("");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        } else {

                            if (searchId.get().getVerifiedFlag().equals('Y')) {
                                EntityResponse response = new EntityResponse();
                                response.setMessage("GL WITH CODE " + searchId.get().getGlCode() + " ALREADY VERIFIED ON " + searchId.get().getVerifiedTime());
                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                response.setEntity("");
                                return new ResponseEntity<>(response, HttpStatus.OK);
                            } else {
                                gl1.setVerifiedFlag('Y');
                                gl1.setVerifiedTime(new Date());
                                gl1.setVerifiedBy(UserRequestContext.getCurrentUser());
                                glRepository.save(gl1);
                                EntityResponse response = new EntityResponse();
                                response.setMessage("GL WITH CODE " + gl1.getGlCode() + " VERIFIED SUCCESSFULLY AT " + gl1.getVerifiedTime());
                                response.setStatusCode(HttpStatus.OK.value());
                                response.setEntity(gl1);
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
    public ResponseEntity<?> deleteGL(@PathVariable String id) {
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
                    Optional<GL> gl = glRepository.findById(Long.parseLong(id));
                    if (gl.isPresent()) {
                        GL gl1 = gl.get();
                        gl1.setDeletedFlag('Y');
                        gl1.setDeletedTime(new Date());
                        gl1.setDeletedBy(UserRequestContext.getCurrentUser());
                        glRepository.save(gl1);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("GL WITH CODE " + gl.get().getGlCode() + " DELETED SUCCESSFULLY AT " + gl.get().getDeletedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(gl1);
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