package ptit.e23cnpm01.nguyenbahung.math_dash.dao;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Challenge;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Player;

/**
 * Lời mời thách đấu ở node challenges/{challengeId}.
 */
public class ChallengeFirebaseDAO extends FirebaseDAO {
    private Query incomingQuery;
    private ValueEventListener incomingWatcher;
    private DatabaseReference sentRef;
    private ValueEventListener sentWatcher;

    // Tạo lời mời đang chờ; người mời tắt app thì lời mời tự bị xóa
    public Challenge sendChallenge(Player challenger, Player opponent) {
        DatabaseReference ref = challengesRef().push();
        Map<String, Object> data = new HashMap<>();
        data.put("challengerId", challenger.getId());
        data.put("challengerName", challenger.getDisplayName());
        data.put("challengerPoints", challenger.getTotalPoints());
        data.put("opponentId", opponent.getId());
        data.put("opponentName", opponent.getDisplayName());
        data.put("status", Challenge.PENDING);
        data.put("createdAt", ServerValue.TIMESTAMP);
        ref.onDisconnect().removeValue();
        ref.setValue(data);
        Challenge challenge = new Challenge();
        challenge.setId(ref.getKey());
        challenge.setStatus(Challenge.PENDING);
        challenge.setChallenger(challenger);
        challenge.setOpponent(opponent);
        return challenge;
    }

    // Nhận các lời mời đang chờ gửi tới playerId
    public void watchIncoming(int playerId, final ChallengeCallback callback) {
        incomingQuery = challengesRef().orderByChild("opponentId").equalTo(playerId);
        incomingWatcher = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    Challenge challenge = toChallenge(child.getKey(), child);
                    if (Challenge.PENDING.equals(challenge.getStatus())) {
                        callback.onChallengeChanged(challenge);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Mất kết nối: không nhận được lời mời mới
            }
        };
        incomingQuery.addValueEventListener(incomingWatcher);
    }

    // Theo dõi lời mời mình đã gửi để biết được đồng ý hay bị từ chối
    public void watchChallenge(String challengeId, final ChallengeCallback callback) {
        stopWatchingSent();
        sentRef = challengesRef().child(challengeId);
        sentWatcher = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    callback.onChallengeChanged(toChallenge(snapshot.getKey(), snapshot));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Mất kết nối: lời mời coi như hết hiệu lực
            }
        };
        sentRef.addValueEventListener(sentWatcher);
    }

    public void rejectChallenge(String challengeId) {
        challengesRef().child(challengeId).child("status").setValue(Challenge.REJECTED);
    }

    // Gỡ mọi lắng nghe (khi rời màn chính)
    public void stopWatching() {
        stopWatchingSent();
        if (incomingWatcher != null) {
            incomingQuery.removeEventListener(incomingWatcher);
            incomingWatcher = null;
        }
    }

    public void stopWatchingSent() {
        if (sentWatcher != null) {
            sentRef.removeEventListener(sentWatcher);
            sentWatcher = null;
        }
    }

    // Đóng gói một node lời mời thành Challenge kèm 2 Player
    private Challenge toChallenge(String id, DataSnapshot data) {
        Challenge challenge = new Challenge();
        challenge.setId(id);
        challenge.setStatus(data.child("status").getValue(String.class));
        challenge.setChallenger(toPlayer(data, "challengerId", "challengerName"));
        challenge.setOpponent(toPlayer(data, "opponentId", "opponentName"));
        Double points = data.child("challengerPoints").getValue(Double.class);
        challenge.getChallenger().setTotalPoints(points == null ? 0 : points);
        Integer matchId = data.child("matchId").getValue(Integer.class);
        challenge.setMatchId(matchId == null ? 0 : matchId);
        return challenge;
    }

    private Player toPlayer(DataSnapshot data, String idKey, String nameKey) {
        Integer id = data.child(idKey).getValue(Integer.class);
        return new Player(id == null ? 0 : id, null, data.child(nameKey).getValue(String.class));
    }

    private DatabaseReference challengesRef() {
        return con.child("challenges");
    }
}
