package ptit.e23cnpm01.nguyenbahung.math_dash;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import ptit.e23cnpm01.nguyenbahung.math_dash.dao.ProblemDAO;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Answer;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.BotGame;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Match;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.MatchPlayer;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.MathProblem;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Player;

public class MatchTest {
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
    public void cungDungHetAiNhanhHonThiThang() {
        Match m = match(10, 38.2, 10, 45.6);
        assertEquals(Match.WIN, m.getResult(HUNG));
        assertEquals(Match.LOSE, m.getResult(HOANG));
        assertEquals(38_200L, m.getFinishTime(HUNG));
    }

    @Test
    public void motNguoiDungHetThiThangDuCham() {
        Match m = match(10, 55, 9, 30);
        assertEquals(Match.WIN, m.getResult(HUNG));
        assertEquals(Match.LOSE, m.getResult(HOANG));
    }

    @Test
    public void caHaiCoCauSaiThiHoa() {
        Match m = match(9, 20, 7, 50);
        assertEquals(Match.DRAW, m.getResult(HUNG));
        assertEquals(Match.DRAW, m.getResult(HOANG));
    }

    @Test
    public void cungDungHetCungThoiGianThiHoa() {
        assertEquals(Match.DRAW, match(10, 40, 10, 40).getResult(HUNG));
    }

    @Test
    public void boTranThiThuaDuDangDungHet() {
        Match m = match(10, 20, 3, -1);
        m.getPlayers().get(0).setForfeited(true);
        assertEquals(Match.LOSE, m.getResult(HUNG));
        assertEquals(Match.WIN, m.getResult(HOANG));
    }

    @Test
    public void diemTranLa1VaNuaVa0() {
        assertEquals(1.0, match(10, 30, 10, 31).getPoints(HUNG), 0);
        assertEquals(0.0, match(10, 30, 10, 31).getPoints(HOANG), 0);
        assertEquals(0.5, match(8, 30, 9, 31).getPoints(HUNG), 0);
    }

    @Test
    public void isOverKhiCa2DaNopHoacCoNguoiBoTran() {
        assertFalse(match(10, 30, 0, -1).isOver());
        assertTrue(match(10, 30, 9, 40).isOver());
        Match m = match(0, -1, 0, -1);
        m.getPlayers().get(1).setForfeited(true);
        assertTrue(m.isOver());
    }

    @Test
    public void countCorrectChamTheoDapAnDung() {
        Match m = new Match();
        m.setQuestions(new ProblemDAO(new Random(37)).generateProblems(Match.QUESTION_COUNT));
        List<Answer> answers = new ArrayList<>();
        for (MathProblem p : m.getQuestions()) {
            answers.add(new Answer(p.getCorrectAnswer(), 2_000));
        }
        assertEquals(10, m.countCorrect(answers));
        answers.get(3).setSelectedOption(Answer.NONE);
        answers.get(7).setSelectedOption(answers.get(7).getSelectedOption() + 1);
        assertEquals(8, m.countCorrect(answers));
        assertFalse(answers.get(3).isAnswered());
        assertFalse(answers.get(7).isCorrect());
        assertTrue(answers.get(0).isCorrect());
        assertEquals(m.getQuestions().get(5), answers.get(5).getProblem());
        assertEquals(0, m.countCorrect(Arrays.asList(new Answer(), new Answer())));
    }

    @Test
    public void botGameDungCungLuat() {
        BotGame game = new BotGame(new Player(HUNG, "hung", "HUNG"), BotGame.MEDIUM);
        game.setCorrectCount(10);
        game.setFinishTime(40_000);
        game.setBotCorrectCount(10);
        game.setBotFinishTime(45_000);
        assertEquals(Match.WIN, game.getResult());
        game.setCorrectCount(9);
        assertEquals(Match.LOSE, game.getResult());
        game.setBotCorrectCount(8);
        assertEquals(Match.DRAW, game.getResult());
    }

    @Test
    public void doiThuLaNguoiConLaiTrongTran() {
        Match m = match(10, 38.2, 10, 45.6);
        assertEquals(HOANG, m.getOpponent(HUNG).getPlayer().getId());
        assertEquals(HUNG, m.getOpponent(HOANG).getPlayer().getId());
    }
}
