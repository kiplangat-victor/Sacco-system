package com.emtechhouse.usersservice.Users;
import com.emtechhouse.usersservice.DTO.MailDto;
import com.emtechhouse.usersservice.DTO.Mailparams;
import com.emtechhouse.usersservice.MailService.MailService;
import com.emtechhouse.usersservice.OTP.OTP;
import com.emtechhouse.usersservice.OTP.OTPRepository;
import com.emtechhouse.usersservice.OTP.OTPService;
import com.emtechhouse.usersservice.Responses.SignupResponse;
import com.emtechhouse.usersservice.Roles.ERole;
import com.emtechhouse.usersservice.Roles.Role;
import com.emtechhouse.usersservice.Roles.RoleRepository;
import com.emtechhouse.usersservice.Requests.*;
import com.emtechhouse.usersservice.Responses.JwtResponse;
import com.emtechhouse.usersservice.Responses.MessageResponse;
import com.emtechhouse.usersservice.Roles.Workclass.Workclass;
import com.emtechhouse.usersservice.Roles.Workclass.WorkclassRepo;
import com.emtechhouse.usersservice.Security.jwt.Clientinformation;
import com.emtechhouse.usersservice.Security.jwt.CurrentUserContext;
import com.emtechhouse.usersservice.Security.jwt.JwtUtils;
import com.emtechhouse.usersservice.Security.services.UserDetailsImpl;
import com.emtechhouse.usersservice.Tellersaccounts.Telleraccount;
import com.emtechhouse.usersservice.Tellersaccounts.TelleraccountRepo;
import com.emtechhouse.usersservice.Users.AuthSessions.AuthSessionService;
import com.emtechhouse.usersservice.utils.EntityResponse;
import com.emtechhouse.usersservice.utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.usersservice.utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.usersservice.utils.PasswordGeneratorUtil;
import com.emtechhouse.usersservice.utils.ServiceCaller;
import com.emtechhouse.usersservice.utils.Session.Activesession;
import com.emtechhouse.usersservice.utils.Session.SessionManager;
import com.emtechhouse.usersservice.utils.exception.InvalidRequestParameterException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.passay.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.time.Instant;
import java.util.*;


@RestController
@RequestMapping("/auth")
@Slf4j
public class UsersController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    AuthSessionService authSessionService;

    @Autowired
    MailService mailService;

    @Autowired
    Clientinformation clientinformation;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private WorkclassRepo workclassRepo;

    @Autowired
    UsersRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    OTPService otpService;
    @Autowired
    ServiceCaller serviceCaller;

    @Autowired
    UsersService usersService;

    @Value("${spring.application.useOTP}")
    private String useOTP;
    @Value("${spring.application.otpProd}")
    private String otpProd;
    @Value("${spring.application.otpTestMail}")
    private String otpTestMail;
    @Autowired
    OTPRepository otpRepository;
    @Autowired
    TelleraccountRepo telleraccountRepo;

    @Value("${organisation.maxNoOfTellers}")
    private Integer maxNoOfTellers;

