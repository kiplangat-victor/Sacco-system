package com.emtechhouse.accounts.StandingOrdersComponent.StandingOrderExecution;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface StandingOrderExecutionRepo extends JpaRepository<StandingOrderExecution, Long> {


    @Query(value = "SELECT * FROM standing_order_execution WHERE standing_order_code=:code AND run_date=:runDate", nativeQuery = true)
    List<StandingOrderExecution> findByStoAndRunDate(@Param("code") String code, @Param("runDate") Date nextRunDate);

}
