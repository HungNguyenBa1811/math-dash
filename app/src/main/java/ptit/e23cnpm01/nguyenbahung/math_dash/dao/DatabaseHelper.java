package ptit.e23cnpm01.nguyenbahung.math_dash.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Tạo CSDL SQLite của app: tblPlayer (bản sao người chơi), tblGameRecord.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "math_dash.db";
    private static final int DB_VERSION = 2;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // Bật khóa ngoại để tblGameRecord.tblPlayerId trỏ đúng tới tblPlayer
    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE tblPlayer ("
                + "id INTEGER PRIMARY KEY, "
                + "username TEXT NOT NULL, "
                + "displayName TEXT NOT NULL)");
        db.execSQL("CREATE TABLE tblGameRecord ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "difficulty INTEGER NOT NULL CHECK (difficulty BETWEEN 1 AND 3), "
                + "score INTEGER NOT NULL, "
                + "maxStreak INTEGER NOT NULL, "
                + "correctCount INTEGER NOT NULL, "
                + "wrongCount INTEGER NOT NULL, "
                + "playedAt TEXT NOT NULL, "
                + "tblPlayerId INTEGER NOT NULL REFERENCES tblPlayer(id))");
        db.execSQL("CREATE INDEX idx_gamerecord_best ON tblGameRecord (tblPlayerId, difficulty, score)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tblGameRecord");
        db.execSQL("DROP TABLE IF EXISTS tblPlayer");
        onCreate(db);
    }
}
