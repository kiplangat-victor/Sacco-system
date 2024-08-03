package com.emtechhouse.System.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class EntityManagement {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 6, nullable = false, unique = true)
    private String entityId;
    @Column(length = 200, nullable = false)
    private String entityName;
    private String entityStatus;
    private String entityDescription;
    //*****************Operational Audit *********************
    @Column(length = 30, nullable = false)
    private String postedBy;
    @Column(nullable = false)
    private Character postedFlag = 'Y';
    @Column(nullable = false)
    private LocalDate postedOn;
    private String modifiedBy;
    private Character modifiedFlag = 'N';
    private LocalDate modifiedOn;
    private String verifiedBy;
    private Character verifiedFlag = 'N';
    private LocalDate verifiedOn;
    private String deletedBy;
    private Character deletedFlag = 'N';
    private LocalDate deletedOn;
}
