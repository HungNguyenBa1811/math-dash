package ptit.e23cnpm01.nguyenbahung.mathdash.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

/**
 * Người chơi. passwordHash chỉ dùng ở server, không bao giờ trả ra JSON.
 */
public class Player {
    private int id;
    private String username;
    private String passwordHash;
    private String displayName;
    private Date createdAt;

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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
