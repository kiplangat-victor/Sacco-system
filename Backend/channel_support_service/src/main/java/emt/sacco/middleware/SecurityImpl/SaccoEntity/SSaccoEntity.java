package emt.sacco.middleware.SecurityImpl.SaccoEntity;

import emt.sacco.middleware.SecurityImpl.EntityBranch.SEntityBranch;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
public class SSaccoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 200, nullable = false, unique = true)
    @JsonProperty(required = true)
    private String entityId;
    @Column(length = 1000, nullable = false, unique = false)
    private String entityDescription;
    @Column(length = 100, nullable = false, unique = false)
    private  String entityLocation;
    @Column(length = 100, nullable = false,unique = false)
    private String entityEmail;
    @Column(length = 12, unique = false)
    private String entityPhoneNumber;
    private String entityStatus;
    @Column(length = 50, unique = false)
    private String emailRegards;
    private String emailMessage;
    private String entityName;
    private String emailRemarks;
    private String entityWebsite;
    private String entityAddress;
    @Column(length = 100, nullable = false, unique = false)
    private String customTitlebarBg;
    @Column(length = 100, nullable = false, unique = false)
    private String customSidebarBg;
    @Lob
    private String entityImageLogo;
    @Lob
    private String entityImageBanner;
    @Column(length = 100, nullable = false, unique = false)
    private String protocol;
    @Column(length = 100, nullable = false, unique = false)
    private String host;
    @Column(length = 100, nullable = false, unique = false)
    private int port;
    @Column(length = 100, nullable = false, unique = false)
    private String smtpUsername;
    @Column(length = 100, nullable = false, unique = false)
    private String smtpPassword;
    @Column(length = 100, nullable = false, unique = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean smtpAuth = true;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean smtpStarttlsEnable = true;
    @Column(length = 100, nullable = false, unique = false)
    private String sslTrust;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String secretKey = "asjakskajsajisasa6sa78s90q3232as"; // 32 bytes
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String algorithm = "AES";
//    public void setSmtpPassword(String password) {
//        try {
//            Cipher cipher = Cipher.getInstance(algorithm);
//            SecretKey secretKeySpec = new SecretKeySpec(secretKey.getBytes(), algorithm);
//            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
//            byte[] encryptedPasswordBytes = cipher.doFinal(password.getBytes());
//            this.smtpPassword = Base64.getEncoder().encodeToString(encryptedPasswordBytes);
//        } catch (Exception e) {
//            e.printStackTrace();
//            // Handle encryption exception
//        }
//    }
//    public String getSmtpPassword() {
//        try {
//            Cipher cipher = Cipher.getInstance(algorithm);
//            SecretKey secretKeySpec = new SecretKeySpec(secretKey.getBytes(), algorithm);
//            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
//            byte[] decryptedPasswordBytes = cipher.doFinal(Base64.getDecoder().decode(this.smtpPassword));
//            return new String(decryptedPasswordBytes);
//        } catch (Exception e) {
//            e.printStackTrace();
//            // Handle decryption exception
//            return null;
//        }
//    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "SSaccoEntity")
//    @JsonIgnore
    private List<SEntityBranch> SEntityBranches;
    //*****************Operational Audit *********************
    @Column(length = 30, nullable = false)
    private String postedBy;
    @Column(nullable = false)
    private Character postedFlag = 'Y';
    @Column(nullable = false)
    private LocalDate postedOn;
    private String modifiedBy;
    private Character modifiedFlag = 'N';
    private LocalDate modifiedOn;
    private String verifiedBy;
    private Character verifiedFlag = 'N';
    private LocalDate verifiedOn;
    private String deletedBy;
    private Character deletedFlag = 'N';
    private LocalDate deletedOn;
}