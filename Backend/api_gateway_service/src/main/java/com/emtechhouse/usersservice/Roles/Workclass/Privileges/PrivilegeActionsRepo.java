package com.emtechhouse.usersservice.Roles.Workclass.Privileges;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrivilegeActionsRepo extends JpaRepository<Privilege_Actions, Long> {
    List<Privilege_Actions> findByPrivilegeId(Long privilegeId);

}
