package com.emtechhouse.accounts.Models.Accounts.AccountClosure;

import com.emtechhouse.accounts.Models.Accounts.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountClosureRepo extends JpaRepository<AccountClosureInfo, Long> {
    Optional<AccountClosureInfo> findByAccount(Account account);

//    @Query(value = "DELETE from account_closure_info where id= :id", nativeQuery = true)
//    Void deleteAccountActivationInfo(Long id);
    @Query(value = "SELECT id ad ID, account_type as accountType, acid as acid,account_status as accountStatus FROM accounts WHERE acid = :acid order by id DESC limit 1", nativeQuery = true)
    Optional<selectAccountByAcid> selectAccountByAcid(String acid);
    interface  selectAccountByAcid {
        public Long getID();
        public String getAcid();
        public  String getAccountStatus();
        public  String getAccountType();

    }

}
