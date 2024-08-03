package com.emtechhouse.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public interface Member {
    Long getId();
    String getBranchCode();
    String getCustomerCode();
    String getCustomerType();
    String getCustomerName();
    String getCustomerUniqueId();
    String getPostedOn();
    String getVerifiedFlag();
    String getIdentity();
}

