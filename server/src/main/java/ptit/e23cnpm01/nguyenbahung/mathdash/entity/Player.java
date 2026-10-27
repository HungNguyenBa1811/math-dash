package ptit.e23cnpm01.nguyenbahung.mathdash.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

public class Player {
    public static final String ROLE_PLAYER = "player";
    public static final String ROLE_ADMIN = "admin";
    public static final String FREE = "free";
    public static final String BUSY = "busy";

    private int id;
    private String username;
    private String passwordHash;
    private String displayName;
    private String role = ROLE_PLAYER;
    private Date createdAt;
    // Các thuộc tính dẫn xuất, không có cột trong CSDL: server tính rồi gán vào để gửi cho app
    private double totalPoints;
    private double avgOpponentPoints;
    private double avgWinTime;
    // Số trận thắng / hòa / thua qua các trận đã xong (UC06), cũng là dẫn xuất
    private int winCount;
    private int drawCount;
    private int loseCount;
    // Trạng thái khi online (free / busy), chỉ nằm trên Firebase
    private String status;

    public Player() {
    }

    public Player(int id, String username, String displayName) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonIgnore
    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public double getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(double totalPoints) {
        this.totalPoints = totalPoints;
    }

    public double getAvgOpponentPoints() {
        return avgOpponentPoints;
    }

    public void setAvgOpponentPoints(double avgOpponentPoints) {
        this.avgOpponentPoints = avgOpponentPoints;
    }

    public double getAvgWinTime() {
        return avgWinTime;
    }

    public void setAvgWinTime(double avgWinTime) {
        this.avgWinTime = avgWinTime;
    }

    public int getWinCount() {
        return winCount;
    }

    public void setWinCount(int winCount) {
        this.winCount = winCount;
    }

    public int getDrawCount() {
        return drawCount;
    }

    public void setDrawCount(int drawCount) {
        this.drawCount = drawCount;
    }

    public int getLoseCount() {
        return loseCount;
    }

    public void setLoseCount(int loseCount) {
        this.loseCount = loseCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
