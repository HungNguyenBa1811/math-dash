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
                .putString("displayName", player.getDisplayName())
                .apply();
    }

    public Player getPlayer() {
        if (!prefs.contains("playerId")) {
            return null;
        }
        return new Player(prefs.getInt("playerId", 0), null, prefs.getString("displayName", ""));
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}
