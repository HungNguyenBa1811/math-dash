package ptit.e23cnpm01.nguyenbahung.math_dash;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Friendship;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Player;

public class FriendshipTest {
    private static final int HUNG = 1;
    private static final int HOANG = 2;

    private Friendship hungGuiHoang() {
        return new Friendship(new Player(HUNG, "hung", "HUNG"), new Player(HOANG, "hoang", "HOANG"));
    }

    @Test
    public void banCuaNguoiGuiLaNguoiNhanVaNguocLai() {
        Friendship f = hungGuiHoang();
        assertEquals(HOANG, f.getFriend(HUNG).getId());
        assertEquals(HUNG, f.getFriend(HOANG).getId());
    }

    @Test
    public void chiNguoiNhanDongYDuocLoiMoiDangCho() {
        Friendship f = hungGuiHoang();
        assertTrue(f.canAccept(HOANG));
        assertFalse(f.canAccept(HUNG));
        f.setStatus(Friendship.ACCEPTED);
        assertFalse(f.canAccept(HOANG));
    }
}
