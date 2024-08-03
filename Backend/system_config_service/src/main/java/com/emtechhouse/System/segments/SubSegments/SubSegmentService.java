package com.emtechhouse.System.segments.SubSegments;

import com.emtechhouse.System.Utils.DataNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SubSegmentService {
    @Autowired
    private SubSegmentRepo subSubSegmentRepo;



    public SubSegment addSubSegment(SubSegment subSubSegment) {
        try {
            return subSubSegmentRepo.save(subSubSegment);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public List<SubSegment> findAllSubSegments() {
        try {
            return subSubSegmentRepo.findAll();
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    public SubSegment findById(Long id){
        try{
            return subSubSegmentRepo.findById(id).orElseThrow(()-> new DataNotFoundException("Data " + id +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }
    public SubSegment findBySubsegmentCode(String miscode){
        try{
            return subSubSegmentRepo.findBySubSegmentCode(miscode).orElseThrow(()-> new DataNotFoundException("Data " + miscode +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }
    public SubSegment updateSubSegment(SubSegment segment) {
        try {
            return subSubSegmentRepo.save(segment);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public void deleteSubSegment(Long id) {
        try {
            subSubSegmentRepo.deleteById(id);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
    }
}

