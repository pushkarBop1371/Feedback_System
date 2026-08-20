package com.campus.feedbacktool.repository;

import com.campus.feedbacktool.entity.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResponseRepository extends JpaRepository<Response, Long> {

    // Listing view of child records for a given parent: GET /surveys/:id/responses
    Page<Response> findBySurveyId(Long surveyId, Pageable pageable);

    // Used by the stats/edge-case calculation, which needs every answer, not a page.
    List<Response> findBySurveyId(Long surveyId);

    long countBySurveyId(Long surveyId);
}
