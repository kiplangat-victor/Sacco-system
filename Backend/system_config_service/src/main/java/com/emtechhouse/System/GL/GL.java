package com.emtechhouse.System.GL;

import com.emtechhouse.System.GLSubhead.GLSubhead;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class GL {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 6, nullable = false)
    private String entityId;
    @Column(length = 12, nullable = false, updatable = false)
    @JsonProperty(required = true)
    private String glCode;
    @Column(length = 200, nullable = false)
    private String classification;
    @Column(length = 200, nullable = false)
    private String glDescription;
    @Column(length = 6 , columnDefinition = "DOUBLE", nullable = false)
    private  Long fromRange;
    @Column(length = 6 , columnDefinition = "DOUBLE", nullable = false)
    private Long toRange;

    @OneToMany(mappedBy = "gl", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<GLSubhead> glSubheads = new ArrayList<>();

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
