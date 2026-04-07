package com.kefi.backend.controller;

import com.kefi.backend.service.SpotifyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final SpotifyService spotifyService;

    public AuthController(SpotifyService spotifyService) {
        this.spotifyService = spotifyService;
    }

    @GetMapping("/login-url")
    public ResponseEntity<Map<String, String>> getLoginUrl() {
        String url = spotifyService.getAuthorizationUrl();
        return ResponseEntity.ok(Map.of("url", url));
    }

    @GetMapping("/callback")
    public ResponseEntity<Void> spotifyCallback(@RequestParam("code") String code) {
        String accessToken = spotifyService.exchangeCodeForToken(code);

        // Redirect back to your React app running on port 5173
        String frontendRedirectUrl = "http://localhost:5173/?token=" + accessToken;

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(frontendRedirectUrl))
                .build();
    }
}