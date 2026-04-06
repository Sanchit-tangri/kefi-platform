package com.kefi.backend.repository;

import com.kefi.backend.model.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrackRepository extends JpaRepository<Track, Long> {
    Optional<Track> findBySpotifyTrackId(String spotifyTrackId);
}