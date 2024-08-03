package com.emtechhouse.accounts.TransactionService.EODProcess.SystemCalendar;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface SystemCalendarRepository extends JpaRepository<SystemCalendar,Long> {
    List<SystemCalendar> findByEntityIdAndDeletedFlag(String entityId, Character deletedFlag);
    List<SystemCalendar> findByDeletedFlag( Character deletedFlag);
    List<SystemCalendar> findByIdAndDeletedFlag(Long id,Character deletedFlag);

    //Queries For Disable user accounts during EOD
    @Transactional
    @Modifying
    @Query(nativeQuery = true,value = "UPDATE users SET username = concat('eod_',username) WHERE username != :username AND username NOT LIKE '%eod_%' AND entity_id=:entityId ")
    void disableUserAccounts(@Param("username") String username, String entityId);

    //Enable User accounts
    @Transactional
    @Modifying
    @Query(nativeQuery = true,value = "UPDATE users SET username = substr(username,5) WHERE username LIKE '%eod_%' AND  entity_id=:entityId")
    void enableUserAccounts( String entityId);

    //Disable Account For One User
    @Transactional
    @Modifying
    @Query(nativeQuery = true,value = "UPDATE users SET username = concat('eod_',username) WHERE username = :username AND username NOT LIKE '%eod_%'")
    void disableAccountForOneUser(@Param("username") String username);

    //Enable Account for One User
    @Transactional
    @Modifying
    @Query(nativeQuery = true,value = "UPDATE users SET username = substr(username,5) WHERE username = :username AND username LIKE '%eod_%'")
    void enableAccountForOneUser(@Param("username") String username);
}
