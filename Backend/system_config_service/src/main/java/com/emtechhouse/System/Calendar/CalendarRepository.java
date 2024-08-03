package com.emtechhouse.System.Calendar;

import com.emtechhouse.System.SalaryCharges.SalaryCharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CalendarRepository extends JpaRepository<Calendar, Long> {
    Optional<Calendar> findByYearAndMonthAndVerifiedFlag(String year, String month, Character flag);
    List<java.util.Calendar> findAllByEntityIdAndDeletedFlagOrderByIdDesc(String entityId, Character deletedFlag);

}
