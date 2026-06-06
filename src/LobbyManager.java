import java.util.ArrayList;
import java.util.List;

// This is the lobby manager that attempts to implement a lobby, due to the single machine implementation
// It is not fully implemented to control the logic of entering and creating a user name and password as those are passed by the client and server config files.
// In a future version of the game the 
public class LobbyManager {

    public enum LobbyStatus { WAITING, IN_PROGRESS }

    public static class Lobby {
        public final String name;
        public final String owner;
        public LobbyStatus status;

        public Lobby(String name, String owner) {
            this.name   = name;
            this.owner  = owner;
            this.status = LobbyStatus.WAITING;
        }

        // Serializes this lobby as a compact JSON object to be sent as a connection request for a different client joining (not the host)
        public String toJson() {
            return "{\"name\":\"" + escape(name) + "\""
                 + ",\"owner\":\"" + escape(owner) + "\""
                 + ",\"status\":\"" + status.name() + "\"}";
        }

        private String escape(String s) {
            return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
        }
    }

    // List for lobbies in the future we plan to support multiple but this is just the one
    private final List<Lobby> lobbies = new ArrayList<>();


    // Adding lobby/to list, multiple methods if there is no name/owner (for guests in the future)
    public synchronized Lobby create(String name, String owner) {
        for (Lobby l : lobbies) {
            if (l.name.equalsIgnoreCase(name)) return null; 
        }
        Lobby lobby = new Lobby(name, owner);
        lobbies.add(lobby);
        return lobby;
    }

    public synchronized Lobby find(String name) {
        for (Lobby l : lobbies) {
            if (l.name.equalsIgnoreCase(name)) return l;
        }
        return null;
    }

    //ToString/array
    public synchronized String toJsonArray() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < lobbies.size(); i++) {
            sb.append(lobbies.get(i).toJson());
            if (i < lobbies.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    // Check for if a client has joined a lobby, boolean since it only runs 1 client
    public synchronized boolean isEmpty() {
        return lobbies.isEmpty();
    }

    // Lobby awaiting game start (IDLING, this realistically will be a game state in the future but since game starts on 1 client connecting because
    // both players play in one client not really used that much)
    public synchronized void markInProgress(String name) {
        Lobby l = find(name);
        if (l != null) l.status = LobbyStatus.IN_PROGRESS;
    }
}