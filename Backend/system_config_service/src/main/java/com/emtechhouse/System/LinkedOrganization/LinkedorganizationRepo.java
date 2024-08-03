package com.emtechhouse.System.LinkedOrganization;

import com.emtechhouse.System.SalaryCharges.SalaryCharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LinkedorganizationRepo extends JpaRepository<Linkedorganization, Long> {
Optional<Linkedorganization> findByIdAndDeletedFlag(Long id, Character flag);
    Optional<Linkedorganization> findByLinkedOrganizationCode(String linkedOrganizationCode);
    List<Linkedorganization> findByEntityIdAndDeletedFlag(String entityId, Character flag);
    Optional<Linkedorganization> findByEntityIdAndLinkedOrganizationCodeAndDeletedFlag(String entityId, String linkedOrganizationCode, Character flag);
    List<Linkedorganization> findAllByEntityIdAndDeletedFlagOrderByIdDesc(String entityId, Character deletedFlag);

//
//    @Query(nativeQuery = true, value = "select * from linkedorganization where is_deleted ='Y'")
//    List<Linkedorganization> findalldeletedLinkedorganizations();
//
//    @Query(nativeQuery = true, value = "select * from linkedorganization where is_deleted ='N'")
//    List<Linkedorganization> findallundeletedLinkedorganizations();

}
