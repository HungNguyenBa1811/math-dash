package ptit.e23cnpm01.nguyenbahung.math_dash.dao;

import java.util.List;

import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Player;

public interface PlayerListCallback {
    void onPlayersChanged(List<Player> players);
}
