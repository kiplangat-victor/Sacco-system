package emt.sacco.middleware.Utils.Config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "sacco.urls")
public class UrlConfig {
    private String retailMemberCreation;
    private String retrieveRetailMember;
    private String accountCreation;
    private String retrieveAccount;
    private String  verifyAccount;
    private String  disbursementVerifier;
}
