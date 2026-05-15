package com.tourism.platform.controller;

import com.tourism.platform.model.Destination;
import com.tourism.platform.service.DestinationService;
import com.tourism.platform.util.ValidationSupport;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/destinations")
public class DestinationController {

    private final DestinationService destinationService;

    public DestinationController(DestinationService destinationService) {
        this.destinationService = destinationService;
    }

    @GetMapping({"", "/", "/list"})
    public String list(Model model) {
        model.addAttribute("destinations", destinationService.findAll());
        return "destination/list";
    }

    @GetMapping("/detail")
    public String detail(@RequestParam Long id, Model model) {
        return destinationService.findById(id)
                .map(d -> {
                    model.addAttribute("destination", d);
                    return "destination/detail";
                })
                .orElse("redirect:/destinations/list");
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("destination", new Destination());
        model.addAttribute("editMode", false);
        return "destination/form";
    }

    @PostMapping("/create")
    public String create(@RequestParam(required = false) String name,
                         @RequestParam(required = false) String country,
                         @RequestParam(required = false) String description,
                         Model model) {
        String err = destinationFieldErrors(name, country, description);
        if (err != null) {
            model.addAttribute("message", err);
            model.addAttribute("destination", buildDestination(null, name, country, description));
            model.addAttribute("editMode", false);
            return "destination/form";
        }
        destinationService.save(buildDestination(null, name, country, description));
        return "redirect:/destinations/list";
    }

    @GetMapping("/edit")
    public String editForm(@RequestParam Long id, Model model) {
        return destinationService.findById(id)
                .map(d -> {
                    model.addAttribute("destination", d);
                    model.addAttribute("editMode", true);
                    return "destination/form";
                })
                .orElse("redirect:/destinations/list");
    }

    @PostMapping("/update")
    public String update(@RequestParam Long id,
                         @RequestParam(required = false) String name,
                         @RequestParam(required = false) String country,
                         @RequestParam(required = false) String description,
                         Model model) {
        String err = destinationFieldErrors(name, country, description);
        if (err != null) {
            model.addAttribute("message", err);
            model.addAttribute("destination", buildDestination(id, name, country, description));
            model.addAttribute("editMode", true);
            return "destination/form";
        }
        destinationService.save(buildDestination(id, name, country, description));
        return "redirect:/destinations/list";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam Long id) {
        destinationService.deleteById(id);
        return "redirect:/destinations/list";
    }

    private static String destinationFieldErrors(String name, String country, String description) {
        if (ValidationSupport.isBlank(name) || name.trim().length() > 160) {
            return "Name is required (max 160 characters).";
        }
        if (ValidationSupport.isBlank(country) || country.trim().length() > 120) {
            return "Country is required (max 120 characters).";
        }
        if (description != null && description.length() > 1000) {
            return "Description must be at most 1000 characters.";
        }
        return null;
    }

    private static Destination buildDestination(Long id, String name, String country, String description) {
        Destination d = new Destination();
        d.setId(id);
        d.setName(ValidationSupport.trimLen(name == null ? "" : name.trim(), 160));
        d.setCountry(ValidationSupport.trimLen(country == null ? "" : country.trim(), 120));
        String descTrim = description == null ? "" : description.trim();
        d.setDescription(ValidationSupport.trimLen(descTrim, 1000));
        return d;
    }
}
