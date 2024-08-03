package com.emtechhouse.System.ChargeParams.EventTypes;


import com.emtechhouse.System.Utils.DataNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class EventtypeService {
    @Autowired
    private EventtypeRepo eventtypeRepo;


    public Eventtype addEventtype(Eventtype eventtype) {
        try {
            return eventtypeRepo.save(eventtype);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public List<Eventtype> findAllEventtypes() {
        try {
            return eventtypeRepo.findAll();
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    public Eventtype findById(Long id){
        try{
            return eventtypeRepo.findById(id).orElseThrow(()-> new DataNotFoundException("Data " + id +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }
    public Eventtype findByEventTypeCode(String eventTypeCode){
        try{
            return eventtypeRepo.findByEventTypeCode(eventTypeCode).orElseThrow(()-> new DataNotFoundException("Data " + eventTypeCode +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }
    public Eventtype updateEventtype(Eventtype eventtype) {
        try {
            return eventtypeRepo.save(eventtype);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public void deleteEventtype(Long id) {
        try {
            eventtypeRepo.deleteById(id);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
    }
}

