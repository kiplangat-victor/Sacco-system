package emt.sacco.middleware.SecurityImpl.enumeration.Roles;

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
import java.util.Random;


@RestController
@RequestMapping("/auth/role")
@Slf4j
public class RoleController {
    private final RoleRepository roleRepository;
    private final RoleService roleService;

    public RoleController(RoleRepository roleRepository, RoleService roleService) {
        this.roleRepository = roleRepository;
        this.roleService = roleService;
    }
    @PostMapping("/add")
    public ResponseEntity<?> addRole(@RequestBody SRole SRole) {
        try {
            System.out.println("Role header: "+ SRole);
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
                    String name = "ROLE_"+ SRole.getName().toUpperCase();
                    SRole.setName(name);
                    Optional<SRole> role1 = roleRepository.findByEntityIdAndNameAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), SRole.getName(), 'N');
                    if (role1.isPresent()) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("Role Exist! with the name "+ SRole.getName());
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity(SRole);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }
                    String roleCode = "ROL" + new Random().ints(4, 0, 10).mapToObj(Integer::toString).reduce("", String::concat);
                    Optional<SRole> findRoleCode = roleRepository.findByEntityIdAndRoleCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(),roleCode,'N');
                    if (findRoleCode.isPresent()) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("Role with the role code "+roleCode+ " exists!");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity(SRole);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        SRole.setPostedBy(UserRequestContext.getCurrentUser());
                        SRole.setEntityId(EntityRequestContext.getCurrentEntityId());
                        SRole.setPostedFlag('Y');
                        SRole.setPostedTime(new Date());
                        SRole.setRoleCode(roleCode);
                        SRole newSRole = roleService.addRole(SRole);
                        EntityResponse response = new EntityResponse();
                        response.setStatusCode(HttpStatus.CREATED.value());
                        response.setMessage("ROLE WITH ROLE_NAME " + SRole.getName() + " CREATED SUCCESSFULLY AT " + SRole.getPostedTime());
                        response.setEntity(newSRole);
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
    public ResponseEntity<?> getAllRoles() {
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
                    System.out.println(EntityRequestContext.getCurrentEntityId());
                    List<SRole> SRole = roleRepository.findByEntityIdAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), 'N');
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(SRole);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<?> getRoleById(@PathVariable("id") Long id) {
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
                    SRole SRole = roleService.findById(id);
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(SRole);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("/modify")
    public ResponseEntity<?> updateRole(@RequestBody SRole SRole) {
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
                    SRole.setModifiedBy(UserRequestContext.getCurrentUser());
                    SRole.setEntityId(EntityRequestContext.getCurrentEntityId());
                    Optional<SRole> role1 = roleRepository.findById(SRole.getId());
                    if (role1.isPresent()) {
                        SRole.setModifiedFlag('Y');
                        SRole.setVerifiedFlag('N');
                        SRole.setModifiedTime(new Date());
                        SRole.setPostedTime(role1.get().getPostedTime());
                        SRole.setPostedBy(role1.get().getPostedBy());
                        SRole.setModifiedBy(UserRequestContext.getCurrentUser());
                        roleService.updateRole(SRole);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("ROLE WITH NAME " + SRole.getName() + " MODIFIED SUCCESSFULLY AT " + SRole.getModifiedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(SRole);
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
    public ResponseEntity<?> verify(@PathVariable Long id) {
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
                    Optional<SRole> role1 = roleRepository.findById(id);
                    if (role1.isPresent()) {
                        SRole SRole = role1.get();
                        // Check Maker Checker
                        if (SRole.getPostedBy().equalsIgnoreCase(UserRequestContext.getCurrentUser())){
                            EntityResponse response = new EntityResponse();
                            response.setMessage("You Can Not Verify What you initiated");
                            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                            response.setEntity("");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        }else{
                            SRole.setVerifiedFlag('Y');
                            SRole.setVerifiedTime(new Date());
                            SRole.setVerifiedBy(UserRequestContext.getCurrentUser());
                            roleRepository.save(SRole);
                            EntityResponse response = new EntityResponse();
                            response.setMessage("ROLE WITH NAME " + SRole.getName() + " VERIFIED SUCCESSFULLY AT " + SRole.getVerifiedTime());
                            response.setStatusCode(HttpStatus.OK.value());
                            response.setEntity(SRole);
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
    public ResponseEntity<?> deleteRole(@PathVariable Long id) {
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
                    Optional<SRole> role1 = roleRepository.findById(id);
                    if (role1.isPresent()) {
                        SRole SRole = role1.get();
                        SRole.setDeletedFlag('Y');
                        SRole.setDeletedTime(new Date());
                        SRole.setPostedTime(role1.get().getPostedTime());
                        SRole.setPostedBy(role1.get().getPostedBy());
                        SRole.setDeletedBy(UserRequestContext.getCurrentUser());
                        roleRepository.save(SRole);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("ROLE WITH NAME " + role1.get().getName() + " DELETED SUCCESSFULLY AT " + SRole.getDeletedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(SRole);
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
