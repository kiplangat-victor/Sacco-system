package emt.sacco.middleware.SecurityImpl.EntityBranch;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EntityBranchRepository extends JpaRepository<SEntityBranch, Long> {
    Optional<SEntityBranch> findEntityBranchById(Long id);
    Optional<SEntityBranch> findByPhoneNumberAndDeletedFlag(String phoneNumber, Character flag);
    Optional<SEntityBranch> findByEmailAndDeletedFlag(String email, Character flag);
    Optional<SEntityBranch> findByBranchDescriptionAndLocation(String branchDescription , String location);
    List<SEntityBranch> findAllByEntityIdAndDeletedFlagAndVerifiedFlagOrderByIdDesc(String entityId, Character flag, Character verifiedFlag);
    Optional<SEntityBranch> findBranchByBranchCode(String branchCode);
    Optional<SEntityBranch> findBranchByEntityIdAndBranchCode(String entityId, String branchCode);
    List<SEntityBranch> findAllByEntityIdAndDeletedFlagOrderByIdDesc(String entityId, Character deletedFlag);
    List<SEntityBranch> findAllByDeletedFlagOrderByIdDesc(Character deletedFlag);

    List<SEntityBranch> findAllByDeletedFlagAndVerifiedFlagOrderByIdDesc(Character deletedFlag, Character verifiedFlag);

    @Query(value = "SELECT * FROM sentity_branch ORDER BY id LIMIT 1", nativeQuery = true)
    List<SEntityBranch> findAllById();
}
