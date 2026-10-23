package ptit.e23cnpm01.nguyenbahung.math_dash.dao;

import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Match;

/**
 * Kết quả bất đồng bộ của MatchFirebaseDAO.
 */
public interface MatchCallback {
    void onSuccess(Match match);

    void onFail(String message);
}
