package ptit.e23cnpm01.nguyenbahung.math_dash.dao;

import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Challenge;

/**
 * Lời mời thách đấu thay đổi: có lời mời mới, hoặc lời mời đã gửi được đồng ý / từ chối.
 */
public interface ChallengeCallback {
    void onChallengeChanged(Challenge challenge);
}
