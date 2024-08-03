package com.emtechhouse.System.SharecapitalParams;

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
@Api(value = "/Sharecapital Params API", tags = "Sharecapital Params API")
@RequestMapping("/api/v1/sharecapital/params")
public class SharecapitalParamController {
    private final SharecapitalParamService sharecapitalParamService;
    private final SharecapitalParamRepo sharecapitalParamRepo;

    public SharecapitalParamController(SharecapitalParamService sharecapitalParamService, SharecapitalParamRepo sharecapitalParamRepo) {
        this.sharecapitalParamService = sharecapitalParamService;
        this.sharecapitalParamRepo = sharecapitalParamRepo;
    }
    @PostMapping("/add")
    public ResponseEntity<?> addSharecapitalParam(@RequestBody SharecapitalParam sharecapitalParam) {
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
                    Optional<SharecapitalParam> checkSharecapitalParam = sharecapitalParamRepo.findByEntityIdAndShareCapitalCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), sharecapitalParam.getShareCapitalCode(),'N');
                    if(checkSharecapitalParam.isPresent()){
                        EntityResponse response = new EntityResponse();
                        response.setMessage("Code already exists!");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }else{
                        sharecapitalParamRepo.setAllInactive();
                        sharecapitalParam.setPostedBy(UserRequestContext.getCurrentUser());
                        sharecapitalParam.setEntityId(EntityRequestContext.getCurrentEntityId());
                        sharecapitalParam.setPostedFlag('Y');
                        sharecapitalParam.setIsActive(true);
                        sharecapitalParam.setPostedTime(new Date());
                        SharecapitalParam newSharecapitalParam = sharecapitalParamService.addSharecapitalParam(sharecapitalParam);
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.CREATED.getReasonPhrase());
                        response.setStatusCode(HttpStatus.CREATED.value());
                        response.setEntity(newSharecapitalParam);
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
    public ResponseEntity<?> getAllSharecapitalParams() {
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
                    List<SharecapitalParam> sharecapitalParam = sharecapitalParamRepo.findByEntityIdAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), 'N');
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(sharecapitalParam);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<?> getSharecapitalParamById(@PathVariable("id") Long id) {
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
                    SharecapitalParam sharecapitalParam = sharecapitalParamService.findById(id);
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(sharecapitalParam);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("/modify")
    public ResponseEntity<?> updateSharecapitalParam(@RequestBody SharecapitalParam sharecapitalParam) {
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
                    sharecapitalParam.setModifiedBy(UserRequestContext.getCurrentUser());
                    sharecapitalParam.setEntityId(EntityRequestContext.getCurrentEntityId());
                    Optional<SharecapitalParam> sharecapitalParam1 = sharecapitalParamRepo.findById(sharecapitalParam.getId());
                    if (sharecapitalParam1.isPresent()) {
                        sharecapitalParam.setPostedTime(sharecapitalParam1.get().getPostedTime());
                        sharecapitalParam.setPostedFlag(sharecapitalParam1.get().getPostedFlag());
                        sharecapitalParam.setPostedBy(sharecapitalParam1.get().getPostedBy());
                        sharecapitalParam.setModifiedFlag('Y');
                        sharecapitalParam.setVerifiedFlag(sharecapitalParam1.get().getVerifiedFlag());
                        sharecapitalParam.setModifiedTime(new Date());
                        sharecapitalParam.setModifiedBy(sharecapitalParam.getModifiedBy());
                        sharecapitalParamService.updateSharecapitalParam(sharecapitalParam);
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(sharecapitalParam);
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
                    Optional<SharecapitalParam> sharecapitalParam1 = sharecapitalParamRepo.findById(Long.parseLong(id));
                    if (sharecapitalParam1.isPresent()) {
                        SharecapitalParam sharecapitalParam = sharecapitalParam1.get();

                        //                    Check Maker Checker
                        if (sharecapitalParam.getPostedBy().equalsIgnoreCase(UserRequestContext.getCurrentUser())){
                            EntityResponse response = new EntityResponse();
                            response.setMessage("You Can Not Verify What you initiated");
                            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                            response.setEntity("");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        }else{
                            sharecapitalParam.setVerifiedFlag('Y');
                            sharecapitalParam.setVerifiedTime(new Date());
                            sharecapitalParam.setVerifiedBy(UserRequestContext.getCurrentUser());
                            sharecapitalParamRepo.save(sharecapitalParam);
                            EntityResponse response = new EntityResponse();
                            response.setMessage(HttpStatus.OK.getReasonPhrase());
                            response.setStatusCode(HttpStatus.OK.value());
                            response.setEntity(sharecapitalParam);
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
    public ResponseEntity<?> deleteSharecapitalParam(@PathVariable Long id) {
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
                    Optional<SharecapitalParam> sharecapitalParam1 = sharecapitalParamRepo.findById(id);
                    if (sharecapitalParam1.isPresent()) {
                        SharecapitalParam sharecapitalParam = sharecapitalParam1.get();
                        sharecapitalParam.setDeletedFlag('Y');
                        sharecapitalParam.setDeletedTime(new Date());
                        sharecapitalParam.setDeletedBy(UserRequestContext.getCurrentUser());
                        sharecapitalParamRepo.save(sharecapitalParam);
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(sharecapitalParam);
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
