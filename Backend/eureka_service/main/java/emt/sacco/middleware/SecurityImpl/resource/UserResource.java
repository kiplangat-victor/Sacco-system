package emt.sacco.middleware.SecurityImpl.resource;//package emt.sacco.middleware.SecurityImpl.resource;
//
//import emt.sacco.middleware.SecurityImpl.Sec.CreateUserRequest;
//import emt.sacco.middleware.SecurityImpl.Sec.HttpResponse;
//import emt.sacco.middleware.SecurityImpl.Sec.UserDetailsImpl;
//import emt.sacco.middleware.SecurityImpl.Sec.Users;
//import emt.sacco.middleware.SecurityImpl.Sec.UserPrincipal;
//import emt.sacco.middleware.SecurityImpl.SecImpl.JWTTokenProvider;
//import emt.sacco.middleware.Utils.Config.UserRequestContext;
//import emt.sacco.middleware.Utils.EntityRequestContext;
//import emt.sacco.middleware.Utils.EntityResponse;
//import emt.sacco.middleware.Utils.PasswordGeneratorUtil;
//import emt.sacco.middleware.SecurityImpl.enumeration.Roles.ERole;
//import emt.sacco.middleware.SecurityImpl.enumeration.Roles.Role;
//import emt.sacco.middleware.SecurityImpl.enumeration.Roles.RoleRepository;
//import emt.sacco.middleware.SecurityImpl.exception.domain.*;
//import emt.sacco.middleware.SecurityImpl.service.UserService;
//import org.passay.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.mail.MessagingException;
//import java.io.IOException;
//import java.util.*;
//
//import static emt.sacco.middleware.SecurityImpl.constants.SecurityConstants.JWT_TOKEN_HEADER;
//import static org.springframework.http.HttpStatus.OK;
//
//@RestController
////@RequestMapping( "/user")
//@RequestMapping("/aut")
//public class UserResource {
////    public class UserResource extends ExceptionHandling {
//    public static final String EMAIL_SENT = "An email with a new password was sent to: ";
//    public static final String USER_DELETED_SUCCESSFULLY = "User deleted successfully";
//    private AuthenticationManager authenticationManager;
//    private UserService userService;
//    @Autowired
//    public final RoleRepository roleRepository;
//    private JWTTokenProvider jwtTokenProvider;
//    @Autowired
//    public  final UsersRepository userRepository;
//    @Autowired
//    PasswordEncoder encoder;
//
//    @Autowired
//    public UserResource(AuthenticationManager authenticationManager, UserService userService, RoleRepository roleRepository, JWTTokenProvider jwtTokenProvider, UsersRepository usersRepository) {
//        this.authenticationManager = authenticationManager;
//        this.userService = userService;
//        this.roleRepository = roleRepository;
//        this.jwtTokenProvider = jwtTokenProvider;
//        this.userRepository = usersRepository;
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<Users> login(@RequestBody Users users) {
//        authenticate(users.getUsername(), users.getPassword());
//        Users loginUsers = userService.findUserByUsername(users.getUsername());
//        UserDetailsImpl userPrincipal = new UserDetailsImpl(loginUsers);
//        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
//        return new ResponseEntity<>(loginUsers, jwtHeader, OK);
//    }
//
//    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<Users> register(@RequestBody Users users) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException {
//        Users newUsers = userService.register(users.getFirstName(), users.getLastName(), users.getUsername(), users.getEmail());
//        return new ResponseEntity<>(newUsers, OK);
//    }
//
////
//@PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
//        EntityResponse response = new EntityResponse();
//        if (UserRequestContext.getCurrentUser().isEmpty()) {
//            response.setMessage("User Name not present in the Request Header");
//            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//            response.setEntity("");
//            return new ResponseEntity<>(response, HttpStatus.OK);
//        } else {
//            if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
//                response.setMessage("Entity not present in the Request Header");
//                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                response.setEntity("");
//                return new ResponseEntity<>(response, HttpStatus.OK);
//            } else {
//                Optional<Users> checkUser = userRepository.findByEmailAndDeletedFlag(signUpRequest.getEmail(),'Y');
//                if (checkUser.isPresent()){
//                    Users users = checkUser.get();
//                    users.setDeletedFlag('N');
//                    userRepository.save(users);
//                    response.setMessage("The user with that email address "+signUpRequest.getEmail()+" has been reactivated! Please reset the password using that email");
//                    response.setStatusCode(HttpStatus.OK.value());
//                    response.setEntity("");
//                    return new ResponseEntity<>(response, HttpStatus.OK);
//                }else{
//                    PasswordGeneratorUtil passwordGeneratorUtil = new PasswordGeneratorUtil();
//                    String generatedPassword = passwordGeneratorUtil.generatePassayPassword();
//                    // Create new user's account
//                    signUpRequest.setPassword(generatedPassword);
//                    signUpRequest.setIsEntityUser("No");
//                    signUpRequest.setEntityId(EntityRequestContext.getCurrentEntityId());
//                    if (validateUser(signUpRequest).getStatusCode() == HttpStatus.OK.value()){
//                        Users users = new Users();
//                        Set<Role> roles = new HashSet<>();
//                        Optional<Role> role = roleRepository.findById(Long.valueOf(signUpRequest.getRoleFk()));
//                        if (role.isPresent()) {
//                            roles.add(role.get());
//                        } else {
//                            Optional<Role> defRole = roleRepository.findByEntityIdAndNameAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), ERole.ROLE_USER.toString(),'N');
//                            roles.add(defRole.get());
//                        }
//                        users.setRoles(roles);
//                        users.setWorkclassFk(Long.valueOf(signUpRequest.getWorkclassFk()));
//                        users.setPostedTime(new Date());
//                        users.setPostedFlag('Y');
//                        users.setPostedBy(UserRequestContext.getCurrentUser());
//                        users.setIsAcctLocked(false);
//                        users.setFirstLogin('Y');
//                        users.setEntityId(signUpRequest.getEntityId());
//                        users.setFirstName(signUpRequest.getFirstName());
//                        users.setLastName(signUpRequest.getLastName());
//                        users.setPhoneNo(signUpRequest.getPhoneNo());
//                        users.setSolCode(signUpRequest.getSolCode());
//                        users.setEmail(signUpRequest.getEmail());
//                        users.setUsername(signUpRequest.getUsername());
//                        users.setEntityId(signUpRequest.getEntityId());
//                        users.setIsEntityUser(signUpRequest.getIsEntityUser());
//                        users.setMemberCode(signUpRequest.getMemberCode());
//                        users.setPassword(encoder.encode(signUpRequest.getPassword()));
//                        //Check whether user with the same membership code exists
//
//                        if (userRepository.findByEntityIdAndMemberCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(),signUpRequest.getMemberCode(),'N').isPresent()) {
//                            response.setMessage("A user with customer code " + signUpRequest.getMemberCode()+ " already exists!");
//                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                            response.setEntity("");
//                            return new ResponseEntity<>(response, HttpStatus.OK);
//                        }
//
//                        // check if the teller list exceeded
//                        if (signUpRequest.getIsTeller().equalsIgnoreCase("Yes") && userRepository.findByEntityIdAndIsTellerAndDeletedFlag(EntityRequestContext.getCurrentEntityId(),"Yes", 'N').size() >= 1) {
////                            response.setMessage("Max No. of Tellers Exceeded! " + maxNoOfTellers);
//                            response.setMessage("Max No. of Tellers Exceeded! " );
//                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                            response.setEntity("");
//                            return new ResponseEntity<>(response, HttpStatus.OK);
//                        } else {
//                            users.setIsTeller(signUpRequest.getIsTeller());
//                            userRepository.save(users);
//                            String mailMessage = "<p>Dear "+ users.getFirstName() +",</p>\n" +
//                                    "    \n" +
//                                    "    <p>Your account has been successfully created. Find the credentials below:</p>\n" +
//                                    "    \n" +
//                                    "    <ul>\n" +
//                                    "        <li><strong>Username:</strong> "+ users.getUsername()+"</li>\n" +
//                                    "        <li><strong>Password:</strong> "+signUpRequest.getPassword()+"</li>\n" +
//                                    "    </ul>\n" +
//                                    "    \n" +
//                                    "    <p>Login to update your password.</p>";
////                            mailService.sendEmail(user.getEntityId(), user.getEmail(), mailMessage, "Account Successfully Created");
//                            response.setMessage("User " + users.getUsername() + " has been registered successfully! The user password is: " + signUpRequest.getPassword());
//                            response.setStatusCode(HttpStatus.CREATED.value());
//                            response.setEntity("");
//                            System.out.println("User created"+signUpRequest.getPassword());
//                            return new ResponseEntity<>(response, HttpStatus.OK);
//                        }
//                    }else{
//                        return new ResponseEntity<>(validateUser(signUpRequest), HttpStatus.OK);
//                    }
//                }
//            }
//        }
//    }
//    public EntityResponse validateUser(SignupRequest signupRequest){
//        EntityResponse response = new EntityResponse();
//        SourceRule sourceRule = new SourceRule();
//        HistoryRule historyRule = new HistoryRule();
//        PasswordData passwords = new PasswordData(signupRequest.getPassword());
////        TODO: Password Should not contain username
//        Rule rule = new UsernameRule();
//        PasswordValidator usernamevalidator = new PasswordValidator(rule);
//        passwords.setUsername(signupRequest.getUsername());
//        RuleResult results = usernamevalidator.validate(passwords);
//        if(results.isValid()){
////            TODO: Username is unique
//            if (userRepository.existsByUsername(signupRequest.getUsername())) {
//                response.setMessage("Username is already taken! "+signupRequest.getUsername());
//                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
//                return response;
//            }else {
////                TODO: Email is unique
//                if (userRepository.existsByEmailAndDeletedFlag(signupRequest.getEmail(),'N')) {
//                    response.setMessage("Email is already in use!");
//                    response.setStatusCode(HttpStatus.BAD_REQUEST.value());
//                    return response;
//                }else{
////                    TODO: Phone number is unique
//                    if (userRepository.existsByEntityIdAndPhoneNoAndDeletedFlag(EntityRequestContext.getCurrentEntityId(),signupRequest.getPhoneNo(),'N')) {
//                        response.setMessage("The Phone number is already registered to another account!");
//                        response.setStatusCode(HttpStatus.BAD_REQUEST.value());
//                        return response;
//                    }else {
////                        TODO: Check if user has a Role
//                        if (signupRequest.getRoleFk() == null) {
//                            response.setMessage("You must provide a role!");
//                            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
//                            response.setEntity("");
//                            return response;
//                        } else {
////                            TODO: Check if user has a workclass
//                            if (signupRequest.getWorkclassFk() == null) {
//                                response.setMessage("You must provide a workclass!");
//                                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
//                                response.setEntity("");
//                                return response;
//                            }else {
//                                response.setMessage("User is valid");
//                                response.setStatusCode(HttpStatus.OK.value());
//                            }
//                        }
//                    }
//
//                }
//            }
//
//        }else{
//            response.setMessage("Password should not contain the username provided i.e "+signupRequest.getUsername());
//            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
//        }
//        return response;
//    }
//
////    @PostMapping(value = "/add",produces = MediaType.APPLICATION_JSON_VALUE)
////    public ResponseEntity<User> addNewUser(@RequestParam("firstName") String firstName,
////                                           @RequestParam("lastName") String lastName,
////                                           @RequestParam("username") String username,
////                                           @RequestParam("email") String email,
////                                           @RequestParam("role") String role,
////                                           @RequestParam("isActive") String isActive,
////                                           @RequestParam("isNonLocked") String isNonLocked,
////                                           @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException, NotAnImageFileException {
////        User newUser = userService.addNewUser(firstName, lastName, username,email, role, Boolean.parseBoolean(isNonLocked), Boolean.parseBoolean(isActive), profileImage);
////        return new ResponseEntity<>(newUser, OK);
////    }
//@PostMapping(value = "/add", produces = MediaType.APPLICATION_JSON_VALUE)
//public ResponseEntity<Users> addNewUser(@RequestBody CreateUserRequest createUserRequest,
//                                        @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException {
//    // Extract information from CreateUserRequest object
//    String firstName = createUserRequest.getFirstName();
//    String lastName = createUserRequest.getLastName();
//    String username = createUserRequest.getUsername();
//    String email = createUserRequest.getEmail();
//    String role = createUserRequest.getRole();
//    boolean isNonLocked = createUserRequest.isNonLocked();
//    boolean isActive = createUserRequest.isActive();
//
//    // Call addNewUser method with extracted parameters
//    Users newUsers = userService.addNewUser(firstName, lastName, username, email, role, isNonLocked, isActive, profileImage);
//    return new ResponseEntity<>(newUsers, HttpStatus.OK);
//}
//
//
//    @PostMapping("/update")
//    public ResponseEntity<Users> update(@RequestParam("currentUsername") String currentUsername,
//                                        @RequestParam("firstName") String firstName,
//                                        @RequestParam("lastName") String lastName,
//                                        @RequestParam("username") String username,
//                                        @RequestParam("email") String email,
//                                        @RequestParam("role") String role,
//                                        @RequestParam("isActive") String isActive,
//                                        @RequestParam("isNonLocked") String isNonLocked,
//                                        @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException {
//        Users updatedUsers = userService.updateUser(currentUsername, firstName, lastName, username,email, role, Boolean.parseBoolean(isNonLocked), Boolean.parseBoolean(isActive), profileImage);
//        return new ResponseEntity<>(updatedUsers, OK);
//    }
//
//    @GetMapping("/find/{username}")
//    public ResponseEntity<Users> getUser(@PathVariable("username") String username) {
//        Users users = userService.findUserByUsername(username);
//        return new ResponseEntity<>(users, OK);
//    }
//
//    @GetMapping("/list")
//    public ResponseEntity<List<Users>> getAllUsers() {
//        List<Users> users = userService.getUsers();
//        return new ResponseEntity<>(users, OK);
//    }
//
//    @GetMapping("/resetpassword/{email}")
//    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email) throws MessagingException, EmailNotFoundException {
//        userService.resetPassword(email);
//        return response(OK, EMAIL_SENT + email);
//    }
//
//    @DeleteMapping("/delete/{username}")
//    @PreAuthorize("hasAnyAuthority('user:delete')")
//    public ResponseEntity<HttpResponse> deleteUser(@PathVariable("username") String username) throws IOException {
//        userService.deleteUser(username);
//        return response(OK, USER_DELETED_SUCCESSFULLY);
//    }
//
//    @PostMapping("/updateProfileImage")
//    public ResponseEntity<Users> updateProfileImage(@RequestParam("username") String username, @RequestParam(value = "profileImage") MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException {
//        Users users = userService.updateProfileImage(username, profileImage);
//        return new ResponseEntity<>(users, OK);
//    }
//
////    @GetMapping(path = "/image/{username}/{fileName}", produces = IMAGE_JPEG_VALUE)
////    public byte[] getProfileImage(@PathVariable("username") String username, @PathVariable("fileName") String fileName) throws IOException {
////        return Files.readAllBytes(Paths.get(USER_FOLDER + username + FORWARD_SLASH + fileName));
////    }
//
////    @GetMapping(path = "/image/profile/{username}", produces = IMAGE_JPEG_VALUE)
////    public byte[] getTempProfileImage(@PathVariable("username") String username) throws IOException {
////        URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + username);
////        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
////        try (InputStream inputStream = url.openStream()) {
////            int bytesRead;
////            byte[] chunk = new byte[1024];
////            while((bytesRead = inputStream.read(chunk)) > 0) {
////                byteArrayOutputStream.write(chunk, 0, bytesRead);
////            }
////        }
////        return byteArrayOutputStream.toByteArray();
////    }
//
//    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
//        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
//                message), httpStatus);
//    }
//
//    private HttpHeaders getJwtHeader(UserDetailsImpl user) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(user));
//        return headers;
//    }
//
//    private void authenticate(String username, String password) {
//        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
//    }
//
//}
