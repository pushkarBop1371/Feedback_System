package com.campus.feedbacktool.dto;

import java.time.LocalDate;

public class SurveyDto {

    private Long id;
    private String title;
    private String question;
    private LocalDate createdDate;
    private long responseCount;

    public SurveyDto() {
    }

    public SurveyDto(Long id, String title, String question, LocalDate createdDate, long responseCount) {
        this.id = id;
        this.title = title;
        this.question = question;
        this.createdDate = createdDate;
        this.responseCount = responseCount;
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

    public long getResponseCount() {
        return responseCount;
    }

    public void setResponseCount(long responseCount) {
        this.responseCount = responseCount;
    }
}
