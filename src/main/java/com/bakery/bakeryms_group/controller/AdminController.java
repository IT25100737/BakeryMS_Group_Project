package com.bakery.bakeryms_group.controller;

import com.bakery.model.User;
import com.bakery.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.bakery.service.ProductService;

@Controller
public class AdminController {

    @Autowired
    private ProductService productService;


    // ======   ADD PRODUCT =======

    @PostMapping("/admin/add-product")
    public String addProduct(@RequestParam String name, @RequestParam double price,
                             @RequestParam String category, @RequestParam String subCategory,
                             @RequestParam String description, @RequestParam int quantity,
                             @RequestParam(value = "isActive", defaultValue = "true") boolean isActive,
                             @RequestParam("imageFile") MultipartFile imageFile, HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null || !"ADMIN".equals(user.getRole())) return "redirect:/login";

        String fileName = "no-image.jpg";
        if (!imageFile.isEmpty()) {
            try {
                fileName = imageFile.getOriginalFilename();
                String srcDir = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "static" + File.separator + "img";
                Files.write(Paths.get(srcDir + File.separator + fileName), imageFile.getBytes());
            } catch (IOException e) { e.printStackTrace(); }
        }

        String data = name + "," + price + "," + category + "," + subCategory + "," + description + "," + quantity + "," + isActive + "," + fileName;
        productService.saveProductToFile(data);
        return "redirect:/admin";
    }

    // =================  (UPDATE PRODUCT) =================
    @PostMapping("/admin/update-product")
    public String updateProduct(@RequestParam("name") String name,
                                @RequestParam("category") String category,
                                @RequestParam("subCategory") String subCategory,
                                @RequestParam("price") Double price,
                                @RequestParam("quantity") Integer quantity,
                                @RequestParam("description") String description,
                                @RequestParam(value = "isActive", defaultValue = "false") boolean isActive,
                                @RequestParam("existingImage") String existingImage,
                                @RequestParam("imageFile") MultipartFile imageFile,
                                RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.getProductByName(name);
            if (product != null) {
                if (imageFile != null && !imageFile.isEmpty()) {
                    product.setImage(imageFile.getOriginalFilename());
                } else {
                    product.setImage(existingImage);
                }
                product.setCategory(category);
                product.setSubCategory(subCategory);
                product.setPrice(price);
                product.setQuantity(quantity);
                product.setDescription(description);
                product.setActive(isActive);

                productService.saveProduct(product);
                redirectAttributes.addFlashAttribute("message", "Product updated!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Update failed.");
        }
        return "redirect:/admin#product-section";
    }

    //===== DELETE PRODUCT =====
    @GetMapping("/admin/delete-product/{name}")
    public String deleteProduct(@PathVariable String name, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ADMIN".equals(user.getRole())) return "redirect:/login";

        productService.deleteProduct(name);
        return "redirect:/admin";
    }
    

}