package ptit.e23cnpm01.nguyenbahung.math_dash.dao;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Match;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.MatchPlayer;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.MathProblem;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Player;

// Node matches/{matchId}
public class MatchFirebaseDAO extends FirebaseDAO {
    private static final String NETWORK_ERROR = "Mất kết nối, kiểm tra kết nối mạng";

    private DatabaseReference matchRef;
    private ValueEventListener watcher;

    // Each change of the node reports: the next match was created, a player stopped, or the new match state
    public void watchMatch(final int matchId, final MatchCallback callback) {
        stopWatching();
        matchRef = con.child("matches").child(String.valueOf(matchId));
        watcher = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot data) {
                if (!data.exists()) {
                    return;
                }
                Integer next = data.child("nextMatchId").getValue(Integer.class);
                Integer stoppedBy = data.child("stoppedBy").getValue(Integer.class);
                if (next != null) {
                    callback.onNextMatch(next);
                    return;
                }
                if (stoppedBy != null) {
                    callback.onStopped(stoppedBy);
                    return;
                }

                Match match = new Match();
                match.setId(matchId);
                match.setStatus(data.child("status").getValue(String.class));
                Long startedAt = data.child("startedAt").getValue(Long.class);
                match.setStartedAt(new Date(startedAt == null ? 0 : startedAt));
                // The correct answer is not synced, the server scores the answers
                for (DataSnapshot q : data.child("questions").getChildren()) {
                    List<Integer> options = new ArrayList<>();
                    for (DataSnapshot o : q.child("answerOptions").getChildren()) {
                        options.add(o.getValue(Integer.class));
                    }
                    Integer a = q.child("operand1").getValue(Integer.class);
                    Integer b = q.child("operand2").getValue(Integer.class);
                    match.getQuestions().add(new MathProblem(a == null ? 0 : a, b == null ? 0 : b,
                            q.child("operator").getValue(String.class), -1, options));
                }
                // submitted is only a flag; the real submit time is kept on the server
                for (DataSnapshot p : data.child("players").getChildren()) {
                    Player player = new Player(Integer.parseInt(p.getKey()), null,
                            p.child("displayName").getValue(String.class));
                    MatchPlayer mp = new MatchPlayer(player);
                    if (Boolean.TRUE.equals(p.child("submitted").getValue(Boolean.class))) {
                        mp.setSubmittedAt(new Date());
                    }
                    mp.setForfeited(Boolean.TRUE.equals(p.child("forfeited").getValue(Boolean.class)));
                    match.getPlayers().add(mp);
                }
                callback.onSuccess(match);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFail(NETWORK_ERROR);
            }
        };
        matchRef.addValueEventListener(watcher);
    }

    public void stopWatching() {
        if (watcher != null) {
            matchRef.removeEventListener(watcher);
            watcher = null;
        }
    }
}
