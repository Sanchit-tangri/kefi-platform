package com.kefi.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/system")
public class PingController {

    @GetMapping("/ping")
    public Map<String, String> pingServer() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("message", "Kefi Backend is live and reachable!");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return response;
    }
}