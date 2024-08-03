package com.emtechhouse.System.ExceptionCode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Exceptioncode {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 6, nullable = false)
    private String entityId;

    @Column(nullable = false, length = 12, unique = true, updatable = false, columnDefinition = "TEXT")
    private String exceptionCode;
    @Column(nullable = false, length = 200, columnDefinition = "TEXT")

    private String exce_description;
    @Column(nullable = false, length = 20, columnDefinition = "TEXT")

    private String exce_code_type;
    @Column(nullable = false, length = 40, columnDefinition = "TEXT")

    private String exce_work_class_role;
    @Column(nullable = false, length = 12, columnDefinition = "TEXT")

    private String exce_ignore_exce_overriding_events;

    //*****************Operational Audit *********************
    @Column(length = 30, nullable = false)
    private String postedBy;
    @Column(nullable = false)
    private Character postedFlag = 'Y';
    @Column(nullable = false)
    private Date postedTime;
    private String modifiedBy;
    private Character modifiedFlag = 'N';
    private Date modifiedTime;
    private String verifiedBy;
    private Character verifiedFlag = 'N';
    private Date verifiedTime;
    private String deletedBy;
    private Character deletedFlag = 'N';
    private Date deletedTime;
}