package ptit.e23cnpm01.nguyenbahung.math_dash.dao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Friendship;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Player;

/**
 * Gọi API quan hệ bạn bè. Các hàm chạy đồng bộ nên phải gọi trong Thread.
 */
public class FriendshipApiDAO extends ApiDAO {

    // GET /api/friendships?playerId={id}: lời mời và bạn bè của người chơi
    public List<Friendship> getFriendships(int playerId) throws IOException, JSONException {
        JSONArray array = getArray("/friendships?playerId=" + playerId);
        List<Friendship> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            list.add(toFriendship(array.getJSONObject(i)));
        }
        return list;
    }

    // POST /api/friendships: gửi lời mời kết bạn tới người có tên đăng nhập receiverUsername
    public Friendship sendRequest(int senderId, String receiverUsername) throws IOException, JSONException {
        JSONObject body = new JSONObject();
        body.put("senderId", String.valueOf(senderId));
        body.put("receiverUsername", receiverUsername);
        return toFriendship(post("/friendships", body));
    }

    // POST /api/friendships/{id}/accept?playerId={id}: người nhận đồng ý lời mời
    public Friendship accept(int friendshipId, int playerId) throws IOException, JSONException {
        return toFriendship(post("/friendships/" + friendshipId + "/accept?playerId=" + playerId, new JSONObject()));
    }

    // DELETE /api/friendships/{id}?playerId={id}: từ chối, rút lời mời hoặc hủy kết bạn
    public void delete(int friendshipId, int playerId) throws IOException, JSONException {
        delete("/friendships/" + friendshipId + "?playerId=" + playerId);
    }

    // Đóng gói JSON thành Friendship kèm người gửi, người nhận
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
