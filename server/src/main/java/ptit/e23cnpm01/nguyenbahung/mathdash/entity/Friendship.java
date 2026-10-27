package ptit.e23cnpm01.nguyenbahung.mathdash.entity;

import java.util.Date;

public class Friendship {
    public static final String PENDING = "pending";
    public static final String ACCEPTED = "accepted";

    private int id;
    private String status;
    private Date createdAt;
    private Player sender;
    private Player receiver;

    public Friendship() {
    }

    public Friendship(Player sender, Player receiver) {
        this.sender = sender;
        this.receiver = receiver;
        this.status = PENDING;
    }

    public Player getFriend(int playerId) {
        return sender.getId() == playerId ? receiver : sender;
    }

    public boolean canAccept(int playerId) {
        return PENDING.equals(status) && receiver.getId() == playerId;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Player getSender() {
        return sender;
    }

    public void setSender(Player sender) {
        this.sender = sender;
    }

    public Player getReceiver() {
        return receiver;
    }

    public void setReceiver(Player receiver) {
        this.receiver = receiver;
    }
}
