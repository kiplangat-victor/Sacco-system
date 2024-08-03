package emt.sacco.middleware.Utils.RequestResponseLongs.RequestResponseLogs;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
public class RequestResponseLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2000)
    private String requestUrl;

    private String requestMethod;

    @Lob
    private byte[] requestBody;

    private LocalDateTime responseBodyTimestamp;

    private String requestIp;

    private LocalDateTime timestamp;

    private int responseStatus;

    @Lob
    private byte[] responseBody;

    // Additional fields for error logging
    @Column(length = 5000)
    private String errorMessage;

    @Lob
    private String stackTrace;

    // Getters and setters
}
