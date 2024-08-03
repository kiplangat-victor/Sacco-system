package com.emtechhouse.System.InterestCodeParams.Interestcodeslabs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterestcodeslabsRepo extends JpaRepository<Interestcodeslabs, Long> {
    @Query(nativeQuery = true, value = "select * from interestcodeslabs where:amount between lower_limit and upper_limit LIMIT 1")
    Optional<Interestcodeslabs> findAllByAmount(String amount);

}
