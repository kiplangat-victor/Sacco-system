package com.emtechhouse.System.MisSector.MisSubsector;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MissubsectorRepo extends JpaRepository<Missubsector, Long> {
    Optional<Missubsector> findByMisSubcode(String missubcode);
    List<Missubsector> findByEntityIdAndDeletedFlagOrderByIdDesc(String entityId, Character flag);
    Optional<Missubsector> findByEntityIdAndMisSubcodeAndDeletedFlag(String entityId, String misSubcode, Character flag);
    Optional<Missubsector> findByEntityIdAndMisSubcode(String entityId, String misSubcode);

}
