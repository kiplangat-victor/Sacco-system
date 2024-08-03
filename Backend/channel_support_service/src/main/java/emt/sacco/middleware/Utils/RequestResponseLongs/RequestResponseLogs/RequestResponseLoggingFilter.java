package emt.sacco.middleware.Utils.RequestResponseLongs.RequestResponseLogs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestResponseLoggingFilter implements Filter {

    @Autowired
    private RequestResponseLogRepository logRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(httpRequest);
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(httpResponse);

        // Proceed with the request
        chain.doFilter(requestWrapper, responseWrapper);

        // Create log entity
        RequestResponseLog log = new RequestResponseLog();
        log.setRequestUrl(requestWrapper.getRequestURI());
        log.setRequestMethod(requestWrapper.getMethod());
        byte[] requestBodyBytes = requestWrapper.getContentAsByteArray();
        if (requestBodyBytes != null && requestBodyBytes.length > 0) {
            // Convert the request body bytes to a string (assuming UTF-8 encoding)
            String requestBody = new String(requestBodyBytes, StandardCharsets.UTF_8);
            log.setRequestBody(requestBody.getBytes());
        }
        // log.setRequestBody(requestWrapper.getContentAsByteArray());
        log.setResponseStatus(responseWrapper.getStatus());
        log.setResponseBodyTimestamp(LocalDateTime.now());
        log.setResponseBody(responseWrapper.getContentAsByteArray());
        log.setTimestamp(LocalDateTime.now());
        log.setRequestIp(httpRequest.getRemoteAddr());


        // Save the log to the database
        logRepository.save(log);

        // Copy response body to the actual response
        responseWrapper.copyBodyToResponse();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code if needed
    }

    @Override
    public void destroy() {
        // Cleanup code if needed
    }
}
