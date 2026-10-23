package ptit.e23cnpm01.nguyenbahung.mathdash.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Match;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.MatchPlayer;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Player;

/**
 * Thao tác bảng tblMatch và tblMatchPlayer.
 */
public class MatchDAO extends DAO {

    // Mỗi dòng kết quả là 1 trận: người chơi a và người chơi b, kèm tên của cả 2
    private static final String SELECT_MATCH = "SELECT m.id, m.roomCode, m.questionSeed, m.createdAt, m.endedAt,"
            + " a.id AS aId, a.score AS aScore, a.forfeited AS aForfeited, pa.id AS aPlayerId, pa.username AS aUsername, pa.displayName AS aName,"
            + " b.id AS bId, b.score AS bScore, b.forfeited AS bForfeited, pb.id AS bPlayerId, pb.username AS bUsername, pb.displayName AS bName"
            + " FROM tblMatch m"
            + " JOIN tblMatchPlayer a ON a.tblMatchId = m.id"
            + " JOIN tblMatchPlayer b ON b.tblMatchId = m.id AND b.id <> a.id"
            + " JOIN tblPlayer pa ON pa.id = a.tblPlayerId"
            + " JOIN tblPlayer pb ON pb.id = b.tblPlayerId";

    // Lưu 1 dòng tblMatch + 2 dòng tblMatchPlayer trong một transaction, gán id vừa sinh vào match
    public boolean insert(Match match) {
        synchronized (conn) {
            try {
                conn.setAutoCommit(false);
                insertMatch(match);
                for (MatchPlayer mp : match.getPlayers()) {
                    insertMatchPlayer(mp, match.getId());
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                rollback();
                return false;
            } finally {
                resetAutoCommit();
            }
        }
    }

    // Tìm trận theo khóa duy nhất (roomCode, createdAt)
    public Match findByRoomCode(String roomCode, Date createdAt) throws SQLException {
        String sql = SELECT_MATCH + " WHERE m.roomCode = ? AND m.createdAt = ? AND a.id < b.id";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomCode);
            ps.setTimestamp(2, toSeconds(createdAt));
            ResultSet rs = ps.executeQuery();
            return rs.next() ? toMatch(rs) : null;
        }
    }

    // Các trận đã kết thúc của người chơi, mới nhất trước; a là người chơi này, b là đối thủ
    public List<Match> findByPlayer(int playerId) throws SQLException {
        String sql = SELECT_MATCH + " WHERE a.tblPlayerId = ? ORDER BY m.endedAt DESC";
        List<Match> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(toMatch(rs));
            }
        }
        return list;
    }

    private void insertMatch(Match match) throws SQLException {
        String sql = "INSERT INTO tblMatch (roomCode, questionSeed, createdAt, endedAt) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, match.getRoomCode());
            ps.setLong(2, match.getQuestionSeed());
            ps.setTimestamp(3, toSeconds(match.getCreatedAt()));
            ps.setTimestamp(4, toSeconds(match.getEndedAt()));
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            keys.next();
            match.setId(keys.getInt(1));
        }
    }

    private void insertMatchPlayer(MatchPlayer mp, int matchId) throws SQLException {
        String sql = "INSERT INTO tblMatchPlayer (score, forfeited, tblMatchId, tblPlayerId) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, mp.getScore());
            ps.setBoolean(2, mp.isForfeited());
            ps.setInt(3, matchId);
            ps.setInt(4, mp.getPlayer().getId());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            keys.next();
            mp.setId(keys.getInt(1));
        }
    }

    // Đóng gói một dòng kết quả thành 1 Match + 2 MatchPlayer + 2 Player
    private Match toMatch(ResultSet rs) throws SQLException {
        Match match = new Match();
        match.setId(rs.getInt("id"));
        match.setRoomCode(rs.getString("roomCode"));
        match.setStatus("finished");
        match.setQuestionSeed(rs.getLong("questionSeed"));
        match.setCreatedAt(rs.getTimestamp("createdAt"));
        match.setEndedAt(rs.getTimestamp("endedAt"));
        match.getPlayers().add(toMatchPlayer(rs, "a"));
        match.getPlayers().add(toMatchPlayer(rs, "b"));
        return match;
    }

    private MatchPlayer toMatchPlayer(ResultSet rs, String side) throws SQLException {
        Player player = new Player(rs.getInt(side + "PlayerId"), rs.getString(side + "Username"), rs.getString(side + "Name"));
        MatchPlayer mp = new MatchPlayer(player);
        mp.setId(rs.getInt(side + "Id"));
        mp.setScore(rs.getInt(side + "Score"));
        mp.setForfeited(rs.getBoolean(side + "Forfeited"));
        // Trận đã lưu là trận đã xong: người không bỏ trận chắc chắn đã hết giờ
        mp.setTimeUp(!mp.isForfeited());
        return mp;
    }

    // DATETIME chỉ giữ đến giây, nên bỏ phần mili giây để lưu và tìm khớp nhau
    private Timestamp toSeconds(Date date) {
        return new Timestamp(date.getTime() / 1000 * 1000);
    }

    private void rollback() {
        try {
            conn.rollback();
        } catch (SQLException ignored) {
            // kết nối đã hỏng thì không còn gì để hoàn tác
        }
    }

    private void resetAutoCommit() {
        try {
            conn.setAutoCommit(true);
        } catch (SQLException ignored) {
            // kết nối đã hỏng, lần gọi sau sẽ báo lỗi
        }
    }
}
