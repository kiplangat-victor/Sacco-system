package emt.sacco.middleware.SecurityImpl.enumeration.Roles.Workclass;


import emt.sacco.middleware.SecurityImpl.enumeration.Roles.Workclass.Privileges.SPrivilege;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class WorkclassRequest {
    private String workClass;
    private Long roleId;
    private List<SPrivilege> SPrivileges;
}
