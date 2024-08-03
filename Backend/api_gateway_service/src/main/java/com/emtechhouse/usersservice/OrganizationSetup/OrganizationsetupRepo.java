package com.emtechhouse.usersservice.OrganizationSetup;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganizationsetupRepo extends JpaRepository<Organizationsetup, Long> {
    List<Organizationsetup> findByDeletedFlag(Character flag);
}
