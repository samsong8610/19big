package net.cmlzw.nineteen.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class QuestionLevel {
    @Id
    @GeneratedValue
    Long id;
    @Column(nullable = false)
    String title;
    int level;
    boolean active;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "question_level_id")
    List<Question> questions;
    @Column(nullable = false)
    Date created;

    public QuestionLevel() {
        this.questions = new ArrayList<>();
        created = new Date();
    }
    public QuestionLevel(String title, int level, boolean active) {
        this();
        this.title = title;
        this.level = level;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void addQuestion(Question question) {
        if (question != null) {
            this.questions.add(question);
        }
    }

    public void clearQuestions() {
        this.questions.clear();
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
