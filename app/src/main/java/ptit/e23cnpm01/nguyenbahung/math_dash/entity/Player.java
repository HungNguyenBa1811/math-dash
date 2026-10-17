package ptit.e23cnpm01.nguyenbahung.math_dash.entity;

import java.io.Serializable;

/**
 * Entity người chơi.
 * Serializable để truyền qua Intent giữa các Activity.
 */
public class Player implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String username;
    private int highScore;
    private String createdAt;

    // TODO: constructor rỗng + constructor đầy đủ tham số
    // TODO: getter / setter
}
