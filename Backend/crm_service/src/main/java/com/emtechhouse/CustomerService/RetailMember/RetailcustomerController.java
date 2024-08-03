package com.emtechhouse.CustomerService.RetailMember;

import com.emtechhouse.CustomerService.CustomerImage.CustomerImage;
import com.emtechhouse.CustomerService.CustomerImage.CustomerImageRepository;
import com.emtechhouse.CustomerService.GroupMember.GroupMemberRepository;
import com.emtechhouse.DTO.Member;
import com.emtechhouse.DTO.MemberClass;
import com.emtechhouse.NotificationComponent.EmailService.MailService;
import com.emtechhouse.NotificationComponent.SmsService.SMSService;
import com.emtechhouse.Utils.*;
import com.emtechhouse.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.Utils.HttpInterceptor.UserRequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("api/v1/customer/retail")
public class RetailcustomerController {
    private final RetailcustomerRepo retailcustomerRepo;
    private final RetailcustomerService retailcustomerService;
    private final CustomerImageRepository customerImageRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final CustomercodeService customercodeService;
    private final SMSService smsService;
    private final MailService mailService;
    private final ServiceCaller serviceCaller;
    @Autowired
    private DatesCalculator datesCalculator;

    public RetailcustomerController(RetailcustomerRepo retailcustomerRepo, RetailcustomerService retailcustomerService, CustomerImageRepository customerImageRepository, GroupMemberRepository groupMemberRepository, CustomercodeService customercodeService, SMSService smsService, MailService mailService, ServiceCaller serviceCaller) {
        this.retailcustomerRepo = retailcustomerRepo;
        this.retailcustomerService = retailcustomerService;
        this.customerImageRepository = customerImageRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.customercodeService = customercodeService;
        this.smsService = smsService;
        this.mailService = mailService;
        this.serviceCaller = serviceCaller;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addRetailcustomer(@RequestBody Retailcustomer retailcustomer) {
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
//                    TODO: CHECK AND SET UNIQUE ID
                    if (retailcustomer.getBirthCertificateNo() == null && retailcustomer.getNationalId() == null) {
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        if (retailcustomer.getNationalId() == null || retailcustomer.getNationalId().isEmpty()) {
                            retailcustomer.setUniqueId(retailcustomer.getBirthCertificateNo());
                            retailcustomer.setUniqueType("Birth Certificate");
                        } else {
                            retailcustomer.setUniqueId(retailcustomer.getNationalId());
                            retailcustomer.setUniqueType("National ID");
                        }
//                        TODO: DEDUP CHECK
                        Optional<Retailcustomer> dedupcheck = retailcustomerRepo.findByEntityIdAndUniqueIdAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), retailcustomer.getUniqueId(), 'N');
                        if (dedupcheck.isPresent()) {
                            response.setMessage("The Provided Unique identity: " + retailcustomer.getUniqueId() + " is already registered with this customer code: " + dedupcheck.get().getCustomerCode() + " Fullname: " + dedupcheck.get().getFirstName() + " " + dedupcheck.get().getMiddleName() + " " + dedupcheck.get().getLastName());
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            response.setEntity("");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        } else {
//                            TODO: CHECK DEDUP BY KRA
                            Optional<Retailcustomer> dedupcheckKra = retailcustomerRepo.findByEntityIdAndKraPinAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), retailcustomer.getKraPin(), 'N');
                            if (dedupcheckKra.isPresent() && dedupcheckKra.get().getKraPin() != null) {
                                response.setMessage("The Provided KRA: " + retailcustomer.getKraPin() + " is already registered with this customer code: " + dedupcheckKra.get().getCustomerCode() + " Fullname: " + dedupcheckKra.get().getFirstName() + " " + dedupcheckKra.get().getMiddleName() + " " + dedupcheckKra.get().getLastName());
                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                response.setEntity("");
                                return new ResponseEntity<>(response, HttpStatus.OK);
                            } else {
                                System.out.println("KRA check: not existing");
//                            TODO: CHECK DEDUP BY Phone
                                Optional<Retailcustomer> dedupcheckPhone = retailcustomerRepo.findByEntityIdAndPhoneNumberAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), retailcustomer.getPhoneNumber(), 'N');
                                if (dedupcheckPhone.isPresent() && dedupcheckPhone.get().getPhoneNumber() != null) {
                                    response.setMessage("The Provided Phone: " + retailcustomer.getPhoneNumber() + " is already registered with this customer code: " + dedupcheckPhone.get().getCustomerCode() + " Fullname: " + dedupcheckPhone.get().getFirstName() + " " + dedupcheckPhone.get().getMiddleName() + " " + dedupcheckPhone.get().getLastName());
                                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                    response.setEntity("");
                                    return new ResponseEntity<>(response, HttpStatus.OK);
                                } else {
                                    if (retailcustomer.getPhoneNumber() == null || retailcustomer.getPhoneNumber().trim().isEmpty()) {
                                        response.setMessage("Phone number is required!");
                                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                    } else {
                                        if (retailcustomer.getPhoneNumber().length() != 12) {
                                            response.setMessage("Phone number format " + retailcustomer.getPhoneNumber() + " is invalid. Kindly provide a 12 char i.e 254********* !");
                                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                        } else {
                                            retailcustomer.setPostedBy(UserRequestContext.getCurrentUser());
                                            retailcustomer.setCustomerCode(null);
                                            retailcustomer.setEntityId(EntityRequestContext.getCurrentEntityId());
                                            retailcustomer.setPostedFlag('Y');
                                            retailcustomer.setPostedTime(new Date());
                                            retailcustomer = retailcustomerService.addRetailcustomer(retailcustomer);
                                            response.setMessage("MEMBER: " + retailcustomer.getFirstName() + " " + retailcustomer.getMiddleName() + " " + retailcustomer.getLastName() + " CREATED SUCCESSFULLY " + retailcustomer.getPostedTime());
                                            response.setStatusCode(HttpStatus.CREATED.value());
                                            response.setEntity(retailcustomer);
                                            return new ResponseEntity<>(response, HttpStatus.OK);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
             log.info("Catched Error {} line: "+e.getStackTrace()[0].getLineNumber()+", "+e.getStackTrace()[0].getClassName()+" " + e);
            return null;
        }
    }

    @GetMapping("/check/by/code/{code}")
    public ResponseEntity<?> checkCustomer(@PathVariable("code") String code) {
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
                    Optional<RetailcustomerRepo.Customers> rCustomer = retailcustomerRepo.checkByCustomerCode(code);
                    if (rCustomer.isPresent()) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("true");
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(rCustomer.get().getCustomerid() + "," + rCustomer.get().getCustomercode());
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("false");
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }
                }
            }
        } catch (Exception exc) {
            EntityResponse response = new EntityResponse(exc.getLocalizedMessage(), null, HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.ok().body(response);
        }
    }

    @GetMapping("/check/by/phone/{phone}")
    public ResponseEntity<?> checkByPhone(@PathVariable("phone") String phone) {
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
                    Optional<RetailcustomerRepo.Customers> rCustomer = retailcustomerRepo.checkByPhone(phone);
                    if (rCustomer.isPresent()) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("true");
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(rCustomer.get().getCustomerid() + "," + rCustomer.get().getCustomercode());
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("false");
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }
                }
            }
        } catch (Exception exc) {
            EntityResponse response = new EntityResponse(exc.getLocalizedMessage(), null, HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.ok().body(response);
        }
    }

    @GetMapping("/find/by/id/{id}")
    public ResponseEntity<?> getMemberById(@PathVariable("id") Long id) {
        System.out.println("inside ID");
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                EntityResponse response = new EntityResponse();
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    Optional<Retailcustomer> memberSearch = retailcustomerRepo.findById(id);
                    if (memberSearch.isPresent()) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(memberSearch);
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
             log.info("Catched Error {} line: "+e.getStackTrace()[0].getLineNumber()+", "+e.getStackTrace()[0].getClassName()+" " + e);
            return null;
        }
    }

    @GetMapping("/find/by/code/{code}")
    public ResponseEntity<?> retrieveCustomer(@PathVariable("code") String code) {
        try {
            System.out.println("INSIDE TO Fetch");
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
                    System.out.println("Headed to fetch data");
                    Optional<Retailcustomer> rCustomer = retailcustomerRepo.findByCustomerCode(code);
                    if (rCustomer.isPresent()) {
                        System.out.println("the data: "+rCustomer);
                        Retailcustomer retailcustomer = rCustomer.get();
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(retailcustomer);
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
        } catch (Exception exc) {
            EntityResponse response = new EntityResponse(exc.getLocalizedMessage(), null, HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.ok().body(response);
        }
    }

    @GetMapping("find/image/by/{id}")
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
                    String entityid = EntityRequestContext.getCurrentEntityId();
                    Optional<CustomerImage> image = customerImageRepository.findById(id);
                    if (image.isPresent()) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(image);
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
        } catch (Exception exc) {
            EntityResponse response = new EntityResponse(exc.getLocalizedMessage(), null, HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.ok().body(response);
        }
    }

    @GetMapping("find/images/by/{id}")
    public ResponseEntity<?> getImagesByCustomerId(@PathVariable("id") Long id) {
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
                    Optional<Retailcustomer> member = retailcustomerRepo.findById(id);
                    if (member.isPresent()) {
                        if (member.get().getImages().size() > 0) {
                            EntityResponse response = new EntityResponse();
                            response.setMessage(HttpStatus.OK.getReasonPhrase());
                            response.setStatusCode(HttpStatus.OK.value());
                            response.setEntity(member.get().getImages());
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        } else {
                            EntityResponse response = new EntityResponse();
                            response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                            response.setStatusCode(HttpStatus.NOT_FOUND.value());
                            response.setEntity("");
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
        } catch (Exception exc) {
            EntityResponse response = new EntityResponse(exc.getLocalizedMessage(), null, HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.ok().body(response);
        }
    }

    @GetMapping("/find/images/by/code/{code}")
    public ResponseEntity<?> retrieveCustomerImages(@PathVariable("code") String code) {
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
                    String entityid = EntityRequestContext.getCurrentEntityId();
                    List<CustomerImage> customerImages = customerImageRepository.findImages(code);
                    if (customerImages.size() > 0) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(customerImages);
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
        } catch (Exception exc) {
            EntityResponse response = new EntityResponse(exc.getLocalizedMessage(), null, HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.ok().body(response);
        }
    }

    //    Universal
    @GetMapping("/get/all/members")
    public EntityResponse<?> getAllMembers() {
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
                    List<Retailcustomer> retailcustomers = retailcustomerRepo.findAllByEntityIdAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), 'N');
                    if (retailcustomers.size() > 0) {
                        response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(retailcustomers);
                    } else {
                        response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity("");
                    }
                }
            }
            return response;
        } catch (Exception e) {
             log.info("Catched Error {} line: "+e.getStackTrace()[0].getLineNumber()+", "+e.getStackTrace()[0].getClassName()+" " + e);
            return null;
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllCustomers(
            @RequestParam(required = false) String customerType,
            @RequestParam(required = false) String uniqueId,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) String customerCode,
            @RequestParam(required = false) String identity,
            @RequestParam(required = false) String verified,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String name
    ) {
        try {
            Character verifiedFlag = '%';
//            if (verified==null || verified.isEmpty()){
//                verifiedFlag = '%';
//            }else {
//                verifiedFlag = verified.charAt(0);
//            }

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
                    if (identity == null || identity.isEmpty()) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("You Must pass identity!");
                        response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        if (identity == "Individual" || identity.equalsIgnoreCase("Individual")) {
                            return getIndividualCustomers(uniqueId, fromDate, toDate, customerCode, verifiedFlag, identity, phoneNumber, name);
                        } else if (identity == "Group" || identity.equalsIgnoreCase("Group")) {
                            return getGroupCustomers(uniqueId, fromDate, toDate, customerCode, verifiedFlag, identity, name);
                        } else {
                            EntityResponse response = new EntityResponse();
                            response.setMessage("Only Acceptable customer types are: Individual or Group!");
                            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                            response.setEntity("");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        }
                    }
                }
            }
        } catch (Exception e) {
             log.info("Catched Error {} line: "+e.getStackTrace()[0].getLineNumber()+", "+e.getStackTrace()[0].getClassName()+" " + e);
            return null;
        }
    }

    //    Filter Individual Groups
    public ResponseEntity<?> getIndividualCustomers(
            String uniqueId,
            String fromDate,
            String toDate,
            String customerCode,
            Character verifiedFlag,
            String identity,
            String phoneNumber,
            String name) {
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
                    List<Member> retailCustomer = null;
                    if (uniqueId == null || uniqueId.isEmpty()) {
                        if (customerCode == null || customerCode.isEmpty()) {
                            if (phoneNumber == null || phoneNumber.isEmpty()) {
                                if (name == null || name.isEmpty()) {
                                    if (fromDate == null || fromDate.isEmpty() || toDate == null || toDate.isEmpty()) {
                                        EntityResponse response = new EntityResponse();
                                        response.setMessage("You Must Provide at Least some parameter(s)!");
                                        response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                                        response.setEntity("");
                                        return new ResponseEntity<>(response, HttpStatus.OK);
                                    } else {
                                        retailCustomer = retailcustomerRepo.findByDateRange(fromDate, toDate, verifiedFlag);
                                    }
                                } else {
                                    System.out.println("Search by name");
                                    retailCustomer = retailcustomerRepo.findByName(name, verifiedFlag);
                                }
                            } else {
                                retailCustomer = retailcustomerRepo.findByPhoneNumber(phoneNumber, verifiedFlag);
                            }
                        } else {
                            retailCustomer = retailcustomerRepo.findCustomerByCustomerCode(customerCode, verifiedFlag);
                        }
                    } else {
                        retailCustomer = retailcustomerRepo.findByUniqueID(uniqueId, verifiedFlag);
                    }
                    List<MemberClass> finalList = new ArrayList<>();
                    for (int i = 0; i < retailCustomer.size(); i++) {
                        Member member = retailCustomer.get(i);
                        MemberClass memberClass = new MemberClass();
                        memberClass.setId(member.getId());
                        memberClass.setBranchCode(member.getBranchCode());
                        memberClass.setCustomerCode(member.getCustomerCode());
                        memberClass.setCustomerType(member.getCustomerType());
                        memberClass.setCustomerName(member.getCustomerName());
                        memberClass.setCustomerUniqueId(member.getCustomerUniqueId());
                        memberClass.setPostedOn(member.getPostedOn());
                        memberClass.setVerifiedFlag(member.getVerifiedFlag());
                        memberClass.setIdentity(identity);
                        finalList.add(memberClass);
                    }
                    if (finalList.size() > 0) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(finalList);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("No Records for the Membership Found: !!");
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity(finalList);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }
                }
            }
        } catch (Exception e) {
             log.info("Catched Error {} line: "+e.getStackTrace()[0].getLineNumber()+", "+e.getStackTrace()[0].getClassName()+" " + e);
            return null;
        }
    }

    //    Filter Groups
    public ResponseEntity<?> getGroupCustomers(
            String uniqueId,
            String fromDate,
            String toDate,
            String customerCode,
            Character verifiedFlag,
            String identity,
            String name) {
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
                    List<Member> retailCustomer = null;
                    if (uniqueId == null || uniqueId.isEmpty()) {
                        if (customerCode == null || customerCode.isEmpty()) {
                            if (name == null || name.isEmpty()) {
                                if (fromDate == null || fromDate.isEmpty() || toDate == null || toDate.isEmpty()) {
                                    EntityResponse response = new EntityResponse();
                                    response.setMessage("You Must pass atleast some parameter(s)!");
                                    response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                                    response.setEntity("");
                                    return new ResponseEntity<>(response, HttpStatus.OK);
                                } else {
                                    retailCustomer = groupMemberRepository.findByDateRange(fromDate, toDate, verifiedFlag);
                                }
                            } else {
                                retailCustomer = groupMemberRepository.findCustomerByName(customerCode);
                            }
                        } else {
                            retailCustomer = groupMemberRepository.findCustomerByCustomerCode(customerCode, verifiedFlag);
                        }
                    } else {
                        retailCustomer = groupMemberRepository.findByUniqueID(uniqueId, verifiedFlag);
                    }
                    List<MemberClass> finalList = new ArrayList<>();
                    for (int i = 0; i < retailCustomer.size(); i++) {
                        Member member = retailCustomer.get(i);
                        MemberClass memberClass = new MemberClass();
                        memberClass.setId(member.getId());
                        memberClass.setBranchCode(member.getBranchCode());
                        memberClass.setCustomerCode(member.getCustomerCode());
                        memberClass.setCustomerType(member.getCustomerType());
                        memberClass.setCustomerName(member.getCustomerName());
                        memberClass.setCustomerUniqueId(member.getCustomerUniqueId());
                        memberClass.setPostedOn(member.getPostedOn());
                        memberClass.setVerifiedFlag(member.getVerifiedFlag());
                        memberClass.setIdentity(identity);
                        finalList.add(memberClass);
                    }
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(finalList);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
             log.info("Catched Error {} line: "+e.getStackTrace()[0].getLineNumber()+", "+e.getStackTrace()[0].getClassName()+" " + e);
            return null;
        }
    }

    //    Filter Individual Groups
