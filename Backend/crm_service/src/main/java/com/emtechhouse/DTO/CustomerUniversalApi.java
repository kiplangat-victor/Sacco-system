package com.emtechhouse.DTO;

import com.emtechhouse.CustomerService.GroupMember.GroupMemberRepository;
import com.emtechhouse.CustomerService.RetailMember.RetailcustomerRepo;
import com.emtechhouse.Utils.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
//@CrossOrigin
@RequestMapping("api/v1/customer/universal")
public class CustomerUniversalApi {
    private final GroupMemberRepository groupMemberRepository;
    private final RetailcustomerRepo retailcustomerRepo;

    public CustomerUniversalApi(GroupMemberRepository groupMemberRepository, RetailcustomerRepo retailcustomerRepo) {
        this.groupMemberRepository = groupMemberRepository;
        this.retailcustomerRepo = retailcustomerRepo;
    }

    @GetMapping("/contact/{code}")
    public EntityResponse checkCustomer(@PathVariable("code") String code) {
        try {
            ContactInfo contactInfo = new ContactInfo();
            EntityResponse response = new EntityResponse();
            Optional<ContactDetails> retailCustomer = retailcustomerRepo.findByCustomerCodeAndDeletedFlag(code,'N');
            if (retailCustomer.isPresent()){
                contactInfo.setEmailAddress(retailCustomer.get().getEmailAddress());
                contactInfo.setPhoneNumber(retailCustomer.get().getPhoneNumber());
                response.setMessage(HttpStatus.OK.getReasonPhrase());
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(contactInfo);
                return response;
            }else {
                Optional<GroupMemberRepository.GroupCustomerContact>  groupCustomer = groupMemberRepository.findByCustomerCodeAndDeletedFlag(code, 'N');
                if (groupCustomer.isPresent()){
                    contactInfo.setEmailAddress(groupCustomer.get().getGroupMail());
                    contactInfo.setPhoneNumber(groupCustomer.get().getPrimaryPhone());
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(contactInfo);
                    return response;
                }else {
                    response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setEntity(contactInfo);
                    return response;
                }
            }
        } catch (Exception exc) {
            EntityResponse response = new EntityResponse(exc.getLocalizedMessage(), null, HttpStatus.BAD_REQUEST.value());
            return response;
        }
    }

    @GetMapping("/customer/{code}")
    public EntityResponse getCustomerInfo(@PathVariable("code") String code) {
        try {
            ContactInfo contactInfo = new ContactInfo();
            EntityResponse response = new EntityResponse();
            List<Member> retailCustomer = retailcustomerRepo.findCustomerByCustomerCode(code, 'Y');
            if (retailCustomer.size()>0){
                Member member = retailCustomer.get(0);
                response.setMessage(HttpStatus.OK.getReasonPhrase());
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(member);
                return response;
            }else {

                List<Member> groupCustomer = groupMemberRepository.findCustomerByCustomerCode(code, 'Y');
                if (groupCustomer.size()>0){
                    Member member = groupCustomer.get(0);
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(member);
                    return response;
                }else {
                    response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    return response;
                }
            }
        } catch (Exception exc) {
            EntityResponse response = new EntityResponse(exc.getLocalizedMessage(), null, HttpStatus.BAD_REQUEST.value());
            return response;
        }
    }
}
