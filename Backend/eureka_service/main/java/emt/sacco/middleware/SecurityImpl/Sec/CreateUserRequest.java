package emt.sacco.middleware.SecurityImpl.Sec;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
@Data
public class CreateUserRequest {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String role;
    private boolean isActive;
    private boolean isNonLocked;
    private MultipartFile profileImage;
}
