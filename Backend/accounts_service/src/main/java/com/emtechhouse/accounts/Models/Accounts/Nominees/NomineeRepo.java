package com.emtechhouse.accounts.Models.Accounts.Nominees;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NomineeRepo extends JpaRepository<Nominee, Long> {
}
