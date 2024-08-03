package com.emtechhouse.System.Branches;

import com.emtechhouse.System.Utils.DataNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class BranchService {
    public final BranchRepo branchRepo;

    public BranchService(BranchRepo branchRepo) {
        this.branchRepo = branchRepo;
    }


    public Branch addBranch(Branch branch) {
        try {
            return branchRepo.save(branch);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public List<Branch> findAllBranchs() {
        try {
            return branchRepo.findAll();
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    public Branch findById(Long id){
        try{
            return branchRepo.findById(id).orElseThrow(()-> new DataNotFoundException("Data " + id +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }
    public Branch findBranchCode(String branchCode) {
        try{
            return branchRepo.findBranchByBranchCode(branchCode).orElseThrow(()-> new DataNotFoundException("Data " + branchCode +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }

    public Branch updateBranch(Branch branch) {
        try {
            return branchRepo.save(branch);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public void deleteBranch(Long id) {
        try {
            branchRepo.deleteById(id);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
    }


}
