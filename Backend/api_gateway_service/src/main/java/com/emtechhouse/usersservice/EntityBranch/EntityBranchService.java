package com.emtechhouse.usersservice.EntityBranch;

import com.emtechhouse.usersservice.SaccoEntity.SaccoEntity;
import com.emtechhouse.usersservice.SaccoEntity.SaccoEntityRepository;
import com.emtechhouse.usersservice.utils.EntityResponse;
import com.emtechhouse.usersservice.utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.usersservice.utils.HttpInterceptor.UserRequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class EntityBranchService {
    private final EntityBranchRepository entityBranchRepository;
    private final SaccoEntityRepository saccoEntityRepository;

    public EntityBranchService(EntityBranchRepository entityBranchRepository, SaccoEntityRepository saccoEntityRepository) {
        this.entityBranchRepository = entityBranchRepository;
        this.saccoEntityRepository = saccoEntityRepository;
    }

    @PostMapping("/add")
    public EntityResponse<?> addEntityBranch(@RequestBody EntityBranch entityBranch, @PathVariable Long fk_id) {
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
                Optional<SaccoEntity> checkSaccoId = saccoEntityRepository.findById(fk_id);
                Optional<EntityBranch> checkBranchCode = entityBranchRepository.findBranchByBranchCode(entityBranch.getBranchCode());
                Optional<EntityBranch> checkPhone = entityBranchRepository.findByPhoneNumberAndDeletedFlag(entityBranch.getPhoneNumber(), 'N');
                Optional<EntityBranch> checkEmail = entityBranchRepository.findByEmailAndDeletedFlag(entityBranch.getEmail(), 'N');
                Optional<EntityBranch> checkBranch = entityBranchRepository.findByBranchDescriptionAndLocation(entityBranch.getBranchDescription(), entityBranch.getLocation());

                if (!checkSaccoId.isPresent()) {
                    response.setMessage("Sacco Entity ID " + entityBranch.getSaccoEntityCode() + " Not Allowed: !! Check with Manager for new Registration/Approval: !!");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                } else if (checkBranchCode.isPresent()) {
                    response.setMessage("Sacco Entity Branch  with Code " + entityBranch.getBranchCode() + " Already Registered: !!");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                } else if (checkPhone.isPresent()) {
                    response.setMessage("Sacco Entity Branch with Phone/Mobile " + entityBranch.getPhoneNumber() + " Already Registered: !!");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                } else if (checkEmail.isPresent()) {
                    response.setMessage("Sacco Entity Branch with Email Address " + entityBranch.getEmail() + " Already Registered: !!");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                } else if (checkBranch.isPresent()) {
                    response.setMessage("Sacco Entity Branch with Description " + entityBranch.getBranchCode() + " Located At " + entityBranch.getLocation() + " already Registered");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                } else {
                    if (entityBranch.getPhoneNumber().length() > 12 || entityBranch.getPhoneNumber().length() < 10) {
                        response.setMessage("Sacco Entity Branch Phone/Mobile Number " + entityBranch.getPhoneNumber() + " is Invalid: !! Check the Correct NO. FORMAT ALLOWED!");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    } else {
                        SaccoEntity saccoEntity = checkSaccoId.get();
                        entityBranch.setSaccoEntity(saccoEntity);
                        entityBranch.setPostedBy(UserRequestContext.getCurrentUser());
                        entityBranch.setEntityId(EntityRequestContext.getCurrentEntityId());
                        entityBranch.setPostedFlag('Y');
                        entityBranch.setPostedTime(new Date());
                        EntityBranch newBranch = entityBranchRepository.save(entityBranch);
                        response.setMessage("Sacco Entity Branch with Code " + newBranch.getBranchCode() + " Created Successfully at " + newBranch.getPostedTime());
                        response.setStatusCode(HttpStatus.CREATED.value());
                        response.setEntity(newBranch);
                    }
                }
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<?> getAllEntityBranches() {
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
                List<EntityBranch> entityBranchList = entityBranchRepository.findAll();
                if (entityBranchList.size() > 0) {
                    response.setMessage("Welcome Manager: " + user + ", You have " + entityBranchList.size() + " Registered Sacco Entity Branches");
                    response.setStatusCode(HttpStatus.FOUND.value());
                    response.setEntity(entityBranchList);
                } else {
                    response.setMessage("Welcome Manager: " + user + ", You have " + entityBranchList.size() + " Registered Sacco Entity Branches");
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setEntity(entityBranchList.size());
                }
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<?> findAllEntityBranches() {
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
                List<EntityBranch> entityBranchList = entityBranchRepository.findAllByDeletedFlagOrderByIdDesc('N');
                if (entityBranchList.size() > 0) {
                    response.setMessage("Welcome Manager: " + user + ", You have " + entityBranchList.size() + " Registered Sacco Entity Branches");
                    response.setStatusCode(HttpStatus.FOUND.value());
                    response.setEntity(entityBranchList);
                } else {
                    response.setMessage("Welcome Manager: " + user + ", You have " + entityBranchList.size() + " Registered Sacco Entity Branches");
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setEntity(entityBranchList.size());
                }
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<?> findAllActiveEntityBranches() {
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
                List<EntityBranch> entityBranchList = entityBranchRepository.findAllByDeletedFlagAndVerifiedFlagOrderByIdDesc('N', 'Y');
                if (entityBranchList.size() > 0) {
                    response.setMessage("Welcome Manager: " + user + ", You have " + entityBranchList.size() + " Active Sacco Entity Branches");
                    response.setStatusCode(HttpStatus.FOUND.value());
                    response.setEntity(entityBranchList);
                } else {
                    response.setMessage("Welcome Manager: " + user + ", You have " + entityBranchList.size() + " Active Sacco Entity Branches");
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setEntity(entityBranchList.size());
                }
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<?> findAllEntityBranchesByEntity() {
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
                List<EntityBranch> entityBranchList = entityBranchRepository.findAllByEntityIdAndDeletedFlagOrderByIdDesc(entityId, 'N');
                if (entityBranchList.size() > 0) {
                    response.setMessage("Welcome : " + user + ", You have " + entityBranchList.size() + " Registered Sacco Entity Branches");
                    response.setStatusCode(HttpStatus.FOUND.value());
                    response.setEntity(entityBranchList);
                } else {
                    response.setMessage("Welcome : " + user + ", You have " + entityBranchList.size() + " Registered Sacco Entity Branches");
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setEntity(entityBranchList.size());
                }
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<?> findAllActiveEntityBranchesByEntity() {
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
                List<EntityBranch> entityBranchList = entityBranchRepository.findAllByEntityIdAndDeletedFlagAndVerifiedFlagOrderByIdDesc(entityId, 'N', 'Y');
                if (entityBranchList.size() > 0) {
                    response.setMessage("Welcome : " + user + ", You have " + entityBranchList.size() + " Active Sacco Entity Branches");
                    response.setStatusCode(HttpStatus.FOUND.value());
                    response.setEntity(entityBranchList);
                } else {
                    response.setMessage("Welcome : " + user + ", You have " + entityBranchList.size() + " Active Sacco Entity Branches");
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setEntity(entityBranchList.size());
                }
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<?> findEntityBranchById(Long id) {
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
                Optional<EntityBranch> entityBranch = entityBranchRepository.findEntityBranchById(id);
                if (entityBranch.isPresent()) {
                    response.setMessage("Welcome : " + user + ", Entity With Name " + entityBranch.get().getBranchDescription() + " Was Found.");
                    response.setStatusCode(HttpStatus.FOUND.value());
                    response.setEntity(entityBranch);
                } else {
                    response.setMessage("Welcome : " + user + ", Entity Not Found: !!");
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                }
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<?> findEntityBranchByCode(String branchCode) {
        try {
            EntityResponse response = new EntityResponse();
            String user = UserRequestContext.getCurrentUser();
            String entityId = EntityRequestContext.getCurrentEntityId();
            if (user.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity(UserRequestContext.getCurrentUser());
            } else if (entityId.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity(EntityRequestContext.getCurrentEntityId());
            } else {
                Optional<EntityBranch> entityBranch = entityBranchRepository.findBranchByBranchCode(branchCode);
                if (entityBranch.isPresent()) {
                    response.setMessage("Welcome Manager: " + UserRequestContext.getCurrentUser() + ", Sacco Entity Branch " + entityBranch.get().getEntityId() + " Was Recorded on " + entityBranch.get().getPostedTime());
                    response.setStatusCode(HttpStatus.FOUND.value());
                    response.setEntity(entityBranch);
                } else {
                    response.setMessage("Welcome Manager: " + UserRequestContext.getCurrentUser() + ", Sacco Entity Branch " + entityBranch + " Available for Registration: !!");
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

    public EntityResponse<?> findEntityBranchByCodeByEntity(String branchCode) {
        try {
            EntityResponse response = new EntityResponse();
            String user = UserRequestContext.getCurrentUser();
            String entityId = EntityRequestContext.getCurrentEntityId();
            if (user.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity(UserRequestContext.getCurrentUser());
            } else if (entityId.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity(EntityRequestContext.getCurrentEntityId());
            } else {
                Optional<EntityBranch> entityBranch = entityBranchRepository.findBranchByEntityIdAndBranchCode(entityId, branchCode);
                if (entityBranch.isPresent()) {
                    response.setMessage("Welcome : " + UserRequestContext.getCurrentUser() + ", Sacco Entity Branch " + entityBranch.get().getEntityId() + " Was Recorded on " + entityBranch.get().getPostedTime());
                    response.setStatusCode(HttpStatus.FOUND.value());
                    response.setEntity(entityBranch);
                } else {
                    response.setMessage("Welcome : " + UserRequestContext.getCurrentUser() + ", Sacco Entity Branch " + entityBranch + " Available for Registration: !!");
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

    public EntityResponse<?> modifyEntityBranch(EntityBranch entityBranch) {
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
                Optional<EntityBranch> checkBranchId = entityBranchRepository.findEntityBranchById(entityBranch.getId());
                if (!checkBranchId.isPresent()) {
                    response.setMessage("Entity Branch With ID  " + entityBranch.getEntityId() + " Not Registered with our Databases: !!");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                } else {
                    if (entityBranch.getPhoneNumber().length() > 12 || entityBranch.getPhoneNumber().length() < 10) {
                        response.setMessage("Sacco Entity Branch Phone/Mobile Number " + entityBranch.getPhoneNumber() + " is Invalid: !! Check the Correct NO. FORMAT ALLOWED!");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    } else {
                        entityBranch.setPostedBy(checkBranchId.get().getPostedBy());
                        entityBranch.setPostedFlag(checkBranchId.get().getPostedFlag());
                        entityBranch.setPostedTime(checkBranchId.get().getPostedTime());
                        entityBranch.setModifiedBy(user);
                        entityBranch.setModifiedTime(new Date());
                        entityBranch.setModifiedFlag('Y');
                        EntityBranch modifyBranch = entityBranchRepository.save(entityBranch);
                        response.setMessage("Sacco Entity Branch with Code " + modifyBranch.getBranchCode() + " Modified Successfully at " + modifyBranch.getModifiedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(modifyBranch);
                    }
                }
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<?> VerifyEntityBranch(Long id) {
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
                Optional<EntityBranch> checkBranchId = entityBranchRepository.findEntityBranchById(id);
                if (!checkBranchId.isPresent()) {
                    response.setMessage("Entity Branch With ID  " + id + " Not Registered with our Databases: !!");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                } else {
                    EntityBranch entityBranch = checkBranchId.get();
                    entityBranch.setVerifiedBy(user);
                    entityBranch.setVerifiedTime(new Date());
                    entityBranch.setVerifiedFlag('Y');
                    EntityBranch verifyBranch = entityBranchRepository.save(entityBranch);
                    response.setMessage("Sacco Entity Branch with Code " + verifyBranch.getBranchCode() + " Verified Successfully at " + verifyBranch.getVerifiedTime());
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
