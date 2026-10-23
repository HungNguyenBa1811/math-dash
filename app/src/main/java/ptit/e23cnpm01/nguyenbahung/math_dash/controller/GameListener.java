package ptit.e23cnpm01.nguyenbahung.math_dash.controller;

import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Match;

/**
 * MatchController báo diễn biến trận về OnlineGameActivity.
 */
public interface GameListener {
    void onMatchChanged(Match match);

    void onTick(int secondsLeft);

    void onTimeUp();

    void onMatchEnd(Match match);

    void onError(String message);
}
