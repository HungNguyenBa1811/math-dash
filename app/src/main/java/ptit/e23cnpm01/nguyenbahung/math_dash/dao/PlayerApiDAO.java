package ptit.e23cnpm01.nguyenbahung.math_dash.dao;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Player;

/**
 * Gọi API tài khoản: đăng ký, đăng nhập.
 */
public class PlayerApiDAO extends ApiDAO {

    // POST /api/auth/register; trùng tên đăng nhập thì ném IOException "Tên đăng nhập đã được dùng"
    public void register(String username, String password, String displayName) throws IOException, JSONException {
        JSONObject body = new JSONObject();
        body.put("username", username);
        body.put("password", password);
        body.put("displayName", displayName);
        post("/auth/register", body);
    }

    // POST /api/auth/login: giữ token JWT cho các lần gọi sau
    public Player login(String username, String password) throws IOException, JSONException {
        JSONObject body = new JSONObject();
        body.put("username", username);
        body.put("password", password);
        JSONObject result = post("/auth/login", body);
        token = result.getString("token");
        JSONObject json = result.getJSONObject("player");
        return new Player(json.getInt("id"), json.getString("username"), json.getString("displayName"));
    }
}
