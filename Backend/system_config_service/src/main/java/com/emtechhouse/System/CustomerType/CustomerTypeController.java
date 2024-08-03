package com.emtechhouse.System.CustomerType;

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

@RestController
@Slf4j
//@CrossOrigin
@RequestMapping("customertype")
public class CustomerTypeController {
    private final CustomerTypeService customerTypeService;
    private final CustomerTypeRepo customerTypeRepo;

    public CustomerTypeController(CustomerTypeService customerTypeService, CustomerTypeRepo customerTypeRepo) {
        this.customerTypeService = customerTypeService;
        this.customerTypeRepo = customerTypeRepo;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addCustomerType(@RequestBody CustomerType customerType) {
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
                    Optional<CustomerType> customerType1 = customerTypeRepo.findByCustomerType(customerType.getCustomerType());
                    if (customerType1.isPresent()) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("CODE GENERATION TYPE WITH REF " + customerType.getCustomerType() + " ALREADY REGISTERED! ENTER UNIQUE TYPE CODE TO CONTINUE");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        if (customerType.getCustomerType().length() > 10) {
                            EntityResponse response = new EntityResponse();
                            response.setMessage("Code Generation with Customer Type" + customerType.getCustomerType() + " is not ALLOWED: !!");
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        } else {
                            customerType.setPostedBy(UserRequestContext.getCurrentUser());
                            customerType.setEntityId(EntityRequestContext.getCurrentEntityId());
                            customerType.setPostedFlag('Y');
                            customerType.setPostedTime(new Date());
                            CustomerType addCustomerType = customerTypeService.addCustomerType(customerType);
                            EntityResponse response = new EntityResponse();
                            response.setMessage("CODE GENERATION TYPE WITH REF " + customerType.getCustomerType() + " CREATED SUCCESSFULLY AT " + customerType.getPostedTime());
                            response.setStatusCode(HttpStatus.CREATED.value());
                            response.setEntity(addCustomerType);
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
    public ResponseEntity<?> getAllCustomerTypes() {
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
                    List<CustomerType> customerType = customerTypeRepo.findAllByEntityIdAndDeletedFlagOrderByIdDesc(EntityRequestContext.getCurrentEntityId(), 'N');
                    if (customerType.size() > 0) {
                        response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(customerType);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity(customerType);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/all/by/table/{db_table}")
    public ResponseEntity<?> getAllCustomerTypesByTable(@PathVariable("db_table") String db_table) {
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
                    List<CustomerType> customerType = customerTypeRepo.findByDb_table(db_table);
                    if (customerType.size() > 0) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(customerType);
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

    @GetMapping("/check/customer_type/{customer_type}")
    public ResponseEntity<?> findcustomer_type(@PathVariable("customer_type") String customer_type) {
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
                    Optional<CustomerType> searchcustomer_type = customerTypeRepo.findByCustomerType(customer_type);
                    if (!searchcustomer_type.isPresent()) {
                        response.setMessage("CODE GENERATION TYPE WITH REF " + customer_type + " AVAILABLE FOR REGISTRATION");
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity("");

                    } else {
                        CustomerType customerType = customerTypeService.findcustomer_type(customer_type);
                        response.setMessage("CODE GENERATION TYPE WITH REF " + customer_type + " ALREADY REGISTERED ON " + searchcustomer_type.get().getPostedTime());
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(customerType);
                    }
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<?> getCustomerTypeById(@PathVariable("id") Long id) {
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

                    Optional<CustomerType> searchCustomerTypeId = customerTypeRepo.findById(id);
                    if (!searchCustomerTypeId.isPresent()) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("CODE GENERATION TYPE WITH REF DOES NOT EXIST!! PLEASE PROVIDE A VALID REGISTERED REFERENCE CODE");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        CustomerType customerType = customerTypeService.findById(id);
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(customerType);
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
    public ResponseEntity<?> updateCustomerType(@RequestBody CustomerType customerType) {
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
                    customerType.setModifiedBy(UserRequestContext.getCurrentUser());
                    customerType.setEntityId(EntityRequestContext.getCurrentEntityId());
                    Optional<CustomerType> customerType1 = customerTypeRepo.findById(customerType.getId());
                    if (customerType1.isPresent()) {
                        customerType.setPostedTime(customerType1.get().getPostedTime());
                        customerType.setPostedFlag(customerType1.get().getPostedFlag());
                        customerType.setPostedBy(customerType1.get().getPostedBy());
                        customerType.setVerifiedFlag(customerType1.get().getVerifiedFlag());
                        customerType.setModifiedFlag('Y');
                        customerType.setModifiedTime(new Date());
                        customerType.setModifiedBy(customerType.getModifiedBy());
                        customerTypeService.updateCustomerType(customerType);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("CODE GENERATION TYPE WITH REF" + " " + customerType.getCustomerType() + " " + "MODIFIED SUCCESSFULLY" + " AT " + customerType.getModifiedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(customerType);
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
                    Optional<CustomerType> customerType1 = customerTypeRepo.findById(Long.parseLong(id));
                    if (customerType1.isPresent()) {
                        CustomerType customerType = customerType1.get();
                        if (customerType.getPostedBy().equalsIgnoreCase(UserRequestContext.getCurrentUser())) {
                            EntityResponse response = new EntityResponse();
                            response.setMessage("You Can Not Verify What you initiated");
                            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                            response.setEntity("");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        } else {
                            if (customerType1.get().getVerifiedFlag().equals('Y')) {
                                EntityResponse response = new EntityResponse();
                                response.setMessage("CODE GENERATION TYPE WITH REF" + " " + customerType1.get().getCustomerType() + " " + " ALREADY VERIFIED ON " + customerType1.get().getVerifiedTime());
                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                response.setEntity("");
                                return new ResponseEntity<>(response, HttpStatus.OK);
                            } else {
                                customerType.setVerifiedFlag('Y');
                                customerType.setVerifiedTime(new Date());
                                customerType.setVerifiedBy(UserRequestContext.getCurrentUser());
                                customerTypeRepo.save(customerType);
                                EntityResponse response = new EntityResponse();
                                response.setMessage("CODE GENERATION TYPE WITH REF" + " " + customerType.getCustomerType() + " " + "VERIFIED SUCCESSFULLY" + " AT " + customerType.getVerifiedTime());
                                response.setStatusCode(HttpStatus.OK.value());
                                response.setEntity(customerType);
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
    public ResponseEntity<?> deleteCustomerType(@PathVariable String id) {
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
                    Optional<CustomerType> customerType1 = customerTypeRepo.findById(Long.parseLong(id));
                    if (customerType1.isPresent()) {
                        CustomerType customerType = customerType1.get();
                        customerType.setDeletedFlag('Y');
                        customerType.setDeletedTime(new Date());
                        customerType.setDeletedBy(UserRequestContext.getCurrentUser());
                        customerTypeRepo.save(customerType);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("CODE GENERATION TYPE WITH REF" + " " + customerType.getCustomerType() + " " + "DELETED SUCCESSFULLY" + " AT " + customerType.getDeletedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(customerType);
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