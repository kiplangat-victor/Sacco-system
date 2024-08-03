//package com.emtechhouse.usersservice;
//
//import com.emtechhouse.usersservice.DTO.Mailparams;
//import com.emtechhouse.usersservice.EntityBranch.EntityBranchRepository;
//import com.emtechhouse.usersservice.MailService.MailService;
//import com.emtechhouse.usersservice.Roles.ERole;
//import com.emtechhouse.usersservice.Roles.Role;
//import com.emtechhouse.usersservice.Roles.RoleRepository;
//import com.emtechhouse.usersservice.Roles.Workclass.Privileges.BasicActions.Basicactions;
//import com.emtechhouse.usersservice.Roles.Workclass.Privileges.BasicActions.BasicactionsService;
//import com.emtechhouse.usersservice.Roles.Workclass.Privileges.Privilege;
//import com.emtechhouse.usersservice.Roles.Workclass.Workclass;
//import com.emtechhouse.usersservice.Roles.Workclass.WorkclassRepo;
//import com.emtechhouse.usersservice.SaccoEntity.SaccoEntity;
//import com.emtechhouse.usersservice.SaccoEntity.SaccoEntityRepository;
//import com.emtechhouse.usersservice.Users.Users;
//import com.emtechhouse.usersservice.Users.UsersRepository;
//import com.emtechhouse.usersservice.utils.InitAuth;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
//import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
//import org.springframework.context.annotation.Bean;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import javax.mail.MessagingException;
//import java.time.LocalDate;
//import java.util.*;
//
//@SpringBootApplication
//@EnableZuulProxy
//@EnableDiscoveryClient
//public class UsersServiceApplication {
//    @Autowired
//    private UsersRepository userRepository;
//    @Autowired
//    WorkclassRepo workclassRepo;
//
//    @Autowired
//    PasswordEncoder encoder;
//
//    @Autowired
//    InitAuth initAuth;
//
//    @Autowired
//    private RoleRepository roleRepository;
//    @Autowired
//    MailService mailService;
//    @Autowired
//    private BasicactionsService basicactionsService;
//
//    @Value("${organisation.superUserEmail}")
//    private String superUserEmail;
//    @Value("${organisation.superUserFirstName}")
//    private String superUserFirstName;
//    @Value("${organisation.superUserLastName}")
//    private String superUserLastName;
//    @Value("${organisation.superUserUserName}")
//    private String superUserUserName;
//    @Value("${organisation.superUserPhone}")
//    private String superUserPhone;
//    @Value("${organisation.superUserSolCode}")
//    private String superUserSolCode;
//    @Value("${organisation.superUserPassword}")
//    private String superUserPassword;
//    String NONE = "NONE";
//    private final SaccoEntityRepository saccoEntityRepository;
//    private final EntityBranchRepository entityBranchRepository;
//
//    public UsersServiceApplication(SaccoEntityRepository saccoEntityRepository, EntityBranchRepository entityBranchRepository) {
//        this.saccoEntityRepository = saccoEntityRepository;
//        this.entityBranchRepository = entityBranchRepository;
//    }
//
//    public static void main(String[] args) {
//        SpringApplication.run(UsersServiceApplication.class, args);
//    }
//
//
//    private void initSaccoEntity() {
//        if (saccoEntityRepository.findAllById().size() < 1) {
//            SaccoEntity saccoEntity = new SaccoEntity();
//            saccoEntity.setEntityId("001");
//            saccoEntity.setEntityName("E&M HOUSE LTD");
//            saccoEntity.setEntityDescription("Em-Tech Technology House Limited");
//            saccoEntity.setEntityEmail("no-reply@emtechhouse.co.ke");
//            saccoEntity.setEntityPhoneNumber("254721845120");
//            saccoEntity.setEntityLocation("TATU CITY");
//            saccoEntity.setEntityStatus("ACTIVE");
//            saccoEntity.setPostedBy("Superuser");
//            saccoEntity.setPostedFlag('Y');
//            saccoEntity.setPostedOn(LocalDate.now());
//            saccoEntity.setVerifiedBy("SYSTEM");
//            saccoEntity.setVerifiedFlag('Y');
//            saccoEntity.setVerifiedOn(LocalDate.now());
//            saccoEntityRepository.save(saccoEntity);
//        }
//    }
//
//    private void initRoles() {
//        List<Role> roleList = new ArrayList<>();
//        if (roleRepository.findByName(ERole.ROLE_USER.toString()).isEmpty()) {
//            Role userRole = new Role();
//            userRole.setName(ERole.ROLE_USER.toString());
//            userRole.setEntityId("001");
//            userRole.setRoleCode("002");
//            userRole.setManagerial(false);
//            userRole.setPostedBy("SYSTEM");
//            userRole.setPostedFlag('Y');
//            userRole.setVerifiedBy("SYSTEM");
//            userRole.setVerifiedFlag('Y');
//            userRole.setVerifiedTime(new Date());
//            userRole.setPostedTime(new Date());
//            roleList.add(userRole);
//        }
//        if (roleRepository.findByName(ERole.ROLE_TELLER.toString()).isEmpty()) {
//            Role tellerRole = new Role();
//            tellerRole.setEntityId("001");
//            tellerRole.setRoleCode("003");
//            tellerRole.setManagerial(false);
//            tellerRole.setPostedBy("SYSTEM");
//            tellerRole.setPostedFlag('Y');
//            tellerRole.setVerifiedBy("SYSTEM");
//            tellerRole.setVerifiedFlag('Y');
//            tellerRole.setVerifiedTime(new Date());
//            tellerRole.setPostedTime(new Date());
//            tellerRole.setName(ERole.ROLE_TELLER.toString());
//            roleList.add(tellerRole);
//        }
//        if (roleRepository.findByName(ERole.ROLE_OFFICER.toString()).isEmpty()) {
//            Role officerRole = new Role();
//            officerRole.setEntityId("001");
//            officerRole.setRoleCode("004");
//            officerRole.setManagerial(false);
//            officerRole.setPostedBy("SYSTEM");
//            officerRole.setPostedFlag('Y');
//            officerRole.setVerifiedBy("SYSTEM");
//            officerRole.setVerifiedFlag('Y');
//            officerRole.setVerifiedTime(new Date());
//            officerRole.setPostedTime(new Date());
//            officerRole.setName(ERole.ROLE_OFFICER.toString());
//            roleList.add(officerRole);
//        }
//
//        if (roleRepository.findByName(ERole.ROLE_SENIOR_OFFICER.toString()).isEmpty()) {
//            Role seniorOfficerRole = new Role();
//            seniorOfficerRole.setEntityId("001");
//            seniorOfficerRole.setRoleCode("005");
//            seniorOfficerRole.setManagerial(false);
//            seniorOfficerRole.setPostedBy("SYSTEM");
//            seniorOfficerRole.setPostedFlag('Y');
//            seniorOfficerRole.setVerifiedBy("SYSTEM");
//            seniorOfficerRole.setVerifiedFlag('Y');
//            seniorOfficerRole.setVerifiedTime(new Date());
//            seniorOfficerRole.setPostedTime(new Date());
//            seniorOfficerRole.setName(ERole.ROLE_SENIOR_OFFICER.toString());
//            roleList.add(seniorOfficerRole);
//        }
//        if (roleRepository.findByName(ERole.ROLE_MANAGER.toString()).isEmpty()) {
//            Role managerRole = new Role();
//            managerRole.setEntityId("001");
//            managerRole.setRoleCode("006");
//            managerRole.setManagerial(false);
//            managerRole.setPostedBy("SYSTEM");
//            managerRole.setPostedFlag('Y');
//            managerRole.setVerifiedBy("SYSTEM");
//            managerRole.setVerifiedFlag('Y');
//            managerRole.setVerifiedTime(new Date());
//            managerRole.setPostedTime(new Date());
//            managerRole.setName(ERole.ROLE_MANAGER.toString());
//            roleList.add(managerRole);
//        }
//
//        if (roleRepository.findByName(ERole.ROLE_NONE.toString()).isEmpty()) {
//            Role noneRole = new Role();
//            noneRole.setEntityId("001");
//            noneRole.setRoleCode("007");
//            noneRole.setManagerial(false);
//            noneRole.setPostedBy("SYSTEM");
//            noneRole.setPostedFlag('Y');
//            noneRole.setVerifiedBy("SYSTEM");
//            noneRole.setVerifiedFlag('Y');
//            noneRole.setVerifiedTime(new Date());
//            noneRole.setPostedTime(new Date());
//            noneRole.setName(ERole.ROLE_NONE.toString());
//            roleList.add(noneRole);
//        }
//        if (roleRepository.findByName(ERole.ROLE_SUPERUSER.toString()).isEmpty()) {
//            Role superuserRole = new Role();
//            superuserRole.setEntityId("001");
//            superuserRole.setRoleCode("001");
//            superuserRole.setManagerial(true);
//            superuserRole.setPostedBy("SYSTEM");
//            superuserRole.setPostedFlag('Y');
//            superuserRole.setVerifiedBy("SYSTEM");
//            superuserRole.setVerifiedFlag('Y');
//            superuserRole.setVerifiedTime(new Date());
//            superuserRole.setPostedTime(new Date());
//            superuserRole.setName(ERole.ROLE_SUPERUSER.toString());
//            roleList.add(superuserRole);
//        }
//        roleRepository.saveAll(roleList);
//    }
//
//    private void initWorkClasses() {
//        if (workclassRepo.findAll().size() < 1) {
//            Optional<Role> role = roleRepository.findByName("ROLE_SUPERUSER");
//            Role role1 = role.get();
//            Workclass workclass = new Workclass();
//            workclass.setEntityId("001");
//            workclass.setWorkClass("SUPERUSER");
//            workclass.setPostedBy("SYSTEM");
//            workclass.setPostedFlag('Y');
//            workclass.setVerifiedBy("SYSTEM");
//            workclass.setVerifiedFlag('Y');
//            workclass.setVerifiedTime(new Date());
//            workclass.setPostedTime(new Date());
//            workclass.setRoleId(role1.getId());
//            workclass.setPrivileges(initAuth.getAllPriviledges());
//            workclassRepo.save(workclass);
//            List<Privilege> privilege = workclass.getPrivileges();
//            for (int i = 0; i < privilege.size(); i++) {
//                Privilege privilege1 = privilege.get(i);
//                List<Basicactions> basicActionsList = initAuth.getAllBasicActions();
//                for (int j = 0; j < basicActionsList.size(); j++) {
//                    Basicactions basicactions = basicActionsList.get(j);
//                    Basicactions newBasicActions = new Basicactions();
//                    newBasicActions.setCode(basicactions.getCode());
//                    newBasicActions.setName(basicactions.getName());
//                    newBasicActions.setSelected(basicactions.isSelected());
//                    newBasicActions.setPostedBy("SYSTEM");
//                    newBasicActions.setPostedFlag('Y');
//                    basicactionsService.addBasicactions(basicActionsList, privilege1.getId(), workclass.getId());
//                }
//            }
//        }
//        String VIEWER = "VIEWER";
//        if (workclassRepo.findByName(VIEWER).isEmpty()) {
//            Optional<Role> role = roleRepository.findByName("ROLE_OFFICER");
//            Role role1 = role.get();
//            Workclass workclass = new Workclass();
//            workclass.setEntityId("001");
//            workclass.setWorkClass(VIEWER);
//            workclass.setPostedBy("System");
//            workclass.setPostedFlag('Y');
//            workclass.setVerifiedBy("System");
//            workclass.setVerifiedFlag('Y');
//            workclass.setVerifiedTime(new Date());
//            workclass.setPostedTime(new Date());
//            workclass.setRoleId(role1.getId());
//            workclass.setPrivileges(initAuth.getAllPriviledges());
//            workclassRepo.save(workclass);
//            List<Privilege> privilege = workclass.getPrivileges();
//            for (int i = 0; i < privilege.size(); i++) {
//                Privilege privilege1 = privilege.get(i);
//                List<Basicactions> basicactionsList = initAuth.getViewBasicActions();
//                for (int j = 0; j < basicactionsList.size(); j++) {
//                    Basicactions basicactions = basicactionsList.get(j);
//                    Basicactions basicactions1 = new Basicactions();
//                    basicactions1.setCode(basicactions.getCode());
//                    basicactions1.setName(basicactions.getName());
//                    basicactions1.setSelected(basicactions.isSelected());
//                    basicactions1.setPostedBy("System");
//                    basicactions1.setPostedFlag('Y');
//                    basicactionsService.addBasicactions(basicactionsList, privilege1.getId(), workclass.getId());
//                }
//            }
//        }
//
//        if (workclassRepo.findByName(NONE).isEmpty()) {
//            Optional<Role> role = roleRepository.findByName(ERole.ROLE_NONE.toString());
//            Role role1 = role.get();
//            Workclass workclass = new Workclass();
//            workclass.setEntityId("001");
//            workclass.setWorkClass(NONE);
//            workclass.setPostedBy("System");
//            workclass.setPostedFlag('Y');
//            workclass.setVerifiedBy("System");
//            workclass.setVerifiedFlag('Y');
//            workclass.setVerifiedTime(new Date());
//            workclass.setPostedTime(new Date());
//            workclass.setRoleId(role1.getId());
//            workclass.setPrivileges(new ArrayList<>());
//            workclassRepo.save(workclass);
//        }
//    }
//
//    private void initSuperUser() throws MessagingException {
//        if (!userRepository.existsByUsername(superUserUserName)) {
//            Set<Role> roles = new HashSet<>();
//            Role userRole = roleRepository.findByName(ERole.ROLE_SUPERUSER.toString())
//                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//            roles.add(userRole);
//            Users user = new Users();
//            user.setFirstName(superUserFirstName);
//            user.setLastName(superUserLastName);
//            user.setEmail(superUserEmail);
//            user.setPhoneNo(superUserPhone);
//            user.setSolCode(superUserSolCode);
//            user.setModifiedBy("SYSTEM");
//            user.setUsername(superUserUserName);
//            user.setRoles(roles);
//            user.setPostedTime(new Date());
//            user.setPostedFlag('Y');
//            user.setPostedBy("SYSTEM");
//            user.setIsAcctLocked(false);
//            user.setWorkclassFk(workclassRepo.findByName("SUPERUSER").get().getId());
//            user.setFirstLogin('Y');
//            user.setSolCode("001");
//            user.setEntityId("001");
//            user.setPassword(encoder.encode(superUserPassword));
//            userRepository.save(user);
//            String mailMessage = "Dear " + user.getFirstName() + " your account has been successfully created using username " + user.getUsername()
//                    + " and password " + superUserPassword + " Login in to change your password.";
//            mailService.sendEmail(user.getEmail(), mailMessage, "Account Successfully Created");
//            Mailparams mailsample = new Mailparams();
//            mailsample.setEmail(user.getEmail());
//            mailsample.setSubject("Account Successfully Created");
//            mailsample.setMessage(mailMessage);
//        } else {
//            Users user = userRepository.findByUsername(superUserUserName).get();
//            if (user.getWorkclassFk() == null || !workclassRepo.existsById(user.getWorkclassFk())) {
//                user.setWorkclassFk(workclassRepo.findByName("SUPERUSER").get().getId());
//                userRepository.save(user);
//            }
//        }
//    }
//
//    private void initOtherUsers() {
//        List<Users> users = userRepository.allWithoutWorkclass();
//        for (Users user : users) {
//            if (user.getWorkclassFk() == null || !workclassRepo.existsById(user.getWorkclassFk())) {
//                user.setWorkclassFk(workclassRepo.findByName(NONE).get().getId());
//                userRepository.save(user);
//            }
//        }
//
//        users = userRepository.allWithoutRoles();
//        for (Users user : users) {
//            Set<Role> roles = new HashSet<>();
//            Role userRole = roleRepository.findByName(ERole.ROLE_NONE.toString())
//                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//            roles.add(userRole);
//            user.setRoles(roles);
//            userRepository.save(user);
//        }
//    }
//
//    @Bean
//    CommandLineRunner runner() {
//        return args -> {
//            initSaccoEntity();
//            initRoles();
//            initWorkClasses();
//            initSuperUser();
//            initOtherUsers();
//            System.out.println("SACCO API GATEWAY INITIALIZED SUCCESSFULLY AT " + new Date());
//        };
//    }
//}