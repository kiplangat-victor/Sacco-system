package com.emtechhouse.accounts.TransactionService.ChequeComponent;

import com.emtechhouse.accounts.TransactionService.Responses.ChequeInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChequebookRepo extends JpaRepository<Chequebook, Long> {
    Optional<Chequebook>  findByEntityIdAndMicrCodeAndDeletedFlag(String entityId, String micrCode, Character deletedFlag);
    List<Chequebook> findByEntityIdAndDeletedFlag(String entityId,Character deletedFlag);

    Optional<Chequebook> findByMicrCodeAndDeletedFlag(String chequeNo,Character deleteFlag);
    @Query(value = "select exists( select * from chequebook where micr_code=:chequeNo) as IsExist",nativeQuery = true)
    Integer checkChequeExistance(String chequeNo);
    Chequebook findByMicrCodeAndEntityIdAndDeletedFlag(String chequeNo,String entity_id,Character deletedFlag);
    @Query(value = " select exists( select * from chequebook where micr_code=:chequeNo and :leafNo between start_serial_no and end_serial_no ) as IsExist;",nativeQuery = true)
    Integer checkIfleafIsInrange(String chequeNo,Integer leafNo);
    @Query(value = "select a.acid,ck.account_number,a.account_balance,a.account_type,ck.micr_code,a.account_name from accounts a join chequebook ck on ck.account_number=a.acid where ck.micr_code=:chequeNo",nativeQuery = true)
    ChequeInfo findChequebookInfo(String chequeNo);



}