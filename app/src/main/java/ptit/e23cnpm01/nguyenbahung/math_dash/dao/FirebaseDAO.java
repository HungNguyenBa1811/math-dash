package ptit.e23cnpm01.nguyenbahung.math_dash.dao;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Lớp cha của mọi DAO Firebase: giữ gốc Realtime Database.
 */
public abstract class FirebaseDAO {
    protected static DatabaseReference con;

    public FirebaseDAO() {
        if (con == null) {
            con = FirebaseDatabase.getInstance().getReference();
        }
    }
}
