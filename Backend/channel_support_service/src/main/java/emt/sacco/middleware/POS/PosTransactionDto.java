package emt.sacco.middleware.POS;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class PosTransactionDto {
    private String merchantName;
    private String merchantAcid;
    private String merchantLocation;
    private String transactionCode;
    private Double totalAmount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date postedTime;
}