package com.rentnotification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

@Service
public class FirebaseNotificationService {
    public void sendRentReminder(String fcmToken, String tenantName, double amount, String dueDate, String reminderType) throws Exception {
        if (fcmToken == null || fcmToken.isEmpty()) {
            System.out.println("No FCM token for tenant: " + tenantName);
            return;
        }

        String messageBody = switch (reminderType) {
            case "BEFORE_DUE" -> String.format("Hi %s, your rent of $%.2f is due tomorrow (%s). Pay now!", tenantName, amount, dueDate);
            case "THREE_DAYS_PAST_DUE" -> String.format("Hi %s, your rent of $%.2f was due on %s. Please pay within 4 days to avoid penalties.", tenantName, amount, dueDate);
            case "SEVEN_DAYS_PAST_DUE" -> String.format("Hi %s, your rent of $%.2f is 7 days overdue (%s). Pay immediately to avoid further action.", tenantName, amount, dueDate);
            default -> "Invalid reminder type";
        };

        Message message = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle("Rent Payment Reminder")
                        .setBody(messageBody)
                        .build())
                .putData("amount", String.valueOf(amount))
                .putData("dueDate", dueDate)
                .putData("reminderType", reminderType)
                .setToken(fcmToken)
                .build();

        String response = FirebaseMessaging.getInstance().send(message);
        System.out.println("FCM message sent to " + tenantName + ": " + response);
    }
}