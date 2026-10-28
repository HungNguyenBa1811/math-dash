package ptit.e23cnpm01.nguyenbahung.math_dash.dao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Friendship;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Player;

// Hàm đồng bộ, phải gọi trong Thread
public class FriendshipApiDAO extends ApiDAO {

    public List<Friendship> getFriendships(int playerId) throws IOException, JSONException {
        JSONArray array = getArray("/friendships?playerId=" + playerId);
        List<Friendship> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            list.add(toFriendship(array.getJSONObject(i)));
        }
        return list;
    }

    public Friendship sendRequest(int senderId, String receiverUsername) throws IOException, JSONException {
        JSONObject body = new JSONObject();
        body.put("senderId", String.valueOf(senderId));
        body.put("receiverUsername", receiverUsername);
        return toFriendship(post("/friendships", body));
    }

    public Friendship accept(int friendshipId, int playerId) throws IOException, JSONException {
        return toFriendship(post("/friendships/" + friendshipId + "/accept?playerId=" + playerId, new JSONObject()));
    }

    public void delete(int friendshipId, int playerId) throws IOException, JSONException {
        delete("/friendships/" + friendshipId + "?playerId=" + playerId);
    }

    private Friendship toFriendship(JSONObject json) throws JSONException {
        Friendship friendship = new Friendship(toPlayer(json.getJSONObject("sender")), toPlayer(json.getJSONObject("receiver")));
        friendship.setId(json.getInt("id"));
        friendship.setStatus(json.optString("status", Friendship.PENDING));
        friendship.setCreatedAt(json.isNull("createdAt") ? null : new Date(json.optLong("createdAt")));
        return friendship;
    }

    private Player toPlayer(JSONObject json) throws JSONException {
        Player player = new Player(json.getInt("id"), json.optString("username"), json.getString("displayName"));
        player.setTotalPoints(json.optDouble("totalPoints", 0));
        return player;
    }
}
