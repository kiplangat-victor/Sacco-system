package emt.sacco.middleware.SecurityImpl.enumeration.Roles.Workclass.Privileges.BasicActions;

import emt.sacco.middleware.SecurityImpl.enumeration.Roles.Workclass.Privileges.SPrivilege;
import emt.sacco.middleware.SecurityImpl.enumeration.Roles.Workclass.Privileges.PrivilegeRepo;
import emt.sacco.middleware.SecurityImpl.enumeration.Roles.Workclass.SWorkclass;
import emt.sacco.middleware.SecurityImpl.enumeration.Roles.Workclass.WorkclassRepo;
import emt.sacco.middleware.Utils.Config.UserRequestContext;
import emt.sacco.middleware.Utils.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class BasicactionsService {
    private final BasicactionsRepo basicactionsRepo;
    private final PrivilegeRepo privilegeRepo;
    private final WorkclassRepo workclassRepo;

    public BasicactionsService(BasicactionsRepo basicactionsRepo, PrivilegeRepo privilegeRepo, WorkclassRepo workclassRepo) {
        this.basicactionsRepo = basicactionsRepo;
        this.privilegeRepo = privilegeRepo;
        this.workclassRepo = workclassRepo;
    }
    @Transactional
    public EntityResponse addBasicactions(List<SBasicactions> basicactions, Long privilegeId, Long workclassId) {
        try {
            EntityResponse response = new EntityResponse();
            Optional<SWorkclass> workclass = workclassRepo.findById(workclassId);
            if (workclass.isPresent()){
                Optional<SPrivilege> privilege = privilegeRepo.findById(privilegeId);
                if(privilege.isPresent()){
                    List<SBasicactions> newBasicActions = new ArrayList<>();
                    for (int i = 0; i<basicactions.size(); i++){
                        SBasicactions SBasicactions1 = basicactions.get(i);
                        List<SBasicactions> basicaction = basicactionsRepo.findBySPrivilegeAndSWorkclassAndCode(privilege.get(), workclass.get(), SBasicactions1.getCode());
                        if (basicaction.size()>0){
                            response.setMessage("Basic Action is already mapped!");
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            response.setEntity("");
                        }else {
                            SBasicactions1.setPostedBy("User");
                            SBasicactions1.setPostedFlag('Y');
                            SBasicactions1.setPostedTime(new Date());
                            SBasicactions1.setSPrivilege(privilege.get());
                            SBasicactions1.setSWorkclass(workclass.get());
                            newBasicActions.add(SBasicactions1);
                        }
                    }
                    if (newBasicActions.size()>0){
                        response.setMessage(HttpStatus.CREATED.getReasonPhrase());
                        response.setStatusCode(HttpStatus.CREATED.value());
                        response.setEntity(basicactionsRepo.saveAll(newBasicActions));
                        return response;
                    }else {
                        response.setMessage("All the Basic Action provided are already mapped!");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                        return response;
                    }
                }else{
                    response.setMessage("Privilege Not Found!");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return response;
                }
            }else {
                response.setMessage("Work Class Not Found!");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return response;
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse findAllBasicactionss(Long privilegeId,Long workclassId) {
        try {
            Optional<SWorkclass> workclass = workclassRepo.findById(workclassId);
            Optional<SPrivilege> privilege = privilegeRepo.findById(privilegeId);
            List<SBasicactions> basicaction = basicactionsRepo.findBySPrivilegeAndSWorkclassAndDeletedFlag(privilege.get(),workclass.get(), 'N');
            if (basicaction.size() > 0){
                EntityResponse response = new EntityResponse();
                response.setMessage("Found!");
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(basicaction);
                return response;
            }else {
                EntityResponse response = new EntityResponse();
                response.setMessage("Not Found!");
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity("");
                return response;
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse findById(Long id){
        try {
            Optional<SBasicactions> basicaction = basicactionsRepo.findById(id);
            if (basicaction.isPresent()){
                EntityResponse response = new EntityResponse();
                response.setMessage("Found!");
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(basicaction);
                return response;
            }else {
                EntityResponse response = new EntityResponse();
                response.setMessage("Not Found!");
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity("");
                return response;
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    public EntityResponse updateBasicactions(Long id, List<SBasicactions> basicactions, Long privilegeId, Long workclassId) {
        try {
            EntityResponse response = new EntityResponse();
            response.setMessage("Updated Successfully!");
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity("");
            for (int i=0;i<basicactions.size(); i++){
                Optional<SBasicactions> basicactions1 = basicactionsRepo.findById(basicactions.get(i).getId());
                if (basicactions1.isPresent()){
                    SBasicactions basic = basicactions1.get();
                    basic.setModifiedFlag('Y');
                    basic.setModifiedBy(UserRequestContext.getCurrentUser());
                    basic.setModifiedTime(new Date());
                    basic.setSelected(basicactions.get(i).isSelected());
                    basicactionsRepo.save(basic);
                }else{
                    response.setMessage("Not Found!");
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity("");
                }
                if (i==basicactions.size()){
                    response.setMessage("Updated Successfully!");
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity("");
                }
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse verify(Long id) {
        try {
            Optional<SBasicactions> basicaction = basicactionsRepo.findById(id);
            if (basicaction.isPresent()){
                SBasicactions SBasicactions = basicaction.get();
                SBasicactions.setVerifiedBy(UserRequestContext.getCurrentUser());
                SBasicactions.setVerifiedFlag('Y');
                SBasicactions.setVerifiedTime(new Date());
                EntityResponse response = new EntityResponse();
                response.setMessage("Verified Successfully!");
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity("");
                basicactionsRepo.save(SBasicactions);
                return response;
            }else {
                EntityResponse response = new EntityResponse();
                response.setMessage("Not Found!");
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity("");
                return response;
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse deleteTemporary(Long id) {
        try {
            Optional<SBasicactions> basicaction = basicactionsRepo.findById(id);
            if (basicaction.isPresent()){
                SBasicactions SBasicactions = basicaction.get();
                SBasicactions.setDeletedBy(UserRequestContext.getCurrentUser());
                SBasicactions.setDeletedFlag('Y');
                SBasicactions.setDeletedTime(new Date());
                EntityResponse response = new EntityResponse();
                response.setMessage("Deleted Successfully!");
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity("");
                basicactionsRepo.save(SBasicactions);
                return response;
            }else {
                EntityResponse response = new EntityResponse();
                response.setMessage("Not Found!");
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity("");
                return response;
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    public EntityResponse restoreId(Long id) {
        try {
            Optional<SBasicactions> basicaction = basicactionsRepo.findById(id);
            if (basicaction.isPresent()){
                EntityResponse response = new EntityResponse();
                response.setMessage("Restored Successfully!");
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity("");
                basicaction.get().setDeletedFlag('N');
                basicactionsRepo.save(basicaction.get());
                return response;
            }else {
                EntityResponse response = new EntityResponse();
                response.setMessage("Not Found!");
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity("");
                return response;
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    public EntityResponse deletePermanently(Long privilegeId, Long workclassId) {
        try {
            EntityResponse response = new EntityResponse();
            response.setMessage("Deleted Successfully!");
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity("");
            Optional<SWorkclass> workclass = workclassRepo.findById(workclassId);
            Optional<SPrivilege> privilege = privilegeRepo.findById(privilegeId);
            List<SBasicactions> basicaction = basicactionsRepo.findBySPrivilegeAndSWorkclassAndDeletedFlag(privilege.get(),workclass.get(), 'N');
            basicactionsRepo.deleteAll(basicaction);
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return  null;
        }
    }
}