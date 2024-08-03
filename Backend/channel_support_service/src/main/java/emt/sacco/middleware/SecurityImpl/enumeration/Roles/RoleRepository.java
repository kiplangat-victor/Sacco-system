package emt.sacco.middleware.SecurityImpl.enumeration.Roles;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<SRole, Long> {
    Optional<SRole> findByEntityIdAndNameAndDeletedFlag(String entityId, String name, Character deletedFlag);
    Optional<SRole> findByEntityIdAndRoleCodeAndDeletedFlag(String entityId, String roleCode, Character deletedFlag);
    List<SRole> findByEntityIdAndDeletedFlag(String entityId, Character deletedFlag);

}
