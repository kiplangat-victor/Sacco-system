package com.emtechhouse.accounts.Utils.Security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Usersdata {
    private Long sn;
    @Column(length = 6, nullable = false)
    private String entityId;
    @Column(name = "username", length = 20, unique = true, nullable = false)
    private String username;
    @Column(name = "firstname", length = 10, nullable = false)
    private String firstName;
    @Column(name = "lastname", length = 10, nullable = false)
    private String lastName;
    @Column(name = "email", length = 150, nullable = false, unique = true)
    private String email;
    @Column(name = "phone", length = 12, nullable = false, unique = true)
    private String phoneNo;
    @Column(name = "password", length = 255, nullable = false)
    private String password;
    @Column(name = "sol_code", length = 5)
    private String solCode;
    @Column(name = "active")
    private boolean isAcctActive;
    @Column(name = "first_login", nullable = false, length = 1)
    private Character firstLogin;
    @Column(name = "locked")
    private boolean isAcctLocked;
    private boolean isTeller = false;
    private String tellerAccount;
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();
}
