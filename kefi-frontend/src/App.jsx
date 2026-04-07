import { useEffect, useState } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import './index.css';

function App() {
  const [stompClient, setStompClient] = useState(null);
  const [spotifyToken, setSpotifyToken] = useState(null); // NEW: Track login state

  const currentRoomId = "123";
  const myUsername = "Sanchit"; // Hardcoded for now, we'll make this dynamic later

  // NEW: Check the URL for the Spotify Token when the app loads
  useEffect(() => {
    const urlParams = new URLSearchParams(window.location.search);
    const token = urlParams.get('token');

    if (token) {
      console.log("🔑 Caught Spotify Token from URL!");
      setSpotifyToken(token);
      // Clean up the URL bar so the massive token isn't sitting there
      window.history.replaceState({}, document.title, "/");
    }
  }, []);

  // Existing WebSocket Connection Logic
  useEffect(() => {
    const socket = new SockJS('http://localhost:8080/ws-kefi');
    const client = new Client({
      webSocketFactory: () => socket,
      debug: (str) => console.log(str),
      onConnect: () => {
        console.log('✅ Connected to Kefi WebSocket Server');
        client.subscribe(`/topic/room/${currentRoomId}`, (message) => {
          const action = JSON.parse(message.body);
          console.log("📻 LIVE UPDATE RECEIVED: ", action);
          alert(`Live Sync: ${action.triggeredBy} hit ${action.action}!`);
        });
      },
      onStompError: (frame) => {
        console.error('Broker reported error: ' + frame.headers['message']);
      },
    });

    client.activate();
    setStompClient(client);

    return () => {
      client.deactivate();
    };
  }, []);

  // NEW: Handle the Spotify Connect Button Click
  const handleSpotifyConnect = async () => {
    try {
      // 1. Ask our Spring Boot backend for the secure Spotify Login URL
      const response = await fetch('http://localhost:8080/api/auth/login-url');
      const data = await response.json();

      // 2. Redirect the user's browser to that Spotify page
      window.location.href = data.url;
    } catch (error) {
      console.error("Error fetching login URL:", error);
      alert("Could not connect to the Kefi backend server. Is it running?");
    }
  };

  const handlePlayClick = () => {
    if (stompClient && stompClient.connected) {
      const playerAction = {
        roomId: currentRoomId,
        action: "PLAY",
        trackId: "spotify:track:7qiZfU4dY1lWllzX7mPBI3",
        timestampMs: 0,
        triggeredBy: myUsername
      };
      stompClient.publish({
        destination: `/app/room/${currentRoomId}/action`,
        body: JSON.stringify(playerAction)
      });
    } else {
      alert("WebSocket not connected yet!");
    }
  };

  return (
    <div className="app-container">
      {/* Sidebar */}
      <nav className="sidebar">
        <div className="room-info">
          <h2><i className="fas fa-fire"></i> Midnight Synth-Pop</h2>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.8rem' }}>3 Listeners • Live</p>
        </div>

        {/* NEW: Conditional UI for the Spotify Button */}
        {!spotifyToken ? (
          <button className="spotify-connect" onClick={handleSpotifyConnect}>
            <i className="fab fa-spotify"></i> Connect Spotify
          </button>
        ) : (
          <div style={{ color: 'var(--spotify-green)', fontWeight: 'bold', display: 'flex', alignItems: 'center', gap: '10px', padding: '10px 15px', border: '1px solid var(--spotify-green)', borderRadius: '20px' }}>
            <i className="fab fa-spotify"></i> Connected
          </div>
        )}

        <div className="members-section">
          <h3>Session Members</h3>
          <ul className="member-list">
            <li className="member">
              <div className="member-info"><div className="avatar">S</div><span>Sanchit</span></div>
              <span className="role-badge role-host">Host</span>
            </li>
          </ul>
        </div>
      </nav>

      {/* Main Content */}
      <main className="main-content">
        <div className="search-bar">
          <i className="fas fa-search"></i>
          <input type="text" placeholder="Search for songs..." />
        </div>
        <h2 className="section-title">Suggested for the Vibe</h2>
        <div className="song-list">
          <div className="song-item">
            <img src="https://via.placeholder.com/45/1e1e2d/ffffff?text=NP" alt="Album Art" className="song-art" />
            <div className="song-details">
              <div className="song-title">Starboy</div>
              <div className="song-artist">The Weeknd, Daft Punk</div>
            </div>
            <button className="add-to-queue-btn"><i className="fas fa-plus"></i> Add to Queue</button>
          </div>
        </div>
      </main>

      {/* Right Sidebar Queue */}
      <aside className="queue-panel">
        <div className="queue-header">
          <h3>Up Next</h3>
          <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>4 tracks</span>
        </div>
        <div className="host-controls">
          <button className="ctrl-btn"><i className="fas fa-lock"></i> Lock Queue</button>
        </div>
        <div className="song-list" style={{ marginTop: '10px' }}>
          <div className="song-item" style={{ background: 'rgba(157, 78, 221, 0.1)', border: '1px solid rgba(157, 78, 221, 0.3)' }}>
            <img src="https://via.placeholder.com/45" alt="Album Art" className="song-art" />
            <div className="song-details">
              <div className="song-title" style={{ color: 'var(--accent-primary)' }}>Blinding Lights</div>
              <div className="song-artist">Added by Sanchit</div>
            </div>
            <i className="fas fa-grip-lines" style={{ color: 'var(--text-muted)', cursor: 'grab' }}></i>
          </div>
        </div>
      </aside>

      {/* Bottom Player */}
      <footer className="player" style={{ position: 'fixed', bottom: 0, width: '100%' }}>
        <div className="now-playing">
          <img src="https://via.placeholder.com/56" alt="Now Playing" style={{ borderRadius: '4px', marginRight: '15px' }} />
          <div>
            <div style={{ fontWeight: '600', fontSize: '0.95rem' }}>Midnight City</div>
            <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>M83</div>
          </div>
        </div>
        <div className="player-controls">
          <div className="control-buttons">
            <i className="fas fa-step-backward"></i>
            <div className="play-btn" onClick={handlePlayClick} style={{ cursor: 'pointer' }}>
              <i className="fas fa-play" style={{ color: 'black', marginLeft: '3px' }}></i>
            </div>
            <i className="fas fa-step-forward"></i>
          </div>
          <div className="progress-container">
            <span>1:24</span>
            <div className="progress-bar"><div className="progress-fill"></div></div>
            <span>4:03</span>
          </div>
        </div>
      </footer>
    </div>
  );
}

export default App;