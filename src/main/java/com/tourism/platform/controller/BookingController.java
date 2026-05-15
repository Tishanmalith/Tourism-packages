package com.tourism.platform.controller;

import com.tourism.platform.model.Admin;
import com.tourism.platform.model.Booking;
import com.tourism.platform.model.TourPackage;
import com.tourism.platform.model.User;
import com.tourism.platform.security.SessionKeys;
import com.tourism.platform.service.BookingService;
import com.tourism.platform.service.NotificationService;
import com.tourism.platform.service.PackageService;
import com.tourism.platform.service.UserService;
import com.tourism.platform.util.TripFeedbackRules;
import com.tourism.platform.util.ValidationSupport;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final PackageService packageService;
    private final UserService userService;
    private final NotificationService notificationService;

    public BookingController(BookingService bookingService,
                             PackageService packageService,
                             UserService userService,
                             NotificationService notificationService) {
        this.bookingService = bookingService;
        this.packageService = packageService;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @GetMapping({"", "/"})
    public String listAll(Model model) {
        model.addAttribute("bookings", bookingService.findAll());
        model.addAttribute("users", userService.findAll());
        model.addAttribute("packages", packageService.findAll());
        return "booking/list";
    }

    @GetMapping("/my")
    public String myBookings(HttpSession session, Model model) {
        User user = (User) session.getAttribute(SessionKeys.CUSTOMER);
        List<Booking> mine = bookingService.findByUserId(user.getId());
        model.addAttribute("bookings", mine);
        model.addAttribute("packages", packageService.findAll());
        model.addAttribute("user", user);
        return "booking/my";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("booking", new Booking());
        model.addAttribute("packages", packageService.findAll());
        model.addAttribute("minTripDate", TripFeedbackRules.today().toString());
        model.addAttribute("maxTripDate", TripFeedbackRules.today().plusYears(2).toString());
        return "booking/customer-form";
    }

    @PostMapping("/create")
    public String create(@RequestParam(required = false) Long packageId,
                         @RequestParam(required = false) String tripDate,
                         @RequestParam(required = false, defaultValue = "") String notes,
                         HttpSession session,
                         Model model) {
        User user = (User) session.getAttribute(SessionKeys.CUSTOMER);
        if (user == null) {
            return "redirect:/users/login";
        }
        model.addAttribute("packages", packageService.findAll());
        model.addAttribute("minTripDate", TripFeedbackRules.today().toString());
        model.addAttribute("maxTripDate", TripFeedbackRules.today().plusYears(2).toString());

        var tripStart = TripFeedbackRules.parseBookingStart(tripDate == null ? "" : tripDate.trim());
        var today = TripFeedbackRules.today();
        Booking republish = stubBookingForForm(user.getId(), packageId, tripDate, notes);

        if (tripStart == null) {
            model.addAttribute("message", "Pick a valid trip start date (yyyy-mm-dd).");
            model.addAttribute("booking", republish);
            return "booking/customer-form";
        }
        if (tripStart.isBefore(today)) {
            model.addAttribute("message", "Trip start cannot be before today.");
            model.addAttribute("booking", republish);
            return "booking/customer-form";
        }
        if (tripStart.isAfter(today.plusYears(2))) {
            model.addAttribute("message", "Trip start cannot be more than two years ahead.");
            model.addAttribute("booking", republish);
            return "booking/customer-form";
        }

        if (packageId == null || packageId < 1 || packageService.findById(packageId).isEmpty()) {
            model.addAttribute("message", "Please choose a valid package.");
            model.addAttribute("booking", republish);
            return "booking/customer-form";
        }

        String noteText = ValidationSupport.trimLen(notes == null ? "" : notes, 500);
        Booking b = new Booking();
        b.setUserId(user.getId());
        b.setPackageId(packageId);
        b.setStatus("PENDING");
        b.setBookingDate(tripStart.toString());
        b.setNotes(noteText);
        bookingService.save(b);
        return "redirect:/bookings/my";
    }

    private Booking stubBookingForForm(String userId, Long packageId, String tripDateRaw, String notes) {
        Booking b = new Booking();
        b.setUserId(userId);
        if (packageId != null && packageId > 0) {
            b.setPackageId(packageId);
        }
        if (tripDateRaw != null && !tripDateRaw.isBlank()) {
            var parsed = TripFeedbackRules.parseBookingStart(tripDateRaw.trim());
            b.setBookingDate(parsed != null ? parsed.toString() : tripDateRaw.trim());
        }
        b.setNotes(ValidationSupport.trimLen(notes == null ? "" : notes, 500));
        return b;
    }

    // -----------------------------------------------------------------------
    // Customer self-service: edit & cancel
    // -----------------------------------------------------------------------

    /**
     * Customer edit form — only allowed while booking is PENDING.
     */
    @GetMapping("/customer/edit")
    public String customerEditForm(@RequestParam Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute(SessionKeys.CUSTOMER);
        if (user == null) return "redirect:/users/login";

        Optional<Booking> opt = bookingService.findById(id);
        if (opt.isEmpty() || !opt.get().getUserId().equals(user.getId())) {
            return "redirect:/bookings/my";
        }
        Booking b = opt.get();
        if (!"PENDING".equalsIgnoreCase(nullToBlank(b.getStatus()))) {
            // Only PENDING bookings can be edited by the customer
            return "redirect:/bookings/my?error=cannotEdit";
        }
        model.addAttribute("booking", b);
        model.addAttribute("packages", packageService.findAll());
        model.addAttribute("minTripDate", TripFeedbackRules.today().toString());
        model.addAttribute("maxTripDate", TripFeedbackRules.today().plusYears(2).toString());
        return "booking/customer-edit";
    }

    /**
     * Customer saves the edited booking — ownership + PENDING guard.
     */
    @PostMapping("/customer/update")
    public String customerUpdate(@RequestParam Long id,
                                 @RequestParam(required = false) Long packageId,
                                 @RequestParam(required = false) String tripDate,
                                 @RequestParam(required = false, defaultValue = "") String notes,
                                 HttpSession session,
                                 Model model) {
        User user = (User) session.getAttribute(SessionKeys.CUSTOMER);
        if (user == null) return "redirect:/users/login";

        Optional<Booking> opt = bookingService.findById(id);
        if (opt.isEmpty() || !opt.get().getUserId().equals(user.getId())) {
            return "redirect:/bookings/my";
        }
        Booking existing = opt.get();
        if (!"PENDING".equalsIgnoreCase(nullToBlank(existing.getStatus()))) {
            return "redirect:/bookings/my?error=cannotEdit";
        }

        // Shared validation helpers
        model.addAttribute("packages", packageService.findAll());
        model.addAttribute("minTripDate", TripFeedbackRules.today().toString());
        model.addAttribute("maxTripDate", TripFeedbackRules.today().plusYears(2).toString());
        model.addAttribute("booking", existing);

        var tripStart = TripFeedbackRules.parseBookingStart(tripDate == null ? "" : tripDate.trim());
        var today     = TripFeedbackRules.today();

        if (tripStart == null) {
            model.addAttribute("message", "Pick a valid trip start date (yyyy-mm-dd).");
            return "booking/customer-edit";
        }
        if (tripStart.isBefore(today)) {
            model.addAttribute("message", "Trip start cannot be before today.");
            return "booking/customer-edit";
        }
        if (tripStart.isAfter(today.plusYears(2))) {
            model.addAttribute("message", "Trip start cannot be more than two years ahead.");
            return "booking/customer-edit";
        }
        if (packageId == null || packageId < 1 || packageService.findById(packageId).isEmpty()) {
            model.addAttribute("message", "Please choose a valid package.");
            return "booking/customer-edit";
        }

        existing.setPackageId(packageId);
        existing.setBookingDate(tripStart.toString());
        existing.setNotes(ValidationSupport.trimLen(notes, 500));
        bookingService.save(existing);
        return "redirect:/bookings/my?success=updated";
    }

    /**
     * Customer cancels their own booking — allowed while PENDING or CONFIRMED.
     * The booking is permanently deleted so it no longer appears in the customer's list.
     */
    @PostMapping("/customer/cancel")
    public String customerCancel(@RequestParam Long id, HttpSession session) {
        User user = (User) session.getAttribute(SessionKeys.CUSTOMER);
        if (user == null) return "redirect:/users/login";

        Optional<Booking> opt = bookingService.findById(id);
        if (opt.isEmpty() || !opt.get().getUserId().equals(user.getId())) {
            return "redirect:/bookings/my";
        }
        Booking b = opt.get();
        String st = nullToBlank(b.getStatus()).toUpperCase();
        if (st.equals("COMPLETED")) {
            // Completed trips cannot be cancelled/deleted
            return "redirect:/bookings/my?error=cannotCancel";
        }
        // Delete the booking entirely so it is removed from the list
        bookingService.deleteById(b.getId());
        return "redirect:/bookings/my?success=cancelled";
    }

    @GetMapping("/edit")
    public String editForm(@RequestParam Long id, Model model, HttpSession session) {
        return bookingService.findById(id)
                .map(b -> {
                    model.addAttribute("booking", b);
                    model.addAttribute("users", userService.findAll());
                    model.addAttribute("packages", packageService.findAll());
                    boolean adminPresent = session.getAttribute(SessionKeys.ADMIN) instanceof Admin;
                    model.addAttribute("adminPresent", adminPresent);
                    return "booking/status-form";
                })
                .orElse("redirect:/bookings");
    }

    @PostMapping("/update-status")
    public String updateStatus(@RequestParam(required = false) Long id,
                               @RequestParam(required = false) String status) {
        if (id == null || id < 1) {
            return "redirect:/bookings";
        }
        Optional<Booking> existing = bookingService.findById(id);
        if (existing.isEmpty()) {
            return "redirect:/bookings";
        }
        Booking b = existing.get();
        if ("COMPLETED".equalsIgnoreCase(nullToBlank(b.getStatus()))) {
            return "redirect:/bookings/edit?id=" + id;
        }
        b.setStatus(normalizeEditableStatus(status));
        bookingService.save(b);
        return "redirect:/bookings/edit?id=" + id;
    }

    /**
     * Admin-only: CONFIRMED → COMPLETED after the trip ran (closes loop for traveler feedback).
     */
    @PostMapping("/mark-completed")
    public String markTripCompleted(@RequestParam(required = false) Long id, HttpSession session) {
        if (!(session.getAttribute(SessionKeys.ADMIN) instanceof Admin)) {
            return "redirect:/login?error=admin";
        }
        if (id == null || id < 1) {
            return "redirect:/bookings";
        }
        Optional<Booking> existing = bookingService.findById(id);
        if (existing.isEmpty()) {
            return "redirect:/bookings";
        }
        Booking b = existing.get();
        if (!"CONFIRMED".equalsIgnoreCase(nullToBlank(b.getStatus()))) {
            return "redirect:/bookings/edit?id=" + id;
        }
        b.setStatus("COMPLETED");
        bookingService.save(b);

        // Notify the customer that their trip has been marked as completed
        String packageName = packageService.findById(b.getPackageId())
                .map(TourPackage::getName)
                .orElse("your package");
        String message = "🎉 Your trip '" + packageName + "' (Booking #" + b.getId()
                + ") has been completed! You can now leave feedback."
                + " Trip date: " + b.getBookingDate() + ".";
        notificationService.create(b.getUserId(), b.getId(), message);

        return "redirect:/bookings/edit?id=" + id;
    }

    private static String nullToBlank(String s) {
        return s == null ? "" : s.trim();
    }

    @GetMapping("/delete")
    public String delete(@RequestParam Long id) {
        bookingService.deleteById(id);
        return "redirect:/bookings";
    }

    /** Status values staff/admin may set from the status form (not COMPLETED — use mark-completed). */
    private static String normalizeEditableStatus(String status) {
        if (status == null || status.isBlank()) {
            return "PENDING";
        }
        String s = status.trim().toUpperCase();
        List<String> allowed = List.of("PENDING", "CONFIRMED", "CANCELLED");
        return allowed.stream().anyMatch(a -> a.equals(s)) ? s : "PENDING";
    }
}
