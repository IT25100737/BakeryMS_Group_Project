package com.bakery.bakeryms_group.service;

import org.springframework.stereotype.Service;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class OrderService {
    private final String ORDER_FILE = System.getProperty("user.dir") + File.separator + "orders.txt";

    public void saveOrderToFile(String orderDetails) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ORDER_FILE, true))) {
            writer.write(orderDetails);
            writer.newLine();
        } catch (IOException e) {}
    }

    public List<String> getAllOrders() {
        List<String> orders = new ArrayList<>();
        File file = new File(ORDER_FILE);
        if (!file.exists()) return orders;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) orders.add(line);
            }
            Collections.reverse(orders);
        } catch (IOException e) {}
        return orders;
    }

    public List<String> getOrdersByUsername(String fullName) {
        List<String> userOrders = new ArrayList<>();
        if (fullName == null) return userOrders;

        File file = new File(ORDER_FILE);
        if (!file.exists()) return userOrders;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("User: " + fullName.trim())) {
                    userOrders.add(line);
                }
            }
            Collections.reverse(userOrders);
        } catch (IOException e) {}
        return userOrders;
    }

    public void updateOrderStatusInFile(int index, String nextStatus) {
        List<String> orders = getAllOrders();
        if (index >= 0 && index < orders.size()) {
            String orderLine = orders.get(index);
            if (orderLine.contains("Status:")) {
                String[] parts = orderLine.split("\\|");
                StringBuilder updatedLine = new StringBuilder();
                for (String part : parts) {
                    if (part.trim().startsWith("Status:")) {
                        updatedLine.append(" Status: ").append(nextStatus).append(" |");
                    } else {
                        updatedLine.append(part).append("|");
                    }
                }
                String finalLine = updatedLine.toString();
                if (finalLine.endsWith("|")) finalLine = finalLine.substring(0, finalLine.length() - 1);

                orders.set(index, finalLine.trim());
                rewriteOrderFile(orders);
            }
        }
    }

    public void deleteOrderFromFile(int index) {
        List<String> orders = getAllOrders();
        if (index >= 0 && index < orders.size()) {
            orders.remove(index);
            rewriteOrderFile(orders);
        }
    }

    public void rewriteOrderFile(List<String> orders) {
        if (orders == null) return;
        List<String> tempOrders = new ArrayList<>(orders);
        Collections.reverse(tempOrders);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ORDER_FILE, false))) {
            for (String o : tempOrders) {
                writer.write(o);
                writer.newLine();
            }
        } catch (IOException e) {}
    }
}
