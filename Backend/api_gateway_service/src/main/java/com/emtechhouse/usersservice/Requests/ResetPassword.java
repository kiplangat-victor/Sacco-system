package com.emtechhouse.usersservice.Requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ResetPassword {
    private String email;
    private String password;
    private String confirmPassword;
}
