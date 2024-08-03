package com.emtechhouse.System.CurrencyParams;

import com.emtechhouse.System.SalaryCharges.SalaryCharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CurrencyRepo extends JpaRepository<Currency,Long> {
    List<Currency> findByEntityIdAndDeletedFlag(String entityId, Character flag);
    Optional<Currency> findByCurrencyCode(String currencyCode);
    Optional<Currency> findByCcyName(String ccyName);
    Optional<Currency> findCurrencyByCountry(String country);
    Optional<Currency> findByEntityIdAndCurrencyCode(String entityId, String currencyCode);
    Optional<Currency> findByEntityIdAndCurrencyCodeAndDeletedFlag(String entityId, String branchCode, Character flag);
    List<Currency> findAllByEntityIdAndDeletedFlagOrderByIdDesc(String entityId, Character deletedFlag);

}
