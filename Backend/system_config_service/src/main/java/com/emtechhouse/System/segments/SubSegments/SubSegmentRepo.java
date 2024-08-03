package com.emtechhouse.System.segments.SubSegments;


import com.emtechhouse.System.SalaryCharges.SalaryCharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubSegmentRepo extends JpaRepository<SubSegment, Long> {

    Optional<SubSegment> findBySubSegmentCode(String subSegmentCode);

    @Query(nativeQuery = true,  value = "select * from sub_segment where deleted_flag = 'Y';")
    List<SubSegment> findalldeletedsubSegments();

    @Query(nativeQuery = true,  value = "select * from sub_segment where deleted_flag = 'N';")
    List<SubSegment> findallundeletedsubSegments();

    Optional<SubSegment> findByEntityIdAndSubSegmentCodeAndDeletedFlag(String entityId, String subSegmentCode, Character flag);
    List<SubSegment> findByEntityIdAndDeletedFlag(String entityId, Character flag);
    Optional<SubSegment> findByEntityIdAndSubSegmentCode(String entityId, String subSegmentCode);
    List<SubSegment> findAllByEntityIdAndDeletedFlagOrderByIdDesc(String entityId, Character deletedFlag);

}