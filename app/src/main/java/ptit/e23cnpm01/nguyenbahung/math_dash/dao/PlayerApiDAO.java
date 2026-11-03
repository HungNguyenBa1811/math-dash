package ptit.e23cnpm01.nguyenbahung.math_dash.dao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Player;

public class PlayerApiDAO extends ApiDAO {

    public void register(String username, String password, String displayName) throws IOException, JSONException {
        JSONObject body = new JSONObject();
        body.put("username", username);
        body.put("password", password);
        body.put("displayName", displayName);
        request("POST", "/auth/register", body);
    }

    public Player login(String username, String password) throws IOException, JSONException {
        JSONObject body = new JSONObject();
        body.put("username", username);
        body.put("password", password);
        return toPlayer(new JSONObject(request("POST", "/auth/login", body)));
    }

    public Player getById(int id) throws IOException, JSONException {
        return toPlayer(new JSONObject(request("GET", "/players/" + id, null)));
    }

    public List<Player> getRanking() throws IOException, JSONException {
        JSONArray array = new JSONArray(request("GET", "/players/ranking", null));
        List<Player> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            list.add(toPlayer(array.getJSONObject(i)));
        }
        return list;
    }

    // mapper
    private Player toPlayer(JSONObject json) throws JSONException {
        Player player = new Player(json.getInt("id"), json.optString("username"), json.getString("displayName"));
        player.setRole(json.optString("role", Player.ROLE_PLAYER));
        player.setTotalPoints(json.optDouble("totalPoints", 0));
        player.setAvgOpponentPoints(json.optDouble("avgOpponentPoints", 0));
        player.setAvgWinTime(json.optDouble("avgWinTime", 0));
        return player;
    }
}
