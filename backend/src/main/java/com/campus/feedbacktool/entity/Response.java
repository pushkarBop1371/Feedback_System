package com.campus.feedbacktool.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Child entity: one respondent's answer to a Survey's question.
 * submittedDate is stamped automatically the moment the response is persisted.
 */
@Entity
@Table(name = "survey_response")
public class Response {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "respondentName is required")
    @Size(max = 120, message = "respondentName must be at most 120 characters")
    @Column(nullable = false, length = 120)
    private String respondentName;

    @NotBlank(message = "answer is required")
    @Size(max = 1000, message = "answer must be at most 1000 characters")
    @Column(nullable = false, length = 1000)
    private String answer;

    @Column(nullable = false, updatable = false)
    private LocalDate submittedDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "survey_id", nullable = false)
    @JsonIgnore
    private Survey survey;

    @PrePersist
    protected void onCreate() {
        this.submittedDate = LocalDate.now();
    }

    public Response() {
    }

    public Response(String respondentName, String answer, Survey survey) {
        this.respondentName = respondentName;
        this.answer = answer;
        this.survey = survey;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRespondentName() {
        return respondentName;
    }

    public void setRespondentName(String respondentName) {
        this.respondentName = respondentName;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public LocalDate getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(LocalDate submittedDate) {
        this.submittedDate = submittedDate;
    }

    public Survey getSurvey() {
        return survey;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }
}
