package com.emtechhouse.usersservice.Roles.Workclass;

import com.emtechhouse.usersservice.Roles.Role;
import com.emtechhouse.usersservice.Roles.RoleRepository;
import com.emtechhouse.usersservice.Roles.Workclass.Privileges.BasicActions.Basicactions;
import com.emtechhouse.usersservice.Roles.Workclass.Privileges.BasicActions.BasicactionsRepo;
import com.emtechhouse.usersservice.Roles.Workclass.Privileges.Privilege;
import com.emtechhouse.usersservice.Roles.Workclass.Privileges.PrivilegeActionsRepo;
import com.emtechhouse.usersservice.Roles.Workclass.Privileges.PrivilegeRepo;
import com.emtechhouse.usersservice.Roles.Workclass.Privileges.Privilege_Actions;
import com.emtechhouse.usersservice.utils.EntityResponse;
import com.emtechhouse.usersservice.utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.usersservice.utils.HttpInterceptor.UserRequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
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
    private final BasicactionsRepo basicactionsRepo;

    private final PrivilegeActionsRepo privilegeActionsRepo;

    //added basicactionsRepo and initialized it in the parameter
    public WorkclassController(WorkclassRepo workclassRepo, WorkclassService workclassService, PrivilegeRepo privilegeRepo, RoleRepository roleRepository, BasicactionsRepo basicactionsRepo, PrivilegeActionsRepo privilegeActionsRepo) {
        this.workclassRepo = workclassRepo;
        this.workclassService = workclassService;
        this.privilegeRepo = privilegeRepo;
        this.roleRepository = roleRepository;
        this.basicactionsRepo = basicactionsRepo;
        this.privilegeActionsRepo = privilegeActionsRepo;
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
                    Optional<Role> role = roleRepository.findById(workclassRequest.getRoleId());
                    if (role.isPresent()){
                        Workclass workclass = new Workclass();
                        workclass.setPostedBy(UserRequestContext.getCurrentUser());
                        workclass.setEntityId(EntityRequestContext.getCurrentEntityId());
                        workclass.setPostedFlag('Y');
                        workclass.setPostedTime(new Date());
                        workclass.setRoleId(role.get().getId());
                        workclass.setWorkClass(workclassRequest.getWorkClass());
                        workclass.setPrivileges(workclassRequest.getPrivileges());
                        Workclass newWorkclass = workclassRepo.save(workclass);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("WORK CLASS WITH CLASS NAME " + workclass.getWorkClass() + " CREATED SUCCESSFULLY AT " + workclass.getPostedTime() );
                        response.setStatusCode(HttpStatus.CREATED.value());
                        response.setEntity(newWorkclass);
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
                    List<Workclass> workclass = workclassRepo.findByEntityIdAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), 'N');
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(workclass);
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
                    Workclass workclass = workclassService.findById(id);
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(workclass);
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
                    Optional<Workclass> workclass = workclassRepo.findById(id);
                    if (workclass.isPresent()){
                        List<Privilege> privileges = privilegeRepo.findByWorkclassAndSelected(workclass.get(),true);
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(privileges);
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
                    List<Workclass> workclass = workclassRepo.findByRoleIdAndDeletedFlag(id, 'N');
                    if (workclass.size()>0){
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(workclass);
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

    //http://localhost:9002/auth/workclass/find/privilege/by/workclass/124 - Getting the workclass


//    @GetMapping("/find/basicaction/by/privilege/{id}")
//    public ResponseEntity<?> getBasicActions(@PathVariable("id") Long id) {
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
//                    Optional<Privilege_Actions> privilege = privilegeActionsRepo.findById(id);
//                    if (privilege.isPresent()){
//                        List<Privilege_Actions> basicactions = privilegeActionsRepo.findByPrivilegeId(id);
//                        EntityResponse response = new EntityResponse();
//                        response.setMessage(HttpStatus.OK.getReasonPhrase());
//                        response.setStatusCode(HttpStatus.OK.value());
//                        response.setEntity(basicactions);
//                        return new ResponseEntity<>(response, HttpStatus.OK);
//                    }else{
//                        EntityResponse response = new EntityResponse();
//                        response.setMessage("Privilege Not available");
//                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
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

    @GetMapping("/find/basicaction/by/privilege/{id}")
    public ResponseEntity<?> getBasicActions(@PathVariable("id") Long id) {
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
                    Optional<Privilege_Actions> privilegeOptional = privilegeActionsRepo.findById(id);

                    if (privilegeOptional.isPresent()) {
                        Privilege_Actions privilege = privilegeOptional.get();
                        String privilegeName = privilege.getPrivilegeName();
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(privilegeName);

                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("Privilege Not available");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Caught Error: {}", e);
            // Return an appropriate error response instead of null
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred");
        }
    }


    @PutMapping("/modify")
    public ResponseEntity<?> updateAccessgroup(@RequestBody Workclass workclass) {
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
                    workclass.setModifiedBy(UserRequestContext.getCurrentUser());
                    workclass.setEntityId(EntityRequestContext.getCurrentEntityId());
                    Optional<Workclass> workclass1 = workclassRepo.findById(workclass.getId());
                    if (workclass1.isPresent()) {
                        workclass.setId(workclass.getId());
                        workclass.setPostedTime(workclass1.get().getPostedTime());
                        workclass.setPostedFlag('Y');
                        workclass.setPostedBy(workclass1.get().getPostedBy());
                        workclass.setModifiedFlag('Y');
                        workclass.setVerifiedFlag('N');
                        workclass.setModifiedTime(new Date());
                        workclass.setModifiedBy(workclass.getModifiedBy());
                        workclassRepo.save(workclass);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("WORK CLAS WITH NAME " + workclass.getWorkClass() + " MODIFIED SUCCESSFULLY AT " + workclass.getModifiedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(workclass);
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
                    Optional<Workclass> workclass1 = workclassRepo.findById(Long.parseLong(id));
                    if (workclass1.isPresent()) {
                        Workclass workclass = workclass1.get();
                        // Check Maker Checker
                        if (workclass.getPostedBy().equalsIgnoreCase(UserRequestContext.getCurrentUser())){
                            EntityResponse response = new EntityResponse();
                            response.setMessage("You Can Not Verify What you initiated");
                            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                            response.setEntity("");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        }else{
                            System.out.println("About to verify work class "+workclass1);
                            workclass.setVerifiedFlag('Y');
                            workclass.setVerifiedTime(new Date());
                            workclass.setVerifiedBy(UserRequestContext.getCurrentUser());
                            workclassRepo.save(workclass);
                            EntityResponse response = new EntityResponse();
                            response.setMessage("WORK CLASS WITH CLASS NAME " + workclass.getWorkClass() + " VERIFIED SUCCESSFULLY AT " + workclass.getVerifiedTime() );
                            response.setStatusCode(HttpStatus.OK.value());
                            response.setEntity(workclass);
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
                    Optional<Workclass> workclass1 = workclassRepo.findById(Long.parseLong(id));
                    if (workclass1.isPresent()) {
                        Workclass workclass = workclass1.get();
                        workclass.setDeletedFlag('Y');
                        workclass.setDeletedTime(new Date());
                        workclass.setDeletedBy(UserRequestContext.getCurrentUser());
                        workclassRepo.save(workclass);
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(workclass);
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
