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
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.MathProblem;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Player;

public class MatchDAO extends DAO {

    // Mỗi dòng là một người chơi trong một trận, kèm thông tin trận và tài khoản
    private static final String SELECT_PLAYERS = "SELECT m.id, m.startedAt, m.endedAt,"
            + " mp.id AS mpId, mp.correctCount, mp.submittedAt, mp.forfeited,"
            + " p.id AS playerId, p.username, p.displayName"
            + " FROM tblMatch m"
            + " JOIN tblMatchPlayer mp ON mp.tblMatchId = m.id"
            + " JOIN tblPlayer p ON p.id = mp.tblPlayerId";

    // Lưu 1 dòng tblMatch + 10 dòng tblMathProblem + 2 dòng tblMatchPlayer trong một transaction,
    // gán id vừa sinh cho trận, câu hỏi và người chơi trong trận
    public boolean insert(Match match) {
        synchronized (conn) {
            try {
                conn.setAutoCommit(false);
                insertMatch(match);
                for (MathProblem problem : match.getQuestions()) {
                    insertProblem(problem, match.getId());
                }
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

    // Trận kèm đề và 2 người chơi; không có thì trả null
    public Match findById(int id) throws SQLException {
        List<Match> list = query(SELECT_PLAYERS + " WHERE m.id = ? ORDER BY mp.id", id);
        if (list.isEmpty()) {
            return null;
        }
        Match match = list.get(0);
        match.setQuestions(findProblems(id));
        return match;
    }

    // Mọi trận đã kết thúc kèm 2 người chơi, không kèm đề (dùng cho tổng điểm, bảng xếp hạng)
    public List<Match> findFinished() throws SQLException {
        return query(SELECT_PLAYERS + " WHERE m.endedAt IS NOT NULL ORDER BY m.id, mp.id", null);
    }

    // Lưu kết quả chấm và thời điểm nộp; chỉ nộp được một lần
    public boolean updateSubmission(int matchId, MatchPlayer mp) throws SQLException {
        String sql = "UPDATE tblMatchPlayer SET correctCount = ?, submittedAt = ?"
                + " WHERE tblMatchId = ? AND tblPlayerId = ? AND submittedAt IS NULL";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, mp.getCorrectCount());
            ps.setTimestamp(2, new Timestamp(mp.getSubmittedAt().getTime()));
            ps.setInt(3, matchId);
            ps.setInt(4, mp.getPlayer().getId());
            return ps.executeUpdate() == 1;
        }
    }

    // Đánh dấu người chơi bỏ trận
    public boolean updateForfeit(int matchId, int playerId) throws SQLException {
        String sql = "UPDATE tblMatchPlayer SET forfeited = 1 WHERE tblMatchId = ? AND tblPlayerId = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, matchId);
            ps.setInt(2, playerId);
            return ps.executeUpdate() == 1;
        }
    }

    // Ghi thời điểm kết thúc; trận đã kết thúc thì không ghi lại
    public boolean updateEndedAt(int matchId, Date endedAt) throws SQLException {
        String sql = "UPDATE tblMatch SET endedAt = ? WHERE id = ? AND endedAt IS NULL";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, new Timestamp(endedAt.getTime()));
            ps.setInt(2, matchId);
            return ps.executeUpdate() == 1;
        }
    }

    // Chạy truy vấn SELECT_PLAYERS, gom các dòng cùng trận thành 1 Match + 2 MatchPlayer
    private List<Match> query(String sql, Integer key) throws SQLException {
        List<Match> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (key != null) {
                ps.setInt(1, key);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Match last = list.isEmpty() ? null : list.get(list.size() - 1);
                if (last == null || last.getId() != rs.getInt("id")) {
                    last = toMatch(rs);
                    list.add(last);
                }
                last.getPlayers().add(toMatchPlayer(rs));
            }
        }
        return list;
    }

    private List<MathProblem> findProblems(int matchId) throws SQLException {
        String sql = "SELECT * FROM tblMathProblem WHERE tblMatchId = ? ORDER BY id";
        List<MathProblem> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, matchId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(toProblem(rs));
            }
        }
        return list;
    }

    private void insertMatch(Match match) throws SQLException {
        String sql = "INSERT INTO tblMatch (startedAt) VALUES (?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setTimestamp(1, new Timestamp(match.getStartedAt().getTime()));
            ps.executeUpdate();
            match.setId(generatedId(ps));
        }
    }

    private void insertProblem(MathProblem p, int matchId) throws SQLException {
        String sql = "INSERT INTO tblMathProblem (operand1, operand2, operator, correctAnswer,"
                + " option1, option2, option3, option4, tblMatchId) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getOperand1());
            ps.setInt(2, p.getOperand2());
            ps.setString(3, p.getOperator());
            ps.setInt(4, p.getCorrectAnswer());
            for (int i = 0; i < 4; i++) {
                ps.setInt(5 + i, p.getAnswerOptions().get(i));
            }
            ps.setInt(9, matchId);
            ps.executeUpdate();
            p.setId(generatedId(ps));
        }
    }

    private void insertMatchPlayer(MatchPlayer mp, int matchId) throws SQLException {
        String sql = "INSERT INTO tblMatchPlayer (tblMatchId, tblPlayerId) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, matchId);
            ps.setInt(2, mp.getPlayer().getId());
            ps.executeUpdate();
            mp.setId(generatedId(ps));
        }
    }

    private int generatedId(PreparedStatement ps) throws SQLException {
        ResultSet keys = ps.getGeneratedKeys();
        keys.next();
        return keys.getInt(1);
    }

    private Match toMatch(ResultSet rs) throws SQLException {
        Match match = new Match();
        match.setId(rs.getInt("id"));
        match.setStartedAt(rs.getTimestamp("startedAt"));
        match.setEndedAt(rs.getTimestamp("endedAt"));
        match.setStatus(match.getEndedAt() == null ? Match.PLAYING : Match.FINISHED);
        return match;
    }

    private MatchPlayer toMatchPlayer(ResultSet rs) throws SQLException {
        Player player = new Player(rs.getInt("playerId"), rs.getString("username"), rs.getString("displayName"));
        MatchPlayer mp = new MatchPlayer(player);
        mp.setId(rs.getInt("mpId"));
        mp.setCorrectCount(rs.getInt("correctCount"));
        mp.setSubmittedAt(rs.getTimestamp("submittedAt"));
        mp.setForfeited(rs.getBoolean("forfeited"));
        return mp;
    }

    private MathProblem toProblem(ResultSet rs) throws SQLException {
        List<Integer> options = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            options.add(rs.getInt("option" + i));
        }
        MathProblem p = new MathProblem(rs.getInt("operand1"), rs.getInt("operand2"),
                rs.getString("operator"), rs.getInt("correctAnswer"), options);
        p.setId(rs.getInt("id"));
        return p;
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
