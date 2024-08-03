package com.emtechhouse.System.Employer;

import com.emtechhouse.System.CustomerType.CustomerType;
import com.emtechhouse.System.SalaryCharges.SalaryCharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployerRepo extends JpaRepository<Employer, Long> {
    @Query(value = "SELECT * FROM employer WHERE employer_code = :code", nativeQuery = true)
    Optional<Employer> findByCode(@Param("code") String code);

    List<Employer> findByEntityIdAndDeletedFlag(String entityId, Character deletedFlag);
    List<Employer> findAllByEntityIdAndDeletedFlagOrderByIdDesc(String entityId, Character deletedFlag);

}
