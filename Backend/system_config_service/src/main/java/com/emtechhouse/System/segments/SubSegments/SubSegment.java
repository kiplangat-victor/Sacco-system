package com.emtechhouse.System.segments.SubSegments;

import com.emtechhouse.System.segments.Segment;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SubSegment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 6, nullable = false)
    private String entityId;
    @Column(nullable = false, length = 12)
    private String subSegmentCode;
    @Column(nullable = false, length = 100, columnDefinition = "TEXT")
    private String subSegmentName;
    @Column(nullable = false, length = 200, columnDefinition = "TEXT")
    private String subSegmentDescription;
    @Column(nullable = false)
    private String segmentCode;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "segment_id")
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Segment segment;

    //*****************Operational Flag *********************
    @Column(length = 15)
    private String postedBy;
    private Character postedFlag = 'N';
    @Temporal(TemporalType.TIMESTAMP)
    private Date postedTime;
    @Column(length = 15)
    private String modifiedBy;
    private Character modifiedFlag;
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