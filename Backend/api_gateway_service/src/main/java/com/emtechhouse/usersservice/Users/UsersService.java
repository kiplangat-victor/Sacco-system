package com.emtechhouse.usersservice.Users;

import com.emtechhouse.usersservice.DTO.Mailparams;
import com.emtechhouse.usersservice.MailService.MailService;
import com.emtechhouse.usersservice.OTP.OTP;
import com.emtechhouse.usersservice.OTP.OTPRepository;
import com.emtechhouse.usersservice.Responses.JwtResponse;
import com.emtechhouse.usersservice.Roles.RoleRepository;
import com.emtechhouse.usersservice.utils.AccountStatement;
import com.emtechhouse.usersservice.utils.EntityResponse;
import com.emtechhouse.usersservice.utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.usersservice.utils.Transactions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class UsersService {
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    private final Date today = new Date();
    @Autowired
    OTPRepository otpRepository;

    @Autowired
    MailService mailService;

    public Users userRegistration(Users user) {
        roleRepository.saveAll(user.getRoles());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return usersRepository.save(user);
    }

    public Users updateUser(Users user) {
        return usersRepository.save(user);
    }

    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }

    public Users getUser(Long id) {
        return usersRepository.findById(id).orElse(null);
    }

    public List<Users> undeletedUsers() {
        return usersRepository.findAllByDeletedFlagAndEntityId('N', EntityRequestContext.getCurrentEntityId() );
    }

    public EntityResponse<List<AccountStatement>> sendStatement(String acid, Integer maxCount) throws MessagingException, IOException {
        EntityResponse entityResponse = new EntityResponse();
        System.out.println("Acid is "+acid+ " and entityId is:"+EntityRequestContext.getCurrentEntityId());
        UsersRepository.getMemberDetails details = usersRepository.getMemberDetails(EntityRequestContext.getCurrentEntityId(), acid);
        List<AccountStatement> accountTransactionsList = usersRepository.getAccountStatement(acid, maxCount);
        if (accountTransactionsList != null && !accountTransactionsList.isEmpty()) {


                String mailMessage = "<p>Dear "+details.getFirstName()+" "+details.getLastName()+"</p>\n" +
                        "    \n" +
                        "    <p>Your Account mini statement was generated successfully</p>\n" +
                        "    \n" +
                        "    <ul>\n" +
                        "        <li><strong>Please download your statement from the attachment </strong> </li>\n" +
                        "    </ul>\n" +
                        "    \n" +
                        "    <p>Thank you for choosing us.</p>";

                mailService.sendStatementToEmail(details.getEntityId(), details.getEmailAddress(), mailMessage, "Account statement generated successfully", details.getFirstName()+" "+details.getLastName(), acid);
                Mailparams mailsample = new Mailparams();
                mailsample.setEmail(details.getEmailAddress());
                mailsample.setSubject("Account statement was Successfully generated");
                mailsample.setMessage(mailMessage);

        }
        entityResponse.setStatusCode(HttpStatus.OK.value());
        entityResponse.setEntity(accountTransactionsList);
        entityResponse.setMessage("Statement generated successfully");
        return entityResponse;
    }

}
