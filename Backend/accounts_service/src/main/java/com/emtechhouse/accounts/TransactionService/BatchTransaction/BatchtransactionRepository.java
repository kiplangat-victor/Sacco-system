package com.emtechhouse.accounts.TransactionService.BatchTransaction;

import com.emtechhouse.accounts.Responses.BatchSalaryCheques;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BatchtransactionRepository extends JpaRepository<Batchtransaction, Long> {
    Optional<Batchtransaction> findByBatchUploadCode(String batchUploadCode);

    @Query(value = "SELECT * FROM batchtransaction  WHERE batch_upload_code = :batchUploadCode and entity_id =:entityId and deleted_flag=:deletedFlag", nativeQuery = true)
    Optional<Batchtransaction> findByEntityIdAndBatchUploadCodeAndDeletedFlag(@Param("entityId") String entityId, @Param("batchUploadCode") String batchUploadCode, @Param("deletedFlag") Character deletedFlag);

    @Query(value = "SELECT * FROM batchtransaction  WHERE  entity_id =:entityId and entered_time  BETWEEN :fromDate AND :toDate and deleted_flag=:deletedFlag", nativeQuery = true)
    List<Batchtransaction> fetchByDate(String entityId, String fromDate, String toDate, Character deletedFlag);

    @Query(value = "SELECT COUNT(*) FROM batchtransaction WHERE deleted_flag = 'N' AND verified_flag = 'N'", nativeQuery = true)
    Integer countAllSalaries();

    @Query(value = "SELECT * FROM batchtransaction  WHERE entered_time  BETWEEN :fromDate AND :toDate and entity_id =:entityId  and  entered_flag='Y'", nativeQuery = true)
    List<Batchtransaction> getentered(String fromDate, String toDate, String entityId);

    //modified
    @Query(value = "SELECT * FROM batchtransaction  WHERE entered_time  BETWEEN :fromDate AND :toDate and entity_id =:entityId  like 'M%' and modified_flag='Y'", nativeQuery = true)
    List<Batchtransaction> getmodified(String fromDate, String toDate, String entityId);

    //posted
    @Query(value = "SELECT * FROM batchtransaction  WHERE entered_time  BETWEEN :fromDate AND :toDate and entity_id =:entityId  and posted_flag='Y'", nativeQuery = true)
    List<Batchtransaction> getposted(String fromDate, String toDate, String entityId);

    //verirified
    @Query(value = "SELECT * FROM batchtransaction  WHERE entered_time  BETWEEN :fromDate AND :toDate and entity_id =:entityId  and verified_flag='Y'", nativeQuery = true)
    List<Batchtransaction> getVerified(String fromDate, String toDate, String entityId);

    //deleleted
    @Query(value = "SELECT * FROM batchtransaction  WHERE entered_time  BETWEEN :fromDate AND :toDate and entity_id =:entityId  and deleted_flag='Y'", nativeQuery = true)
    List<Batchtransaction> getDeleted(String fromDate, String toDate, String entityId);

    @Query(value = "SELECT * FROM batchtransaction  WHERE  (rejected_flag  = 'N' OR  rejected_flag  is null ) and  (verified_flag_2  = 'N' OR  verified_flag  = 'N')   and deleted_flag='N' limit 50", nativeQuery = true)
    List<Batchtransaction> getApprovalList();
    @Query(value = "SELECT * FROM batchtransaction  WHERE entity_id =:entityId AND entered_time  BETWEEN :fromDate AND :toDate AND entered_flag='Y' AND deleted_flag = 'N' AND verified_flag = 'N' OR verified_flag_2 = 'N'", nativeQuery = true)
    List<Batchtransaction> findAllBatchTransactionsByEntityIdAndDateRange(String entityId, String fromDate, String toDate);

    @Query(value = "SELECT COUNT(*) FROM batchtransaction WHERE deleted_flag = 'N' AND rejected_flag = 'N' AND verified_flag = 'Y' AND verified_flag_2 = 'N'", nativeQuery = true)
    Integer countAllBatchTransactions();
    @Query(value = "SELECT 'Batch Transaction' AS type, 'batch' AS typeCode, amount, id, batch_upload_code AS uniqueCode, debit_account AS account, entered_time As entryTime, verified_flag, verified_flag_2 FROM batchtransaction WHERE (rejected_flag  = 'N' OR  rejected_flag  is null ) and  (verified_flag = 'N' OR verified_flag_2 = 'N')  and (verified_flag = 'N' OR (verified_flag = 'Y' and verified_by <> :username )) ", nativeQuery = true)
    List<BatchSalaryCheques> getRoughApprovalList(@Param("username") String username);
}