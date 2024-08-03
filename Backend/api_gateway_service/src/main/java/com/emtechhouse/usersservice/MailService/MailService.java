package com.emtechhouse.usersservice.MailService;
import com.emtechhouse.usersservice.SaccoEntity.SaccoEntity;
import com.emtechhouse.usersservice.SaccoEntity.SaccoEntityRepository;
import com.emtechhouse.usersservice.utils.HttpInterceptor.EntityRequestContext;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
@Service
@Slf4j
public class MailService {
    @Autowired
    private SaccoEntityRepository saccoEntityRepository;
    @Value("${spring.organisation.company_logo_path}")
    private String company_logo_path;
    @Value("${spring.organisation.image_banner}")
    private String banner_path;
    @Value("${spring.application.enableEmail}")
    private String enableEmail;
    @Value("${spring.application.service.account_statement_download_path}")
    private String account_statement_download_path;

    @Autowired
    JavaMailSender javaMailSender;
    @Autowired
    MailConfig mailConfig;

    public void sendEmail(String entityId, String to, String message, String subject) throws MessagingException, IOException {
        System.out.println("-------------------------send email init---------------------------------------");
        mailConfig.updateJavaMailSenderConfig(entityId);
        Optional<SaccoEntity> dataCheck = saccoEntityRepository.findByEntityIdAndDeletedFlag(entityId, 'N');
        if (dataCheck.isPresent()){
            SaccoEntity saccoEntity = dataCheck.get();
            if (enableEmail.equalsIgnoreCase("false")) {
                System.out.println("----------------------------------------------------------------");
                System.out.println("Email sending is disabled! Check application.yml");
            } else {
                System.out.println("----------------------------------------------------------------");
                System.out.println("Email is enabled!");
                MimeMessage mimeMessage = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                helper.setTo(to);
//                String emailSalutation = "Dear Sir/Madam";
                helper.setFrom(saccoEntity.getSmtpUsername());
                helper.setSubject(subject);
                helper.setText(
                        "<!DOCTYPE html>\n" +
                                "<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\">\n" +
                                "<head>\n" +
                                "  <meta charset=\"utf-8\">\n" +
                                "  <meta name=\"viewport\" content=\"width=device-width,initial-scale=1\">\n" +
                                "  <meta name=\"x-apple-disable-message-reformatting\">\n" +
                                "  <title></title>\n" +
                                "  <!--[if mso]>\n" +
                                "  <style>\n" +
                                "    table {border-collapse:collapse;border-spacing:0;border:none;margin:0; margin-top:10px;margin-bottom:10px;}\n" +
                                "    div, td {padding:0;}\n" +
                                "    div {margin:0 !important;}\n" +
                                "  </style>\n" +
                                "  <noscript>\n" +
                                "    <xml>\n" +
                                "      <o:OfficeDocumentSettings>\n" +
                                "        <o:PixelsPerInch>96</o:PixelsPerInch>\n" +
                                "      </o:OfficeDocumentSettings>\n" +
                                "    </xml>\n" +
                                "  </noscript>\n" +
                                "  <![endif]-->\n" +
                                "  <style>\n" +
                                "    table, td, div, h1, p {\n" +
                                "      font-family: Arial, sans-serif;\\n\"\n" +
                                "    }\n" +
                                "    @media screen and (max-width: 530px) {\n" +
                                "      .unsub {\n" +
                                "        display: block;\n" +
                                "        padding: 8px;\n" +
                                "        margin-top: 14px;\n" +
                                "        border-radius: 6px;\n" +
                                "        background-color: #555555;\n" +
                                "        text-decoration: none !important;\n" +
                                "        font-weight: bold;\n" +
                                "      }\n" +
                                "      .col-lge {\n" +
                                "        max-width: 100% !important;\n" +
                                "      }\n" +
                                "    }\n" +
                                "    @media screen and (min-width: 531px) {\n" +
                                "      .col-sml {\n" +
                                "        max-width: 27% !important;\n" +
                                "      }\n" +
                                "      .col-lge {\n" +
                                "        max-width: 73% !important;\n" +
                                "      }\n" +
                                "    }\n" +
                                "  </style>\n" +
                                "</head>\n" +
                                "<body style=\" margin-top:10px; margin-bottom:10px; margin:0;padding:0;word-spacing:normal;background-color: #566fff;\">\n" +
//                            "  <div role=\"article\" aria-roledescription=\"email\" lang=\"en\" style=\"text-size-adjust:100%;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;background-color:#939297;\">\n" +
                                "  <div role=\"article\" aria-roledescription=\"email\" lang=\"en\" style=\"text-size-adjust:100%;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;background-color:#566fff;\">\n" +
                                "    <table role=\"presentation\" style=\"width:100%; padding-top: 10px; padding-bottom: 10px; border:none;border-spacing:0;\">\n" +
                                "      <tr>\n" +
                                "        <td align=\"center\" style=\"padding:0;\">\n" +
                                "          <!--[if mso]>\n" +
                                "          <table role=\"presentation\" align=\"center\" style=\"width:600px; margin-top: 10px; margin-bottom: 10px;\">\n" +
                                "          <tr>\n" +
                                "          <td>\n" +
                                "          <![endif]-->\n" +
                                "          <table role=\"presentation\" style=\"width:94%;max-width:600px;border:none;border-spacing:0;text-align:left;font-family:Arial,sans-serif;font-size:16px;line-height:22px;color:#363636;\">\n" +
                                "              <td style=\"padding:5px;text-align:center;font-size:12px;background-color:#ffffff;\">\n" +
                                "                <a href=\"http://www.example.com/\" style=\"text-decoration:none;\"><img src='cid:companyLogo' alt=\"Logo\" style=\"width:20%; text-align:center; margin:auto; height:auto;border:none;text-decoration:none;color:#ffffff;\"></a>\n" +
                                "                <hr>\n" +
                                "              </td>\n" +
                                "            <tr>\n" +
                                "              <td style=\"padding:30px;background-color:#ffffff;\">\n" +
                                "                    </p>\n" +
                                "                   <p style=\"margin:0;\">\n" + message + "\n" +
                                "                   <p style=\"margin:0;\">\n" + saccoEntity.getEmailRemarks() + "\n" +
                                "                    </p>\n" +
                                "                   <p style=\"margin:0;\">\n" + saccoEntity.getEmailRegards() + "\n" +
                                "                    </p>\n" +
                                "                   <p style=\"margin:0;\">\n" + saccoEntity.getEntityName() + "\n" +
                                "                    </p>\n" +
                                "                   <p style=\"margin:0;\">\n" + "<b>Tel/Phone: </b> " + saccoEntity.getEntityPhoneNumber() + "\n" +
                                "                    </p>\n" +
                                "                   <p style=\"margin:0;\">\n" + "<b>Email: </b> " + saccoEntity.getEntityEmail() + "\n" +
                                "                    </p>\n" +
                                "                   <p style=\"margin:0;\">\n" + "<b>Address: </b> " + saccoEntity.getEntityAddress() + "\n" +
                                "                    </p>\n" +
                                "                   <p style=\"margin:0;\">\n" + "<b>Location: </b> " + saccoEntity.getEntityLocation() + "\n" +
                                "                    </p>\n" +
                                "                   <p style=\"margin:0;\">\n" + "<b>Website: </b> " + saccoEntity.getEntityWebsite() + "\n" +
                                "                    </p>\n" +
                                "              </td>\n" +
                                "            </tr>\n" +
                                "              <td style=\"padding:0px; margin-bottom: 0px;text-align:center;font-size:12px;background-color:#ffffff;\">\n" +
                                "                       <img src='cid:bannerImage' style='width:100%;'/>" +
                                "              </td>\n" +
                                "              <td style=\"padding:0px; margin-bottom: 0px;text-align:center;font-size:12px;background-color:#ffffff;\">\n" +
                                "              </td>\n" +
                                "            <tr>\n" +
                                "            </tr>\n" +
                                "           \n" +
                                "            <tr>\n" +
                                "              <td style=\"padding:30px;text-align:center;font-size:12px;background-color:#001c27;color:#cccccc;\">\n" +
                                "              <p style=\"margin:0;font-size:14px;line-height:20px;\">&reg; copyright 2021<br></p>\n" +
                                "              </td>\n" +
                                "            </tr>\n" +
                                "          </table>\n" +
                                "          <!--[if mso]>\n" +
                                "          </td>\n" +
                                "          </tr>\n" +
                                "          </table>\n" +
                                "          <![endif]-->\n" +
                                "        </td>\n" +
                                "      </tr>\n" +
                                "    </table>\n" +
                                "  </div>\n" +
                                "</body>\n" +
                                "</html>", true);
//                String companyLogoBase64 = removeInvalidCharacters(saccoEntity.getEntityImageLogo());
//                byte[] decodedLogoImageBytes = Base64.getDecoder().decode(companyLogoBase64);
//                String logoImageType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(decodedLogoImageBytes));
//                System.out.println(logoImageType);
//                DataSource companyLogoDataSource = new ByteArrayDataSource(Base64.getDecoder().decode(companyLogoBase64), logoImageType);
//                helper.addInline("companyLogo", companyLogoDataSource);
//                // Add inline image - bannerImage
//                String bannerImageBase64 = removeInvalidCharacters(saccoEntity.getEntityImageBanner());
//                byte[] decodedImageBytes = Base64.getDecoder().decode(bannerImageBase64);
//                String imageType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(decodedImageBytes));
//                System.out.println(imageType);
//                DataSource bannerImageDataSource = new ByteArrayDataSource(Base64.getDecoder().decode(bannerImageBase64), imageType);
//                helper.addInline("bannerImage", bannerImageDataSource);


                // Handle companyLogoBase64
                String companyLogoBase64 = saccoEntity.getEntityImageLogo();
                // Remove the data URI prefix
                companyLogoBase64 = companyLogoBase64.replaceAll("^data:[^;]+;base64,", "");
                byte[] decodedLogoImageBytes = null;

                try {
                    decodedLogoImageBytes = Base64.getDecoder().decode(companyLogoBase64);
                    String logoImageType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(decodedLogoImageBytes));
                    DataSource companyLogoDataSource = new ByteArrayDataSource(decodedLogoImageBytes, logoImageType);
                    helper.addInline("companyLogo", companyLogoDataSource);
                } catch (IllegalArgumentException e) {
                    // Handle the exception, or simply log the error message
                    System.err.println("Error decoding companyLogoBase64. Skipping adding inline image.");
                }

                // Handle bannerImageBase64
                String bannerImageBase64 = saccoEntity.getEntityImageBanner();
                // Remove the data URI prefix
                bannerImageBase64 = bannerImageBase64.replaceAll("^data:[^;]+;base64,", "");
                byte[] decodedImageBytes = null;

                try {
                    decodedImageBytes = Base64.getDecoder().decode(bannerImageBase64);
                    String imageType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(decodedImageBytes));
                    DataSource bannerImageDataSource = new ByteArrayDataSource(decodedImageBytes, imageType);
                    helper.addInline("bannerImage", bannerImageDataSource);
                } catch (IllegalArgumentException e) {
                    // Handle the exception, or simply log the error message
                    System.err.println("Error decoding bannerImageBase64. Skipping adding inline image.");
                }

//                String companyLogoBase64 = saccoEntity.getEntityImageLogo();
//
//                System.out.println(companyLogoBase64);
//                System.out.println("image above");
//                byte[] decodedLogoImageBytes = null;
//
//                try {
//                    decodedLogoImageBytes = Base64.getDecoder().decode(companyLogoBase64.getBytes(StandardCharsets.UTF_8));
//
//                    String logoImageType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(decodedLogoImageBytes));
//                    DataSource companyLogoDataSource = new ByteArrayDataSource(decodedLogoImageBytes, logoImageType);
//                    helper.addInline("companyLogo", companyLogoDataSource);
//                } catch (IllegalArgumentException e) {
//                    e.printStackTrace();
//                    // Handle the exception, or simply log the error message
//                    System.err.println("Error decoding companyLogoBase64. Skipping adding inline image.");
//                }
//
//// Similarly, handle bannerImageBase64 with a try-catch block
//                String bannerImageBase64 = saccoEntity.getEntityImageBanner();
//                System.out.println(bannerImageBase64);
//                byte[] decodedImageBytes = null;
//
//                try {
//                    decodedImageBytes = Base64.getDecoder().decode(bannerImageBase64.getBytes(StandardCharsets.UTF_8));
//                    String imageType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(decodedImageBytes));
//                    DataSource bannerImageDataSource = new ByteArrayDataSource(decodedImageBytes, imageType);
//                    helper.addInline("bannerImage", bannerImageDataSource);
//                } catch (IllegalArgumentException e) {
//                    // Handle the exception, or simply log the error message
//                    System.err.println("Error decoding bannerImageBase64. Skipping adding inline image.");
//
//                    e.printStackTrace();
//                }



                javaMailSender.send(mimeMessage);
                System.out.println("Mail sent successfully to: " + to);
                log.info("Sent successfully,sent to: {}", to);
            }
        }

        System.out.println("-------------------------send email end---------------------------------------");
    }
    private static String removeInvalidCharacters(String base64String) {
        // Remove characters that are not valid in Base64 encoding
        return base64String.replaceAll("3a", "");
    }

    public void sendStatementToEmail(String entityId, String to, String message, String subject, String fullName, String acid) throws MessagingException, IOException {
        System.out.println("-------------------------send email init---------------------------------------");
        System.out.println("Email details: "+entityId+" "+to+" "+subject);
        mailConfig.updateJavaMailSenderConfig(entityId);
        Optional<SaccoEntity> dataCheck = saccoEntityRepository.findByEntityIdAndDeletedFlag(entityId, 'N');
        if (dataCheck.isPresent()){
            SaccoEntity saccoEntity = dataCheck.get();
            if (enableEmail.equalsIgnoreCase("false")) {
                System.out.println("----------------------------------------------------------------");
                System.out.println("Email sending is disabled! Check application.yml");
            } else {
                System.out.println("----------------------------------------------------------------");
                System.out.println("Email is enabled!");
                MimeMessage mimeMessage = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

                LocalDateTime currentTime = LocalDateTime.now();
                LocalDateTime oneYearAgo = currentTime.minusYears(1);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String current_time = currentTime.format(formatter);
                String aYearAgo  = oneYearAgo.format(formatter);

                String url= "http://localhost:9008/api/v1/reports/account-statement?acid="+acid+"&fromdate="+aYearAgo+"&todate="+current_time+"";
                System.out.println(url);
                //String downloadPath = "C:/Users/Francis/Downloads/"+fullName+"_"+new Random().nextInt(9000)+".pdf";
                String downloadPath = account_statement_download_path+fullName+"_"+new Random().nextInt(9000)+".pdf";
                downloadAccountStatementPdf(url,downloadPath);
                helper.setTo(to);
                helper.setFrom(saccoEntity.getSmtpUsername());
                helper.setSubject(subject);
                helper.addAttachment(fullName+" Account-statement", new File(downloadPath));
                helper.setText(
                        "<!DOCTYPE html>\n" +
                                "<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\">\n" +
                                "<head>\n" +
                                "  <meta charset=\"utf-8\">\n" +
                                "  <meta name=\"viewport\" content=\"width=device-width,initial-scale=1\">\n" +
                                "  <meta name=\"x-apple-disable-message-reformatting\">\n" +
                                "  <title></title>\n" +
                                "  <!--[if mso]>\n" +
                                "  <style>\n" +
                                "    table {border-collapse:collapse;border-spacing:0;border:none;margin:0; margin-top:10px;margin-bottom:10px;}\n" +
                                "    div, td {padding:0;}\n" +
                                "    div {margin:0 !important;}\n" +
                                "  </style>\n" +
                                "  <noscript>\n" +
                                "    <xml>\n" +
                                "      <o:OfficeDocumentSettings>\n" +
                                "        <o:PixelsPerInch>96</o:PixelsPerInch>\n" +
                                "      </o:OfficeDocumentSettings>\n" +
                                "    </xml>\n" +
                                "  </noscript>\n" +
                                "  <![endif]-->\n" +
                                "  <style>\n" +
                                "    table, td, div, h1, p {\n" +
                                "      font-family: Arial, sans-serif;\\n\"\n" +
                                "    }\n" +
                                "    @media screen and (max-width: 530px) {\n" +
                                "      .unsub {\n" +
                                "        display: block;\n" +
                                "        padding: 8px;\n" +
                                "        margin-top: 14px;\n" +
                                "        border-radius: 6px;\n" +
                                "        background-color: #555555;\n" +
                                "        text-decoration: none !important;\n" +
                                "        font-weight: bold;\n" +
                                "      }\n" +
                                "      .col-lge {\n" +
                                "        max-width: 100% !important;\n" +
                                "      }\n" +
                                "    }\n" +
                                "    @media screen and (min-width: 531px) {\n" +
                                "      .col-sml {\n" +
                                "        max-width: 27% !important;\n" +
                                "      }\n" +
                                "      .col-lge {\n" +
                                "        max-width: 73% !important;\n" +
                                "      }\n" +
                                "    }\n" +
                                "  </style>\n" +
                                "</head>\n" +
                                "<body style=\" margin-top:10px; margin-bottom:10px; margin:0;padding:0;word-spacing:normal;background-color: #566fff;\">\n" +
//                            "  <div role=\"article\" aria-roledescription=\"email\" lang=\"en\" style=\"text-size-adjust:100%;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;background-color:#939297;\">\n" +
                                "  <div role=\"article\" aria-roledescription=\"email\" lang=\"en\" style=\"text-size-adjust:100%;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;background-color:#566fff;\">\n" +
                                "    <table role=\"presentation\" style=\"width:100%; padding-top: 10px; padding-bottom: 10px; border:none;border-spacing:0;\">\n" +
                                "      <tr>\n" +
                                "        <td align=\"center\" style=\"padding:0;\">\n" +
                                "          <!--[if mso]>\n" +
                                "          <table role=\"presentation\" align=\"center\" style=\"width:600px; margin-top: 10px; margin-bottom: 10px;\">\n" +
                                "          <tr>\n" +
                                "          <td>\n" +
                                "          <![endif]-->\n" +
                                "          <table role=\"presentation\" style=\"width:94%;max-width:600px;border:none;border-spacing:0;text-align:left;font-family:Arial,sans-serif;font-size:16px;line-height:22px;color:#363636;\">\n" +
                                "              <td style=\"padding:5px;text-align:center;font-size:12px;background-color:#ffffff;\">\n" +
                                "                <a href=\"http://www.example.com/\" style=\"text-decoration:none;\"><img src='cid:companyLogo' alt=\"Logo\" style=\"width:20%; text-align:center; margin:auto; height:auto;border:none;text-decoration:none;color:#ffffff;\"></a>\n" +
                                "                <hr>\n" +
                                "              </td>\n" +
                                "            <tr>\n" +
                                "              <td style=\"padding:30px;background-color:#ffffff;\">\n" +
                                "                    </p>\n" +
                                "                   <p style=\"margin:0;\">\n" + message + "\n" +
                                "                   <p style=\"margin:0;\">\n" + saccoEntity.getEmailRemarks() + "\n" +
                                "                    </p>\n" +
                                "                   <p style=\"margin:0;\">\n" + saccoEntity.getEmailRegards() + "\n" +
                                "                    </p>\n" +
                                "                   <p style=\"margin:0;\">\n" + saccoEntity.getEntityName() + "\n" +
                                "                    </p>\n" +
                                "                   <p style=\"margin:0;\">\n" + "<b>Tel/Phone: </b> " + saccoEntity.getEntityPhoneNumber() + "\n" +
                                "                    </p>\n" +
                                "                   <p style=\"margin:0;\">\n" + "<b>Email: </b> " + saccoEntity.getEntityEmail() + "\n" +
                                "                    </p>\n" +
                                "                   <p style=\"margin:0;\">\n" + "<b>Address: </b> " + saccoEntity.getEntityAddress() + "\n" +
                                "                    </p>\n" +
                                "                   <p style=\"margin:0;\">\n" + "<b>Location: </b> " + saccoEntity.getEntityLocation() + "\n" +
                                "                    </p>\n" +
                                "                   <p style=\"margin:0;\">\n" + "<b>Website: </b> " + saccoEntity.getEntityWebsite() + "\n" +
                                "                    </p>\n" +
                                "              </td>\n" +
                                "            </tr>\n" +
                                "              <td style=\"padding:0px; margin-bottom: 0px;text-align:center;font-size:12px;background-color:#ffffff;\">\n" +
                                "                       <img src='cid:bannerImage' style='width:100%;'/>" +
                                "              </td>\n" +
                                "              <td style=\"padding:0px; margin-bottom: 0px;text-align:center;font-size:12px;background-color:#ffffff;\">\n" +
                                "              </td>\n" +
                                "            <tr>\n" +
                                "            </tr>\n" +
                                "           \n" +
                                "            <tr>\n" +
                                "              <td style=\"padding:30px;text-align:center;font-size:12px;background-color:#001c27;color:#cccccc;\">\n" +
                                "              <p style=\"margin:0;font-size:14px;line-height:20px;\">&reg; copyright 2021<br></p>\n" +
                                "              </td>\n" +
                                "            </tr>\n" +
                                "          </table>\n" +
                                "          <!--[if mso]>\n" +
                                "          </td>\n" +
                                "          </tr>\n" +
                                "          </table>\n" +
                                "          <![endif]-->\n" +
                                "        </td>\n" +
                                "      </tr>\n" +
                                "    </table>\n" +
                                "  </div>\n" +
                                "</body>\n" +
                                "</html>", true);

                String LogoBase64 = saccoEntity.getEntityImageLogo();
                // Remove the data URI prefix
                LogoBase64 = LogoBase64.replaceAll("^data:[^;]+;base64,", "");
                byte[] decodedLogoImageByte = null;

                try {
                    decodedLogoImageByte = Base64.getDecoder().decode(LogoBase64);
                    String logoImageType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(decodedLogoImageByte));
                    DataSource companyLogoDataSource = new ByteArrayDataSource(decodedLogoImageByte, logoImageType);
                    helper.addInline("companyLogo", companyLogoDataSource);
                } catch (IllegalArgumentException e) {
                    // Handle the exception, or simply log the error message
                    System.err.println("Error decoding companyLogoBase64. Skipping adding inline image.");
                }

                // Handle bannerImageBase64
                String bannerImageBase64 = saccoEntity.getEntityImageBanner();
                // Remove the data URI prefix
                bannerImageBase64 = bannerImageBase64.replaceAll("^data:[^;]+;base64,", "");
                byte[] decodedImageByte = null;

                try {
                    decodedImageByte = Base64.getDecoder().decode(bannerImageBase64);
                    String imageType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(decodedImageByte));
                    DataSource bannerImageDataSource = new ByteArrayDataSource(decodedImageByte, imageType);
                    helper.addInline("bannerImage", bannerImageDataSource);
                } catch (IllegalArgumentException e) {
                    // Handle the exception, or simply log the error message
                    System.err.println("Error decoding bannerImageBase64. Skipping adding inline image.");
                }

                javaMailSender.send(mimeMessage);
                System.out.println("Mail sent successfully to: " + to);
                log.info("Sent successfully,sent to: {}", to);
            }
        }

        System.out.println("-------------------------send email end---------------------------------------");
    }

    private static void downloadAccountStatementPdf(String url, String localFilePath) throws IOException {
        URL pdfUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) pdfUrl.openConnection();

        try (InputStream inputStream = connection.getInputStream();
             FileOutputStream outputStream = new FileOutputStream(localFilePath)) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } finally {
            connection.disconnect();
        }

        System.out.println("-------------------------send email end---------------------------------------");
    }
//    private static String removeInvalidCharacters(String base64String) {
//        // Remove characters that are not valid in Base64 encoding
//        return base64String.replaceAll("3a", "");
//    }
}



