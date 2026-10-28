package ptit.e23cnpm01.nguyenbahung.math_dash.dao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Player;

// Hàm đồng bộ, phải gọi trong Thread
public class PlayerApiDAO extends ApiDAO {

    public void register(String username, String password, String displayName) throws IOException, JSONException {
        JSONObject body = new JSONObject();
        body.put("username", username);
        body.put("password", password);
        body.put("displayName", displayName);
        post("/auth/register", body);
    }

    public Player login(String username, String password) throws IOException, JSONException {
        JSONObject body = new JSONObject();
        body.put("username", username);
        body.put("password", password);
        return toPlayer(post("/auth/login", body));
    }

    public Player getById(int id) throws IOException, JSONException {
        return toPlayer(get("/players/" + id));
    }

    public List<Player> getRanking() throws IOException, JSONException {
        JSONArray array = getArray("/players/ranking");
        List<Player> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            list.add(toPlayer(array.getJSONObject(i)));
        }
        return list;
    }

    // Các trường dẫn xuất có thể vắng
    private Player toPlayer(JSONObject json) throws JSONException {
        Player player = new Player(json.getInt("id"), json.optString("username"), json.getString("displayName"));
        player.setRole(json.optString("role", Player.ROLE_PLAYER));
        player.setTotalPoints(json.optDouble("totalPoints", 0));
        player.setAvgOpponentPoints(json.optDouble("avgOpponentPoints", 0));
        player.setAvgWinTime(json.optDouble("avgWinTime", 0));
        return player;
    }
}
