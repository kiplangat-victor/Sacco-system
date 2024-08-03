package com.emtechhouse.System.Entity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("entity/configurations")
public class EntityManagementController {
    public final EntityManagementService entityManagementService;
    public final EntityManagementRepository entityManagementRepository;

    public EntityManagementController(EntityManagementService entityManagementService, EntityManagementRepository entityManagementRepository) {
        this.entityManagementService = entityManagementService;
        this.entityManagementRepository = entityManagementRepository;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addSaccoEntity(@RequestBody EntityManagement entityManagement) {
        try {
            return ResponseEntity.ok().body(entityManagementService.addSaccoEntity(entityManagement));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/get/all")
    public ResponseEntity<?> getAllSaccoEntities() {
        try {
            return ResponseEntity.ok().body(entityManagementService.getAllSaccoEntities());
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/find/all")
    public ResponseEntity<?> findAllSaccoEntities() {
        try {
            return ResponseEntity.ok().body(entityManagementService.findAllSaccoEntities());
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/find/all/active")
    public ResponseEntity<?> findAllActiveSaccoEntities() {
        try {
            return ResponseEntity.ok().body(entityManagementService.findAllActiveSaccoEntities());
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("/modify")
    public ResponseEntity<?> modifySaccoEntity(@RequestBody EntityManagement entityManagement) {
        try {
            return ResponseEntity.ok().body(entityManagementService.modifySaccoEntity(entityManagement));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("/verify/{id}")
    public ResponseEntity<?> VerifySaccoEntity(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok().body(entityManagementService.VerifySaccoEntity(id));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("/temporary/delete/{id}")
    public ResponseEntity<?> temporaryDelete(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok().body(entityManagementService.temporaryDelete(id));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @DeleteMapping("/permanent/delete/{id}")
    public ResponseEntity<?> permanentDelete(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok().body(entityManagementService.permanentDelete(id));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
}
