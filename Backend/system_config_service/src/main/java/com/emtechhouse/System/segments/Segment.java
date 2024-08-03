package com.emtechhouse.System.segments;

import com.emtechhouse.System.segments.SubSegments.SubSegment;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Segment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String entityId;
    @Column(length = 12, nullable = false)
    private String segmentCode;
    @Column(length = 100, nullable = false, columnDefinition = "TEXT")
    private String segmentName;
    @Column(length = 200, nullable = false, columnDefinition = "TEXT")
    private String segmentDescription;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "segment")
//    @JsonIgnore
    private List<SubSegment> subSegments;

    //*****************Operational Flag *********************
    @Column(length = 15)
    private String postedBy;
    private Character postedFlag;
    @Temporal(TemporalType.TIMESTAMP)
    private Date postedTime;
    @Column(length = 15)
    private String modifiedBy;
    private Character modifiedFlag = 'N';
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedTime;
    @Column(length = 15)
    private String verifiedBy;
    private Character verifiedFlag = 'N';
    @Temporal(TemporalType.TIMESTAMP)
    private Date verifiedTime;
    @Column(length = 15)
    private String deletedBy;
    private Character deletedFlag = 'N';
    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedTime;
}