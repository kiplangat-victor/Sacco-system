package com.emtechhouse.System.LinkedOrganization;

import com.emtechhouse.System.Utils.DataNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class LinkedorganizationService {
    @Autowired
    private LinkedorganizationRepo linkedorganizationRepo;

    public Linkedorganization addLinkedorganization(Linkedorganization linkedorganization) {
        try {
            return linkedorganizationRepo.save(linkedorganization);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public List<Linkedorganization> getAllLinkedorganizations() {
        try {
            return linkedorganizationRepo.findAll();
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    public Linkedorganization findById(Long id){
        try{
            return linkedorganizationRepo.findById(id).orElseThrow(()-> new DataNotFoundException("Data " + id +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }
    public Linkedorganization findByLinkedOrganizationCode(String linkedOrganizationCode){
        try{
            return linkedorganizationRepo.findByLinkedOrganizationCode(linkedOrganizationCode).orElseThrow(()-> new DataNotFoundException("Data " + linkedOrganizationCode +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }
    public Linkedorganization updateLinkedorganization(Linkedorganization linkedorganization) {
        try {
            return linkedorganizationRepo.save(linkedorganization);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public void deleteLinkedorganization(Long id) {
        try {
            linkedorganizationRepo.deleteById(id);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
    }
}

