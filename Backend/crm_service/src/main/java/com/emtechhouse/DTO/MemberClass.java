package com.emtechhouse.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MemberClass {
    Long Id;
    String BranchCode;
    String CustomerCode;
    String CustomerType;
    String CustomerName;
    String CustomerUniqueId;
    String PostedOn;
    String VerifiedFlag;
    String Identity;
}
