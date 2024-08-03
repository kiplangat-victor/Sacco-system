package com.emtechhouse.usersservice.Roles.Workclass;


import com.emtechhouse.usersservice.Roles.Workclass.Privileges.Privilege;
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
    private List<Privilege> privileges;
}
