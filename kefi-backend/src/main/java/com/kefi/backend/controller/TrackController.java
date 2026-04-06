package com.kefi.backend.controller;

import com.kefi.backend.model.Track;
import com.kefi.backend.service.SpotifyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tracks")
@CrossOrigin(origins = "*") // Allows your frontend to call this API later
public class TrackController {

    private final SpotifyService spotifyService;

    public TrackController(SpotifyService spotifyService) {
        this.spotifyService = spotifyService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<Track>> searchMusic(@RequestParam String q) {
        List<Track> results = spotifyService.searchTracks(q);
        return ResponseEntity.ok(results);
    }
}