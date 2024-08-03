package com.emtechhouse.System.ChargeParams.ChargeAmtSlab;

import com.emtechhouse.System.SalaryCharges.SalaryCharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChrgamtslapRepo extends JpaRepository<Chrgamtslab, Long> {

    @Query(nativeQuery = true, value = "select * from chrgamtslap where slap_code = :slap_code")
    Optional<Chrgamtslab> findBySlapCode(@Param("slap_code") String slap_code);

    @Query(nativeQuery = true, value = "select * from chrgamtslap where is_deleted = 'Y'")
    List<Chrgamtslab> findalldeletedchargeamountslap();

    @Query(nativeQuery = true, value = "select * from chrgamtslap where is_deleted = 'N'")
    List<Chrgamtslab> findallundeletedchargeamountslap();
    List<Chrgamtslab> findAllByEntityIdAndDeletedFlagOrderByIdDesc(String entityId, Character deletedFlag);

}
