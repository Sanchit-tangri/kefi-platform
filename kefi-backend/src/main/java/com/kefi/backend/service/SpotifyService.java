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

    // Automatically get a server token when the app starts
    @PostConstruct
    public void init() {
        authenticate();
    }

    private void authenticate() {
        try {
            ClientCredentialsRequest request = spotifyApi.clientCredentials().build();
            ClientCredentials credentials = request.execute();
            spotifyApi.setAccessToken(credentials.getAccessToken());
            System.out.println("Successfully authenticated with Spotify (Server App)!");
        } catch (Exception e) {
            System.err.println("Failed to authenticate with Spotify: " + e.getMessage());
        }
    }

    // Existing Search Method
    public List<Track> searchTracks(String query) {
        try {
            SearchTracksRequest searchTracksRequest = spotifyApi.searchTracks(query)
                    .limit(10)
                    .build();

            Paging<se.michaelthelin.spotify.model_objects.specification.Track> trackPaging = searchTracksRequest.execute();

            List<Track> kefiTracks = new ArrayList<>();
            for (se.michaelthelin.spotify.model_objects.specification.Track spotifyTrack : trackPaging.getItems()) {
                Track track = Track.builder()
                        .spotifyTrackId(spotifyTrack.getId())
                        .title(spotifyTrack.getName())
                        .artist(spotifyTrack.getArtists()[0].getName())
                        .durationMs(spotifyTrack.getDurationMs())
                        // Prevent crash if a song has no album art
                        .albumArtUrl(spotifyTrack.getAlbum().getImages().length > 0 ? spotifyTrack.getAlbum().getImages()[0].getUrl() : "")
                        .build();
                kefiTracks.add(track);
            }
            return kefiTracks;

        } catch (Exception e) {
            authenticate();
            throw new RuntimeException("Error searching tracks, trying to re-authenticate. Try your search again.", e);
        }
    }

    // --- NEW METHODS FOR USER LOGIN ---

    public String getAuthorizationUrl() {
        return spotifyApi.authorizationCodeUri()
                .scope("streaming,user-read-email,user-read-private,user-modify-playback-state,user-read-playback-state")
                .show_dialog(true)
                .build()
                .execute()
                .toString();
    }

    public String exchangeCodeForToken(String code) {
        try {
            var credentials = spotifyApi.authorizationCode(code).build().execute();
            spotifyApi.setAccessToken(credentials.getAccessToken());
            spotifyApi.setRefreshToken(credentials.getRefreshToken());
            System.out.println("✅ User Logged In! Access Token Acquired.");

            return credentials.getAccessToken();
        } catch (Exception e) {
            throw new RuntimeException("Failed to exchange Spotify code for token", e);
        }
    }
}