package com.emtechhouse.System.Welfare;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WelfareRepository extends JpaRepository<Welfare, Long> {
    Optional<Welfare> findByEntityIdAndWelfareCode(String entityId, String welfareCode);
    Optional<Welfare> findByWelfareCode(String welfareCode);
    Optional<Welfare> findByEntityIdAndWelfareCodeAndDeletedFlag(String entityId, String welfareCode, Character flag);
    List<Welfare> findByEntityIdAndDeletedFlagOrderByIdDesc(String entityId, Character flag);
    List<Welfare> findByEntityIdAndDeletedFlagAndVerifiedFlagOrderByIdDesc(String entityId, Character deletedFlag, Character verifiedFlag);
}
