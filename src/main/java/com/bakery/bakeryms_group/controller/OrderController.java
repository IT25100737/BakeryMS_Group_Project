package com.bakery.bakeryms_group.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import com.bakery.bakeryms_group.model.Product;
import com.bakery.bakeryms_group.model.CartItem;
import com.bakery.bakeryms_group.model.User;
import com.bakery.bakeryms_group.service.ProductService;
import com.bakery.bakeryms_group.service.OrderService;
import com.bakery.bakeryms_group.service.MessageService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class
OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private MessageService messageService;

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

    // --- (Cart & Checkout) ---

    @PostMapping("/add-to-cart")
    public String addToCart(@RequestParam String productName, @RequestParam int quantity, HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) cart = new ArrayList<>();

        boolean exists = false;
        for (CartItem item : cart) {
            if (item.getProductName().equals(productName)) {
                item.setQuantity(item.getQuantity() + quantity);
                exists = true;
                break;
            }
        }

        if (!exists) {
            List<Product> products = productService.getAllProducts();
            for (Product p : products) {
                if (p.getName().equals(productName)) {
                    cart.add(new CartItem(p.getName(), p.getPrice(), quantity, p.getImage()));
                    break;
                }
            }
        }
        session.setAttribute("cart", cart);
        return "redirect:/product";
    }

    @GetMapping("/cart")
    public String showCartPage(Model model, HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) cart = new ArrayList<>();
        double total = cart.stream().mapToDouble(i -> i.getPrice() * i.getQuantity()).sum();
        model.addAttribute("cartItems", cart);
        model.addAttribute("totalAmount", total);
        return "cart";
    }

    @PostMapping("/update-cart")
    public String updateCart(@RequestParam String productName, @RequestParam int action, HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart != null) {
            for (CartItem item : cart) {
                if (item.getProductName().equals(productName)) {
                    if (action == 1) item.setQuantity(item.getQuantity() + 1);
                    else if (action == -1 && item.getQuantity() > 1) item.setQuantity(item.getQuantity() - 1);
                    break;
                }
            }
        }
        return "redirect:/cart";
    }

    @GetMapping("/remove-from-cart/{name}")
    public String removeFromCart(@PathVariable String name, HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart != null) cart.removeIf(item -> item.getProductName().equals(name));
        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String showCheckoutPage(HttpSession session, Model model) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) return "redirect:/cart";
        double total = cart.stream().mapToDouble(i -> i.getPrice() * i.getQuantity()).sum();
        model.addAttribute("totalAmount", total);
        return "checkout";
    }

    @PostMapping("/confirm-payment")
    public String confirmPayment(@RequestParam String address,
                                 @RequestParam(defaultValue = "Card") String paymentMethod,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        User user = (User) session.getAttribute("user");

        if (user == null) return "redirect:/login";
        if (cart == null || cart.isEmpty()) return "redirect:/product";

        double total = cart.stream().mapToDouble(i -> i.getPrice() * i.getQuantity()).sum();
        StringBuilder itemsStr = new StringBuilder();
        for(CartItem item : cart) itemsStr.append(item.getProductName()).append("(").append(item.getQuantity()).append("), ");

        String orderData = "WEB ORDER | User: " + user.getUsername() + " | Status: New | Method: " + paymentMethod +
                " | Address: " + address + " | Items: " + itemsStr + " | Total: " + total;

        orderService.saveOrderToFile(orderData);
        session.removeAttribute("cart");

        redirectAttributes.addFlashAttribute("message", "Payment Successful! Your order is on the way.");
        return "redirect:/profile";
    }

    // =================  (Custom Cake Oder) =================

    @PostMapping("/book-custom-cake")
    public String bookCustomCake(@RequestParam("cakeDesign") MultipartFile cakeDesign,
                                 @RequestParam String flavor, @RequestParam String size,
                                 @RequestParam String greeting, @RequestParam String gColor,
                                 @RequestParam String description, @RequestParam String mobile,
                                 @RequestParam String deliveryMethod,
                                 @RequestParam(required = false) String address,
                                 @RequestParam String deliveryDate,
                                 HttpSession session, RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        String finalAddress = "Pickup".equalsIgnoreCase(deliveryMethod) ? "Store Pickup" : address;
        if (finalAddress == null || finalAddress.isEmpty()) finalAddress = "Not Provided";

        String finalDesc = (description == null || description.isEmpty()) ? "No additional instructions" : description;
        String finalGreeting = (greeting == null || greeting.isEmpty()) ? "No Greeting" : greeting + " (" + gColor + ")";

        String fileName = "no-design.jpg";
        if (!cakeDesign.isEmpty()) {
            try {
                fileName = System.currentTimeMillis() + "_" + cakeDesign.getOriginalFilename();
                String uploadDir = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "static" + File.separator + "img" + File.separator + "custom_orders";

                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs();

                Files.write(Paths.get(uploadDir + File.separator + fileName), cakeDesign.getBytes());
            } catch (IOException e) { e.printStackTrace(); }
        }

        String orderData = "CUSTOM CAKE | User: " + user.getUsername() +
                " | Status: New | Design: " + fileName +
                " | Flavor: " + flavor + " | Size: " + size +
                " | Greeting: " + finalGreeting +
                " | Contact: " + mobile + " | Date: " + deliveryDate +
                " | Address: " + finalAddress + " | Desc: " + finalDesc;

        orderService.saveOrderToFile(orderData);

        redirectAttributes.addFlashAttribute("message", "Custom cake request sent!");
        return "redirect:/profile";
    }

    // =================  (CONTACT MESSAGE) =================
    @PostMapping("/send-message")
    public String sendMessage(@RequestParam String name,
                              @RequestParam String email,
                              @RequestParam String subject,
                              @RequestParam String message,
                              RedirectAttributes ra) {
        try {
            String raw = name + " | " + email + " | " + subject + " | " + message + " | " + LocalDate.now();
            messageService.saveContactMessage(raw);
            ra.addFlashAttribute("message", "Message sent successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "The message could not be sent.");
        }
        return "redirect:/contact";
    }


}