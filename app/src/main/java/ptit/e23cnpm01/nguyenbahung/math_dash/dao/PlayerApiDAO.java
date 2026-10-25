package ptit.e23cnpm01.nguyenbahung.math_dash.dao;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Player;

/**
 * Gọi API tài khoản và người chơi. Các hàm chạy đồng bộ nên phải gọi trong Thread.
 */
public class PlayerApiDAO extends ApiDAO {

    // POST /api/auth/register
    public void register(String username, String password, String displayName) throws IOException, JSONException {
        JSONObject body = new JSONObject();
        body.put("username", username);
        body.put("password", password);
        body.put("displayName", displayName);
        post("/auth/register", body);
    }

    // POST /api/auth/login: trả người chơi kèm vai trò
    public Player login(String username, String password) throws IOException, JSONException {
        JSONObject body = new JSONObject();
        body.put("username", username);
        body.put("password", password);
        return toPlayer(post("/auth/login", body));
    }

    // GET /api/players/{id}: người chơi kèm tổng điểm hiện có
    public Player getById(int id) throws IOException, JSONException {
        return toPlayer(get("/players/" + id));
    }

    // Đóng gói JSON thành Player; các trường dẫn xuất có thể vắng
    private Player toPlayer(JSONObject json) throws JSONException {
        Player player = new Player(json.getInt("id"), json.optString("username"), json.getString("displayName"));
        player.setRole(json.optString("role", Player.ROLE_PLAYER));
        player.setTotalPoints(json.optDouble("totalPoints", 0));
        player.setAvgOpponentPoints(json.optDouble("avgOpponentPoints", 0));
        player.setAvgWinTime(json.optDouble("avgWinTime", 0));
        return player;
    }
}
