package com.emtechhouse.accounts.Utils;

import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class AuditDetails {
    public EntityResponse checkUserAndEntity(){
        EntityResponse response = new EntityResponse();
        String user = UserRequestContext.getCurrentUser();
        String entityId = EntityRequestContext.getCurrentEntityId();
        if (user.isEmpty()) {
            response.setMessage("User Name not present in the Request Header");
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            response.setEntity("");
            return response;
        } else if (entityId.isEmpty()) {
            response.setMessage("Entity not present in the Request Header");
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            return response;
        } else {
            response.setMessage(HttpStatus.OK.getReasonPhrase());
            response.setStatusCode(HttpStatus.OK.value());
        }
        return response;
    }


}
