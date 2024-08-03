package com.emtechhouse.accounts.TransactionService.SalaryUploads;

import com.emtechhouse.accounts.Responses.BatchSalaryCheques;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalaryuploadRepo extends JpaRepository<Salaryupload, Long> {
    @Query(value = "SELECT * FROM salaryupload  WHERE salary_upload_code = :salaryUploadCode and entity_id =:entityId and deleted_flag=:deletedFlag", nativeQuery = true)
    Optional<Salaryupload> findByEntityIdAndSalaryUploadCodeAndDeletedFlag(@Param("entityId") String entityId, @Param("salaryUploadCode") String salaryUploadCode, @Param("deletedFlag") Character deletedFlag);

    @Query(value = "SELECT * FROM salaryupload  WHERE  entity_id =:entityId and posted_time  BETWEEN :fromDate AND :toDate and deleted_flag=:deletedFlag", nativeQuery = true)
    List<Salaryupload> fetchByDate(String entityId, String fromDate, String toDate, Character deletedFlag);

    @Query(value = "SELECT * FROM salaryupload  WHERE date(entered_time)  BETWEEN :fromDate AND :toDate and entity_id =:entityId  and  entered_flag='Y'   and deleted_flag='N'  and posted_flag='N'", nativeQuery = true)
    List<Salaryupload> getentered(String fromDate, String toDate, String entityId);

    //modified
    @Query(value = "SELECT * FROM salaryupload  WHERE date(entered_time)  BETWEEN :fromDate AND :toDate and entity_id =:entityId  like 'M%' and modified_flag='Y'   and deleted_flag='N'  and posted_flag='N'", nativeQuery = true)
    List<Salaryupload> getmodified(String fromDate, String toDate, String entityId);

    //posted
    @Query(value = "SELECT * FROM salaryupload  WHERE date(entered_time)  BETWEEN :fromDate AND :toDate and entity_id =:entityId   and deleted_flag='N'  and posted_flag='Y'", nativeQuery = true)
    List<Salaryupload> getposted(String fromDate, String toDate, String entityId);

    //verirified
    @Query(value = "SELECT * FROM salaryupload  WHERE date(entered_time)  BETWEEN :fromDate AND :toDate and entity_id =:entityId  and verified_flag='Y'   and deleted_flag='N'  and posted_flag='N'", nativeQuery = true)
    List<Salaryupload> getVerified(String fromDate, String toDate, String entityId);

    //deleleted
    @Query(value = "SELECT * FROM salaryupload  WHERE date(entered_time)  BETWEEN :fromDate AND :toDate and entity_id =:entityId  and deleted_flag='Y'", nativeQuery = true)
    List<Salaryupload> getDeleted(String fromDate, String toDate, String entityId);

    @Query(value = "SELECT * FROM salaryupload  WHERE (rejected_flag  = 'N' OR  rejected_flag  is null ) and   (verified_flag_2  = 'N' OR  verified_flag  = 'N')  and deleted_flag='N' limit 50", nativeQuery = true)
    List<Salaryupload> getApprovalList();

    @Query(value = "SELECT COUNT(*) FROM salaryupload WHERE deleted_flag = 'N' AND rejected_flag = 'N' AND verified_flag = 'Y' AND verified_flag_2 = 'N'", nativeQuery = true)
    Integer countAllSalaryUploads();

    @Query(value = "SELECT 'Salary Processing' AS type, 'salary' AS typeCode, amount, id, salary_upload_code AS uniqueCode, debit_account AS account, entered_time As entryTime, verified_flag, verified_flag_2 FROM salaryupload WHERE   (rejected_flag  = 'N' OR  rejected_flag  is null ) and  (verified_flag = 'N' OR verified_flag_2 = 'N') and deleted_flag = 'N'  and (verified_flag = 'N' OR (verified_flag = 'Y' and verified_by <> :username )) ", nativeQuery = true)
    List<BatchSalaryCheques> getRoughApprovalList(@Param("username") String username);
}