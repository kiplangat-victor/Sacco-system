package com.emtechhouse.usersservice.utils;

import lombok.Data;

import java.util.Date;

@Data
public class Transactions {
    public String product_code;
    public String acid;
    public String sn;
    public String account_name;
    public String product_code_desc;
    public String tran_particulars;
    public String transaction_code;
    public String tran_amount;
    public String relative_amount;
    public String c_amount;
    public String cls;
    public String d_amount;
    public Date tran_date;
    public String dtd_sn;
    public String rel_amount;
}
