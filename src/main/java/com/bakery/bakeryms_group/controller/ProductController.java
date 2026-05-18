package com.bakery.bakeryms_group.controller;

import com.bakery.bakeryms_group.model.Product;
import com.bakery.bakeryms_group.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import com.bakery.bakeryms_group.service.ReviewService;
import com.bakery.bakeryms_group.model.ProductReview;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ReviewService reviewService;

    // =================  (Show Product) =================
    @GetMapping("/product")
    public String showProducts(@RequestParam(value = "category", required = false, defaultValue = "All") String category,
                               @RequestParam(value = "search", required = false) String search,
                               Model model) {

        List<Product> products;

        if (search != null && !search.isEmpty()) {
            products = productService.searchProducts(search);
        } else if (!category.equals("All")) {
            products = productService.getProductsByCategory(category);
        } else {
            products = productService.getAllActiveProducts();
        }

        model.addAttribute("products", products);
        model.addAttribute("selectedCat", category);
        return "product";
    }

    @GetMapping("/product-detail")
    public String showProductDetail(@RequestParam("name") String name, Model model) {
        Product product = productService.getProductByName(name);

        if (product != null) {
            model.addAttribute("product", product);

            // Getting the review list
            List<ProductReview> productReviews = reviewService.getReviewsForProduct(name);

            // --- Calculating the Average Rating---
            double averageRating = 0.0;
            if (productReviews != null && !productReviews.isEmpty()) {
                double sum = 0;
                for (ProductReview r : productReviews) {
                    sum += r.getRating();
                }
                averageRating = sum / productReviews.size();
            }
            // --------------------------------------------------

            model.addAttribute("reviews", productReviews);
            model.addAttribute("averageRating", averageRating);
            model.addAttribute("totalReviews", productReviews != null ? productReviews.size() : 0); // Total number of reviews

            return "product-detail";
        }

        return "redirect:/product";
    }


}