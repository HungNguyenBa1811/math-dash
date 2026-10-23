package ptit.e23cnpm01.nguyenbahung.mathdash.entity;

/**
 * Một người chơi trong một trận: điểm, đã bỏ trận chưa, máy đã hết giờ chưa.
 */
public class MatchPlayer {
    private int id;
    private int score;
    private boolean forfeited;
    private boolean timeUp;
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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isForfeited() {
        return forfeited;
    }

    public void setForfeited(boolean forfeited) {
        this.forfeited = forfeited;
    }

    public boolean isTimeUp() {
        return timeUp;
    }

    public void setTimeUp(boolean timeUp) {
        this.timeUp = timeUp;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
