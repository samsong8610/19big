package net.cmlzw.nineteen.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class QuizArchive {
    @Id
    @GeneratedValue
    Long id;
    int level;
    String username;
    Long organizationId;
    String phone;
    int score;
    @Column(columnDefinition = "date")
    Date created;
    Date archived;

    public QuizArchive(Quiz quiz) {
        level = quiz.getLevel();
        username = quiz.getUsername();
        organizationId = quiz.getOrganizationId();
        phone = quiz.getPhone();
        score = quiz.getScore();
        created = quiz.getCreated();
        archived = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getArchived() {
        return archived;
    }

    public void setArchived(Date archived) {
        this.archived = archived;
    }
}
