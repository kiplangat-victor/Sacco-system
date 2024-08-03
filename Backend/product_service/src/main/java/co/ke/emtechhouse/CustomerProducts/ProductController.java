package co.ke.emtechhouse.CustomerProducts;

import co.ke.emtechhouse.CustomerProducts.Dtos.ProdCodeDto;
import co.ke.emtechhouse.CustomerProducts.Dtos.ProductEventDto;
import co.ke.emtechhouse.CustomerProducts.Product_specific_details.LAA_Details.LoanLimitCondition;
import co.ke.emtechhouse.CustomerProducts.Product_specific_details.LAA_Details.LoanLimitRepo;
import co.ke.emtechhouse.CustomerProducts.product_gls.Productgls;
import co.ke.emtechhouse.CustomerProducts.product_gls.ProductglsRepo;
import co.ke.emtechhouse.Utils.HttpInterceptor.EntityRequestContext;
import co.ke.emtechhouse.Utils.HttpInterceptor.UserRequestContext;
import co.ke.emtechhouse.Utils.OkhttpService;
import co.ke.emtechhouse.Utils.Responses.EntityResponse;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@Api(value = "/product")
@RequestMapping("/api/v1/product")
public class ProductController {
    private final ProductRepo productRepo;
    private final ProductService productService;
    private final OkhttpService okhttpService;
    private final ProductglsRepo productglsRepo;
    @Autowired
    LoanLimitRepo loanLimitRepo;
    public ProductController(ProductRepo productRepo, ProductService productService, OkhttpService okhttpService, ProductglsRepo productglsRepo) {
        this.productRepo = productRepo;
        this.productService = productService;
        this.okhttpService = okhttpService;
        this.productglsRepo = productglsRepo;
    }

