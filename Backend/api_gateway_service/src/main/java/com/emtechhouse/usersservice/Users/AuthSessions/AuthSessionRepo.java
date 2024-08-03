package com.emtechhouse.usersservice.Users.AuthSessions;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthSessionRepo extends JpaRepository<AuthSession, Long> {
}
