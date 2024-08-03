package com.emtechhouse.System.LinkedOrganization.OrganizationCharges;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Organizationeventid {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String org_lnk_event_id;

}
