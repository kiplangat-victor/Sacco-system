package com.emtechhouse.accounts.Models.Accounts.AccountClosure;

import com.emtechhouse.accounts.Models.Accounts.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountActivationRepo extends JpaRepository<AccountActivationInfo, Long> {

    Optional<AccountActivationInfo> findByAccount(Account account);

    @Query(value = "SELECT id ad ID, account_type as accountType, acid as acid,account_status as accountStatus FROM accounts WHERE acid = :acid order by id DESC limit 1", nativeQuery = true)
    Optional<selectAccountByAcid> selectAccountByAcid(String acid);

    @Query(value = "SELECT dtd.transaction_code from dtd join part_tran pt on pt.transaction_header_id = dtd.sn WHERE pt.acid = :acid and charge_event_id = 'ACT' limit 1", nativeQuery = true)
    String selectTransactionCode(String acid);

    @Query(value = "SELECT dtd.transaction_code from dtd join part_tran pt on pt.transaction_header_id = dtd.sn WHERE pt.acid = :acid and charge_event_id = 'BLE' order by dtd.sn DESC LIMIT 1", nativeQuery = true)
    String selectBalanceTransactionCode(String acid);

    @Query(value = "SELECT dtd.transaction_code from dtd join part_tran pt on pt.transaction_header_id = dtd.sn WHERE pt.acid = :acid and charge_event_id = 'SMS' order by dtd.sn DESC LIMIT 1", nativeQuery = true)
    String selectSmsTransactionCode(String acid);

    @Query(value = "SELECT dtd.transaction_code from dtd join part_tran pt on pt.transaction_header_id = dtd.sn WHERE dtd.transaction_code = :transactionCode order by dtd.sn DESC, pt.sn DESC limit 1", nativeQuery = true)
    String selectActivationCode(String transactionCode);

    interface  selectAccountByAcid {
        public Long getID();
        public String getAcid();
        public  String getAccountStatus();
        public  String getAccountType();

    }

}
