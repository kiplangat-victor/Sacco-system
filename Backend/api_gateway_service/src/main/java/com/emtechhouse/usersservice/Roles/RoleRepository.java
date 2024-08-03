package com.emtechhouse.usersservice.Roles;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByEntityIdAndNameAndDeletedFlag(String entityId,String name,Character deletedFlag);
    Optional<Role> findByEntityIdAndRoleCodeAndDeletedFlag(String entityId,String roleCode,Character deletedFlag);
    List<Role> findByEntityIdAndDeletedFlag(String entityId, Character deletedFlag);

}
