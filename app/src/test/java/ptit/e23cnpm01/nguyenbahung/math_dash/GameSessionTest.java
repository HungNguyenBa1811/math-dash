package ptit.e23cnpm01.nguyenbahung.math_dash;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ptit.e23cnpm01.nguyenbahung.math_dash.entity.GameSession;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Player;

/**
 * Luật điểm, mạng và mức của GameSession.
 */
public class GameSessionTest {
    private final Player player = new Player(1, "hung", "HUNG");

    @Test
    public void trangThaiBanDau() {
        GameSession session = new GameSession(player, 1, false);
        assertEquals(0, session.getScore());
        assertEquals(0, session.getCurrentStreak());
        assertEquals(GameSession.MAX_LIVES, session.getLivesLeft());
        assertFalse(session.isGameOver());
    }

    @Test
    public void dungLienTiepDuocCongTheoChuoiDung() {
        GameSession session = new GameSession(player, 1, false);
        assertEquals(12, session.recordCorrectAnswer());
        assertEquals(14, session.recordCorrectAnswer());
        assertEquals(26, session.getScore());
        assertEquals(2, session.getMaxStreak());
    }

    @Test
    public void saiThiChuoiDungVe0VaMatMang() {
        GameSession session = new GameSession(player, 1, false);
        session.recordCorrectAnswer();
        session.recordWrongAnswer();
        assertEquals(0, session.getCurrentStreak());
        assertEquals(GameSession.MAX_LIVES - 1, session.getLivesLeft());
        assertEquals(12, session.recordCorrectAnswer());
    }

    @Test
    public void sai3LanThiKetThuc() {
        GameSession session = new GameSession(player, 1, false);
        session.recordWrongAnswer();
        session.recordWrongAnswer();
        assertFalse(session.isGameOver());
        session.recordWrongAnswer();
        assertTrue(session.isGameOver());
    }

    @Test
    public void onlineSaiKhongMatMang() {
        GameSession session = new GameSession(player, 1, true);
        for (int i = 0; i < 5; i++) {
            session.recordWrongAnswer();
        }
        assertEquals(GameSession.MAX_LIVES, session.getLivesLeft());
        assertFalse(session.isGameOver());
        assertEquals(5, session.getWrongCount());
    }

    @Test
    public void mucBatDauTheoDoKho() {
        assertEquals(1, new GameSession(player, 1, false).getCurrentLevel());
        assertEquals(2, new GameSession(player, 2, false).getCurrentLevel());
        assertEquals(3, new GameSession(player, 3, false).getCurrentLevel());
    }

    @Test
    public void cu5CauTang1MucToiDaMuc4() {
        GameSession session = new GameSession(player, 3, true);
        for (int i = 0; i < 5; i++) {
            session.recordCorrectAnswer();
        }
        assertEquals(4, session.getCurrentLevel());
        for (int i = 0; i < 10; i++) {
            session.recordWrongAnswer();
        }
        assertEquals(GameSession.MAX_LEVEL, session.getCurrentLevel());
    }
}
