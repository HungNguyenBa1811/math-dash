package ptit.e23cnpm01.nguyenbahung.math_dash.dao;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Match;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.MatchPlayer;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Player;

/**
 * Đọc / ghi trận đang diễn ra ở node matches/{roomCode} trên Firebase.
 * Firebase trả dữ liệu node dạng Map không có kiểu, nên các phép ép kiểu sang Map được bỏ cảnh báo.
 */
@SuppressWarnings("unchecked")
public class MatchFirebaseDAO extends FirebaseDAO {
    private static final String NETWORK_ERROR = "Không kết nối được, kiểm tra kết nối mạng";

    private ValueEventListener matchWatcher;
    private ValueEventListener connectionWatcher;
    private boolean connected;

    // Tạo phòng: ghi trận mới bằng transaction; mã đã có trận chưa kết thúc thì báo trùng (node finished coi như mã trống)
    public void createMatch(Player me, final MatchCallback callback) {
        final Match match = newMatch(me);
        final DatabaseReference ref = matchRef(match.getRoomCode());
        ref.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData data) {
                Object status = data.child("status").getValue();
                if (status != null && !Match.FINISHED.equals(status)) {
                    return Transaction.abort();
                }
                data.setValue(toData(match));
                return Transaction.success(data);
            }

            @Override
            public void onComplete(DatabaseError error, boolean committed, DataSnapshot snapshot) {
                if (!committed) {
                    callback.onFail(error != null ? NETWORK_ERROR : "Mã phòng đã được dùng, hãy thử lại");
                    return;
                }
                // Người tạo phòng tắt app khi đang chờ thì phòng bị xóa
                ref.onDisconnect().removeValue();
                callback.onSuccess(toMatch(match.getRoomCode(), (Map<String, Object>) snapshot.getValue()));
            }
        });
    }

    // Vào phòng bằng transaction; luật vào phòng Match.canJoin chạy bên trong transaction
    public void joinMatch(final String roomCode, final Player me, final MatchCallback callback) {
        matchRef(roomCode).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData data) {
                // Máy chưa có dữ liệu cục bộ thì data rỗng; Firebase sẽ chạy lại với dữ liệu thật
                if (data.getValue() == null) {
                    return Transaction.success(data);
                }
                Match match = toMatch(roomCode, (Map<String, Object>) data.getValue());
                if (!match.canJoin(me.getId())) {
                    return Transaction.abort();
                }
                data.child("players").child(String.valueOf(me.getId())).setValue(toData(me));
                data.child("status").setValue(Match.PLAYING);
                return Transaction.success(data);
            }

            @Override
            public void onComplete(DatabaseError error, boolean committed, DataSnapshot snapshot) {
                onJoinComplete(roomCode, error, committed, snapshot, callback);
            }
        });
    }

    // Theo dõi trận: mỗi lần trận thay đổi thì đóng gói lại thành Match; mất kết nối thì báo lỗi
    public void watchMatch(final String roomCode, final MatchCallback callback) {
        stopWatching(roomCode);
        matchWatcher = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    callback.onFail("Phòng đã bị hủy");
                    return;
                }
                callback.onSuccess(toMatch(roomCode, (Map<String, Object>) snapshot.getValue()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFail(NETWORK_ERROR);
            }
        };
        matchRef(roomCode).addValueEventListener(matchWatcher);
        watchConnection(callback);
    }

    // Gỡ các lắng nghe đã đặt ở watchMatch
    public void stopWatching(String roomCode) {
        if (matchWatcher != null) {
            matchRef(roomCode).removeEventListener(matchWatcher);
            matchWatcher = null;
        }
        if (connectionWatcher != null) {
            con.getRoot().child(".info/connected").removeEventListener(connectionWatcher);
            connectionWatcher = null;
        }
    }

    // Hủy phòng đang chờ
    public void deleteMatch(String roomCode) {
        matchRef(roomCode).onDisconnect().cancel();
        matchRef(roomCode).removeValue();
    }

    public void updateScore(String roomCode, int playerId, int score) {
        playerRef(roomCode, playerId).child("score").setValue(score);
    }

    public void setTimeUp(String roomCode, int playerId) {
        playerRef(roomCode, playerId).child("timeUp").setValue(true);
    }

    public void forfeit(String roomCode, int playerId) {
        playerRef(roomCode, playerId).child("forfeited").setValue(true);
    }

    // Khi máy mất kết nối, máy chủ Firebase tự ghi connected = false, forfeited = true
    public void registerDisconnect(String roomCode, int playerId) {
        // Bỏ lệnh xóa phòng đặt lúc tạo phòng, vì trận đã bắt đầu
        matchRef(roomCode).onDisconnect().cancel();
        Map<String, Object> values = new HashMap<>();
        values.put("connected", false);
        values.put("forfeited", true);
        playerRef(roomCode, playerId).onDisconnect().updateChildren(values);
    }

    // Trận mới: mã phòng 6 chữ số, questionSeed ngẫu nhiên, người tạo là người chơi đầu tiên, trạng thái chờ
    private Match newMatch(Player me) {
        Random random = new Random();
        Match match = new Match();
        match.setRoomCode(String.format(Locale.US, "%06d", random.nextInt(1000000)));
        match.setStatus(Match.WAITING);
        match.setQuestionSeed(random.nextLong());
        match.getPlayers().add(new MatchPlayer(me));
        return match;
    }

    // Đóng gói dữ liệu node thành Match + các MatchPlayer
    private Match toMatch(String roomCode, Map<String, Object> data) {
        Match match = new Match();
        match.setRoomCode(roomCode);
        match.setStatus((String) data.get("status"));
        match.setQuestionSeed((Long) data.get("questionSeed"));
        Object createdAt = data.get("createdAt");
        if (createdAt instanceof Long) {
            match.setCreatedAt(new Date((Long) createdAt));
        }
        addPlayers(match, data.get("players"));
        return match;
    }

    // Firebase đổi node có khóa là số (id người chơi) thành List, nên đọc được cả 2 dạng
    private void addPlayers(Match match, Object players) {
        if (players instanceof Map) {
            for (Map.Entry<String, Object> entry : ((Map<String, Object>) players).entrySet()) {
                match.getPlayers().add(toMatchPlayer(Integer.parseInt(entry.getKey()), (Map<String, Object>) entry.getValue()));
            }
        } else if (players instanceof List) {
            List<Object> list = (List<Object>) players;
            for (int id = 0; id < list.size(); id++) {
                if (list.get(id) != null) {
                    match.getPlayers().add(toMatchPlayer(id, (Map<String, Object>) list.get(id)));
                }
            }
        }
    }

    private MatchPlayer toMatchPlayer(int playerId, Map<String, Object> data) {
        MatchPlayer mp = new MatchPlayer(new Player(playerId, null, (String) data.get("displayName")));
        mp.setScore(((Long) data.get("score")).intValue());
        mp.setForfeited(Boolean.TRUE.equals(data.get("forfeited")));
        mp.setTimeUp(Boolean.TRUE.equals(data.get("timeUp")));
        return mp;
    }

    private void onJoinComplete(String roomCode, DatabaseError error, boolean committed,
                                DataSnapshot snapshot, MatchCallback callback) {
        if (error != null) {
            callback.onFail(NETWORK_ERROR);
        } else if (!snapshot.exists() || Match.FINISHED.equals(snapshot.child("status").getValue())) {
            callback.onFail("Không tìm thấy phòng");
        } else if (!committed) {
            callback.onFail("Phòng đã đầy");
        } else {
            callback.onSuccess(toMatch(roomCode, (Map<String, Object>) snapshot.getValue()));
        }
    }

    // Lắng nghe .info/connected: đang kết nối mà bị ngắt thì báo lỗi
    private void watchConnection(final MatchCallback callback) {
        connected = false;
        connectionWatcher = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean now = Boolean.TRUE.equals(snapshot.getValue(Boolean.class));
                if (connected && !now) {
                    callback.onFail("Mất kết nối, trận đấu đã kết thúc");
                }
                connected = now;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        con.getRoot().child(".info/connected").addValueEventListener(connectionWatcher);
    }

    // Dữ liệu node của trận mới: người tạo phòng là người chơi đầu tiên
    private Map<String, Object> toData(Match match) {
        Map<String, Object> players = new HashMap<>();
        for (MatchPlayer mp : match.getPlayers()) {
            players.put(String.valueOf(mp.getPlayer().getId()), toData(mp.getPlayer()));
        }
        Map<String, Object> data = new HashMap<>();
        data.put("status", match.getStatus());
        data.put("questionSeed", match.getQuestionSeed());
        data.put("createdAt", ServerValue.TIMESTAMP);
        data.put("players", players);
        return data;
    }

    // Dữ liệu node của một người chơi khi vào trận; displayName chép từ Player để đối thủ thấy tên
    private Map<String, Object> toData(Player player) {
        Map<String, Object> data = new HashMap<>();
        data.put("displayName", player.getDisplayName());
        data.put("score", 0);
        data.put("forfeited", false);
        data.put("connected", true);
        data.put("timeUp", false);
        return data;
    }

    private DatabaseReference matchRef(String roomCode) {
        return con.child("matches").child(roomCode);
    }

    private DatabaseReference playerRef(String roomCode, int playerId) {
        return matchRef(roomCode).child("players").child(String.valueOf(playerId));
    }
}
