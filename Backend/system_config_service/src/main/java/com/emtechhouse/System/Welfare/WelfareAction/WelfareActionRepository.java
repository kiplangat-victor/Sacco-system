package com.emtechhouse.System.Welfare.WelfareAction;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WelfareActionRepository extends JpaRepository<WelfareAction, Long> {
    Optional<WelfareAction> findByActionCode(String actionCode);
}
