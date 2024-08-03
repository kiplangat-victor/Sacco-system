package emt.sacco.middleware.SecurityImpl.repository;

import emt.sacco.middleware.SecurityImpl.Sec.SwitchUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@RequestMapping
public interface UserRepository extends JpaRepository<SwitchUsers, Long> {

    SwitchUsers findUserByUsername(String username);
    Optional<SwitchUsers> findByUsername(String username);

    SwitchUsers findUserByEmail(String email);
}
