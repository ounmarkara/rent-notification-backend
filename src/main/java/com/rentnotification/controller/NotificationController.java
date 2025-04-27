package com.rentnotification.controller;

import com.rentnotification.request.NotificationRequest;
import com.rentnotification.request.TokenRegistrationRequest;

import com.rentnotification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")

public class NotificationController {
    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/register-token")
    public ResponseEntity<String> registerToken(@RequestBody TokenRegistrationRequest request) {
        try {
            if (request.getId() == null || request.getFcmToken() == null) {
                return ResponseEntity.badRequest().body("Invalid request: id and fcmToken are required");
            }
            notificationService.registerToken(request.getId(), request.getFcmToken());
            return ResponseEntity.ok("Token registered successfully");
        } catch (Exception e) {
            System.err.println("Error registering token: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to register token: " + e.getMessage());
        }
    }

    @PostMapping("/send-rent-reminder")
    public ResponseEntity<String> sendRentReminder(@RequestBody NotificationRequest request) {
        try {
            notificationService.sendNotification(
                    request.getFcmToken(),
                    request.getTenantName(),
                    request.getAmount(),
                    request.getDueDate(),
                    request.getReminderType()
            );
            return ResponseEntity.ok("Notification sent successfully");
        } catch (Exception e) {
            System.err.println("Error sending notification: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to send notification: " + e.getMessage());
        }
    }
}