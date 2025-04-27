package com.rentnotification.service;

import com.corundumstudio.socketio.SocketIOServer;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private final SqlSessionFactory sqlSessionFactory;
    private final SocketIOServer socketIOServer;

    @Autowired
    public NotificationService(SqlSessionFactory sqlSessionFactory, SocketIOServer socketIOServer) {
        this.sqlSessionFactory = sqlSessionFactory;
        this.socketIOServer = socketIOServer;
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
            // Broadcast to all clients
            socketIOServer.getBroadcastOperations().sendEvent("tokenRegistered", "Token registered for tenant ID: " + tenantId);
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
            // Broadcast notification sent
            socketIOServer.getBroadcastOperations().sendEvent("notificationSent", "Notification sent to " + tenantName);
        } catch (Exception e) {
            System.err.println("Error sending notification: " + e.getMessage());
            throw new RuntimeException("Failed to send notification: " + e.getMessage(), e);
        }
    }
}