//    @PostMapping(path = "/otp/createOTP-collateral")
//    public ResponseEntity<?> createOTPCollateral(@RequestParam String collateralCode, @RequestParam String email) throws MessagingException {
////        call crm
////        resp Phone and Email
//        String otp = "Your otp code is " + otpService.generateOTP(collateralCode,email);
////        proceed to save
//        Mailparams mailsample = new Mailparams();
//        mailsample.setEmail("paulnjoroge430@gmail.com");
//        mailsample.setSubject("OTP Code");
//        mailsample.setMessage(otp);
//        mailService.sendEmail(email, otp, "OTP");
//
//        EntityResponse res =new EntityResponse();
//        res.setStatusCode(HttpStatus.CREATED.value());
//        res.setMessage(otp);
//        res.setEntity(otp);
//        return ResponseEntity.ok(res);
//    }

    @GetMapping(path = "/otp/verifyOTP-collateral")
    public ResponseEntity<?> validateOTPCollateral(@RequestParam String collateralCode, @RequestParam Integer otpCode) {
        EntityResponse res =new EntityResponse();
        OTP otp = otpRepository.validOTP(collateralCode);
        if (Objects.isNull(otp) || !Objects.equals(otp.getOtp(), otpCode)) {
            res.setMessage("OTP is not valid!");
            res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            res.setEntity(otp);
        } else {
            res.setMessage("OTP is  valid!");
            res.setStatusCode(HttpStatus.OK.value());
            res.setEntity(otp);
        }
        return ResponseEntity.ok(res);
    }

    @PostMapping(path = "/send/email")
    public ResponseEntity<?> sendAnEmail(@RequestBody MailDto mail) {
        serviceCaller.sendEmail(mail);
        return ResponseEntity.ok("res");
    }
    @PostMapping("/signin")
    public EntityResponse authenticateUser(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) throws MessagingException {
        EntityResponse response = new EntityResponse();
        Users user = userRepository.findByUsername(loginRequest.getUsername()).orElse(null);
        if (user == null) {
            response.setMessage("Account Access Restricted! Check with the  System Admin");
            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
            return response;
        } else {
//        Check if Account is Locked
            if (user.isAcctLocked) {
                response.setMessage("Account is Locked!");
                response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                return response;
            } else {
                if (user.getDeletedFlag() == 'Y') {
                    response.setMessage("This account has been deleted!");
                    response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                    return response;
                } else {
                    Authentication authentication = authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    String jwt = jwtUtils.generateJwtToken(authentication);
                    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                    Workclass workclass = null;
//                    get workclass from user
                    Optional<Workclass> workclass1 = workclassRepo.findById(user.getWorkclassFk());
                    if (workclass1.isPresent()){
                        workclass = workclass1.get();
                    }
                    Set<Role> roles = user.getRoles();
                    String otp = "Your otp code is " + otpService.generateOTP(userDetails.getUsername());
                    JwtResponse jwtResponse = new JwtResponse();
                    jwtResponse.setToken(jwt);
                    jwtResponse.setId(userDetails.getId().longValue());
                    jwtResponse.setUsername(loginRequest.getUsername());
                    jwtResponse.setEmail(userDetails.getEmail());
                    jwtResponse.setSolCode(user.getSolCode());
                    jwtResponse.setEntityId(user.getEntityId());
                    jwtResponse.setFirstName(user.getFirstName());
                    jwtResponse.setLastName(user.getLastName());
                    jwtResponse.setFirstLogin(user.getFirstLogin());
                    jwtResponse.setMemberCode(user.getMemberCode());
                    jwtResponse.setIsSystemGenPassword(user.getIsSystemGenPassword());
                    jwtResponse.setRoles(roles);
                    jwtResponse.setWorkclasses(workclass);
                    jwtResponse.setUuid(CurrentUserContext.getCurrentActiveUser().getUuid());
                    jwtResponse.setStatus(CurrentUserContext.getCurrentActiveUser().getStatus());
                    jwtResponse.setLoginAt(CurrentUserContext.getCurrentActiveUser().getLoginAt());
                    jwtResponse.setAddress(CurrentUserContext.getCurrentActiveUser().getAddress());
                    jwtResponse.setOs(CurrentUserContext.getCurrentActiveUser().getOs());
                    jwtResponse.setBrowser(CurrentUserContext.getCurrentActiveUser().getBrowser());
                    response.setEntity(jwtResponse);
//            If he is a teller, he must have an account mapping
                    if (user.getIsTeller().equalsIgnoreCase("Yes")) {
                        Optional<Telleraccount> telleraccount = telleraccountRepo.findByEntityIdAndTellerIdAndDeletedFlag(user.getEntityId(), String.valueOf(user.getSn()), 'N');
                        if (telleraccount.isPresent()) {
                            jwtResponse.setTellerAc(telleraccount.get().getTellerAc());
                            jwtResponse.setAgencyAc(telleraccount.get().getAgencyAc());
//                    proceed to save
                            Mailparams mailsample = new Mailparams();
                            mailsample.setEmail(userDetails.getEmail());
                            mailsample.setSubject("OTP Code");
                            mailsample.setMessage(otp);
//                    topicProducer.send(mailsample);
                            user.setIsAcctActive(true);
                            userRepository.save(user);
                            response.setMessage("Authenticated Successfully! Kindly verify token to complete Authorization process.");
                            response.setStatusCode(HttpStatus.OK.value());
                            return response;
                        } else {
                            response.setMessage("Teller users Must have an Teller Account mapped with!");
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            response.setEntity("");
                            return response;
                        }
                    }else{
//                        log.info("not a teller account");
                        Mailparams mailsample = new Mailparams();
                        mailsample.setEmail(userDetails.getEmail());
                        mailsample.setSubject("OTP Code");
                        mailsample.setMessage(otp);
//                topicProducer.send(mailsample);
                        user.setIsAcctActive(true);
                        userRepository.save(user);
                        response.setMessage("Authenticated Successfully! Kindly verify token to complete Authorization process.");
                        response.setStatusCode(HttpStatus.OK.value());
                        String activity = "Sign in";
                        Character isActive = 'Y';
                        authSessionService.saveLoginSession(request, user.getUsername(), activity, isActive);
                        return response;

                    }

                }
            }
        }
    }
    @PostMapping("/signin/new")
    public EntityResponse signin(@Valid @RequestBody LoginRequest loginRequest,HttpServletRequest request) throws MessagingException {
        EntityResponse response = new EntityResponse();
        Users user = userRepository.findByUsername(loginRequest.getUsername()).orElse(null);
        if (user == null) {
            response.setMessage("Account Access RESTRICTED!!! Check with the System Admin");
            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
            return response;
        } else {
//        Check if Account is Locked
            if (user.isAcctLocked) {
                response.setMessage("Account is Locked!");
                response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                return response;
            } else {
                if (user.getDeletedFlag() == 'Y') {
                    response.setMessage("This account has been deleted!");
                    response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                    return response;
                } else {
                    Authentication authentication = authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
//            If he is a teller, he must have an account mapping
                    if (user.getIsTeller().equalsIgnoreCase("Yes")) {
                        log.info("teller account");
                        Optional<Telleraccount> telleraccount = telleraccountRepo.findByEntityIdAndTellerIdAndDeletedFlag(user.getEntityId(), String.valueOf(user.getSn()), 'N');
                        if (telleraccount.isPresent()) {

                            if (useOTP.equalsIgnoreCase("false")){

                                JwtResponse jwtResponse = getAccessToken(loginRequest.getUsername());
                                jwtResponse.setTellerAc(telleraccount.get().getTellerAc());
                                jwtResponse.setAgencyAc(telleraccount.get().getAgencyAc());
                                jwtResponse.setOtpEnabled(false);
                                user.setIsAcctActive(true);
                                userRepository.save(user);
                                response.setMessage("Welcome " + user.getUsername() + ", You have been Authenticated Successfully at " + new Date());
                                response.setStatusCode(HttpStatus.OK.value());
                                response.setEntity(jwtResponse);
                                log.info("About to login as a teller with jwt response: "+jwtResponse);
                            } else {
                                String email = otpTestMail;
                                String otp = otpService.generateOTP(loginRequest.getUsername());
                                if (otpProd.equalsIgnoreCase("true")){
                                    email = user.getEmail();

                                }else {
                                    email = email;
                                }
                                String to = email;
                                String subject = "OTP Verification";
                                String message = "Your OTP is "+otp;

//                                mailService.sendEmail(to, message, subject);

                                serviceCaller.sendEmail(new MailDto(message,email,subject,user.getPhoneNo()));

                                response.setMessage("Welcome " + user.getUsername() + ", Application 2FA Has Started and Verification OTP has been sent to your email. Please VERIFY the Provided OTP to complete Authentication Process.");
                                response.setStatusCode(HttpStatus.OK.value());
                                JwtResponse jwtResponse = new JwtResponse();
                                jwtResponse.setOtpEnabled(true);
                                jwtResponse.setUsername(loginRequest.getUsername());
                                response.setEntity(jwtResponse);
                            }
                        } else {
                            response.setMessage("Sorry " + user.getUsername() + " Account Access RESTRICTED!! You MUST Have a Teller Account: !!");
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            response.setEntity("");
                        }
                    }else{
                        log.info("not a teller account");
                        if (useOTP.equalsIgnoreCase("false")){
                            log.info("otp is false");
                            JwtResponse jwtResponse = getAccessToken(loginRequest.getUsername());
                            log.info("<=============================================================>");
                            jwtResponse.setOtpEnabled(false);
                            user.setIsAcctActive(true);
                            userRepository.save(user);
                            response.setMessage("Welcome " + user.getUsername() + ", You have been Authenticated Successfully at " + new Date());
                            response.setStatusCode(HttpStatus.OK.value());
                            response.setEntity(jwtResponse);
//                            String activity = "Sign in";
//                            Character isActive = 'Y';
//                            authSessionService.saveLoginSession(request, user.getUsername(), activity, isActive);
                        }else {
                            log.info("otp is true");
                            String email = otpTestMail;
                            String otp = otpService.generateOTP(loginRequest.getUsername());
                            if (otpProd.equalsIgnoreCase("true")){
                                email = user.getEmail();
                            }else {
                                email = email;
                            }
                            String to = email;
                            String subject = "OTP Verification";
                            String message = "Your OTP is "+otp;
//                            mailService.sendEmail(to, message, subject);
                            serviceCaller.sendEmail(new MailDto(message,email,subject,user.getPhoneNo()));
                            response.setMessage("Welcome " + user.getUsername() + ", Application 2FA Has Started and Verification OTP has been sent to your email. Please VERIFY the Provided OTP to complete Authentication Process.");
                            response.setStatusCode(HttpStatus.OK.value());
                            JwtResponse jwtResponse = new JwtResponse();
                            jwtResponse.setOtpEnabled(true);
                            jwtResponse.setUsername(loginRequest.getUsername());
                            response.setEntity(jwtResponse);
                        }
                    }
                }
            }
        }
        return response;
    }
    @GetMapping(path = "/otp/verify")
    public EntityResponse verifyOTP(@RequestParam String usedUserName, @RequestParam Integer otpCode, HttpServletRequest request) {
        log.info("Verifying otp");
        EntityResponse response =new EntityResponse();
        OTP otp = otpRepository.validOTP(usedUserName);
        if (Objects.isNull(otp) || !Objects.equals(otp.getOtp(), otpCode)) {
            response.setMessage("Failed.Invalid OTP Code");
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
        } else {
            log.info("getting user");
            Optional<Users> userCheck = userRepository.findByUsername(usedUserName);
            log.info("getting user");
            Users user = userCheck.get();
            // gen jwt
            log.info("getting jwt");
            JwtResponse jwtResponse = getAccessToken(usedUserName);
            log.info("getting jwt");
            jwtResponse.setOtpEnabled(true);
            user.setIsAcctActive(true);
            userRepository.save(user);
            response.setMessage("Authenticated Successfully!");
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(jwtResponse);
            String activity = "Verify OTP";
            Character isActive = 'N';
            log.info("saving loggin session");
            authSessionService.saveLoginSession(request, user.getUsername(), activity, isActive);
            log.info("saved loggin session");
        }
        return response;
    }

    @GetMapping(path = "sign/in/otp")
    public EntityResponse<?> signInOtp(
            @RequestParam(required = false) Integer otpCode,
            @RequestParam(required = false) String userName) {
        try {
            EntityResponse response = new EntityResponse();
            if (userName.equals(null) || userName.equals(" ")){
                response.setMessage("You Must Provide Account UserName to Proceed: !!");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            } else if (otpCode == null  || otpCode == ' ') {
                response.setMessage("You Must Provide Account OTP for Verification: !!");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            } else {
                OTP otp = otpRepository.validOTP(userName);
                if (Objects.isNull(otp) || !Objects.equals(otp.getOtp(), otpCode)) {
                    response.setMessage("Failed: !! OTP Code " + otpCode + " for user " + userName + " is invalid: !! " + " Make Sure You are FULLY Authenticated to avoid Anonymous!");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                } else {
                    Optional<Users> userCheck = userRepository.findByUsername(userName);
                    Users user = userCheck.get();
                    //gen jwt
                    JwtResponse jwtResponse = getAccessToken(userName);
                    jwtResponse.setOtpEnabled(true);
                    user.setIsAcctActive(true);
                    userRepository.save(user);
                    response.setMessage("Welcome " + user.getUsername() + ", You have been FULLY Authenticated Successfully at " + new Date());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(jwtResponse);
                }
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    private JwtResponse getAccessToken(String username){
        Optional<Users> userCheck = userRepository.findByUsername(username);
        log.info("User check "+userCheck.isPresent());
        Users user = userCheck.get();
        String jwt = jwtUtils.generateJwtTokenWithUsername(username);
        Workclass workclass = null;
        JwtResponse jwtResponse = new JwtResponse();
        // get workclass from user
        Optional<Workclass> workclass1 = workclassRepo.findById(user.getWorkclassFk());
        Optional<Telleraccount> telleraccount = telleraccountRepo.findByEntityIdAndDeletedFlagAndTellerUserName(EntityRequestContext.getCurrentEntityId(), 'N', username);
        if (workclass1.isPresent()){
            workclass = workclass1.get();

        }
        if(telleraccount.isPresent()){
            jwtResponse.setTellerAc(telleraccount.get().getTellerAc());
            jwtResponse.setAgencyAc(telleraccount.get().getAgencyAc());
        }
        Set<Role> roles = user.getRoles();

        jwtResponse.setToken(jwt);
        jwtResponse.setId(user.getSn().longValue());
        jwtResponse.setUsername(user.getUsername());
        jwtResponse.setEmail(user.getEmail());
        jwtResponse.setSolCode(user.getSolCode());
        jwtResponse.setEntityId(user.getEntityId());
        jwtResponse.setFirstName(user.getFirstName());
        jwtResponse.setLastName(user.getLastName());
        jwtResponse.setFirstLogin(user.getFirstLogin());
        jwtResponse.setMemberCode(user.getMemberCode());
        jwtResponse.setIsSystemGenPassword(user.getIsSystemGenPassword());
        jwtResponse.setRoles(roles);
        jwtResponse.setWorkclasses(workclass);
        log.info("User "+user.getUsername()+" check111 "+userCheck.isPresent());
        jwtResponse.setUuid(CurrentUserContext.getCurrentActiveUser().getUuid());
        jwtResponse.setStatus(CurrentUserContext.getCurrentActiveUser().getStatus());
        jwtResponse.setLoginAt(CurrentUserContext.getCurrentActiveUser().getLoginAt());
        jwtResponse.setAddress(CurrentUserContext.getCurrentActiveUser().getAddress());
        jwtResponse.setOs(CurrentUserContext.getCurrentActiveUser().getOs());
        log.info("User status is: "+CurrentUserContext.getCurrentActiveUser().getStatus()+" and address is "+CurrentUserContext.getCurrentActiveUser().getAddress());
        jwtResponse.setBrowser(CurrentUserContext.getCurrentActiveUser().getBrowser());
        return jwtResponse;
    }
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) throws MessagingException, IOException {
        EntityResponse response = new EntityResponse();
        if (UserRequestContext.getCurrentUser().isEmpty()) {
            response.setMessage("User Name not present in the Request Header");
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            response.setEntity("");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                Optional<Users> checkUser = userRepository.findByEmailAndDeletedFlag(signUpRequest.getEmail(),'Y');
                if (checkUser.isPresent()){
                    Users users = checkUser.get();
                    users.setDeletedFlag('N');
                    userRepository.save(users);
                    response.setMessage("The user with that email address "+signUpRequest.getEmail()+" has been reactivated! Please reset the password using that email");
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity("");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }else{
                    PasswordGeneratorUtil passwordGeneratorUtil = new PasswordGeneratorUtil();
                    String generatedPassword = passwordGeneratorUtil.generatePassayPassword();
                    // Create new user's account
                    signUpRequest.setPassword(generatedPassword);
                    signUpRequest.setIsEntityUser("No");
                    signUpRequest.setEntityId(EntityRequestContext.getCurrentEntityId());
                    if (validateUser(signUpRequest).getStatusCode() == HttpStatus.OK.value()){
                        Users user = new Users();
                        Set<Role> roles = new HashSet<>();
                        Optional<Role> role = roleRepository.findById(Long.valueOf(signUpRequest.getRoleFk()));
                        if (role.isPresent()) {
                            roles.add(role.get());
                        } else {
                            Optional<Role> defRole = roleRepository.findByEntityIdAndNameAndDeletedFlag(EntityRequestContext.getCurrentEntityId(),ERole.ROLE_USER.toString(),'N');
                            roles.add(defRole.get());
                        }
                        user.setRoles(roles);
                        user.setWorkclassFk(Long.valueOf(signUpRequest.getWorkclassFk()));
                        user.setPostedTime(new Date());
                        user.setPostedFlag('Y');
                        user.setPostedBy(UserRequestContext.getCurrentUser());
                        user.setIsAcctLocked(false);
                        user.setFirstLogin('Y');
                        user.setEntityId(signUpRequest.getEntityId());
                        user.setFirstName(signUpRequest.getFirstName());
                        user.setLastName(signUpRequest.getLastName());
                        user.setPhoneNo(signUpRequest.getPhoneNo());
                        user.setSolCode(signUpRequest.getSolCode());
                        user.setEmail(signUpRequest.getEmail());
                        user.setUsername(signUpRequest.getUsername());
                        user.setEntityId(signUpRequest.getEntityId());
                        user.setIsEntityUser(signUpRequest.getIsEntityUser());
                        user.setMemberCode(signUpRequest.getMemberCode());
                        user.setPassword(encoder.encode(signUpRequest.getPassword()));
                        //Check whether user with the same membership code exists

                        if (userRepository.findByEntityIdAndMemberCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(),signUpRequest.getMemberCode(),'N').isPresent()) {
                            response.setMessage("A user with customer code " + signUpRequest.getMemberCode()+ " already exists!");
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            response.setEntity("");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        }

                        // check if the teller list exceeded
                        if (signUpRequest.getIsTeller().equalsIgnoreCase("Yes") && userRepository.findByEntityIdAndIsTellerAndDeletedFlag(EntityRequestContext.getCurrentEntityId(),"Yes", 'N').size() >= maxNoOfTellers) {
                            response.setMessage("Max No. of Tellers Exceeded! " + maxNoOfTellers);
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            response.setEntity("");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        } else {
                            user.setIsTeller(signUpRequest.getIsTeller());
                            userRepository.save(user);
                            String mailMessage = "<p>Dear "+ user.getFirstName() +",</p>\n" +
                                    "    \n" +
                                    "    <p>Your account has been successfully created. Find the credentials below:</p>\n" +
                                    "    \n" +
                                    "    <ul>\n" +
                                    "        <li><strong>Username:</strong> "+ user.getUsername()+"</li>\n" +
                                    "        <li><strong>Password:</strong> "+signUpRequest.getPassword()+"</li>\n" +
                                    "    </ul>\n" +
                                    "    \n" +
                                    "    <p>Login to update your password.</p>";
                            mailService.sendEmail(user.getEntityId(), user.getEmail(), mailMessage, "Account Successfully Created");
                            response.setMessage("User " + user.getUsername() + " has been registered successfully! The user password is: " + signUpRequest.getPassword());
                            response.setStatusCode(HttpStatus.CREATED.value());
                            response.setEntity("");
                            System.out.println("User created");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        }
                    }else{
                        return new ResponseEntity<>(validateUser(signUpRequest), HttpStatus.OK);
                    }
                }
            }
        }
    }

    @PostMapping("/entity/user/signup")
    public ResponseEntity<?> registerEntityUser(@Valid @RequestBody SignupRequest signUpRequest) throws MessagingException, IOException {

        EntityResponse response = new EntityResponse();
        if (UserRequestContext.getCurrentUser().isEmpty()) {
            response.setMessage("User Name not present in the Request Header");
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            response.setEntity("");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                Optional<Users> checkUser = userRepository.findByEmailAndDeletedFlag(signUpRequest.getEmail(),'Y');
                if (checkUser.isPresent()){
                    Users users = checkUser.get();
                    users.setDeletedFlag('N');
                    userRepository.save(users);
                    response.setMessage("The user with that email address "+signUpRequest.getEmail()+" has been reactivated! Please reset the password using that email");
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity("");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }else {

                    PasswordGeneratorUtil passwordGeneratorUtil = new PasswordGeneratorUtil();
                    String generatedPassword = passwordGeneratorUtil.generatePassayPassword();
                    // Create new user's account
                    signUpRequest.setPassword(generatedPassword);
                    signUpRequest.setIsEntityUser("Yes");
                    signUpRequest.setSolCode("001");
                    signUpRequest.setMemberCode(signUpRequest.getEntityId()+""+signUpRequest.getSolCode());
                    if (validateUser(signUpRequest).getStatusCode() == HttpStatus.OK.value()){
                        Users user = new Users();
                        Set<Role> roles = new HashSet<>();
                        Optional<Role> role = roleRepository.findById(Long.valueOf(signUpRequest.getRoleFk()));
                        if (role.isPresent()) {
                            roles.add(role.get());
                        } else {
                            Optional<Role> defRole = roleRepository.findByEntityIdAndNameAndDeletedFlag(EntityRequestContext.getCurrentEntityId(),ERole.ROLE_USER.toString(),'N');
                            roles.add(defRole.get());
                        }
                        user.setRoles(roles);
                        user.setWorkclassFk(Long.valueOf(signUpRequest.getWorkclassFk()));
                        user.setPostedTime(new Date());
                        user.setPostedFlag('Y');
                        user.setPostedBy(UserRequestContext.getCurrentUser());
                        user.setIsAcctLocked(false);
                        user.setFirstLogin('Y');
                        user.setIsSystemGenPassword('N');
                        user.setEntityId(signUpRequest.getEntityId());
                        user.setFirstName(signUpRequest.getFirstName());
                        user.setLastName(signUpRequest.getLastName());
                        user.setPhoneNo(signUpRequest.getPhoneNo());
                        user.setSolCode(signUpRequest.getSolCode());
                        user.setEmail(signUpRequest.getEmail());
                        user.setUsername(signUpRequest.getUsername());
                        user.setIsEntityUser(signUpRequest.getIsEntityUser());
                        user.setMemberCode(signUpRequest.getMemberCode());
                        user.setPassword(encoder.encode(signUpRequest.getPassword()));
                        //Check whether user with the same membership code exists

//                    if (userRepository.findByEntityIdAndMemberCodeAndDeletedFlag(signUpRequest.getEntityId(),signUpRequest.getMemberCode(),'N').isPresent()) {
//                        response.setMessage("A user for entity " + signUpRequest.getEntityId()+ " already exists!");
//                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                        response.setEntity("");
//                        return new ResponseEntity<>(response, HttpStatus.OK);
//                    }

                        // check if the teller list exceeded
                        if (signUpRequest.getIsTeller().equalsIgnoreCase("Yes") && userRepository.findByEntityIdAndIsTellerAndDeletedFlag(EntityRequestContext.getCurrentEntityId(),"Yes", 'N').size() >= maxNoOfTellers) {
                            response.setMessage("Max No. of Tellers Exceeded! " + maxNoOfTellers);
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            response.setEntity("");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        } else {
                            user.setIsTeller(signUpRequest.getIsTeller());
                            userRepository.save(user);
                            String mailMessage = "<p>Dear "+ user.getFirstName() +",</p>\n" +
                                    "    \n" +
                                    "    <p>Your account has been successfully created. Find the credentials below:</p>\n" +
                                    "    \n" +
                                    "    <ul>\n" +
                                    "        <li><strong>Username:</strong> "+ user.getUsername()+"</li>\n" +
                                    "        <li><strong>Password:</strong> "+signUpRequest.getPassword()+"</li>\n" +
                                    "    </ul>\n" +
                                    "    \n" +
                                    "    <p>Login to update your password.</p>";
                            mailService.sendEmail(user.getEntityId(), user.getEmail(), mailMessage, "Account Successfully Created");
                            response.setMessage("User " + user.getUsername() + " has been registered successfully! The user password is: " + signUpRequest.getPassword());
                            response.setStatusCode(HttpStatus.CREATED.value());
                            response.setEntity("");
                            System.out.println("User created");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        }
                    }else{
                        return new ResponseEntity<>(validateUser(signUpRequest), HttpStatus.OK);
                    }
                }
            }
        }
    }

    public EntityResponse validateUser(SignupRequest signupRequest){
        EntityResponse response = new EntityResponse();
        SourceRule sourceRule = new SourceRule();
        HistoryRule historyRule = new HistoryRule();
        PasswordData passwords = new PasswordData(signupRequest.getPassword());
//        TODO: Password Should not contain username
        Rule rule = new UsernameRule();
        PasswordValidator usernamevalidator = new PasswordValidator(rule);
        passwords.setUsername(signupRequest.getUsername());
        RuleResult results = usernamevalidator.validate(passwords);
        if(results.isValid()){
//            TODO: Username is unique
            if (userRepository.existsByUsername(signupRequest.getUsername())) {
                response.setMessage("Username is already taken! "+signupRequest.getUsername());
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                return response;
            }else {
//                TODO: Email is unique
                if (userRepository.existsByEmailAndDeletedFlag(signupRequest.getEmail(),'N')) {
                    response.setMessage("Email is already in use!");
                    response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                    return response;
                }else{
//                    TODO: Phone number is unique
                    if (userRepository.existsByEntityIdAndPhoneNoAndDeletedFlag(EntityRequestContext.getCurrentEntityId(),signupRequest.getPhoneNo(),'N')) {
                        response.setMessage("The Phone number is already registered to another account!");
                        response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                        return response;
                    }else {
//                        TODO: Check if user has a Role
                        if (signupRequest.getRoleFk() == null) {
                            response.setMessage("You must provide a role!");
                            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                            response.setEntity("");
                            return response;
                        } else {
//                            TODO: Check if user has a workclass
                            if (signupRequest.getWorkclassFk() == null) {
                                response.setMessage("You must provide a workclass!");
                                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                                response.setEntity("");
                                return response;
                            }else {
                                response.setMessage("User is valid");
                                response.setStatusCode(HttpStatus.OK.value());
                            }
                        }
                    }

                }
            }

        }else{
            response.setMessage("Password should not contain the username provided i.e "+signupRequest.getUsername());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        }
        return response;
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public SignupResponse handlePasswordValidationException(MethodArgumentNotValidException e) {
        //Returning password error message as a response.
        return SignupResponse.builder()
                .message(Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage())
                .timestamp(Instant.now())
                .build();

    }

    @ExceptionHandler(InvalidRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public SignupResponse handleInvalidRequestParameterResponse(InvalidRequestParameterException e) {

        return SignupResponse.builder()
                .message(e.getMessage())
                .timestamp(Instant.now())
                .build();

    }

    @GetMapping(path = "/users")
    public List<Users> allUsers() {
        System.out.println("Getting all users entity id is: "+EntityRequestContext.getCurrentEntityId());
        List<Users> users = userRepository.findAllByDeletedFlagAndEntityId('N', EntityRequestContext.getCurrentEntityId());
        return userRepository.findAllByDeletedFlagAndEntityId('N', EntityRequestContext.getCurrentEntityId());
    }
    @GetMapping(path = "/all/entity/users")
    public EntityResponse allEntityUsers() {
        EntityResponse response = new EntityResponse();
        System.out.println("Getting all entity users entity id is: "+EntityRequestContext.getCurrentEntityId());
        List<Users> users = userRepository.findAllEntityUsersByDeletedFlag('N', "Yes");
        response.setMessage("Records Found");
        response.setStatusCode(HttpStatus.FOUND.value());
        response.setEntity(userRepository.findAllEntityUsersByDeletedFlag('N', "Yes"));
        return response;
    }

    @GetMapping(path = "/tellers")
    public List<Users> tellers(){
        return userRepository.findByEntityIdAndIsTellerAndDeletedFlag(EntityRequestContext.getCurrentEntityId(),"Yes",'N');
    }
    @GetMapping(path = "/active/sessions")
    public ResponseEntity<?> getActiveSession() throws JsonProcessingException {
        EntityResponse response = new EntityResponse();
        if (UserRequestContext.getCurrentUser().isEmpty()) {
            response.setMessage("User Name not present in the Request Header");
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            response.setEntity("");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                List<Activesession>  responseArray = new ArrayList<>();
                HashMap<String, String> map = (HashMap<String, String>) sessionManager.getActiveSession().getEntity();
                Gson g = new Gson();
                for (Map.Entry<String, String> set :
                        map.entrySet()) {
                    JwtResponse jwtResponse  = g.fromJson(set.getValue(),JwtResponse.class);
                    Activesession activesession = new Activesession();
                    activesession.setUuid(jwtResponse.getUuid());
                    activesession.setUsername(jwtResponse.getUsername());
                    activesession.setStatus(jwtResponse.getStatus());
                    activesession.setLoginAt(jwtResponse.getLoginAt());
                    activesession.setAddress(jwtResponse.getAddress());
                    activesession.setOs(jwtResponse.getOs());
                    activesession.setBrowser(jwtResponse.getBrowser());
                    responseArray.add(activesession);
                }
                response.setMessage(HttpStatus.OK.getReasonPhrase());
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(responseArray);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }
    }

    @GetMapping(path = "/find/by/username/{user}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String user){
        EntityResponse res = new EntityResponse<>();
        try{
            Optional<Users> user1=  userRepository.findByUsername(user);
            if(user1.isPresent()){
                res.setMessage(HttpStatus.FOUND.getReasonPhrase());
                res.setStatusCode(HttpStatus.FOUND.value());
                res.setEntity(user1.get());
            }else {
                res.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                res.setStatusCode(HttpStatus.NOT_FOUND.value());
                res.setEntity(null);
            }
            return new ResponseEntity<>(res, HttpStatus.OK);
        }catch (Exception e){
            log.error("Catched Error {} "+e);
            return null;
        }

    }
    @PutMapping(path = "/users/update")
    public ResponseEntity<?> updateUser(@Valid @RequestBody SignupRequest signUpRequest) throws MessagingException, IOException {
        EntityResponse response = new EntityResponse();
        if (UserRequestContext.getCurrentUser().isEmpty()) {
            response.setMessage("User Name not present in the Request Header");
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            response.setEntity("");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                Optional<Users> user1 = userRepository.findById(signUpRequest.getSn());
                Users user = user1.get();
                Set<Role> roles = new HashSet<>();
                if (signUpRequest.getRoleFk() == null) {
                    response.setMessage("You must provide a role!");
                    response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                    response.setEntity("");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    Optional<Role> role = roleRepository.findById(Long.valueOf(signUpRequest.getRoleFk()));
                    if (role.isPresent()) {
                        roles.add(role.get());
                    } else {
                        Optional<Role> defRole = roleRepository.findByEntityIdAndNameAndDeletedFlag(EntityRequestContext.getCurrentEntityId(),ERole.ROLE_USER.toString(),'N');
                        roles.add(defRole.get());
                    }
                    if (signUpRequest.getWorkclassFk() == null) {
                        response.setMessage("You must provide a workclass!");
                        response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        user.setRoles(roles);
                        user.setWorkclassFk(Long.valueOf(signUpRequest.getWorkclassFk()));
                        user.setPostedTime(new Date());
                        user.setPostedFlag('Y');
                        user.setPostedBy(UserRequestContext.getCurrentUser());
                        user.setIsAcctLocked(false);
                        user.setFirstLogin('Y');
                        user.setEntityId(signUpRequest.getEntityId());
                        user.setFirstName(signUpRequest.getFirstName());
                        user.setLastName(signUpRequest.getLastName());
                        user.setPhoneNo(signUpRequest.getPhoneNo());
                        user.setSolCode(signUpRequest.getSolCode());
                        user.setEmail(signUpRequest.getEmail());
                        user.setUsername(signUpRequest.getUsername());
                        user.setEntityId(signUpRequest.getEntityId());
                        user.setIsTeller(signUpRequest.getIsTeller());
                        //                check if the teller list exceeded
                        if (userRepository.findByEntityIdAndIsTellerAndDeletedFlag(EntityRequestContext.getCurrentEntityId(),"Yes", 'N').size() > maxNoOfTellers) {
                            response.setMessage("Max No. of Tellers Exceeded! " + maxNoOfTellers);
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            response.setEntity("");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        } else {
                            userRepository.save(user);
                            String mailMessage = "<p>Dear "+ user.getFirstName() +",</p>\n" +
                                    "    \n" +
                                    "    <p>your account details has been updated. Find the credentials below:</p>\n" +
                                    "    \n" +
                                    "    <ul>\n" +
                                    "        <li><strong>Username:</strong> "+ user.getUsername()+"</li>\n" +
                                    "        <li><strong>Password:</strong> "+signUpRequest.getPassword()+"</li>\n" +
                                    "    </ul>\n" +
                                    "    \n" +
                                    "    <p>Login to update your password.</p>";
                            mailService.sendEmail(user.getEntityId(), user.getEmail(), mailMessage, "Account Successfully Updated");

                            Mailparams mailsample = new Mailparams();
                            mailsample.setEmail(signUpRequest.getEmail());
                            mailsample.setSubject("Account Updated");
                            mailsample.setMessage(mailMessage);
                            response.setMessage("User " + signUpRequest.getUsername() + " has been updated successfully!");
                            response.setStatusCode(HttpStatus.CREATED.value());
                            response.setEntity("");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        }
                    }
                }
            }
        }
    }

    @PutMapping(path = "/verify/user/{id}")
    public ResponseEntity<?> verifyUser(@PathVariable Long id) {
        EntityResponse response = new EntityResponse();
        Optional<Users> users = userRepository.findById(id);
        if (users.isPresent()){
//            check if account is active
            Users user = users.get();
            user.setVerifiedFlag('Y');
            user.setVerifiedBy(UserRequestContext.getCurrentUser());
            user.setVerifiedTime(new Date());
            userRepository.save(user);
            response.setMessage("User verified Successfully!");
            response.setStatusCode(HttpStatus.OK.value());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            response.setMessage("User Not Found");
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @GetMapping(path = "/roles")
    public ResponseEntity<?> getRoles() {
        return ResponseEntity.ok().body(roleRepository.findByEntityIdAndDeletedFlag(EntityRequestContext.getCurrentEntityId(),'N'));
    }
    @PostMapping(path = "/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordResetRequest passwordResetRequest) throws MessagingException, IOException {
        System.out.println("Password reset header: "+passwordResetRequest);
        if (passwordResetRequest.getPassword().length() < 6 || passwordResetRequest.getPassword().isEmpty() || passwordResetRequest.getPassword() == null) {
            return ResponseEntity.ok().body(new MessageResponse(HttpStatus.BAD_REQUEST.value(),"Password Can not be less than 6 charecters"));
        } else {
            if (!userRepository.existsByEmailAndDeletedFlag(passwordResetRequest.getEmailAddress(),'N')) {
                return ResponseEntity.ok().body(new MessageResponse(HttpStatus.BAD_REQUEST.value(), "No such user exists"));
            } else {
                Users user = userRepository.findByEmailAndDeletedFlag(passwordResetRequest.getEmailAddress(),'N').orElse(null);
                if (passwordResetRequest.getPassword().equals(passwordResetRequest.getConfirmPassword())) {
                    user.setPassword(encoder.encode(passwordResetRequest.getPassword()));
                    user.setFirstLogin('N');
                    user.setIsSystemGenPassword('N');
                    userRepository.save(user);

                    String mailMessage = "<p>Dear "+ user.getFirstName() +",</p>\n" +
                            "    \n" +
                            "    <p>your account details has been reset. Find the credentials below:</p>\n" +
                            "    \n" +
                            "    <ul>\n" +
                            "        <li><strong>Username:</strong> "+ user.getUsername()+"</li>\n" +
                            "        <li><strong>Password:</strong> "+passwordResetRequest.getPassword()+"</li>\n" +
                            "    </ul>\n" +
                            "    \n" +
                            "    <p>Login to update your password.</p>";
                    mailService.sendEmail(user.getEntityId(), user.getEmail(), mailMessage, "Account Successfully Updated");

                    return ResponseEntity.ok().body(new MessageResponse(HttpStatus.OK.value(),"User Password updated successfully"));
                } else {
                    return ResponseEntity.ok().body(new MessageResponse(HttpStatus.BAD_REQUEST.value(),"Password mismatch. Try Again"));
                }
            }
        }
    }
    @PostMapping(path = "/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Forgotpassword forgotpassword) throws MessagingException, IOException {
        if (!userRepository.existsByEmailAndDeletedFlag(forgotpassword.getEmailAddress(),'N')) {
            EntityResponse response = new EntityResponse();
            response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setEntity("");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            PasswordGeneratorUtil passwordGeneratorUtil = new PasswordGeneratorUtil();
            String generatedPassword = passwordGeneratorUtil.generatePassayPassword();
            Optional<Users> user = userRepository.findByEmailAndDeletedFlag(forgotpassword.getEmailAddress(),'N');
            if (user.isPresent()){
                Users user1 = user.get();
                user1.setPassword(encoder.encode(generatedPassword));
                user1.setModifiedBy(user.get().getUsername());
                user1.setIsSystemGenPassword('Y');
                user1.setModifiedFlag('Y');
                user1.setModifiedTime(new Date());
                userRepository.save(user1);
                String mailMessage = "<p>Dear "+ user1.getFirstName() +",</p>\n" +
                        "    \n" +
                        "    <p>Your account details has been reset. Find the credentials below:</p>\n" +
                        "    \n" +
                        "    <ul>\n" +
                        "        <li><strong>Username:</strong> "+ user1.getUsername()+"</li>\n" +
                        "        <li><strong>Password:</strong> "+generatedPassword+"</li>\n" +
                        "    </ul>\n" +
                        "    \n" +
                        "    <p>Login to update your password.</p>";
                mailService.sendEmail(user1.getEntityId(), user1.getEmail(), mailMessage, "Password Reset Successfull");
                Mailparams mailsample = new Mailparams();
                mailsample.setEmail(user1.getEmail());
                mailsample.setSubject("Password Reset Successfull");
                mailsample.setMessage(mailMessage);

                EntityResponse response = new EntityResponse();
                response.setMessage("Password Reset Successfully! Password has been sent to the requested email");
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }else{
                EntityResponse response = new EntityResponse();
                response.setMessage("User with email address not found!");
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }
    }
    @PutMapping(path = "/lock/{id}")
    public EntityResponse lock(@PathVariable Long id) {
        EntityResponse response = new EntityResponse();
        Optional<Users> users = userRepository.findById(id);
        if (users.isPresent()){
//            check if account is active
            Users users1 = users.get();
            users1.setIsAcctLocked(true);
            userRepository.save(users1);
            response.setMessage("Logout Successful!");
            response.setStatusCode(HttpStatus.OK.value());
            return response;
        }else{
            response.setMessage("User Not Found");
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            return response;
        }
    }
    @PutMapping(path = "/unlock/{id}")
    public EntityResponse unlock(@PathVariable Long id) {
        EntityResponse response = new EntityResponse();
        Optional<Users> users = userRepository.findById(id);
        if (users.isPresent()){
//            check if account is active
            Users users1 = users.get();
            users1.setIsAcctLocked(false);
            userRepository.save(users1);
            response.setMessage("Locked Successful!");
            response.setStatusCode(HttpStatus.OK.value());
            return response;
        }else{
            response.setMessage("User Not Found");
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            return response;
        }
    }
    @PutMapping(path = "/logout/{id}")
    public EntityResponse logout(@PathVariable Long id, HttpServletRequest request) {
        EntityResponse response = new EntityResponse();
        Optional<Users> users = userRepository.findById(id);
        if (users.isPresent()){
//            check if account is active
            Users users1 = users.get();
            users1.setIsAcctActive(false);
            userRepository.save(users1);
            response.setMessage("Logout Successful!");
            response.setStatusCode(HttpStatus.OK.value());
            String activity = "Sign out";
            Character isActive = 'N';
            authSessionService.saveLoginSession(request, users1.getUsername(), activity, isActive);
            return response;
        }else{
            response.setMessage("User Not Found");
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            return response;
        }
    }
    @PutMapping(path = "/restore/{id}")
    public ResponseEntity<?> restore(@PathVariable Long id) {
        EntityResponse response = new EntityResponse();
        Optional<Users> users = userRepository.findById(id);
        if (users.isPresent()){
//            check if account is active
            Users user = users.get();
            user.setIsAcctActive(true);
            user.setDeletedFlag('N');
            user.setVerifiedFlag('N');
            user.setModifiedFlag('Y');
            user.setModifiedBy(UserRequestContext.getCurrentUser());
            user.setModifiedTime(new Date());
            userRepository.save(user);
            response.setMessage("Unlocked Successful!");
            response.setStatusCode(HttpStatus.OK.value());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            response.setMessage("User Not Found");
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }
    //    If is active
    @DeleteMapping(path = "/delete/{id}")
    public EntityResponse delete(@PathVariable Long id) {
        EntityResponse response = new EntityResponse();
        Optional<Users> users = userRepository.findById(id);
        if (users.isPresent()){
//            check if account is active
            if (users.get().isAcctLocked){
                response.setMessage("Active account can not be deleted");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;
            }else {
                if (telleraccountRepo.findById(id).isPresent()){
                    response.setMessage("This user has a Teller account attached! Kindly detach the user from account first");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    return response;
                }else{
                    Boolean isSuperuser = false;
                    Boolean isManager = false;
                    for (Role role : users.get().getRoles()) {
                        if (role.getName().equalsIgnoreCase(String.valueOf(ERole.ROLE_SUPERUSER))) {
                            isSuperuser = true;
                        } else if (role.getName().equalsIgnoreCase(String.valueOf(ERole.ROLE_MANAGER))) {
                            isManager = true;
                        }
                    }
                    if (isSuperuser) {
                        response.setMessage("Superuser can not be deleted");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        return response;
                    } else {
                        if (isManager) {
                            response.setMessage("Manager can not be deleted");
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            return response;
                        } else {
                            Users user = users.get();
                            user.setIsAcctActive(false);
                            user.setDeletedFlag('Y');
                            user.setDeletedBy(UserRequestContext.getCurrentUser());
                            user.setDeletedTime(new Date());
                            userRepository.save(user);
                            response.setMessage("User Deleted");
                            response.setStatusCode(HttpStatus.OK.value());
                            return response;
                        }
                    }
                }
            }
        }else{
            response.setMessage("User Not Found");
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            return response;
        }
    }

    @DeleteMapping(path = "temp/delete/{id}")
    public EntityResponse tempDelete(@PathVariable Long id) {
        EntityResponse response = new EntityResponse();
        Optional<Users> users = userRepository.findById(id);
        if (users.isPresent()){
//            check if account is active
            userRepository.deleteById(id);
            return new EntityResponse<>();
        }else{
            response.setMessage("User Not Found");
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            return response;
        }
    }
    @GetMapping("send/account-statement")
    public ResponseEntity<EntityResponse> sendStatementToEmail(@RequestParam("acid") String acid,
                                                                                       @RequestParam("maxCount") Integer maxCount) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(usersService.sendStatement(acid, maxCount));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
}