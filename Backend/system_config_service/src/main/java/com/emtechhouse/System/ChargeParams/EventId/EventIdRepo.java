package com.emtechhouse.System.ChargeParams.EventId;

import com.emtechhouse.System.SalaryCharges.SalaryCharge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventIdRepo extends JpaRepository<EventId,Long> {
    Optional<EventId> findByEventIdCode(String eventIdCode);
    List<EventId> findByEntityIdAndDeletedFlag(String entityId, Character flag);
    Optional<EventId> findByEntityIdAndAndChargeTypeAndDeletedFlag(String entityId,String chargeType, Character flag);
    Optional<EventId> findByEventIdCodeAndDeletedFlag(String eventIdCode, Character flag);
    Optional<EventId> findByEntityIdAndEventIdCode(String entityId, String eventIdCode);
    List<EventId> findAllByEntityIdAndDeletedFlagOrderByIdDesc(String entityId, Character deletedFlag);

}
