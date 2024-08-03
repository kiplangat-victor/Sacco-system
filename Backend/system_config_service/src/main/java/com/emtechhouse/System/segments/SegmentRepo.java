package com.emtechhouse.System.segments;

import com.emtechhouse.System.SalaryCharges.SalaryCharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SegmentRepo extends JpaRepository<Segment, Long> {
    Optional<Segment> findBySegmentCode(String segmentCode);
      List<Segment> findByEntityIdAndDeletedFlag(String entityId, Character flag);
    Optional<Segment> findByEntityIdAndSegmentCodeAndDeletedFlag(String entityId, String segmentCode, Character flag);
    Optional<Segment> findByEntityIdAndSegmentCode(String entityId, String segmentCode);
    List<Segment> findAllByEntityIdAndDeletedFlagOrderByIdDesc(String entityId, Character deletedFlag);

}
