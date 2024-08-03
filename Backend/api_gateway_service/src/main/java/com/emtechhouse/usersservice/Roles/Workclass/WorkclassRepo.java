package com.emtechhouse.usersservice.Roles.Workclass;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkclassRepo extends JpaRepository<Workclass, Long> {
    List<Workclass> findByEntityIdAndDeletedFlag(String entityId, Character flag);
    List<Workclass> findByRoleIdAndDeletedFlag(Long role_id, Character flag);

    @Query(value = "SELECT * FROM workclass WHERE work_class = :name", nativeQuery = true)
    Optional<Workclass> findByName(@Param("name") String name);
}
