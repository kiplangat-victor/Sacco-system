package com.emtechhouse.usersservice.SaccoEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface SaccoEntityRepository extends JpaRepository<SaccoEntity, Long> {
    Optional<SaccoEntity> findByEntityPhoneNumberAndDeletedFlag(String entityPhoneNumber,Character deletedFlag);
    Optional<SaccoEntity> findByEntityNameAndEntityLocationAndDeletedFlag(String entityName,String entityLocation,Character deletedFlag);
    Optional<SaccoEntity> findByEntityEmailAndDeletedFlag(String entityEmail,Character deletedFlag);
    Optional<SaccoEntity> findByEntityIdAndDeletedFlag(String entityId,Character deletedFlag);
    List<SaccoEntity> findByDeletedFlag(Character deletedFlag);
    List<SaccoEntity> findByDeletedFlagAndVerifiedFlag(Character deletedFlag, Character verifiedFlag);

    @Query(value = "select id as Id, entity_id as EntityId, entity_name as EntityName, entity_location as EntityLocation, entity_status as EntityStatus, posted_by as PostedBY, verified_flag as VerifiedFlag from sacco_entity where deleted_flag=:deletedFlag", nativeQuery = true)
    List<Saccomin> findMinDetails(Character deletedFlag);

    interface Saccomin {
        Long getId();
        String getEntityId();
        String getEntityName();
        String getEntityLocation();
        String getEntityStatus();
        String getPostedBy();
        String isVerifiedFlag();
    }

}

