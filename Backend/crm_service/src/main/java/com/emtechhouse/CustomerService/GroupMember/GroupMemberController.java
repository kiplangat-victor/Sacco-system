package com.emtechhouse.CustomerService.GroupMember;

import com.emtechhouse.CustomerService.CustomerImage.CustomerImage;
import com.emtechhouse.CustomerService.CustomerImage.CustomerImageRepository;
import com.emtechhouse.DTO.Member;
import com.emtechhouse.NotificationComponent.SmsService.SMSService;
import com.emtechhouse.Utils.CustomercodeService;
import com.emtechhouse.Utils.EntityResponse;
import com.emtechhouse.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.Utils.HttpInterceptor.UserRequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "api/v1/groups")
@Slf4j
public class GroupMemberController {
    private final GroupMemberService groupMemberService;
    private final GroupMemberRepository groupMemberRepository;
    private final CustomercodeService customercodeService;
    private final SMSService smsService;
    private final CustomerImageRepository customerImageRepository;

    public GroupMemberController(GroupMemberService groupMemberService, GroupMemberRepository groupMemberRepository, CustomercodeService customercodeService, SMSService smsService, CustomerImageRepository customerImageRepository) {
        this.groupMemberService = groupMemberService;
        this.groupMemberRepository = groupMemberRepository;
        this.customercodeService = customercodeService;
        this.smsService = smsService;
        this.customerImageRepository = customerImageRepository;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addGroupMember(@RequestBody GroupMember groupMember) {
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
                    Optional<GroupMember> checkDocNumber = groupMemberRepository.findGroupMemberByVerificationDocNumber(groupMember.getVerificationDocNumber());
                    if (groupMember.getVerificationDocNumber() == null || groupMember.getVerificationDocNumber() == "" || groupMember.getVerificationDocNumber() == " ") {
                        response.setMessage("Group Membership DOCUMENT Must Be Provided: !! If the Group has NOT been Registered By the Public Board, Check the requirements By The Sacco CEO.");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else if (groupMember.getVerificationDocNumber() == null || groupMember.getVerificationDocNumber() == "" || groupMember.getVerificationDocNumber() == " ") {
                        response.setMessage("Group Membership DOCUMENT NUMBER Must Be Provided: !! If the Group has NOT been Registered By the Public Board, Check the requirements By The Sacco CEO.");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else if (checkDocNumber.isPresent()) {
                        response.setMessage("Group Membership DOCUMENT NUMBER " + checkDocNumber.get().getVerificationDocNumber() + " ALREADY Registered for Another Group: " + checkDocNumber.get().getGroupName());
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else if (groupMember.getPrimaryPhone() == null || groupMember.getPrimaryPhone().trim().isEmpty()) {
                        response.setMessage("Group Membership PHONE/MOBILE NUMBER Must Be Provided: !! If the Group has NOT been Registered By the Public Board, Check the requirements By The Sacco CEO.");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else if (groupMember.getPrimaryPhone().length() != 12) {
                        response.setMessage("Group Membership PHONE/MOBILE NUMBER " + groupMember.getPrimaryPhone() + " format is INVALID. Kindly provide the Required Format RULE 25471234567: !!");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        groupMember.setCustomerCode(null);
                        groupMember.setUniqueId(groupMember.getVerificationDocNumber());
                        groupMember.setUniqueType(groupMember.getVerificationDocument());
                        groupMember.setPostedBy(UserRequestContext.getCurrentUser());
                        groupMember.setEntityId(EntityRequestContext.getCurrentEntityId());
                        groupMember.setPostedFlag('Y');
                        groupMember.setPostedTime(new Date());
                        GroupMember addGroupMember = groupMemberService.addGroupMember(groupMember);
                        response.setMessage("GROUP MEMBERSHIP WITH NAME: " + addGroupMember.getGroupName() + " CREATED SUCCESSFULLY" + " At " + addGroupMember.getPostedTime());
                        response.setStatusCode(HttpStatus.CREATED.value());
                        response.setEntity(addGroupMember);
                    }

                }
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/find/by/id/{id}")
    public ResponseEntity<?> getMemberById(@PathVariable("id") Long id) {
        try {
            System.out.println("group id find");
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
                    Optional<GroupMember> memberSearch = groupMemberRepository.findById(id);
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
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/find/by/code/{code}")
    public ResponseEntity<?> retrieveCustomer(@PathVariable("code") String code) {
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
                    Optional<GroupMember> rCustomer = groupMemberRepository.findByCustomerCode(code);
                    System.out.println("-----------------------------");
                    System.out.println(rCustomer.get().getImages().size());
                    if (rCustomer.isPresent()) {
                        GroupMember groupMember = rCustomer.get();
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(groupMember);
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
                    String entityid = EntityRequestContext.getCurrentEntityId();
                    Optional<GroupMember> member = groupMemberRepository.findById(id);
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

    @GetMapping("/get/all/group/members")
    public EntityResponse<?> getAllGroupMembers() {
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
                    List<GroupMember> groupMemberList = groupMemberRepository.findAllByEntityIdAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), 'N');
                    if (groupMemberList.size() > 0) {
                        response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(groupMemberList);
                    } else {
                        response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity("");
                    }
                }
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    //    Universal
    @GetMapping("/all")
    public ResponseEntity<?> getAllCustomers(
            @RequestParam(required = false) String customerType,
            @RequestParam(required = false) String uniqueId,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) String customerCode
    ) {
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
                    if (customerType == null || customerType.isEmpty()) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("You Must pass Customer Type!");
                        response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        if (customerType == "Individual" || customerType.equalsIgnoreCase("Individual")) {
                            return getIndividualCustomers(uniqueId, fromDate, toDate, customerCode, '%');
                        } else if (customerType == "Group" || customerType.equalsIgnoreCase("Group")) {
                            return getGroupCustomers(uniqueId, fromDate, toDate, customerCode, '%');
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
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    //    Filter Individual Groups
    public ResponseEntity<?> getIndividualCustomers(
            String uniqueId,
            String fromDate,
            String toDate,
            String customerCode,
            Character verifiedFlag
    ) {
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
                    System.out.println("****************check uniq id************passed");
                    if (uniqueId == null || uniqueId.isEmpty()) {
                        if (customerCode == null || customerCode.isEmpty()) {
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
                            retailCustomer = groupMemberRepository.findCustomerByCustomerCode(customerCode, verifiedFlag);
                        }
                    } else {
                        retailCustomer = groupMemberRepository.findByUniqueID(uniqueId, verifiedFlag);
                    }
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(retailCustomer);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    //    Filter Groups
    public ResponseEntity<?> getGroupCustomers(
            String uniqueId,
            String fromDate,
            String toDate,
            String customerCode,
            Character verifiedFlag
    ) {
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
                            if (fromDate == null || fromDate.isEmpty() || toDate == null || toDate.isEmpty()) {
                                EntityResponse response = new EntityResponse();
                                response.setMessage("You Must pass at least some parameter(s)!");
                                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                                response.setEntity("");
                                return new ResponseEntity<>(response, HttpStatus.OK);
                            } else {
                                retailCustomer = groupMemberRepository.findByDateRange(fromDate, toDate, verifiedFlag);
                            }
                        } else {
                            retailCustomer = groupMemberRepository.findCustomerByCustomerCode(customerCode, verifiedFlag);
                        }
                    } else {
                        retailCustomer = groupMemberRepository.findByUniqueID(uniqueId, verifiedFlag);
                    }
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(retailCustomer);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("/modify")
    public ResponseEntity<?> updateGroupMember(@RequestBody GroupMember groupMember) {
        try {
            return ResponseEntity.ok().body(groupMemberService.updateGroupMember(groupMember));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
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
                    Optional<GroupMember> checkId = groupMemberRepository.findById(id);
                    if (checkId.isPresent()) {
                        GroupMember groupMember = checkId.get();
                        // Check Maker Checker
                        if (groupMember.getCustomerCode() == null || groupMember.getCustomerCode().isEmpty()) {
                            System.out.println("Customer type is emtpy");
                            groupMember.setCustomerCode(customercodeService.getCodeGenGroupCustomerCode(groupMember.getGroupType()));
                        }

                        if (groupMember.getPostedBy().equalsIgnoreCase(UserRequestContext.getCurrentUser())) {
                            EntityResponse response = new EntityResponse();
                            response.setMessage("You can not verify a record that you created!");
                            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        } else {
                            groupMember.setVerifiedFlag('Y');
                            groupMember.setVerifiedTime(new Date());
                            groupMember.setVerifiedBy(UserRequestContext.getCurrentUser());
                            GroupMember verifyGroup = groupMemberRepository.save(groupMember);

                            //send sms
                            String message = "Thank you " + groupMember.getGroupName() + " for Joining our organization.You have been onboarded successfully with Group Member Ref. Code " + groupMember.getCustomerCode() + " .Always use this code when making inquiry. Thank you, & have a nice time.";
                            String phone = verifyGroup.getPrimaryPhone();
                            smsService.sendSMS(message, phone);

                            EntityResponse response = new EntityResponse();
                            response.setMessage("Group Membership with Name: " + " " + verifyGroup.getGroupName() + " And Code " + verifyGroup.getCustomerCode() + " VERIFIED SUCCESSFULLY " + " At " + verifyGroup.getVerifiedTime());
                            response.setStatusCode(HttpStatus.OK.value());
                            response.setEntity(verifyGroup);
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
    public ResponseEntity<?> deleteGroupMember(@PathVariable("id") Long id) {
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
                    Optional<GroupMember> groupMember1 = groupMemberRepository.findById(id);
                    if (groupMember1.isPresent()) {
                        GroupMember groupMember = groupMember1.get();
                        groupMember.setDeletedFlag('Y');
                        groupMember.setDeletedTime(new Date());
                        groupMember.setDeletedBy(UserRequestContext.getCurrentUser());
                        groupMemberRepository.save(groupMember);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("GROUP MEMBERSHIP WITH NUMBER: " + " " + groupMember.getCustomerCode() + "AND NAME: " + " " + groupMember.getGroupName() + " " + "DELETED SUCCESSFULLY" + " " + "AT" + " " + " " + groupMember.getDeletedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(groupMember);
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


    @GetMapping("all/unVerified/group/members/list")
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
                    List<Member> groupMembers = groupMemberRepository.findAllByEntityIdAndVerifiedFlagAndDateRange(EntityRequestContext.getCurrentEntityId(), fromDate, toDate);
                    if (groupMembers.size() > 0) {
                        response.setMessage("Welcome " + UserRequestContext.getCurrentUser() + ", There are " + groupMembers.size() + " Group Member(s) For Approval.");
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(groupMembers);
                    } else {
                        response.setMessage("Welcome " + UserRequestContext.getCurrentUser() + ", There are " + groupMembers.size() + " Group Member(s) For Approval.");
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity("Total Group Members " + groupMembers.size());
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

    @GetMapping("/get/total/group/members")
    public EntityResponse<?> getTotalGroupMembers() {
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
                    return ResponseEntity.ok().body(groupMemberService.getTotalGroupMembers()).getBody();
                }
            }
            return response;

        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("/reject/{id}")
    public EntityResponse<?> rejectGroupMember(@PathVariable("id") Long id) {
        try {
            EntityResponse response = new EntityResponse();
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                } else {
                    Optional<GroupMember> groupMember = groupMemberRepository.findById(id);
                    if (groupMember.isPresent()) {
                        GroupMember groupMember1 = groupMember.get();
                        groupMember1.setStatus("REJECTED");
                        groupMember1.setRejectedFlag('Y');
                        groupMember1.setRejectedTime(new Date());
                        groupMember1.setRejectedBy(UserRequestContext.getCurrentUser());
                        GroupMember reject = groupMemberRepository.save(groupMember1);
                        response.setMessage("Group Membership with NO." + reject.getCustomerCode() + " and name " + reject.getGroupName() + " REJECTED SUCCESSFULLY AT " + reject.getDeletedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(reject);
                    } else {
                        response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    }
                }
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
}
