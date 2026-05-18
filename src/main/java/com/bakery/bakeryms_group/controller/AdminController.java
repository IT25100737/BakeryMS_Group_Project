package com.bakery.bakeryms_group.controller;

import com.bakery.bakeryms_group.model.User;
import com.bakery.bakeryms_group.model.Order;
import com.bakery.bakeryms_group.model.OrderItem;
import com.bakery.bakeryms_group.model.Product;
import com.bakery.bakeryms_group.model.ContactMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import com.bakery.bakeryms_group.service.UserService;
import com.bakery.bakeryms_group.service.ProductService;
import com.bakery.bakeryms_group.service.OrderService;
import com.bakery.bakeryms_group.service.MessageService;

@Controller
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private MessageService messageService;


    // =================  (ADMIN DASHBOARD) =================
    @GetMapping("/admin")
    public String showAdminDashboard(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || !"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            return "redirect:/login";
        }

        List<User> allUsers = userService.getAllUsers();
        List<Product> allProducts = productService.getAllProducts();
        List<String> rawOrders = orderService.getAllOrders();

        // Orders Parse
        List<Order> parsedOrders = new ArrayList<>();
        for (int i = 0; i < rawOrders.size(); i++) {
            try {
                Order o = parseOrderString(rawOrders.get(i));
                if (o != null) {
                    o.setIndex(i);
                    parsedOrders.add(o);
                }
            } catch (Exception e) {
                System.err.println("Error parsing admin order at index " + i + ": " + e.getMessage());
            }
        }

        //  (Messages) Parse
        List<String> rawMessages = messageService.getAllMessages();
        List<ContactMessage> parsedMessages = new ArrayList<>();
        for (int i = 0; i < rawMessages.size(); i++) {
            String m = rawMessages.get(i);
            String[] mParts = m.split(" \\| ");
            if (mParts.length >= 5) {
                ContactMessage msg = new ContactMessage(mParts[0], mParts[1], mParts[2], mParts[3], mParts[4]);
                msg.setIndex(i);
                parsedMessages.add(msg);
            }
        }

        model.addAttribute("users", allUsers);
        model.addAttribute("products", allProducts);
        model.addAttribute("orders", parsedOrders);
        model.addAttribute("messages", parsedMessages);

        return "admin";
    }

    //===== DELETE USER ======
    @PostMapping("/admin/delete-user")
    public String deleteUser(@RequestParam("username") String username, RedirectAttributes redirectAttributes) {
        try {
            // Logic to delete the user from the database
            userService.deleteUserByUsername(username);
            redirectAttributes.addFlashAttribute("message", "User deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete user. Please try again.");
        }
        // Redirect back to the user section on the admin panel
        return "redirect:/admin#user-section";
    }

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


    // =================  (UPDATE STATUS) =================
    @PostMapping("/admin/update-order-status")
    public String updateOrderStatus(@RequestParam("orderIndex") int index,
                                    @RequestParam("currentStatus") String currentStatus,
                                    RedirectAttributes redirectAttributes) {
        try {
            String newStatus = "";
            String status = currentStatus.trim();
            if ("New".equalsIgnoreCase(status)) newStatus = "Accepted";
            else if ("Accepted".equalsIgnoreCase(status)) newStatus = "Pending";
            else if ("Pending".equalsIgnoreCase(status)) newStatus = "Done";

            if (!newStatus.isEmpty()) {
                orderService.updateOrderStatusInFile(index, newStatus);
                redirectAttributes.addFlashAttribute("message", "Status updated to " + newStatus);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Update failed: " + e.getMessage());
        }
        return "redirect:/admin#order-section";
    }

    // =================  (DELETE ORDER) =================
    @GetMapping("/admin/delete-order-record/{index}")
    public String deleteOrder(@PathVariable("index") int index, RedirectAttributes redirectAttributes) {
        try {
            orderService.deleteOrderFromFile(index);
            redirectAttributes.addFlashAttribute("message", "Order record deleted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Delete failed.");
        }
        return "redirect:/admin#order-section";
    }

    // ================= Data extraction assistant (PARSE LOGIC) =================
    private Order parseOrderString(String raw) {
        if (raw == null || raw.trim().isEmpty()) return null;
        try {
            String[] parts = raw.split(" \\| ");
            Order o = new Order();
            o.setOrderId("#" + (Math.abs(raw.hashCode() % 9000) + 1000));

            String orderType = parts[0].trim();
            o.setType(orderType);
            o.setUsername(extractValue(parts[1], "User:"));
            o.setStatus(extractValue(parts[2], "Status:"));

            if (orderType.equalsIgnoreCase("CUSTOM CAKE")) {
                o.setMethod("Delivery/Pickup");
                o.setTotal("Custom Quote");
                for (String part : parts) {
                    String p = part.trim();
                    if (p.startsWith("Date:")) o.setDeliveryDate(p.replace("Date:", "").trim());
                    if (p.startsWith("Flavor:")) o.setFlavor(p.replace("Flavor:", "").trim());
                    if (p.startsWith("Size:")) o.setSize(p.replace("Size:", "").trim());
                    if (p.startsWith("Greeting:")) o.setGreeting(p.replace("Greeting:", "").trim());
                    if (p.startsWith("Contact:")) o.setMobile(p.replace("Contact:", "").trim());
                    if (p.startsWith("Address:")) o.setAddress(p.replace("Address:", "").trim());
                    if (p.startsWith("Desc:")) o.setSpecialDescription(p.replace("Desc:", "").trim());
                    if (p.startsWith("Design:") || p.startsWith("Image:")) {
                        String imgName = p.substring(p.indexOf(":") + 1).trim();
                        o.setImageName(imgName);
                        o.setCakeDesign(imgName);
                    }
                }
            } else {
                o.setMethod(extractValue(parts[3], "Method:"));
                o.setAddress(extractValue(parts[4], "Address:"));
                o.setTotal(extractValue(parts[6], "Total:"));

                String rawItems = extractValue(parts[5], "Items:");
                List<OrderItem> itemList = new ArrayList<>();
                if (!"N/A".equals(rawItems)) {
                    for (String itemStr : rawItems.split(",")) {
                        if (itemStr.contains("(") && itemStr.contains(")")) {
                            String itemName = itemStr.substring(0, itemStr.indexOf("(")).trim();
                            String qty = itemStr.substring(itemStr.indexOf("(") + 1, itemStr.indexOf(")")).trim();
                            Product prod = productService.getProductByName(itemName);
                            String priceVal = (prod != null) ? String.valueOf(prod.getPrice()) : "0.0";
                            itemList.add(new OrderItem(itemName, qty, priceVal));
                        }
                    }
                }
                o.setParsedItems(itemList);
            }
            return o;
        } catch (Exception e) {
            return null;
        }
    }

    private String extractValue(String part, String prefix) {
        if (part == null || !part.contains(prefix)) return "N/A";
        return part.substring(part.indexOf(prefix) + prefix.length()).trim();
    }

    // =================  (DELETE MESSAGE) =================
    @GetMapping("/admin/delete-message/{index}")
    public String deleteMessage(@PathVariable("index") int index, RedirectAttributes ra) {
        try {
            List<String> allMessages = messageService.getAllMessages();
            if (index >= 0 && index < allMessages.size()) {
                allMessages.remove(index);
                messageService.rewriteMessageFile(allMessages);
                ra.addFlashAttribute("message", "Message deleted successfully.");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Delete failed.");
        }
        return "redirect:/admin#contact-section";
    }



}