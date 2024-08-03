package emt.sacco.middleware.SecurityImpl.Sec;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
@Component
@Data
public class UserDetailsImpl implements UserDetails {
    private SwitchUsers switchUsers;
    private final Long id;
    private final String username;
    private final String email;
    @JsonIgnore
    private final String password;

    public UserDetailsImpl(SwitchUsers switchUsers, Long id, String username, String email, String password, Collection<? extends GrantedAuthority> authorities){
        this.switchUsers = switchUsers;
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id, String username, String email, String password,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }


    public static UserDetailsImpl build(SwitchUsers user) {
        List<GrantedAuthority> authorities = user.getSRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getSn(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                authorities);
    }
    public UserDetailsImpl() {
        // Default constructor
        this.id = null;
        this.username = null;
        this.email = null;
        this.password = null;
        this.authorities = null;
    }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return stream(this.users.getAuthorities()).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
//       // return stream(this.users.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName()))).collect(Collectors.toList());
//    }
//
//    @Override
//    public String getPassword() {
//        return this.users.getPassword();
//    }
//
//    @Override
//    public String getUsername() {
//        return this.users.getUsername();
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
////        return this.user.isNotLocked();
//        return this.users.getIsAcctLocked();
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
////        return this.user.isActive();
//        return this.users.getIsAcctActive();
//    }
@Override
public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
}

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }
}
