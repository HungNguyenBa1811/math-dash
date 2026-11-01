package ptit.e23cnpm01.nguyenbahung.mathdash.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Match;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.MatchPlayer;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Player;

class RankingTest {
    private static final long START = 1_000_000L;

    private Player player(int id, String name) {
        return new Player(id, name.toLowerCase(), name);
    }

    private Match match(Player a, int aCorrect, double aSeconds, Player b, int bCorrect, double bSeconds) {
        Match match = new Match();
        match.setStartedAt(new Date(START));
        match.getPlayers().add(matchPlayer(a, aCorrect, aSeconds));
        match.getPlayers().add(matchPlayer(b, bCorrect, bSeconds));
        return match;
    }

    private MatchPlayer matchPlayer(Player p, int correct, double seconds) {
        MatchPlayer mp = new MatchPlayer(new Player(p.getId(), p.getUsername(), p.getDisplayName()));
        mp.setCorrectCount(correct);
        mp.setSubmittedAt(new Date(START + (long) (seconds * 1000)));
        return mp;
    }

    @Test
    void tongDiemTrungBinhDoiThuVaThoiGianThang() {
        Player hung = player(1, "HUNG");
        Player hoang = player(2, "HOANG");
        Player lan = player(3, "LAN");
        List<Match> matches = Arrays.asList(
                match(hung, 10, 38, hoang, 10, 45),   // HUNG wins, 38 s
                match(lan, 10, 40, hoang, 9, 30),     // LAN wins, 40 s
                match(hung, 8, 20, lan, 9, 25));      // draw
        List<Player> ranking = PlayerDAO.rank(new ArrayList<>(Arrays.asList(hoang, lan, hung)), matches);

        // Same points and avg opponent points, HUNG wins faster so ranks higher
        assertEquals("HUNG", ranking.get(0).getDisplayName());
        assertEquals("LAN", ranking.get(1).getDisplayName());
        assertEquals("HOANG", ranking.get(2).getDisplayName());
        assertEquals(1.5, ranking.get(0).getTotalPoints());
        assertEquals(0.75, ranking.get(0).getAvgOpponentPoints());
        assertEquals(38.0, ranking.get(0).getAvgWinTime());
        assertEquals(0.0, ranking.get(2).getAvgWinTime());
    }

    @Test
    void cungDiemThiDoiThuManhHonDungTren() {
        Player a = player(1, "A");
        Player b = player(2, "B");
        Player c = player(3, "C");
        Player d = player(4, "D");
        List<Match> matches = Arrays.asList(
                match(c, 10, 30, d, 5, 30),    // C 1 point
                match(c, 10, 30, d, 5, 30),    // C 2 points
                match(a, 10, 50, c, 5, 30),    // A beats C (strong)
                match(b, 10, 20, d, 5, 30));   // B beats D (weak)
        List<Player> ranking = PlayerDAO.rank(new ArrayList<>(Arrays.asList(a, b, c, d)), matches);
        // A and B both have 1 point but A beat C (2 points) so ranks higher
        assertEquals("C", ranking.get(0).getDisplayName());
        assertEquals("A", ranking.get(1).getDisplayName());
        assertEquals("B", ranking.get(2).getDisplayName());
    }
}
