package ptit.e23cnpm01.nguyenbahung.math_dash.entity;

import java.util.Date;

/**
 * Kết quả một ván offline đã kết thúc.
 */
public class GameRecord {
    private int id;
    private int difficulty;
    private int score;
    private int maxStreak;
    private int correctCount;
    private int wrongCount;
    private Date playedAt;
    private Player player;

    public GameRecord() {
    }

    // Tỉ lệ trả lời đúng, từ 0 đến 1
    public double getAccuracy() {
        int total = correctCount + wrongCount;
        return total == 0 ? 0 : (double) correctCount / total;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getMaxStreak() {
        return maxStreak;
    }

    public void setMaxStreak(int maxStreak) {
        this.maxStreak = maxStreak;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(int correctCount) {
        this.correctCount = correctCount;
    }

    public int getWrongCount() {
        return wrongCount;
    }

    public void setWrongCount(int wrongCount) {
        this.wrongCount = wrongCount;
    }

    public Date getPlayedAt() {
        return playedAt;
    }

    public void setPlayedAt(Date playedAt) {
        this.playedAt = playedAt;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
