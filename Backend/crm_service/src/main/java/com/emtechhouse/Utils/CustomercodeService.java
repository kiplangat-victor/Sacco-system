package com.emtechhouse.Utils;

import com.emtechhouse.CustomerService.GroupMember.GroupMember;
import com.emtechhouse.CustomerService.GroupMember.GroupMemberRepository;
import com.emtechhouse.CustomerService.RetailMember.Retailcustomer;
import com.emtechhouse.CustomerService.RetailMember.RetailcustomerRepo;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomercodeService {
    private final GroupMemberRepository groupMemberRepository;
    private final RetailcustomerRepo retailcustomerRepo;

    public CustomercodeService(GroupMemberRepository groupMemberRepository, RetailcustomerRepo retailcustomerRepo) {
        this.groupMemberRepository = groupMemberRepository;
        this.retailcustomerRepo = retailcustomerRepo;
    }
//todo: MWAMBA CODE GENERATION
    public String getCodeGenRetailCustomerCode(String memberType, int startIncrement) {
        System.out.println(memberType);
        int memberRunningCode = 1;
        String customerCode;
        Optional<RetailcustomerRepo.CustomerCode> customer1 = retailcustomerRepo.findLastEntry(memberType);
        Optional<RetailcustomerRepo.CustomerType> customerTypeOptional = retailcustomerRepo.getCustomerType(memberType);
        if (!customerTypeOptional.isPresent()) {
            return "";
        }
        RetailcustomerRepo.CustomerType customerType = customerTypeOptional.get();
        if (customer1.isPresent()) {
            System.out.println(customer1.get().getCustomerCode());
            String existingCustomerCode = customer1.get().getCustomerCode();
            String existCustRunningNo = lastNChars(existingCustomerCode, customerType.getRunning_size());
            memberRunningCode = Integer.parseInt(existCustRunningNo) + startIncrement;
        } else {
            System.out.println("Did not find previous customerCode");
        }
        System.out.println("Increment "+startIncrement);
        String strMemberRunningCode = String.valueOf(memberRunningCode);
        strMemberRunningCode = preceedZeros(strMemberRunningCode, customerType.getRunning_size());
        customerCode = customerType.getCode_structure();
        customerCode = customerCode.replaceAll("RUNNO", strMemberRunningCode).replaceAll("TYPE", memberType);
        System.out.println(customerCode);

        Optional<Retailcustomer> retailcustomer = retailcustomerRepo.findByCustomerCode(customerCode);
        if (retailcustomer.isPresent()) {
            return getCodeGenRetailCustomerCode(memberType, startIncrement+1);
        }
        return customerCode;
    }

    public String getCodeGenRetailCustomerCode(String memberType) {
        return getCodeGenRetailCustomerCode(memberType, 1);
    }

    public String lastNChars(String str, int n) {
        return str.length() > n ? str.substring(str.length() - n) : str;
    }
    public String preceedZeros(String string, int expectedSize) {
        while (string.length() < expectedSize) {
            string = "0"+string;
        }
        return string;
    }

    public String getCodeGenGroupCustomerCode(String groupType) {
        int memberRunningCode = 1;
        String customerCode;
        Optional<GroupMemberRepository.CustomerCode> groupMember = groupMemberRepository.findLastEntry(groupType);
        Optional<GroupMemberRepository.GroupType> checkGroup = groupMemberRepository.getCustomerType(groupType);
        if (!checkGroup.isPresent()) {
            return "";
        }
        GroupMemberRepository.GroupType checkGroupType = checkGroup.get();
        if (groupMember.isPresent()) {
            String existingCustomerCode = groupMember.get().getCustomerCode();
            String existCustRunningNo = lastNChars(existingCustomerCode, checkGroupType.getRunning_size());
            memberRunningCode = Integer.parseInt(existCustRunningNo) + 1;
        }
        String strMemberRunningCode = String.valueOf(memberRunningCode);
        strMemberRunningCode = preceedZeros(strMemberRunningCode, checkGroupType.getRunning_size());
        customerCode = checkGroupType.getCode_structure();
        System.out.println(customerCode);
        customerCode = customerCode.replaceAll("RUNNO", strMemberRunningCode).replaceAll("TYPE", groupType);
        System.out.println(customerCode);
        return customerCode;
    }


}