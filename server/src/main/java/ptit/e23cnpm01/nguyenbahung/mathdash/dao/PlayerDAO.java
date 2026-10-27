package ptit.e23cnpm01.nguyenbahung.mathdash.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    // ---------- Quản trị (UC06, Hoàng) ----------

    // Người chơi (vai trò player) có tên hiển thị chứa name, kèm tổng điểm; name rỗng thì lấy tất cả
    public List<Player> search(String name) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM tblPlayer WHERE role = ? AND displayName LIKE ? ORDER BY id";
        List<Player> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, Player.ROLE_PLAYER);
            ps.setString(2, "%" + name + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(toPlayer(rs));
            }
        }
        addTotalPoints(list, new MatchDAO().findFinished());
        return list;
    }

    // Gán số trận thắng / hòa / thua cho người chơi, tính từ các trận đã xong
    public void countResults(Player player) throws SQLException {
        countResults(player, new MatchDAO().findFinished());
    }

    // Đếm kết quả bằng luật của trận (Match.getResult); hàm thuần, không truy cập CSDL
    public static void countResults(Player player, List<Match> matches) {
        int win = 0;
        int draw = 0;
        int lose = 0;
        for (Match match : matches) {
            if (!hasPlayer(match, player.getId())) {
                continue;
            }
            String result = match.getResult(player.getId());
            if (Match.WIN.equals(result)) {
                win++;
            } else if (Match.DRAW.equals(result)) {
                draw++;
            } else {
                lose++;
            }
        }
        player.setWinCount(win);
        player.setDrawCount(draw);
        player.setLoseCount(lose);
    }

    // Sửa tên hiển thị của một người chơi; không có người chơi đó thì trả false
    public boolean updateDisplayName(int id, String displayName) throws SQLException {
        return executeUpdate("UPDATE tblPlayer SET displayName = ? WHERE id = ? AND role = ?",
                displayName, id, Player.ROLE_PLAYER);
    }

    // Đặt mật khẩu mới (đã băm BCrypt) cho một người chơi
    public boolean updatePassword(int id, String passwordHash) throws SQLException {
        return executeUpdate("UPDATE tblPlayer SET passwordHash = ? WHERE id = ? AND role = ?",
                passwordHash, id, Player.ROLE_PLAYER);
    }

    // Xóa người chơi chưa có trận nào; đã có trận (có dòng tblMatchPlayer) thì không xóa, trả false
    public boolean delete(int id) throws SQLException {
        return executeUpdate("DELETE FROM tblPlayer WHERE id = ? AND role = ?"
                        + " AND NOT EXISTS (SELECT 1 FROM tblMatchPlayer WHERE tblPlayerId = ?)",
                id, Player.ROLE_PLAYER, id);
    }

    // Tổng điểm của từng người trong danh sách, cộng điểm trận qua các trận đã xong
    private void addTotalPoints(List<Player> players, List<Match> matches) {
        for (Player player : players) {
            double total = 0;
            for (Match match : matches) {
                if (hasPlayer(match, player.getId())) {
                    total += match.getPoints(player.getId());
                }
            }
            player.setTotalPoints(total);
        }
    }

    // Chạy một câu UPDATE / DELETE với các tham số theo thứ tự; đúng 1 dòng bị ảnh hưởng thì trả true
    private boolean executeUpdate(String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            return ps.executeUpdate() == 1;
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

    // Bảng xếp hạng: mọi người chơi (vai trò player) kèm 3 tiêu chí, đã sắp xếp
    public List<Player> getRanking() throws SQLException {
        return rank(findAllPlayers(), new MatchDAO().findFinished());
    }

    // Tính 3 tiêu chí từ các trận đã xong rồi sắp xếp; hàm thuần, không truy cập CSDL
    public static List<Player> rank(List<Player> players, List<Match> matches) {
        Map<Integer, Player> byId = new HashMap<>();
        for (Player p : players) {
            byId.put(p.getId(), p);
        }
        Map<Integer, Set<Integer>> opponents = new HashMap<>();
        Map<Integer, List<Long>> winTimes = new HashMap<>();
        for (Match match : matches) {
            addMatch(match, byId, opponents, winTimes);
        }
        for (Player p : players) {
            p.setAvgOpponentPoints(averagePoints(opponents.get(p.getId()), byId));
            p.setAvgWinTime(averageSeconds(winTimes.get(p.getId())));
        }
        Collections.sort(players, new RankingComparator());
        return players;
    }

    // Cộng điểm trận, ghi đối thủ đã gặp và thời gian của trận thắng cho cả 2 người
    private static void addMatch(Match match, Map<Integer, Player> byId,
                                 Map<Integer, Set<Integer>> opponents, Map<Integer, List<Long>> winTimes) {
        for (MatchPlayer mp : match.getPlayers()) {
            int id = mp.getPlayer().getId();
            Player player = byId.get(id);
            if (player == null) {
                continue;
            }
            player.setTotalPoints(player.getTotalPoints() + match.getPoints(id));
            addOpponent(opponents, id, opponentId(match, id));
            long time = match.getFinishTime(id);
            if (Match.WIN.equals(match.getResult(id)) && time != Long.MAX_VALUE) {
                addWinTime(winTimes, id, time);
            }
        }
    }

    private static int opponentId(Match match, int playerId) {
        for (MatchPlayer mp : match.getPlayers()) {
            if (mp.getPlayer().getId() != playerId) {
                return mp.getPlayer().getId();
            }
        }
        return playerId;
    }

    private static void addOpponent(Map<Integer, Set<Integer>> opponents, int playerId, int opponentId) {
        if (!opponents.containsKey(playerId)) {
            opponents.put(playerId, new HashSet<Integer>());
        }
        opponents.get(playerId).add(opponentId);
    }

    private static void addWinTime(Map<Integer, List<Long>> winTimes, int playerId, long time) {
        if (!winTimes.containsKey(playerId)) {
            winTimes.put(playerId, new ArrayList<Long>());
        }
        winTimes.get(playerId).add(time);
    }

    // Trung bình tổng điểm của các đối thủ khác nhau đã gặp; chưa gặp ai thì 0
    private static double averagePoints(Set<Integer> ids, Map<Integer, Player> byId) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        double sum = 0;
        for (int id : ids) {
            Player p = byId.get(id);
            sum += p == null ? 0 : p.getTotalPoints();
        }
        return sum / ids.size();
    }

    // Trung bình thời gian các trận thắng, tính bằng giây; chưa thắng trận nào thì 0
    private static double averageSeconds(List<Long> times) {
        if (times == null || times.isEmpty()) {
            return 0;
        }
        long sum = 0;
        for (long t : times) {
            sum += t;
        }
        return sum / 1000.0 / times.size();
    }

    // Tổng điểm giảm dần → trung bình điểm đối thủ giảm dần → trung bình thời gian thắng tăng dần
    // (người chưa thắng trận nào đứng sau)
    private static class RankingComparator implements Comparator<Player> {
        @Override
        public int compare(Player a, Player b) {
            if (a.getTotalPoints() != b.getTotalPoints()) {
                return Double.compare(b.getTotalPoints(), a.getTotalPoints());
            }
            if (a.getAvgOpponentPoints() != b.getAvgOpponentPoints()) {
                return Double.compare(b.getAvgOpponentPoints(), a.getAvgOpponentPoints());
            }
            return Double.compare(winTimeKey(a), winTimeKey(b));
        }

        private double winTimeKey(Player p) {
            return p.getAvgWinTime() == 0 ? Double.MAX_VALUE : p.getAvgWinTime();
        }
    }

    private List<Player> findAllPlayers() throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM tblPlayer WHERE role = ? ORDER BY id";
        List<Player> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, Player.ROLE_PLAYER);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(toPlayer(rs));
            }
        }
        return list;
    }

    private static boolean hasPlayer(Match match, int playerId) {
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
