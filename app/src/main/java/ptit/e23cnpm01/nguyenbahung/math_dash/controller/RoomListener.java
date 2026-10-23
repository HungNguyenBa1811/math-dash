package ptit.e23cnpm01.nguyenbahung.math_dash.controller;

import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Match;

/**
 * MatchController báo kết quả vào phòng về OnlineActivity, LobbyActivity.
 */
public interface RoomListener {
    void onRoomReady(Match match);

    void onError(String message);
}
