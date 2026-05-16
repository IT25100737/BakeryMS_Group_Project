package com.bakery.bakeryms_group.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OrderController {

    @GetMapping("/")
    public String showHomePage() {
        return "index";
    }

    @GetMapping("/about")
    public String showAboutPage() {
        return "about";
    }

    @GetMapping("/service")
    public String showServicePage() {
        return "service";
    }

    @GetMapping("/contact")
    public String showContactPage() {
        return "contact";
    }

    @GetMapping("/products")
    public String showProductPage() {
        return "products";
    }

    @GetMapping("/custom-cake")
    public String showCustomCakePage() {
        return "custom-cake";
    }
}