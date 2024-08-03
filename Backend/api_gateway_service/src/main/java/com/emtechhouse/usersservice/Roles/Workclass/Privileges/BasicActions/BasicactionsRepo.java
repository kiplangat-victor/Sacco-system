package com.emtechhouse.usersservice.Roles.Workclass.Privileges.BasicActions;

import com.emtechhouse.usersservice.Roles.Workclass.Privileges.Privilege;
import com.emtechhouse.usersservice.Roles.Workclass.Workclass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BasicactionsRepo extends JpaRepository<Basicactions, Long> {
    List<Basicactions> findByPrivilegeAndWorkclassAndCode(Privilege privilege, Workclass workclass, String code);
    List<Basicactions> findByPrivilegeAndWorkclassAndDeletedFlag(Privilege privilege, Workclass workclass, Character deletedFlag);
    List<Basicactions> findByDeletedFlag(Character deletedFlag);

    List<Basicactions> findByPrivilege(Privilege privilege);

    //added
//    @Query(value = "SELECT * FROM basicactions WHERE privilege_fk = :privilegeId AND workclass_fk = :workclassId", nativeQuery = true)
//    List<Basicactions> findAllRelated(Long privilegeId,Long workclassId);
//    List<Basicactions> findByPrivilegeAndSelected(Privilege privilege, Boolean selected);

//    List<Basicactions> findAllRelated(Long privilegeId,Long workclassId);
}
