package ptit.e23cnpm01.nguyenbahung.math_dash.dao;

import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Match;

/**
 * Kết quả bất đồng bộ khi theo dõi trận trên Firebase.
 */
public interface MatchCallback {
    // Trận thay đổi: có đề, đối thủ nộp bài, trận kết thúc
    void onSuccess(Match match);

    // Cả 2 chọn chơi tiếp: server đã tạo trận mới
    void onNextMatch(int nextMatchId);

    // Một người dừng chơi hoặc bỏ trận
    void onStopped(int playerId);

    void onFail(String message);
}
