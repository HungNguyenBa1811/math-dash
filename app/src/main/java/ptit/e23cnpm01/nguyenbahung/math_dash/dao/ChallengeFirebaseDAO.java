package ptit.e23cnpm01.nguyenbahung.math_dash.dao;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Challenge;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Player;

// Node challenges/{challengeId}
public class ChallengeFirebaseDAO extends FirebaseDAO {
    private Query incomingQuery;
    private ValueEventListener incomingWatcher;
    private DatabaseReference sentRef;
    private ValueEventListener sentWatcher;

    // Người mời tắt app thì lời mời tự bị xóa
    public Challenge sendChallenge(Player challenger, Player opponent) {
        DatabaseReference ref = challengesRef().push();
        Challenge challenge = new Challenge();
        challenge.setId(ref.getKey());
        challenge.setStatus(Challenge.PENDING);
        challenge.setChallenger(challenger);
        challenge.setOpponent(opponent);
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
        return challenge;
    }

    public void watchIncoming(int playerId, final ChallengeCallback callback) {
        incomingQuery = challengesRef().orderByChild("opponentId").equalTo(playerId);
        incomingWatcher = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Gửi cả lời mời đã bị hủy để máy người được mời đóng hộp thoại
                for (DataSnapshot child : snapshot.getChildren()) {
                    callback.onChallengeChanged(toChallenge(child.getKey(), child));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        incomingQuery.addValueEventListener(incomingWatcher);
    }

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
            }
        };
        sentRef.addValueEventListener(sentWatcher);
    }

    public void rejectChallenge(String challengeId) {
        challengesRef().child(challengeId).child("status").setValue(Challenge.REJECTED);
    }

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
        Long createdAt = data.child("createdAt").getValue(Long.class);
        challenge.setCreatedAt(createdAt == null ? null : new Date(createdAt));
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
