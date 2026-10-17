package ptit.e23cnpm01.nguyenbahung.math_dash.controller;

import ptit.e23cnpm01.nguyenbahung.math_dash.entity.GameSession;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.MathProblem;

/**
 * Điều khiển ván chơi offline: đếm giờ, chấm điểm, báo kết quả cho giao diện.
 */
public class GameController {

    // Giao diện (Activity) implement interface này để nhận cập nhật
    public interface GameControllerListener {
        void onProblemLoaded(MathProblem problem);
        void onScoreUpdated(int score, int streak, int highScore);
        void onLivesUpdated(int currentLives, int maxLives);
        void onTimerTick(long millisLeft, long totalMillis);
        void onFeedback(boolean isCorrect, String message);
        void onGameOver(GameSession session, boolean isNewRecord);
    }

    public static final long QUESTION_DURATION_MS = 6000L;

    // TODO: constructor + các field cần thiết

    public void startGame() {
        // TODO
    }

    public void submitAnswer(int candidate) {
        // TODO
    }

    public void onPause() {
        // TODO
    }

    public void onResume() {
        // TODO
    }

    public void onDestroy() {
        // TODO
    }
}
