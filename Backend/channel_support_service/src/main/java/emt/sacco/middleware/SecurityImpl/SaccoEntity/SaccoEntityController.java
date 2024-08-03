package emt.sacco.middleware.SecurityImpl.SaccoEntity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
@RequestMapping("sacco/entity/configurations")
public class SaccoEntityController {

    private final SaccoEntityService saccoEntityService;
    private final SaccoEntityRepository saccoEntityRepository;

    public SaccoEntityController(SaccoEntityService saccoEntityService, SaccoEntityRepository saccoEntityRepository) {
        this.saccoEntityService = saccoEntityService;
        this.saccoEntityRepository = saccoEntityRepository;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addSaccoEntity(@RequestBody SSaccoEntity SSaccoEntity) {
        try {
            return ResponseEntity.ok().body(saccoEntityService.addSaccoEntity(SSaccoEntity));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/get/all")
    public ResponseEntity<?> getAllSaccoEntities() {
        try {
            return ResponseEntity.ok().body(saccoEntityService.getAllSaccoEntities());
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/find/all")
    public ResponseEntity<?> findAllSaccoEntities() {
        try {
            return ResponseEntity.ok().body(saccoEntityService.findAllSaccoEntities());
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/find/all/active")
    public ResponseEntity<?> findAllActiveSaccoEntities() {
        try {
            return ResponseEntity.ok().body(saccoEntityService.findAllActiveSaccoEntities());
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    @GetMapping("/find/by/id/{id}")
    public ResponseEntity<?> findSaccoEntityById(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok().body(saccoEntityService.findSaccoEntityById(id));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    @GetMapping("/find/by/entityId/{saccoentity}")
    public ResponseEntity<?> findBySaccoEntityId(@PathVariable("saccoentity") String saccoentity) {
        try {
            return ResponseEntity.ok().body(saccoEntityService.findBySaccoEntityId(saccoentity));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("/modify")
    public ResponseEntity<?> modifySaccoEntity(@RequestBody SSaccoEntity SSaccoEntity) {
        try {
            return ResponseEntity.ok().body(saccoEntityService.modifySaccoEntity(SSaccoEntity));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("/verify/{id}")
    public ResponseEntity<?> VerifySaccoEntity(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok().body(saccoEntityService.VerifySaccoEntity(id));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("/temporary/delete/{id}")
    public ResponseEntity<?> temporaryDelete(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok().body(saccoEntityService.temporaryDelete(id));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @DeleteMapping("/permanent/delete/{id}")
    public ResponseEntity<?> permanentDelete(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok().body(saccoEntityService.permanentDelete(id));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
}