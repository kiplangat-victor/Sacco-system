package com.emtechhouse.accounts.Models.Accounts.Loans.InterestReversal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterestReversalRepo extends JpaRepository<InterestReversal, Long> {
    @Query(value = "SELECT * FROM interest_reversal  WHERE interest_reversal_code = :interest_reversalCode and entity_id =:entityId and deleted_flag=:deletedFlag", nativeQuery = true)
    Optional<InterestReversal> findByEntityIdAndInterestReversalUploadCodeAndDeletedFlag(@Param("entityId") String entityId, @Param("interest_reversalCode") String interest_reversalCode, @Param("deletedFlag") Character deletedFlag);

    @Query(value = "SELECT * FROM interest_reversal  WHERE  entity_id =:entityId and posted_time  BETWEEN :fromDate AND :toDate and deleted_flag=:deletedFlag", nativeQuery = true)
    List<InterestReversal> fetchByDate(String entityId, String fromDate, String toDate, Character deletedFlag);

    @Query(value = "SELECT * FROM interest_reversal  WHERE date(entered_time)  BETWEEN :fromDate AND :toDate and entity_id =:entityId  and  entered_flag='Y'   and deleted_flag='N'  and posted_flag='N'", nativeQuery = true)
    List<InterestReversal> getentered(String fromDate, String toDate, String entityId);

    //modified
    @Query(value = "SELECT * FROM interest_reversal  WHERE date(entered_time)  BETWEEN :fromDate AND :toDate and entity_id =:entityId  like 'M%' and modified_flag='Y'   and deleted_flag='N'  and posted_flag='N'", nativeQuery = true)
    List<InterestReversal> getmodified(String fromDate, String toDate, String entityId);

    //posted
    @Query(value = "SELECT * FROM interest_reversal  WHERE date(entered_time)  BETWEEN :fromDate AND :toDate and entity_id =:entityId   and deleted_flag='N'  and posted_flag='Y'", nativeQuery = true)
    List<InterestReversal> getposted(String fromDate, String toDate, String entityId);

    //verirified
    @Query(value = "SELECT * FROM interest_reversal  WHERE date(entered_time)  BETWEEN :fromDate AND :toDate and entity_id =:entityId  and verified_flag='Y'   and deleted_flag='N'  and posted_flag='N'", nativeQuery = true)
    List<InterestReversal> getVerified(String fromDate, String toDate, String entityId);

    //deleleted
    @Query(value = "SELECT * FROM interest_reversal  WHERE date(entered_time)  BETWEEN :fromDate AND :toDate and entity_id =:entityId  and deleted_flag='Y'", nativeQuery = true)
    List<InterestReversal> getDeleted(String fromDate, String toDate, String entityId);

    @Query(value = "SELECT * FROM interest_reversal  WHERE (rejected_flag  = 'N' OR  rejected_flag  is null ) and   (verified_flag_2  = 'N' OR  verified_flag  = 'N')  and deleted_flag='N' limit 50", nativeQuery = true)
    List<InterestReversal> getApprovalList();

    @Query(value = "SELECT COUNT(*) FROM interest_reversal WHERE deleted_flag = 'N' AND rejected_flag = 'N' AND verified_flag = 'Y' AND verified_flag_2 = 'N'", nativeQuery = true)
    Integer countAllInterestReversalUploads();

    @Query(value = "SELECT * FROM interest_reversal  WHERE date(entered_time)  BETWEEN :fromDate AND :toDate and entity_id =:entityId  and deleted_flag='N' ", nativeQuery = true)
    List<InterestReversal> loadAll(String fromDate, String toDate, String entityId);
}