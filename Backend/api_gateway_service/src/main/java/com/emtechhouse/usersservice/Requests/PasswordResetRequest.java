package com.emtechhouse.usersservice.Requests;

import com.emtechhouse.usersservice.utils.ValidationConstraints.Password;
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
