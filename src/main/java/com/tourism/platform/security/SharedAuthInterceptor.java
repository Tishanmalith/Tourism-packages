package com.tourism.platform.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SharedAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/?loginRequired=true");
            return false;
        }

        boolean isAdmin = session.getAttribute(SessionKeys.ADMIN) != null;
        boolean isCustomer = session.getAttribute(SessionKeys.CUSTOMER) != null;
        boolean isStaff = session.getAttribute(SessionKeys.STAFF) != null;

        if (!isAdmin && !isCustomer && !isStaff) {
            response.sendRedirect(request.getContextPath() + "/?loginRequired=true");
            return false;
        }
        return true;
    }
}