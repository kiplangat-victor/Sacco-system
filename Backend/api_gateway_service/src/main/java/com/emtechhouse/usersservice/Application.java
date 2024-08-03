package com.emtechhouse.usersservice;

import com.emtechhouse.usersservice.DTO.Mailparams;
import com.emtechhouse.usersservice.MailService.MailService;
import com.emtechhouse.usersservice.Roles.ERole;
import com.emtechhouse.usersservice.Roles.Role;
import com.emtechhouse.usersservice.Roles.RoleRepository;
import com.emtechhouse.usersservice.Roles.Workclass.Privileges.BasicActions.Basicactions;
import com.emtechhouse.usersservice.Roles.Workclass.Privileges.BasicActions.BasicactionsService;
import com.emtechhouse.usersservice.Roles.Workclass.Privileges.Privilege;
import com.emtechhouse.usersservice.Roles.Workclass.Privileges.PrivilegeRepo;
import com.emtechhouse.usersservice.Roles.Workclass.Workclass;
import com.emtechhouse.usersservice.Roles.Workclass.WorkclassRepo;
import com.emtechhouse.usersservice.SaccoEntity.SaccoEntity;
import com.emtechhouse.usersservice.SaccoEntity.SaccoEntityRepository;
import com.emtechhouse.usersservice.Users.Users;
import com.emtechhouse.usersservice.Users.UsersController;
import com.emtechhouse.usersservice.Users.UsersRepository;
import com.emtechhouse.usersservice.utils.ImageToBase64;
import com.emtechhouse.usersservice.utils.InitAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;


import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@SpringBootApplication
@EnableZuulProxy
@EnableDiscoveryClient
public class Application {
    @Autowired
    private UsersRepository userRepository;
    @Autowired
    SaccoEntityRepository saccoEntityRepository;
    @Autowired
    WorkclassRepo workclassRepo;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    InitAuth initAuth;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    MailService mailService;
    @Autowired
    private UsersController usersController;
    @Autowired
    private ImageToBase64 imageToBase64;

