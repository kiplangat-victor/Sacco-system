package com.emtechhouse.usersservice.FSIConfigurations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/fsi/headers")
public class FSIConfigurationsController {
    private final FSIConfigurationsService fsiConfigurationsService;

    public FSIConfigurationsController(FSIConfigurationsService fsiConfigurationsService) {
        this.fsiConfigurationsService = fsiConfigurationsService;
    }
    @PostMapping( "/add")
    public ResponseEntity<?> addHeader(@RequestBody FSIConfigurations fsiConfigurations) {
        try {
            return ResponseEntity.ok().body(fsiConfigurationsService.addHeader(fsiConfigurations));
        } catch (Exception e) {
            log.info("Caught Error " + "" + e);
            return null;
        }
    }
    @GetMapping( "/all")
    public ResponseEntity<?> getHeaders() {
        try {
            return ResponseEntity.ok().body(fsiConfigurationsService.getHeaders());
        } catch (Exception e) {
            log.info("Caught Error " + "" + e);
            return null;
        }
    }
    @GetMapping( "/id/{id}")
    public ResponseEntity<?> getHeaderById(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok().body(fsiConfigurationsService.getHeaderById(id));
        } catch (Exception e) {
            log.info("Caught Error " + "" + e);
            return null;
        }
    }
    @GetMapping( "/entity-id/{entityCode}")
    public ResponseEntity<?> getHeaderByEntityId(@PathVariable("entityCode") String entityCode) {
        try {
            return ResponseEntity.ok().body(fsiConfigurationsService.getHeaderByEntityId(entityCode));
        } catch (Exception e) {
            log.info("Caught Error " + "" + e);
            return null;
        }
    }
    @PutMapping( "/update")
    public ResponseEntity<?> updateHeader(@RequestBody FSIConfigurations fsiConfigurations) {
        try {
            return ResponseEntity.ok().body(fsiConfigurationsService.updateHeader(fsiConfigurations));
        } catch (Exception e) {
            log.info("Caught Error " + "" + e);
            return null;
        }
    }
    @DeleteMapping( "/delete/{id]")
    public ResponseEntity<?> deleteHeaderById(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok().body(fsiConfigurationsService.deleteHeaderById(id));
        } catch (Exception e) {
            log.info("Caught Error " + "" + e);
            return null;
        }
    }
    @GetMapping( "/attach-headers")
    public ResponseEntity<?> attachHeaders() {
        try {
            return ResponseEntity.ok().body(fsiConfigurationsService.attachHeaders());
        } catch (Exception e) {
            log.info("Caught Error " + "" + e);
            return null;
        }
    }
    @PutMapping( "/verify/{id}")
    public ResponseEntity<?> verify(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok().body(fsiConfigurationsService.verify(id));
        } catch (Exception e) {
            log.info("Caught Error " + "" + e);
            return null;
        }
    }
}
