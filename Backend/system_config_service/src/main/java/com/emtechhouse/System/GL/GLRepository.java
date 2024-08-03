package com.emtechhouse.System.GL;

import com.emtechhouse.System.SalaryCharges.SalaryCharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GLRepository extends JpaRepository<GL, Long> {
    Optional<GL> findByEntityIdAndGlCode(String entityId, String glCode);
    Optional<GL> findByGlCode(String glCode);
    Optional<GL> findByEntityIdAndGlCodeAndDeletedFlag(String entityId, String glCode, Character flag);
    List<GL> findByEntityIdAndDeletedFlag(String entityId, Character flag);
    List<GL> findAllByEntityIdAndDeletedFlagOrderByIdDesc(String entityId, Character deletedFlag);

}
