package com.bakery.service;

import com.bakery.model.User;
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final String USER_FILE = System.getProperty("user.dir") + File.separator + "users.txt";

    public void saveUserToFile(String userData) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE, true))) {
            writer.write(userData);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error saving user: " + e.getMessage());
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        File file = new File(USER_FILE);
        if (!file.exists()) createDefaultAdmin();

        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] u = line.split(",");
                if (u.length >= 9) {
                    users.add(new User(u[0].trim(), u[1].trim(), u[2].trim(), u[3].trim(),
                            u[4].trim(), u[5].trim(), u[6].trim(), u[7].trim(), u[8].trim()));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading users: " + e.getMessage());
        }
        return users;
    }

    public void rewriteUserFile(List<User> users) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE, false))) {
            for (User u : users) {
                writer.write(u.getFullName() + "," + u.getUsername() + "," + u.getEmail() + "," +
                        u.getMobile() + "," + u.getAddress() + "," + u.getPostCode() + "," +
                        u.getPassword() + "," + u.getImage() + "," + u.getRole());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error rewriting users: " + e.getMessage());
        }
    }

    private void createDefaultAdmin() {
        saveUserToFile("System Admin,admin,admin@bakery.com,011,Colombo,10100,admin123,default-user.png,ADMIN");
    }

    public void deleteUserByUsername(String username) {
        if (username == null) return;
        List<User> allUsers = getAllUsers();
        allUsers.removeIf(u -> u.getUsername().equalsIgnoreCase(username.trim()));
        rewriteUserFile(allUsers);
    }
}
