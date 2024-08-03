package com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader;

import com.emtechhouse.accounts.DTO.PartTran;
import com.emtechhouse.accounts.TransactionService.Requests.MinistatementInterface;
import com.emtechhouse.accounts.TransactionService.Requests.RepaymentAcounts;
import com.emtechhouse.accounts.TransactionService.Requests.TransactionStatementInterface;
import com.emtechhouse.accounts.TransactionService.Responses.AccountBalanceInterface;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface TranHeaderRepository extends JpaRepository<TransactionHeader, Long> {
    List<TransactionHeader> findByVerifiedFlag(String verifiedFlag);
    @Query(value = "SELECT * FROM transaction_header WHERE  sn=:transactionId AND verified_flag=:verifiedFlag AND delete_flag=:deleteFlag", nativeQuery = true)
    Optional<TransactionHeader> findByTransactionIdAndDeleteFlagAndVerifiedFlag(Long transactionId, String deleteFlag, String verifiedFlag);
    @Query(value = "SELECT * FROM transaction_header WHERE sn=:transactionId AND verified_flag=:verifiedFlag", nativeQuery = true)
    Optional<TransactionHeader> findByTransactionIdAndVerifiedFlag(Integer transactionId,String verifiedFlag);
    @Query(value = "SELECT * FROM transaction_header WHERE sn=:id AND verified_flag=:flag", nativeQuery = true)
    Optional<TransactionHeader> findByTransactionIdAndDeleteFlag(Integer id, String flag);

    @Query(value = "SELECT * FROM transaction_header WHERE verified_flag=:verifiedFlag AND delete_flag=:deleteFlag", nativeQuery = true)
    List<TransactionHeader> findByVerifiedFlagAndDeleteFlag(String verifiedFlag, String deleteFlag);


    @Query(value = "SELECT SUM(CAST(CASE WHEN pt.part_tran_type = 'Debit' THEN - pt.tran_amount ELSE pt.tran_amount END AS DECIMAL (65 , 2 ))) AS account_balance FROM part_tran AS pt JOIN dtd AS d ON pt.transaction_header_id = d.sn AND d.posted_flag = 'Y' WHERE acid = :acid AND pt.tran_date <= :endDate", nativeQuery = true)
    Double getExpenseBalanceAtEndOfYear(String acid, String endDate);

    @Query(value = "SELECT SUM(CAST(CASE WHEN pt.part_tran_type = 'Debit' THEN - pt.tran_amount ELSE pt.tran_amount END AS DECIMAL (65 , 2 ))) AS account_balance FROM part_tran AS pt JOIN dtd AS d ON pt.transaction_header_id = d.sn AND d.posted_flag = 'Y' WHERE acid = :acid AND pt.tran_date <= :endDate", nativeQuery = true)
    Double getIncomeBalanceAtEndOfYear(String acid, String endDate);


    @Query(value = "select acid from part_tran where transaction_header_id = :TransactionHeaderId AND account_type ='SBA'  limit 1",nativeQuery = true)
    String getAcid(String TransactionHeaderId);


    @Query(value = "select acid from part_tran where transaction_header_id = :TransactionHeaderId AND account_type ='SBA' and part_tran_type = 'Credit'  limit 1",nativeQuery = true)
    String getAcidCredit(String TransactionHeaderId);


@Query(value = "SELECT transaction_code FROM loan_demand WHERE transaction_code IN " +
        "(SELECT transaction_code FROM loan_demand " +
        "WHERE demand_date BETWEEN :startDate AND :endDate " +
        "AND (deleted_flag IS NULL OR deleted_flag = 'N') AND loan_sn IN " +
        "(SELECT sn FROM loan WHERE account_id_fk IN " +
        "(SELECT id FROM accounts WHERE acid = :acid))) ORDER BY created_on DESC ;", nativeQuery = true)
List<String> transactionsForMassiveReversal(String acid, String startDate, String endDate);



    @Query(value = "select * from dtd where transaction_type LIKE :transactionType AND entered_time between :fromDate and :toDate AND status LIKE :status ORDER BY sn DESC", nativeQuery = true)
    List<TransactionHeader> findAllTransactionsbyTransactionTypeAndDateRangeAndStatus(String transactionType, String fromDate, String toDate, String status);


    Optional<TransactionHeader> findByTransactionCode(String transactionCode);

    @Query(value = "SELECT * FROM dtd  WHERE transaction_code =:transactionCode and  deleted_flag='N'", nativeQuery = true)
    Optional<TransactionHeader> findByTransactionCode1(String transactionCode);

    Optional<TransactionHeader> findByEntityIdAndTransactionCodeAndDeletedFlag(String entityId, String transactionCode, Character flag);

    @Query(value = "select product_type from product where product_code =:product_code",nativeQuery = true)
    String getProductType(String product_code);
    //entered
    @Query(value = "SELECT * FROM dtd  WHERE entered_time  BETWEEN :fromDate AND :toDate and entity_id =:entityId and  entered_flag='Y'", nativeQuery = true)
    List<TransactionHeader> getentered(String fromDate, String toDate,String entityId);
    //modified
    @Query(value = "SELECT * FROM dtd  WHERE entered_time  BETWEEN :fromDate AND :toDate and entity_id =:entityId and modified_flag='Y'", nativeQuery = true)
    List<TransactionHeader> getmodified(String fromDate, String toDate,String entityId);
    //posted
    @Query(value = "SELECT * FROM dtd  WHERE entered_time  BETWEEN :fromDate AND :toDate and entity_id =:entityId and posted_flag='Y'", nativeQuery = true)
    List<TransactionHeader> getposted(String fromDate, String toDate,String entityId );
    //verirified
    @Query(value = "SELECT * FROM dtd  WHERE entered_time  BETWEEN :fromDate AND :toDate and entity_id =:entityId and verified_flag='Y'", nativeQuery = true)
    List<TransactionHeader> getVerified(String fromDate, String toDate,String entityId);
    //deleleted
    @Query(value = "SELECT * FROM dtd  WHERE entered_time  BETWEEN :fromDate AND :toDate and entity_id =:entityId and deleted_flag='Y'", nativeQuery = true)
    List<TransactionHeader> getDeleted(String fromDate, String toDate,String entityId );


    @Query(value = "SELECT * FROM dtd WHERE tran_date >= :systemDate AND tran_date < :systemDate + INTERVAL 1 DAY and deleted_flag='N' AND entity_id =:entityId", nativeQuery = true)
    List<TransactionHeader> getTodaytransactions(String systemDate, String entityId);

//    @Query(value = "SELECT * FROM dtd d JOIN part_tran pt ON d.sn=pt.transaction_header_id WHERE d.tran_date >= :date AND d.tran_date < CURDATE() + INTERVAL 1 DAY AND d.deleted_flag = 'N'", nativeQuery = true)
//    List<com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans.PartTran> getTodaytransaction(String date);

    @Query(value = "select tran_date, total_amount from dtd where tran_date >= :date AND tran_date < CURDATE() + INTERVAL 1 DAY AND deleted_flag = 'N' LIMIT 1;", nativeQuery = true)
    List<com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans.PartTran> getTodaytransaction(String date);


    @Transactional
    @Modifying
    @Query(value = "DELETE FROM dtd WHERE eod_status = 'Y'",nativeQuery = true)
    void deleteAllBackedUp();

    @Transactional
    @Modifying
    @Query(value = "SELECT * FROM dtd WHERE eod_status = 'Y'",nativeQuery = true)
    List<TransactionHeader> AllBackedUp();

    @Transactional
    @Modifying
    @Query(value = "UPDATE dtd SET eod_status= 'Y' WHERE sn = :sn",nativeQuery = true)
    void updateEODstatusInDTD(Long sn);

    @Query(value = "select account_balance,book_balance,account_name,currency,account_type,account_status from accounts where acid=:acid",nativeQuery = true)
    AccountBalanceInterface getbalance(String acid);

    @Query(value = "select account_balance from accounts where acid=:acid",nativeQuery = true)
    Double Accountbalance(String acid);

    @Query(value = "select lien_amount from accounts where acid=:acid",nativeQuery = true)
    Double getLienAmount(String acid);
    @Query(value = "select book_balance from accounts where acid=:acid",nativeQuery = true)
    Double getBookBalance(String acid);

    @Query(value = "select account_type from accounts where acid=:acid",nativeQuery = true)
    String getAccountType(String acid);
    @Query(value = "select account_status from accounts where acid=:acid",nativeQuery = true)
    String checkAccountStatus(String acid);
    @Query(value = "select verified_flag from accounts where acid=:acid",nativeQuery = true)
    Character checkVerifiedStatus(String acid);

    @Query(value = "select is_withdrawal_allowed from accounts where acid=:acid",nativeQuery = true)
    boolean checkDebitpermision(String acid);


    @Query(value = "select exists( select * from accounts where acid =:acid) as IsExist",nativeQuery = true)
    Integer checkAccountExistance(String acid);


    @Query(value = "SELECT pt.acid, pt.parttran_identity, ac.account_name, ac.account_balance, dtd.posted_time, pt.tran_amount, CAST(pt.tran_date AS DATETIME) as tran_date, pt.part_tran_type, pt.tran_particulars, dtd.transaction_code, dtd.transaction_type FROM dtd JOIN part_tran pt ON pt.transaction_header_id = dtd.sn JOIN accounts ac ON ac.acid = pt.acid WHERE pt.acid =:acid ORDER BY tran_date DESC LIMIT 5",nativeQuery = true)
    List<MinistatementInterface> getMinistatement(String acid);

    @Query(value = "SELECT pt.tran_amount, dtd.transaction_code from dtd join part_tran pt on pt.transaction_header_id = dtd.sn WHERE pt.acid = :acid order by dtd.sn DESC LIMIT 1;",nativeQuery = true)
    List<TransactionStatementInterface> getTransactionStatement(String acid);

    @Query(value = "select pt.acid,pt.account_balance,pt.tran_amount,pt.tran_particulars,CAST(pt.tran_date AS DATETIME) as tran_date,pt.part_tran_type,dtd.transaction_code,dtd.transaction_type from dtd join part_tran pt on pt.transaction_header_id=dtd.sn  where pt.acid =:acid  and pt.tran_date BETWEEN :fromDate AND :toDate order by tran_date",nativeQuery = true)
    List<MinistatementInterface> getstatement(String acid,String fromDate, String toDate);



    @Query(value = "select YEAR(dtd.tran_date) as Year, MONTHNAME(dtd.tran_date) as Month, part_tran.acid as Account, dtd.transaction_code as TransactionCode,dtd.tran_date as TranDate,part_tran.part_tran_type as ParttranType,part_tran.tran_amount as TranAmount, dtd.posted_by as ServiceBy from dtd join part_tran on dtd.sn=part_tran.transaction_header_id WHERE dtd.verified_flag='Y' AND part_tran.acid =:acid AND YEAR(dtd.tran_date) =:year  GROUP BY dtd.tran_date\n" +
            "UNION\n" +
            "select YEAR(htd.tran_date) as Year, MONTHNAME(htd.tran_date) as Month,  part_tran_history.acid as Account, htd.transaction_code as TransactionCode,htd.tran_date as TranDate,part_tran_history.part_tran_type as ParttranType,part_tran_history.tran_amount as TranAmount, htd.posted_by as ServiceBy from htd join part_tran_history on htd.sn=part_tran_history.transaction_header_id WHERE htd.verified_flag='Y' AND part_tran_history.acid =:acid AND YEAR(htd.tran_date)=:year  GROUP BY htd.tran_date;", nativeQuery = true)
    List<Sharecapital> findTransactionPerAccountAndYear(String acid, Integer year);


    @Query(value = "select product_code as ProductCode,account_type as AccountType,acid as Acid,account_balance as AccountBalance,account_status as AccountStatus,account_name as AccountName from accounts where product_code ='SA06' and account_status='ACTIVE' and entity_id=:entity_id",nativeQuery = true)
    List<RepaymentAcounts> fetchRepaymentsAccounts(String entity_id);

    @Query(value = "select c.leaf_no from dtd d join cheque_instruments c on d.sn=c.transaction_header_id where d.transaction_type='Process Cheque' and d.posted_flag='Y'",nativeQuery = true)
    Integer[] getChequeLeafNos();
    @Query(value = "SELECT COUNT(*) FROM dtd WHERE deleted_flag = 'N' AND verified_flag = 'N'", nativeQuery = true)
    Integer countAllTransactios();

    @Query(value = " select teller_user_name as username, teller_ac as account from telleraccount where teller_user_name = :user", nativeQuery = true)
    Optional<TellerDetails> getTellerDetails(@Param("user") String user);


    @Query(value = "select * from dtd where ( verified_flag_2 = 'N' OR  verified_flag = 'N' ) and (verified_flag = 'N' OR (verified_flag = 'Y' and verified_by <> :username ))   and rejected_flag = 'N' and entered_by <> 'SYSTEM' ORDER BY sn DESC limit 50", nativeQuery = true)
    List<TransactionHeader> findAllForApproval(@Param("username") String username);


    @Query(value = "select * from dtd d left join part_tran pt on d.sn = pt.transaction_header_id where pt.sn = :sn", nativeQuery = true)
    Optional<TransactionHeader> findBySn(Long sn);

    @Query(value = "select * from dtd where tran_date =:tranDate ", nativeQuery = true)
    List<TransactionHeader> findByDate(String tranDate);

    interface TellerDetails {
        String getUsername();
        String getAccount();
    }

    interface Sharecapital {
        Integer getYear();
        String getMonth();
        String getAccount();
        String getTransactionCode();
        String getTranDate();
        String getParttranType();
        Double getTranAmount();
        String getServiceBy();
    }

//    @Transactional
//    @Query(value = "select amt from charges where low_amt <= ? and max_amt >=?",nativeQuery = true);
//
}