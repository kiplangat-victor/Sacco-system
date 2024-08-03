package com.emtechhouse.System.ChargeParams.EventId.TieredCharges;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TieredchargesRepo extends JpaRepository<Tieredcharges, Long> {
    @Query(value = "select * from tieredcharges where:amount between lower_limit and upper_limit and eventid_fk=:eventid_fk limit 1", nativeQuery = true)
    Optional<Tieredcharges> findBetweenRange(@Param("amount") String amount, Long eventid_fk);

    @Query(value = "SELECT * FROM tieredcharges WHERE eventid_fk=:eventid_fk and upper_limit = (SELECT MAX(upper_limit) FROM tieredcharges) limit 1", nativeQuery = true)
    Optional<Tieredcharges> findMaxByEventId(Long eventid_fk);
}
