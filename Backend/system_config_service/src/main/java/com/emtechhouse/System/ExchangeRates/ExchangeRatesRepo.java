package com.emtechhouse.System.ExchangeRates;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExchangeRatesRepo extends JpaRepository<ExchangeRate,Long> {


    Optional<ExchangeRate> findByForeignCurrency(String currency);

}
