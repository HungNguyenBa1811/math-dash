package ptit.e23cnpm01.nguyenbahung.math_dash.dao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Match;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.MatchPlayer;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Player;

/**
 * Gọi API trận đấu. Các hàm chạy đồng bộ nên phải gọi trong Thread.
 */
public class MatchApiDAO extends ApiDAO {

    // POST /api/matches/{roomCode}/finish: server kiểm tra, lưu trận và trả trận đã lưu
    public Match finishMatch(String roomCode) throws IOException, JSONException {
        return toMatch(post("/matches/" + roomCode + "/finish", new JSONObject()));
    }

    // GET /api/matches?playerId={id}: các trận đã kết thúc của người chơi, mới nhất trước
    public List<Match> getHistory(int playerId) throws IOException, JSONException {
        JSONArray array = getArray("/matches?playerId=" + playerId);
        List<Match> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            list.add(toMatch(array.getJSONObject(i)));
        }
        return list;
    }

    // Đóng gói JSON thành Match + 2 MatchPlayer + 2 Player
    private Match toMatch(JSONObject json) throws JSONException {
        Match match = new Match();
        match.setId(json.getInt("id"));
        match.setRoomCode(json.getString("roomCode"));
        match.setStatus(json.getString("status"));
        match.setQuestionSeed(json.getLong("questionSeed"));
        match.setCreatedAt(new Date(json.getLong("createdAt")));
        match.setEndedAt(new Date(json.getLong("endedAt")));
        JSONArray players = json.getJSONArray("players");
        for (int i = 0; i < players.length(); i++) {
            JSONObject item = players.getJSONObject(i);
            JSONObject p = item.getJSONObject("player");
            MatchPlayer mp = new MatchPlayer(new Player(p.getInt("id"), p.optString("username"), p.getString("displayName")));
            mp.setId(item.getInt("id"));
            mp.setScore(item.getInt("score"));
            mp.setForfeited(item.getBoolean("forfeited"));
            mp.setTimeUp(item.getBoolean("timeUp"));
            match.getPlayers().add(mp);
        }
        return match;
    }
}
