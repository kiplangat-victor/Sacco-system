package com.emtechhouse.System.Calendar;

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
public class Calendar {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 6, nullable = false)
    private Long entityId;

    @Column(length = 4, nullable = false)
    private String year;
    @Column(length = 2, nullable = false)
    private String month;
    @Column(nullable = false, length = 31)
    private String holidayString;

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
