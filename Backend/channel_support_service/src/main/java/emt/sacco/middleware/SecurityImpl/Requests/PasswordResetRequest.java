package emt.sacco.middleware.SecurityImpl.Requests;

import emt.sacco.middleware.Utils.ValidationConstraints.Password;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PasswordResetRequest {
    private String emailAddress;
    @Password
    private String password;
    private String confirmPassword;
}
