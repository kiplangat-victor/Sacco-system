package com.emtechhouse.accounts.Models.Accounts.RelatedParties;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RelatedPartiesRepo extends JpaRepository<RelatedParties, Long> {
}