//    public ResponseEntity<?> getIndividsdsualCustomers(
//            String uniqueId,
//            String fromDate,
//            String toDate,
//            String customerCode,
//            String customerType
//    ) {
//        try {
//            if (UserRequestContext.getCurrentUser().isEmpty()) {
//                EntityResponse response = new EntityResponse();
//                response.setMessage("User Name not present in the Request Header");
//                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                response.setEntity("");
//                return new ResponseEntity<>(response, HttpStatus.OK);
//            } else {
//                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
//                    EntityResponse response = new EntityResponse();
//                    response.setMessage("Entity not present in the Request Header");
//                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                    response.setEntity("");
//                    return new ResponseEntity<>(response, HttpStatus.OK);
//                } else {
//                    List<Member> retailCustomer = null;
//                    if (uniqueId == null || uniqueId.isEmpty()) {
//                        if (customerCode == null || customerCode.isEmpty()) {
//                            if (fromDate == null || fromDate.isEmpty() || toDate == null || toDate.isEmpty()) {
//                                EntityResponse response = new EntityResponse();
//                                response.setMessage("You must pass date range!");
//                                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
//                                response.setEntity("");
//                                return new ResponseEntity<>(response, HttpStatus.OK);
//                            } else {
//                                retailCustomer = retailcustomerRepo.findByDateRange(fromDate, toDate, customerType);
//                            }
//                        } else {
//                            retailCustomer = retailcustomerRepo.findCustomerByCustomerCode(customerCode);
//                        }
//                    } else {
//                        retailCustomer = retailcustomerRepo.findByUniqueID(uniqueId);
//                    }
//                    EntityResponse response = new EntityResponse();
//                    response.setMessage(HttpStatus.OK.getReasonPhrase());
//                    response.setStatusCode(HttpStatus.OK.value());
//                    response.setEntity(retailCustomer);
//                    return new ResponseEntity<>(response, HttpStatus.OK);
//                }
//            }
//        } catch (Exception e) {
//             log.info("Catched Error {} line: "+e.getStackTrace()[0].getLineNumber()+", "+e.getStackTrace()[0].getClassName()+" " + e);
//            return null;
//        }
//    }

    //    Filter Groups
