package com.emtechhouse.System.Entity;

import com.emtechhouse.System.Utils.EntityResponse;
import com.emtechhouse.System.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.System.Utils.HttpInterceptor.UserRequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class EntityManagementService {
    private final EntityManagementRepository entityManagementRepository;

    public EntityManagementService(EntityManagementRepository entityManagementRepository) {
        this.entityManagementRepository = entityManagementRepository;
    }

    public EntityResponse<?> addSaccoEntity(EntityManagement entityManagement) {
        try {
            EntityResponse response = new EntityResponse();
            String user = UserRequestContext.getCurrentUser();
            String entityId = EntityRequestContext.getCurrentEntityId();
            if (user.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity(user);
            } else if (entityId.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity(entityId);
            } else {
                Optional<EntityManagement> searchEntityId = entityManagementRepository.findEntityManagementByEntityIdAndDeletedFlag(entityManagement.getEntityId(), 'N');
                Optional<EntityManagement> searchEntityName = entityManagementRepository.findEntityManagementByEntityNameAndDeletedFlag(entityManagement.getEntityName(), 'N');
                if (searchEntityId.isPresent()) {
                    response.setMessage("Sacco with Entity ID " + entityManagement.getEntityId() + " was mapped to " + searchEntityId.get().getEntityName() + " on " + searchEntityId.get().getPostedOn());
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                } else if (searchEntityName.isPresent()) {
                    response.setMessage("Sacco with Entity Name " + searchEntityName.get().getEntityName() + " already registered on " + searchEntityName.get().getPostedOn());
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                } else {
                    entityManagement.setPostedBy(UserRequestContext.getCurrentUser());
                    entityManagement.setPostedFlag('Y');
                    entityManagement.setPostedOn(LocalDate.now());
                    entityManagement.setEntityStatus("PENDING");
                    EntityManagement addSaccoEntity = entityManagementRepository.save(entityManagement);
                    response.setMessage("Sacco with Entity ID  " + entityManagement.getEntityId() + " Created Successfully at " + LocalDate.now());
                    response.setStatusCode(HttpStatus.CREATED.value());
                    response.setEntity(addSaccoEntity);
                }
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<?> getAllSaccoEntities() {
        try {
            EntityResponse response = new EntityResponse();
            String user = UserRequestContext.getCurrentUser();
            String entityId = EntityRequestContext.getCurrentEntityId();
            if (user.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity(user);
            } else if (entityId.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity(entityId);
            } else {
                List<EntityManagement> entityManagementList = entityManagementRepository.findAll();
                if (entityManagementList.size() > 0) {
                    response.setMessage("Welcome Manager: " + user + ", You have " + entityManagementList.size() + " Created Sacco's.");
                    response.setStatusCode(HttpStatus.FOUND.value());
                    response.setEntity(entityManagementList);
                } else {
                    response.setMessage("Welcome Manager: " + user + ", You have " + entityManagementList.size() + " Created Sacco's.");
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setEntity(entityManagementList.size());
                }
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<?> findAllSaccoEntities() {
        try {
            EntityResponse response = new EntityResponse();
            String user = UserRequestContext.getCurrentUser();
            String entityId = EntityRequestContext.getCurrentEntityId();
            if (user.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity(user);
            } else if (entityId.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity(entityId);
            } else {
                List<EntityManagement> entityManagementList = entityManagementRepository.findAllByDeletedFlagOrderByIdDesc('N');
                if (entityManagementList.size() > 0) {
                    response.setMessage("Welcome Manager: " + user + ", You have " + entityManagementList.size() + " Registered Sacco's.");
                    response.setStatusCode(HttpStatus.FOUND.value());
                    response.setEntity(entityManagementList);
                } else {
                    response.setMessage("Welcome Manager: " + user + ", You have " + entityManagementList.size() + " Registered Sacco's.");
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setEntity(entityManagementList.size());
                }
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<?> findAllActiveSaccoEntities() {
        try {
            EntityResponse response = new EntityResponse();
            String user = UserRequestContext.getCurrentUser();
            String entityId = EntityRequestContext.getCurrentEntityId();
            if (user.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity(user);
            } else if (entityId.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity(entityId);
            } else {
                List<EntityManagement> entityManagementList = entityManagementRepository.findAllByDeletedFlagAndVerifiedFlagOrderByIdDesc('N', 'Y');
                if (entityManagementList.size() > 0) {
                    response.setMessage("Welcome Manager: " + user + ", You have " + entityManagementList.size() + " Active Sacco's.");
                    response.setStatusCode(HttpStatus.FOUND.value());
                    response.setEntity(entityManagementList);
                } else {
                    response.setMessage("Welcome Manager: " + user + ", You have " + entityManagementList.size() + " Active Sacco's.");
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setEntity(entityManagementList.size());
                }
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<?> modifySaccoEntity(EntityManagement entityManagement) {
        try {
            EntityResponse response = new EntityResponse();
            String user = UserRequestContext.getCurrentUser();
            String entityId = EntityRequestContext.getCurrentEntityId();
            if (user.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity(user);
            } else if (entityId.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity(entityId);
            } else {
                Optional<EntityManagement> checkSaccoId = entityManagementRepository.findEntityManagementById(entityManagement.getId());
                if (!checkSaccoId.isPresent()) {
                    response.setMessage("Sacco with Entity ID  " + entityManagement.getEntityId() + " Not Registered with our Databases: !!");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                } else {
                    entityManagement.setPostedBy(checkSaccoId.get().getPostedBy());
                    entityManagement.setPostedFlag(checkSaccoId.get().getPostedFlag());
                    entityManagement.setPostedOn(checkSaccoId.get().getPostedOn());
                    entityManagement.setEntityStatus(checkSaccoId.get().getEntityStatus());
                    entityManagement.setModifiedBy(UserRequestContext.getCurrentUser());
                    entityManagement.setModifiedOn(LocalDate.now());
                    entityManagement.setModifiedFlag('Y');
                    EntityManagement modifySaccoEntity = entityManagementRepository.save(entityManagement);
                    response.setMessage("Sacco with Entity ID " + entityManagement.getEntityId() + " Modified Successfully at " + LocalDate.now());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(modifySaccoEntity);
                }
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<?> VerifySaccoEntity(Long id) {
        try {
            EntityResponse response = new EntityResponse();
            String user = UserRequestContext.getCurrentUser();
            String entityId = EntityRequestContext.getCurrentEntityId();
            if (user.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity(user);
            } else if (entityId.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity(entityId);
            } else {
                Optional<EntityManagement> checkSaccoId = entityManagementRepository.findEntityManagementById(id);
                EntityManagement entityManagement = checkSaccoId.get();
                if (!checkSaccoId.isPresent()) {
                    response.setMessage("Sacco Entity Not Registered with our Databases: !!");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                } else {
                    entityManagement.setVerifiedBy(user);
                    entityManagement.setVerifiedOn(LocalDate.now());
                    entityManagement.setVerifiedFlag('Y');
                    entityManagement.setEntityStatus("ACTIVE");
                    entityManagementRepository.save(entityManagement);
                    response.setMessage("Sacco with Entity ID " + entityManagement.getEntityId() + " Verified Successfully at " + LocalDate.now());
                    response.setStatusCode(HttpStatus.OK.value());
                }
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<?> temporaryDelete(Long id) {
        try {
            EntityResponse response = new EntityResponse();
            String user = UserRequestContext.getCurrentUser();
            String entityId = EntityRequestContext.getCurrentEntityId();
            if (user.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity(user);
            } else if (entityId.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity(entityId);
            } else {
                Optional<EntityManagement> checkSaccoId = entityManagementRepository.findEntityManagementById(id);
                EntityManagement entityManagement = checkSaccoId.get();
                if (!checkSaccoId.isPresent()) {
                    response.setMessage("Sacco Entity Not Registered with our Databases: !!");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                } else {
                    entityManagement.setDeletedBy(user);
                    entityManagement.setDeletedOn(LocalDate.now());
                    entityManagement.setDeletedFlag('Y');
                    entityManagement.setEntityStatus("DORMANT");
                    entityManagementRepository.save(entityManagement);
                    response.setMessage("Sacco with Entity ID " + entityManagement.getEntityId() + " Deleted Successfully at " + LocalDate.now());
                    response.setStatusCode(HttpStatus.OK.value());
                }
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<?> permanentDelete(Long id) {
        try {
            EntityResponse response = new EntityResponse();
            String user = UserRequestContext.getCurrentUser();
            String entityId = EntityRequestContext.getCurrentEntityId();
            if (user.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity(user);
            } else if (entityId.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity(entityId);
            } else {
                Optional<EntityManagement> checkSaccoId = entityManagementRepository.findEntityManagementById(id);
                EntityManagement entityManagement = checkSaccoId.get();
                if (!checkSaccoId.isPresent()) {
                    response.setMessage("Sacco Entity Not Registered with our Databases: !!");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                } else {
                    entityManagementRepository.deleteById(id);
                    response.setMessage("Sacco with Entity ID " + entityManagement.getEntityId() + " Deleted Successfully at " + LocalDate.now());
                    response.setStatusCode(HttpStatus.OK.value());
                }
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
}
