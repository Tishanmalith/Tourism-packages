package com.tourism.platform.controller;

import com.tourism.platform.model.TourPackage;
import com.tourism.platform.service.DestinationService;
import com.tourism.platform.service.PackageService;
import com.tourism.platform.util.ValidationSupport;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/packages")
public class PackageController {

    private final PackageService packageService;
    private final DestinationService destinationService;

    public PackageController(PackageService packageService, DestinationService destinationService) {
        this.packageService = packageService;
        this.destinationService = destinationService;
    }

    @GetMapping({"", "/", "/list"})
    public String list(Model model) {
        model.addAttribute("packages", packageService.findAll());
        model.addAttribute("destinations", destinationService.findAll());
        return "package/list";
    }

    @GetMapping("/detail")
    public String detail(@RequestParam Long id, Model model) {
        return packageService.findById(id)
                .map(p -> {
                    model.addAttribute("tourPackage", p);
                    destinationService.findById(p.getDestinationId())
                            .ifPresent(d -> model.addAttribute("destination", d));
                    return "package/detail";
                })
                .orElse("redirect:/packages/list");
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("tourPackage", new TourPackage());
        model.addAttribute("destinations", destinationService.findAll());
        model.addAttribute("editMode", false);
        return "package/form";
    }

    @PostMapping("/create")
    public String create(@RequestParam String name,
                         @RequestParam(required = false) String description,
                         @RequestParam(required = false) Double price,
                         @RequestParam(required = false) Integer durationDays,
                         @RequestParam(required = false) Long destinationId,
                         Model model) {
        String err = packageFieldErrors(name, description, price, durationDays, destinationId);
        if (err != null) {
            TourPackage p = buildPackage(null, name, description, priceOrZero(price),
                    durationOrZero(durationDays), destinationId);
            model.addAttribute("message", err);
            model.addAttribute("tourPackage", p);
            model.addAttribute("destinations", destinationService.findAll());
            model.addAttribute("editMode", false);
            return "package/form";
        }
        if (destinationService.findById(destinationId).isEmpty()) {
            TourPackage p = buildPackage(null, name, description, priceOrZero(price),
                    durationOrZero(durationDays), destinationId);
            model.addAttribute("message", "That destination no longer exists. Pick another.");
            model.addAttribute("tourPackage", p);
            model.addAttribute("destinations", destinationService.findAll());
            model.addAttribute("editMode", false);
            return "package/form";
        }
        packageService.save(buildPackage(null, name, description, price, durationDays, destinationId));
        return "redirect:/packages/list";
    }

    @GetMapping("/edit")
    public String editForm(@RequestParam Long id, Model model) {
        return packageService.findById(id)
                .map(p -> {
                    model.addAttribute("tourPackage", p);
                    model.addAttribute("destinations", destinationService.findAll());
                    model.addAttribute("editMode", true);
                    return "package/form";
                })
                .orElse("redirect:/packages/list");
    }

    @PostMapping("/update")
    public String update(@RequestParam Long id,
                         @RequestParam String name,
                         @RequestParam(required = false) String description,
                         @RequestParam(required = false) Double price,
                         @RequestParam(required = false) Integer durationDays,
                         @RequestParam(required = false) Long destinationId,
                         Model model) {
        String err = packageFieldErrors(name, description, price, durationDays, destinationId);
        if (err != null) {
            TourPackage p = buildPackage(id, name, description, priceOrZero(price),
                    durationOrZero(durationDays), destinationId);
            model.addAttribute("message", err);
            model.addAttribute("tourPackage", p);
            model.addAttribute("destinations", destinationService.findAll());
            model.addAttribute("editMode", true);
            return "package/form";
        }
        if (destinationService.findById(destinationId).isEmpty()) {
            TourPackage p = buildPackage(id, name, description, priceOrZero(price),
                    durationOrZero(durationDays), destinationId);
            model.addAttribute("message", "That destination no longer exists. Pick another.");
            model.addAttribute("tourPackage", p);
            model.addAttribute("destinations", destinationService.findAll());
            model.addAttribute("editMode", true);
            return "package/form";
        }
        packageService.save(buildPackage(id, name, description, price, durationDays, destinationId));
        return "redirect:/packages/list";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam Long id) {
        packageService.deleteById(id);
        return "redirect:/packages/list";
    }

    private static Double priceOrZero(Double price) {
        return price == null ? Double.valueOf(0) : price;
    }

    private static int durationOrZero(Integer durationDays) {
        return durationDays == null ? 0 : durationDays;
    }

    /**
     * @return error message or null if OK
     */
    private static String packageFieldErrors(String name,
                                             String description,
                                             Double price,
                                             Integer durationDays,
                                             Long destinationId) {
        if (ValidationSupport.isBlank(name) || name.trim().length() > 160) {
            return "Name is required (max 160 characters).";
        }
        if (description != null && description.length() > 1000) {
            return "Description must be at most 1000 characters.";
        }
        if (destinationId == null || destinationId < 1) {
            return "Choose a destination.";
        }
        if (price == null) {
            return "Enter a price in LKR.";
        }
        if (!ValidationSupport.validPositivePriceLkr(price)) {
            return "Price must be a positive LKR amount.";
        }
        if (durationDays == null) {
            return "Enter duration in days.";
        }
        if (durationDays < 1 || durationDays > 365) {
            return "Duration must be between 1 and 365 days.";
        }
        return null;
    }

    private static TourPackage buildPackage(Long id,
                                            String name,
                                            String description,
                                            Double price,
                                            Integer durationDays,
                                            Long destinationId) {
        TourPackage p = new TourPackage();
        p.setId(id);
        p.setName(ValidationSupport.trimLen(name == null ? "" : name.trim(), 160));
        String descTrim = description == null ? "" : description.trim();
        p.setDescription(ValidationSupport.trimLen(descTrim, 1000));
        p.setPrice(price == null ? 0 : Math.max(price, 0));
        p.setDurationDays(durationDays == null ? 7 : durationDays);
        p.setDestinationId(destinationId);
        return p;
    }
}
