package com.emtechhouse.usersservice.MailService;

import com.emtechhouse.usersservice.SaccoEntity.SaccoEntity;
import com.emtechhouse.usersservice.SaccoEntity.SaccoEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Optional;
import java.util.Properties;

@Configuration
public class MailConfig {

    private final SaccoEntityRepository saccoEntityRepository;
    private JavaMailSender javaMailSender;

    @Autowired
    public MailConfig(SaccoEntityRepository saccoEntityRepository) {
        this.saccoEntityRepository = saccoEntityRepository;
    }

//    @Bean
//    public JavaMailSender javaMailSender() {
//        if (javaMailSender == null) {
//            initializeJavaMailSender();
//        }
//        return javaMailSender;
//    }

//    @Bean
//    public JavaMailSender javaMailSender() {
//        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//        mailSender.setHost("your_mail_host");
//        mailSender.setPort(587); // set your port
//        mailSender.setUsername("your_mail_username");
//        mailSender.setPassword("your_mail_password");
//
//        Properties props = mailSender.getJavaMailProperties();
//        props.put("mail.transport.protocol", "smtp");
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enable", "true");
//
//        return mailSender;
//    }

    public void updateJavaMailSenderConfig(String entityId) {
        Optional<SaccoEntity> dataCheck = saccoEntityRepository.findByEntityIdAndDeletedFlag(entityId, 'N');
        dataCheck.ifPresent(this::initializeJavaMailSender);
    }

    private void initializeJavaMailSender() {
        Optional<SaccoEntity> dataCheck = saccoEntityRepository.findByEntityIdAndDeletedFlag("001", 'N');
        dataCheck.ifPresent(this::initializeJavaMailSender);
    }

    private void initializeJavaMailSender(SaccoEntity newConfig) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(newConfig.getHost());
        mailSender.setPort(newConfig.getPort());
        mailSender.setProtocol(newConfig.getProtocol());
        mailSender.setUsername(newConfig.getSmtpUsername());
        mailSender.setPassword(newConfig.getSmtpPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", newConfig.getProtocol());
        props.put("mail.smtp.auth", String.valueOf(newConfig.isSmtpAuth()));
        props.put("mail.smtp.starttls.enable", String.valueOf(newConfig.isSmtpStarttlsEnable()));
        props.put("mail.smtp.ssl.trust", newConfig.getSslTrust());

        this.javaMailSender = mailSender;

        // Log the current JavaMailSender username
        logCurrentJavaMailSenderUsername();
    }

    private void logCurrentJavaMailSenderUsername() {
        if (javaMailSender != null) {
            System.out.println("Current Java Mailer username: " + ((JavaMailSenderImpl) javaMailSender).getUsername());
            System.out.println("Current Java Mailer Host: " + ((JavaMailSenderImpl) javaMailSender).getHost());
            System.out.println("Current Java Mailer Protocol: " + ((JavaMailSenderImpl) javaMailSender).getProtocol());
            System.out.println("Current Java Mailer Port: " + ((JavaMailSenderImpl) javaMailSender).getPort());
            System.out.println("Current Java Mailer Session: " + ((JavaMailSenderImpl) javaMailSender).getSession());
        } else {
            System.out.println("Java Mailer is not initialized yet.");
        }
    }
}
