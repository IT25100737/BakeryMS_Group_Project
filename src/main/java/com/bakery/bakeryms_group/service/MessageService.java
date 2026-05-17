package com.bakery.bakeryms_group.service;

import org.springframework.stereotype.Service;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class MessageService {
    private final String MESSAGE_FILE = System.getProperty("user.dir") + File.separator + "messages.txt";

    public void saveContactMessage(String rawMessage) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(MESSAGE_FILE, true))) {
            writer.write(rawMessage);
            writer.newLine();
        } catch (IOException e) {}
    }

    public List<String> getAllMessages() {
        List<String> messages = new ArrayList<>();
        File file = new File(MESSAGE_FILE);
        if (!file.exists()) return messages;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) messages.add(line);
            }
            Collections.reverse(messages);
        } catch (IOException e) {}
        return messages;
    }

    public void rewriteMessageFile(List<String> messages) {
        List<String> tempMessages = new ArrayList<>(messages);
        Collections.reverse(tempMessages);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(MESSAGE_FILE, false))) {
            for (String msg : tempMessages) {
                writer.write(msg);
                writer.newLine();
            }
        } catch (IOException e) {}
    }
}
