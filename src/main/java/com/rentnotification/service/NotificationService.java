package com.rentnotification.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Service
public class NotificationService {
    private final SqlSessionFactory sqlSessionFactory;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public NotificationService(SqlSessionFactory sqlSessionFactory, SimpMessagingTemplate messagingTemplate) {
        this.sqlSessionFactory = sqlSessionFactory;
        this.messagingTemplate = messagingTemplate;
    }

    @PostConstruct
    public void initializeFirebase() {
        try {
            ClassPathResource resource = new ClassPathResource("firebase/rent-notifications-firebase-adminsdk.json");
            System.out.println("Service account file exists: " + resource.exists());
            GoogleCredentials credentials = GoogleCredentials.fromStream(resource.getInputStream());
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("Firebase initialized successfully");
            }
        } catch (IOException e) {
            System.err.println("Failed to initialize Firebase: " + e.getMessage());
            throw new RuntimeException("Firebase initialization failed", e);
        }
    }

    public void registerToken(Long tenantId, String fcmToken) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            int updated = session.update("com.rentnotification.mapper.TenantMapper.updateFcmToken",
                    new TenantUpdate(tenantId, fcmToken));
            if (updated == 0) {
                throw new RuntimeException("No tenant found with ID: " + tenantId);
            }
            session.commit();
            System.out.println("FCM token updated for tenant ID: " + tenantId);
            messagingTemplate.convertAndSend("/topic/notifications",
                    "Token registered for tenant ID: " + tenantId);
        } catch (Exception e) {
            System.err.println("Error updating FCM token: " + e.getMessage());
            throw new RuntimeException("Failed to update FCM token: " + e.getMessage(), e);
        }
    }

    public void sendNotification(String fcmToken, String tenantName, double amount, String dueDate, String reminderType) {
        try {
            Message message = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(Notification.builder()
                            .setTitle("Rent Reminder")
                            .setBody(String.format("%s, your rent of $%.2f is due on %s (%s).", tenantName, amount, dueDate, reminderType))
                            .build())
                    .putData("amount", String.valueOf(amount))
                    .putData("dueDate", dueDate)
                    .putData("reminderType", reminderType)
                    .build();
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Notification sent: " + response);
            messagingTemplate.convertAndSend("/topic/notifications",
                    "Notification sent to " + tenantName + ": Rent of $" + amount + " due on " + dueDate);
        } catch (Exception e) {
            System.err.println("Error sending notification: " + e.getMessage());
            throw new RuntimeException("Failed to send notification: " + e.getMessage(), e);
        }
    }
}