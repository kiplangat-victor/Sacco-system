package emt.sacco.middleware.Utils.NewLogs;//package emt.sacco.middleware.Utils.NewLogs;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import javax.transaction.Transactional;
//import java.time.LocalDateTime;
//
//@Service
//public class RequestLogService {
//
//    @Autowired
////    private RequestLogRepository requestLogRepository;
//
//    @Transactional
//    public void saveLog(String method, String uri, String queryString, String protocol, String remoteAddr, int remotePort, String userAgent) {
//        RequestLog log = new RequestLog();
//        log.setMethod(method);
//        log.setUri(uri);
//        log.setQueryString(queryString);
//        log.setProtocol(protocol);
//        log.setRemoteAddr(remoteAddr);
//        log.setRemotePort(remotePort);
//        log.setUserAgent(userAgent);
//        log.setTimestamp(LocalDateTime.now());
//
//        requestLogRepository.save(log);
//    }
//}
