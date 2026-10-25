package ptit.e23cnpm01.nguyenbahung.math_dash.entity;

public class Player {
    public static final String ROLE_PLAYER = "player";
    public static final String ROLE_ADMIN = "admin";
    public static final String FREE = "free";
    public static final String BUSY = "busy";

    private int id;
    private String username;
    private String displayName;
    private String role = ROLE_PLAYER;
    // Các thuộc tính dẫn xuất do server tính, app chỉ hiển thị
    private double totalPoints;
    private double avgOpponentPoints;
    private double avgWinTime;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
