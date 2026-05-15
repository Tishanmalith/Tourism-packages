package com.tourism.platform.controller;

import com.tourism.platform.model.User;
import com.tourism.platform.security.SessionKeys;
import com.tourism.platform.service.NotificationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /** Customer notification inbox. */
    @GetMapping("/my")
    public String myNotifications(HttpSession session, Model model) {
        User user = (User) session.getAttribute(SessionKeys.CUSTOMER);
        if (user == null) {
            return "redirect:/users/login";
        }
        // Mark all as read when customer opens the inbox
        notificationService.markAllRead(user.getId());
        model.addAttribute("notifications", notificationService.findByUserId(user.getId()));
        model.addAttribute("user", user);
        return "notification/inbox";
    }

    /** Mark a single notification as read (called via form POST or link). */
    @PostMapping("/mark-read")
    public String markRead(@RequestParam Long id, HttpSession session) {
        User user = (User) session.getAttribute(SessionKeys.CUSTOMER);
        if (user == null) {
            return "redirect:/users/login";
        }
        notificationService.markRead(id);
        return "redirect:/notifications/my";
    }
}
