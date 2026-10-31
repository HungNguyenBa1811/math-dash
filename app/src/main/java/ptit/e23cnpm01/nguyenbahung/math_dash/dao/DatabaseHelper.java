package ptit.e23cnpm01.nguyenbahung.math_dash.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "math_dash.db";
    private static final int DB_VERSION = 4;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE tblPlayer ("
                + "id INTEGER PRIMARY KEY, "
                + "username VARCHAR(255) NOT NULL, "
                + "displayName VARCHAR(255) NOT NULL)");
        db.execSQL("CREATE TABLE tblBotGame ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "difficulty INTEGER NOT NULL CHECK (difficulty BETWEEN 1 AND 3), "
                + "correctCount INTEGER NOT NULL, "
                + "finishTime INTEGER NOT NULL, "
                + "botCorrectCount INTEGER NOT NULL, "
                + "botFinishTime INTEGER NOT NULL, "
                + "playedAt VARCHAR(255) NOT NULL, "
                + "tblPlayerId INTEGER NOT NULL REFERENCES tblPlayer(id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tblGameRecord");
        db.execSQL("DROP TABLE IF EXISTS tblBotGame");
        db.execSQL("DROP TABLE IF EXISTS tblPlayer");
        onCreate(db);
    }
}
