package ptit.e23cnpm01.nguyenbahung.math_dash.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Một ván đấu với máy. Lưu trên máy (SQLite), không tính vào tổng điểm.
 */
public class BotGame {
    public static final int EASY = 1;
    public static final int MEDIUM = 2;
    public static final int HARD = 3;

    private int id;
    private int difficulty;
    private int correctCount;
    private long finishTime;
    private int botCorrectCount;
    private long botFinishTime;
    private Date playedAt;
    private Player player;
    // Đề của ván, chỉ nằm trong bộ nhớ
    private List<MathProblem> questions = new ArrayList<>();

    public BotGame() {
    }

    public BotGame(Player player, int difficulty) {
        this.player = player;
        this.difficulty = difficulty;
    }

    // WIN / LOSE / DRAW của người chơi so với máy, cùng luật với trận đối kháng
    public String getResult() {
        return Match.judge(correctCount, finishTime, botCorrectCount, botFinishTime);
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

    public int getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(int correctCount) {
        this.correctCount = correctCount;
    }

    public long getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(long finishTime) {
        this.finishTime = finishTime;
    }

    public int getBotCorrectCount() {
        return botCorrectCount;
    }

    public void setBotCorrectCount(int botCorrectCount) {
        this.botCorrectCount = botCorrectCount;
    }

    public long getBotFinishTime() {
        return botFinishTime;
    }

    public void setBotFinishTime(long botFinishTime) {
        this.botFinishTime = botFinishTime;
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

    public List<MathProblem> getQuestions() {
        return questions;
    }

    public void setQuestions(List<MathProblem> questions) {
        this.questions = questions;
    }
}
