package com.emtechhouse.usersservice.EntityBranch;

import com.emtechhouse.usersservice.SaccoEntity.SaccoEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
@RequestMapping("entity/branches")
public class EntityBranchController {
    private final EntityBranchService entityBranchService;

    private final EntityBranchRepository entityBranchRepository;

    public EntityBranchController(EntityBranchService entityBranchService, EntityBranchRepository entityBranchRepository) {
        this.entityBranchService = entityBranchService;
        this.entityBranchRepository = entityBranchRepository;
    }
    @PostMapping("/add")
    public ResponseEntity<?> addEntityBranch(@RequestBody EntityBranch entityBranch, @RequestParam Long fk_id) {
        try {
            return ResponseEntity.ok().body(entityBranchService.addEntityBranch(entityBranch, fk_id));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }



    @GetMapping("/get/all")
    public ResponseEntity<?> getAllEntityBranches() {
        try {
            return ResponseEntity.ok().body(entityBranchService.getAllEntityBranches());
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/find/all")
    public ResponseEntity<?> findAllEntityBranches() {
        try {
            return ResponseEntity.ok().body(entityBranchService.findAllEntityBranches());
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/find/all/active")
    public ResponseEntity<?> findAllActiveEntityBranches() {
        try {
            return ResponseEntity.ok().body(entityBranchService.findAllActiveEntityBranches());
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    @GetMapping("/find/all/By/Entity")
    public ResponseEntity<?> findAllEntityBranchesByEntity() {
        try {
            return ResponseEntity.ok().body(entityBranchService.findAllEntityBranchesByEntity());
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    @GetMapping("/find/all/active/by/entity")
    public ResponseEntity<?> findAllActiveEntityBranchesByEntity() {
        try {
            return ResponseEntity.ok().body(entityBranchService.findAllActiveEntityBranchesByEntity());
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    @GetMapping("/find/by/id/{id}")
    public ResponseEntity<?> findEntityBranchById(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok().body(entityBranchService.findEntityBranchById(id));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    @GetMapping("/find/by/entity/branch/{branchCode}")
    public ResponseEntity<?> findEntityBranchByCode(@PathVariable("branchCode") String branchCode) {
        try {
            return ResponseEntity.ok().body(entityBranchService.findEntityBranchByCode(branchCode));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    @GetMapping("/find/by/entity/branch/by/entity/{branchCode}")
    public ResponseEntity<?> findEntityBranchByCodeByEntity(@PathVariable("branchCode") String branchCode) {
        try {
            return ResponseEntity.ok().body(entityBranchService.findEntityBranchByCodeByEntity(branchCode));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("/modify")
    public ResponseEntity<?> modifyEntityBranch(@RequestBody EntityBranch entityBranch) {
        try {
            return ResponseEntity.ok().body(entityBranchService.modifyEntityBranch(entityBranch));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("/verify/{id}")
    public ResponseEntity<?> VerifyEntityBranch(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok().body(entityBranchService.VerifyEntityBranch(id));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

//    @PutMapping("/temporary/delete/{id}")
//    public ResponseEntity<?> temporaryDelete(@PathVariable("id") Long id) {
//        try {
//            return ResponseEntity.ok().body(entityBranchService.temporaryDelete(id));
//        } catch (Exception e) {
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }
//
//    @DeleteMapping("/permanent/delete/{id}")
//    public ResponseEntity<?> permanentDelete(@PathVariable("id") Long id) {
//        try {
//            return ResponseEntity.ok().body(entityBranchService.permanentDelete(id));
//        } catch (Exception e) {
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }
}
