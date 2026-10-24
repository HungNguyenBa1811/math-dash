package ptit.e23cnpm01.nguyenbahung.mathdash.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Match {
    public static final String WIN = "WIN";
    public static final String LOSE = "LOSE";
    public static final String DRAW = "DRAW";

    private int id;
    private String roomCode;
    private String status;
    private long questionSeed;
    private Date createdAt;
    private Date endedAt;
    private List<MatchPlayer> players = new ArrayList<>();

    // Trận đã xong: có người bỏ trận, hoặc cả 2 người đã hết giờ
    @JsonIgnore
    public boolean isOver() {
        int timeUpCount = 0;
        for (MatchPlayer mp : players) {
            if (mp.isForfeited()) {
                return true;
            }
            if (mp.isTimeUp()) {
                timeUpCount++;
            }
        }
        return players.size() == 2 && timeUpCount == 2;
    }

    // Kết quả của playerId: ai bỏ trận thì thua, không ai bỏ thì so điểm
    public String getResult(int playerId) {
        MatchPlayer me = null;
        MatchPlayer opponent = null;
        for (MatchPlayer mp : players) {
            if (mp.getPlayer().getId() == playerId) {
                me = mp;
            } else {
                opponent = mp;
            }
        }
        if (me.isForfeited()) {
            return LOSE;
        }
        if (opponent.isForfeited()) {
            return WIN;
        }
        if (me.getScore() == opponent.getScore()) {
            return DRAW;
        }
        return me.getScore() > opponent.getScore() ? WIN : LOSE;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getQuestionSeed() {
        return questionSeed;
    }

    public void setQuestionSeed(long questionSeed) {
        this.questionSeed = questionSeed;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(Date endedAt) {
        this.endedAt = endedAt;
    }

    public List<MatchPlayer> getPlayers() {
        return players;
    }

    public void setPlayers(List<MatchPlayer> players) {
        this.players = players;
    }
}
