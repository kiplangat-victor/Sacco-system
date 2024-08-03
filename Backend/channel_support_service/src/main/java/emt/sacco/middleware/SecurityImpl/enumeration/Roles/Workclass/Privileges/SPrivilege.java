package emt.sacco.middleware.SecurityImpl.enumeration.Roles.Workclass.Privileges;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import emt.sacco.middleware.SecurityImpl.enumeration.Roles.Workclass.Privileges.BasicActions.SBasicactions;
import emt.sacco.middleware.SecurityImpl.enumeration.Roles.Workclass.SWorkclass;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
//@EqualsAndHashCode(exclude = {"basicactions"})
public class SPrivilege {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    private boolean selected;
    private String code;

    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "workclassFk")
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private SWorkclass SWorkclass;

    @OneToMany(cascade = CascadeType.DETACH, fetch = FetchType.LAZY, mappedBy = "SPrivilege")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<SBasicactions> basicactions;

}
