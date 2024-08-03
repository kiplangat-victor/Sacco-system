package com.emtechhouse.accounts.Models.Accounts.LienComponent;

import com.emtechhouse.accounts.Exception.ApiRequestException;
import com.emtechhouse.accounts.Utils.DatesCalculator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;

@RequestMapping("lien")
@RestController
@Slf4j
public class LienController {
    @Autowired
    private  LienService lienService;

    @Autowired
    private LienRepository lienRepository;
    @Autowired
    private DatesCalculator datesCalculator;
    @PostMapping("add")
    public ResponseEntity<?> openAccount(@RequestBody Lien lien) {
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(lienService.addLien(lien));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            throw new ApiRequestException("failed");
        }
    }

    @PutMapping("satisfy/lien/by/acid/{acid}")
    public ResponseEntity<?> satisfyLien(@PathVariable("acid") String acid){
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(lienService.satisfyLiens(acid));
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            throw new ApiRequestException("failed");
        }
    }

    @PutMapping("satisfy/lien/by/lien-code/{code}")
    public ResponseEntity<?> satisfyLienByLienCode(@PathVariable("code") String lienCode){
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(lienService.satisfyLienByLienCode(lienCode));
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            throw new ApiRequestException("failed");
        }
    }

    @GetMapping("find/by/source-account/{acid}")
    public ResponseEntity<?> findLiensBySourceAccount(@PathVariable("acid") String acid){
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(lienService.getAllLiensBySourceAccount(acid));
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            throw new ApiRequestException("failed");
        }
    }

    @GetMapping("find/by/lien-code/{code}")
    public ResponseEntity<?> findLienByLienCode(@PathVariable("code") String code){
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(lienService.getLienByLienCode(code));
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            throw new ApiRequestException("failed");
        }
    }

    @PutMapping("verify/lien/by/lien-code/{code}")
    public ResponseEntity<?> verifyLiens(@PathVariable("code") String code){
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(lienService.verifyLien(code));
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            throw new ApiRequestException("failed");
        }
    }

    @PutMapping("modify/lien")
    public ResponseEntity<?> modifyLiens(@RequestBody Lien lien){
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(lienService.modifyLien(lien));
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            throw new ApiRequestException("failed");
        }
    }

    @PutMapping("close/lien/by/lien-code/{code}")
    public ResponseEntity<?> closeLiens(@PathVariable("code") String code){
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(lienService.closeLien(code));
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            throw new ApiRequestException("failed");
        }
    }
    @PutMapping("verify/lien/close/by/lien-code/{code}")
    public ResponseEntity<?> verifyLiensClose(@PathVariable("code") String code){
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(lienService.verifyLienClose(code));
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            throw new ApiRequestException("failed");
        }
    }
    @GetMapping("find/by/destination-account/{acid}")
    public ResponseEntity<?> findLiensByDestinationAccount(@PathVariable("acid") String acid){
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(lienService.getAllLiensByDestinationAccount(acid));
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            throw new ApiRequestException("failed");
        }
    }

    @GetMapping("active/liens")
    public ResponseEntity<?> getAllActiveLiens(){
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(lienService.getAllActiveLiens());
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            throw new ApiRequestException("failed");
        }
    }
    @GetMapping("satisfied/liens")
    public ResponseEntity<?> getAllSatisfiedLiens(){
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(lienService.getAllSatisfiedLiens());
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            throw new ApiRequestException("failed");
        }
    }

    @PutMapping("update/expired/liens")
    public ResponseEntity<?> updateExpiredLiens(){
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(lienService.updateLiensToExpired());
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            throw new ApiRequestException("failed");
        }
    }

    @PatchMapping("update/effective/liens")
    public ResponseEntity<?> updateEffectiveLien(){
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(lienService.updateLiensToActive());
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            throw new ApiRequestException("failed");
        }
    }

    @DeleteMapping("delete/all")
    public ResponseEntity<?> deleteAll(){
        try{
            lienRepository.deleteAll();
            return ResponseEntity.status(HttpStatus.CREATED).body("deleted");
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            throw new ApiRequestException("failed");
        }
    }
}