    @Autowired
    private PrivilegeRepo privilegeRepository;
    @Autowired
    private BasicactionsService basicactionsService;
    @Value("${organisation.superUserEmail}")
    private String superUserEmail;
    @Value("${organisation.superUserFirstName}")
    private String superUserFirstName;
    @Value("${organisation.superUserLastName}")
    private String superUserLastName;
    @Value("${organisation.superUserUserName}")
    private String superUserUserName;
    @Value("${organisation.superUserPhone}")
    private String superUserPhone;
    @Value("${organisation.superUserSolCode}")
    private String superUserSolCode;
    @Value("${organisation.superUserPassword}")
    private String superUserPassword;
    String NONE = "NONE";

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    private void initEntity() {
        if (saccoEntityRepository.findAll().size() < 1) {
            SaccoEntity entityGroup = new SaccoEntity();
            entityGroup.setEntityId("01");
            entityGroup.setEntityDescription("Main Entity");
            entityGroup.setEntityName("Main Entity");
            entityGroup.setEntityLocation("Kenya");
            entityGroup.setEntityPhoneNumber("");
            entityGroup.setPostedBy("System");
            entityGroup.setPostedFlag('Y');
            entityGroup.setVerifiedBy("System");
            entityGroup.setVerifiedFlag('Y');
            entityGroup.setVerifiedOn(LocalDate.now());
            entityGroup.setPostedOn(LocalDate.now());
            entityGroup.setProtocol("smtps");
            entityGroup.setHost("mail.emtechhouse.co.ke");
            entityGroup.setPort(465);
            entityGroup.setSmtpUsername("no-reply@emtechhouse.co.ke");
            entityGroup.setSmtpPassword("Pass123");
            entityGroup.setSmtpAuth(true);
            entityGroup.setSmtpStarttlsEnable(true);
            entityGroup.setSslTrust("mail.emtechhouse.co.ke");
            entityGroup.setCustomSidebarBg("#FFFFFF");
            entityGroup.setCustomTitlebarBg("#0000FF");
            entityGroup.setEmailRemarks("Kind Regards");
            entityGroup.setEntityEmail("no-reply@emtechhouse.co.ke");
            entityGroup.setEmailRegards("E&M Technology");
            entityGroup.setEntityPhoneNumber("07******");
            entityGroup.setEntityAddress("P.O BOX 11001-00100 GPO, Nairobi, Kenya. ");
            entityGroup.setEntityWebsite("www.emtechhouse.co.ke");
            entityGroup.setEntityImageLogo(imageToBase64.convertImageToBase64("e&m-logo.png"));
            entityGroup.setEntityImageBanner(imageToBase64.convertImageToBase64("e&m_banner.jpg"));
            saccoEntityRepository.save(entityGroup);
        }
    }
    private void initRoles() {
        List<Role> currentRoles = roleRepository.findAll();
        List<Role> roleList = new ArrayList<>();
        if (roleRepository.findByEntityIdAndNameAndDeletedFlag("001",ERole.ROLE_USER.toString(),'N').isEmpty()) {
            Role userRole = new Role();
            userRole.setName(ERole.ROLE_USER.toString());
            userRole.setEntityId("001");
            userRole.setPostedBy("System");
            userRole.setPostedFlag('Y');
            userRole.setRoleCode(String.valueOf(System.currentTimeMillis()));
            userRole.setVerifiedBy("System");
            userRole.setVerifiedFlag('Y');
            userRole.setVerifiedTime(new Date());
            userRole.setPostedTime(new Date());
            roleList.add(userRole);
        }
        if (roleRepository.findByEntityIdAndNameAndDeletedFlag("001",ERole.ROLE_TELLER.toString(),'N').isEmpty()) {
            Role tellerRole = new Role();
            tellerRole.setEntityId("001");
            tellerRole.setPostedBy("System");
            tellerRole.setPostedFlag('Y');
            tellerRole.setVerifiedBy("System");
            tellerRole.setRoleCode(String.valueOf(System.currentTimeMillis()));
            tellerRole.setVerifiedFlag('Y');
            tellerRole.setVerifiedTime(new Date());
            tellerRole.setPostedTime(new Date());
            tellerRole.setName(ERole.ROLE_TELLER.toString());
            roleList.add(tellerRole);
        }
        if (roleRepository.findByEntityIdAndNameAndDeletedFlag("001",ERole.ROLE_OFFICER.toString(),'N').isEmpty()) {
            Role officerRole = new Role();
            officerRole.setEntityId("001");
            officerRole.setPostedBy("System");
            officerRole.setPostedFlag('Y');
            officerRole.setRoleCode(String.valueOf(System.currentTimeMillis()));
            officerRole.setVerifiedBy("System");
            officerRole.setVerifiedFlag('Y');
            officerRole.setVerifiedTime(new Date());
            officerRole.setPostedTime(new Date());
            officerRole.setName(ERole.ROLE_OFFICER.toString());
            roleList.add(officerRole);
        }

        if (roleRepository.findByEntityIdAndNameAndDeletedFlag("001",ERole.ROLE_SENIOR_OFFICER.toString(),'N').isEmpty()) {
            Role seniorOfficerRole = new Role();
            seniorOfficerRole.setEntityId("001");
            seniorOfficerRole.setPostedBy("System");
            seniorOfficerRole.setPostedFlag('Y');
            seniorOfficerRole.setVerifiedBy("System");
            seniorOfficerRole.setRoleCode(String.valueOf(System.currentTimeMillis()));
            seniorOfficerRole.setVerifiedFlag('Y');
            seniorOfficerRole.setVerifiedTime(new Date());
            seniorOfficerRole.setPostedTime(new Date());
            seniorOfficerRole.setName(ERole.ROLE_SENIOR_OFFICER.toString());
            roleList.add(seniorOfficerRole);
        }
        if (roleRepository.findByEntityIdAndNameAndDeletedFlag("001",ERole.ROLE_MANAGER.toString(),'N').isEmpty()) {
            Role managerRole = new Role();
            managerRole.setEntityId("001");
            managerRole.setPostedBy("System");
            managerRole.setPostedFlag('Y');
            managerRole.setVerifiedBy("System");
            managerRole.setVerifiedFlag('Y');
            managerRole.setRoleCode(String.valueOf(System.currentTimeMillis()));
            managerRole.setVerifiedTime(new Date());
            managerRole.setPostedTime(new Date());
            managerRole.setName(ERole.ROLE_MANAGER.toString());
            roleList.add(managerRole);
        }

        if (roleRepository.findByEntityIdAndNameAndDeletedFlag("001",ERole.ROLE_NONE.toString(),'N').isEmpty()) {
            Role noneRole = new Role();
            noneRole.setEntityId("001");
            noneRole.setPostedBy("System");
            noneRole.setPostedFlag('Y');
            noneRole.setVerifiedBy("System");
            noneRole.setVerifiedFlag('Y');
            noneRole.setRoleCode(String.valueOf(System.currentTimeMillis()));
            noneRole.setVerifiedTime(new Date());
            noneRole.setPostedTime(new Date());
            noneRole.setName(ERole.ROLE_NONE.toString());
            roleList.add(noneRole);
        }

        if (roleRepository.findByEntityIdAndNameAndDeletedFlag("001",ERole.ROLE_SUPERUSER.toString(), 'N').isEmpty()) {
            Role superuserRole = new Role();
            superuserRole.setEntityId("001");
            superuserRole.setPostedBy("System");
            superuserRole.setPostedFlag('Y');
            superuserRole.setVerifiedBy("System");
            superuserRole.setRoleCode(String.valueOf(System.currentTimeMillis()));
            superuserRole.setVerifiedFlag('Y');
            superuserRole.setVerifiedTime(new Date());
            superuserRole.setPostedTime(new Date());
            superuserRole.setName(ERole.ROLE_SUPERUSER.toString());
            roleList.add(superuserRole);
        }
        roleRepository.saveAll(roleList);
    }

