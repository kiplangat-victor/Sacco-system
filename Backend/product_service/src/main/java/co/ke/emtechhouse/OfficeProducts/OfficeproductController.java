package co.ke.emtechhouse.OfficeProducts;

import co.ke.emtechhouse.Utils.HttpInterceptor.EntityRequestContext;
import co.ke.emtechhouse.Utils.HttpInterceptor.UserRequestContext;
import co.ke.emtechhouse.Utils.Responses.EntityResponse;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@Api(value = "/officeproduct/office")
@RequestMapping("/api/v1/product/office")
public class OfficeproductController {
    private final OfficeproductRepo officeproductRepo;
    private final OfficeproductService officeproductService;

    public OfficeproductController(OfficeproductRepo officeproductRepo, OfficeproductService officeproductService) {
        this.officeproductRepo = officeproductRepo;
        this.officeproductService = officeproductService;
    }

    @PostMapping("/oab/add")
    public ResponseEntity<?> addOABProduct(@RequestBody Officeproduct officeproduct) {
        try {
            EntityResponse response = new EntityResponse();
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                } else {
                    Optional<Officeproduct> checkpPoduct = officeproductRepo.findByEntityIdAndProductCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), officeproduct.getProductCode(), 'N');
                    if (checkpPoduct.isPresent()) {
                        response.setMessage("Product Type " + checkpPoduct.get().getProductType() + " With Code " + checkpPoduct.get().getProductCode() + " Already Registered On " + checkpPoduct.get().getPostedTime());
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                    } else {
                        officeproduct.setPostedBy(UserRequestContext.getCurrentUser());
                        officeproduct.setEntityId(EntityRequestContext.getCurrentEntityId());
                        officeproduct.setPostedFlag('Y');
                        officeproduct.setPostedTime(new Date());
                        officeproduct.setProductType("OAB");
                        officeproduct.setEffectiveDate(officeproduct.getEffective_from_date());
                        officeproduct.setExpiryDate(officeproduct.getEffective_to_date());
                        officeproduct.setShortName(officeproduct.getSchemeSupervisorID() + officeproduct.getSchemeName());
                        Officeproduct addProduct = officeproductService.add(officeproduct);
                        response.setMessage(officeproduct.getProductType() + " Product Type With Code " + officeproduct.getProductCode() + " Registered Successfully At " + officeproduct.getPostedTime());
                        response.setStatusCode(HttpStatus.CREATED.value());
                        response.setEntity(addProduct);
                    }
                }
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Error {} " + e);
            return null;
        }
    }

    @GetMapping("/oab/all")
    public ResponseEntity<?> getAllOABProducts() {
        try {
            EntityResponse response = new EntityResponse();
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                } else {
                    List<Officeproduct> officeProductList = officeproductRepo.findByEntityIdAndProductTypeAndDeletedFlagOrderByIdDesc(EntityRequestContext.getCurrentEntityId(), "OAB", 'N');
                    if (officeProductList.size() > 0) {
                        response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(officeProductList);
                    } else {
                        response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity("");
                    }
                }
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Error {} " + e);
            return null;
        }
    }

    @GetMapping("/oab/{productCode}")
    public ResponseEntity<?> findByCode(@PathVariable("productCode") String productCode) {
        try {
            EntityResponse response = new EntityResponse();
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                } else {
                    Optional<Officeproduct> checkProduct = officeproductRepo.findByEntityIdAndProductCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), productCode, 'N');
                    if (checkProduct.isPresent()) {
                        Officeproduct officeproduct = checkProduct.get();
                        response.setMessage("Product " + checkProduct.get().getProductType() + " With Code " + checkProduct.get().getProductCode() + " Already Registered On " + checkProduct.get().getPostedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(officeproduct);
                    } else {
                        response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity("");
                    }
                }
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Error {} " + e);
            return null;
        }
    }

    @PutMapping("/oab/modify")
    public ResponseEntity<?> update(@RequestBody Officeproduct officeproduct) {
        try {
            EntityResponse response = new EntityResponse();
            Optional<Officeproduct> checkofficeproduct = officeproductRepo.findByEntityIdAndProductCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), officeproduct.getProductCode(), 'N');
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
                    if (checkofficeproduct.isPresent()) {
                        officeproduct.setModifiedBy(UserRequestContext.getCurrentUser());
                        officeproduct.setProductType(checkofficeproduct.get().getProductType());
                        officeproduct.setPostedTime(checkofficeproduct.get().getPostedTime());
                        officeproduct.setPostedBy(checkofficeproduct.get().getPostedBy());
                        officeproduct.setEntityId(checkofficeproduct.get().getEntityId());
                        officeproduct.setModifiedFlag('Y');
                        officeproduct.setVerifiedFlag('N');
                        officeproduct.setModifiedTime(new Date());
                        Officeproduct modifyProduct = officeproductService.update(officeproduct);
                        response.setMessage(officeproduct.getProductType() + " Product Type With Code " + officeproduct.getProductCode() + " Modified Successfully At " + officeproduct.getModifiedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(modifyProduct);
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
            log.info("Error {} " + e);
            return null;
        }
    }

    @PutMapping("/verify/{productCode}")
    public ResponseEntity<?> verify(@PathVariable("productCode") String productCode) {
        try {
            EntityResponse response = new EntityResponse();
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                } else {
                    Optional<Officeproduct> checkOfficeProduct = officeproductRepo.findByEntityIdAndProductCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), productCode, 'N');
                    if (checkOfficeProduct.isPresent()) {
                        Officeproduct officeproduct = checkOfficeProduct.get();
                        if (checkOfficeProduct.get().getPostedBy().equalsIgnoreCase(UserRequestContext.getCurrentUser())) {
                            response.setMessage("You Can Not Verify What you initiated");
                            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                            response.setEntity("");
                        } else if (checkOfficeProduct.get().getVerifiedFlag().equals('Y')) {
                            response.setMessage("Product " + checkOfficeProduct.get().getProductType() + " With Code " + checkOfficeProduct.get().getProductCode() + " Already Verified On " + checkOfficeProduct.get().getVerifiedTime());
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            response.setEntity("");
                        } else {
                            officeproduct.setVerifiedFlag('Y');
                            officeproduct.setVerifiedTime(new Date());
                            officeproduct.setVerifiedBy(UserRequestContext.getCurrentUser());
                            Officeproduct VerifyProduct = officeproductRepo.save(officeproduct);
                            response.setMessage("Product " + officeproduct.getProductType() + " With Code " + officeproduct.getProductCode() + " Verified Successfully At" + officeproduct.getVerifiedTime());
                            response.setStatusCode(HttpStatus.OK.value());
                            response.setEntity(VerifyProduct);
                        }
                    } else {
                        response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity("");
                    }
                }
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Error {} " + e);
            return null;
        }
    }

    @DeleteMapping("/temp/delete/{productCode}")
    public ResponseEntity<?> tempDelete(@PathVariable("productCode") String productCode) {
        try {
            EntityResponse response = new EntityResponse();
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                } else {
                    Optional<Officeproduct> checkofficeproduct = officeproductRepo.findByEntityIdAndProductCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), productCode, 'N');
                    if (checkofficeproduct.isPresent()) {
                        Officeproduct officeproduct = checkofficeproduct.get();
                        officeproduct.setDeletedFlag('Y');
                        officeproduct.setDeletedBy(UserRequestContext.getCurrentUser());
                        officeproduct.setDeletedTime(new Date());
                        Officeproduct tempDelete = officeproductRepo.save(officeproduct);
                        response.setMessage("Product " + officeproduct.getProductType() + " With Code " + officeproduct.getProductCode() + " Deleted Successfully At " + officeproduct.getDeletedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(tempDelete);
                    } else {
                        response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity("");
                    }
                }
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Error {} " + e);
            return null;
        }
    }
}