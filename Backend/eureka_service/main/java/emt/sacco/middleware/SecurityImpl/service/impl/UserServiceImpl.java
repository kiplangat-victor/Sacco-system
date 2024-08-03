package emt.sacco.middleware.SecurityImpl.service.impl;//package emt.sacco.middleware.SecurityImpl.service.impl;
//
//import emt.sacco.middleware.SecurityImpl.Sec.Users;
//import emt.sacco.middleware.SecurityImpl.Sec.UserDetailsImpl;
//import emt.sacco.middleware.SecurityImpl.enumeration.Role;
//import emt.sacco.middleware.SecurityImpl.exception.domain.*;
//import emt.sacco.middleware.SecurityImpl.repository.UserRepository;
//import emt.sacco.middleware.SecurityImpl.service.EmailService;
//import emt.sacco.middleware.SecurityImpl.service.LoginAttemptService;
//import emt.sacco.middleware.SecurityImpl.service.UserService;
//import org.apache.commons.lang3.RandomStringUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.tomcat.util.http.fileupload.FileUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
//
//import javax.mail.MessagingException;
//import javax.transaction.Transactional;
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.Arrays;
//import java.util.Date;
//import java.util.List;
//
//import static emt.sacco.middleware.SecurityImpl.constants.FileConstant.*;
//import static emt.sacco.middleware.SecurityImpl.constants.FileConstant.NOT_AN_IMAGE_FILE;
//import static emt.sacco.middleware.SecurityImpl.constants.UserImplConstant.*;
//import static emt.sacco.middleware.SecurityImpl.enumeration.Role.*;
//import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
//import static org.apache.commons.lang3.StringUtils.EMPTY;
//import static org.springframework.http.MediaType.*;
//
//@Service
//@Transactional
//@Qualifier("userDetailsService")
//public class UserServiceImpl implements UserService, UserDetailsService {
//    private Logger LOGGER = LoggerFactory.getLogger(getClass());
//    private UserRepository userRepository;
//
//    private BCryptPasswordEncoder passwordEncoder;
//    private LoginAttemptService loginAttemptService;
//    private EmailService emailService;
//
//    @Autowired
//    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, LoginAttemptService loginAttemptService, EmailService emailService) {
//        this.userRepository = userRepository;
//        this.passwordEncoder = passwordEncoder;
//        this.loginAttemptService = loginAttemptService;
//        this.emailService = emailService;
//    }
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        Users users = userRepository.findUserByUsername(username);
//        if (users == null) {
//            LOGGER.error(NO_USER_FOUND_BY_USERNAME + username);
//            throw new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + username);
//        } else {
//            validateLoginAttempt(users);
//            users.setLastLoginDateDisplay(users.getLastLoginDate());
//            users.setFirstLogin('Y');
////            user.setLastLoginDateDisplay(user.getLastLoginDate());
////            user.setLastLoginDate(new Date());
//            userRepository.save(users);
//            UserDetailsImpl userPrincipal = new UserDetailsImpl(users);
//            LOGGER.info(FOUND_USER_BY_USERNAME + username);
//            return userPrincipal;
//        }
//    }
//
//    @Override
//    public Users register(String firstName, String lastName, String username, String email) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException {
//        validateNewUsernameAndEmail(EMPTY, username, email);
//        Users users = new Users();
//        users.setUserId(generateUserId());
//        String password = generatePassword();
//        users.setFirstName(firstName);
//        users.setLastName(lastName);
//        users.setUsername(username);
//        users.setEmail(email);
//        users.setJoinDate(new Date());
//        users.setPassword(encodePassword(password));
//        users.setActive(true);
//        users.setNotLocked(true);
//        users.setRole(ROLE_USER.name());
//        users.setAuthorities(ROLE_USER.getAuthorities());
//        users.setProfileImageUrl(getTemporaryProfileImageUrl(username));
//        userRepository.save(users);
//        LOGGER.info("New user password: " + password);
//        emailService.sendNewPasswordEmail(firstName, password, email);
//        return users;
//    }
//
//    @Override
//    public Users addNewUser(String firstName, String lastName, String username, String email, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException {
//        validateNewUsernameAndEmail(EMPTY, username, email);
//        Users users = new Users();
//        String password = generatePassword();
//        users.setUserId(generateUserId());
//        users.setFirstName(firstName);
//        users.setLastName(lastName);
//        users.setJoinDate(new Date());
//        users.setUsername(username);
//        users.setEmail(email);
//        users.setPassword(encodePassword(password));
//        users.setActive(isActive);
//        users.setNotLocked(isNonLocked);
//        users.setRole(getRoleEnumName(role).name());
//        users.setAuthorities(getRoleEnumName(role).getAuthorities());
//        users.setProfileImageUrl(getTemporaryProfileImageUrl(username));
//        userRepository.save(users);
//        saveProfileImage(users, profileImage);
//        LOGGER.info("New user password: " + password);
//        return users;
//    }
//
//    @Override
//    public Users updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername, String newEmail, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException {
//        Users currentUsers = validateNewUsernameAndEmail(currentUsername, newUsername, newEmail);
//        currentUsers.setFirstName(newFirstName);
//        currentUsers.setLastName(newLastName);
//        currentUsers.setUsername(newUsername);
//        currentUsers.setEmail(newEmail);
//        currentUsers.setActive(isActive);
//        currentUsers.setNotLocked(isNonLocked);
//        currentUsers.setRole(getRoleEnumName(role).name());
//        currentUsers.setAuthorities(getRoleEnumName(role).getAuthorities());
//        userRepository.save(currentUsers);
//        saveProfileImage(currentUsers, profileImage);
//        return currentUsers;
//    }
//
//    @Override
//    public void resetPassword(String email) throws MessagingException, EmailNotFoundException {
//        Users users = userRepository.findUserByEmail(email);
//        if (users == null) {
//            throw new EmailNotFoundException(NO_USER_FOUND_BY_EMAIL + email);
//        }
//        String password = generatePassword();
//        users.setPassword(encodePassword(password));
//        userRepository.save(users);
//        LOGGER.info("New user password: " + password);
//        emailService.sendNewPasswordEmail(users.getFirstName(), password, users.getEmail());
//    }
//
//    @Override
//    public Users updateProfileImage(String username, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException {
//        Users users = validateNewUsernameAndEmail(username, null, null);
//        saveProfileImage(users, profileImage);
//        return users;
//    }
//
//    @Override
//    public List<Users> getUsers() {
//        return userRepository.findAll();
//    }
//
//    @Override
//    public Users findUserByUsername(String username) {
//        return userRepository.findUserByUsername(username);
//    }
//
//    @Override
//    public Users findUserByEmail(String email) {
//        return userRepository.findUserByEmail(email);
//    }
//
//    @Override
//    public void deleteUser(String username) throws IOException {
//        Users users = userRepository.findUserByUsername(username);
//        Path userFolder = Paths.get(USER_FOLDER + users.getUsername()).toAbsolutePath().normalize();
//        FileUtils.deleteDirectory(new File(userFolder.toString()));
//        userRepository.deleteById(users.getSn());
//    }
//
//    private void saveProfileImage(Users users, MultipartFile profileImage) throws IOException, NotAnImageFileException {
//        if (profileImage != null) {
//            if(!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE).contains(profileImage.getContentType())) {
//                throw new NotAnImageFileException(profileImage.getOriginalFilename() + NOT_AN_IMAGE_FILE);
//            }
//            Path userFolder = Paths.get(USER_FOLDER + users.getUsername()).toAbsolutePath().normalize();
//            if(!Files.exists(userFolder)) {
//                Files.createDirectories(userFolder);
//                LOGGER.info(DIRECTORY_CREATED + userFolder);
//            }
//            Files.deleteIfExists(Paths.get(userFolder + users.getUsername() + DOT + JPG_EXTENSION));
//            Files.copy(profileImage.getInputStream(), userFolder.resolve(users.getUsername() + DOT + JPG_EXTENSION), REPLACE_EXISTING);
//            users.setProfileImageUrl(setProfileImageUrl(users.getUsername()));
//            userRepository.save(users);
//            LOGGER.info(FILE_SAVED_IN_FILE_SYSTEM + profileImage.getOriginalFilename());
//        }
//    }
//
//    private String setProfileImageUrl(String username) {
//        return ServletUriComponentsBuilder.fromCurrentContextPath().path(USER_IMAGE_PATH + username + FORWARD_SLASH
//        + username + DOT + JPG_EXTENSION).toUriString();
//    }
//
//    private Role getRoleEnumName(String role) {
//        return Role.valueOf(role.toUpperCase());
//    }
//
//    private String getTemporaryProfileImageUrl(String username) {
//        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH + username).toUriString();
//    }
//
//    private String encodePassword(String password) {
//        return passwordEncoder.encode(password);
//    }
//
//    private String generatePassword() {
//        return RandomStringUtils.randomAlphanumeric(10);
//    }
//
//    private String generateUserId() {
//        return RandomStringUtils.randomNumeric(10);
//    }
//
//    private void validateLoginAttempt(Users users) {
//        if(users.isNotLocked()) {
//            if(loginAttemptService.hasExceededMaxAttempts(users.getUsername())) {
//                users.setNotLocked(false);
//            } else {
//                users.setNotLocked(true);
//            }
//        } else {
//            loginAttemptService.evictUserFromLoginAttemptCache(users.getUsername());
//        }
//    }
//
//    private Users validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail) throws UserNotFoundException, UsernameExistException, EmailExistException {
//        Users usersByNewUsername = findUserByUsername(newUsername);
//        Users usersByNewEmail = findUserByEmail(newEmail);
//        if(StringUtils.isNotBlank(currentUsername)) {
//            Users currentUsers = findUserByUsername(currentUsername);
//            if(currentUsers == null) {
//                throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + currentUsername);
//            }
//            if(usersByNewUsername != null && !currentUsers.getSn().equals(usersByNewUsername.getSn())) {
//                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
//            }
//            if(usersByNewEmail != null && !currentUsers.getSn().equals(usersByNewEmail.getSn())) {
//                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
//            }
//            return currentUsers;
//        } else {
//            if(usersByNewUsername != null) {
//                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
//            }
//            if(usersByNewEmail != null) {
//                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
//            }
//            return null;
//        }
//    }
//
//}