//    public ResponseEntity<?> getGroupCustomers(
//            String uniqueId,
//            String fromDate,
//            String toDate,
//            String customerCode,
//            String customerType) {
//        try {
//            if (UserRequestContext.getCurrentUser().isEmpty()) {
//                EntityResponse response = new EntityResponse();
//                response.setMessage("User Name not present in the Request Header");
//                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                response.setEntity("");
//                return new ResponseEntity<>(response, HttpStatus.OK);
//            } else {
//                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
//                    EntityResponse response = new EntityResponse();
//                    response.setMessage("Entity not present in the Request Header");
//                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                    response.setEntity("");
//                    return new ResponseEntity<>(response, HttpStatus.OK);
//                } else {
//                    List<Member> retailCustomer = null;
//                    if (uniqueId == null || uniqueId.isEmpty()) {
//                        if (customerCode == null || customerCode.isEmpty()) {
//                            if (fromDate == null || fromDate.isEmpty() || toDate == null || toDate.isEmpty()) {
//                                EntityResponse response = new EntityResponse();
//                                response.setMessage("You Must pass atleast some parameter(s)!");
//                                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
//                                response.setEntity("");
//                                return new ResponseEntity<>(response, HttpStatus.OK);
//                            } else {
//                                retailCustomer = groupMemberRepository.findByDateRange(fromDate, toDate, customerType);
//                            }
//                        } else {
//                            retailCustomer = groupMemberRepository.findCustomerByCustomerCode(customerCode);
//                        }
//                    } else {
//                        retailCustomer = groupMemberRepository.findByUniqueID(uniqueId);
//                    }
//                    EntityResponse response = new EntityResponse();
//                    response.setMessage(HttpStatus.OK.getReasonPhrase());
//                    response.setStatusCode(HttpStatus.OK.value());
//                    response.setEntity(retailCustomer);
//                    return new ResponseEntity<>(response, HttpStatus.OK);
//                }
//            }
//        } catch (Exception e) {
//             log.info("Catched Error {} line: "+e.getStackTrace()[0].getLineNumber()+", "+e.getStackTrace()[0].getClassName()+" " + e);
//            return null;
//        }
//    }

    @GetMapping("/find/{id}")
    public ResponseEntity<?> getRetailcustomerById(@PathVariable("id") Long id) {
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
                    Retailcustomer retailcustomer = retailcustomerService.findById(id);
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(retailcustomer);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
             log.info("Catched Error {} line: "+e.getStackTrace()[0].getLineNumber()+", "+e.getStackTrace()[0].getClassName()+" " + e);
            return null;
        }
    }

    @PutMapping("/modify")
    public ResponseEntity<?> updateRetailcustomer(@RequestBody Retailcustomer retailcustomer) {
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
                    retailcustomer.setModifiedBy(UserRequestContext.getCurrentUser());
                    retailcustomer.setEntityId(EntityRequestContext.getCurrentEntityId());
                    Optional<Retailcustomer> retailcustomer1 = retailcustomerRepo.findById(retailcustomer.getId());
                    if (retailcustomer1.isPresent()) {
                        retailcustomer.setPostedTime(retailcustomer1.get().getPostedTime());
                        retailcustomer.setPostedFlag('Y');
                        retailcustomer.setPostedBy(retailcustomer1.get().getPostedBy());
                        retailcustomer.setModifiedFlag('Y');
                        retailcustomer.setVerifiedFlag('N');
                        retailcustomer.setModifiedTime(new Date());
                        retailcustomer.setModifiedBy(retailcustomer.getModifiedBy());
                        retailcustomerService.updateRetailcustomer(retailcustomer);
                        // TODO: 5/22/2023  send sms
//                        String message = "Dear "+retailcustomer.getFirstName()+" we wish to inform you that your customer information file was modified successfully on "+ new Date()+". Thank you for banking with us";
//                        String message = "Dear " + retailcustomer.getFirstName() + ", your account " + retailcustomer.getCustomerCode() + " was modified on " + new Date() + ". Thank you for banking with us";
                        String message = "Dear " + retailcustomer.getFirstName() + ", your account " + retailcustomer.getCustomerCode() + " was modified on " + datesCalculator.dateFormat(new Date())+ " at "+datesCalculator.timeFormat(new Date())+". Thank you for banking with us.";
                        String phone = retailcustomer.getPhoneNumber();
                        SmsDto smsDto = new SmsDto();
                        smsDto.setMsisdn(phone);
                        smsDto.setText(message);
                        serviceCaller.sendSmsEmt(smsDto);

                        EntityResponse response = new EntityResponse();
                        response.setMessage("MEMBER WITH CODE" + " " + retailcustomer.getCustomerCode() + " AND NAME " + retailcustomer.getFirstName() + " " + retailcustomer.getMiddleName() + " " + retailcustomer.getLastName() + " MODIFIED SUCCESSFULLY AT " + retailcustomer.getModifiedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(retailcustomer);
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
             log.info("Catched Error {} line: "+e.getStackTrace()[0].getLineNumber()+", "+e.getStackTrace()[0].getClassName()+" " + e);
            return null;
        }
    }

    @PutMapping("/verify/{id}")
    public ResponseEntity<?> verify(@PathVariable("id") Long id) {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                EntityResponse response = new EntityResponse();
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    Optional<Retailcustomer> retailcustomer1 = retailcustomerRepo.findById(id);
                    if (retailcustomer1.isPresent()) {

                        Retailcustomer retailcustomer = retailcustomer1.get();
                        //                    Check Maker Checker
                        if (retailcustomer.getCustomerCode() == null || retailcustomer.getCustomerCode().isEmpty()) {
                            retailcustomer.setCustomerCode(customercodeService.getCodeGenRetailCustomerCode(retailcustomer.getMemberType()));
                        } else {
                        }
                        if (retailcustomer.getPostedBy().equalsIgnoreCase(UserRequestContext.getCurrentUser())) {
                            EntityResponse response = new EntityResponse();
                            response.setMessage("You can not verify a record that you created!");
                            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        } else if (retailcustomer.getVerifiedFlag().equals('Y')) {
                            EntityResponse response = new EntityResponse();
                            response.setMessage("Member Already Verified");
                            response.setStatusCode(HttpStatus.ALREADY_REPORTED.value());
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        } else {
                            retailcustomer.setVerifiedFlag('Y');
                            retailcustomer.setVerifiedTime(new Date());
                            retailcustomer.setVerifiedBy(UserRequestContext.getCurrentUser());
                            Retailcustomer verifyMember = retailcustomerRepo.save(retailcustomer);
//                            send sms
                            String message = "Dear " + retailcustomer.getFirstName() + ", your account has successfully been created ref member no " + retailcustomer.getCustomerCode() + ". Thank you for choosing Goodway.";
                            String phone = retailcustomer.getPhoneNumber();
                            SmsDto smsDto = new SmsDto();
                            smsDto.setMsisdn(phone);
                            smsDto.setText(message);
                            serviceCaller.sendSmsEmt(smsDto);

//                            smsService.SMSNotification(message, phone);
//                            send email
                            if (retailcustomer.getEmailAddress() == null || retailcustomer.getEmailAddress().isEmpty()) {
                            } else {
//                                send mail
                                String subject = "Sacco Membership Registration";
                                mailService.sendSimpleMail(retailcustomer.getEmailAddress(), subject, message);
                            }
                            EntityResponse response = new EntityResponse();
                            response.setMessage("MEMBER WITH CODE" + " " + verifyMember.getCustomerCode() + " AND NAME " + verifyMember.getFirstName() + " " + verifyMember.getMiddleName() + " " + verifyMember.getLastName() + " VERIFIED SUCCESSFULLY AT " + verifyMember.getVerifiedTime());
                            response.setStatusCode(HttpStatus.OK.value());
                            response.setEntity(verifyMember);
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        }
                    } else {
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }
                }
            }
        } catch (Exception e) {
             log.info("Catched Error {} line: "+e.getStackTrace()[0].getLineNumber()+", "+e.getStackTrace()[0].getClassName()+" " + e);
            return null;
        }
    }

    @PutMapping("/reject/{id}")
    public ResponseEntity<?> rejectRetailcustomer(@PathVariable String id) {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                EntityResponse response = new EntityResponse();
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    Optional<Retailcustomer> retailcustomer1 = retailcustomerRepo.findById(Long.parseLong(id));
                    if (retailcustomer1.isPresent()) {
                        Retailcustomer retailcustomer = retailcustomer1.get();
                        retailcustomer.setStatus("REJECTED");
                        retailcustomer.setRejectedFlag('Y');
                        retailcustomer.setRejectedTime(new Date());
                        retailcustomer.setRejectedBy(UserRequestContext.getCurrentUser());
                        Retailcustomer rejectMember = retailcustomerRepo.save(retailcustomer);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("MEMBER WITH CODE" + " " + rejectMember.getCustomerCode() + " AND NAME " + rejectMember.getFirstName() + " " + rejectMember.getMiddleName() + " " + rejectMember.getLastName() + " REJECTED SUCCESSFULLY AT " + rejectMember.getRejectedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(rejectMember);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }
                }
            }
        } catch (Exception e) {
             log.info("Catched Error {} line: "+e.getStackTrace()[0].getLineNumber()+", "+e.getStackTrace()[0].getClassName()+" " + e);
            return null;
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteRetailcustomer(@PathVariable String id) {
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
                    Optional<Retailcustomer> retailcustomer1 = retailcustomerRepo.findById(Long.parseLong(id));
                    if (retailcustomer1.isPresent()) {
                        Retailcustomer retailcustomer = retailcustomer1.get();
                        retailcustomer.setDeletedFlag('Y');
                        retailcustomer.setDeletedTime(new Date());
                        retailcustomer.setDeletedBy(UserRequestContext.getCurrentUser());
                        retailcustomerRepo.save(retailcustomer);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("MEMBER WITH CODE" + " " + retailcustomer.getCustomerCode() + " AND NAME " + retailcustomer.getFirstName() + " " + retailcustomer.getMiddleName() + " " + retailcustomer.getLastName() + " DELETED SUCCESSFULLY AT " + retailcustomer.getDeletedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(retailcustomer);
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
             log.info("Catched Error {} line: "+e.getStackTrace()[0].getLineNumber()+", "+e.getStackTrace()[0].getClassName()+" " + e);
            return null;
        }
    }

    @GetMapping("/get/customer/email/{customerCode}")
    public ResponseEntity<?> getCustomersEmail(@PathVariable("customerCode") String customerCode) {
        try {
            EntityResponse response = retailcustomerService.getCustomerEmail(customerCode);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
             log.info("Catched Error {} line: "+e.getStackTrace()[0].getLineNumber()+", "+e.getStackTrace()[0].getClassName()+" " + e);
            return null;
        }
    }

    @GetMapping("/get/total/retail/customers")
    public EntityResponse<?> getTotalRetailCustomers() {
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
                    return ResponseEntity.ok().body(retailcustomerService.getTotalRetailCustomers()).getBody();
                }
            }
            return response;

        } catch (Exception e) {
             log.info("Catched Error {} line: "+e.getStackTrace()[0].getLineNumber()+", "+e.getStackTrace()[0].getClassName()+" " + e);
            return null;
        }
    }

