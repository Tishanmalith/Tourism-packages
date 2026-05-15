package com.tourism.platform.security;

import com.tourism.platform.model.User;
import com.tourism.platform.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Injects the unread notification count into every request so the
 * customer nav badge can display the live count without scriptlets.
 */
@Component
public class NotificationBadgeInterceptor implements HandlerInterceptor {

    private final NotificationService notificationService;

    public NotificationBadgeInterceptor(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute(SessionKeys.CUSTOMER);
            if (user != null) {
                long unread = notificationService.countUnread(user.getId());
                request.setAttribute("_navUnread", unread);
            }
        }
        return true;
    }
}