    private void initWorkClasses() {
        if (workclassRepo.findAll().size() < 1) {
            Optional<Role> role = roleRepository.findByEntityIdAndNameAndDeletedFlag("001","ROLE_SUPERUSER",'N');
            Role role1 = role.get();
            Workclass workclass = new Workclass();
            workclass.setEntityId("001");
            workclass.setWorkClass("SUPERUSER");
            workclass.setPostedBy("System");
            workclass.setPostedFlag('Y');
            workclass.setVerifiedBy("System");
            workclass.setVerifiedFlag('Y');
            workclass.setVerifiedTime(new Date());
            workclass.setPostedTime(new Date());
            workclass.setRoleId(role1.getId());
            workclass.setPrivileges(initAuth.getAllPriviledges());
            workclassRepo.save(workclass);
            List<Privilege> privilege = workclass.getPrivileges();
            for (int i = 0; i < privilege.size(); i++) {
                Privilege privilege1 = privilege.get(i);
                List<Basicactions> basicactionsList = initAuth.getAllBasicActions();
                for (int j = 0; j < basicactionsList.size(); j++) {
                    Basicactions basicactions = basicactionsList.get(j);
                    Basicactions basicactions1 = new Basicactions();
                    basicactions1.setCode(basicactions.getCode());
                    basicactions1.setName(basicactions.getName());
                    basicactions1.setSelected(basicactions.isSelected());
                    basicactions1.setPostedBy("System");
                    basicactions1.setPostedFlag('Y');
                    basicactionsService.addBasicactions(basicactionsList, privilege1.getId(), workclass.getId());
                }
            }
        }
        String VIEWER = "VIEWER";
        if (workclassRepo.findByName(VIEWER).isEmpty()) {
            Optional<Role> role = roleRepository.findByEntityIdAndNameAndDeletedFlag("001","ROLE_OFFICER",'N');
            Role role1 = role.get();
            Workclass workclass = new Workclass();
            workclass.setEntityId("001");
            workclass.setWorkClass(VIEWER);
            workclass.setPostedBy("System");
            workclass.setPostedFlag('Y');
            workclass.setVerifiedBy("System");
            workclass.setVerifiedFlag('Y');
            workclass.setVerifiedTime(new Date());
            workclass.setPostedTime(new Date());
            workclass.setRoleId(role1.getId());
            workclass.setPrivileges(initAuth.getAllPriviledges());
            workclassRepo.save(workclass);
            List<Privilege> privilege = workclass.getPrivileges();
            for (int i = 0; i < privilege.size(); i++) {
                Privilege privilege1 = privilege.get(i);
                List<Basicactions> basicactionsList = initAuth.getViewBasicActions();
                for (int j = 0; j < basicactionsList.size(); j++) {
                    Basicactions basicactions = basicactionsList.get(j);
                    Basicactions basicactions1 = new Basicactions();
                    basicactions1.setCode(basicactions.getCode());
                    basicactions1.setName(basicactions.getName());
                    basicactions1.setSelected(basicactions.isSelected());
                    basicactions1.setPostedBy("System");
                    basicactions1.setPostedFlag('Y');
                    basicactionsService.addBasicactions(basicactionsList, privilege1.getId(), workclass.getId());
                }
            }
        }

        if (workclassRepo.findByName(NONE).isEmpty()) {
            Optional<Role> role = roleRepository.findByEntityIdAndNameAndDeletedFlag("001",ERole.ROLE_NONE.toString(),'N');
            Role role1 = role.get();
            Workclass workclass = new Workclass();
            workclass.setEntityId("001");
            workclass.setWorkClass(NONE);
            workclass.setPostedBy("System");
            workclass.setPostedFlag('Y');
            workclass.setVerifiedBy("System");
            workclass.setVerifiedFlag('Y');
            workclass.setVerifiedTime(new Date());
            workclass.setPostedTime(new Date());
            workclass.setRoleId(role1.getId());
            workclass.setPrivileges(new ArrayList<>());
            workclassRepo.save(workclass);
        }
    }

