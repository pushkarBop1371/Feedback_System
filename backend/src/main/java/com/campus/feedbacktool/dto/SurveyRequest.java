package com.campus.feedbacktool.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SurveyRequest {

    @NotBlank(message = "title is required")
    @Size(max = 150, message = "title must be at most 150 characters")
    private String title;

    @NotBlank(message = "question is required")
    @Size(max = 500, message = "question must be at most 500 characters")
    private String question;

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
}
