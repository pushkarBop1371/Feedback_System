package com.campus.feedbacktool.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ResponseRequest {

    @NotBlank(message = "respondentName is required")
    @Size(max = 120, message = "respondentName must be at most 120 characters")
    private String respondentName;

    @NotBlank(message = "answer is required")
    @Size(max = 1000, message = "answer must be at most 1000 characters")
    private String answer;

    @NotNull(message = "surveyId is required")
    private Long surveyId;

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

    public Long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }
}
