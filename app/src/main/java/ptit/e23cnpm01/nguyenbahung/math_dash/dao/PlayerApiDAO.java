package ptit.e23cnpm01.nguyenbahung.math_dash.dao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    // GET /api/players/{id}: người chơi kèm tổng điểm và số trận thắng / hòa / thua
    public Player getById(int id) throws IOException, JSONException {
        return toPlayer(get("/players/" + id));
    }

    // GET /api/players/ranking: mọi người chơi kèm 3 tiêu chí, đã xếp hạng
    public List<Player> getRanking() throws IOException, JSONException {
        return toPlayers(getArray("/players/ranking"));
    }

    // ---------- Quản trị (UC06, Hoàng) ----------

    // GET /api/players?name=: người chơi có tên hiển thị chứa name, kèm tổng điểm
    public List<Player> search(String name) throws IOException, JSONException {
        return toPlayers(getArray("/players?name=" + URLEncoder.encode(name, "UTF-8")));
    }

    // PUT /api/players/{id}: sửa tên hiển thị
    public void update(int id, String displayName) throws IOException, JSONException {
        JSONObject body = new JSONObject();
        body.put("displayName", displayName);
        put("/players/" + id, body);
    }

    // PUT /api/players/{id}/password: đặt lại mật khẩu, server băm BCrypt trước khi lưu
    public void resetPassword(int id, String password) throws IOException, JSONException {
        JSONObject body = new JSONObject();
        body.put("password", password);
        put("/players/" + id + "/password", body);
    }

    // DELETE /api/players/{id}: người chơi đã có trận thì server trả 409, ném IOException mang câu thông báo
    public void delete(int id) throws IOException, JSONException {
        delete("/players/" + id);
    }

    // ---------- Đóng gói ----------

    private List<Player> toPlayers(JSONArray array) throws JSONException {
        List<Player> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            list.add(toPlayer(array.getJSONObject(i)));
        }
        return list;
    }

    // Đóng gói JSON thành Player; các trường dẫn xuất có thể vắng
    private Player toPlayer(JSONObject json) throws JSONException {
        Player player = new Player(json.getInt("id"), json.optString("username"), json.getString("displayName"));
        player.setRole(json.optString("role", Player.ROLE_PLAYER));
        player.setCreatedAt(json.isNull("createdAt") ? null : new Date(json.getLong("createdAt")));
        player.setTotalPoints(json.optDouble("totalPoints", 0));
        player.setAvgOpponentPoints(json.optDouble("avgOpponentPoints", 0));
        player.setAvgWinTime(json.optDouble("avgWinTime", 0));
        player.setWinCount(json.optInt("winCount", 0));
        player.setDrawCount(json.optInt("drawCount", 0));
        player.setLoseCount(json.optInt("loseCount", 0));
        return player;
    }
}
