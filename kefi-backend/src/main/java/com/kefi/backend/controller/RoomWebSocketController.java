package com.kefi.backend.controller;

import com.kefi.backend.model.PlayerAction;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class RoomWebSocketController {

    // When a user sends a message to: /app/room/{roomId}/action
    @MessageMapping("/room/{roomId}/action")
    // The server instantly broadcasts it to everyone subscribed to: /topic/room/{roomId}
    @SendTo("/topic/room/{roomId}")
    public PlayerAction handlePlayerAction(@DestinationVariable String roomId, @Payload PlayerAction action) {

        // Log it to the console so we can see it working
        System.out.println("\n[LIVE EVENT] Room " + roomId + ": " + action.getTriggeredBy() + " triggered " + action.getAction());
        System.out.println("Track: " + action.getTrackId() + " at " + action.getTimestampMs() + "ms");

        // Return the action so the broker broadcasts it to all listeners in the room
        return action;
    }
}