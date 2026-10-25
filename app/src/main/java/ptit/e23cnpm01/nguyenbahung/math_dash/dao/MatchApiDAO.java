package ptit.e23cnpm01.nguyenbahung.math_dash.dao;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Challenge;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Match;

/**
 * Gọi API trận đấu, mỗi hàm một API. Các hàm chạy đồng bộ nên phải gọi trong Thread.
 * Thiết kế: docs/hung/4-lop-thiet-ke.md mục 1.2. Thân các hàm làm ở M2-02 → M2-04.
 */
public class MatchApiDAO extends ApiDAO {

    // POST /api/matches: tạo trận từ lời mời đã đồng ý
    public Match createMatch(Challenge challenge) throws IOException, JSONException {
        throw new UnsupportedOperationException("TODO M2-02");
    }

    // POST /api/matches/{id}/submit: nộp bài làm
    public Match submit(int matchId, int playerId, List<Integer> answers) throws IOException, JSONException {
        throw new UnsupportedOperationException("TODO M2-03");
    }

    // POST /api/matches/{id}/forfeit: bỏ trận
    public Match forfeit(int matchId, int playerId) throws IOException, JSONException {
        throw new UnsupportedOperationException("TODO M2-04");
    }

    // POST /api/matches/{id}/continue: chơi tiếp (true) hoặc dừng (false)
    public Match continueMatch(int matchId, int playerId, boolean choice) throws IOException, JSONException {
        throw new UnsupportedOperationException("TODO M2-04");
    }

    // GET /api/matches/{id}: trận đầy đủ, có đáp án khi trận đã xong
    public Match getById(int matchId) throws IOException, JSONException {
        throw new UnsupportedOperationException("TODO M2-03");
    }

    // Đóng gói JSON thành Match + MathProblem + 2 MatchPlayer + 2 Player
    private Match toMatch(JSONObject json) throws JSONException {
        throw new UnsupportedOperationException("TODO M2-02");
    }
}
