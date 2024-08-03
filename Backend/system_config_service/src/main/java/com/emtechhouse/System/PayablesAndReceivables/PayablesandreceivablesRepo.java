package com.emtechhouse.System.PayablesAndReceivables;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PayablesandreceivablesRepo extends JpaRepository<Payablesandreceivables, Long> {
    List<Payablesandreceivables> findByEntityIdAndDeletedFlag(String entityId, Character flag);
}
