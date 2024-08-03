package com.emtechhouse.accounts.TransactionService.Requests;

import java.util.Date;

public interface MinistatementInterface {
//    pt.acid,pt.tran_amount,pt.tran_date,dtd.transaction_code,dtd.transaction_type,pt.part_tran_type from dtd join part_tran pt on pt.transaction_header_id=dtd.sn  where pt.acid =:acid

    String getAcid();
    String getTransaction_type();

    Date getPosted_time();
    String getAccount_name();
    Double getTran_amount();
    Date getTran_date();
    String getTransaction_code();
    String getPart_tran_type();
    String getTran_particulars();
    Double getAccount_balance();


}

