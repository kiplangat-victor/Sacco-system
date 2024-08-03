package com.emtechhouse.accounts.Responses;

import java.util.Date;

public interface BatchSalaryCheques {
    public String getTypeCode();
    public String getType();
    public String getStatus();
    public Double getAmount();
    public String getUniqueCode();
    public String getAccount();
    public Long getId();
    public Character getVerified_flag();
    public Character getVerified_flag_2();
    public Date getEntryTime();
}