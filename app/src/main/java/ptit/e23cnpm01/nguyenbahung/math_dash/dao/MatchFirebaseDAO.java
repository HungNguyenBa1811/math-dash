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

// Node matches/{matchId}: app chỉ đọc, server ghi
public class MatchFirebaseDAO extends FirebaseDAO {
    private static final String NETWORK_ERROR = "Mất kết nối, kiểm tra kết nối mạng";

    private DatabaseReference matchRef;
    private ValueEventListener watcher;

    public void watchMatch(int matchId, final MatchCallback callback) {
        stopWatching(matchId);
        matchRef = con.child("matches").child(String.valueOf(matchId));
        watcher = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    return;
                }
                Integer next = snapshot.child("nextMatchId").getValue(Integer.class);
                Integer stoppedBy = snapshot.child("stoppedBy").getValue(Integer.class);
                if (next != null) {
                    callback.onNextMatch(next);
                } else if (stoppedBy != null) {
                    callback.onStopped(stoppedBy);
                } else {
                    callback.onSuccess(toMatch(Integer.parseInt(snapshot.getKey()), snapshot));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFail(NETWORK_ERROR);
            }
        };
        matchRef.addValueEventListener(watcher);
    }

    public void stopWatching(int matchId) {
        if (watcher != null) {
            matchRef.removeEventListener(watcher);
            watcher = null;
        }
    }

    // model mapper
    private Match toMatch(int matchId, DataSnapshot data) {
        Match match = new Match();
        match.setId(matchId);
        match.setStatus(data.child("status").getValue(String.class));
        Long startedAt = data.child("startedAt").getValue(Long.class);
        match.setStartedAt(new Date(startedAt == null ? 0 : startedAt));
        for (DataSnapshot q : data.child("questions").getChildren()) {
            match.getQuestions().add(toProblem(q));
        }
        for (DataSnapshot p : data.child("players").getChildren()) {
            match.getPlayers().add(toMatchPlayer(Integer.parseInt(p.getKey()), p));
        }
        return match;
    }

    private MathProblem toProblem(DataSnapshot q) {
        List<Integer> options = new ArrayList<>();
        for (DataSnapshot o : q.child("answerOptions").getChildren()) {
            options.add(o.getValue(Integer.class));
        }
        Integer a = q.child("operand1").getValue(Integer.class);
        Integer b = q.child("operand2").getValue(Integer.class);
        return new MathProblem(a == null ? 0 : a, b == null ? 0 : b,
                q.child("operator").getValue(String.class), -1, options);
    }

    // submitted chỉ là cờ; thời điểm nộp thật nằm ở server
    private MatchPlayer toMatchPlayer(int playerId, DataSnapshot p) {
        MatchPlayer mp = new MatchPlayer(new Player(playerId, null, p.child("displayName").getValue(String.class)));
        if (Boolean.TRUE.equals(p.child("submitted").getValue(Boolean.class))) {
            mp.setSubmittedAt(new Date());
        }
        mp.setForfeited(Boolean.TRUE.equals(p.child("forfeited").getValue(Boolean.class)));
        return mp;
    }
}
