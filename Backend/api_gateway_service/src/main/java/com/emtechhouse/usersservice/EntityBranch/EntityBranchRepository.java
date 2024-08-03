package com.emtechhouse.usersservice.EntityBranch;

import com.emtechhouse.usersservice.SaccoEntity.SaccoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EntityBranchRepository extends JpaRepository<EntityBranch, Long> {
    Optional<EntityBranch> findEntityBranchById(Long id);
    Optional<EntityBranch> findByPhoneNumberAndDeletedFlag(String phoneNumber, Character flag);
    Optional<EntityBranch> findByEmailAndDeletedFlag(String email, Character flag);
    Optional<EntityBranch> findByBranchDescriptionAndLocation(String branchDescription , String location);
    List<EntityBranch> findAllByEntityIdAndDeletedFlagAndVerifiedFlagOrderByIdDesc(String entityId, Character flag, Character verifiedFlag);
    Optional<EntityBranch> findBranchByBranchCode(String branchCode);
    Optional<EntityBranch> findBranchByEntityIdAndBranchCode(String entityId,String branchCode);
    List<EntityBranch> findAllByEntityIdAndDeletedFlagOrderByIdDesc(String entityId, Character deletedFlag);
    List<EntityBranch> findAllByDeletedFlagOrderByIdDesc(Character deletedFlag);

    List<EntityBranch> findAllByDeletedFlagAndVerifiedFlagOrderByIdDesc(Character deletedFlag, Character verifiedFlag);

    @Query(value = "SELECT * FROM entity_branch ORDER BY id LIMIT 1", nativeQuery = true)
    List<EntityBranch> findAllById();
}