    public ResponseEntity<?> checkByInterestTableCode(@RequestParam String productCode) {
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
                    Optional<Product> checkproduct = productRepo.findByEntityIdAndProductCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), productCode, 'N');
                    if (!checkproduct.isPresent()) {
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(checkproduct);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        response.setMessage("Code Exists!");
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

    // TODO: LAA*********************************************************************************
    @PostMapping("/laa/add")
    public ResponseEntity<?> addLAAProduct(@RequestBody Product product) {
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
                    Optional<Product> checkproduct = productRepo.findByEntityIdAndProductCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), product.getProductCode(), 'N');
                    if (checkproduct.isPresent()) {
                        response.setMessage("Product " + checkproduct.get().getProductType() + " With Code " + checkproduct.get().getProductCode() + " Already Registered On " + checkproduct.get().getPostedTime());
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        product.setPostedBy(UserRequestContext.getCurrentUser());
                        product.setPostedFlag('Y');
                        product.setPostedTime(new Date());
                        product.setEntityId(EntityRequestContext.getCurrentEntityId());
                        product.setProductType("LAA");
                        Product addProduct = productService.add(product);
                        response.setMessage("Product Type " + product.getProductType() + " With Code " + product.getProductCode() + " Created Successfully At " + product.getPostedTime());
                        response.setStatusCode(HttpStatus.CREATED.value());
                        response.setEntity(addProduct);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }
                }
            }

        } catch (Exception e) {
            log.info("Error {} " + e);
            return null;
        }
    }

    @GetMapping("/laa/all")
    public ResponseEntity<?> getAllProducts() {
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
                    List<Product> productList = productRepo.findByEntityIdAndProductTypeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), "LAA", 'N');
                    if (productList.size() > 0) {
                        response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(productList);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.OK.value());
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


    @GetMapping("/laa/od")
    public ResponseEntity<?> getODProducts() {
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
                    List<Product> productList = productRepo.findByEntityIdAndProductCode(EntityRequestContext.getCurrentEntityId(), "OD");
                    if (productList.size() > 0) {
                        response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(productList);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.OK.value());
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


    @GetMapping("/laa")
    public ResponseEntity<?> getAllProducts(@RequestParam String productCode) {
        System.out.println("Product Code: "+productCode);
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
                    Optional<Product> checkproduct = productRepo.findByEntityIdAndProductCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), productCode, 'N');
                    if (checkproduct.isPresent()) {
                        Product product = checkproduct.get();
                        response.setMessage("Product " + checkproduct.get().getProductType() + " With Code " + checkproduct.get().getProductCode() + " Already Registered On " + checkproduct.get().getPostedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(product);
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




    @PutMapping("/laa/modify")
    public ResponseEntity<?> updateLAA(@RequestBody Product product) {
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
                    Optional<Product> checkproduct = productRepo.findByEntityIdAndProductCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), product.getProductCode(), 'N');
                    if (checkproduct.isPresent()) {
                        product.setPostedTime(checkproduct.get().getPostedTime());
                        product.setPostedBy(checkproduct.get().getPostedBy());
                        product.setEntityId(checkproduct.get().getEntityId());
                        product.setProductType(checkproduct.get().getProductType());
                        product.setModifiedFlag('Y');
                        product.setModifiedBy(UserRequestContext.getCurrentUser());
                        product.setModifiedTime(new Date());
                        Product updateProduct = productService.update(product);
                        response.setMessage("Product Type " + product.getProductType() + " With Code " + product.getProductCode() + " Modified Successfully At " + product.getModifiedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(updateProduct);
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

    @GetMapping("/laa/productlimits")
    public ResponseEntity<?> getLoanLimits(@RequestParam String productCode) {
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
                    Optional<Product> checkproduct = productRepo.findByEntityIdAndProductCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), productCode, 'N');
                    if (checkproduct.isPresent()) {
                        List<LoanLimitCondition> loanLimitConditions = loanLimitRepo.findAllByProductCode(productCode);
                        if (loanLimitConditions.size() == 0) {
                            response.setMessage("This product has no limits set");
                            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
                        }
                        response.setMessage("Has loan limits");
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(loanLimitConditions);
                        return new ResponseEntity<>(response, HttpStatus.FOUND);
                    } else {
                        response.setMessage("This product not found or deleted");
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
                    }
                }
            }

        } catch (Exception e) {
            log.info("Error {} " + e);
            return null;
        }
    }

    // TODO: CAA*********************************************************************************
    @PostMapping("/caa/add")
    public ResponseEntity<?> addCAAproduct(@RequestBody Product product) {
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
                    Optional<Product> checkproduct = productRepo.findByEntityIdAndProductCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), product.getProductCode(), 'N');
                    if (checkproduct.isPresent()) {
                        response.setMessage("Product " + checkproduct.get().getProductType() + " With Code " + checkproduct.get().getProductCode() + " Already Registered On " + checkproduct.get().getPostedTime());
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                    } else {
                        product.setPostedBy(UserRequestContext.getCurrentUser());
                        product.setEntityId(EntityRequestContext.getCurrentEntityId());
                        product.setProductType("CAA");
                        product.setPostedTime(new Date());
                        product.setPostedFlag('Y');
                        Product addProduct = productService.add(product);
                        response.setMessage("Product Type " + product.getProductType() + " With Code " + product.getProductCode() + " Registered Successfully At " + product.getPostedTime());
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

    @GetMapping("/caa/all")
    public ResponseEntity<?> getCAA() {
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
                } else {
                    List<Product> productList = productRepo.findByEntityIdAndProductTypeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), "CAA", 'N');
                    if (productList.size() > 0) {
                        response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(productList);
                    }
                    else {
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

    @GetMapping("/caa")
    public ResponseEntity<?> getCaa(@RequestParam String productCode) {
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
                    Optional<Product> checkproduct = productRepo.findByEntityIdAndProductCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), productCode, 'N');
                    if (checkproduct.isPresent()) {
                        Product product = checkproduct.get();
                        response.setMessage("Product " + checkproduct.get().getProductType() + " With Code " + checkproduct.get().getProductCode() + " Already Registered On " + checkproduct.get().getPostedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(product);
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

    @PutMapping("/caa/modify")
    public ResponseEntity<?> updateproduct(@RequestBody Product product) {
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
                    Optional<Product> checkproduct = productRepo.findByEntityIdAndProductCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), product.getProductCode(), 'N');
                    if (checkproduct.isPresent()) {
                        Product pProduct= checkproduct.get();
                        pProduct.setAutoAddedAccounts(null);
                        //
                        product.setPostedTime(checkproduct.get().getPostedTime());
                        product.setPostedBy(checkproduct.get().getPostedBy());
                        product.setEntityId(checkproduct.get().getEntityId());
                        product.setProductType(checkproduct.get().getProductType());
                        product.setModifiedFlag('Y');
                        product.setVerifiedFlag('N');
                        product.setModifiedBy(UserRequestContext.getCurrentUser());
                        product.setModifiedTime(new Date());
                        Product updateProduct = productService.update(product);
                        response.setMessage("Product Type " + product.getProductType() + " With Code " + product.getProductCode() + " Modified Successfully At " + product.getModifiedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(updateProduct);
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

    // TODO: SBA*********************************************************************************
    @PostMapping("/sba/add")
    public ResponseEntity<?> addSBAproduct(@RequestBody Product product) {
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
                    Optional<Product> checkproduct = productRepo.findByEntityIdAndProductCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), product.getProductCode(), 'N');
                    if (checkproduct.isPresent()) {
                        response.setMessage("Product " + checkproduct.get().getProductType() + " With Code " + checkproduct.get().getProductCode() + " Already Registered On " + checkproduct.get().getPostedTime());
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                    } else {
                        if (product.getSbaDetails().getWithdrawalsAllowed().equals(true) && product.getSbaDetails().getWithdrawalCharge() == null) {
                            response.setMessage("You must provide charge code when withdrawals is checked!");
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            response.setEntity("");
                        } else {
                            product.setPostedBy(UserRequestContext.getCurrentUser());
                            product.setEntityId(EntityRequestContext.getCurrentEntityId());
                            product.setProductType("SBA");
                            product.setPostedTime(new Date());
                            product.setPostedFlag('Y');
                            Product addProduct = productService.add(product);
                            response.setMessage("Product " + product.getProductType() + " With Code " + product.getProductCode() + " Registered Successfully At " + product.getPostedTime());
                            response.setStatusCode(HttpStatus.CREATED.value());
                            response.setEntity(addProduct);
                        }
                    }
                }
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Error {} " + e);
            return null;
        }
    }

    @GetMapping("/sba/all")
    public ResponseEntity<?> getSBA() {
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
                    List<Product> productList = productRepo.findByEntityIdAndProductTypeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), "SBA", 'N');
                    if(productList.size()>0){
                        response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(productList);
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

    @GetMapping("/sba")
    public ResponseEntity<?> getSBA(@RequestParam String productCode) {
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
                    Optional<Product> checkproduct = productRepo.findByEntityIdAndProductCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), productCode, 'N');
                    if (checkproduct.isPresent()) {
                        Product product = checkproduct.get();
                        response.setMessage("Product " + checkproduct.get().getProductType() + " With Code " + checkproduct.get().getProductCode() + " Already Registered On " + checkproduct.get().getPostedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(product);
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

    @PutMapping("/sba/modify")
    public ResponseEntity<?> updateSBAProduct(@RequestBody Product product) {
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
                    Optional<Product> checkproduct = productRepo.findByEntityIdAndProductCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), product.getProductCode(), 'N');
                    if (checkproduct.isPresent()) {
                        product.setPostedTime(checkproduct.get().getPostedTime());
                        product.setPostedBy(checkproduct.get().getPostedBy());
                        product.setEntityId(checkproduct.get().getEntityId());
                        product.setProductType(checkproduct.get().getProductType());
                        product.setModifiedFlag('Y');
                        product.setVerifiedFlag('N');
                        product.setModifiedBy(UserRequestContext.getCurrentUser());
                        product.setModifiedTime(new Date());
                        Product updateProduct = productService.update(product);
                        response.setMessage("Product Type " + product.getProductType() + " With Code " + product.getProductCode() + " Modified Successfully At " + product.getModifiedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(updateProduct);
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

    // TODO: ODA*********************************************************************************
    @PostMapping("/oda/add")
    public ResponseEntity<?> addODAProduct(@RequestBody Product product) {
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
                    Optional<Product> checkproduct = productRepo.findByEntityIdAndProductCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), product.getProductCode(), 'N');
                    if (checkproduct.isPresent()) {
                        response.setMessage("Product Type " + checkproduct.get().getProductType() + " With Code " + checkproduct.get().getProductCode() + " Already Registered On " + checkproduct.get().getPostedTime());
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                    } else {
                        product.setPostedBy(UserRequestContext.getCurrentUser());
                        product.setEntityId(EntityRequestContext.getCurrentEntityId());
                        product.setProductType("ODA");
                        product.setPostedTime(new Date());
                        product.setPostedFlag('Y');
                        Product addProduct = productService.add(product);
                        response.setMessage("Product Type " + product.getProductType() + " With Code " + product.getProductCode() + " Registered Successfully At " + product.getPostedTime());
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

    @GetMapping("/oda/all")
    public ResponseEntity<?> getODA() {
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
                    List<Product> productList = productRepo.findByEntityIdAndProductTypeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), "ODA", 'N');
                    if (productList.size() > 0) {
                        response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(productList);
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

    @GetMapping("/oda")
    public ResponseEntity<?> getODA(@RequestParam String productCode) {
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
                    Optional<Product> checkproduct = productRepo.findByEntityIdAndProductCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), productCode, 'N');
                    if (checkproduct.isPresent()) {
                        Product product = checkproduct.get();
                        response.setMessage("Product " + checkproduct.get().getProductType() + " With Code " + checkproduct.get().getProductCode() + " Already Registered On " + checkproduct.get().getPostedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(product);
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

    @PutMapping("/oda/modify")
    public ResponseEntity<?> updateODAproduct(@RequestBody Product product) {
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
                    Optional<Product> checkproduct = productRepo.findByEntityIdAndProductCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), product.getProductCode(), 'N');
                    if (checkproduct.isPresent()) {
                        product.setPostedTime(checkproduct.get().getPostedTime());
                        product.setPostedBy(checkproduct.get().getPostedBy());
                        product.setEntityId(checkproduct.get().getEntityId());
                        product.setProductType(checkproduct.get().getProductType());
                        product.setModifiedFlag('Y');
                        product.setVerifiedFlag(checkproduct.get().getVerifiedFlag());
                        product.setModifiedBy(UserRequestContext.getCurrentUser());
                        product.setModifiedTime(new Date());
                        Product updateProduct = productService.update(product);
                        response.setMessage("Product Type " + product.getProductType() + " With Code " + product.getProductCode() + " Modified Successfully At " + product.getModifiedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(updateProduct);
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

    // TODO: TDA*********************************************************************************
    @PostMapping("/tda/add")
    public ResponseEntity<?> addTDAproduct(@RequestBody Product product) {
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
                    Optional<Product> checkproduct = productRepo.findByEntityIdAndProductCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), product.getProductCode(), 'N');
                    if (checkproduct.isPresent()) {
                        response.setMessage("Product " + checkproduct.get().getProductType() + " With Code " + checkproduct.get().getProductCode() + " Already Registered On " + checkproduct.get().getPostedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        product.setPostedBy(UserRequestContext.getCurrentUser());
                        product.setEntityId(EntityRequestContext.getCurrentEntityId());
                        product.setProductType("TDA");
                        product.setPostedTime(new Date());
                        product.setPostedFlag('Y');
                        Product addProduct = productService.add(product);
                        response.setMessage("Product " + product.getProductType() + " With Code " + product.getProductCode() + " Registered Successfully At " + product.getPostedTime());
                        response.setStatusCode(HttpStatus.CREATED.value());
                        response.setEntity(addProduct);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }
                }
            }
        } catch (Exception e) {
            log.info("Error {} " + e);
            return null;
        }
    }

    @GetMapping("/tda/all")
    public ResponseEntity<?> getTDA() {
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
                    List<Product> productList = productRepo.findByEntityIdAndProductTypeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), "TDA", 'N');
                    if (productList.size() > 0) {
                        response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(productList);
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

    @GetMapping("/tda")
    public ResponseEntity<?> getTDA(@RequestParam String productCode) {
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
                    Optional<Product> checkproduct = productRepo.findByEntityIdAndProductCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), productCode, 'N');
                    if (checkproduct.isPresent()) {
                        response.setMessage("Product " + checkproduct.get().getProductType() + " With Code " + checkproduct.get().getProductCode() + " Already Registered On " + checkproduct.get().getPostedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(checkproduct.get());
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

    @PutMapping("/tda/modify")
    public ResponseEntity<?> updateTDAproduct(@RequestBody Product product) {
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
                    Optional<Product> checkproduct = productRepo.findByEntityIdAndProductCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), product.getProductCode(), 'N');
                    if (checkproduct.isPresent()) {
                        product.setPostedTime(checkproduct.get().getPostedTime());
                        product.setPostedBy(checkproduct.get().getPostedBy());
                        product.setEntityId(checkproduct.get().getEntityId());
                        product.setProductType(checkproduct.get().getProductType());
                        product.setModifiedFlag('Y');
                        product.setVerifiedFlag(checkproduct.get().getVerifiedFlag());
                        product.setModifiedBy(UserRequestContext.getCurrentUser());
                        product.setModifiedTime(new Date());
                        Product updateProduct = productService.update(product);
                        response.setMessage("Product Type " + product.getProductType() + " With Code " + product.getProductCode() + " Modified Successfully At " + product.getModifiedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(updateProduct);
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

    //    TODO: General Details
    @PutMapping("/verify/{productCode}")
    public ResponseEntity<?> updateproduct(@PathVariable("productCode") String productCode) {
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
                    Optional<Product> checkproduct = productRepo.findByEntityIdAndProductCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), productCode, 'N');
                    if (checkproduct.isPresent()) {
                        Product product = checkproduct.get();
                        if (product.getPostedBy().equalsIgnoreCase(UserRequestContext.getCurrentUser())) {
                            EntityResponse response = new EntityResponse();
                            response.setMessage("You Can Not Verify What you initiated");
                            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                            response.setEntity("");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        } else if (product.getVerifiedFlag().equals('Y')) {
                            EntityResponse response = new EntityResponse();
                            response.setMessage("Product " + checkproduct.get().getProductType() + " With Code " + checkproduct.get().getProductCode() + " Already Verified On " + checkproduct.get().getVerifiedTime());
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            response.setEntity("");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        } else {
                            product.setVerifiedFlag('Y');
                            product.setVerifiedTime(new Date());
                            product.setVerifiedBy(UserRequestContext.getCurrentUser());
                            Product VerifyProduct = productRepo.save(product);
                            EntityResponse response = new EntityResponse();
                            response.setMessage("Product " + product.getProductType() + " With Code " + product.getProductCode() + " Verified Successfully At" + product.getVerifiedTime());
                            response.setStatusCode(HttpStatus.OK.value());
                            response.setEntity(VerifyProduct);
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
            log.info("Error {} " + e);
            return null;
        }
    }

    //    TODO: Get Interest
    @GetMapping("/interest/value")
    public ResponseEntity<?> getInterestRate(@RequestParam String productCode, Double amount) {
        System.out.println("Have arrived to fetch interest");
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
                    System.out.println("Product code: "+productCode);
                    Optional<Product> checkproduct = productRepo.findByEntityIdAndProductCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), productCode, 'N');
                    if (checkproduct.isPresent()) {
                        System.out.println("In check");
                        Product product = checkproduct.get();
                        if (product.getVerifiedFlag() == 'N') {
                            response.setMessage("Product Not Verified!");
                            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                            response.setEntity("");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        } else {
                            String code = product.getInterest_table_code();
                            log.info("The code: "+code);
                            if (code != null && amount != null) {
                                response = okhttpService.getInterestCode(code, amount);
                                response.setMessage(response.getMessage());
                                response.setStatusCode(response.getStatusCode());
                                response.setEntity(response.getEntity());
                            } else {
                                response.setMessage("Code and ammount cannot be null");
                                response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                                response.setEntity("");
                            }
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        }
                    } else {
                        response.setMessage("Product " + HttpStatus.NOT_FOUND.getReasonPhrase());
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

    @DeleteMapping("/delete/{productCode}")
    public ResponseEntity<?> delete(@PathVariable("productCode") String productCode) {
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
                    Optional<Product> checkproduct = productRepo.findByEntityIdAndProductCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), productCode, 'N');
                    if (checkproduct.isPresent()) {
                        if (checkproduct.get().getDeletedFlag().equals('Y')) {
                            EntityResponse response = new EntityResponse();
                            response.setMessage("Product " + checkproduct.get().getProductType() + " With Code " + checkproduct.get().getProductCode() + " Already Deleted On " + checkproduct.get().getDeletedTime());
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            response.setEntity("");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        } else {
                            Product product = checkproduct.get();
                            product.setDeletedFlag('Y');
                            product.setDeletedBy(UserRequestContext.getCurrentUser());
                            product.setDeletedTime(new Date());
                            Product deleteProduct = productRepo.save(product);
                            EntityResponse response = new EntityResponse();
                            response.setMessage("Product " + product.getProductType() + " With Code " + product.getProductCode() + " Deleted Successfully At" + product.getDeletedTime());
                            response.setStatusCode(HttpStatus.OK.value());
                            response.setEntity(deleteProduct);
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
            log.info("Error {} " + e);
            return null;
        }
    }

    @GetMapping("/find/by/glsubhead/{glsubhead}")
    public ResponseEntity<?> checkGlSUbhead(@PathVariable String glsubhead) {
        try {
            boolean available = false;
            List<Productgls> productgls = productglsRepo.findByGl__Subhead(glsubhead);
            System.out.println(productgls.size());
            System.out.println(glsubhead);
            if (productgls.size() > 0) {
                available = true;
            } else {
                available = false;
            }
            EntityResponse response = new EntityResponse();
            response.setMessage(String.valueOf(available));
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(available);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Error {} " + e);
            EntityResponse response = new EntityResponse();
            response.setEntity(HttpStatus.OK.getReasonPhrase());
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(e.toString());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

    }

    // TODO: 3/17/2023 Account look up
    @GetMapping("product/lookUp")
    public ResponseEntity<?> getProductLookup(@RequestParam(required = false) String productType, @RequestParam(required = false) String productCode) {
        try {
            EntityResponse res = productService.productLookup(productType, productCode);
            return ResponseEntity.status(HttpStatus.OK).body(res);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("product/ledger/events")
    public ResponseEntity<?> getProdLedgerEvents(@RequestBody List<ProdCodeDto> productCode) {
        try {
            EntityResponse res = productService.getProductLedgerEvents(productCode);
            return ResponseEntity.status(HttpStatus.OK).body(res);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
}
