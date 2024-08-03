package com.emtechhouse.System.Utils.Security;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Role {
    private Long id;
    @Column(length = 20, nullable = false, unique = true)
    private String name;

}