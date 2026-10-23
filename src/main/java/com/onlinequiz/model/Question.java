package com.onlinequiz.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "questiontable")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String question;
    private String a;
    private String b;
    private String c;
    private String d;
    private String answer;

    public Question() {}

    public Question(String question, String a, String b, String c, String d, String answer) {
        this.question = question;
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.answer = answer;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public String getA() { return a; }
    public void setA(String a) { this.a = a; }
    public String getB() { return b; }
    public void setB(String b) { this.b = b; }
    public String getC() { return c; }
    public void setC(String c) { this.c = c; }
    public String getD() { return d; }
    public void setD(String d) { this.d = d; }
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
}
