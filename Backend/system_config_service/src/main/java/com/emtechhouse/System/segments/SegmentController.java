package com.emtechhouse.System.segments;


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

@RequestMapping("api/v1/segment")
//@CrossOrigin
@RestController
@Slf4j
@Api(value = "Segments API", tags = "Segment API")
public class SegmentController {
    @Autowired
    private SegmentRepo segmentRepo;
    @Autowired
    private Segmentservice segmentService;

    @PostMapping("/add")
    public ResponseEntity<?> addSegment(@RequestBody Segment segment) {
        System.out.println("Add segment");
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
                    Optional<Segment> checkSegment = segmentRepo.findByEntityIdAndSegmentCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), segment.getSegmentCode(), 'N');
                    if (checkSegment.isPresent()) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("SEGMENT WITH CODE " + checkSegment.get().getSegmentCode() + " ALREADY CREATED ON " + checkSegment.get().getPostedTime());
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        segment.setPostedBy(UserRequestContext.getCurrentUser());
                        segment.setEntityId(EntityRequestContext.getCurrentEntityId());
                        segment.setPostedFlag('Y');
                        segment.setPostedTime(new Date());
                        Segment newSegment = segmentService.addSegment(segment);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("SEGMENT WITH CODE " + segment.getSegmentCode() + " CREATED SUCCESSFULLY AT " + segment.getPostedTime());
                        response.setStatusCode(HttpStatus.CREATED.value());
                        response.setEntity(newSegment);
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
    public ResponseEntity<?> getAllSegments() {
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
                    List<Segment> segment = segmentRepo.findAllByEntityIdAndDeletedFlagOrderByIdDesc(EntityRequestContext.getCurrentEntityId(), 'N');
                    if (segment.size() > 0) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(segment);
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
    public ResponseEntity<?> getSegmentById(@PathVariable("id") Long id) {
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
                    Segment segment = segmentService.findById(id);
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(segment);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/find/segment/code/{code}")
    public ResponseEntity<?> getSegmentByCode(@PathVariable("code") String segmentCode) {
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
                    Optional<Segment> searchCode = segmentRepo.findByEntityIdAndSegmentCode(EntityRequestContext.getCurrentEntityId(), segmentCode);
                    if (searchCode.isPresent()) {
                        EntityResponse response = new EntityResponse();
                        Optional<Segment> segment = segmentRepo.findBySegmentCode(segmentCode);
                        response.setMessage("SEGMENT WITH CODE " + segmentCode + " ALREADY REGISTERED");
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(segment);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("SEGMENT WITH CODE " + segmentCode + " AVAILABLE FOR REGISTRATION");
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
    public ResponseEntity<?> updateSegment(@RequestBody Segment segment) {
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
                    segment.setModifiedBy(UserRequestContext.getCurrentUser());
                    segment.setEntityId(EntityRequestContext.getCurrentEntityId());
                    Optional<Segment> segment1 = segmentRepo.findById(segment.getId());
                    if (segment1.isPresent()) {
                        segment.setPostedTime(segment1.get().getPostedTime());
                        segment.setPostedFlag(segment1.get().getPostedFlag());
                        segment.setPostedBy(segment1.get().getPostedBy());
                        segment.setModifiedFlag('Y');
                        segment.setVerifiedFlag(segment1.get().getVerifiedFlag());
                        segment.setModifiedTime(new Date());
                        segment.setModifiedBy(segment.getModifiedBy());
                        segment = segmentService.updateSegment(segment);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("SEGMENT WITH CODE " + segment.getSegmentCode() + " MODIFIED SUCCESSFULLY AT " + segment.getModifiedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(segment);
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
                    Optional<Segment> segment1 = segmentRepo.findById(Long.parseLong(id));
                    if (segment1.isPresent()) {
                        Segment segment = segment1.get();
                        if (segment.getPostedBy().equalsIgnoreCase(UserRequestContext.getCurrentUser())) {
                            EntityResponse response = new EntityResponse();
                            response.setMessage("You Can Not Verify What you initiated");
                            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                            response.setEntity("");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        } else {
                            if (segment.getVerifiedFlag().equals('Y')) {
                                EntityResponse response = new EntityResponse();
                                response.setMessage("SEGMENT WITH CODE " + segment.getSegmentCode() + " ALREADY VERIFIED");
                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                response.setEntity("");
                                return new ResponseEntity<>(response, HttpStatus.OK);
                            } else {
                                segment.setVerifiedFlag('Y');
                                segment.setVerifiedTime(new Date());
                                segment.setVerifiedBy(UserRequestContext.getCurrentUser());
                                segmentRepo.save(segment);
                                EntityResponse response = new EntityResponse();
                                response.setMessage("SEGMENT WITH CODE " + segment.getSegmentCode() + " VERIFIED SUCCESSFULLY AT " + segment.getVerifiedTime());
                                response.setStatusCode(HttpStatus.OK.value());
                                response.setEntity(segment);
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
    public ResponseEntity<?> deleteSegment(@PathVariable Long id) {
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
                    Optional<Segment> segment1 = segmentRepo.findById(id);
                    if (segment1.isPresent()) {
                        Segment segment = segment1.get();
                        segment.setDeletedFlag('Y');
                        segment.setDeletedTime(new Date());
                        segment.setDeletedBy(UserRequestContext.getCurrentUser());
                        segmentRepo.save(segment);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("SEGMENT WITH CODE " + segment.getSegmentCode() + " DELETED SUCCESSFULLY AT " + segment.getDeletedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(segment);
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
