package emt.sacco.middleware.ATM.Reports;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
@Component
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AtmErrorLogs {
    @Id
 private Long atmId;
 private String url;
 private String httpRequests;
 private String responses;
 private String requests;
 private String timestamp;
 private String severity;
 private String errorCode;
 private  String resolutionStatus;
}
