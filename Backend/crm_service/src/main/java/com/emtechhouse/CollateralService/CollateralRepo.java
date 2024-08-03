package com.emtechhouse.CollateralService;

import com.emtechhouse.CustomerService.RetailMember.RetailcustomerRepo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface CollateralRepo extends JpaRepository<Collateral, Long> {
    List<Collateralmin> findByEntityIdAndDeletedFlag(String entityId, Character flag);
    Optional<Collateral> findByEntityIdAndCollateralCodeAndDeletedFlag(String entityId, String collateralSerial, Character flag);

    interface Collateralmin{
        Long getSn();
        String getEntityId();
        String getCollateralCode();
        String getCollateralValue();
        String getCollateralType();
        String getCustomerCode();
        String getCustomerName();
        String getDescription();
        String getPostedBy();
        Date getPostedTime();
        String getVerifiedBy();
        Character getVerifiedFlag();
        Date getVerifiedTime();
    }

    Optional<Collateral> findByCollateralCodeAndDeletedFlag(String collateralCode, Character flag);

    @Query(nativeQuery = true, value = "SELECT c.`sn` AS Sn , c.`collateral_code` AS CollateralCode,c.`collateral_value` AS CollateralValue,c.`collateral_type` AS CollateralType,c.`description` AS Description,c.verified_flag AS VerifiedFlag, r.phone_number AS PhoneNumber, r.email_address AS EmailAddress FROM `collateral` c JOIN (select customer_code, primary_phone AS phone_number, group_mail as email_address from group_member union select customer_code, phone_number, email_address from retailcustomer) r ON c.customer_code=r.customer_code WHERE c.collateral_code=:collateralCode AND c.deleted_flag='N'")
    Optional<CollateralDto> getCollateralDetails(String collateralCode);

}
