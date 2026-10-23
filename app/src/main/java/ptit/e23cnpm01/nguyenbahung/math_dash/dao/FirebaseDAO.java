package ptit.e23cnpm01.nguyenbahung.math_dash.dao;

import android.util.Log;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.ExecutionException;

/**
 * Lớp cha của mọi DAO Firebase: giữ gốc Realtime Database và FirebaseAuth.
 */
public abstract class FirebaseDAO {
    protected static DatabaseReference con;
    protected static FirebaseAuth auth;

    public FirebaseDAO() {
        if (con == null) {
            con = FirebaseDatabase.getInstance().getReference();
            auth = FirebaseAuth.getInstance();
        }
    }

    // Đăng nhập Firebase bằng custom token server cấp, khi đó auth.uid = playerId. Gọi trong Thread
    public static void signIn(String firebaseToken) throws ExecutionException, InterruptedException {
        Tasks.await(FirebaseAuth.getInstance().signInWithCustomToken(firebaseToken));
        Log.d("FirebaseDAO", "Firebase uid = " + FirebaseAuth.getInstance().getUid());
    }

    public static void signOut() {
        FirebaseAuth.getInstance().signOut();
    }
}
