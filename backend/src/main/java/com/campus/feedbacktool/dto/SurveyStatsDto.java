package com.campus.feedbacktool.dto;

public class SurveyStatsDto {

    private Long surveyId;
    private String surveyTitle;
    private long totalResponses;
    private long numericResponses;
    private long nonNumericResponses;
    private Double averageNumericAnswer;
    private Double minNumericAnswer;
    private Double maxNumericAnswer;

    public SurveyStatsDto() {
    }

    public SurveyStatsDto(Long surveyId, String surveyTitle, long totalResponses, long numericResponses,
                           long nonNumericResponses, Double averageNumericAnswer,
                           Double minNumericAnswer, Double maxNumericAnswer) {
        this.surveyId = surveyId;
        this.surveyTitle = surveyTitle;
        this.totalResponses = totalResponses;
        this.numericResponses = numericResponses;
        this.nonNumericResponses = nonNumericResponses;
        this.averageNumericAnswer = averageNumericAnswer;
        this.minNumericAnswer = minNumericAnswer;
        this.maxNumericAnswer = maxNumericAnswer;
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

    public long getTotalResponses() {
        return totalResponses;
    }

    public void setTotalResponses(long totalResponses) {
        this.totalResponses = totalResponses;
    }

    public long getNumericResponses() {
        return numericResponses;
    }

    public void setNumericResponses(long numericResponses) {
        this.numericResponses = numericResponses;
    }

    public long getNonNumericResponses() {
        return nonNumericResponses;
    }

    public void setNonNumericResponses(long nonNumericResponses) {
        this.nonNumericResponses = nonNumericResponses;
    }

    public Double getAverageNumericAnswer() {
        return averageNumericAnswer;
    }

    public void setAverageNumericAnswer(Double averageNumericAnswer) {
        this.averageNumericAnswer = averageNumericAnswer;
    }

    public Double getMinNumericAnswer() {
        return minNumericAnswer;
    }

    public void setMinNumericAnswer(Double minNumericAnswer) {
        this.minNumericAnswer = minNumericAnswer;
    }

    public Double getMaxNumericAnswer() {
        return maxNumericAnswer;
    }

    public void setMaxNumericAnswer(Double maxNumericAnswer) {
        this.maxNumericAnswer = maxNumericAnswer;
    }
}
