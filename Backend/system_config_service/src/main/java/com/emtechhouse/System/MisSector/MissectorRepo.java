package com.emtechhouse.System.MisSector;

import com.emtechhouse.System.SalaryCharges.SalaryCharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MissectorRepo extends JpaRepository<Missector, Long> {
    Optional<Missector> findByMisCode(String miscode);
    List<Missector> findByEntityIdAndDeletedFlag(String entityId, Character flag);
    Optional<Missector> findByEntityIdAndMisCodeAndDeletedFlag(String entityId, String miscode, Character flag);
    Optional<Missector> findByEntityIdAndMisCode(String entityId, String customerCode);
    List<Missector> findAllByEntityIdAndDeletedFlagOrderByIdDesc(String entityId, Character deletedFlag);

}
