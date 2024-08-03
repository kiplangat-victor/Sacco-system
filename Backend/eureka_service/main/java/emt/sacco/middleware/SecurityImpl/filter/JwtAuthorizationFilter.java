package emt.sacco.middleware.SecurityImpl.filter;

import emt.sacco.middleware.SecurityImpl.Sec.SwitchUsers;
import emt.sacco.middleware.SecurityImpl.SecImpl.JWTTokenProvider;
import emt.sacco.middleware.SecurityImpl.resource.UsersRepository;
import emt.sacco.middleware.Utils.Config.UserRequestContext;
import emt.sacco.middleware.Utils.CustomerInfo.UserDetailsRequestContext;
import emt.sacco.middleware.Utils.EntityRequestContext;
import emt.sacco.middleware.Utils.NewLogs.RequestLog;
import emt.sacco.middleware.Utils.NewLogs.RequestLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Slf4j
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String OPTIONS_HTTP_METHOD = "OPTIONS";

    private final Logger logger = LoggerFactory.getLogger(JwtAuthorizationFilter.class);

    @Value("${spring.application.logs.user}")
    private String userLogs;

    @Autowired
    private JWTTokenProvider jwtTokenProvider;
    @Autowired
    private Clientinformation clientinformation;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private RequestLogRepository requestLogRepository;

    @Autowired
    private UserDetailsService userDetailsService;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        String currentUserDetails = request.getHeader("userName");
