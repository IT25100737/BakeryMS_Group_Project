package com.bakery.bakeryms_group.service;

import com.bakery.bakeryms_group.model.ProductReview;
import org.springframework.stereotype.Service;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ReviewService {
    private final String REVIEW_FILE = System.getProperty("user.dir") + File.separator + "reviews.txt";

    public void saveReviewToFile(ProductReview review) {
        String reviewData = review.getProductName() + " | " + review.getUserName() + " | " +
                (review.getUserImage() != null ? review.getUserImage() : "default-user.png") + " | " +
                review.getRating() + " | " + review.getComment().replaceAll("\\r?\\n", " ") + " | " +
                review.getReviewDate();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(REVIEW_FILE, true))) {
            writer.write(reviewData);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error saving review: " + e.getMessage());
        }
    }

    public List<ProductReview> getReviewsForProduct(String productName) {
        List<ProductReview> productReviews = new ArrayList<>();
        File file = new File(REVIEW_FILE);
        if (!file.exists()) return productReviews;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] r = line.split(" \\| ");

                if (r.length >= 6 && r[0].trim().equalsIgnoreCase(productName.trim())) {
                    productReviews.add(new ProductReview(
                            r[0].trim(), r[1].trim(), r[2].trim(),
                            Integer.parseInt(r[3].trim()), r[4].trim(),
                            LocalDate.parse(r[5].trim())
                    ));
                }
            }
            Collections.reverse(productReviews);
        } catch (IOException | DateTimeParseException | NumberFormatException e) {
            System.err.println("Error reading reviews: " + e.getMessage());
        }
        return productReviews;
    }

    public double calculateAverageRating(String productName) {
        List<ProductReview> reviews = getReviewsForProduct(productName);
        if (reviews.isEmpty()) return 0.0;

        double sum = 0;
        for (ProductReview r : reviews) {
            sum += r.getRating();
        }
        return sum / (double) reviews.size();
    }

    public void deleteReview(String productName, String userName, String date) {
        List<ProductReview> allReviews = new ArrayList<>();
        File file = new File(REVIEW_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] r = line.split(" \\| ");
                if (r.length >= 6) {
                    allReviews.add(new ProductReview(
                            r[0].trim(), r[1].trim(), r[2].trim(),
                            Integer.parseInt(r[3].trim()), r[4].trim(),
                            LocalDate.parse(r[5].trim())
                    ));
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading all reviews for deletion: " + e.getMessage());
        }

        allReviews.removeIf(r -> r.getProductName().equalsIgnoreCase(productName.trim()) &&
                r.getUserName().equalsIgnoreCase(userName.trim()) &&
                r.getReviewDate().toString().equals(date.trim()));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(REVIEW_FILE, false))) {
            for (ProductReview r : allReviews) {
                String data = r.getProductName() + " | " + r.getUserName() + " | " +
                        r.getUserImage() + " | " + r.getRating() + " | " +
                        r.getComment() + " | " + r.getReviewDate();
                writer.write(data);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error rewriting reviews file: " + e.getMessage());
        }
    }
}
