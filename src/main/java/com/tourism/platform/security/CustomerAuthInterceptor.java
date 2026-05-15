package com.tourism.platform.security;

import com.tourism.platform.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class CustomerAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute(SessionKeys.CUSTOMER);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/?loginRequired=true");
            return false;
        }
        return true;
    }
}
