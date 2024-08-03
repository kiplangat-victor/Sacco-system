package com.emtechhouse.usersservice.Responses;

import com.emtechhouse.usersservice.Roles.Role;
import com.emtechhouse.usersservice.Roles.Workclass.Workclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class JwtResponse {
    private boolean otpEnabled = false;
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String firstName;
    private String lastName;

    private String email;
    private Set<Role> roles;
    private String solCode;
    private String entityId;
    private Character firstLogin;
    private Workclass workclasses;
    private UUID uuid;
    private String Status;
    private String loginAt;
    private String address;
    private String os;
    private String browser;
    private String agencyAc;
    private String tellerAc;
    private String memberCode;
    private Character isSystemGenPassword;
}
