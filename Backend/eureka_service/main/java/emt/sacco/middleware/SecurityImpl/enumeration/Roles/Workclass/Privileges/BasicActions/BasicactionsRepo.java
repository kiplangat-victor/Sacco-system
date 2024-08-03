package emt.sacco.middleware.SecurityImpl.enumeration.Roles.Workclass.Privileges.BasicActions;

import emt.sacco.middleware.SecurityImpl.enumeration.Roles.Workclass.Privileges.SPrivilege;
import emt.sacco.middleware.SecurityImpl.enumeration.Roles.Workclass.SWorkclass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BasicactionsRepo extends JpaRepository<SBasicactions, Long> {
    List<SBasicactions> findBySPrivilegeAndSWorkclassAndCode(SPrivilege SPrivilege, SWorkclass SWorkclass, String code);
    List<SBasicactions> findBySPrivilegeAndSWorkclassAndDeletedFlag(SPrivilege SPrivilege, SWorkclass SWorkclass, Character deletedFlag);
    List<SBasicactions> findByDeletedFlag(Character deletedFlag);
}
