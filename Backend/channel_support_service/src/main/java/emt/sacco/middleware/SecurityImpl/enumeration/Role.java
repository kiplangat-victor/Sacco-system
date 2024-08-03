package emt.sacco.middleware.SecurityImpl.enumeration;

import static emt.sacco.middleware.SecurityImpl.constants.Authority.*;

public enum Role {
    ROLE_USER(USER_AUTHORITIES),
    ROLE_HR(HR_AUTHORITIES),
    ROLE_MANAGER(MANAGER_AUTHORITIES),
    ROLE_ADMIN(ADMIN_AUTHORITIES),
    ROLE_SUPER_ADMIN(SUPER_ADMIN_AUTHORITIES);

//
//    ROLE_USER,
//    ROLE_TELLER,
//    ROLE_NONE,
//    ROLE_OFFICER,
//    ROLE_SENIOR_OFFICER,
//    ROLE_MANAGER,
//    ROLE_SUPERUSER
    private String[] authorities;

    Role(String... authorities) {
        this.authorities = authorities;
    }

    public String[] getAuthorities() {
        return authorities;
    }
}
