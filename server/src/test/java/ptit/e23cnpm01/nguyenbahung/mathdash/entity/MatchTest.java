package ptit.e23cnpm01.nguyenbahung.mathdash.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import ptit.e23cnpm01.nguyenbahung.mathdash.dao.ProblemDAO;

class MatchTest {
    private static final int HUNG = 1;
    private static final int HOANG = 2;
    private static final long START = 1_000_000L;

    // Số giây âm = chưa nộp
    private Match match(int hungCorrect, double hungSeconds, int hoangCorrect, double hoangSeconds) {
        Match match = new Match();
        match.setStartedAt(new Date(START));
        match.getPlayers().add(matchPlayer(HUNG, hungCorrect, hungSeconds));
        match.getPlayers().add(matchPlayer(HOANG, hoangCorrect, hoangSeconds));
        return match;
    }

    private MatchPlayer matchPlayer(int playerId, int correct, double seconds) {
        MatchPlayer mp = new MatchPlayer(new Player(playerId, "u" + playerId, "P" + playerId));
        mp.setCorrectCount(correct);
        if (seconds >= 0) {
            mp.setSubmittedAt(new Date(START + (long) (seconds * 1000)));
        }
        return mp;
    }

    @Test
    void cungDungHetAiNhanhHonThiThang() {
        Match m = match(10, 38.2, 10, 45.6);
        assertEquals(Match.WIN, m.getResult(HUNG));
        assertEquals(Match.LOSE, m.getResult(HOANG));
        assertEquals(38_200, m.getFinishTime(HUNG));
    }

    @Test
    void motNguoiDungHetThiThangDuCham() {
        Match m = match(10, 55, 9, 30);
        assertEquals(Match.WIN, m.getResult(HUNG));
        assertEquals(Match.LOSE, m.getResult(HOANG));
    }

    @Test
    void caHaiCoCauSaiThiHoa() {
        Match m = match(9, 20, 7, 50);
        assertEquals(Match.DRAW, m.getResult(HUNG));
        assertEquals(Match.DRAW, m.getResult(HOANG));
    }

    @Test
    void cungDungHetCungThoiGianThiHoa() {
        assertEquals(Match.DRAW, match(10, 40, 10, 40).getResult(HUNG));
    }

    @Test
    void boTranThiThuaDuDangDungHet() {
        Match m = match(10, 20, 3, -1);
        m.getPlayers().get(0).setForfeited(true);
        assertEquals(Match.LOSE, m.getResult(HUNG));
        assertEquals(Match.WIN, m.getResult(HOANG));
    }

    @Test
    void diemTranLa1VaNuaVa0() {
        assertEquals(1.0, match(10, 30, 10, 31).getPoints(HUNG));
        assertEquals(0.0, match(10, 30, 10, 31).getPoints(HOANG));
        assertEquals(0.5, match(8, 30, 9, 31).getPoints(HUNG));
    }

    @Test
    void isOverKhiCa2DaNopHoacCoNguoiBoTran() {
        assertFalse(match(10, 30, 0, -1).isOver());
        assertTrue(match(10, 30, 9, 40).isOver());
        Match m = match(0, -1, 0, -1);
        m.getPlayers().get(1).setForfeited(true);
        assertTrue(m.isOver());
    }

    @Test
    void countCorrectChamTheoDapAnDung() {
        Match m = new Match();
        m.setQuestions(new ProblemDAO(new Random(37)).generateProblems(Match.QUESTION_COUNT));
        List<Integer> answers = new ArrayList<>();
        for (MathProblem p : m.getQuestions()) {
            answers.add(p.getCorrectAnswer());
        }
        assertEquals(10, m.countCorrect(answers));
        answers.set(3, -1);
        answers.set(7, answers.get(7) + 1);
        assertEquals(8, m.countCorrect(answers));
        assertEquals(0, m.countCorrect(Arrays.asList(-1, -1)));
    }

    @Test
    void doiThuLaNguoiConLaiTrongTran() {
        Match m = match(10, 38.2, 10, 45.6);
        assertEquals(HOANG, m.getOpponent(HUNG).getPlayer().getId());
        assertEquals(HUNG, m.getOpponent(HOANG).getPlayer().getId());
    }
}
