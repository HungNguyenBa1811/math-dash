package ptit.e23cnpm01.nguyenbahung.mathdash.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MatchPlayer {
    private int id;
    // -1 là câu chưa chọn; không lưu CSDL
    private List<Integer> answers = new ArrayList<>();
    private int correctCount;
    private Date submittedAt;
    private boolean forfeited;
    private Player player;

    public MatchPlayer() {
    }

    public MatchPlayer(Player player) {
        this.player = player;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Integer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Integer> answers) {
        this.answers = answers;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(int correctCount) {
        this.correctCount = correctCount;
    }

    public Date getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Date submittedAt) {
        this.submittedAt = submittedAt;
    }

    public boolean isForfeited() {
        return forfeited;
    }

    public void setForfeited(boolean forfeited) {
        this.forfeited = forfeited;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
