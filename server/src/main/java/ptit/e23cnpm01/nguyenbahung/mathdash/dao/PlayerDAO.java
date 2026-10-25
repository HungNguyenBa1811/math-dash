package ptit.e23cnpm01.nguyenbahung.mathdash.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Match;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.MatchPlayer;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Player;

public class PlayerDAO extends DAO {
    private static final String COLUMNS = "id, username, passwordHash, displayName, role, createdAt";

    // Tìm người chơi theo tên đăng nhập, không có thì trả null
    public Player findByUsername(String username) throws SQLException {
        return findOne("SELECT " + COLUMNS + " FROM tblPlayer WHERE username = ?", username);
    }

    // Tìm người chơi theo id, không có thì trả null
    public Player findById(int id) throws SQLException {
        return findOne("SELECT " + COLUMNS + " FROM tblPlayer WHERE id = ?", id);
    }

    // Thêm người chơi mới với vai trò player; trùng tên đăng nhập thì trả false
    public boolean insert(Player player) {
        String sql = "INSERT INTO tblPlayer (username, passwordHash, displayName, role) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, player.getUsername());
            ps.setString(2, player.getPasswordHash());
            ps.setString(3, player.getDisplayName());
            ps.setString(4, Player.ROLE_PLAYER);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            return false;
        }
    }

    // Tổng điểm: cộng điểm trận (1 / 0,5 / 0) qua các trận đã xong mà người chơi tham gia
    public double getTotalPoints(int playerId) throws SQLException {
        List<Match> matches = new MatchDAO().findFinished();
        double total = 0;
        for (Match match : matches) {
            if (hasPlayer(match, playerId)) {
                total += match.getPoints(playerId);
            }
        }
        return total;
    }

    private boolean hasPlayer(Match match, int playerId) {
        for (MatchPlayer mp : match.getPlayers()) {
            if (mp.getPlayer().getId() == playerId) {
                return true;
            }
        }
        return false;
    }

    private Player findOne(String sql, Object key) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, key);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? toPlayer(rs) : null;
        }
    }

    // Đóng gói một dòng tblPlayer thành Player
    private Player toPlayer(ResultSet rs) throws SQLException {
        Player player = new Player(rs.getInt("id"), rs.getString("username"), rs.getString("displayName"));
        player.setPasswordHash(rs.getString("passwordHash"));
        player.setRole(rs.getString("role"));
        player.setCreatedAt(rs.getTimestamp("createdAt"));
        return player;
    }
}
