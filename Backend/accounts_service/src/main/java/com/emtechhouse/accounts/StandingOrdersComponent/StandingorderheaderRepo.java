package com.emtechhouse.accounts.StandingOrdersComponent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface StandingorderheaderRepo extends JpaRepository<Standingorderheader, Long> {
    Boolean existsByStandingOrderCode(String standingOrderCode);
    Boolean existsBy();
//    boolean existsCarByModel
    @Query(value = "SELECT * FROM  standingorderheader WHERE status = 'ACTIVE' AND next_run_date <= current_timestamp AND end_date >= current_timestamp AND verified_flag = 'Y'", nativeQuery = true)
    List<Standingorderheader> findByNextRunDate(@Param("nextRunDate") Date nextRunDate);

    List<Standingorderheader> findAllByPostedFlagAndDeletedFlagAndVerifiedFlag(Character postedFlag, Character deletedFlag, Character verifiedFlag);

    @Query(value = "SELECT * FROM standingorderheader  WHERE standing_order_code = :stoCode ", nativeQuery = true)
    Optional<Standingorderheader> findByStoCode(@Param("stoCode") String stoCode);
}