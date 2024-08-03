package com.emtechhouse.System.HolidayParams;

import com.emtechhouse.System.SalaryCharges.SalaryCharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HolidayparamsRepo extends JpaRepository<Holidayparams, Long> {
    List<Holidayparams> findByEntityIdAndDeletedFlag(String entityId, Character flag);
    Optional<Holidayparams> findByEntityIdAndHolidayCode(String entityId, String holidayCode);
    Optional<Holidayparams> findByHolidayCode(String holidayCode);
    Optional<Holidayparams> findByEntityIdAndHolidayCodeAndDeletedFlag(String entityId, String holidayCode, Character flag);
    List<Holidayparams> findAllByEntityIdAndDeletedFlagOrderByIdDesc(String entityId, Character deletedFlag);

}
