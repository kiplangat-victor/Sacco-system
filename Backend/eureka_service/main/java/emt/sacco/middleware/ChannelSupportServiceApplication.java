package emt.sacco.middleware;
import com.fasterxml.jackson.databind.ObjectMapper;
//import emt.sacco.middleware.Utils.RequestResponseLongs.RequestResponseLogs.RequestResponseLoggingFilter;
import emt.sacco.middleware.SecurityImpl.SaccoEntity.SSaccoEntity;
import emt.sacco.middleware.SecurityImpl.SaccoEntity.SaccoEntityRepository;
import emt.sacco.middleware.SecurityImpl.Sec.SwitchUsers;
import emt.sacco.middleware.SecurityImpl.enumeration.Roles.ERole;
import emt.sacco.middleware.SecurityImpl.enumeration.Roles.SRole;
import emt.sacco.middleware.SecurityImpl.enumeration.Roles.RoleRepository;
import emt.sacco.middleware.SecurityImpl.enumeration.Roles.Workclass.Privileges.BasicActions.SBasicactions;
import emt.sacco.middleware.SecurityImpl.enumeration.Roles.Workclass.Privileges.BasicActions.BasicactionsService;
import emt.sacco.middleware.SecurityImpl.enumeration.Roles.Workclass.Privileges.SPrivilege;
import emt.sacco.middleware.SecurityImpl.enumeration.Roles.Workclass.Privileges.PrivilegeRepo;
import emt.sacco.middleware.SecurityImpl.enumeration.Roles.Workclass.SWorkclass;
import emt.sacco.middleware.SecurityImpl.enumeration.Roles.Workclass.WorkclassRepo;
import emt.sacco.middleware.SecurityImpl.resource.NewController;
import emt.sacco.middleware.SecurityImpl.resource.UsersRepository;
import emt.sacco.middleware.Utils.InitAuth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
@SpringBootApplication
@Slf4j
public class  ChannelSupportServiceApplication {
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
//    @Autowired
//    MailService mailService;
    @Autowired
    private NewController usersController;
//    @Autowired
//    private ImageToBase64 imageToBase64;

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
    @Bean
    public ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }

    public static void main(String... args) {
        SpringApplication.run(ChannelSupportServiceApplication.class, args);

        System.out.println("CHANNEL-SUPPORT-SERVICE INITIALIZED SUCCESSFULLY AT " + new Date());
    }

    private void initEntity() {
        if (saccoEntityRepository.findAll().size() < 1) {
            SSaccoEntity entityGroup = new SSaccoEntity();
            entityGroup.setEntityId("100");
            entityGroup.setEntityDescription("switch Entity");
            entityGroup.setEntityName("switch Entity");
            entityGroup.setEntityLocation("Kenya");
            entityGroup.setEntityPhoneNumber("");
            entityGroup.setPostedBy("switch");
            entityGroup.setPostedFlag('Y');
            entityGroup.setVerifiedBy("switch");
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
           // entityGroup.setEntityImageLogo(imageToBase64.convertImageToBase64("e&m-logo.png"));
         //   entityGroup.setEntityImageBanner(imageToBase64.convertImageToBase64("e&m_banner.jpg"));
            saccoEntityRepository.save(entityGroup);
        }
    }
    private void initRoles() {
        List<SRole> currentSRoles = roleRepository.findAll();
        List<SRole> SRoleList = new ArrayList<>();
        if (roleRepository.findByEntityIdAndNameAndDeletedFlag("100", ERole.ROLE_USER.toString(),'N').isEmpty()) {
            SRole userSRole = new SRole();
            userSRole.setName(ERole.ROLE_USER.toString());
            userSRole.setEntityId("100");
            userSRole.setPostedBy("System");
            userSRole.setPostedFlag('Y');
            userSRole.setRoleCode(String.valueOf(System.currentTimeMillis()));
            userSRole.setVerifiedBy("System");
            userSRole.setVerifiedFlag('Y');
            userSRole.setVerifiedTime(new Date());
            userSRole.setPostedTime(new Date());
            SRoleList.add(userSRole);
        }
        if (roleRepository.findByEntityIdAndNameAndDeletedFlag("100",ERole.ROLE_TELLER.toString(),'N').isEmpty()) {
            SRole tellerSRole = new SRole();
            tellerSRole.setEntityId("100");
            tellerSRole.setPostedBy("System");
            tellerSRole.setPostedFlag('Y');
            tellerSRole.setVerifiedBy("System");
            tellerSRole.setRoleCode(String.valueOf(System.currentTimeMillis()));
            tellerSRole.setVerifiedFlag('Y');
            tellerSRole.setVerifiedTime(new Date());
            tellerSRole.setPostedTime(new Date());
            tellerSRole.setName(ERole.ROLE_TELLER.toString());
            SRoleList.add(tellerSRole);
        }
        if (roleRepository.findByEntityIdAndNameAndDeletedFlag("100",ERole.ROLE_OFFICER.toString(),'N').isEmpty()) {
            SRole officerSRole = new SRole();
            officerSRole.setEntityId("100");
            officerSRole.setPostedBy("System");
            officerSRole.setPostedFlag('Y');
            officerSRole.setRoleCode(String.valueOf(System.currentTimeMillis()));
            officerSRole.setVerifiedBy("System");
            officerSRole.setVerifiedFlag('Y');
            officerSRole.setVerifiedTime(new Date());
            officerSRole.setPostedTime(new Date());
            officerSRole.setName(ERole.ROLE_OFFICER.toString());
            SRoleList.add(officerSRole);
        }

        if (roleRepository.findByEntityIdAndNameAndDeletedFlag("100",ERole.ROLE_SENIOR_OFFICER.toString(),'N').isEmpty()) {
            SRole seniorOfficerSRole = new SRole();
            seniorOfficerSRole.setEntityId("100");
            seniorOfficerSRole.setPostedBy("System");
            seniorOfficerSRole.setPostedFlag('Y');
            seniorOfficerSRole.setVerifiedBy("System");
            seniorOfficerSRole.setRoleCode(String.valueOf(System.currentTimeMillis()));
            seniorOfficerSRole.setVerifiedFlag('Y');
            seniorOfficerSRole.setVerifiedTime(new Date());
            seniorOfficerSRole.setPostedTime(new Date());
            seniorOfficerSRole.setName(ERole.ROLE_SENIOR_OFFICER.toString());
            SRoleList.add(seniorOfficerSRole);
        }
        if (roleRepository.findByEntityIdAndNameAndDeletedFlag("100",ERole.ROLE_MANAGER.toString(),'N').isEmpty()) {
            SRole managerSRole = new SRole();
            managerSRole.setEntityId("100");
            managerSRole.setPostedBy("System");
            managerSRole.setPostedFlag('Y');
            managerSRole.setVerifiedBy("System");
            managerSRole.setVerifiedFlag('Y');
            managerSRole.setRoleCode(String.valueOf(System.currentTimeMillis()));
            managerSRole.setVerifiedTime(new Date());
            managerSRole.setPostedTime(new Date());
            managerSRole.setName(ERole.ROLE_MANAGER.toString());
            SRoleList.add(managerSRole);
        }

        if (roleRepository.findByEntityIdAndNameAndDeletedFlag("100",ERole.ROLE_NONE.toString(),'N').isEmpty()) {
            SRole noneSRole = new SRole();
            noneSRole.setEntityId("100");
            noneSRole.setPostedBy("System");
            noneSRole.setPostedFlag('Y');
            noneSRole.setVerifiedBy("System");
            noneSRole.setVerifiedFlag('Y');
            noneSRole.setRoleCode(String.valueOf(System.currentTimeMillis()));
            noneSRole.setVerifiedTime(new Date());
            noneSRole.setPostedTime(new Date());
            noneSRole.setName(ERole.ROLE_NONE.toString());
            SRoleList.add(noneSRole);
        }

        if (roleRepository.findByEntityIdAndNameAndDeletedFlag("100",ERole.ROLE_SUPERUSER.toString(), 'N').isEmpty()) {
            SRole superuserSRole = new SRole();
            superuserSRole.setEntityId("100");
            superuserSRole.setPostedBy("System");
            superuserSRole.setPostedFlag('Y');
            superuserSRole.setVerifiedBy("System");
            superuserSRole.setRoleCode(String.valueOf(System.currentTimeMillis()));
            superuserSRole.setVerifiedFlag('Y');
            superuserSRole.setVerifiedTime(new Date());
            superuserSRole.setPostedTime(new Date());
            superuserSRole.setName(ERole.ROLE_SUPERUSER.toString());
            SRoleList.add(superuserSRole);
        }
        roleRepository.saveAll(SRoleList);
    }

    private void initWorkClasses() {
        if (workclassRepo.findAll().size() < 1) {
            Optional<SRole> role = roleRepository.findByEntityIdAndNameAndDeletedFlag("100","ROLE_SUPERUSER",'N');
            SRole SRole1 = role.get();
            SWorkclass SWorkclass = new SWorkclass();
            SWorkclass.setEntityId("100");
            SWorkclass.setWorkClass("SUPERUSER");
            SWorkclass.setPostedBy("System");
            SWorkclass.setPostedFlag('Y');
            SWorkclass.setVerifiedBy("System");
            SWorkclass.setVerifiedFlag('Y');
            SWorkclass.setVerifiedTime(new Date());
            SWorkclass.setPostedTime(new Date());
            SWorkclass.setRoleId(SRole1.getId());
            SWorkclass.setSPrivileges(initAuth.getAllPriviledges());
            workclassRepo.save(SWorkclass);
            List<SPrivilege> SPrivilege = SWorkclass.getSPrivileges();
            for (int i = 0; i < SPrivilege.size(); i++) {
                SPrivilege SPrivilege1 = SPrivilege.get(i);
                List<SBasicactions> SBasicactionsList = initAuth.getAllBasicActions();
                for (int j = 0; j < SBasicactionsList.size(); j++) {
                    SBasicactions SBasicactions = SBasicactionsList.get(j);
                    SBasicactions SBasicactions1 = new SBasicactions();
                    SBasicactions1.setCode(SBasicactions.getCode());
                    SBasicactions1.setName(SBasicactions.getName());
                    SBasicactions1.setSelected(SBasicactions.isSelected());
                    SBasicactions1.setPostedBy("System");
                    SBasicactions1.setPostedFlag('Y');
                    basicactionsService.addBasicactions(SBasicactionsList, SPrivilege1.getId(), SWorkclass.getId());
                }
            }
        }
        String VIEWER = "VIEWER";
        if (workclassRepo.findByName(VIEWER).isEmpty()) {
            Optional<SRole> role = roleRepository.findByEntityIdAndNameAndDeletedFlag("100","ROLE_OFFICER",'N');
            SRole SRole1 = role.get();
            SWorkclass SWorkclass = new SWorkclass();
            SWorkclass.setEntityId("100");
            SWorkclass.setWorkClass(VIEWER);
            SWorkclass.setPostedBy("System");
            SWorkclass.setPostedFlag('Y');
            SWorkclass.setVerifiedBy("System");
            SWorkclass.setVerifiedFlag('Y');
            SWorkclass.setVerifiedTime(new Date());
            SWorkclass.setPostedTime(new Date());
            SWorkclass.setRoleId(SRole1.getId());
            SWorkclass.setSPrivileges(initAuth.getAllPriviledges());
            workclassRepo.save(SWorkclass);
            List<SPrivilege> SPrivilege = SWorkclass.getSPrivileges();
            for (int i = 0; i < SPrivilege.size(); i++) {
                SPrivilege SPrivilege1 = SPrivilege.get(i);
                List<SBasicactions> SBasicactionsList = initAuth.getViewBasicActions();
                for (int j = 0; j < SBasicactionsList.size(); j++) {
                    SBasicactions SBasicactions = SBasicactionsList.get(j);
                    SBasicactions SBasicactions1 = new SBasicactions();
                    SBasicactions1.setCode(SBasicactions.getCode());
                    SBasicactions1.setName(SBasicactions.getName());
                    SBasicactions1.setSelected(SBasicactions.isSelected());
                    SBasicactions1.setPostedBy("System");
                    SBasicactions1.setPostedFlag('Y');
                    basicactionsService.addBasicactions(SBasicactionsList, SPrivilege1.getId(), SWorkclass.getId());
                }
            }
        }

        if (workclassRepo.findByName(NONE).isEmpty()) {
            Optional<SRole> role = roleRepository.findByEntityIdAndNameAndDeletedFlag("100",ERole.ROLE_NONE.toString(),'N');
            SRole SRole1 = role.get();
            SWorkclass SWorkclass = new SWorkclass();
            SWorkclass.setEntityId("100");
            SWorkclass.setWorkClass(NONE);
            SWorkclass.setPostedBy("System");
            SWorkclass.setPostedFlag('Y');
            SWorkclass.setVerifiedBy("System");
            SWorkclass.setVerifiedFlag('Y');
            SWorkclass.setVerifiedTime(new Date());
            SWorkclass.setPostedTime(new Date());
            SWorkclass.setRoleId(SRole1.getId());
            SWorkclass.setSPrivileges(new ArrayList<>());
            workclassRepo.save(SWorkclass);
        }
    }

    private void initSuperUser() throws MessagingException, IOException {
        if (!userRepository.existsByUsername(superUserUserName)) {
            Set<SRole> SRoles = new HashSet<>();
            SRole userSRole = roleRepository.findByEntityIdAndNameAndDeletedFlag("100",ERole.ROLE_SUPERUSER.toString(),'N')
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            SRoles.add(userSRole);
            SwitchUsers user = new SwitchUsers();
            user.setFirstName(superUserFirstName);
            user.setLastName(superUserLastName);
            user.setEmail(superUserEmail);
            user.setPhoneNo(superUserPhone);
            user.setSolCode(superUserSolCode);
            user.setModifiedBy("");
            user.setUsername(superUserUserName);
            user.setSRoles(SRoles);
            user.setPostedTime(new Date());
            user.setIsEntityUser("N");
            user.setPostedFlag('Y');
            user.setPostedBy("System");
            user.setIsAcctLocked(false);
            user.setWorkclassFk(workclassRepo.findByName("SUPERUSER").get().getId());
            user.setFirstLogin('Y');
            user.setSolCode("100");
            user.setEntityId("100");
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




//            mailService.sendEmail(user.getEntityId(), user.getEmail(), mailMessage, "Account Successfully Created");
//            Mailparams mailsample = new Mailparams();
//            mailsample.setEmail(user.getEmail());
//            mailsample.setSubject("Account Successfully Created");
//            mailsample.setMessage(mailMessage);
        } else {
            SwitchUsers user = userRepository.findByUsername(superUserUserName).get();
            if (user.getWorkclassFk() == null || !workclassRepo.existsById(user.getWorkclassFk())) {
                user.setWorkclassFk(workclassRepo.findByName("SUPERUSER").get().getId());
                userRepository.save(user);
            }
        }
    }

    private void initOtherUsers(String entityId) {
        List<SwitchUsers> users = userRepository.allWithoutSWorkclass(entityId);
        for (SwitchUsers user : users) {
            if (user.getWorkclassFk() == null || !workclassRepo.existsById(user.getWorkclassFk())) {
                user.setWorkclassFk(workclassRepo.findByName(NONE).get().getId());
                userRepository.save(user);
            }
        }

        users = userRepository.allWithoutRoles(entityId);
        for (SwitchUsers user : users) {
            Set<SRole> SRoles = new HashSet<>();
            SRole userSRole = roleRepository.findByEntityIdAndNameAndDeletedFlag("100",ERole.ROLE_NONE.toString(),'N')
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            SRoles.add(userSRole);
            user.setSRoles(SRoles);
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
        };
    }

}
