package com.emtechhouse.accounts.Utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Lob;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class Customerimages {
    @Lob
    private String image;
    @Lob
    private String image_name;
}
