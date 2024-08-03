package com.emtechhouse.accounts.NotificationComponent.SmsService;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SMSNOtificaionRepo extends JpaRepository<SMSNotifications,Long> {
        Optional<SMSNotifications> findByMessageId(String messageId);
}
