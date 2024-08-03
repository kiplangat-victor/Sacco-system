package com.emtechhouse.reports.Responses;

import com.emtechhouse.reports.Resources.ReportRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReportRequestRepo extends JpaRepository<ReportRequest, Long> {

    @Query(value = "SELECT description FROM customer_type WHERE customer_type = :customer_type", nativeQuery = true)
    Optional<String> category(@Param("customer_type") String customer_type);
}
