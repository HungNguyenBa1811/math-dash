package ptit.e23cnpm01.nguyenbahung.math_dash.entity;

import java.io.Serializable;

/**
 * Entity một bản ghi kết quả ván offline, dùng cho Bảng vàng.
 */
public class GameRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int playerId;
    private String playerName;
    private int score;
    private int maxStreak;
    private int totalCorrect;
    private int totalWrong;
    private String playedAt;

    // TODO: constructor rỗng + constructor đầy đủ tham số
    // TODO: getter / setter
}
