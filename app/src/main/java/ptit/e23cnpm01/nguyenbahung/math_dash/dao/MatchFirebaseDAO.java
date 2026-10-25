package ptit.e23cnpm01.nguyenbahung.math_dash.dao;

import com.google.firebase.database.DataSnapshot;

import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Match;

/**
 * Theo dõi trận đang đấu ở node matches/{matchId}. App chỉ đọc, mọi ghi do server làm.
 * Thiết kế: docs/hung/4-lop-thiet-ke.md mục 1.2. Thân các hàm làm ở M2-03, M2-04.
 */
public class MatchFirebaseDAO extends FirebaseDAO {

    public void watchMatch(int matchId, MatchCallback callback) {
        throw new UnsupportedOperationException("TODO M2-03");
    }

    public void stopWatching(int matchId) {
        throw new UnsupportedOperationException("TODO M2-03");
    }

    // Đóng gói node thành Match kèm 10 câu hỏi (không có đáp án) và 2 người chơi
    private Match toMatch(int matchId, DataSnapshot data) {
        throw new UnsupportedOperationException("TODO M2-03");
    }
}
