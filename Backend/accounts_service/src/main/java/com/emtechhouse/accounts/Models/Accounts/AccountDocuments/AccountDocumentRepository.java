package com.emtechhouse.accounts.Models.Accounts.AccountDocuments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountDocumentRepository extends JpaRepository<AccountDocument, Long> {
    @Query(value = "select ad.* from account_document ad JOIN accounts a ON ad.account_id_fk= a.id WHERE a.acid=:acid", nativeQuery = true)
    List<AccountDocument> findAccountDocumentByAcid(String acid);
}
