package ptit.e23cnpm01.nguyenbahung.math_dash;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Match;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.MatchPlayer;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Player;

/**
 * Luật vào phòng Match.canJoin.
 */
public class MatchTest {
    private static final int HUNG = 1;
    private static final int HOANG = 2;
    private static final int MINH = 3;

    // Trận mã 482915 do Hùng tạo, trạng thái cho trước
    private Match match(String status) {
        Match match = new Match();
        match.setRoomCode("482915");
        match.setStatus(status);
        match.getPlayers().add(new MatchPlayer(new Player(HUNG, "hung", "HUNG")));
        return match;
    }

    @Test
    public void phongChoMotNguoiKhac() {
        assertTrue(match(Match.WAITING).canJoin(HOANG));
    }

    @Test
    public void tranDangDau() {
        assertFalse(match(Match.PLAYING).canJoin(HOANG));
    }

    @Test
    public void phongDaDu2Nguoi() {
        Match match = match(Match.WAITING);
        match.getPlayers().add(new MatchPlayer(new Player(HOANG, "hoang", "HOANG")));
        assertFalse(match.canJoin(MINH));
    }

    @Test
    public void phongCuaChinhMinh() {
        assertFalse(match(Match.WAITING).canJoin(HUNG));
    }
}
