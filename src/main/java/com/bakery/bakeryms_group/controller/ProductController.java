package com.bakery.bakeryms_group.controller;

import com.bakery.model.Product;
import com.bakery.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

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


}