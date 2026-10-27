package ptit.e23cnpm01.nguyenbahung.mathdash.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Match;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.MatchPlayer;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Player;

/**
 * Số trận thắng / hòa / thua của một người chơi (UC06), đếm bằng luật của trận.
 */
class ResultCountTest {
    private static final long START = 1_000_000L;

    private Player player(int id, String name) {
        return new Player(id, name.toLowerCase(), name);
    }

    // Trận đã xong giữa a và b: số câu đúng và số giây nộp bài của từng người
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
    void demThangHoaThuaVaBoQuaTranKhongThamGia() {
        Player hung = player(1, "HUNG");
        Player hoang = player(2, "HOANG");
        Player lan = player(3, "LAN");
        List<Match> matches = Arrays.asList(
                match(hoang, 10, 38, hung, 10, 45),   // HOANG thắng vì nộp nhanh hơn
                match(hoang, 9, 30, lan, 10, 50),     // HOANG thua vì có câu sai
                match(hung, 8, 20, hoang, 7, 25),     // hòa vì cả 2 có câu sai
                match(hung, 10, 20, lan, 10, 25));    // HOANG không tham gia
        PlayerDAO.countResults(hoang, matches);

        assertEquals(1, hoang.getWinCount());
        assertEquals(1, hoang.getDrawCount());
        assertEquals(1, hoang.getLoseCount());
    }

    @Test
    void boTranThiTinhLaThua() {
        Player hung = player(1, "HUNG");
        Player hoang = player(2, "HOANG");
        Match match = match(hoang, 10, 20, hung, 5, 30);
        match.getPlayers().get(0).setForfeited(true);
        PlayerDAO.countResults(hoang, Arrays.asList(match));
        PlayerDAO.countResults(hung, Arrays.asList(match));

        assertEquals(1, hoang.getLoseCount());
        assertEquals(1, hung.getWinCount());
    }

    @Test
    void chuaCoTranNaoThiDeuBangKhong() {
        Player hoang = player(2, "HOANG");
        PlayerDAO.countResults(hoang, Arrays.<Match>asList());

        assertEquals(0, hoang.getWinCount());
        assertEquals(0, hoang.getDrawCount());
        assertEquals(0, hoang.getLoseCount());
    }
}
