package ptit.e23cnpm01.nguyenbahung.mathdash.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Luật của quan hệ bạn bè: ai là bạn của ai (getFriend), ai được đồng ý lời mời (canAccept).
 */
class FriendshipTest {
    private static final int HUNG = 1;
    private static final int HOANG = 2;

    private Friendship hungGuiHoang() {
        return new Friendship(new Player(HUNG, "hung", "HUNG"), new Player(HOANG, "hoang", "HOANG"));
    }

    @Test
    void banCuaNguoiGuiLaNguoiNhanVaNguocLai() {
        Friendship f = hungGuiHoang();
        assertEquals(HOANG, f.getFriend(HUNG).getId());
        assertEquals(HUNG, f.getFriend(HOANG).getId());
    }

    @Test
    void chiNguoiNhanDongYDuocLoiMoiDangCho() {
        Friendship f = hungGuiHoang();
        assertTrue(f.canAccept(HOANG));
        assertFalse(f.canAccept(HUNG));
        f.setStatus(Friendship.ACCEPTED);
        assertFalse(f.canAccept(HOANG));
    }
}
