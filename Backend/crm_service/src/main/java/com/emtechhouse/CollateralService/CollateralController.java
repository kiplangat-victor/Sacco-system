package com.emtechhouse.CollateralService;

import com.emtechhouse.CollateralService.CollateralDocuments.Collateraldocument;
import com.emtechhouse.CollateralService.CollateralDocuments.CollateraldocumentRepo;
import com.emtechhouse.Utils.EntityResponse;
import com.emtechhouse.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.Utils.HttpInterceptor.UserRequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
//@CrossOrigin
@RequestMapping("api/v1/collateral")
public class CollateralController {
    private final CollateralRepo collateralRepo;
    private final CollateralService collateralService;
    private final CollateraldocumentRepo collateraldocumentRepo;

    public CollateralController(CollateralRepo collateralRepo, CollateralService collateralService, CollateraldocumentRepo collateraldocumentRepo) {
        this.collateralRepo = collateralRepo;
        this.collateralService = collateralService;
        this.collateraldocumentRepo = collateraldocumentRepo;
    }


    @PostMapping("/add")
    public ResponseEntity<?> addCollateral(@RequestBody Collateral collateral) {
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
                    Optional<Collateral> collateral1 = collateralRepo.findByEntityIdAndCollateralCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), collateral.getCollateralCode(), 'N');
                    if (collateral1.isPresent()){
                        EntityResponse response = new EntityResponse();
                        response.setMessage("Serial Exists");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }else{
                        collateral.setPostedBy(UserRequestContext.getCurrentUser());
                        collateral.setEntityId(EntityRequestContext.getCurrentEntityId());
                        collateral.setPostedFlag('Y');
                        collateral.setPostedTime(new Date());

                        collateralService.addCollateral(collateral);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("COLLATERAL WITH SERIAL NUMBER: " + " "+  collateral.getCollateralCode() + " " + "CREATED SUCCESSFULLY"+ " " + "AT" + " " + collateral.getPostedTime());
//                        response.setMessage(HttpStatus.CREATED.getReasonPhrase());
                        response.setStatusCode(HttpStatus.CREATED.value());
                        response.setEntity(collateral);
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
    public ResponseEntity<?> getAllCollaterals() {
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
                    List<CollateralRepo.Collateralmin> collateral = collateralRepo.findByEntityIdAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), 'N');
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(collateral);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    @GetMapping("/find/{id}")
    public ResponseEntity<?> getCollateralById(@PathVariable("id") Long id) {
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
                    Optional<Collateral> collateral = collateralRepo.findById(id);
                    if (collateral.isPresent()){
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(collateral);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }else{
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

    @GetMapping("/find/image/by/{id}")
    public ResponseEntity<?> getImageById(@PathVariable("id") Long id) {
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
                    Optional<Collateraldocument> collateraldocument =collateraldocumentRepo.findById(id);
                    if ( collateraldocument.isPresent()){
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(collateraldocument);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }else{
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
    @PutMapping("/modify")
    public ResponseEntity<?> updateCollateral(@RequestBody Collateral collateral) {
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
                    collateral.setModifiedBy(UserRequestContext.getCurrentUser());
                    collateral.setEntityId(EntityRequestContext.getCurrentEntityId());
                    Optional<Collateral> collateral1 = collateralRepo.findById(collateral.getSn());
                    if (collateral1.isPresent()) {
                        collateral.setPostedTime(collateral1.get().getPostedTime());
                        collateral.setPostedFlag('Y');
                        collateral.setPostedBy(collateral1.get().getPostedBy());
                        collateral.setModifiedFlag('Y');
                        collateral.setVerifiedFlag('N');
                        collateral.setModifiedTime(new Date());
                        collateral.setModifiedBy(collateral.getModifiedBy());
                        collateralService.updateCollateral(collateral);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("COLLATERAL WITH SERIAL NUMBER: " + " " + collateral.getCollateralCode() + " " + "MODIFIED SUCCESSFULLY"+ " " + "AT" + " " + collateral.getModifiedTime());
//                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(collateral);
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
                    Optional<Collateral> collateral1 = collateralRepo.findById(Long.parseLong(id));
                    if (collateral1.isPresent()) {
                        Collateral collateral = collateral1.get();
                        //                    Check Maker Checker
                        if (collateral.getPostedBy().equalsIgnoreCase(UserRequestContext.getCurrentUser())){
                            EntityResponse response = new EntityResponse();
                            response.setMessage("You Can Not Verify What you initiated");
                            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                            response.setEntity("");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        }else{
                            collateral.setVerifiedFlag('Y');
                            collateral.setVerifiedTime(new Date());
                            collateral.setVerifiedBy(UserRequestContext.getCurrentUser());
                            collateralRepo.save(collateral);
                            EntityResponse response = new EntityResponse();
                            response.setMessage("COLLATERAL WITH SERIAL NUMBER: " + " " + collateral.getCollateralCode() + " " + "VERIFIED SUCCESSFULLY"+ " " + "AT" + " " + collateral.getVerifiedTime());
//                            response.setMessage(HttpStatus.OK.getReasonPhrase());
                            response.setStatusCode(HttpStatus.OK.value());
                            response.setEntity(collateral);
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
    public ResponseEntity<?> deleteCollateral(@PathVariable String id) {
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
                    Optional<Collateral> collateral1 = collateralRepo.findById(Long.parseLong(id));
                    if (collateral1.isPresent()) {
                        Collateral collateral = collateral1.get();
                        collateral.setDeletedFlag('Y');
                        collateral.setDeletedTime(new Date());
                        collateral.setDeletedBy(UserRequestContext.getCurrentUser());
                        collateralRepo.save(collateral);
                        EntityResponse response = new EntityResponse();
//                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setMessage("COLLATERAL WITH SERIAL NUMBER: " + " " + collateral.getCollateralCode() + " " + "DELETED SUCCESSFULLY"+ " " + "AT" + " " + collateral.getDeletedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(collateral);
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

    @GetMapping("/find/by/collateral-code/{collateralCode}")
    public ResponseEntity<?> getByCollateralCode(@PathVariable("collateralCode") String collateralCode) {
        try {
            EntityResponse response= collateralService.findByCollateralCode(collateralCode);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
}
