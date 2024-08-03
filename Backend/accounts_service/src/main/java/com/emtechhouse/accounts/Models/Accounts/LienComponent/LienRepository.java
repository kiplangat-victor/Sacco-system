package com.emtechhouse.accounts.Models.Accounts.LienComponent;

import com.emtechhouse.accounts.Models.Accounts.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface LienRepository extends JpaRepository<Lien, Long> {

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE lien SET lien_adjusted_amount=lien_adjusted_amount+:amount WHERE id=:id", nativeQuery = true)
    void updateLienAdjustedAmount(Double amount, Long id);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE lien SET `status`=:status WHERE `id`=:id", nativeQuery = true)
    void updateLienStatus(String status, Long id);

    @Query(value = "SELECT `id` AS id, `lien_code` AS lienCode FROM `lien` ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Optional<LienCode> findAccountLastEntryOfficeAccount();
    interface LienCode{
        public Long getId();
        public String getlienCode();
    }

    List<Lien> findBySourceAccount(Account account);
    List<Lien> findByStatusAndDeletedFlag(String status, Character deletedFlag);
    List<Lien> findByDestinationAccount(Account account);
    Optional<Lien> findByLienCode(String lienCode);

    @Query(value = "update lien set verified_flag ='Y', verified_time = CURRENT_TIMESTAMP(0), verified_by= :user WHERE lien_code= :lienCode", nativeQuery = true)
    Void verifyLiensByLienCode(String lienCode, String user);

    @Query(value = "update lien set modified_time = CURRENT_TIMESTAMP(0), priority = :lienPriority, lien_type = :lienType, effective_from = :effectiveFrom, expiry_date = :expiryDate, lien_adjusted_amount = :lienAdjustedAmount, lien_description = :lienDescription, lien_amount = :lienAmount, modified_by= :user, lien_type = :lienType, verified_flag = 'N', status = 'ACTIVE'  WHERE lien_code= :lienCode", nativeQuery = true)
    Void modifyLien(String lienCode, String lienType, String lienPriority, String lienDescription, Double  lienAmount, Date effectiveFrom, Date expiryDate, Double lienAdjustedAmount, String user);

    @Query(value = "select id as ID, lien_code as lienCode, deleted_by as deletedBy, deleted_flag as deletedFlag, deleted_time as lienDeletedTime, verified_time as lienVerifiedTime, posted_by as postedByWhom, verified_flag as verifiedFlag, verified_by as verifiedByWho,deleted_flag as lienDeletedFlag from lien WHERE lien_code= :lienCode", nativeQuery = true)
    Optional<checkLiensByLienCode> checkLiensByLienCode(String lienCode);

    interface checkLiensByLienCode{
        public Long getId();
        public String getlienCode();
        public String getVerifiedFlag();
        public String getVerifiedByWho();
        public String getLienDeletedFlag();
        public  String getPostedByWhom();
        public  Date getLienVerifiedTime();
        public  String getDeletedBy();
        public  String getLienDeletedTime();

    }

    @Query(value = "update lien set deleted_by = :user, deleted_time = CURRENT_TIMESTAMP(0)  WHERE lien_code= :lienCode", nativeQuery = true)
    Void closeLienLienByLienCode(String lienCode, String user);
    @Query(value = "update lien set verified_by = :user, verified_time = CURRENT_TIMESTAMP(0),  deleted_flag ='Y', deleted_time = CURRENT_TIMESTAMP(0), deleted_by= :user, status= 'CLOSED' WHERE lien_code= :lienCode", nativeQuery = true)
    Void verifyLienClosureByLienCode(String lienCode, String user);
    @Query(value = "SELECT * FROM `lien` WHERE `effective_from`<=:date AND `status`=:status", nativeQuery = true)
    List<Lien> findEffectiveFromDateAfterAndStatus(Date date, String status);

    @Query(value = "SELECT * FROM `lien` WHERE `expiry_date`<=:date AND `status`=:status", nativeQuery = true)
    List<Lien> findExpiryDateAfterAndStatus(Date date, String status);

    List<Lien> findByStatusOrderByPriorityAsc(String status);
}
