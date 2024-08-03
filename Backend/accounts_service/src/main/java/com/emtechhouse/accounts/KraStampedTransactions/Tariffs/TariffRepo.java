package com.emtechhouse.accounts.KraStampedTransactions.Tariffs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TariffRepo extends JpaRepository<Tariff, Long> {
    Optional<Tariff> findByTarrifNm(String tarrifNm);
}
