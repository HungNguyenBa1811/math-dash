package ptit.e23cnpm01.nguyenbahung.mathdash.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Luật thắng thua (getResult) và điều kiện kết thúc trận (isOver) của Match.
 */
class MatchResultTest {
    private static final int HUNG = 1;
    private static final int HOANG = 2;

    // Trận giữa Hùng và Hoàng với điểm và trạng thái bỏ trận cho trước
    private Match match(int hungScore, boolean hungForfeited, int hoangScore, boolean hoangForfeited) {
        Match match = new Match();
        match.getPlayers().add(matchPlayer(HUNG, hungScore, hungForfeited, true));
        match.getPlayers().add(matchPlayer(HOANG, hoangScore, hoangForfeited, true));
        return match;
    }

    private MatchPlayer matchPlayer(int playerId, int score, boolean forfeited, boolean timeUp) {
        MatchPlayer mp = new MatchPlayer(new Player(playerId, "u" + playerId, "P" + playerId));
        mp.setScore(score);
        mp.setForfeited(forfeited);
        mp.setTimeUp(timeUp);
        return mp;
    }

    @Test
    void thangDiem() {
        assertEquals(Match.WIN, match(120, false, 96, false).getResult(HUNG));
    }

    @Test
    void thuaDiem() {
        assertEquals(Match.LOSE, match(60, false, 132, false).getResult(HUNG));
    }

    @Test
    void hoa() {
        Match match = match(84, false, 84, false);
        assertEquals(Match.DRAW, match.getResult(HUNG));
        assertEquals(Match.DRAW, match.getResult(HOANG));
    }

    @Test
    void doiThuBoTranKhiDangDanDiem() {
        assertEquals(Match.WIN, match(36, false, 72, true).getResult(HUNG));
    }

    @Test
    void minhBoTranKhiDangDanDiem() {
        assertEquals(Match.LOSE, match(72, true, 36, false).getResult(HUNG));
    }

    @Test
    void chuaAiHetGio() {
        Match match = new Match();
        match.getPlayers().add(matchPlayer(HUNG, 10, false, false));
        match.getPlayers().add(matchPlayer(HOANG, 20, false, false));
        assertFalse(match.isOver());
    }

    @Test
    void moiMotNguoiHetGio() {
        Match match = new Match();
        match.getPlayers().add(matchPlayer(HUNG, 10, false, true));
        match.getPlayers().add(matchPlayer(HOANG, 20, false, false));
        assertFalse(match.isOver());
    }

    @Test
    void coNguoiBoTran() {
        Match match = new Match();
        match.getPlayers().add(matchPlayer(HUNG, 10, false, false));
        match.getPlayers().add(matchPlayer(HOANG, 20, true, false));
        assertTrue(match.isOver());
    }
}
