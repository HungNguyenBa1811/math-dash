package ptit.e23cnpm01.nguyenbahung.math_dash.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Quản lý file database SQLite và tạo bảng.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "math_dash.db";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO: tạo các bảng
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO
    }
}
