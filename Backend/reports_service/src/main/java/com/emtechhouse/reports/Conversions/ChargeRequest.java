

package com.emtechhouse.reports.Conversions;

import lombok.Data;

@Data
public class ChargeRequest {
    Integer pagesNumber;
    String description;
    String drAccount;
    String eventId;

    public ChargeRequest(int size, String statemnt_charges, String acid, String chargeEvent) {
        pagesNumber = size;
        description = statemnt_charges;
        drAccount = acid;
        eventId = chargeEvent;
    }
}



