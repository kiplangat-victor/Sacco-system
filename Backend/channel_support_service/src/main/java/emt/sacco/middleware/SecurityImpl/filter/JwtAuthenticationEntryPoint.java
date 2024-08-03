package emt.sacco.middleware.SecurityImpl.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import emt.sacco.middleware.SecurityImpl.Sec.HttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

import static emt.sacco.middleware.SecurityImpl.constants.SecurityConstants.FORBIDDEN_MESSAGE;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
@Component
public class JwtAuthenticationEntryPoint extends Http403ForbiddenEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)throws IOException {
        HttpResponse httpResponse=new HttpResponse(FORBIDDEN.value(),FORBIDDEN,FORBIDDEN.getReasonPhrase().toUpperCase(),FORBIDDEN_MESSAGE);

        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(FORBIDDEN.value());
        OutputStream outputStream=response.getOutputStream();
        ObjectMapper mapper=new ObjectMapper();
        mapper.writeValue(outputStream,httpResponse);
        outputStream.flush();


        //   logger.debug("Pre-authenticated entry point called. Rejecting access");
     //   response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
    }


}
