package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;
import ptit.e23cnpm01.nguyenbahung.math_dash.controller.GameController;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.GameSession;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.MathProblem;

/**
 * Màn hình chơi offline.
 */
public class GameActivity extends AppCompatActivity implements GameController.GameControllerListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        // TODO
    }

    @Override
    public void onProblemLoaded(MathProblem problem) {
        // TODO
    }

    @Override
    public void onScoreUpdated(int score, int streak, int highScore) {
        // TODO
    }

    @Override
    public void onLivesUpdated(int currentLives, int maxLives) {
        // TODO
    }

    @Override
    public void onTimerTick(long millisLeft, long totalMillis) {
        // TODO
    }

    @Override
    public void onFeedback(boolean isCorrect, String message) {
        // TODO
    }

    @Override
    public void onGameOver(GameSession session, boolean isNewRecord) {
        // TODO
    }
}
