package com.bakery.bakeryms_group.controller;

import com.bakery.model.User;
import com.bakery.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class LoginController {

    @Autowired
    private UserService UserService;

    // --- LOGIN SECTION ---

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam String username,
                              @RequestParam String password,
                              HttpSession session,
                              Model model) {

        // Getting all users through the FileService
        List<User> users = UserService.getAllUsers();

        for (User u : users) {
            if (u.getUsername().trim().equals(username.trim()) &&
                    u.getPassword().trim().equals(password.trim())) {

                session.setAttribute("user", u);

                String userRole = (u.getRole() != null) ? u.getRole().trim() : "";

                if ("ADMIN".equalsIgnoreCase(userRole)) {
                    return "redirect:/admin";
                } else {
                    return "redirect:/product";
                }
            }
        }

        model.addAttribute("error", "Invalid username or password!");
        return "login";
    }

    // --- REGISTER SECTION ---

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String fullName,
                               @RequestParam String username,
                               @RequestParam String email,
                               @RequestParam String mobile,
                               @RequestParam String address,
                               @RequestParam String postCode,
                               @RequestParam String password,
                               @RequestParam(required = false) MultipartFile imageFile,
                               RedirectAttributes redirectAttributes) {

        String fileName = "default-user.png";
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                String uploadDir = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "static" + File.separator + "img";

                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs();

                Files.write(Paths.get(uploadDir + File.separator + fileName), imageFile.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String userData = fullName.trim() + "," + username.trim() + "," + email.trim() + "," +
                mobile.trim() + "," + address.trim() + "," + postCode.trim() + "," +
                password.trim() + "," + fileName + ",USER";

        UserService.saveUserToFile(userData);

        redirectAttributes.addFlashAttribute("message", "Registration Successful! Please Login.");
        return "redirect:/login";
    }

    // --- LOGOUT SECTION ---

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }
}