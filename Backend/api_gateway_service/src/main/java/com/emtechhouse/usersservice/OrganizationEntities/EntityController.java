//package com.emtechhouse.usersservice.OrganizationEntities;
//
//import com.emtechhouse.usersservice.utils.EntityResponse;
//import com.emtechhouse.usersservice.utils.HttpInterceptor.EntityRequestContext;
//import com.emtechhouse.usersservice.utils.HttpInterceptor.UserRequestContext;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Date;
//import java.util.List;
//import java.util.Optional;
//
//
//@RestController
//@RequestMapping("/api/v1/entity")
//@Slf4j
//public class EntityController {
//    private final EntityRepo entityRepo;
//    private final EntityService entityService;
//
//    public EntityController(EntityRepo entityRepo, EntityService entityService) {
//        this.entityRepo = entityRepo;
//        this.entityService = entityService;
//    }
//
//    @PostMapping("/add")
//    public ResponseEntity<?> addEntity(@RequestBody Entitygroup entityGroup) {
//        try {
//            if (UserRequestContext.getCurrentUser().isEmpty()) {
//                EntityResponse response = new EntityResponse();
//                response.setMessage("User Name not present in the Request Header");
//                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                response.setEntity("");
//                return new ResponseEntity<>(response, HttpStatus.OK);
//            } else {
//                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
//                    EntityResponse response = new EntityResponse();
//                    response.setMessage("Entity not present in the Request Header");
//                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                    response.setEntity("");
//                    return new ResponseEntity<>(response, HttpStatus.OK);
//                } else {
////                    Check if code exist
//                    Optional<Entitygroup> entity1 = entityRepo.findByEntityCodeAndDeletedFlag(entityGroup.getEntityCode(), 'N');
//                    if (entity1.isPresent()){
//                        EntityResponse response = new EntityResponse();
//                        response.setMessage("Code Exists");
//                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                        response.setEntity("");
//                        return new ResponseEntity<>(response, HttpStatus.OK);
//                    }else{
//                        entityGroup.setPostedBy(UserRequestContext.getCurrentUser());
//                        entityGroup.setPostedFlag('Y');
//                        entityGroup.setPostedTime(new Date());
//
//                        Entitygroup newEntityGroup = entityService.addEntity(entityGroup);
//                        EntityResponse response = new EntityResponse();
//                        response.setMessage(HttpStatus.CREATED.getReasonPhrase());
//                        response.setStatusCode(HttpStatus.CREATED.value());
//                        response.setEntity(newEntityGroup);
//                        return new ResponseEntity<>(response, HttpStatus.OK);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }
//
//    @GetMapping("/all")
//    public ResponseEntity<?> getAllEntitys() {
//        try {
//            if (UserRequestContext.getCurrentUser().isEmpty()) {
//                EntityResponse response = new EntityResponse();
//                response.setMessage("User Name not present in the Request Header");
//                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                response.setEntity("");
//                return new ResponseEntity<>(response, HttpStatus.OK);
//            } else {
//                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
//                    EntityResponse response = new EntityResponse();
//                    response.setMessage("Entity not present in the Request Header");
//                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                    response.setEntity("");
//                    return new ResponseEntity<>(response, HttpStatus.OK);
//                } else {
//                    List<Entitygroup> entityGroup = entityRepo.findByDeletedFlag('N');
//                    EntityResponse response = new EntityResponse();
//                    response.setMessage(HttpStatus.OK.getReasonPhrase());
//                    response.setStatusCode(HttpStatus.OK.value());
//                    response.setEntity(entityGroup);
//                    return new ResponseEntity<>(response, HttpStatus.OK);
//                }
//            }
//        } catch (Exception e) {
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }
//
//    @GetMapping("/find/{id}")
//    public ResponseEntity<?> getEntityById(@PathVariable("id") Long id) {
//        try {
//            if (UserRequestContext.getCurrentUser().isEmpty()) {
//                EntityResponse response = new EntityResponse();
//                response.setMessage("User Name not present in the Request Header");
//                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                response.setEntity("");
//                return new ResponseEntity<>(response, HttpStatus.OK);
//            } else {
//                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
//                    EntityResponse response = new EntityResponse();
//                    response.setMessage("Entity not present in the Request Header");
//                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                    response.setEntity("");
//                    return new ResponseEntity<>(response, HttpStatus.OK);
//                } else {
//                    Entitygroup entityGroup = entityService.findById(id);
//                    EntityResponse response = new EntityResponse();
//                    response.setMessage(HttpStatus.OK.getReasonPhrase());
//                    response.setStatusCode(HttpStatus.OK.value());
//                    response.setEntity(entityGroup);
//                    return new ResponseEntity<>(response, HttpStatus.OK);
//                }
//            }
//        } catch (Exception e) {
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }
//
//    @PutMapping("/modify")
//    public ResponseEntity<?> updateEntity(@RequestBody Entitygroup entityGroup) {
//        try {
//            if (UserRequestContext.getCurrentUser().isEmpty()) {
//                EntityResponse response = new EntityResponse();
//                response.setMessage("User Name not present in the Request Header");
//                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                response.setEntity("");
//                return new ResponseEntity<>(response, HttpStatus.OK);
//            } else {
//                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
//                    EntityResponse response = new EntityResponse();
//                    response.setMessage("Entity not present in the Request Header");
//                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                    response.setEntity("");
//                    return new ResponseEntity<>(response, HttpStatus.OK);
//                } else {
//                    entityGroup.setModifiedBy(UserRequestContext.getCurrentUser());
//                    Optional<Entitygroup> entity1 = entityRepo.findById(entityGroup.getId());
//                    if (entity1.isPresent()) {
//                        entityGroup.setPostedTime(entity1.get().getPostedTime());
//                        entityGroup.setPostedFlag('Y');
//                        entityGroup.setPostedBy(entity1.get().getPostedBy());
//                        entityGroup.setModifiedFlag('Y');
//                        entityGroup.setVerifiedFlag('N');
//                        entityGroup.setModifiedTime(new Date());
//                        entityGroup.setModifiedBy(entityGroup.getModifiedBy());
//                        entityService.updateEntity(entityGroup);
//                        EntityResponse response = new EntityResponse();
//                        response.setMessage(HttpStatus.OK.getReasonPhrase());
//                        response.setStatusCode(HttpStatus.OK.value());
//                        response.setEntity(entityGroup);
//                        return new ResponseEntity<>(response, HttpStatus.OK);
//                    } else {
//                        EntityResponse response = new EntityResponse();
//                        response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
//                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
//                        response.setEntity("");
//                        return new ResponseEntity<>(response, HttpStatus.OK);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }
//
//    @PutMapping("/verify/{id}")
//    public ResponseEntity<?> verify(@PathVariable String id) {
//        try {
//            if (UserRequestContext.getCurrentUser().isEmpty()) {
//                EntityResponse response = new EntityResponse();
//                response.setMessage("User Name not present in the Request Header");
//                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                response.setEntity("");
//                return new ResponseEntity<>(response, HttpStatus.OK);
//            } else {
//                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
//                    EntityResponse response = new EntityResponse();
//                    response.setMessage("Entity not present in the Request Header");
//                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                    response.setEntity("");
//                    return new ResponseEntity<>(response, HttpStatus.OK);
//                } else {
//                    Optional<Entitygroup> entity1 = entityRepo.findById(Long.parseLong(id));
//                    if (entity1.isPresent()) {
//                        Entitygroup entityGroup = entity1.get();
//                        //                    Check Maker Checker
//                        if (entityGroup.getPostedBy().equalsIgnoreCase(UserRequestContext.getCurrentUser())){
//                            EntityResponse response = new EntityResponse();
//                            response.setMessage("You Can Not Verify What you initiated");
//                            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
//                            response.setEntity("");
//                            return new ResponseEntity<>(response, HttpStatus.OK);
//                        }else{
//                            entityGroup.setVerifiedFlag('Y');
//                            entityGroup.setVerifiedTime(new Date());
//                            entityGroup.setVerifiedBy(UserRequestContext.getCurrentUser());
//                            entityRepo.save(entityGroup);
//                            EntityResponse response = new EntityResponse();
//                            response.setMessage(HttpStatus.OK.getReasonPhrase());
//                            response.setStatusCode(HttpStatus.OK.value());
//                            response.setEntity(entityGroup);
//                            return new ResponseEntity<>(response, HttpStatus.OK);
//                        }
//                    } else {
//                        EntityResponse response = new EntityResponse();
//                        response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
//                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
//                        response.setEntity("");
//                        return new ResponseEntity<>(response, HttpStatus.OK);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }
//
//    @DeleteMapping("/delete/{id}")
//    public ResponseEntity<?> deleteEntity(@PathVariable String id) {
//        try {
//            if (UserRequestContext.getCurrentUser().isEmpty()) {
//                EntityResponse response = new EntityResponse();
//                response.setMessage("User Name not present in the Request Header");
//                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                response.setEntity("");
//                return new ResponseEntity<>(response, HttpStatus.OK);
//            } else {
//                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
//                    EntityResponse response = new EntityResponse();
//                    response.setMessage("Entity not present in the Request Header");
//                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                    response.setEntity("");
//                    return new ResponseEntity<>(response, HttpStatus.OK);
//                } else {
//                    Optional<Entitygroup> entity1 = entityRepo.findById(Long.parseLong(id));
//                    if (entity1.isPresent()) {
//                        Entitygroup entityGroup = entity1.get();
//                        entityGroup.setDeletedFlag('Y');
//                        entityGroup.setDeletedTime(new Date());
//                        entityGroup.setDeletedBy(UserRequestContext.getCurrentUser());
//                        entityRepo.save(entityGroup);
//                        EntityResponse response = new EntityResponse();
//                        response.setMessage(HttpStatus.OK.getReasonPhrase());
//                        response.setStatusCode(HttpStatus.OK.value());
//                        response.setEntity(entityGroup);
//                        return new ResponseEntity<>(response, HttpStatus.OK);
//                    } else {
//                        EntityResponse response = new EntityResponse();
//                        response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
//                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
//                        response.setEntity("");
//                        return new ResponseEntity<>(response, HttpStatus.OK);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }
//}