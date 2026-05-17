package com.bakery.bakeryms_group.controller;

import com.bakery.model.User;
import com.bakery.model.ProductReview;
import com.bakery.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;

@Controller
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // --- ADD REVIEW ---
    @PostMapping("/add-review")
    public String addReview(@RequestParam("productName") String productName,
                            @RequestParam("rating") int rating,
                            @RequestParam("comment") String comment,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Please login to add a review.");
            return "redirect:/login";
        }

        ProductReview review = new ProductReview();
        review.setProductName(productName);
        review.setUserName(user.getUsername());

        if (user.getImage() != null && !user.getImage().isEmpty()) {
            review.setUserImage(user.getImage());
        } else {
            review.setUserImage("user-avatar1.jpg");
        }

        review.setRating(rating);
        review.setComment(comment);
        review.setReviewDate(LocalDate.now());

        reviewService.saveReviewToFile(review);

        redirectAttributes.addFlashAttribute("message", "Review added successfully!");
        return "redirect:/product-detail?name=" + productName;
    }

    // --- DELETE REVIEW ---
    @PostMapping("/delete-review")
    public String deleteReview(@RequestParam String productName,
                               @RequestParam String userName,
                               @RequestParam String date,
                               HttpSession session,
                               RedirectAttributes ra) {

        User currentUser = (User) session.getAttribute("user");
        if (currentUser != null && (currentUser.getUsername().equals(userName) || "ADMIN".equals(currentUser.getRole()))) {
            reviewService.deleteReview(productName, userName, date);
            ra.addFlashAttribute("message", "Review deleted successfully!");
        } else {
            ra.addFlashAttribute("error", "Unauthorized action!");
        }

        return "redirect:/product-detail?name=" + productName;
    }
}
