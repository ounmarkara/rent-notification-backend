package com.rentnotification.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {
    @PostConstruct
    public void initialize() {
        try (InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("firebase/rent-notifications-firebase-adminsdk.json")) {
            if (serviceAccount == null) {
                System.err.println("Error: rent-notifications-firebase-adminsdk.json not found in resources/firebase/. Ensure it is in src/main/resources/firebase/.");
                throw new IOException("rent-notifications-firebase-adminsdk.json not found in resources/firebase/");
            }
            System.out.println("Successfully loaded rent-notifications-firebase-adminsdk.json from resources/firebase/.");
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("FirebaseApp initialized successfully.");
            } else {
                System.out.println("FirebaseApp already initialized.");
            }
        } catch (IOException e) {
            System.err.println("Failed to initialize Firebase: " + e.getMessage());
            throw new RuntimeException("Failed to initialize Firebase: " + e.getMessage(), e);
        }
    }
}