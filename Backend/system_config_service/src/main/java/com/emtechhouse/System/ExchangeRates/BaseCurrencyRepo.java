package com.emtechhouse.System.ExchangeRates;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BaseCurrencyRepo extends JpaRepository<BaseCurency,Long> {



}
