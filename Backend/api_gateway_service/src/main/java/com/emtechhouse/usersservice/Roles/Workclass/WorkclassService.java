package com.emtechhouse.usersservice.Roles.Workclass;

import com.emtechhouse.usersservice.Roles.Workclass.Privileges.Privilege;
import com.emtechhouse.usersservice.utils.DataNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class WorkclassService {
    private final WorkclassRepo workclassRepo;

    public WorkclassService(WorkclassRepo workclassRepo) {
        this.workclassRepo = workclassRepo;
    }


    public Workclass addWorkclass(Workclass workclass) {
        try {
//            List<Privilege> privileges = workclass.getPrivileges();

            return workclassRepo.save(workclass);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public List<Workclass> findAllWorkclasss() {
        try {
            return workclassRepo.findAll();
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    public Workclass findById(Long id){
        try{
            return workclassRepo.findById(id).orElseThrow(()-> new DataNotFoundException("Data " + id +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }
    public Workclass updateWorkclass(Workclass workclass) {
        try {
            return workclassRepo.save(workclass);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public void deleteWorkclass(Long id) {
        try {
            workclassRepo.deleteById(id);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
    }
}