//    @GetMapping("/check/by/branchcode/{branchCode}")
//    public ResponseEntity<?>  checkByBranchCode(@PathVariable String branchCode){
//        boolean available = false;
//        if (retailcustomerRepo.findByBranchCode(branchCode).size()>0){
//            available= true;
//        }else{
//            if (groupMemberRepository.findByBranchCode(branchCode).size()>0){
//                available = true;
//            }else{
//                available = false;
//            }
//        }
//        EntityResponse response = new EntityResponse();
//        response.setMessage(String.valueOf(available));
//        response.setStatusCode(HttpStatus.OK.value());
//        response.setEntity("");
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }

    @GetMapping("all/unVerified/members/list")
    public ResponseEntity<?> findAllUnVerifiedMembers(
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate
    ) throws IOException {
        try {
            EntityResponse response = new EntityResponse();
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
            } else if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            } else {
                if (fromDate == null || toDate == null) {
                    response.setMessage("You must provide a date range !");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                } else {
                    List<Member> memberList = retailcustomerRepo.findAllByEntityIdAndVerifiedFlagAndDateRange(EntityRequestContext.getCurrentEntityId(), fromDate, toDate);
                    if (memberList.size() > 0) {
                        response.setMessage("Welcome " + UserRequestContext.getCurrentUser() + ", There are " + memberList.size() + " Member(s) For Approval.");
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(memberList);
                    } else {
                        response.setMessage("Welcome " + UserRequestContext.getCurrentUser() + ", There are " + memberList.size() + " Member(s) For Approval.");
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity("Total Members " + memberList.size());
                    }
                }
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            EntityResponse response = new EntityResponse();
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("all/unVerified/members/joint")
    public ResponseEntity<?> findAllUnVerifiedMembersGroupAndRetail(
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate
    ) throws IOException {
        try {
            EntityResponse response = new EntityResponse();
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
            } else if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            } else {
                if (fromDate == null || toDate == null) {
                    response.setMessage("You must provide a date range !");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                } else {
                    List<Member> memberList = groupMemberRepository.findAllByEntityIdAndVerifiedFlagAndDateRange(EntityRequestContext.getCurrentEntityId(), fromDate, toDate);
                    System.out.println("Count before individual: "+memberList.size());
                    memberList.addAll(retailcustomerRepo.findAllByEntityIdAndVerifiedFlagAndDateRange(EntityRequestContext.getCurrentEntityId(), fromDate, toDate));
                    System.out.println("Count after individual: "+memberList.size());
                    if (memberList.size() > 0) {
                        response.setMessage("Welcome " + UserRequestContext.getCurrentUser() + ", There are " + memberList.size() + " Member(s) For Approval.");
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(memberList);
                    } else {
                        response.setMessage("Welcome " + UserRequestContext.getCurrentUser() + ", There are " + memberList.size() + " Member(s) For Approval.");
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity("Total Members " + memberList.size());
                    }
                }
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            EntityResponse response = new EntityResponse();
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

    }
}
