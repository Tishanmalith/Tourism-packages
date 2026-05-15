package com.tourism.platform.controller;

import com.tourism.platform.model.User;
import com.tourism.platform.security.SessionKeys;
import com.tourism.platform.service.UserService;
import com.tourism.platform.util.ValidationSupport;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping({"", "/", "/list"})
    public String list(@RequestParam(value = "q", required = false) String query, Model model) {
        model.addAttribute("users", userService.search(query));
        model.addAttribute("searchQuery", query == null ? "" : query);
        return "user/list";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "user/register";
    }

    @PostMapping("/register")
    public String registerSubmit(@RequestParam String username,
                                 @RequestParam String password,
                                 @RequestParam String fullName,
                                 @RequestParam String email,
                                 @RequestParam String phone,
                                 Model model) {
        if (invalid(username, password, fullName, email)) {
            model.addAttribute("message", "Username, password, full name and email are required.");
            model.addAttribute("user", populateUser(null, username, password, fullName, email, phone));
            return "user/register";
        }
        if (!ValidationSupport.validUsername(username)) {
            model.addAttribute("message", "Username must be 3–120 characters (letters, digits, underscore, dot, or hyphen).");
            model.addAttribute("user", populateUser(null, username, password, fullName, email, phone));
            return "user/register";
        }
        if (!ValidationSupport.validEmail(email)) {
            model.addAttribute("message", "Enter a valid email address.");
            model.addAttribute("user", populateUser(null, username, password, fullName, email, phone));
            return "user/register";
        }
        if (!ValidationSupport.validOptionalPhone(phone)) {
            model.addAttribute("message", "Phone must be 9–15 digits (spaces or dashes allowed).");
            model.addAttribute("user", populateUser(null, username, password, fullName, email, phone));
            return "user/register";
        }
        if (userService.usernameExistsIgnoreCase(username, null)) {
            model.addAttribute("message", "Username already exists.");
            model.addAttribute("user", populateUser(null, username, password, fullName, email, phone));
            return "user/register";
        }
        userService.save(populateUser(null, username, password, fullName, email, phone));
        return "redirect:/users/login?registered=1";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "registered", required = false) String registered,
                            @RequestParam(value = "error", required = false) String error,
                            Model model) {
        if ("1".equals(registered)) {
            model.addAttribute("message", "Registration successful. Please sign in.");
        }
        if ("1".equals(error)) {
            model.addAttribute("message", "Invalid username or password.");
        }
        model.addAttribute("loginUsername", "");
        return "user/login";
    }

    @PostMapping("/login")
    public String loginSubmit(@RequestParam String username,
                              @RequestParam String password,
                              HttpServletRequest request,
                              Model model) {
        Optional<User> match = userService.findByUsernamePassword(username, password);
        if (match.isEmpty()) {
            model.addAttribute("message", "Invalid username or password.");
            model.addAttribute("loginUsername", username);
            return "user/login";
        }
        HttpSession session = request.getSession(true);
        session.invalidate();
        HttpSession fresh = request.getSession(true);
        fresh.setAttribute(SessionKeys.CUSTOMER, match.get());
        return "redirect:/bookings/my";
    }

    @GetMapping("/new")
    public String adminNewForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("editMode", false);
        return "user/form";
    }

    @PostMapping("/create")
    public String adminCreate(@RequestParam String username,
                              @RequestParam String password,
                              @RequestParam String fullName,
                              @RequestParam String email,
                              @RequestParam String phone,
                              Model model) {
        if (invalid(username, password, fullName, email)) {
            model.addAttribute("message", "Username, password, full name and email are required.");
            model.addAttribute("user", populateUser(null, username, password, fullName, email, phone));
            model.addAttribute("editMode", false);
            return "user/form";
        }
        if (!ValidationSupport.validUsername(username)) {
            model.addAttribute("message", "Username must be 3–120 characters (letters, digits, underscore, dot, or hyphen).");
            model.addAttribute("user", populateUser(null, username, password, fullName, email, phone));
            model.addAttribute("editMode", false);
            return "user/form";
        }
        if (!ValidationSupport.validEmail(email)) {
            model.addAttribute("message", "Enter a valid email address.");
            model.addAttribute("user", populateUser(null, username, password, fullName, email, phone));
            model.addAttribute("editMode", false);
            return "user/form";
        }
        if (!ValidationSupport.validOptionalPhone(phone)) {
            model.addAttribute("message", "Phone must be 9–15 digits (spaces or dashes allowed).");
            model.addAttribute("user", populateUser(null, username, password, fullName, email, phone));
            model.addAttribute("editMode", false);
            return "user/form";
        }
        if (userService.usernameExistsIgnoreCase(username, null)) {
            model.addAttribute("message", "Username already exists.");
            model.addAttribute("user", populateUser(null, username, password, fullName, email, phone));
            model.addAttribute("editMode", false);
            return "user/form";
        }
        userService.save(populateUser(null, username, password, fullName, email, phone));
        return "redirect:/users/list";
    }

    @GetMapping("/detail")
    public String detail(@RequestParam String id, Model model) {
        Optional<User> user = userService.findById(id);
        if (user.isEmpty()) {
            return "redirect:/users/list";
        }
        model.addAttribute("user", user.get());
        return "user/detail";
    }

    @GetMapping("/edit")
    public String editForm(@RequestParam String id, Model model) {
        return userService.findById(id)
                .map(u -> {
                    model.addAttribute("user", u);
                    model.addAttribute("editMode", true);
                    return "user/form";
                })
                .orElse("redirect:/users/list");
    }

    @PostMapping("/update")
    public String update(@RequestParam String id,
                         @RequestParam String username,
                         @RequestParam String password,
                         @RequestParam String fullName,
                         @RequestParam String email,
                         @RequestParam String phone,
                         Model model) {
        if (invalid(username, password, fullName, email)) {
            model.addAttribute("message", "Username, password, full name and email are required.");
            model.addAttribute("user", populateUser(id, username, password, fullName, email, phone));
            model.addAttribute("editMode", true);
            return "user/form";
        }
        if (!ValidationSupport.validUsername(username)) {
            model.addAttribute("message", "Username must be 3–120 characters (letters, digits, underscore, dot, or hyphen).");
            model.addAttribute("user", populateUser(id, username, password, fullName, email, phone));
            model.addAttribute("editMode", true);
            return "user/form";
        }
        if (!ValidationSupport.validEmail(email)) {
            model.addAttribute("message", "Enter a valid email address.");
            model.addAttribute("user", populateUser(id, username, password, fullName, email, phone));
            model.addAttribute("editMode", true);
            return "user/form";
        }
        if (!ValidationSupport.validOptionalPhone(phone)) {
            model.addAttribute("message", "Phone must be 9–15 digits (spaces or dashes allowed).");
            model.addAttribute("user", populateUser(id, username, password, fullName, email, phone));
            model.addAttribute("editMode", true);
            return "user/form";
        }
        if (userService.usernameExistsIgnoreCase(username, id)) {
            model.addAttribute("message", "Username already exists.");
            model.addAttribute("user", populateUser(id, username, password, fullName, email, phone));
            model.addAttribute("editMode", true);
            return "user/form";
        }
        userService.save(populateUser(id, username, password, fullName, email, phone));
        return "redirect:/users/list";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String id) {
        userService.deleteById(id);
        return "redirect:/users/list";
    }

    private static boolean invalid(String username, String password, String fullName, String email) {
        return username == null || username.isBlank()
                || password == null || password.isBlank()
                || fullName == null || fullName.isBlank()
                || email == null || email.isBlank();
    }

    private static final String UNIFIED_ACCOUNT_TYPE = "Passenger";

    private static User populateUser(String id,
                                     String username,
                                     String password,
                                     String fullName,
                                     String email,
                                     String phone) {
        User u = new User();
        u.setId(id);
        u.setUsername(ValidationSupport.trimLen(username == null ? "" : username.trim(), 120));
        u.setPassword(password);
        u.setFullName(ValidationSupport.trimLen(fullName == null ? "" : fullName.trim(), 160));
        u.setEmail(ValidationSupport.trimLen(email == null ? "" : email.trim(), 160));
        u.setPhone(ValidationSupport.trimLen(phone == null ? "" : phone.trim(), 60));
        u.setUserType(UNIFIED_ACCOUNT_TYPE);
        return u;
    }
}
