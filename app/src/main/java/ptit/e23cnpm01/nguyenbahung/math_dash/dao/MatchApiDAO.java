package ptit.e23cnpm01.nguyenbahung.math_dash.dao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Challenge;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Match;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.MatchPlayer;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.MathProblem;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Player;

/**
 * Gọi API trận đấu, mỗi hàm một API. Các hàm chạy đồng bộ nên phải gọi trong Thread.
 */
public class MatchApiDAO extends ApiDAO {

    // POST /api/matches: tạo trận từ lời mời đã đồng ý
    public Match createMatch(Challenge challenge) throws IOException, JSONException {
        JSONObject body = new JSONObject();
        body.put("id", challenge.getId());
        body.put("challenger", new JSONObject().put("id", challenge.getChallenger().getId()));
        body.put("opponent", new JSONObject().put("id", challenge.getOpponent().getId()));
        return toMatch(post("/matches", body));
    }

    // POST /api/matches/{id}/submit: nộp bài làm
    public Match submit(int matchId, int playerId, List<Integer> answers) throws IOException, JSONException {
        JSONObject body = new JSONObject();
        body.put("player", new JSONObject().put("id", playerId));
        body.put("answers", new JSONArray(answers));
        return toMatch(post("/matches/" + matchId + "/submit", body));
    }

    // POST /api/matches/{id}/forfeit: bỏ trận
    public Match forfeit(int matchId, int playerId) throws IOException, JSONException {
        return toMatch(post("/matches/" + matchId + "/forfeit?playerId=" + playerId, new JSONObject()));
    }

    // POST /api/matches/{id}/continue: chơi tiếp (true) hoặc dừng (false)
    public Match continueMatch(int matchId, int playerId, boolean choice) throws IOException, JSONException {
        String path = "/matches/" + matchId + "/continue?playerId=" + playerId + "&choice=" + choice;
        return toMatch(post(path, new JSONObject()));
    }

    // GET /api/matches/{id}: trận đầy đủ, có đáp án khi trận đã xong
    public Match getById(int matchId) throws IOException, JSONException {
        return toMatch(get("/matches/" + matchId));
    }

    // Đóng gói JSON thành Match + MathProblem + 2 MatchPlayer + 2 Player
    private Match toMatch(JSONObject json) throws JSONException {
        Match match = new Match();
        match.setId(json.getInt("id"));
        match.setStatus(json.optString("status", Match.PLAYING));
        match.setStartedAt(new Date(json.getLong("startedAt")));
        match.setEndedAt(json.isNull("endedAt") ? null : new Date(json.getLong("endedAt")));
        JSONArray questions = json.getJSONArray("questions");
        for (int i = 0; i < questions.length(); i++) {
            match.getQuestions().add(toProblem(questions.getJSONObject(i)));
        }
        JSONArray players = json.getJSONArray("players");
        for (int i = 0; i < players.length(); i++) {
            match.getPlayers().add(toMatchPlayer(players.getJSONObject(i)));
        }
        return match;
    }

    private MathProblem toProblem(JSONObject json) throws JSONException {
        List<Integer> options = new ArrayList<>();
        JSONArray array = json.getJSONArray("answerOptions");
        for (int i = 0; i < array.length(); i++) {
            options.add(array.getInt(i));
        }
        MathProblem p = new MathProblem(json.getInt("operand1"), json.getInt("operand2"),
                json.getString("operator"), json.getInt("correctAnswer"), options);
        p.setId(json.getInt("id"));
        return p;
    }

    private MatchPlayer toMatchPlayer(JSONObject json) throws JSONException {
        JSONObject p = json.getJSONObject("player");
        Player player = new Player(p.getInt("id"), p.optString("username"), p.getString("displayName"));
        player.setTotalPoints(p.optDouble("totalPoints", 0));
        MatchPlayer mp = new MatchPlayer(player);
        mp.setId(json.getInt("id"));
        mp.setCorrectCount(json.getInt("correctCount"));
        mp.setSubmittedAt(json.isNull("submittedAt") ? null : new Date(json.getLong("submittedAt")));
        mp.setForfeited(json.getBoolean("forfeited"));
        return mp;
    }
}
