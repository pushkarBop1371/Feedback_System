package com.campus.feedbacktool.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Parent entity: a Survey has a title and a single question that every
 * Response answers. createdDate is stamped automatically the moment the
 * survey is persisted.
 */
@Entity
@Table(name = "survey")
public class Survey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "title is required")
    @Size(max = 150, message = "title must be at most 150 characters")
    @Column(nullable = false, length = 150)
    private String title;

    @NotBlank(message = "question is required")
    @Size(max = 500, message = "question must be at most 500 characters")
    @Column(nullable = false, length = 500)
    private String question;

    @Column(nullable = false, updatable = false)
    private LocalDate createdDate;

    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Response> responses = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDate.now();
    }

    public Survey() {
    }

    public Survey(String title, String question) {
        this.title = title;
        this.question = question;
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

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public List<Response> getResponses() {
        return responses;
    }

    public void setResponses(List<Response> responses) {
        this.responses = responses;
    }
}
