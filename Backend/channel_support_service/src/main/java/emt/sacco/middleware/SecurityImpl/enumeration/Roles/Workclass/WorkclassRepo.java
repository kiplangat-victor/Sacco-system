package emt.sacco.middleware.SecurityImpl.enumeration.Roles.Workclass;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkclassRepo extends JpaRepository<SWorkclass, Long> {
    List<SWorkclass> findByEntityIdAndDeletedFlag(String entityId, Character flag);
    List<SWorkclass> findByRoleIdAndDeletedFlag(Long role_id, Character flag);

    @Query(value = "SELECT * FROM sworkclass WHERE work_class = :name", nativeQuery = true)
    Optional<SWorkclass> findByName(@Param("name") String name);
}
