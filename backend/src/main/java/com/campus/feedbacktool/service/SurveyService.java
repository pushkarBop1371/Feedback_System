package com.campus.feedbacktool.service;

import com.campus.feedbacktool.dto.SurveyDto;
import com.campus.feedbacktool.dto.SurveyRequest;
import com.campus.feedbacktool.dto.SurveyStatsDto;
import com.campus.feedbacktool.entity.Response;
import com.campus.feedbacktool.entity.Survey;
import com.campus.feedbacktool.exception.ResourceNotFoundException;
import com.campus.feedbacktool.repository.ResponseRepository;
import com.campus.feedbacktool.repository.SurveyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@Transactional
public class SurveyService {

    private final SurveyRepository surveyRepository;
    private final ResponseRepository responseRepository;

    public SurveyService(SurveyRepository surveyRepository, ResponseRepository responseRepository) {
        this.surveyRepository = surveyRepository;
        this.responseRepository = responseRepository;
    }

    public Page<SurveyDto> list(String titleFilter, Pageable pageable) {
        Page<Survey> page = (titleFilter == null || titleFilter.isBlank())
                ? surveyRepository.findAll(pageable)
                : surveyRepository.findByTitleContainingIgnoreCase(titleFilter.trim(), pageable);
        return page.map(this::toDto);
    }

    public SurveyDto getById(Long id) {
        return toDto(findEntity(id));
    }

    public SurveyDto create(SurveyRequest request) {
        Survey survey = new Survey(request.getTitle().trim(), request.getQuestion().trim());
        return toDto(surveyRepository.save(survey));
    }

    public SurveyDto update(Long id, SurveyRequest request) {
        Survey survey = findEntity(id);
        survey.setTitle(request.getTitle().trim());
        survey.setQuestion(request.getQuestion().trim());
        return toDto(surveyRepository.save(survey));
    }

    public void delete(Long id) {
        Survey survey = findEntity(id);
        surveyRepository.delete(survey);
    }

    /**
     * Edge case required by the brief: compute basic aggregate stats
     * (count, average where numeric) for a survey's responses.
     * Answers that don't parse as a number are counted but excluded
     * from the average/min/max.
     */
    public SurveyStatsDto getStats(Long id) {
        Survey survey = findEntity(id);
        List<Response> responses = responseRepository.findBySurveyId(id);

        long total = responses.size();
        double sum = 0;
        long numericCount = 0;
        Double min = null;
        Double max = null;

        for (Response response : responses) {
            Optional<Double> numeric = parseNumeric(response.getAnswer());
            if (numeric.isPresent()) {
                double value = numeric.get();
                sum += value;
                numericCount++;
                min = (min == null) ? value : Math.min(min, value);
                max = (max == null) ? value : Math.max(max, value);
            }
        }

        Double average = numericCount > 0 ? sum / numericCount : null;

        return new SurveyStatsDto(
                survey.getId(),
                survey.getTitle(),
                total,
                numericCount,
                total - numericCount,
                average,
                min,
                max
        );
    }

    private Optional<Double> parseNumeric(String rawAnswer) {
        if (rawAnswer == null) {
            return Optional.empty();
        }
        String trimmed = rawAnswer.trim().toLowerCase(Locale.ROOT);
        try {
            return Optional.of(Double.parseDouble(trimmed));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    Survey findEntity(Long id) {
        return surveyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found with id " + id));
    }

    private SurveyDto toDto(Survey survey) {
        long count = responseRepository.countBySurveyId(survey.getId());
        return new SurveyDto(survey.getId(), survey.getTitle(), survey.getQuestion(),
                survey.getCreatedDate(), count);
    }
}
