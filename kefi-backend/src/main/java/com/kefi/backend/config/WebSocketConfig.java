package com.kefi.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // This is the URL your frontend HTML/JS will connect to
        registry.addEndpoint("/ws-kefi")
                .setAllowedOriginPatterns("*") // Crucial for cross-device connections
                .withSockJS(); // Provides a fallback if a browser doesn't support raw WebSockets
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Devices subscribe to "/topic" to listen for updates (e.g., /topic/room/123)
        registry.enableSimpleBroker("/topic");

        // Devices send messages to "/app" to trigger an action (e.g., /app/room/123/play)
        registry.setApplicationDestinationPrefixes("/app");
    }
}