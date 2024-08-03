package com.emtechhouse.System.SharecapitalParams;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SharecapitalParamRepo extends JpaRepository<SharecapitalParam, Long> {

    Optional<SharecapitalParam> findByShareCapitalCode(String shareCapitalCode);
    List<SharecapitalParam> findByEntityIdAndDeletedFlag(String entityId, Character flag);
    Optional<SharecapitalParam> findByEntityIdAndShareCapitalCodeAndDeletedFlag(String entityId, String shareCapitalCode, Character flag);
    @Query(value = "update sharecapital_param set is_active=false", nativeQuery = true)
    Integer setAllInactive();
}
