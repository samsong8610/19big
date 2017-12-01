package net.cmlzw.nineteen.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;

@Entity
public class Question {
    public static final String DELIMITOR = ",";

    @Id
    @GeneratedValue
    Long id;
    @Column(nullable = false)
    String title;
    @JsonIgnore
//    @Column(columnDefinition = "varchar(255)")
//    @Convert(converter = StringListConverter.class)
//    List<String> answers;
    @Column(nullable = false)
    String answers;
    @Column(nullable = false)
    String answer;

    public Question() {}
    public Question(String title, String[] answers, String answer) {
        this.title = title;
        this.answers = String.join(DELIMITOR, answers);
//        this.answers = Arrays.asList(answers);
        this.answer = answer;
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

    @JsonGetter("answers")
    public List<String> getAnswersList() {
        return Arrays.asList(answers.split(DELIMITOR));
//        return answers;
    }

    public String getAnswers() {
        return answers;
    }

//    public void setAnswers(String[] answers) {
//        setAnswers(Arrays.asList(answers));
//    }

    @JsonSetter
    public void setAnswers(List<String> answers) {
        this.answers = String.join(DELIMITOR, answers);
//        this.answers = answers;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
