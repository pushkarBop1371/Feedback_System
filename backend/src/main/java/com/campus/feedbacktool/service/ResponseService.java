package com.campus.feedbacktool.service;

import com.campus.feedbacktool.dto.ResponseDto;
import com.campus.feedbacktool.dto.ResponseRequest;
import com.campus.feedbacktool.entity.Response;
import com.campus.feedbacktool.entity.Survey;
import com.campus.feedbacktool.exception.ResourceNotFoundException;
import com.campus.feedbacktool.repository.ResponseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ResponseService {

    private final ResponseRepository responseRepository;
    private final SurveyService surveyService;

    public ResponseService(ResponseRepository responseRepository, SurveyService surveyService) {
        this.responseRepository = responseRepository;
        this.surveyService = surveyService;
    }

    public Page<ResponseDto> list(Pageable pageable) {
        return responseRepository.findAll(pageable).map(this::toDto);
    }

    public Page<ResponseDto> listBySurvey(Long surveyId, Pageable pageable) {
        // Ensures a 404 (not an empty page) when the survey itself doesn't exist.
        surveyService.findEntity(surveyId);
        return responseRepository.findBySurveyId(surveyId, pageable).map(this::toDto);
    }

    public ResponseDto getById(Long id) {
        return toDto(findEntity(id));
    }

    public ResponseDto create(ResponseRequest request) {
        Survey survey = surveyService.findEntity(request.getSurveyId());
        Response response = new Response(request.getRespondentName().trim(), request.getAnswer().trim(), survey);
        return toDto(responseRepository.save(response));
    }

    public ResponseDto update(Long id, ResponseRequest request) {
        Response response = findEntity(id);
        // Allow moving a response to a different survey if the caller asks for it.
        if (!response.getSurvey().getId().equals(request.getSurveyId())) {
            Survey survey = surveyService.findEntity(request.getSurveyId());
            response.setSurvey(survey);
        }
        response.setRespondentName(request.getRespondentName().trim());
        response.setAnswer(request.getAnswer().trim());
        return toDto(responseRepository.save(response));
    }

    public void delete(Long id) {
        Response response = findEntity(id);
        responseRepository.delete(response);
    }

    private Response findEntity(Long id) {
        return responseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Response not found with id " + id));
    }

    private ResponseDto toDto(Response response) {
        return new ResponseDto(
                response.getId(),
                response.getRespondentName(),
                response.getAnswer(),
                response.getSubmittedDate(),
                response.getSurvey().getId(),
                response.getSurvey().getTitle()
        );
    }
}
