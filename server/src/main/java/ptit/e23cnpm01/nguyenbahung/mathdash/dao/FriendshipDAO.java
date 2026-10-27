package ptit.e23cnpm01.nguyenbahung.mathdash.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Friendship;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Player;

public class FriendshipDAO extends DAO {

    // Mỗi dòng là một quan hệ kèm thông tin người gửi (s) và người nhận (r)
    private static final String SELECT = "SELECT f.id, f.status, f.createdAt,"
            + " s.id AS senderId, s.username AS senderUsername, s.displayName AS senderName,"
            + " r.id AS receiverId, r.username AS receiverUsername, r.displayName AS receiverName"
            + " FROM tblFriendship f"
            + " JOIN tblPlayer s ON s.id = f.tblPlayerSenderId"
            + " JOIN tblPlayer r ON r.id = f.tblPlayerReceiverId";

    // Lời mời và bạn bè của người chơi, ở cả 2 vai trò người gửi và người nhận
    public List<Friendship> findByPlayer(int playerId) throws SQLException {
        String sql = SELECT + " WHERE f.tblPlayerSenderId = ? OR f.tblPlayerReceiverId = ? ORDER BY f.createdAt DESC, f.id DESC";
        List<Friendship> list = new ArrayList<>();
        synchronized (conn) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, playerId);
                ps.setInt(2, playerId);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    list.add(toFriendship(rs));
                }
            }
        }
        return list;
    }

    // Quan hệ theo id, không có thì trả null
    public Friendship findById(int id) throws SQLException {
        synchronized (conn) {
            try (PreparedStatement ps = conn.prepareStatement(SELECT + " WHERE f.id = ?")) {
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                return rs.next() ? toFriendship(rs) : null;
            }
        }
    }

    // Giữa 2 người đã có lời mời hoặc đã là bạn bè chưa, xét cả 2 chiều
    public boolean existsBetween(int playerId1, int playerId2) throws SQLException {
        String sql = "SELECT 1 FROM tblFriendship WHERE (tblPlayerSenderId = ? AND tblPlayerReceiverId = ?)"
                + " OR (tblPlayerSenderId = ? AND tblPlayerReceiverId = ?)";
        synchronized (conn) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, playerId1);
                ps.setInt(2, playerId2);
                ps.setInt(3, playerId2);
                ps.setInt(4, playerId1);
                return ps.executeQuery().next();
            }
        }
    }

    // Thêm lời mời mới, gán id và thời điểm gửi; trùng khóa duy nhất thì trả false
    public boolean insert(Friendship friendship) {
        String sql = "INSERT INTO tblFriendship (status, createdAt, tblPlayerSenderId, tblPlayerReceiverId) VALUES (?, ?, ?, ?)";
        Date now = new Date();
        synchronized (conn) {
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, friendship.getStatus());
                ps.setTimestamp(2, new Timestamp(now.getTime()));
                ps.setInt(3, friendship.getSender().getId());
                ps.setInt(4, friendship.getReceiver().getId());
                ps.executeUpdate();
                ResultSet keys = ps.getGeneratedKeys();
                keys.next();
                friendship.setId(keys.getInt(1));
                friendship.setCreatedAt(now);
                return true;
            } catch (SQLException e) {
                return false;
            }
        }
    }

    // Đổi trạng thái; chỉ lời mời đang chờ mới đổi được
    public boolean updateStatus(int id, String status) throws SQLException {
        String sql = "UPDATE tblFriendship SET status = ? WHERE id = ? AND status = ?";
        synchronized (conn) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, status);
                ps.setInt(2, id);
                ps.setString(3, Friendship.PENDING);
                return ps.executeUpdate() == 1;
            }
        }
    }

    // Xóa quan hệ: từ chối, rút lời mời hoặc hủy kết bạn
    public boolean delete(int id) throws SQLException {
        synchronized (conn) {
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM tblFriendship WHERE id = ?")) {
                ps.setInt(1, id);
                return ps.executeUpdate() == 1;
            }
        }
    }

    private Friendship toFriendship(ResultSet rs) throws SQLException {
        Friendship f = new Friendship(
                new Player(rs.getInt("senderId"), rs.getString("senderUsername"), rs.getString("senderName")),
                new Player(rs.getInt("receiverId"), rs.getString("receiverUsername"), rs.getString("receiverName")));
        f.setId(rs.getInt("id"));
        f.setStatus(rs.getString("status"));
        f.setCreatedAt(rs.getTimestamp("createdAt"));
        return f;
    }
}
