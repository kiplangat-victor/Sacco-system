package com.emtechhouse.System.AssetClassifications;

import com.emtechhouse.System.CurrencyParams.Currency;
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

@RestController
//@CrossOrigin
@Api(tags = "Main Classification API")
@Slf4j
@RequestMapping("api/v1/assets/classification")
public class AssetClassificationController {

    private final AssetClassificationRepo assetClassificationRepo;
    private final AssetClassificationService assetClassificationService;

    public AssetClassificationController(AssetClassificationRepo assetClassificationRepo, AssetClassificationService assetClassificationService) {
        this.assetClassificationRepo = assetClassificationRepo;
        this.assetClassificationService = assetClassificationService;
    }
    @PostMapping("/add")
    public ResponseEntity<?> addMainClassification(@RequestBody AssetClassification assetClasification) {
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
                    Optional<AssetClassification> checkMainClassification = assetClassificationRepo.findByAssetClassificationCodeAndDeletedFlag(assetClasification.getAssetClassificationCode(),'N');
                    if(checkMainClassification.isPresent()){
                        EntityResponse response = new EntityResponse();
                        response.setMessage("Code already exists!");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }else{
                        assetClasification.setPostedBy(UserRequestContext.getCurrentUser());
                        assetClasification.setEntityId(EntityRequestContext.getCurrentEntityId());
                        assetClasification.setPostedFlag('Y');
                        assetClasification.setPostedTime(new Date());

                        AssetClassification newMainClassification = assetClassificationService.addMainClassification(assetClasification);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("ASSET CLASSIFICATION WITH CODE " + assetClasification.getAssetClassificationCode() + " CREATED SUCCESSFULLY AT " + assetClasification.getPostedTime());
                        response.setStatusCode(HttpStatus.CREATED.value());
                        response.setEntity(newMainClassification);
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
    public ResponseEntity<?> getAllMainClassifications() {
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
                    List<AssetClassification> assetClasification = assetClassificationRepo.findAllByEntityIdAndDeletedFlagOrderByIdDesc(EntityRequestContext.getCurrentEntityId(), 'N');
                    if (assetClasification.size()>0){
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(assetClasification);
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
    public ResponseEntity<?> getMainClassificationById(@PathVariable("id") Long id) {
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
                    AssetClassification assetClasification = assetClassificationService.findById(id);
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(assetClasification);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/find/by/code/{code}")
    public ResponseEntity<?> getMainClassificationByCode(@PathVariable("code") String assetClassificationCode) {
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
                    Optional<AssetClassification> searchCode = assetClassificationRepo.findByEntityIdAndAssetClassificationCode(EntityRequestContext.getCurrentEntityId(),assetClassificationCode);
                    if (searchCode.isPresent()){
                        Optional<AssetClassification>  assetClassification = assetClassificationRepo.findByAssetClassificationCode(assetClassificationCode);
                        response.setMessage("ASSET CLASSIFICATION WITH CODE " + assetClassificationCode + " ALREADY REGISTERED");
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(assetClassification);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        response.setMessage("ASSET CLASSIFICATION  WITH CODE " + assetClassificationCode + " AVAILABLE FOR REGISTRATION");
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
    public ResponseEntity<?> updateMainClassification(@RequestBody AssetClassification assetClasification) {
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
                    assetClasification.setModifiedBy(UserRequestContext.getCurrentUser());
                    assetClasification.setEntityId(EntityRequestContext.getCurrentEntityId());
                    Optional<AssetClassification> assetClasification1 = assetClassificationRepo.findById(assetClasification.getId());
                    if (assetClasification1.isPresent()) {
                        assetClasification.setPostedTime(assetClasification1.get().getPostedTime());
                        assetClasification.setPostedFlag(assetClasification1.get().getPostedFlag());
                        assetClasification.setPostedBy(assetClasification1.get().getPostedBy());
                        assetClasification.setModifiedFlag('Y');
                        assetClasification.setVerifiedFlag(assetClasification1.get().getVerifiedFlag());
                        assetClasification.setModifiedTime(new Date());
                        assetClasification.setModifiedBy(assetClasification.getModifiedBy());
                        assetClassificationService.updateMainClassification(assetClasification);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("ASSET CLASSIFICATION WITH CODE " + assetClasification.getAssetClassificationCode() + " MODIFIED SUCCESSFULLY AT " + assetClasification.getModifiedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(assetClasification);
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
                    Optional<AssetClassification> mainClassification1 = assetClassificationRepo.findById(Long.parseLong(id));
                    if (mainClassification1.isPresent()) {
                        AssetClassification mainClassification = mainClassification1.get();
                        if (mainClassification.getPostedBy().equalsIgnoreCase(UserRequestContext.getCurrentUser())){
                            EntityResponse response = new EntityResponse();
                            response.setMessage("You Can Not Verify What you initiated");
                            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                            response.setEntity("");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        }else{
                            mainClassification.setVerifiedFlag('Y');
                            mainClassification.setVerifiedTime(new Date());
                            mainClassification.setVerifiedBy(UserRequestContext.getCurrentUser());
                            assetClassificationRepo.save(mainClassification);
                            EntityResponse response = new EntityResponse();
                            response.setMessage("ASSET CLASSIFICATION WITH CODE " + mainClassification.getAssetClassificationCode() + " VERIFIED SUCCESSFULLY AT " + mainClassification.getVerifiedTime());
                            response.setStatusCode(HttpStatus.OK.value());
                            response.setEntity(mainClassification);
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
    public ResponseEntity<?> deleteMainClassification(@PathVariable Long id) {
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
                    Optional<AssetClassification> assetClasification1 = assetClassificationRepo.findById(id);
                    if (assetClasification1.isPresent()) {
                        AssetClassification assetClasification = assetClasification1.get();
                        assetClasification.setDeletedFlag('Y');
                        assetClasification.setDeletedTime(new Date());
                        assetClasification.setDeletedBy(UserRequestContext.getCurrentUser());
                        assetClassificationRepo.save(assetClasification);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("ASSET CLASSIFICATION WITH CODE " + assetClasification.getAssetClassificationCode() + " DELETED SUCCESSFULLY AT " + assetClasification.getDeletedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(assetClasification);
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
