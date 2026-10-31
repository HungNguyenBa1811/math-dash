package ptit.e23cnpm01.nguyenbahung.math_dash.dao;

import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Match;

public interface MatchCallback {
    void onSuccess(Match match);

    void onNextMatch(int nextMatchId);

    void onStopped(int playerId);

    void onFail(String message);
}
