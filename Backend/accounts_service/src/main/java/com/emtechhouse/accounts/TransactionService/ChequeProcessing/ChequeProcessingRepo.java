package com.emtechhouse.accounts.TransactionService.ChequeProcessing;
import com.emtechhouse.accounts.Responses.BatchSalaryCheques;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;


public interface ChequeProcessingRepo extends JpaRepository<ChequeProcessing, Long> {


    @Query(value = "select id as Id, cheque_number as ChequeNumber, bank_name as BankName, bank_code as BankCode, bank_branch as BankBranch, debitoabaccount as DebitAccount, name_as_per_cheque as NameAsPerCheque, credit_cust_operative_account as CreditCustOperativeAccount, status as Status, cheque_rand_code as ChequeRandCode, verified_flag as VerifiedFlag_1, verified_flag_2 as VerifiedFlag_2 from cheque_processing", nativeQuery = true)
    List<CheckProcessingAll> getAllCheques();
    @Query(value = "select id as Id, cheque_number as ChequeNumber, bank_name as BankName, bank_code as BankCode, bank_branch as BankBranch, debitoabaccount as DebitAccount, name_as_per_cheque AS NameAsPerCheque, credit_cust_operative_account AS CreditCustOperativeAccount, status AS Status, cheque_rand_code AS ChequeRandCode, verified_flag as VerifiedFlag_1, verified_flag_2 as VerifiedFlag_2 from cheque_processing WHERE verified_flag_2 ='N' LIMIT 50", nativeQuery = true)
    List<CheckProcessingAll> getAllUnVerifiedCheques();

    @Query(value = "SELECT id AS Id, cheque_number AS ChequeNumber,bank_name AS BankName, bank_code AS BankCode, bank_branch AS BankBranch, debitoabaccount AS DebitAccount, name_as_per_cheque as NameAsPerCheque, credit_cust_operative_account as CreditCustOperativeAccount, status as Status, cheque_rand_code as ChequeRandCode,verified_flag as VerifiedFlag_1, verified_flag_2 as VerifiedFlag_2 from cheque_processing where entered_time between :fromDate and :toDate AND status LIKE :status ORDER BY id DESC", nativeQuery = true)
    List<CheckProcessingAll> findAllDatabyDateRangeAndStatus(String fromDate, String toDate, String status);

    @Query(value = "SELECT * FROM cheque_charge where charge_code = :chargeCode", nativeQuery = true)
    Optional<ChequeCharge> getChequeCharges(@Param("chargeCode") String chargeCode);

    @Query(value = "SELECT id as Id, cheque_number as ChequeNumber, bank_name as BankName, bank_code as BankCode, bank_branch as BankBranch, debitoabaccount as DebitAccount, name_as_per_cheque as NameAsPerCheque, credit_cust_operative_account as CreditCustOperativeAccount, status as Status, cheque_rand_code as ChequeRandCode, verified_flag as VerifiedFlag_1, verified_flag_2 as VerifiedFlag_2  FROM salaryupload  WHERE (verified_flag_2  = 'N' OR  verified_flag  = 'N')   and deleted_flag='N' limit 50", nativeQuery = true)
    List<CheckProcessingAll> getApprovalList();

    @Query(value = "SELECT 'Cheque Entry' AS type, 'cheque' AS typeCode, amount, id, cheque_rand_code AS uniqueCode, credit_cust_operative_account AS account, entered_time As entryTime, verified_flag, verified_flag_2 FROM cheque_processing WHERE   (rejected_flag  = 'N' OR  rejected_flag  is null ) and  (verified_flag = 'N' OR verified_flag_2 = 'N')  and (verified_flag = 'N' OR (verified_flag = 'Y' and verified_by <> :username )) ", nativeQuery = true)
    List<BatchSalaryCheques> getRoughApprovalList(@Param("username") String username);
    @Query(value = "SELECT 'Unclosed Cheque' AS type, IF(cleared_flag = 'Y' , 'Cleared', 'Not Cleared') AS status, 'cheque' AS typeCode, amount, id, cheque_rand_code AS uniqueCode, credit_cust_operative_account AS account, entered_time As entryTime, verified_flag, verified_flag_2 FROM cheque_processing WHERE   (rejected_flag  = 'N' OR  rejected_flag  is null ) and (posted_flag = 'N' OR posted_flag is null) ", nativeQuery = true)
    List<BatchSalaryCheques> getRoughUnpostedList(@Param("username") String username);

    interface ChequeCharge {
        Long getId();

        String getCharge_code();

        String getName();

        Character getCollect_charges();// Y or N

        String getEvent_id();
    }

    interface CheckProcessingAll {
        long getId();

        String getChequeNumber();

        Double getAmount();


        String getBankName();

        String getBankCode();

        String getBankBranch();

        String getDebitAccount();

        String getNameAsPerCheque();

        String getCreditCustOperativeAccount();

        String getStatus();

        Character getEnteredFlag();

        Date getEnteredTime();

        String getEnteredBy();

        String getChequeRandCode();

        Character getVerifiedFlag_1();

        Character getVerifiedFlag_2();

    }

    Optional<ChequeProcessing> findByChequeRandCode(String chequeRandCode);

    @Query(value = "select id AS Id, cheque_number AS ChequeNumber, bank_name AS BankName, bank_code AS BankCode, bank_branch AS BankBranch, debitoabaccount AS DebitAccount, name_as_per_cheque AS NameAsPerCheque, credit_cust_operative_account AS CreditCustOperativeAccount, status AS Status, cheque_rand_code AS ChequeRandCode,entered_time AS enteredTime, entered_flag AS enteredFlag, entered_by AS enteredBy, verified_flag AS VerifiedFlag_1, verified_flag_2 AS VerifiedFlag_2 FROM cheque_processing WHERE entity_id=:entityId AND entered_time BETWEEN :fromDate AND :toDate AND verified_flag = 'N' AND verified_flag_2 = 'N' ORDER BY id DESC", nativeQuery = true)
    List<CheckProcessingAll> findAllChequesProcessingByEntityIdDateRange(String entityId, String fromDate, String toDate);
    @Query(value = "SELECT COUNT(*) FROM cheque_processing WHERE entity_id=:entityId AND deleted_flag = 'N' AND verified_flag = 'N' AND verified_flag_2 = 'N'", nativeQuery = true)
    Integer countAllAccountsChequesForProcessing(String entityId);
}
