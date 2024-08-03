package com.emtechhouse.accounts.TransactionService.EODProcess.PushToDB;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface EODStatusReportRepository extends JpaRepository<EODStatusReport,Long> {
    Optional<EODStatusReport> findByEntityIdAndPrevSystemDate(String entityId, Date prevSystemDate);
    List<EODStatusReport> findByEntityId(String entityId);
}
