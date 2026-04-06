package com.kefi.backend.service;

import com.kefi.backend.model.Track;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;

import java.util.ArrayList;
import java.util.List;

@Service
public class SpotifyService {

    private final SpotifyApi spotifyApi;

    public SpotifyService(SpotifyApi spotifyApi) {
        this.spotifyApi = spotifyApi;
    }

    // Automatically get an access token when the server starts
    @PostConstruct
    public void init() {
        authenticate();
    }

    private void authenticate() {
        try {
            ClientCredentialsRequest request = spotifyApi.clientCredentials().build();
            ClientCredentials credentials = request.execute();
            spotifyApi.setAccessToken(credentials.getAccessToken());
            System.out.println("Successfully authenticated with Spotify!");
        } catch (Exception e) {
            System.err.println("Failed to authenticate with Spotify: " + e.getMessage());
        }
    }

    public List<Track> searchTracks(String query) {
        try {
            SearchTracksRequest searchTracksRequest = spotifyApi.searchTracks(query)
                    .limit(10) // Let's return top 10 results
                    .build();

            Paging<se.michaelthelin.spotify.model_objects.specification.Track> trackPaging = searchTracksRequest.execute();

            List<Track> kefiTracks = new ArrayList<>();
            for (se.michaelthelin.spotify.model_objects.specification.Track spotifyTrack : trackPaging.getItems()) {
                Track track = Track.builder()
                        .spotifyTrackId(spotifyTrack.getId())
                        .title(spotifyTrack.getName())
                        .artist(spotifyTrack.getArtists()[0].getName()) // Getting the primary artist
                        .durationMs(spotifyTrack.getDurationMs())
                        .albumArtUrl(spotifyTrack.getAlbum().getImages()[0].getUrl())
                        .build();
                kefiTracks.add(track);
            }
            return kefiTracks;

        } catch (Exception e) {
            // If token expired, re-authenticate and try again (basic retry logic)
            authenticate();
            throw new RuntimeException("Error searching tracks, trying to re-authenticate. Try your search again.", e);
        }
    }
}