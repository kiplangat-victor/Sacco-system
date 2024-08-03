package emt.sacco.middleware.SecurityImpl.enumeration.Roles.Workclass;

import emt.sacco.middleware.SecurityImpl.enumeration.Roles.SRole;
import emt.sacco.middleware.SecurityImpl.enumeration.Roles.RoleRepository;
import emt.sacco.middleware.SecurityImpl.enumeration.Roles.Workclass.Privileges.SPrivilege;
import emt.sacco.middleware.SecurityImpl.enumeration.Roles.Workclass.Privileges.PrivilegeRepo;
import emt.sacco.middleware.Utils.Config.UserRequestContext;
import emt.sacco.middleware.Utils.EntityRequestContext;
import emt.sacco.middleware.Utils.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/auth/workclass")
@Slf4j
public class WorkclassController {
    private final WorkclassRepo workclassRepo;
    private final WorkclassService workclassService;
    private final PrivilegeRepo privilegeRepo;
    private final RoleRepository roleRepository;

    public WorkclassController(WorkclassRepo workclassRepo, WorkclassService workclassService, PrivilegeRepo privilegeRepo, RoleRepository roleRepository) {
        this.workclassRepo = workclassRepo;
        this.workclassService = workclassService;
        this.privilegeRepo = privilegeRepo;
        this.roleRepository = roleRepository;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addAccessgroup(@RequestBody WorkclassRequest workclassRequest) {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                EntityResponse response = new EntityResponse();
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
//                    Check if exist
                    Optional<SRole> role = roleRepository.findById(workclassRequest.getRoleId());
                    if (role.isPresent()){
                        SWorkclass SWorkclass = new SWorkclass();
                        SWorkclass.setPostedBy(UserRequestContext.getCurrentUser());
                        SWorkclass.setEntityId(EntityRequestContext.getCurrentEntityId());
                        SWorkclass.setPostedFlag('Y');
                        SWorkclass.setPostedTime(new Date());
                        SWorkclass.setRoleId(role.get().getId());
                        SWorkclass.setWorkClass(workclassRequest.getWorkClass());
                        SWorkclass.setSPrivileges(workclassRequest.getSPrivileges());
                        SWorkclass newSWorkclass = workclassRepo.save(SWorkclass);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("WORK CLASS WITH CLASS NAME " + SWorkclass.getWorkClass() + " CREATED SUCCESSFULLY AT " + SWorkclass.getPostedTime() );
                        response.setStatusCode(HttpStatus.CREATED.value());
                        response.setEntity(newSWorkclass);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }else{
                        EntityResponse response = new EntityResponse();
                        response.setMessage("Role is not present");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }

                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllAccessgroups() {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                EntityResponse response = new EntityResponse();
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    List<SWorkclass> SWorkclasses = workclassRepo.findByEntityIdAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), 'N');
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(SWorkclasses);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<?> getAccessgroupById(@PathVariable("id") Long id) {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                EntityResponse response = new EntityResponse();
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    SWorkclass SWorkclass = workclassService.findById(id);
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(SWorkclass);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/find/privilege/by/workclass/{id}")
    public ResponseEntity<?> getPrevileges(@PathVariable("id") Long id) {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                EntityResponse response = new EntityResponse();
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    Optional<SWorkclass> workclass = workclassRepo.findById(id);
                    if (workclass.isPresent()){
                        List<SPrivilege> SPrivileges = privilegeRepo.findBySWorkclassAndSelected(workclass.get(),true);
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(SPrivileges);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }else{
                        EntityResponse response = new EntityResponse();
                        response.setMessage("Workclass Not available");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }

                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/find/workclass/by/role/{id}")
    public ResponseEntity<?> getWorkCLassByrole(@PathVariable("id") Long id) {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                EntityResponse response = new EntityResponse();
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    List<SWorkclass> SWorkclasses = workclassRepo.findByRoleIdAndDeletedFlag(id, 'N');
                    if (SWorkclasses.size()>0){
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(SWorkclasses);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }else{
                        EntityResponse response = new EntityResponse();
                        response.setMessage("Workclass Not available");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }

                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }



    @PutMapping("/modify")
    public ResponseEntity<?> updateAccessgroup(@RequestBody SWorkclass SWorkclass) {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                EntityResponse response = new EntityResponse();
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    SWorkclass.setModifiedBy(UserRequestContext.getCurrentUser());
                    SWorkclass.setEntityId(EntityRequestContext.getCurrentEntityId());
                    Optional<SWorkclass> workclass1 = workclassRepo.findById(SWorkclass.getId());
                    if (workclass1.isPresent()) {
                        SWorkclass.setId(SWorkclass.getId());
                        SWorkclass.setPostedTime(workclass1.get().getPostedTime());
                        SWorkclass.setPostedFlag('Y');
                        SWorkclass.setPostedBy(workclass1.get().getPostedBy());
                        SWorkclass.setModifiedFlag('Y');
                        SWorkclass.setVerifiedFlag('N');
                        SWorkclass.setModifiedTime(new Date());
                        SWorkclass.setModifiedBy(SWorkclass.getModifiedBy());
                        workclassRepo.save(SWorkclass);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("WORK CLAS WITH NAME " + SWorkclass.getWorkClass() + " MODIFIED SUCCESSFULLY AT " + SWorkclass.getModifiedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(SWorkclass);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    @PutMapping("/verify/{id}")
    public ResponseEntity<?> verify(@PathVariable String id) {
        try {
            System.out.println("Verify work class id: "+id);
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                EntityResponse response = new EntityResponse();
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    Optional<SWorkclass> workclass1 = workclassRepo.findById(Long.parseLong(id));
                    if (workclass1.isPresent()) {
                        SWorkclass SWorkclass = workclass1.get();
                        // Check Maker Checker
                        if (SWorkclass.getPostedBy().equalsIgnoreCase(UserRequestContext.getCurrentUser())){
                            EntityResponse response = new EntityResponse();
                            response.setMessage("You Can Not Verify What you initiated");
                            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                            response.setEntity("");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        }else{
                            System.out.println("About to verify work class "+workclass1);
                            SWorkclass.setVerifiedFlag('Y');
                            SWorkclass.setVerifiedTime(new Date());
                            SWorkclass.setVerifiedBy(UserRequestContext.getCurrentUser());
                            workclassRepo.save(SWorkclass);
                            EntityResponse response = new EntityResponse();
                            response.setMessage("WORK CLASS WITH CLASS NAME " + SWorkclass.getWorkClass() + " VERIFIED SUCCESSFULLY AT " + SWorkclass.getVerifiedTime() );
                            response.setStatusCode(HttpStatus.OK.value());
                            response.setEntity(SWorkclass);
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        }
                    } else {
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAccessgroup(@PathVariable String id) {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                EntityResponse response = new EntityResponse();
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    Optional<SWorkclass> workclass1 = workclassRepo.findById(Long.parseLong(id));
                    if (workclass1.isPresent()) {
                        SWorkclass SWorkclass = workclass1.get();
                        SWorkclass.setDeletedFlag('Y');
                        SWorkclass.setDeletedTime(new Date());
                        SWorkclass.setDeletedBy(UserRequestContext.getCurrentUser());
                        workclassRepo.save(SWorkclass);
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(SWorkclass);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
}
