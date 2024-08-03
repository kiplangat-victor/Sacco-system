package com.emtechhouse.System.Coinage;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CoinageRepo extends JpaRepository<Coinage, Long> {
    Optional<Coinage> findCoinageByCoinageCode(String CoinageCode);
    List<Coinage> findByEntityIdAndDeletedFlag(String entityId, Character flag);
    List<Coinage> findAllByEntityIdAndDeletedFlagAndVerifiedFlagOrderByIdDesc(String entityId, Character flag, Character verifiedFlag);
    List<Coinage> findCoinageByDeletedFlag( Character deletedFlag);
    Optional<Coinage> findCoinageByCoinageCodeAndDeletedFlag(String CoinageCode, Character deletedFlag);
    Optional<Coinage> findByEntityIdAndCoinageCodeAndDeletedFlag(String entityId, String CoinageCode, Character flag);
    List<Coinage> findAllByEntityIdAndDeletedFlagOrderByIdDesc(String entityId, Character deletedFlag);


}
