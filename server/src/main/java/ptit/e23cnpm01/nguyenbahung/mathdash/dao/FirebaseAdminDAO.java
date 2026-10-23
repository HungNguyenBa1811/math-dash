package ptit.e23cnpm01.nguyenbahung.mathdash.dao;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Match;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.MatchPlayer;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Player;

/**
 * Đọc / ghi trận đang diễn ra trên Firebase bằng Admin SDK (quyền server, không bị Security Rules chặn).
 * Không dùng JDBC nên không kế thừa DAO.
 */
public class FirebaseAdminDAO {
    private FirebaseDatabase db;

    public FirebaseAdminDAO() {
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(loadOptions());
        }
        db = FirebaseDatabase.getInstance();
    }

    // Tạo custom token để app đăng nhập Firebase, khi đó auth.uid = playerId
    public String createCustomToken(int playerId) throws FirebaseAuthException {
        return FirebaseAuth.getInstance().createCustomToken(String.valueOf(playerId));
    }

    // Đọc node matches/{roomCode} và đóng gói thành Match; không có trận thì trả null
    public Match readMatch(String roomCode) throws InterruptedException, ExecutionException {
        final CompletableFuture<DataSnapshot> result = new CompletableFuture<>();
        db.getReference("matches/" + roomCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                result.complete(snapshot);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                result.completeExceptionally(error.toException());
            }
        });
        DataSnapshot snapshot = result.get();
        return snapshot.exists() ? toMatch(roomCode, snapshot) : null;
    }

    // Ghi trạng thái trận, chờ Firebase xác nhận
    public void updateStatus(String roomCode, String status) throws InterruptedException, ExecutionException {
        db.getReference("matches/" + roomCode + "/status").setValueAsync(status).get();
    }

    private Match toMatch(String roomCode, DataSnapshot snapshot) {
        Match match = new Match();
        match.setRoomCode(roomCode);
        match.setStatus(snapshot.child("status").getValue(String.class));
        match.setQuestionSeed(snapshot.child("questionSeed").getValue(Long.class));
        match.setCreatedAt(new Date(snapshot.child("createdAt").getValue(Long.class)));
        for (DataSnapshot child : snapshot.child("players").getChildren()) {
            match.getPlayers().add(toMatchPlayer(child));
        }
        return match;
    }

    private MatchPlayer toMatchPlayer(DataSnapshot child) {
        Player player = new Player();
        player.setId(Integer.parseInt(child.getKey()));
        player.setDisplayName(child.child("displayName").getValue(String.class));
        MatchPlayer mp = new MatchPlayer(player);
        mp.setScore(child.child("score").getValue(Integer.class));
        mp.setForfeited(Boolean.TRUE.equals(child.child("forfeited").getValue(Boolean.class)));
        mp.setTimeUp(Boolean.TRUE.equals(child.child("timeUp").getValue(Boolean.class)));
        return mp;
    }

    // Service account để ở resources (không commit), địa chỉ database ở application-local.properties
    private FirebaseOptions loadOptions() {
        Properties config = DAO.loadConfig();
        try (InputStream serviceAccount = FirebaseAdminDAO.class.getResourceAsStream(config.getProperty("firebase.credentials"))) {
            if (serviceAccount == null) {
                throw new IllegalStateException("Thiếu file " + config.getProperty("firebase.credentials"));
            }
            return FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl(config.getProperty("firebase.databaseUrl"))
                    .build();
        } catch (IOException e) {
            throw new IllegalStateException("Không đọc được service account Firebase", e);
        }
    }
}
