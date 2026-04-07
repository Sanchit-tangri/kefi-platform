package com.kefi.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerAction {
    private String roomId;
    private String action;       // e.g., "PLAY", "PAUSE", "SYNC", "NEW_TRACK"
    private String trackId;      // The Spotify ID of the song
    private Integer timestampMs; // Where the scrubber is (e.g., 45000 for 45 seconds)
    private String triggeredBy;  // Username of the person who clicked it
}