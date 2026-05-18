package com.bakery.bakeryms_group.service;

import com.bakery.bakeryms_group.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final String PRODUCT_FILE = System.getProperty("user.dir") + File.separator + "products.txt";

    @Autowired
    private ReviewService reviewService;

    public void saveProductToFile(String data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PRODUCT_FILE, true))) {
            writer.write(data);
            writer.newLine();
        } catch (IOException e) {}
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        File file = new File(PRODUCT_FILE);
        if (!file.exists()) return products;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length >= 8) {
                    Product product = new Product(p[0].trim(), Double.parseDouble(p[1].trim()), p[2].trim(),
                            p[3].trim(), p[4].trim(), Integer.parseInt(p[5].trim()),
                            Boolean.parseBoolean(p[6].trim()), p[7].trim(), 0.0);

                    product.setAverageRating(reviewService.calculateAverageRating(product.getName()));
                    products.add(product);
                }
            }
        } catch (Exception e) {}
        return products;
    }

    public List<Product> getAllActiveProducts() {
        return getAllProducts().stream().filter(Product::isActive).collect(Collectors.toList());
    }

    public List<Product> getProductsByCategory(String category) {
        return getAllActiveProducts().stream()
                .filter(p -> p.getCategory().equalsIgnoreCase(category)).collect(Collectors.toList());
    }

    public List<Product> searchProducts(String query) {
        if (query == null || query.isEmpty()) return getAllActiveProducts();
        String lowerQuery = query.toLowerCase().trim();
        return getAllActiveProducts().stream()
                .filter(p -> p.getName().toLowerCase().contains(lowerQuery) ||
                        p.getCategory().toLowerCase().contains(lowerQuery)).collect(Collectors.toList());
    }

    public Product getProductByName(String name) {
        if (name == null) return null;
        return getAllProducts().stream()
                .filter(p -> p.getName().equalsIgnoreCase(name.trim())).findFirst().orElse(null);
    }

    public void saveProduct(Product updatedProduct) {
        List<Product> products = getAllProducts();
        boolean found = false;
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getName().equalsIgnoreCase(updatedProduct.getName().trim())) {
                products.set(i, updatedProduct);
                found = true;
                break;
            }
        }
        if (!found) products.add(updatedProduct);
        rewriteProductFile(products);
    }

    public void deleteProduct(String productName) {
        List<Product> products = getAllProducts();
        products.removeIf(p -> p.getName().equalsIgnoreCase(productName.trim()));
        rewriteProductFile(products);
    }

    private void rewriteProductFile(List<Product> products) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PRODUCT_FILE, false))) {
            for (Product p : products) {
                writer.write(p.getName() + "," + p.getPrice() + "," + p.getCategory() + "," +
                        p.getSubCategory() + "," + p.getDescription() + "," +
                        p.getQuantity() + "," + p.isActive() + "," + p.getImage());
                writer.newLine();
            }
        } catch (IOException e) {}
    }
}