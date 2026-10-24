package ptit.e23cnpm01.nguyenbahung.math_dash.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public abstract class DAO {
    protected static SQLiteDatabase con;

    public DAO(Context context) {
        if (con == null) {
            con = new DatabaseHelper(context.getApplicationContext()).getWritableDatabase();
        }
    }
}
