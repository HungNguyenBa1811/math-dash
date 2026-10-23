package ptit.e23cnpm01.nguyenbahung.math_dash.dao;

import android.content.ContentValues;
import android.content.Context;

import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Player;

/**
 * Bản sao người chơi trong SQLite, để chơi offline khi mất mạng.
 */
public class PlayerDAO extends DAO {

    public PlayerDAO(Context context) {
        super(context);
    }

    // Lưu bản sao người chơi vừa đăng nhập (cùng id với MySQL), đã có thì cập nhật
    // Không dùng REPLACE vì REPLACE xóa dòng cũ, vướng khóa ngoại của tblGameRecord
    public void saveOrUpdate(Player player) {
        ContentValues values = new ContentValues();
        values.put("username", player.getUsername());
        values.put("displayName", player.getDisplayName());
        String[] args = {String.valueOf(player.getId())};
        if (con.update("tblPlayer", values, "id = ?", args) == 0) {
            values.put("id", player.getId());
            con.insert("tblPlayer", null, values);
        }
    }
}
