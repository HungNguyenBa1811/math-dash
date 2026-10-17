package ptit.e23cnpm01.nguyenbahung.math_dash.entity;

import java.io.Serializable;

/**
 * Trạng thái của một ván đang chơi: điểm, combo, mạng, câu hỏi hiện tại.
 * Luật điểm: đúng được BASE_POINTS + streak * COMBO_BONUS, sai thì reset combo và mất 1 mạng.
 */
public class GameSession implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int DEFAULT_MAX_LIVES = 3;
    private static final int BASE_POINTS = 10;
    private static final int COMBO_BONUS = 2;

    private Player player;
    private int score;
    private int streak;
    private int maxStreak;
    private int lives;
    private int maxLives;
    private int totalCorrect;
    private int totalWrong;
    private MathProblem currentProblem;
    private boolean isGameOver;

    // TODO: constructor
    // TODO: getter / setter

    // Đưa ván về trạng thái ban đầu
    public void reset() {
        // TODO
    }

    // Ghi nhận trả lời đúng, trả về số điểm vừa cộng
    public int recordCorrectAnswer() {
        // TODO
        return 0;
    }

    // Ghi nhận trả lời sai (hoặc hết giờ)
    public void recordWrongAnswer() {
        // TODO
    }
}
