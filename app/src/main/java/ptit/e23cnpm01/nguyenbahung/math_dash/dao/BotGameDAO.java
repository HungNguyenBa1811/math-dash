package ptit.e23cnpm01.nguyenbahung.math_dash.dao;

import android.content.ContentValues;
import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Locale;

import ptit.e23cnpm01.nguyenbahung.math_dash.entity.BotGame;

/**
 * Lưu các ván chơi với máy vào bảng tblBotGame của SQLite. Phải gọi trong Thread.
 */
public class BotGameDAO extends DAO {
    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public BotGameDAO(Context context) {
        super(context);
    }

    // Thêm một ván; thành công thì gán id mới cho ván và trả true
    public boolean insert(BotGame game) {
        ContentValues values = new ContentValues();
        values.put("difficulty", game.getDifficulty());
        values.put("correctCount", game.getCorrectCount());
        values.put("finishTime", game.getFinishTime());
        values.put("botCorrectCount", game.getBotCorrectCount());
        values.put("botFinishTime", game.getBotFinishTime());
        values.put("playedAt", new SimpleDateFormat(DATE_PATTERN, Locale.US).format(game.getPlayedAt()));
        values.put("tblPlayerId", game.getPlayer().getId());
        long id = con.insert("tblBotGame", null, values);
        if (id == -1) {
            return false;
        }
        game.setId((int) id);
        return true;
    }
}
