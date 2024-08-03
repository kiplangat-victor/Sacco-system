package com.emtechhouse.System.ChargeParams.EventTypes;

import com.emtechhouse.System.ChargeParams.EventId.EventId;
import com.emtechhouse.System.SalaryCharges.SalaryCharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventtypeRepo extends JpaRepository<Eventtype,Long> {
    Optional<Eventtype> findByEventTypeCode(String eventTypeCode);
    List<Eventtype> findByEntityIdAndDeletedFlag(String entityId, Character flag);
    Optional<Eventtype> findByEntityIdAndEventTypeCodeAndDeletedFlag(String entityId, String eventTypeCode, Character flag);
    Optional<Eventtype> findByEntityIdAndEventTypeCode(String entityId, String eventTypeCode);
    List<Eventtype> findAllByEntityIdAndDeletedFlagOrderByIdDesc(String entityId, Character deletedFlag);

}
