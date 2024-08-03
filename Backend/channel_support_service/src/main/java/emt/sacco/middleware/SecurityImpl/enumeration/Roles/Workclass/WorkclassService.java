package emt.sacco.middleware.SecurityImpl.enumeration.Roles.Workclass;

import emt.sacco.middleware.Utils.DataNotFoundException;
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


    public SWorkclass addWorkclass(SWorkclass SWorkclass) {
        try {
//            List<Privilege> privileges = workclass.getPrivileges();

            return workclassRepo.save(SWorkclass);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public List<SWorkclass> findAllWorkclasss() {
        try {
            return workclassRepo.findAll();
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    public SWorkclass findById(Long id){
        try{
            return workclassRepo.findById(id).orElseThrow(()-> new DataNotFoundException("Data " + id +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }
    public SWorkclass updateWorkclass(SWorkclass SWorkclass) {
        try {
            return workclassRepo.save(SWorkclass);
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
