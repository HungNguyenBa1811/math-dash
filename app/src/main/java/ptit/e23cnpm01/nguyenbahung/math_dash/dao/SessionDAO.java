package ptit.e23cnpm01.nguyenbahung.math_dash.dao;

import android.content.Context;
import android.content.SharedPreferences;

import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Player;

public class SessionDAO {
    private SharedPreferences prefs;

    public SessionDAO(Context context) {
        prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE);
    }

    public void save(Player player) {
        prefs.edit()
                .putInt("playerId", player.getId())
                .putString("username", player.getUsername())
                .putString("displayName", player.getDisplayName())
                .putString("role", player.getRole())
                .apply();
    }

    // null nếu chưa đăng nhập
    public Player getPlayer() {
        if (!prefs.contains("playerId")) {
            return null;
        }
        Player player = new Player(prefs.getInt("playerId", 0), prefs.getString("username", ""),
                prefs.getString("displayName", ""));
        player.setRole(prefs.getString("role", Player.ROLE_PLAYER));
        return player;
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}
