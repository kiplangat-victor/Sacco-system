package com.emtechhouse.usersservice.Roles.Workclass.Privileges;

import com.emtechhouse.usersservice.Roles.Workclass.Workclass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrivilegeRepo extends JpaRepository<Privilege, Long> {
    List<Privilege> findByWorkclassAndSelected(Workclass workclass, Boolean selected);
}

