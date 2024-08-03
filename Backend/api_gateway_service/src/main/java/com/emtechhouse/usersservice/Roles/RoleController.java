package com.emtechhouse.usersservice.Roles;

import com.emtechhouse.usersservice.utils.EntityResponse;
import com.emtechhouse.usersservice.utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.usersservice.utils.HttpInterceptor.UserRequestContext;
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
    public ResponseEntity<?> addRole(@RequestBody Role role) {
        try {
            System.out.println("Role header: "+role);
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
                    String name = "ROLE_"+role.getName().toUpperCase();
                    role.setName(name);
                    Optional<Role> role1 = roleRepository.findByEntityIdAndNameAndDeletedFlag(EntityRequestContext.getCurrentEntityId(),role.getName(), 'N');
                    if (role1.isPresent()) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("Role Exist! with the name "+role.getName());
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity(role);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }
                    String roleCode = "ROL" + new Random().ints(4, 0, 10).mapToObj(Integer::toString).reduce("", String::concat);
                    Optional<Role> findRoleCode = roleRepository.findByEntityIdAndRoleCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(),roleCode,'N');
                    if (findRoleCode.isPresent()) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("Role with the role code "+roleCode+ " exists!");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity(role);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        role.setPostedBy(UserRequestContext.getCurrentUser());
                        role.setEntityId(EntityRequestContext.getCurrentEntityId());
                        role.setPostedFlag('Y');
                        role.setPostedTime(new Date());
                        role.setRoleCode(roleCode);
                        Role newRole = roleService.addRole(role);
                        EntityResponse response = new EntityResponse();
                        response.setStatusCode(HttpStatus.CREATED.value());
                        response.setMessage("ROLE WITH ROLE_NAME " + role.getName() + " CREATED SUCCESSFULLY AT " + role.getPostedTime());
                        response.setEntity(newRole);
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
                    List<Role> role = roleRepository.findByEntityIdAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), 'N');
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(role);
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
                    Role role = roleService.findById(id);
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(role);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("/modify")
    public ResponseEntity<?> updateRole(@RequestBody Role role) {
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
                    role.setModifiedBy(UserRequestContext.getCurrentUser());
                    role.setEntityId(EntityRequestContext.getCurrentEntityId());
                    Optional<Role> role1 = roleRepository.findById(role.getId());
                    if (role1.isPresent()) {
                        role.setModifiedFlag('Y');
                        role.setVerifiedFlag('N');
                        role.setModifiedTime(new Date());
                        role.setPostedTime(role1.get().getPostedTime());
                        role.setPostedBy(role1.get().getPostedBy());
                        role.setModifiedBy(UserRequestContext.getCurrentUser());
                        roleService.updateRole(role);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("ROLE WITH NAME " + role.getName() + " MODIFIED SUCCESSFULLY AT " + role.getModifiedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(role);
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
                    Optional<Role> role1 = roleRepository.findById(id);
                    if (role1.isPresent()) {
                        Role role = role1.get();
                        // Check Maker Checker
                        if (role.getPostedBy().equalsIgnoreCase(UserRequestContext.getCurrentUser())){
                            EntityResponse response = new EntityResponse();
                            response.setMessage("You Can Not Verify What you initiated");
                            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                            response.setEntity("");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        }else{
                            role.setVerifiedFlag('Y');
                            role.setVerifiedTime(new Date());
                            role.setVerifiedBy(UserRequestContext.getCurrentUser());
                            roleRepository.save(role);
                            EntityResponse response = new EntityResponse();
                            response.setMessage("ROLE WITH NAME " + role.getName() + " VERIFIED SUCCESSFULLY AT " + role.getVerifiedTime());
                            response.setStatusCode(HttpStatus.OK.value());
                            response.setEntity(role);
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
                    Optional<Role> role1 = roleRepository.findById(id);
                    if (role1.isPresent()) {
                        Role role = role1.get();
                        role.setDeletedFlag('Y');
                        role.setDeletedTime(new Date());
                        role.setPostedTime(role1.get().getPostedTime());
                        role.setPostedBy(role1.get().getPostedBy());
                        role.setDeletedBy(UserRequestContext.getCurrentUser());
                        roleRepository.save(role);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("ROLE WITH NAME " + role1.get().getName() + " DELETED SUCCESSFULLY AT " + role.getDeletedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(role);
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
