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

    // The invitation is removed automatically when the inviter closes the app
    public Challenge sendChallenge(Player challenger, Player opponent) {
        DatabaseReference ref = con.child("challenges").push();
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

    // Invitations sent to the player
    public void watchIncoming(int playerId, final ChallengeCallback callback) {
        incomingQuery = con.child("challenges").orderByChild("opponentId").equalTo(playerId);
        incomingWatcher = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Also emit cancelled invitations so the invitee's dialog closes
                for (DataSnapshot child : snapshot.getChildren()) {
                    callback.onChallengeChanged(toChallenge(child));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        incomingQuery.addValueEventListener(incomingWatcher);
    }

    // The invitation the player sent: rejected, or accepted with a matchId
    public void watchChallenge(String challengeId, final ChallengeCallback callback) {
        stopWatchingSent();
        sentRef = con.child("challenges").child(challengeId);
        sentWatcher = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    callback.onChallengeChanged(toChallenge(snapshot));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        sentRef.addValueEventListener(sentWatcher);
    }

    public void rejectChallenge(String challengeId) {
        con.child("challenges").child(challengeId).child("status").setValue(Challenge.REJECTED);
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

    private Challenge toChallenge(DataSnapshot data) {
        Integer challengerId = data.child("challengerId").getValue(Integer.class);
        Player challenger = new Player(challengerId == null ? 0 : challengerId, null,
                data.child("challengerName").getValue(String.class));
        Double points = data.child("challengerPoints").getValue(Double.class);
        challenger.setTotalPoints(points == null ? 0 : points);
        Integer opponentId = data.child("opponentId").getValue(Integer.class);
        Player opponent = new Player(opponentId == null ? 0 : opponentId, null,
                data.child("opponentName").getValue(String.class));

        Challenge challenge = new Challenge();
        challenge.setId(data.getKey());
        challenge.setStatus(data.child("status").getValue(String.class));
        challenge.setChallenger(challenger);
        challenge.setOpponent(opponent);
        Integer matchId = data.child("matchId").getValue(Integer.class);
        challenge.setMatchId(matchId == null ? 0 : matchId);
        Long createdAt = data.child("createdAt").getValue(Long.class);
        challenge.setCreatedAt(createdAt == null ? null : new Date(createdAt));
        return challenge;
    }
}
