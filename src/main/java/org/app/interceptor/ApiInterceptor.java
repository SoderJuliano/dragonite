package org.app.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.app.Exceptions.UnauthorizedException;
import org.app.utils.Commons;
import org.app.utils.LocalLog;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static org.app.utils.Commons.isNotTheSame;
import static org.app.utils.Commons.split;

@Component
public class ApiInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        if (request.getRequestURL().toString().contains("localhost")) {
            return true;
        }

        String gotApiKey = request.getHeader("Authorization");

        if (!Commons.notEmpty(gotApiKey) && isNotTheSame("Y3VzdG9tY3ZvbmxpbmU=", split(gotApiKey, "Bearer ", 1))) {
            LocalLog.logErr("Invalid or not present api-key");
            throw new UnauthorizedException("Invalid or missing API key");
        }
        return true;
    }
}
