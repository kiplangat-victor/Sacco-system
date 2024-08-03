package com.emtechhouse.CustomerService.GroupMember;

import com.emtechhouse.CustomerService.RetailMember.RetailcustomerRepo;
import com.emtechhouse.DTO.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    Optional<GroupMember> findGroupMemberByVerificationDocNumber(String verificationDocNumber);
    Optional<GroupCustomerContact> findByCustomerCodeAndDeletedFlag(String code, Character flag);

    interface GroupCustomerContact {
        String getPrimaryPhone();

        String getGroupMail();
    }

    Optional<GroupMember> findByCustomerCode(String code);

    List<GroupMember> findAllByEntityIdAndDeletedFlag(String entityId, Character flag);

    @Query(value = "SELECT id As Id, customer_code AS CustomerCode FROM group_member WHERE verified_flag = 'Y' AND customer_code IN (SELECT MAX(customer_code) FROM group_member WHERE customer_code like concat(:groupType, '%') AND customer_code NOT LIKE 'G%')", nativeQuery = true)
    Optional<CustomerCode> findLastEntry(String groupType);

    @Query(nativeQuery = true, value = "SELECT `running_size`,`code_structure`,`customer_type` FROM customer_type WHERE customer_type=:groupType")
    Optional<GroupType> getCustomerType(String groupType);


    interface GroupType{
        public Integer getRunning_size();
        public String getGroup_type();
        public String getCode_structure();
    }

    interface CustomerCode {
        public Long getId();

        public String getCustomerCode();
    }

    //    List<Customers> findByBranchCode(String branchCode);
    interface Customers {
        String getCustomercode();

        String getMembertype();

        String getFullname();

        String getCustomerid();

        String getCustomerbranch();

        String getCustomerpostedon();
    }

    //    FILTER UNIT
    @Query(nativeQuery = true, value =
            "SELECT id, branch_code as BranchCode, group_member.group_type as CustomerType, group_member.customer_code as CustomerCode, group_member.unique_id as CustomerUniqueId, \n" +
                    "group_member.group_name as customerName,\n" +
                    "group_member.posted_time as PostedOn,\n" +
                    "group_member.verified_flag as VerifiedFlag,  'group' AS identity \n" +
                    "from group_member where group_member.unique_id LIKE :uniqueId AND verified_flag LIKE :verifiedFlag  AND  group_member.deleted_flag = 'N' ORDER BY `id` DESC")
    List<Member> findByUniqueID(String uniqueId, Character verifiedFlag);

    @Query(nativeQuery = true, value = "SELECT id,  branch_code as BranchCode, group_member.group_type as CustomerType, group_member.customer_code as CustomerCode, group_member.unique_id as CustomerUniqueId, \n" +
            "group_member.group_name as customerName,\n" +
            "group_member.posted_time as PostedOn,\n" +
            "group_member.verified_flag as VerifiedFlag,  'group' AS identity  \n" +
            "from group_member  where group_member.posted_time BETWEEN :fromDate AND :toDate AND verified_flag LIKE :verifiedFlag  AND  group_member.deleted_flag = 'N' ORDER BY `id` DESC")
    List<Member> findByDateRange(String fromDate, String toDate, Character verifiedFlag);

    @Query(nativeQuery = true, value = "SELECT id,  branch_code as BranchCode, group_member.group_type as CustomerType, group_member.customer_code as CustomerCode, group_member.unique_id as CustomerUniqueId, \n" +
            "group_member.group_name as customerName,\n" +
            "group_member.posted_time as PostedOn,\n" +
            "group_member.verified_flag as VerifiedFlag,  'group' AS identity  \n" +
            "from group_member  where group_member.posted_time BETWEEN :fromDate AND :toDate AND verified_flag LIKE :verifiedFlag  AND  group_member.deleted_flag = 'N' AND group_type = :customerType ORDER BY `id` DESC")
    List<Member> findByDateRange(String fromDate, String toDate, String customerType, Character verifiedFlag);

    @Query(nativeQuery = true, value = "SELECT id,  branch_code as BranchCode, group_member.group_type as CustomerType, group_member.customer_code as CustomerCode, group_member.unique_id as CustomerUniqueId, \n" +
            "group_member.group_name as customerName,\n" +
            "group_member.posted_time as PostedOn,\n" +
            "group_member.verified_flag as VerifiedFlag,  'group' AS identity  \n" +
            "from group_member  where group_member.customer_code LIKE :customerCode AND verified_flag LIKE :verifiedFlag  AND  group_member.deleted_flag = 'N' ORDER BY `id` DESC")
    List<Member> findCustomerByCustomerCode(String customerCode, Character verifiedFlag);

    @Query(nativeQuery = true, value = "SELECT id,  branch_code as BranchCode, group_member.group_type as CustomerType, group_member.customer_code as CustomerCode, group_member.unique_id as CustomerUniqueId, \n" +
            "group_member.group_name as customerName,\n" +
            "group_member.posted_time as PostedOn,\n" +
            "group_member.verified_flag as VerifiedFlag,  'group' AS identity  \n" +
            "from group_member  where group_member.group_name  LIKE CONCAT('%', :name, '%')  AND  group_member.deleted_flag = 'N' ORDER BY `id` DESC")
    List<Member> findCustomerByName(String name);

    @Query(nativeQuery = true, value = "SELECT id,  branch_code as BranchCode, group_member.group_type as CustomerType, group_member.customer_code as CustomerCode, group_member.unique_id as CustomerUniqueId, \n" +
            "group_member.group_name as customerName,\n" +
            "group_member.posted_time as PostedOn,\n" +
            "group_member.verified_flag as VerifiedFlag,  'group' AS identity \n" +
            "from group_member  where group_member.posted_time BETWEEN :fromDate AND :toDate AND group_member.entity_id=:entityId AND group_member.deleted_flag = 'N' AND  (rejected_flag  = 'N' OR  rejected_flag  is null ) AND group_member.verified_flag = 'N' ORDER BY `id` DESC")
    List<Member> findAllByEntityIdAndVerifiedFlagAndDateRange(String entityId, String fromDate, String toDate);

    @Query(value = "SELECT COUNT(*) FROM group_member WHERE deleted_flag = 'N' AND verified_flag = 'N'", nativeQuery = true)
    Integer countAllGroupMembers();
}