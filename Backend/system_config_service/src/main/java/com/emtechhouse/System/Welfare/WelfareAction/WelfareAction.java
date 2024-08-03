package com.emtechhouse.System.Welfare.WelfareAction;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class WelfareAction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 12, nullable = false, updatable = false, unique = true)
    @JsonProperty(required = true)
    private String actionCode;
    @Column(nullable = false)
    private Boolean allowAccountChange = false;
    @Column(length = 60, nullable = false)
    private String actionName;
    @Column(length = 25, nullable = false)
    private String actionAccount;
    @Column(length = 25, nullable = false)
    private String tranAction;
}