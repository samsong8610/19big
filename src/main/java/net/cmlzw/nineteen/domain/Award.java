package net.cmlzw.nineteen.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;
import java.util.Date;

@Entity
public class Award {
    @Id
    @GeneratedValue
    Long id;
    String nickname;
    String phone;
    int gift;
    Date created;
    boolean notified;
    @Version
    Date claimed;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getGift() {
        return gift;
    }

    public void setGift(int gift) {
        this.gift = gift;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public boolean isNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }

    public Date getClaimed() {
        return claimed;
    }

    public void setClaimed(Date claimed) {
        this.claimed = claimed;
    }
}
