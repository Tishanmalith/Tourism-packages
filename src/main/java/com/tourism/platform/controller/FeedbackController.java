package com.tourism.platform.controller;

import com.tourism.platform.model.Booking;
import com.tourism.platform.model.Feedback;
import com.tourism.platform.model.TourPackage;
import com.tourism.platform.model.User;
import com.tourism.platform.security.SessionKeys;
import com.tourism.platform.service.BookingService;
import com.tourism.platform.service.FeedbackService;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final PackageService packageService;
    private final UserService userService;
    private final BookingService bookingService;

    public FeedbackController(FeedbackService feedbackService,
                              PackageService packageService,
                              UserService userService,
                              BookingService bookingService) {
        this.feedbackService = feedbackService;
        this.packageService = packageService;
        this.userService = userService;
        this.bookingService = bookingService;
    }

    @GetMapping({"", "/", "/list"})
    public String list(Model model) {
        model.addAttribute("feedbackList", feedbackService.findAll());
        return "feedback/list";
    }

    @GetMapping("/detail")
    public String detail(@RequestParam Long id, Model model) {
        return feedbackService.findById(id)
                .map(f -> {
                    model.addAttribute("feedback", f);
                    packageService.findById(f.getPackageId()).ifPresent(p -> model.addAttribute("tourPackage", p));
                    return "feedback/detail";
                })
                .orElse("redirect:/feedback/list");
    }

    @GetMapping("/new")
    public String adminNewForm(Model model) {
        model.addAttribute("feedback", new Feedback());
        model.addAttribute("packages", packageService.findAll());
        model.addAttribute("users", userService.findAll());
        model.addAttribute("editMode", false);
        return "feedback/form";
    }

    @PostMapping("/create")
    public String adminCreate(@RequestParam(required = false) String userId,
                              @RequestParam(required = false) Long packageId,
                              @RequestParam(required = false) Integer rating,
                              @RequestParam(required = false) String comment,
                              Model model) {
        String err = adminFeedbackErrors(userId, packageId, rating, comment);
        if (err != null) {
            int r = rating == null ? 0 : clampRating(rating);
            Feedback f = buildFeedback(null, userId, packageId, r, comment,
                    TripFeedbackRules.today().toString(), 1000);
            model.addAttribute("message", err);
            model.addAttribute("feedback", f);
            model.addAttribute("packages", packageService.findAll());
            model.addAttribute("users", userService.findAll());
            model.addAttribute("editMode", false);
            return "feedback/form";
        }
        feedbackService.save(buildFeedback(null, userId.trim(), packageId, clampRating(rating), comment,
                TripFeedbackRules.today().toString(), 1000));
        return "redirect:/feedback/list";
    }

    @GetMapping("/edit")
    public String adminEditForm(@RequestParam Long id, Model model) {
        return feedbackService.findById(id)
                .map(f -> {
                    model.addAttribute("feedback", f);
                    model.addAttribute("packages", packageService.findAll());
                    model.addAttribute("users", userService.findAll());
                    model.addAttribute("editMode", true);
                    return "feedback/form";
                })
                .orElse("redirect:/feedback/list");
    }

    @PostMapping("/update")
    public String adminUpdate(@RequestParam Long id,
                              @RequestParam(required = false) String userId,
                              @RequestParam(required = false) Long packageId,
                              @RequestParam(required = false) Integer rating,
                              @RequestParam(required = false) String comment,
                              Model model) {
        Optional<Feedback> existing = feedbackService.findById(id);
        if (existing.isEmpty()) {
            return "redirect:/feedback/list";
        }
        String err = adminFeedbackErrors(userId, packageId, rating, comment);
        if (err != null) {
            int r = rating == null ? 0 : clampRating(rating);
            Feedback f = buildFeedback(id, userId, packageId, r, comment,
                    existing.get().getCreatedAt(), 1000);
            model.addAttribute("message", err);
            model.addAttribute("feedback", f);
            model.addAttribute("packages", packageService.findAll());
            model.addAttribute("users", userService.findAll());
            model.addAttribute("editMode", true);
            return "feedback/form";
        }
        Feedback updated = buildFeedback(id, userId.trim(), packageId, clampRating(rating), comment,
                existing.get().getCreatedAt(), 1000);
        feedbackService.save(updated);
        return "redirect:/feedback/list";
    }

    @GetMapping("/delete")
    public String adminDelete(@RequestParam Long id) {
        feedbackService.deleteById(id);
        return "redirect:/feedback/list";
    }

    @GetMapping("/customer/add")
    public String customerForm(HttpSession session, Model model) {
        User user = (User) session.getAttribute(SessionKeys.CUSTOMER);
        if (user == null) {
            return "redirect:/users/login";
        }
        List<TourPackage> eligible = eligiblePackagesForReviewer(user.getId());
        model.addAttribute("packages", eligible);
        model.addAttribute("noEligibleTrips", eligible.isEmpty());
        return "feedback/customer-add";
    }

    @PostMapping("/customer/add")
    public String customerSubmit(@RequestParam(required = false) Long packageId,
                                 @RequestParam(required = false) Integer rating,
                                 @RequestParam(required = false, defaultValue = "") String comment,
                                 HttpSession session,
                                 Model model) {
        User user = (User) session.getAttribute(SessionKeys.CUSTOMER);
        if (user == null) {
            return "redirect:/users/login";
        }
        List<TourPackage> eligible = eligiblePackagesForReviewer(user.getId());
        if (packageId == null || packageId < 1
                || rating == null || !ValidationSupport.inRange(rating, 1, 5)) {
            model.addAttribute("message", "Select a package and a star rating.");
            model.addAttribute("packages", eligible);
            model.addAttribute("noEligibleTrips", eligible.isEmpty());
            return "feedback/customer-add";
        }
        if (!isEligibleFeedbackPackage(user.getId(), packageId)) {
            model.addAttribute("message",
                    "You can rate trips only after they’re finished—admin-marked COMPLETED or confirmed trips past your package’s last day.");
            model.addAttribute("packages", eligible);
            model.addAttribute("noEligibleTrips", eligible.isEmpty());
            return "feedback/customer-add";
        }
        Feedback f = buildFeedback(null, user.getId(), packageId, clampRating(rating), comment,
                TripFeedbackRules.today().toString(), 900);
        feedbackService.save(f);
        return "redirect:/feedback/customer/my";
    }

    /** Customer: list their own feedback. */
    @GetMapping("/customer/my")
    public String customerMyFeedback(HttpSession session, Model model) {
        User user = (User) session.getAttribute(SessionKeys.CUSTOMER);
        if (user == null) return "redirect:/users/login";
        List<Feedback> myFeedback = feedbackService.findByUserId(user.getId());
        model.addAttribute("feedbackList", myFeedback);
        model.addAttribute("packages", packageService.findAll());
        model.addAttribute("user", user);
        return "feedback/customer-my";
    }

    /** Customer: show edit form for their own feedback. */
    @GetMapping("/customer/edit")
    public String customerEditForm(@RequestParam Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute(SessionKeys.CUSTOMER);
        if (user == null) return "redirect:/users/login";
        Optional<Feedback> opt = feedbackService.findById(id);
        if (opt.isEmpty() || !opt.get().getUserId().equals(user.getId())) {
            return "redirect:/feedback/customer/my";
        }
        model.addAttribute("feedback", opt.get());
        model.addAttribute("editMode", true);
        return "feedback/customer-edit";
    }

    /** Customer: save edited feedback (only their own). */
    @PostMapping("/customer/edit")
    public String customerUpdate(@RequestParam Long id,
                                 @RequestParam(required = false) Integer rating,
                                 @RequestParam(required = false, defaultValue = "") String comment,
                                 HttpSession session,
                                 Model model) {
        User user = (User) session.getAttribute(SessionKeys.CUSTOMER);
        if (user == null) return "redirect:/users/login";
        Optional<Feedback> opt = feedbackService.findById(id);
        if (opt.isEmpty() || !opt.get().getUserId().equals(user.getId())) {
            return "redirect:/feedback/customer/my";
        }
        if (rating == null || !ValidationSupport.inRange(rating, 1, 5)) {
            model.addAttribute("message", "Please choose a star rating between 1 and 5.");
            model.addAttribute("feedback", opt.get());
            model.addAttribute("editMode", true);
            return "feedback/customer-edit";
        }
        Feedback existing = opt.get();
        Feedback updated = buildFeedback(id, user.getId(), existing.getPackageId(),
                clampRating(rating), comment, existing.getCreatedAt(), 900);
        feedbackService.save(updated);
        return "redirect:/feedback/customer/my";
    }

    /** Customer: delete their own feedback. */
    @PostMapping("/customer/delete")
    public String customerDelete(@RequestParam Long id, HttpSession session) {
        User user = (User) session.getAttribute(SessionKeys.CUSTOMER);
        if (user == null) return "redirect:/users/login";
        feedbackService.findById(id).ifPresent(f -> {
            if (f.getUserId().equals(user.getId())) {
                feedbackService.deleteById(id);
            }
        });
        return "redirect:/feedback/customer/my";
    }

    /**
     * Confirmed bookings whose itinerary end date has passed ({@link TripFeedbackRules});
     * each package appears once.
     */
    private List<TourPackage> eligiblePackagesForReviewer(String userId) {
        LocalDate today = TripFeedbackRules.today();
        Map<Long, TourPackage> byId = new LinkedHashMap<>();
        for (Booking b : bookingService.findByUserId(userId)) {
            Long pid = b.getPackageId();
            if (pid == null) {
                continue;
            }
            packageService.findById(pid).ifPresent(pkg -> {
                if (TripFeedbackRules.isEligibleCompletedTrip(b, pkg, today)) {
                    byId.putIfAbsent(pkg.getId(), pkg);
                }
            });
        }
        return new ArrayList<>(byId.values());
    }

    private boolean isEligibleFeedbackPackage(String userId, Long packageId) {
        return packageService.findById(packageId)
                .map(pkg -> bookingService.findByUserId(userId).stream()
                        .anyMatch(b -> Objects.equals(b.getPackageId(), packageId)
                                && TripFeedbackRules.isEligibleCompletedTrip(b, pkg, TripFeedbackRules.today())))
                .orElse(false);
    }

    private String adminFeedbackErrors(String userId, Long packageId, Integer rating, String comment) {
        if (ValidationSupport.isBlank(userId)) {
            return "Choose a traveler.";
        }
        if (packageId == null || packageId < 1 || packageService.findById(packageId).isEmpty()) {
            return "Choose a package that exists in the catalog.";
        }
        if (rating == null || !ValidationSupport.inRange(rating, 1, 5)) {
            return "Rating must be between 1 and 5.";
        }
        if (userService.findById(userId.trim()).isEmpty()) {
            return "That traveler record does not exist.";
        }
        if (comment != null && comment.length() > 1000) {
            return "Comment must be at most 1000 characters.";
        }
        return null;
    }

    private static int clampRating(int rating) {
        return Math.min(Math.max(rating, 1), 5);
    }

    private static Feedback buildFeedback(Long id,
                                          String userId,
                                          Long packageId,
                                          int rating,
                                          String comment,
                                          String createdAt,
                                          int commentMaxLen) {
        Feedback f = new Feedback();
        f.setId(id);
        f.setUserId(userId == null ? "" : userId.trim());
        f.setPackageId(packageId);
        f.setRating(rating);
        f.setComment(ValidationSupport.trimLen(comment == null ? "" : comment, commentMaxLen));
        f.setCreatedAt(createdAt);
        return f;
    }
}
