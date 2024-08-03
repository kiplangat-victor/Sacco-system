package emt.sacco.middleware.SecurityImpl.Sec;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import emt.sacco.middleware.SecurityImpl.enumeration.Roles.SRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

//
//@Entity
//public class User implements Serializable {
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    @Column(nullable = false, updatable = false)
//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
//    private Long id;
//    private String userId;
//    private String firstName;
//    private String lastName;
//    private String username;
//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
//    private String password;
//    private String email;
//    private String profileImageUrl;
//    private Date lastLoginDate;
//    private Date lastLoginDateDisplay;
//    private Date joinDate;
//    private String role; //ROLE_USER{ read, edit }, ROLE_ADMIN {delete}
//    private String[] authorities;
//    private boolean isActive;
//    private boolean isNotLocked;
//
//    public User(){}
//
//    public User(Long id, String userId, String firstName, String lastName, String username, String password, String email, String profileImageUrl, Date lastLoginDate, Date lastLoginDateDisplay, Date joinDate, String role, String[] authorities, boolean isActive, boolean isNotLocked) {
//        this.id = id;
//        this.userId = userId;
//        this.firstName = firstName;
//        this.lastName = lastName;
//        this.username = username;
//        this.password = password;
//        this.email = email;
//        this.profileImageUrl = profileImageUrl;
//        this.lastLoginDate = lastLoginDate;
//        this.lastLoginDateDisplay = lastLoginDateDisplay;
//        this.joinDate = joinDate;
//        this.role = role;
//        this.authorities = authorities;
//        this.isActive = isActive;
//        this.isNotLocked = isNotLocked;
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getUserId() {
//        return userId;
//    }
//
//    public void setUserId(String userId) {
//        this.userId = userId;
//    }
//
//    public String getFirstName() {
//        return firstName;
//    }
//
//    public void setFirstName(String firstName) {
//        this.firstName = firstName;
//    }
//
//    public String getLastName() {
//        return lastName;
//    }
//
//    public void setLastName(String lastName) {
//        this.lastName = lastName;
//    }
//
//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public String getProfileImageUrl() {
//        return profileImageUrl;
//    }
//
//    public void setProfileImageUrl(String profileImageUrl) {
//        this.profileImageUrl = profileImageUrl;
//    }
//
//    public Date getLastLoginDate() {
//        return lastLoginDate;
//    }
//
//    public void setLastLoginDate(Date lastLoginDate) {
//        this.lastLoginDate = lastLoginDate;
//    }
//
//    public Date getLastLoginDateDisplay() {
//        return lastLoginDateDisplay;
//    }
//
//    public void setLastLoginDateDisplay(Date lastLoginDateDisplay) {
//        this.lastLoginDateDisplay = lastLoginDateDisplay;
//    }
//
//    public Date getJoinDate() {
//        return joinDate;
//    }
//
//    public void setJoinDate(Date joinDate) {
//        this.joinDate = joinDate;
//    }
//
//    public String getRole() {
//        return role;
//    }
//
//    public void setRole(String role) {
//        this.role = role;
//    }
//
//    public String[] getAuthorities() {
//        return authorities;
//    }
//
//    public void setAuthorities(String[] authorities) {
//        this.authorities = authorities;
//    }
//
//    public boolean isActive() {
//        return isActive;
//    }
//
//    public void setActive(boolean active) {
//        isActive = active;
//    }
//
//    public boolean isNotLocked() {
//        return isNotLocked;
//    }
//
//    public void setNotLocked(boolean notLocked) {
//        isNotLocked = notLocked;
//    }



@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
@Table(name = "switchUsers")
public class SwitchUsers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sn", updatable = false)
    private Long sn;
    @Column(nullable = false)
    private String entityId ="001";
    @Column(name = "username", length = 50, unique = true, nullable = false)
    private String username;
    @Column(name = "firstname", length = 50, nullable = false)
    private String firstName;
    @Column(name = "lastname", length = 50, nullable = false)
    private String lastName;

    @Column(name = "email", length = 150, nullable = false, unique = true)
    private String email;
    @Column(name = "phone", length = 50, nullable = false)
    private String phoneNo;
    @Column(length = 50)
    private String memberCode;

    @Column(name = "password", length = 255, nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private Character isSystemGenPassword = 'Y';
    @Column(name = "sol_code", length = 10)
    private String solCode;
    @Column(name = "active")
    private Boolean isAcctActive = true;
    @Column(name = "first_login", nullable = false, length = 1)
    private Character firstLogin;
    @Column(name = "locked")
    public Boolean isAcctLocked;
    private String isTeller = "No";
    @Column(nullable = false, length = 3)
    private String isEntityUser;
    @Column(length = 5)
    private String isBoardMember = "No";
    private String tellerAccount;
    private  String onBoardingMethod;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "suser_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<SRole> SRoles = new HashSet<>();

    private Long workclassFk;
    //*****************Operational Audit *********************
    @Column(length = 30, nullable = false)
    @JsonIgnore
    private String postedBy;
    @Column(nullable = false)
    @JsonIgnore
    private Character postedFlag = 'Y';
    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date postedTime;
    @JsonIgnore
    private String modifiedBy;
    @JsonIgnore
    private Character modifiedFlag = 'N';
    @JsonIgnore
    private Date modifiedTime;
    @JsonIgnore
    private String verifiedBy;
    @JsonIgnore
    private Character verifiedFlag = 'N';
    @JsonIgnore
    private Date verifiedTime;
    @JsonIgnore
    private String deletedBy;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Character deletedFlag = 'N';
    @JsonIgnore
    private Date deletedTime;






    private String[] authorities;
    private Date lastLoginDateDisplay;


    private String userId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)

    private String profileImageUrl;
    private Date lastLoginDate;

    private Date joinDate;
    private String role; //ROLE_USER{ read, edit }, ROLE_ADMIN {delete}

    private boolean isActive;
    private boolean isNotLocked;
}
