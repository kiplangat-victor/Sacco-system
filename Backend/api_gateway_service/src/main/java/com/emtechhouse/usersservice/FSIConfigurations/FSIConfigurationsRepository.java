package com.emtechhouse.usersservice.FSIConfigurations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FSIConfigurationsRepository extends JpaRepository<FSIConfigurations, Long> {
    Optional<FSIConfigurations> findByEntityId(String entityId);

    @Query(value = "SELECT * FROM fsiconfigurations WHERE deleted_flag='N' AND verified_flag='Y' ORDER BY entity_id ASC LIMIT 1", nativeQuery = true)
    Optional<FSIConfigurations> getConfiguredHeaders();
}
