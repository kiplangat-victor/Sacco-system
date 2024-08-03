package com.emtechhouse.accounts.Models.Accounts.OfficeAccount;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface OfficeAccountRepository extends JpaRepository<OfficeAccount, Long>{
    @Query(value = "SELECT oa.* FROM `office_account` oa JOIN accounts a ON oa.account_id_fk=a.id WHERE a.acid=:acid", nativeQuery = true)
    Optional<OfficeAccount> getOfficeAccountSpecificDetails(String acid);
}
