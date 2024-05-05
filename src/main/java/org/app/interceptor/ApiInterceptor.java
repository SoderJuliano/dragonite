package org.app.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.app.utils.Commons;
import org.app.utils.LocalLog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;

import static org.app.utils.Commons.isTheSame;

public class ApiInterceptor implements HandlerInterceptor {

    @Value("${api.key}")
    private String apiKey;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String gotApiKey = request.getHeader("api-key");
        if (Commons.isEmpty(gotApiKey) && isTheSame(this.apiKey, gotApiKey)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            LocalLog.logErr("Invalid or not present api-key");
            return false;
        }
        return true;
    }
}
