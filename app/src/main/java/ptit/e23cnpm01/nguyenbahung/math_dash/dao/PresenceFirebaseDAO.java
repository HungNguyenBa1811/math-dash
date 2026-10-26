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

import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Player;

/**
 * Danh sách người chơi online ở node presence/{playerId}: tên, tổng điểm, trạng thái rỗi / bận.
 */
public class PresenceFirebaseDAO extends FirebaseDAO {
    private ValueEventListener watcher;

    // Ghi mình vào danh sách online với trạng thái rỗi; app tắt hoặc mất mạng thì node tự bị xóa
    public void goOnline(Player player) {
        DatabaseReference ref = presenceRef().child(String.valueOf(player.getId()));
        ref.onDisconnect().removeValue();
        Map<String, Object> data = new HashMap<>();
        data.put("displayName", player.getDisplayName());
        data.put("totalPoints", player.getTotalPoints());
        data.put("status", Player.FREE);
        ref.setValue(data);
    }

    // Đăng xuất: rời danh sách online
    public void goOffline(int playerId) {
        presenceRef().child(String.valueOf(playerId)).removeValue();
    }

    public void watchOnlinePlayers(final PlayerListCallback callback) {
        stopWatching();
        watcher = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Player> players = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    players.add(toPlayer(child.getKey(), child));
                }
                callback.onPlayersChanged(players);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Mất quyền đọc hoặc mất kết nối: giữ danh sách cũ
            }
        };
        presenceRef().addValueEventListener(watcher);
    }

    public void stopWatching() {
        if (watcher != null) {
            presenceRef().removeEventListener(watcher);
            watcher = null;
        }
    }

    // Đóng gói một node presence thành Player
    private Player toPlayer(String id, DataSnapshot data) {
        Player player = new Player(Integer.parseInt(id), null, data.child("displayName").getValue(String.class));
        Number points = data.child("totalPoints").getValue(Double.class);
        player.setTotalPoints(points == null ? 0 : points.doubleValue());
        String status = data.child("status").getValue(String.class);
        player.setStatus(status == null ? Player.FREE : status);
        return player;
    }

    private DatabaseReference presenceRef() {
        return con.child("presence");
    }
}
