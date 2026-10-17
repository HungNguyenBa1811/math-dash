package ptit.e23cnpm01.nguyenbahung.math_dash.dao;

import android.content.Context;

import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Player;

/**
 * Thao tác SQLite trên bảng players.
 */
public class PlayerDAO {

    public PlayerDAO(Context context) {
        // TODO
    }

    // Tìm người chơi theo tên, chưa có thì tạo mới
    public Player findOrCreateByUsername(String username) {
        // TODO
        return null;
    }

    // Cập nhật kỷ lục nếu điểm mới cao hơn, trả về true nếu là kỷ lục mới
    public boolean updateHighScoreIfHigher(int playerId, int score) {
        // TODO
        return false;
    }
}
