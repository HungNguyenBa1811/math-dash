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

    // POST /api/auth/login: sai tên đăng nhập hoặc mật khẩu thì ném IOException "Sai tên đăng nhập hoặc mật khẩu"
    public Player login(String username, String password) throws IOException, JSONException {
        JSONObject body = new JSONObject();
        body.put("username", username);
        body.put("password", password);
        JSONObject json = post("/auth/login", body);
        return new Player(json.getInt("id"), json.getString("username"), json.getString("displayName"));
    }
}
