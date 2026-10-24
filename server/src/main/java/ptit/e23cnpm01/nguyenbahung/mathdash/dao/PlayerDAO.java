package ptit.e23cnpm01.nguyenbahung.mathdash.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Player;

public class PlayerDAO extends DAO {

    // Tìm người chơi theo tên đăng nhập, không có thì trả null
    public Player findByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, passwordHash, displayName, createdAt FROM tblPlayer WHERE username = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                return null;
            }
            Player player = new Player(rs.getInt("id"), rs.getString("username"), rs.getString("displayName"));
            player.setPasswordHash(rs.getString("passwordHash"));
            player.setCreatedAt(rs.getTimestamp("createdAt"));
            return player;
        }
    }

    // Thêm người chơi mới; trùng tên đăng nhập thì trả false
    public boolean insert(Player player) {
        String sql = "INSERT INTO tblPlayer (username, passwordHash, displayName) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, player.getUsername());
            ps.setString(2, player.getPasswordHash());
            ps.setString(3, player.getDisplayName());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            return false;
        }
    }
}
