package emt.sacco.middleware.SecurityImpl.SaccoEntity;

import emt.sacco.middleware.Utils.EntityResponse;
import emt.sacco.middleware.Utils.EntityRequestContext;
import emt.sacco.middleware.Utils.Config.UserRequestContext;
import emt.sacco.middleware.Utils.code_generator.CodeGeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class SaccoEntityService {
    private final SaccoEntityRepository saccoEntityRepository;
    @Autowired
    private CodeGeneratorService codeGeneratorService;

    public SaccoEntityService(SaccoEntityRepository saccoEntityRepository) {
        this.saccoEntityRepository = saccoEntityRepository;
    }

    public EntityResponse<?> addSaccoEntity(SSaccoEntity SSaccoEntity) {
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
                System.out.println("data 1 ---------------");
                SSaccoEntity.setEntityId(codeGeneratorService.generateCode());
                SSaccoEntity.setEntityStatus("Pending");

                System.out.println("data ---------------");
                SSaccoEntity.setPostedBy(user);
                SSaccoEntity.setPostedFlag('Y');
                SSaccoEntity.setPostedOn(LocalDate.now());
                SSaccoEntity.setEntityStatus("PENDING");
                SSaccoEntity addSSaccoEntity = saccoEntityRepository.save(SSaccoEntity);
                response.setMessage("Sacco with Entity ID  " + addSSaccoEntity.getEntityId() + " Created Successfully at " + addSSaccoEntity.getPostedOn());
                response.setStatusCode(HttpStatus.CREATED.value());
                response.setEntity(addSSaccoEntity);
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
                List<SSaccoEntity> entityManagementList = saccoEntityRepository.findAll();
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
                List<SaccoEntityRepository.Saccomin> entityManagementList = saccoEntityRepository.findMinDetails('N');
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
                List<SSaccoEntity> entityManagementList = saccoEntityRepository.findByDeletedFlagAndVerifiedFlag('N', 'Y');
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

    public EntityResponse<?> findSaccoEntityById(Long id) {
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
                Optional<SSaccoEntity> saccoEntity = saccoEntityRepository.findById(id);
                if (saccoEntity.isPresent()) {
                    response.setMessage("Welcome Manager: " + user + ", Entity With Name " + saccoEntity.get().getEntityName() + " Was Found.");
                    response.setStatusCode(HttpStatus.FOUND.value());
                    response.setEntity(saccoEntity);
                } else {
                    response.setMessage("Welcome Manager: " + user + ", Entity Not Found: !!");
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                }
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<?> findBySaccoEntityId(String saccoentity) {
        try {
            EntityResponse response = new EntityResponse();
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity(UserRequestContext.getCurrentUser());
            } else if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity(EntityRequestContext.getCurrentEntityId());
            } else {
                Optional<SSaccoEntity> optionalEntityManagement = saccoEntityRepository.findByEntityIdAndDeletedFlag(saccoentity,'N');
                if (optionalEntityManagement.isPresent()) {
                    response.setMessage("Welcome Manager: " + UserRequestContext.getCurrentUser() + ", Sacco with Entity ID " + optionalEntityManagement.get().getEntityId() + " Was Recorded on " + optionalEntityManagement.get().getPostedOn());
                    response.setStatusCode(HttpStatus.FOUND.value());
                    response.setEntity(optionalEntityManagement);
                } else {
                    response.setMessage("Welcome Manager: " + UserRequestContext.getCurrentUser() + ", Sacco with Entity ID " + saccoentity + " Available for Registration: !!");
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setEntity("");
                }

            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<?> modifySaccoEntity(SSaccoEntity SSaccoEntity) {
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
                Optional<SSaccoEntity> checkSaccoId = saccoEntityRepository.findById(SSaccoEntity.getId());
                if (!checkSaccoId.isPresent()) {
                    response.setMessage("Sacco with Entity ID  " + SSaccoEntity.getEntityId() + " Not Registered with our Databases: !!");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                } else {
                    SSaccoEntity.setPostedBy(checkSaccoId.get().getPostedBy());
                    SSaccoEntity.setPostedFlag(checkSaccoId.get().getPostedFlag());
                    SSaccoEntity.setPostedOn(checkSaccoId.get().getPostedOn());
                    SSaccoEntity.setEntityStatus(checkSaccoId.get().getEntityStatus());
                    SSaccoEntity.setModifiedBy(UserRequestContext.getCurrentUser());
                    SSaccoEntity.setModifiedOn(LocalDate.now());
                    SSaccoEntity.setModifiedFlag('Y');
                    SSaccoEntity modifySSaccoEntity = saccoEntityRepository.save(SSaccoEntity);
                    response.setMessage("Sacco with Entity ID " + SSaccoEntity.getEntityId() + " Modified Successfully at " + LocalDate.now());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(modifySSaccoEntity);
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
                Optional<SSaccoEntity> checkSaccoId = saccoEntityRepository.findById(id);
                SSaccoEntity entityManagement = checkSaccoId.get();
                if (!checkSaccoId.isPresent()) {
                    response.setMessage("Sacco Entity Not Registered with our Databases: !!");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                } else {
                    entityManagement.setVerifiedBy(user);
                    entityManagement.setVerifiedOn(LocalDate.now());
                    entityManagement.setVerifiedFlag('Y');
                    entityManagement.setEntityStatus("ACTIVE");
                    saccoEntityRepository.save(entityManagement);
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
                Optional<SSaccoEntity> checkSaccoId = saccoEntityRepository.findById(id);
                SSaccoEntity entityManagement = checkSaccoId.get();
                if (!checkSaccoId.isPresent()) {
                    response.setMessage("Sacco Entity Not Registered with our Databases: !!");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                } else {
                    entityManagement.setDeletedBy(user);
                    entityManagement.setDeletedOn(LocalDate.now());
                    entityManagement.setDeletedFlag('Y');
                    entityManagement.setEntityStatus("DORMANT");
                    saccoEntityRepository.save(entityManagement);
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
                Optional<SSaccoEntity> checkSaccoId = saccoEntityRepository.findById(id);
                SSaccoEntity entityManagement = checkSaccoId.get();
                if (!checkSaccoId.isPresent()) {
                    response.setMessage("Sacco Entity Not Registered with our Databases: !!");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                } else {
                    saccoEntityRepository.deleteById(id);
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

