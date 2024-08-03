package com.emtechhouse.usersservice.OrganizationSetup;

import com.emtechhouse.usersservice.utils.DataNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class OrganizationService {
    private final OrganizationsetupRepo organizationsetupRepo;

    public OrganizationService(OrganizationsetupRepo organizationsetupRepo) {
        this.organizationsetupRepo = organizationsetupRepo;
    }


    public Organizationsetup addEntity(Organizationsetup organizationsetup) {
        try {
            return organizationsetupRepo.save(organizationsetup);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public List<Organizationsetup> findAllEntitys() {
        try {
            return organizationsetupRepo.findAll();
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    public Organizationsetup findById(Long id){
        try{
            return organizationsetupRepo.findById(id).orElseThrow(()-> new DataNotFoundException("Data " + id +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }

    public Organizationsetup updateEntity(Organizationsetup organizationsetup) {
        try {
            return organizationsetupRepo.save(organizationsetup);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public void deleteEntity(Long id) {
        try {
            organizationsetupRepo.deleteById(id);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
    }
}