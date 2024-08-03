package com.emtechhouse.System.Employer;

import com.emtechhouse.System.Utils.DataNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class EmployerService {
    public final EmployerRepo employerRepo;

    public EmployerService(EmployerRepo employerRepo) {
        this.employerRepo = employerRepo;
    }


    public Employer addEmployer(Employer employer) {
        try {
            if (employer.getCustomerCode().trim().length() == 0)
                employer.setCustomerCode(null);
            return employerRepo.save(employer);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public List<Employer> findAllEmployers() {
        try {
            return employerRepo.findAll();
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    public Employer findById(Long id){
        try{
            return employerRepo.findById(id).orElseThrow(()-> new DataNotFoundException("Data " + id +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }
    public Employer findByCode(String code) {
        try{
            return employerRepo.findByCode(code).orElseThrow(()-> new DataNotFoundException("Data " + code +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }

    public Employer updateEmployer(Employer employer) {
        try {
            if (employer.getCustomerCode().trim().length() == 0)
                employer.setCustomerCode(null);
            return employerRepo.save(employer);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public void deleteEmployer(Long id) {
        try {
            employerRepo.deleteById(id);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
    }
}
