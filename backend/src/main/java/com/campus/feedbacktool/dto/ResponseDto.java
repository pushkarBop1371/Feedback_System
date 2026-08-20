package com.campus.feedbacktool.dto;

import java.time.LocalDate;

public class ResponseDto {

    private Long id;
    private String respondentName;
    private String answer;
    private LocalDate submittedDate;
    private Long surveyId;
    private String surveyTitle;

    public ResponseDto() {
    }

    public ResponseDto(Long id, String respondentName, String answer, LocalDate submittedDate,
                        Long surveyId, String surveyTitle) {
        this.id = id;
        this.respondentName = respondentName;
        this.answer = answer;
        this.submittedDate = submittedDate;
        this.surveyId = surveyId;
        this.surveyTitle = surveyTitle;
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

    public Long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public String getSurveyTitle() {
        return surveyTitle;
    }

    public void setSurveyTitle(String surveyTitle) {
        this.surveyTitle = surveyTitle;
    }
}
