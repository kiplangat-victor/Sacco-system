package com.emtechhouse.System.Entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EntityManagementRepository extends JpaRepository<EntityManagement, Long> {
    Optional<EntityManagement> findEntityManagementById(Long id);

    Optional<EntityManagement> findEntityManagementByEntityIdAndDeletedFlag(String entityId, Character deletedFlag);

    Optional<EntityManagement> findEntityManagementByEntityNameAndDeletedFlag(String entityName, Character deletedFlag);

    List<EntityManagement> findAll();

    List<EntityManagement> findAllByDeletedFlagOrderByIdDesc(Character deletedFlag);

    List<EntityManagement> findAllByDeletedFlagAndVerifiedFlagOrderByIdDesc(Character deletedFlag, Character verifiedFlag);
}
