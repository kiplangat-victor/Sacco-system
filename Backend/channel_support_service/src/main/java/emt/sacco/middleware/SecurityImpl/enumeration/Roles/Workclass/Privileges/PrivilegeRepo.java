package emt.sacco.middleware.SecurityImpl.enumeration.Roles.Workclass.Privileges;

import emt.sacco.middleware.SecurityImpl.enumeration.Roles.Workclass.SWorkclass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrivilegeRepo extends JpaRepository<SPrivilege, Long> {
    List<SPrivilege> findBySWorkclassAndSelected(SWorkclass SWorkclass, Boolean selected);
}
