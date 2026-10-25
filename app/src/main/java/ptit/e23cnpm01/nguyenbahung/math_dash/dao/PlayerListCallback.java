package ptit.e23cnpm01.nguyenbahung.math_dash.dao;

import java.util.List;

import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Player;

/**
 * Danh sách người chơi online thay đổi (có người vào, ra, đổi trạng thái, đổi tổng điểm).
 */
public interface PlayerListCallback {
    void onPlayersChanged(List<Player> players);
}
