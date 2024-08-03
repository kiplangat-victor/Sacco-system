package com.emtechhouse.System.segments.SubSegments;


import com.emtechhouse.System.Utils.EntityResponse;
import com.emtechhouse.System.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.System.Utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.System.segments.Segment;
import com.emtechhouse.System.segments.SegmentRepo;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

//@CrossOrigin
@RestController
@RequestMapping("/api/v1/subsegment")
//@CrossOrigin
@Slf4j
@Api(value = "Sub-SubSegment API", tags = "Sub-SubSegment API")
public class SubSegmentController {
    @Autowired
    private SubSegmentRepo subSegmentRepo;
    @Autowired
    private SubSegmentService subSegmentService;
    @Autowired
    private SegmentRepo segmentRepo;

    @PostMapping("/add")
    public ResponseEntity<?> addSubSegment(@RequestBody SubSegment subSegment, @RequestParam Long segmentId) {
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
                    Optional<SubSegment> checkSubSegment = subSegmentRepo.findByEntityIdAndSubSegmentCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), subSegment.getSubSegmentCode(), 'N');
                    if (checkSubSegment.isPresent()) {
                        response.setMessage("SUB SEGMENT WITH CODE " + checkSubSegment.get().getSubSegmentCode() + " ALREADY CREATED ON " + checkSubSegment.get().getPostedTime());
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        Optional<Segment> checSegment = segmentRepo.findById(segmentId);
                        if (checSegment.isPresent()) {
                            Segment segment = checSegment.get();
                            subSegment.setSegment(segment);
                            subSegment.setPostedBy(UserRequestContext.getCurrentUser());
                            subSegment.setEntityId(EntityRequestContext.getCurrentEntityId());
                            subSegment.setPostedFlag('Y');
                            subSegment.setPostedTime(new Date());
                            SubSegment newSubSegment = subSegmentService.addSubSegment(subSegment);
                            response.setMessage("SUB SEGMENT WITH CODE " + subSegment.getSubSegmentCode() + " CREATED SUCCESSFULLY AT " + subSegment.getPostedTime());
                            response.setStatusCode(HttpStatus.CREATED.value());
                            response.setEntity(newSubSegment);
                        } else {
                            response.setMessage("Segment Not Found");
                            response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        }
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
    public ResponseEntity<?> getAllSubSegments() {
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
                    List<SubSegment> subSegment = subSegmentRepo.findAllByEntityIdAndDeletedFlagOrderByIdDesc(EntityRequestContext.getCurrentEntityId(), 'N');
                    if (subSegment.size() > 0) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(subSegment);
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
    public ResponseEntity<?> getSubSegmentById(@PathVariable("id") Long id) {
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
                    SubSegment subSegment = subSegmentService.findById(id);
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(subSegment);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/find/subSegment/code/{code}")
    public ResponseEntity<?> getSubSegmentByCode(@PathVariable("code") String subSegmentCode) {
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
                    Optional<SubSegment> searchCode = subSegmentRepo.findByEntityIdAndSubSegmentCode(EntityRequestContext.getCurrentEntityId(), subSegmentCode);
                    if (searchCode.isPresent()) {
                        EntityResponse response = new EntityResponse();
                        Optional<SubSegment> subSegment = subSegmentRepo.findBySubSegmentCode(subSegmentCode);
                        response.setMessage("SUB SEGMENT WITH CODE " + subSegmentCode + " ALREADY REGISTERED");
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(subSegment);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("SUB SEGMENT WITH CODE " + subSegmentCode + " AVAILABLE FOR REGISTRATION");
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
    public ResponseEntity<?> updateSubSegment(@RequestBody SubSegment subSegment) {
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
                    subSegment.setModifiedBy(UserRequestContext.getCurrentUser());
                    subSegment.setEntityId(EntityRequestContext.getCurrentEntityId());
                    Optional<SubSegment> subSegment1 = subSegmentRepo.findById(subSegment.getId());
                    if (subSegment1.isPresent()) {
                        subSegment.setPostedTime(subSegment1.get().getPostedTime());
                        subSegment.setPostedFlag(subSegment1.get().getPostedFlag());
                        subSegment.setPostedBy(subSegment1.get().getPostedBy());
                        subSegment.setModifiedFlag('Y');
                        subSegment.setVerifiedFlag(subSegment1.get().getVerifiedFlag());
                        subSegment.setModifiedTime(new Date());
                        subSegment.setModifiedBy(subSegment.getModifiedBy());
                        subSegmentService.updateSubSegment(subSegment);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("SUB SEGMENT WITH CODE " + subSegment.getSubSegmentCode() + " MODIFIED SUCCESSFULLY AT " + subSegment.getModifiedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(subSegment);
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
                    Optional<SubSegment> subSegment1 = subSegmentRepo.findById(Long.parseLong(id));
                    if (subSegment1.isPresent()) {
                        SubSegment subSegment = subSegment1.get();
                        if (subSegment.getPostedBy().equalsIgnoreCase(UserRequestContext.getCurrentUser())) {
                            EntityResponse response = new EntityResponse();
                            response.setMessage("You Can Not Verify What you initiated");
                            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                            response.setEntity("");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        } else {
                            if (subSegment.getVerifiedFlag().equals('Y')) {
                                EntityResponse response = new EntityResponse();
                                response.setMessage("SUB SEGMENT WITH CODE " + subSegment.getSubSegmentCode() + " ALREADY VERIFIED");
                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                response.setEntity("");
                                return new ResponseEntity<>(response, HttpStatus.OK);
                            } else {
                                subSegment.setVerifiedFlag('Y');
                                subSegment.setVerifiedTime(new Date());
                                subSegment.setVerifiedBy(UserRequestContext.getCurrentUser());
                                subSegmentRepo.save(subSegment);
                                EntityResponse response = new EntityResponse();
                                response.setMessage("SUB SEGMENT WITH CODE " + subSegment.getSubSegmentCode() + " VERIFIED SUCCESSFULLY AT " + subSegment.getVerifiedTime());
                                response.setStatusCode(HttpStatus.OK.value());
                                response.setEntity(subSegment);
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
    public ResponseEntity<?> deleteSubSegment(@PathVariable Long id) {
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
                    Optional<SubSegment> subSegment1 = subSegmentRepo.findById(id);
                    if (subSegment1.isPresent()) {
                        SubSegment subSegment = subSegment1.get();
                        subSegment.setDeletedFlag('Y');
                        subSegment.setDeletedTime(new Date());
                        subSegment.setDeletedBy(UserRequestContext.getCurrentUser());
                        subSegmentRepo.save(subSegment);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("SUB SEGMENT WITH CODE " + subSegment.getSubSegmentCode() + " DELETED SUCCESSFULLY AT " + subSegment.getDeletedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(subSegment);
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
