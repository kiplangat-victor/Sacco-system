package com.emtechhouse.usersservice.Roles.Workclass.Privileges;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Privilege_Actions {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Id
    private Long privilegeId;
    private String privilegeName;
    private Long basicActionId;
    private String basicActionName;
    @Column(length = 6, nullable = false)
    private String entityId;
}
