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

    public Player findByUsername(String username) throws SQLException {
        return findOne("SELECT " + COLUMNS + " FROM tblPlayer WHERE username = ?", username);
    }

    public Player findById(int id) throws SQLException {
        return findOne("SELECT " + COLUMNS + " FROM tblPlayer WHERE id = ?", id);
    }

    // Trùng tên đăng nhập thì trả false
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

    public List<Player> getRanking() throws SQLException {
        return rank(findAllPlayers(), new MatchDAO().findFinished());
    }

    // Hàm thuần, không truy cập CSDL
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

    // Chưa gặp ai thì 0
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

    // Tính bằng giây; chưa thắng thì 0
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

    // Tổng điểm ↓, TB điểm đối thủ ↓, TB thời gian thắng ↑ (chưa thắng đứng sau)
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

    private Player toPlayer(ResultSet rs) throws SQLException {
        Player player = new Player(rs.getInt("id"), rs.getString("username"), rs.getString("displayName"));
        player.setPasswordHash(rs.getString("passwordHash"));
        player.setRole(rs.getString("role"));
        player.setCreatedAt(rs.getTimestamp("createdAt"));
        return player;
    }
}
