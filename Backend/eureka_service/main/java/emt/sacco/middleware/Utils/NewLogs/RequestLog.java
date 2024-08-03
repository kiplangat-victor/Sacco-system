package emt.sacco.middleware.Utils.NewLogs;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity

@Table(name = "request_logs")
public class RequestLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "method")
    private String method;

    @Column(name = "uri")
    private String uri;

    @Column(name = "query_string")
    private String queryString;

    @Column(name = "protocol")
    private String protocol;

    @Column(name = "remote_addr")
    private String remoteAddr;

    @Column(name = "remote_port")
    private int remotePort;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Column(name="requestBody")
    private String requestBody;

    // Constructors, getters, and setters

    public RequestLog() {}

    public RequestLog(String method, String uri, String queryString, String protocol, String remoteAddr, int remotePort, String userAgent, LocalDateTime timestamp) {
        this.method = method;
        this.uri = uri;
        //this.queryString = queryString;
        this.protocol = protocol;
        this.remoteAddr = remoteAddr;
        this.remotePort = remotePort;
        this.userAgent = userAgent;
        this.timestamp = timestamp;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public RequestLog(String queryString) {
        this.queryString = queryString;
    }
}