    private void initSuperUser() throws MessagingException, IOException {
        if (!userRepository.existsByUsername(superUserUserName)) {
            Set<Role> roles = new HashSet<>();
            Role userRole = roleRepository.findByEntityIdAndNameAndDeletedFlag("001",ERole.ROLE_SUPERUSER.toString(),'N')
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
            Users user = new Users();
            user.setFirstName(superUserFirstName);
            user.setLastName(superUserLastName);
            user.setEmail(superUserEmail);
            user.setPhoneNo(superUserPhone);
            user.setSolCode(superUserSolCode);
            user.setModifiedBy("");
            user.setUsername(superUserUserName);
            user.setRoles(roles);
            user.setPostedTime(new Date());
            user.setIsEntityUser("N");
            user.setPostedFlag('Y');
            user.setPostedBy("System");
            user.setIsAcctLocked(false);
            user.setWorkclassFk(workclassRepo.findByName("SUPERUSER").get().getId());
            user.setFirstLogin('Y');
            user.setSolCode("001");
            user.setEntityId("001");
            user.setPassword(encoder.encode(superUserPassword));
            userRepository.save(user);

            String mailMessage = "<p>Dear"+ user.getFirstName() +",</p>\n" +
                    "    \n" +
                    "    <p>Your account has been successfully created. Find the credentials below:</p>\n" +
                    "    \n" +
                    "    <ul>\n" +
                    "        <li><strong>Username:</strong> "+ user.getUsername()+"</li>\n" +
                    "        <li><strong>Password:</strong> "+superUserPassword+"</li>\n" +
                    "    </ul>\n" +
                    "    \n" +
                    "    <p>Login to update your password.</p>";




            mailService.sendEmail(user.getEntityId(), user.getEmail(), mailMessage, "Account Successfully Created");
            Mailparams mailsample = new Mailparams();
            mailsample.setEmail(user.getEmail());
            mailsample.setSubject("Account Successfully Created");
            mailsample.setMessage(mailMessage);
        } else {
            Users user = userRepository.findByUsername(superUserUserName).get();
            if (user.getWorkclassFk() == null || !workclassRepo.existsById(user.getWorkclassFk())) {
                user.setWorkclassFk(workclassRepo.findByName("SUPERUSER").get().getId());
                userRepository.save(user);
            }
        }
    }

    private void initOtherUsers(String entityId) {
        List<Users> users = userRepository.allWithoutWorkclass(entityId);
        for (Users user : users) {
            if (user.getWorkclassFk() == null || !workclassRepo.existsById(user.getWorkclassFk())) {
                user.setWorkclassFk(workclassRepo.findByName(NONE).get().getId());
                userRepository.save(user);
            }
        }

        users = userRepository.allWithoutRoles(entityId);
        for (Users user : users) {
            Set<Role> roles = new HashSet<>();
            Role userRole = roleRepository.findByEntityIdAndNameAndDeletedFlag("001",ERole.ROLE_NONE.toString(),'N')
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
            user.setRoles(roles);
            userRepository.save(user);
        }
    }

    @Bean
    CommandLineRunner runner() {
        return args -> {
            initEntity();
            initRoles();
            initWorkClasses();
            initSuperUser();
            initOtherUsers("1");
            System.out.println("API GATEWAY INITIALIZED SUCCESSFULLY AT " + new Date());
        };
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Allow all origins, methods, and headers. You may want to restrict this in production.
        config.addAllowedOrigin("*");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }


}