package com.emtechhouse.CustomerService.RetailMember;


import com.emtechhouse.DTO.ContactDetails;
import com.emtechhouse.DTO.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RetailcustomerRepo extends JpaRepository<Retailcustomer, Long> {
    Optional<ContactDetails> findByCustomerCodeAndDeletedFlag(String code,Character flag);

    Optional<Retailcustomer> findByCustomerCode(String code);


    List<Retailcustomer> findAllByEntityIdAndDeletedFlag(String entityId, Character deletedFlag);
    Optional<Retailcustomer> findByEntityIdAndUniqueIdAndDeletedFlag(String entityId, String uniqueId, Character flag);

    @Query(value = "select * from retailcustomer where entity_id = :entityId and kra_pin = :kraPin and deleted_flag = :flag limit 1", nativeQuery = true)
    Optional<Retailcustomer> findByEntityIdAndKraPinAndDeletedFlag(String entityId, String kraPin, Character flag);
    Optional<Retailcustomer> findByEntityIdAndPhoneNumberAndDeletedFlag(String entityId, String phoneNumber, Character flag);
    @Query(nativeQuery = true, value = "SELECT id as Id, customer_code as CustomerCode FROM `retailcustomer` WHERE customer_code IN (SELECT MAX(customer_code) FROM retailcustomer WHERE customer_code like CONCAT(:memberType, '%')) ")
    Optional<CustomerCode> findLastEntry(String memberType);

    @Query(nativeQuery = true, value = "SELECT `running_size`,`code_structure`,`customer_type` FROM customer_type WHERE customer_type=:memberType")
    Optional<CustomerType> getCustomerType(String memberType);
    interface CustomerCode{
        public Long getId();
        public String getCustomerCode();
    }
    interface CustomerType{
        public Integer getRunning_size();
        public String getCustomer_type();
        public String getCode_structure();
    }
    @Query(nativeQuery = true, value = "SELECT retailcustomer.member_type as Membertype, retailcustomer.customer_code as Customercode, retailcustomer.unique_id as Customerid, retailcustomer.first_name as Customerfirstname, retailcustomer.middle_name as Customermiddlename, retailcustomer.last_name as Customerlastname, retailcustomer.branch_code as Customerbranch, retailcustomer.posted_time as Customerpostedon from retailcustomer where retailcustomer.customer_code LIKE :customer_code AND retailcustomer.deleted_flag = 'N' ORDER BY `id` DESC")
    Optional<Customers> checkByCustomerCode(String customer_code);

    @Query(nativeQuery = true, value = "SELECT retailcustomer.member_type as Membertype, retailcustomer.customer_code as Customercode, retailcustomer.unique_id as Customerid, retailcustomer.first_name as Customerfirstname, retailcustomer.middle_name as Customermiddlename, retailcustomer.last_name as Customerlastname, retailcustomer.branch_code as Customerbranch, retailcustomer.posted_time as Customerpostedon from retailcustomer where retailcustomer.phone_number LIKE :phoneNumber AND retailcustomer.deleted_flag = 'N' ORDER BY `id` DESC")
    Optional<Customers> checkByPhone(String phoneNumber);

    @Query(nativeQuery = true, value = "SELECT db_table from customer_type where customer_type = :customerType")
    Optional<String> customerTypeTable(@Param("customerType") String customerType);


    List<Retailcustomer>  findByMemberType(String memberType);

//    List<Customers> findByBranchCode(String branchCode);

    interface Customers {
        String getCustomercode();
        String getMembertype();
        String getCustomerfirstname();
        String getCustomermiddlename();
        String getCustomerlastname();
        String getCustomerid();
        String getCustomerbranch();
        String getCustomerpostedon();
    }

    public interface IStatistics{
        String getOnboardingYear();
    }

    public interface OnboardedCustomersYearWiseStatistics{
        String getOnboardingYear();
        String getTotalCustomers();
    }

    public interface OnboardedCustomersMonthWiseStatistics{
        String getMonthName();
        String getTotalCustomers();
    }

    public interface OnboardedCustomersDayWiseStatistics{
        String getOnboardingDay();
        String getTotalCustomers();
    }

    @Query(nativeQuery = true, value = "select year(posted_time) as onboardingYear from retailcustomer group by year(posted_time)")
    List<IStatistics> findOnboardingYears();

    @Query(nativeQuery = true, value = "select year(posted_time) as onboardingYear, count(id) as totalCustomers from retailcustomer group by year(posted_time)")
    List<OnboardedCustomersYearWiseStatistics> findOnboardedCustomersYearWise();

    @Query(nativeQuery = true, value = "select monthname(posted_time) as monthName, count(id) as totalCustomers from retailcustomer where year(posted_time)= :onboardingYear group by monthName")
    List<OnboardedCustomersMonthWiseStatistics> findOnboardedCustomersMonthWise(@Param("onboardingYear") String onboardingYear);

    @Query(nativeQuery = true, value = "select day(posted_time) as onboardingDay, count(id) as totalCustomers from retailcustomer where year(posted_time)= :year and monthname(posted_time)= :monthName group by day(posted_time)")
    List<OnboardedCustomersDayWiseStatistics> findOnboardedCustomersDayWise(@Param("year") String year, @Param("monthName") String monthName);

    //    FILTER UNIT
    @Query(nativeQuery = true, value =
            "SELECT id, branch_code as BranchCode, retailcustomer.member_type as CustomerType, retailcustomer.customer_code as CustomerCode, retailcustomer.unique_id as CustomerUniqueId, \n" +
            "CONCAT(retailcustomer.first_name,' ', retailcustomer.middle_name,' ',retailcustomer.last_name) as customerName,\n" +
            "retailcustomer.posted_time as PostedOn,\n" +
            "retailcustomer.verified_flag as VerifiedFlag, 'retail' AS identity \n" +
            "from retailcustomer where retailcustomer.unique_id LIKE :uniqueId AND verified_flag LIKE :verifiedFlag  AND  retailcustomer.deleted_flag = 'N' ORDER BY `id` DESC")
    List<Member> findByUniqueID(String uniqueId, Character verifiedFlag);
    @Query(nativeQuery = true, value =
            "SELECT id, branch_code as BranchCode, retailcustomer.member_type as CustomerType, retailcustomer.customer_code as CustomerCode, retailcustomer.phone_number as PhoneNumber, retailcustomer.unique_id as CustomerUniqueId,  \n" +
                    "CONCAT(retailcustomer.first_name,' ', retailcustomer.middle_name,' ',retailcustomer.last_name) as customerName, \n" +
                    "retailcustomer.posted_time as PostedOn, \n" +
                    "retailcustomer.verified_flag as VerifiedFlag, 'retail' AS identity \n" +
                    "from retailcustomer where retailcustomer.phone_number LIKE :phoneNumber AND verified_flag LIKE :verifiedFlag  AND  retailcustomer.deleted_flag = 'N' ORDER BY `id` DESC")
    List<Member> findByPhoneNumber(String phoneNumber, Character verifiedFlag);
    @Query(nativeQuery = true, value =
            "SELECT id, branch_code as BranchCode, retailcustomer.member_type as CustomerType, retailcustomer.customer_code as CustomerCode, retailcustomer.phone_number as PhoneNumber, retailcustomer.unique_id as CustomerUniqueId,  \n" +
                    "CONCAT(retailcustomer.first_name,' ', retailcustomer.middle_name,' ',retailcustomer.last_name) as customerName, \n" +
                    "retailcustomer.posted_time as PostedOn, \n" +
                    "retailcustomer.verified_flag as VerifiedFlag, 'retail' AS identity \n" +
                    "from retailcustomer where CONCAT(retailcustomer.first_name,' ', retailcustomer.middle_name,' ',retailcustomer.last_name) LIKE CONCAT('%', :name, '%') AND verified_flag LIKE :verifiedFlag  AND  retailcustomer.deleted_flag = 'N' ORDER BY `id` DESC")
    List<Member> findByName(String name, Character verifiedFlag);
    @Query(nativeQuery = true, value = "SELECT id,  branch_code as BranchCode,retailcustomer.member_type as CustomerType, retailcustomer.customer_code as CustomerCode, retailcustomer.unique_id as CustomerUniqueId, \n" +
            "CONCAT(retailcustomer.first_name,' ', retailcustomer.middle_name,' ',retailcustomer.last_name) as customerName,\n" +
            "retailcustomer.posted_time as PostedOn,\n" +
            "retailcustomer.verified_flag as VerifiedFlag, 'retail' AS identity \n" +
            "from retailcustomer  where retailcustomer.posted_time BETWEEN :fromDate AND :toDate AND verified_flag LIKE :verifiedFlag  AND  retailcustomer.deleted_flag = 'N'  ORDER BY `id` DESC")
    List<Member> findByDateRange(String fromDate, String toDate, Character verifiedFlag);

    @Query(nativeQuery = true, value = "SELECT id, branch_code as BranchCode,retailcustomer.member_type as CustomerType, retailcustomer.customer_code as CustomerCode, retailcustomer.unique_id as CustomerUniqueId, \n" +
            "CONCAT(retailcustomer.first_name,' ', retailcustomer.middle_name,' ',retailcustomer.last_name) as customerName,\n" +
            "retailcustomer.posted_time as PostedOn,\n" +
            "retailcustomer.verified_flag as VerifiedFlag, 'retail' AS identity \n" +
            "from retailcustomer  where retailcustomer.posted_time BETWEEN :fromDate AND :toDate AND verified_flag LIKE :verifiedFlag  AND retailcustomer.deleted_flag = 'N' and member_type = :customerCode ORDER BY `id` DESC")
    List<Member> findByDateRange(String fromDate, String toDate, String customerCode, Character verifiedFlag);
    @Query(nativeQuery = true, value = "SELECT id, branch_code as BranchCode, retailcustomer.member_type as CustomerType, retailcustomer.customer_code as CustomerCode, retailcustomer.unique_id as CustomerUniqueId, \n" +
            "CONCAT(retailcustomer.first_name,' ', retailcustomer.middle_name,' ',retailcustomer.last_name) as customerName,\n" +
            "retailcustomer.posted_time as PostedOn,\n" +
            "retailcustomer.verified_flag as VerifiedFlag, 'retail' AS identity " +
            "from retailcustomer  where retailcustomer.customer_code LIKE :customerCode AND verified_flag LIKE :verifiedFlag  AND retailcustomer.deleted_flag = 'N' ORDER BY `id` DESC")
    List<Member> findCustomerByCustomerCode(String customerCode, Character verifiedFlag);


    @Query(nativeQuery = true, value = "SELECT `email_address` AS emailAddress FROM `retailcustomer` WHERE `customer_code`=:customerCode")
    Optional<CustomerEmail> getCustomerEmail(String customerCode);


    public  interface CustomerEmail{
        public String getEmailAddress();
    }
    @Query(value = "SELECT COUNT(*) FROM retailcustomer WHERE deleted_flag = 'N' AND verified_flag = 'N'", nativeQuery = true)
    Integer countAllRetailCustomers();
    @Query(nativeQuery = true, value = "SELECT id AS ID,  branch_code AS BranchCode,retailcustomer.member_type AS CustomerType, retailcustomer.customer_code AS CustomerCode, retailcustomer.unique_id AS CustomerUniqueId, \n" +
            "CONCAT(retailcustomer.first_name,' ', retailcustomer.middle_name,' ',retailcustomer.last_name) AS customerName,\n" +
            "retailcustomer.posted_time AS PostedOn,\n" +
            "retailcustomer.verified_flag AS VerifiedFlag, 'retail' AS identity  " +
            "FROM retailcustomer WHERE retailcustomer.posted_time BETWEEN :fromDate AND :toDate AND retailcustomer.entity_id=:entityId AND retailcustomer.deleted_flag = 'N' AND  (rejected_flag  = 'N' OR  rejected_flag  is null ) AND retailcustomer.verified_flag = 'N' ORDER BY `id` DESC")
    List<Member> findAllByEntityIdAndVerifiedFlagAndDateRange(String entityId, String fromDate, String toDate);
}