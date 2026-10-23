package ptit.e23cnpm01.nguyenbahung.math_dash.entity;

/**
 * Ván đang chơi, dùng chung cho chơi offline và chơi online.
 * Đúng: cộng 10 + chuỗi đúng × 2. Sai: chuỗi đúng về 0; chỉ chế độ offline mới mất mạng.
 */
public class GameSession {
    public static final int MAX_LIVES = 3;
    public static final int MAX_LEVEL = 4;
    private static final int BASE_POINTS = 10;
    private static final int STREAK_BONUS = 2;
    private static final int QUESTIONS_PER_LEVEL = 5;

    private int difficulty;
    private boolean onlineMode;
    private int questionsAsked;
    private int score;
    private int currentStreak;
    private int maxStreak;
    private int livesLeft = MAX_LIVES;
    private int correctCount;
    private int wrongCount;
    private boolean gameOver;
    private Player player;
    private GameConfig config;
    private MathProblem currentProblem;

    // difficulty: 1 = Dễ, 2 = Trung bình, 3 = Khó (trận online luôn là 1)
    public GameSession(Player player, int difficulty, boolean onlineMode) {
        this.player = player;
        this.difficulty = difficulty;
        this.onlineMode = onlineMode;
    }

    // Trả lời đúng: tăng chuỗi đúng, cộng điểm, trả về số điểm vừa cộng
    public int recordCorrectAnswer() {
        questionsAsked++;
        correctCount++;
        currentStreak++;
        maxStreak = Math.max(maxStreak, currentStreak);
        int points = BASE_POINTS + currentStreak * STREAK_BONUS;
        score += points;
        return points;
    }

    // Trả lời sai (hoặc hết giờ của câu): chuỗi đúng về 0; offline thì mất 1 mạng, hết mạng thì kết thúc
    public void recordWrongAnswer() {
        questionsAsked++;
        wrongCount++;
        currentStreak = 0;
        if (onlineMode) {
            return;
        }
        livesLeft--;
        if (livesLeft <= 0) {
            gameOver = true;
        }
    }

    // Mức hiện tại: bắt đầu từ độ khó, cứ 5 câu tăng 1 mức, tối đa mức 4
    public int getCurrentLevel() {
        return Math.min(difficulty + questionsAsked / QUESTIONS_PER_LEVEL, MAX_LEVEL);
    }

    public int getDifficulty() {
        return difficulty;
    }

    public boolean isOnlineMode() {
        return onlineMode;
    }

    public int getQuestionsAsked() {
        return questionsAsked;
    }

    public int getScore() {
        return score;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public int getMaxStreak() {
        return maxStreak;
    }

    public int getLivesLeft() {
        return livesLeft;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public int getWrongCount() {
        return wrongCount;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public Player getPlayer() {
        return player;
    }

    public GameConfig getConfig() {
        return config;
    }

    public void setConfig(GameConfig config) {
        this.config = config;
    }

    public MathProblem getCurrentProblem() {
        return currentProblem;
    }

    public void setCurrentProblem(MathProblem currentProblem) {
        this.currentProblem = currentProblem;
    }
}
