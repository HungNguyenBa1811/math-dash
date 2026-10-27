package ptit.e23cnpm01.nguyenbahung.mathdash.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Match {
    public static final String WIN = "WIN";
    public static final String LOSE = "LOSE";
    public static final String DRAW = "DRAW";
    public static final String PLAYING = "playing";
    public static final String FINISHED = "finished";
    public static final int QUESTION_COUNT = 10;

    private int id;
    private String status;
    private Date startedAt;
    private Date endedAt;
    private List<MathProblem> questions = new ArrayList<>();
    private List<MatchPlayer> players = new ArrayList<>();

    // Gắn câu thứ i của đề vào bài làm câu thứ i rồi đếm số câu đúng
    public int countCorrect(List<Answer> answers) {
        int count = 0;
        for (int i = 0; i < questions.size() && i < answers.size(); i++) {
            answers.get(i).setProblem(questions.get(i));
            if (answers.get(i).isCorrect()) {
                count++;
            }
        }
        return count;
    }

    @JsonIgnore
    public boolean isOver() {
        int submitted = 0;
        for (MatchPlayer mp : players) {
            if (mp.isForfeited()) {
                return true;
            }
            if (mp.getSubmittedAt() != null) {
                submitted++;
            }
        }
        return players.size() == 2 && submitted == 2;
    }

    // Chưa nộp thì coi như chậm nhất
    public long getFinishTime(int playerId) {
        MatchPlayer mp = find(playerId, true);
        if (mp.getSubmittedAt() == null) {
            return Long.MAX_VALUE;
        }
        return mp.getSubmittedAt().getTime() - startedAt.getTime();
    }

    public String getResult(int playerId) {
        MatchPlayer me = find(playerId, true);
        MatchPlayer opponent = find(playerId, false);
        if (me.isForfeited()) {
            return LOSE;
        }
        if (opponent.isForfeited()) {
            return WIN;
        }
        return judge(me.getCorrectCount(), getFinishTime(playerId),
                opponent.getCorrectCount(), getFinishTime(opponent.getPlayer().getId()));
    }

    public double getPoints(int playerId) {
        String result = getResult(playerId);
        return WIN.equals(result) ? 1 : DRAW.equals(result) ? 0.5 : 0;
    }

    public MatchPlayer getOpponent(int playerId) {
        return find(playerId, false);
    }

    // Luật thắng thua của đề, dùng chung cho chơi với máy
    public static String judge(int myCorrect, long myTime, int otherCorrect, long otherTime) {
        boolean meAll = myCorrect == QUESTION_COUNT;
        boolean otherAll = otherCorrect == QUESTION_COUNT;
        if (meAll != otherAll) {
            return meAll ? WIN : LOSE;
        }
        if (!meAll || myTime == otherTime) {
            return DRAW;
        }
        return myTime < otherTime ? WIN : LOSE;
    }

    // mine = false: lấy đối thủ của playerId
    private MatchPlayer find(int playerId, boolean mine) {
        for (MatchPlayer mp : players) {
            if ((mp.getPlayer().getId() == playerId) == mine) {
                return mp;
            }
        }
        throw new IllegalArgumentException("Người chơi " + playerId + " không có trong trận " + id);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Date startedAt) {
        this.startedAt = startedAt;
    }

    public Date getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(Date endedAt) {
        this.endedAt = endedAt;
    }

    public List<MathProblem> getQuestions() {
        return questions;
    }

    public void setQuestions(List<MathProblem> questions) {
        this.questions = questions;
    }

    public List<MatchPlayer> getPlayers() {
        return players;
    }

    public void setPlayers(List<MatchPlayer> players) {
        this.players = players;
    }
}
