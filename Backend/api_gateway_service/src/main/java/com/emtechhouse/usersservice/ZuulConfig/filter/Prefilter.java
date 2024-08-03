package com.emtechhouse.usersservice.ZuulConfig.filter;

import com.emtechhouse.usersservice.utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.usersservice.utils.HttpInterceptor.UserDetailsRequestContext;
import com.emtechhouse.usersservice.utils.HttpInterceptor.UserRequestContext;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;


/**
 * @author - GreenLearner(https://www.youtube.com/channel/UCaH2MTg94hrJZTolW01a3ZA)
 */

@Configuration
public class Prefilter extends ZuulFilter {
    private static Logger log = LoggerFactory.getLogger(Prefilter.class);

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String  Authorization = request.getHeader("Authorization");
        String accessToken = Authorization.substring(7, Authorization.length());
        String userDetails = UserDetailsRequestContext.getcurrentUserDetails();
        ctx.addZuulRequestHeader("entityId", EntityRequestContext.getCurrentEntityId());
        ctx.addZuulRequestHeader("userName", UserRequestContext.getCurrentUser());
        ctx.addZuulRequestHeader("accessToken", accessToken);
        ctx.addZuulRequestHeader("userDetails", userDetails);
        log.info("Request by Username: " +UserRequestContext.getCurrentUser()+ " on Entity: " +EntityRequestContext.getCurrentEntityId());
        log.info("Request method = {}, url = {}", request.getMethod(), request.getRequestURL().toString());
        return null;
    }
}
