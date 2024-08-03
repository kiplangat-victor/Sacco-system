package emt.sacco.middleware.SecurityImpl.EntityBranch;

import emt.sacco.middleware.Utils.EntityResponse;
import emt.sacco.middleware.Utils.EntityRequestContext;
import emt.sacco.middleware.Utils.Config.UserRequestContext;

import emt.sacco.middleware.SecurityImpl.SaccoEntity.SSaccoEntity;
import emt.sacco.middleware.SecurityImpl.SaccoEntity.SaccoEntityRepository;
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
    public EntityResponse<?> addEntityBranch(@RequestBody SEntityBranch SEntityBranch, @PathVariable Long fk_id) {
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
                Optional<SSaccoEntity> checkSaccoId = saccoEntityRepository.findById(fk_id);
                Optional<SEntityBranch> checkBranchCode = entityBranchRepository.findBranchByBranchCode(SEntityBranch.getBranchCode());
                Optional<SEntityBranch> checkPhone = entityBranchRepository.findByPhoneNumberAndDeletedFlag(SEntityBranch.getPhoneNumber(), 'N');
                Optional<SEntityBranch> checkEmail = entityBranchRepository.findByEmailAndDeletedFlag(SEntityBranch.getEmail(), 'N');
                Optional<SEntityBranch> checkBranch = entityBranchRepository.findByBranchDescriptionAndLocation(SEntityBranch.getBranchDescription(), SEntityBranch.getLocation());

                if (!checkSaccoId.isPresent()) {
                    response.setMessage("Sacco Entity ID " + SEntityBranch.getSaccoEntityCode() + " Not Allowed: !! Check with Manager for new Registration/Approval: !!");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                } else if (checkBranchCode.isPresent()) {
                    response.setMessage("Sacco Entity Branch  with Code " + SEntityBranch.getBranchCode() + " Already Registered: !!");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                } else if (checkPhone.isPresent()) {
                    response.setMessage("Sacco Entity Branch with Phone/Mobile " + SEntityBranch.getPhoneNumber() + " Already Registered: !!");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                } else if (checkEmail.isPresent()) {
                    response.setMessage("Sacco Entity Branch with Email Address " + SEntityBranch.getEmail() + " Already Registered: !!");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                } else if (checkBranch.isPresent()) {
                    response.setMessage("Sacco Entity Branch with Description " + SEntityBranch.getBranchCode() + " Located At " + SEntityBranch.getLocation() + " already Registered");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                } else {
                    if (SEntityBranch.getPhoneNumber().length() > 12 || SEntityBranch.getPhoneNumber().length() < 10) {
                        response.setMessage("Sacco Entity Branch Phone/Mobile Number " + SEntityBranch.getPhoneNumber() + " is Invalid: !! Check the Correct NO. FORMAT ALLOWED!");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    } else {
                        SSaccoEntity SSaccoEntity = checkSaccoId.get();
                        SEntityBranch.setSSaccoEntity(SSaccoEntity);
                        SEntityBranch.setPostedBy(UserRequestContext.getCurrentUser());
                        SEntityBranch.setEntityId(EntityRequestContext.getCurrentEntityId());
                        SEntityBranch.setPostedFlag('Y');
                        SEntityBranch.setPostedTime(new Date());
                        SEntityBranch newBranch = entityBranchRepository.save(SEntityBranch);
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
                List<SEntityBranch> SEntityBranchList = entityBranchRepository.findAll();
                if (SEntityBranchList.size() > 0) {
                    response.setMessage("Welcome Manager: " + user + ", You have " + SEntityBranchList.size() + " Registered Sacco Entity Branches");
                    response.setStatusCode(HttpStatus.FOUND.value());
                    response.setEntity(SEntityBranchList);
                } else {
                    response.setMessage("Welcome Manager: " + user + ", You have " + SEntityBranchList.size() + " Registered Sacco Entity Branches");
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setEntity(SEntityBranchList.size());
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
                List<SEntityBranch> SEntityBranchList = entityBranchRepository.findAllByDeletedFlagOrderByIdDesc('N');
                if (SEntityBranchList.size() > 0) {
                    response.setMessage("Welcome Manager: " + user + ", You have " + SEntityBranchList.size() + " Registered Sacco Entity Branches");
                    response.setStatusCode(HttpStatus.FOUND.value());
                    response.setEntity(SEntityBranchList);
                } else {
                    response.setMessage("Welcome Manager: " + user + ", You have " + SEntityBranchList.size() + " Registered Sacco Entity Branches");
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setEntity(SEntityBranchList.size());
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
                List<SEntityBranch> SEntityBranchList = entityBranchRepository.findAllByDeletedFlagAndVerifiedFlagOrderByIdDesc('N', 'Y');
                if (SEntityBranchList.size() > 0) {
                    response.setMessage("Welcome Manager: " + user + ", You have " + SEntityBranchList.size() + " Active Sacco Entity Branches");
                    response.setStatusCode(HttpStatus.FOUND.value());
                    response.setEntity(SEntityBranchList);
                } else {
                    response.setMessage("Welcome Manager: " + user + ", You have " + SEntityBranchList.size() + " Active Sacco Entity Branches");
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setEntity(SEntityBranchList.size());
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
                List<SEntityBranch> SEntityBranchList = entityBranchRepository.findAllByEntityIdAndDeletedFlagOrderByIdDesc(entityId, 'N');
                if (SEntityBranchList.size() > 0) {
                    response.setMessage("Welcome : " + user + ", You have " + SEntityBranchList.size() + " Registered Sacco Entity Branches");
                    response.setStatusCode(HttpStatus.FOUND.value());
                    response.setEntity(SEntityBranchList);
                } else {
                    response.setMessage("Welcome : " + user + ", You have " + SEntityBranchList.size() + " Registered Sacco Entity Branches");
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setEntity(SEntityBranchList.size());
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
                List<SEntityBranch> SEntityBranchList = entityBranchRepository.findAllByEntityIdAndDeletedFlagAndVerifiedFlagOrderByIdDesc(entityId, 'N', 'Y');
                if (SEntityBranchList.size() > 0) {
                    response.setMessage("Welcome : " + user + ", You have " + SEntityBranchList.size() + " Active Sacco Entity Branches");
                    response.setStatusCode(HttpStatus.FOUND.value());
                    response.setEntity(SEntityBranchList);
                } else {
                    response.setMessage("Welcome : " + user + ", You have " + SEntityBranchList.size() + " Active Sacco Entity Branches");
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setEntity(SEntityBranchList.size());
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
                Optional<SEntityBranch> entityBranch = entityBranchRepository.findEntityBranchById(id);
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
                Optional<SEntityBranch> entityBranch = entityBranchRepository.findBranchByBranchCode(branchCode);
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
                Optional<SEntityBranch> entityBranch = entityBranchRepository.findBranchByEntityIdAndBranchCode(entityId, branchCode);
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

    public EntityResponse<?> modifyEntityBranch(SEntityBranch SEntityBranch) {
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
                Optional<SEntityBranch> checkBranchId = entityBranchRepository.findEntityBranchById(SEntityBranch.getId());
                if (!checkBranchId.isPresent()) {
                    response.setMessage("Entity Branch With ID  " + SEntityBranch.getEntityId() + " Not Registered with our Databases: !!");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                } else {
                    if (SEntityBranch.getPhoneNumber().length() > 12 || SEntityBranch.getPhoneNumber().length() < 10) {
                        response.setMessage("Sacco Entity Branch Phone/Mobile Number " + SEntityBranch.getPhoneNumber() + " is Invalid: !! Check the Correct NO. FORMAT ALLOWED!");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    } else {
                        SEntityBranch.setPostedBy(checkBranchId.get().getPostedBy());
                        SEntityBranch.setPostedFlag(checkBranchId.get().getPostedFlag());
                        SEntityBranch.setPostedTime(checkBranchId.get().getPostedTime());
                        SEntityBranch.setModifiedBy(user);
                        SEntityBranch.setModifiedTime(new Date());
                        SEntityBranch.setModifiedFlag('Y');
                        SEntityBranch modifyBranch = entityBranchRepository.save(SEntityBranch);
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
                Optional<SEntityBranch> checkBranchId = entityBranchRepository.findEntityBranchById(id);
                if (!checkBranchId.isPresent()) {
                    response.setMessage("Entity Branch With ID  " + id + " Not Registered with our Databases: !!");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                } else {
                    SEntityBranch SEntityBranch = checkBranchId.get();
                    SEntityBranch.setVerifiedBy(user);
                    SEntityBranch.setVerifiedTime(new Date());
                    SEntityBranch.setVerifiedFlag('Y');
                    SEntityBranch verifyBranch = entityBranchRepository.save(SEntityBranch);
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
