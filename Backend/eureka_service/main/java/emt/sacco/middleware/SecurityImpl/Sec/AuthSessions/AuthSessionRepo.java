package emt.sacco.middleware.SecurityImpl.Sec.AuthSessions;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthSessionRepo extends JpaRepository<SAuthSession, Long> {
}
