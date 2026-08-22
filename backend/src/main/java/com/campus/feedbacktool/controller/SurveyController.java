package com.campus.feedbacktool.controller;

import com.campus.feedbacktool.dto.*;
import com.campus.feedbacktool.service.ResponseService;
import com.campus.feedbacktool.service.SurveyService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/surveys")
public class SurveyController {

    private final SurveyService surveyService;
    private final ResponseService responseService;

    public SurveyController(SurveyService surveyService, ResponseService responseService) {
        this.surveyService = surveyService;
        this.responseService = responseService;
    }

    // GET /api/surveys?page=0&size=10&title=onboarding
    @GetMapping
    public PageResponse<SurveyDto> list(
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<SurveyDto> result = surveyService.list(title, pageable);
        return new PageResponse<>(result.getContent(), result);
    }

    @GetMapping("/{id}")
    public SurveyDto getById(@PathVariable Long id) {
        return surveyService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SurveyDto create(@Valid @RequestBody SurveyRequest request) {
        return surveyService.create(request);
    }

    @PutMapping("/{id}")
    public SurveyDto update(@PathVariable Long id, @Valid @RequestBody SurveyRequest request) {
        return surveyService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        surveyService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Listing view of child records for a given parent.
    // GET /api/surveys/{id}/responses?page=0&size=10
    @GetMapping("/{id}/responses")
    public PageResponse<ResponseDto> listResponses(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<ResponseDto> result = responseService.listBySurvey(id, pageable);
        return new PageResponse<>(result.getContent(), result);
    }

    // Edge case: aggregate stats (count, average where numeric) per survey.
    @GetMapping("/{id}/stats")
    public SurveyStatsDto getStats(@PathVariable Long id) {
        return surveyService.getStats(id);
    }
}
