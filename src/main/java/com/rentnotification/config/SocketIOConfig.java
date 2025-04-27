package com.rentnotification.config;

import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SocketIOConfig {
    @Bean
    public SocketIOServer socketIOServer() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname("localhost");
        config.setPort(9092); // Use a different port from HTTP (8080)
        SocketIOServer server = new SocketIOServer(config);
        server.addConnectListener(client -> {
            System.out.println("Client connected: " + client.getSessionId());
            client.sendEvent("connectionStatus", "Connected to Socket.IO server");
        });
        server.addDisconnectListener(client -> {
            System.out.println("Client disconnected: " + client.getSessionId());
        });
        server.addEventListener("registerToken", String.class, (client, token, ackSender) -> {
            System.out.println("Received token from client: " + token);
            client.sendEvent("tokenStatus", "Token received by server");
        });
        server.start();
        return server;
    }
}