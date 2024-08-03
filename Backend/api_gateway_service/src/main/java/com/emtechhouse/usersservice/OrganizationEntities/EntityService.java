//package com.emtechhouse.usersservice.OrganizationEntities;
//
//import com.emtechhouse.usersservice.utils.DataNotFoundException;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@Slf4j
//public class EntityService {
//    private final EntityRepo entityRepo;
//
//    public EntityService(EntityRepo entityRepo) {
//        this.entityRepo = entityRepo;
//    }
//
//    public Entitygroup addEntity(Entitygroup entityGroup) {
//        try {
//            return entityRepo.save(entityGroup);
//        } catch (Exception e) {
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }
//
//    public List<Entitygroup> findAllEntitys() {
//        try {
//            return entityRepo.findAll();
//        } catch (Exception e) {
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }
//    public Entitygroup findById(Long id){
//        try{
//            return entityRepo.findById(id).orElseThrow(()-> new DataNotFoundException("Data " + id +"was not found"));
//        } catch (Exception e) {
//            log.info("Catched Error {} "+e);
//            return null;
//        }
//    }
//
//    public Entitygroup updateEntity(Entitygroup entityGroup) {
//        try {
//            return entityRepo.save(entityGroup);
//        } catch (Exception e) {
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }
//
//    public void deleteEntity(Long id) {
//        try {
//            entityRepo.deleteById(id);
//        } catch (Exception e) {
//            log.info("Catched Error {} " + e);
//        }
//    }
//}