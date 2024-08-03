package com.emtechhouse.accounts.Models.Accounts.AccountDtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountImageDto {
    private String image;
    private String image_name;
}
