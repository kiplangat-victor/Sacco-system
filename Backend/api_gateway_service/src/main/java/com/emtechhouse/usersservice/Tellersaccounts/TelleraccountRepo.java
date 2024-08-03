package com.emtechhouse.usersservice.Tellersaccounts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.Column;
import java.util.List;
import java.util.Optional;

@Repository
public interface TelleraccountRepo extends JpaRepository<Telleraccount, Long> {
    List<Telleraccount> findByEntityIdAndDeletedFlag(String entityId, Character flag);
    Optional<Telleraccount> findByEntityIdAndDeletedFlagAndTellerUserName(String entityId, Character deletedFlag, String tellerUserName);
    Optional<Telleraccount> findByEntityIdAndTellerIdAndDeletedFlag(String entityId,String tellerId,Character flag);
    Optional<Telleraccount> findByEntityIdAndTellerAcAndDeletedFlag(String entityId,String tellerAc, Character flag);
}
