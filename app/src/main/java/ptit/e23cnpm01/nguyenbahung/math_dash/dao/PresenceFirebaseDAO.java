package ptit.e23cnpm01.nguyenbahung.math_dash.dao;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Player;

// Node presence/{playerId}
public class PresenceFirebaseDAO extends FirebaseDAO {
    private ValueEventListener watcher;

    // The node is removed automatically on disconnect
    public void goOnline(Player player) {
        DatabaseReference ref = con.child("presence").child(String.valueOf(player.getId()));
        ref.onDisconnect().removeValue();
        Map<String, Object> data = new HashMap<>();
        data.put("displayName", player.getDisplayName());
        data.put("totalPoints", player.getTotalPoints());
        data.put("status", Player.FREE);
        ref.setValue(data);
    }

    public void goOffline(int playerId) {
        con.child("presence").child(String.valueOf(playerId)).removeValue();
    }

    // Every online player with total points and status free or playing
    public void watchOnlinePlayers(final PlayerListCallback callback) {
        stopWatching();
        watcher = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Player> players = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Player player = new Player(Integer.parseInt(child.getKey()), null,
                            child.child("displayName").getValue(String.class));
                    Double points = child.child("totalPoints").getValue(Double.class);
                    player.setTotalPoints(points == null ? 0 : points);
                    String status = child.child("status").getValue(String.class);
                    player.setStatus(status == null ? Player.FREE : status);
                    players.add(player);
                }
                callback.onPlayersChanged(players);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Keep the previous list
            }
        };
        con.child("presence").addValueEventListener(watcher);
    }

    public void stopWatching() {
        if (watcher != null) {
            con.child("presence").removeEventListener(watcher);
            watcher = null;
        }
    }
}