//        UserDetailsRequestContext.setCurrentUserDetails(currentUserDetails);
//        UserRequestContext.setCurrentUser(request.getHeader("userName"));
//        log.info(UserRequestContext.getCurrentUser());
//
//        EntityRequestContext.setCurrentEntityId(request.getHeader("entityId"));
//        log.info(EntityRequestContext.getCurrentEntityId());
//
//        getLogs(request);
//
//        if (request.getMethod().equalsIgnoreCase(OPTIONS_HTTP_METHOD)) {
//            response.setStatus(HttpServletResponse.SC_OK);
//        } else {
//            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
//            if (authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_PREFIX)) {
//                filterChain.doFilter(request, response);
//                return;
//            }
//            String token = authorizationHeader.substring(TOKEN_PREFIX.length());
//            String username = jwtTokenProvider.getSubject(token);
//
//            UserDetailsRequestContext.setCurrentUserDetails(currentUserDetails);
//            UserRequestContext.setCurrentUser(request.getHeader("userName"));
//            EntityRequestContext.setCurrentEntityId(request.getHeader("entityId"));
//
//            if (request.getRequestURI().matches("/auth/signin") || request.getRequestURI().matches("/auth/signup") || request.getRequestURI().matches("/swagger-ui/")) {
//                UserRequestContext.setCurrentUser("Guest");
//                EntityRequestContext.setCurrentEntityId(request.getHeader("entityId"));
//            }
//            UserDetailsRequestContext.setCurrentUserDetails(currentUserDetails);
//
//            if (jwtTokenProvider.isTokenValid(username, token) && SecurityContextHolder.getContext().getAuthentication() == null) {
//                Optional<Users> user = usersRepository.findByUsername(username);
//                if (user.isPresent()) {
//                    UserRequestContext.setCurrentUser(username);
//                    EntityRequestContext.setCurrentEntityId(user.get().getEntityId());
//                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//                    List<GrantedAuthority> authorities = jwtTokenProvider.getAurhorities(token);
//                    Authentication authentication = jwtTokenProvider.getAuthentication(username, authorities, request);
//                    SecurityContextHolder.getContext().setAuthentication(authentication);
//                } else {
//                    logger.warn("User with username {} not found in the database", username);
//                }
//            } else {
//                SecurityContextHolder.clearContext();
//            }
//        }
//        processRequest(request, response, filterChain); // Process the request
//    }

        try {
            getLogs(request);
            log.info("-------------------------------Authentication Entry---------------------");
            log.info("Requested URI: " +request.getRequestURI());
            log.info(String.valueOf("Request URL:"+request.getRequestURL()));
            String accessToken = request.getHeader("accessToken");
            String currentUserDetails = request.getHeader("userName");
            UserRequestContext.setCurrentUser(request.getHeader("userName"));
            EntityRequestContext.setCurrentEntityId(request.getHeader("entityId"));
            if (request.getRequestURI().matches("/auth/signin") || request.getRequestURI().matches("/auth/signup") || request.getRequestURI().matches("/swagger-ui/") ){
                UserRequestContext.setCurrentUser("Guest");
                EntityRequestContext.setCurrentEntityId(request.getHeader("entityId"));
            }
            UserDetailsRequestContext.setCurrentUserDetails(currentUserDetails);
            String jwt = accessToken;
            clientinformation.getClientInformation(request);
            if (jwt != null && jwtTokenProvider.isTokenValid(jwt) ){
                String username = jwtTokenProvider.getSubject(jwt);
//				SET USER contact
                Optional<SwitchUsers> user = usersRepository.findByUsername(username);
                UserRequestContext.setCurrentUser(username);
                EntityRequestContext.setCurrentEntityId(user.get().getEntityId());
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            log.info("-------------------------------Authentication ends---------------------------");
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }

//        filterChain.doFilter(request, response);
        processRequest(request, response, filterChain);

    }
    public void processRequest(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            saveLog(request);
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            logger.error("Error processing request: " + ex.getMessage(), ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("An error occurred while processing the request");
        }
    }

    public void saveLog(HttpServletRequest request) {
        try {
            RequestLog log = createRequestLog(request);
            requestLogRepository.save(log);
            logger.info("Request log saved successfully.");
        } catch (Exception ex) {
            logger.error("Error saving request log: " + ex.getMessage(), ex);
        }
    }

    private RequestLog createRequestLog(HttpServletRequest request) {
        RequestLog log = new RequestLog();
        log.setMethod(request.getMethod());
        log.setUri(request.getRequestURI());
        log.setQueryString(request.getQueryString());
        log.setProtocol(request.getProtocol());
        log.setRemoteAddr(request.getRemoteAddr());
        log.setRemotePort(request.getRemotePort());
        log.setUserAgent(request.getHeader("User-Agent"));

        // Retrieve and set request body
        String requestBody = retrieveRequestBody(request);
        log.setRequestBody(requestBody);

        return log;
    }

    private String retrieveRequestBody(HttpServletRequest request) {
        try {
            ContentCachingRequestWrapper wrapper = new ContentCachingRequestWrapper(request);
            wrapper.getParameterMap(); // This reads the request body
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                return new String(buf, 0, buf.length, wrapper.getCharacterEncoding());
            }
        } catch (IOException ex) {
            logger.error("Error retrieving request body: " + ex.getMessage(), ex);
        }
        return null;
    }

    public void getLogs(HttpServletRequest request) throws IOException {
        String currentUserDetails = request.getHeader("userName");
        Path path = Paths.get(userLogs);
        System.out.println("-----------------------------generating logs------------------------------");
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String protocol = request.getProtocol();
        String remoteAddr = request.getRemoteAddr();
        int remotePort = request.getRemotePort();
        String userAgent = request.getHeader("User-Agent");
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String fileName = now.format(formatter) + currentUserDetails + "RequestLogs.log";

        RequestLog log = new RequestLog();
        log.setMethod(method);
        log.setUri(uri);
        log.setQueryString(queryString);
        log.setProtocol(protocol);
        log.setRemoteAddr(remoteAddr);
        log.setRemotePort(remotePort);
        log.setUserAgent(userAgent);

//        try {
//            if (Files.exists(path)) {
//                // if the file exists, append the data to the end of the file
//                FileWriter writer = new FileWriter(fileName);
//                writer.write("method: " + method + " uri: " + uri + " queryString: " + queryString + " protocol: " + protocol + " remoteAddr: " + remoteAddr + " " + " remotePort: " + remotePort + " userAgent: " + userAgent);
//                writer.close();
//            } else {
//                // if the file does not exist, create a new one and add the data
//                FileWriter writer = new FileWriter(fileName);
//                writer.write("method: " + method + " uri: " + uri + " queryString: " + queryString + " protocol: " + protocol + " remoteAddr: " + remoteAddr + " " + " remotePort: " + remotePort + " userAgent: " + userAgent);
//                writer.close();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

}
