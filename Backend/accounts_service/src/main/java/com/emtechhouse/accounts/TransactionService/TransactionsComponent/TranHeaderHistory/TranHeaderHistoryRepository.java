package com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeaderHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TranHeaderHistoryRepository extends JpaRepository<TranHeaderHistory, Long> {
    Optional<TranHeaderHistory> findByTransactionCode(String transactionCode);
    List<TranHeaderHistory> findByVerifiedFlag(String verifiedFlag);
    @Query(value = "SELECT * FROM transaction_header WHERE  sn=:transactionId AND verified_flag=:verifiedFlag AND delete_flag=:deleteFlag", nativeQuery = true)
    Optional<TranHeaderHistory> findByTransactionIdAndDeleteFlagAndVerifiedFlag(Long transactionId, String deleteFlag, String verifiedFlag);
    @Query(value = "SELECT * FROM transaction_header WHERE sn=:transactionId AND verified_flag=:verifiedFlag", nativeQuery = true)
    Optional<TranHeaderHistory> findByTransactionIdAndVerifiedFlag(Integer transactionId,String verifiedFlag);
    @Query(value = "SELECT * FROM transaction_header WHERE sn=:id AND verified_flag=:flag", nativeQuery = true)
    Optional<TranHeaderHistory> findByTransactionIdAndDeleteFlag(Integer id, String flag);
    @Query(value = "SELECT * FROM transaction_header WHERE verified_flag=:verifiedFlag AND delete_flag=:deleteFlag", nativeQuery = true)
    List<TranHeaderHistory> findByVerifiedFlagAndDeleteFlag(String verifiedFlag, String deleteFlag);
    Optional<TranHeaderHistory> findByEntityIdAndTransactionCodeAndDeletedFlag(String entityId, String transactionCode, Character flag);
    @Query(value = "select product_type from product where product_code =:product_code",nativeQuery = true)
    String getProductType(String product_code);
    @Query(value = "SELECT * FROM dtd  WHERE posted_time  BETWEEN :fromDate AND :toDate and entity_id =:entityId and deleted_flag='N'", nativeQuery = true)
    List<TranHeaderHistory> filterByDate(String fromDate, String toDate,String entityId);
}
