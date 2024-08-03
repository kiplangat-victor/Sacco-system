package com.emtechhouse.usersservice.Requests;

import com.emtechhouse.usersservice.utils.ValidationConstraints.Password;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class SignupRequest {
    private Long sn;
    @NotBlank
    private String username;
    @NotBlank
    @Size(max = 50)
    @Email
    private String email;
    private String roleFk;
//    @Password
    private String password;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotBlank
//    @Size(min = 12, max = 13)
    private String phoneNo;
    @NotBlank
    private String solCode;
    @NotBlank
    private String entityId;
    private String isEntityUser;
    private String isTeller = "No";
    private String workclassFk;
    private String memberCode;
    private  String onBoardingMethod;
}