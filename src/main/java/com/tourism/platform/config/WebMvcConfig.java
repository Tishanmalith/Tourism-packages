package com.tourism.platform.config;

import com.tourism.platform.security.AdminAuthInterceptor;
import com.tourism.platform.security.BookingManagementInterceptor;
import com.tourism.platform.security.CustomerAuthInterceptor;
import com.tourism.platform.security.NotificationBadgeInterceptor;
import com.tourism.platform.security.SharedAuthInterceptor;
import com.tourism.platform.security.StaffAuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AdminAuthInterceptor adminAuthInterceptor;
    private final StaffAuthInterceptor staffAuthInterceptor;
    private final CustomerAuthInterceptor customerAuthInterceptor;
    private final BookingManagementInterceptor bookingManagementInterceptor;
    private final NotificationBadgeInterceptor notificationBadgeInterceptor;
    private final SharedAuthInterceptor sharedAuthInterceptor;

    public WebMvcConfig(AdminAuthInterceptor adminAuthInterceptor,
                        StaffAuthInterceptor staffAuthInterceptor,
                        CustomerAuthInterceptor customerAuthInterceptor,
                        BookingManagementInterceptor bookingManagementInterceptor,
                        NotificationBadgeInterceptor notificationBadgeInterceptor,
                        SharedAuthInterceptor sharedAuthInterceptor) {
        this.adminAuthInterceptor = adminAuthInterceptor;
        this.staffAuthInterceptor = staffAuthInterceptor;
        this.customerAuthInterceptor = customerAuthInterceptor;
        this.bookingManagementInterceptor = bookingManagementInterceptor;
        this.notificationBadgeInterceptor = notificationBadgeInterceptor;
        this.sharedAuthInterceptor = sharedAuthInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminAuthInterceptor)
                .addPathPatterns("/users/**")
                .excludePathPatterns("/users/register", "/users/login");

        registry.addInterceptor(adminAuthInterceptor)
                .addPathPatterns("/packages/**")
                .excludePathPatterns("/packages/detail", "/packages/list", "/packages/");

        registry.addInterceptor(adminAuthInterceptor)
                .addPathPatterns("/destinations/**");

        registry.addInterceptor(adminAuthInterceptor)
                .addPathPatterns("/feedback/**")
                .excludePathPatterns("/feedback/customer/**");

        registry.addInterceptor(adminAuthInterceptor)
                .addPathPatterns("/admin/**");

        registry.addInterceptor(adminAuthInterceptor)
                .addPathPatterns("/bookings/delete", "/bookings/delete/**");

        registry.addInterceptor(adminAuthInterceptor)
                .addPathPatterns("/bookings/mark-completed");

        registry.addInterceptor(staffAuthInterceptor)
                .addPathPatterns("/staff/**");

        registry.addInterceptor(customerAuthInterceptor)
                .addPathPatterns("/bookings/new", "/bookings/create", "/bookings/my",
                        "/bookings/customer/edit", "/bookings/customer/update", "/bookings/customer/cancel",
                        "/feedback/customer/**",
                        "/notifications/my", "/notifications/mark-read");

        registry.addInterceptor(sharedAuthInterceptor)
                .addPathPatterns("/packages/detail", "/packages/list", "/packages/");

        registry.addInterceptor(bookingManagementInterceptor)
                .addPathPatterns("/bookings/**")
                .excludePathPatterns(
                        "/bookings/new",
                        "/bookings/create",
                        "/bookings/my",
                        "/bookings/customer/edit",
                        "/bookings/customer/update",
                        "/bookings/customer/cancel",
                        "/bookings/delete",
                        "/bookings/delete/**");

        // Inject unread notification count for all customer pages
        registry.addInterceptor(notificationBadgeInterceptor)
                .addPathPatterns("/bookings/my", "/bookings/new", "/bookings/create",
                        "/bookings/customer/edit", "/bookings/customer/update",
                        "/feedback/customer/**", "/notifications/**", "/packages/detail");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}