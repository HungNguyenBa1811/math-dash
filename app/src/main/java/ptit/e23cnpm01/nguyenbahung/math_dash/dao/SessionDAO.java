package ptit.e23cnpm01.nguyenbahung.math_dash.dao;

import android.content.Context;
import android.content.SharedPreferences;

import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Player;

/**
 * Phiên đăng nhập lưu trong SharedPreferences "session".
 */
public class SessionDAO {
    private SharedPreferences prefs;

    public SessionDAO(Context context) {
        prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE);
    }

    // Ghi nhớ người chơi vừa đăng nhập
    public void save(String token, Player player) {
        prefs.edit()
                .putString("token", token)
                .putInt("playerId", player.getId())
                .putString("displayName", player.getDisplayName())
                .apply();
    }

    // Đọc phiên và đóng gói thành Player; chưa đăng nhập thì trả null
    public Player getPlayer() {
        if (!prefs.contains("playerId")) {
            return null;
        }
        return new Player(prefs.getInt("playerId", 0), null, prefs.getString("displayName", ""));
    }

    public String getToken() {
        return prefs.getString("token", null);
    }

    // Đăng xuất: xóa toàn bộ phiên
    public void clear() {
        prefs.edit().clear().apply();
    }
}
