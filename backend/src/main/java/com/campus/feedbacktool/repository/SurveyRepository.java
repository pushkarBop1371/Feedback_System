package com.campus.feedbacktool.repository;

import com.campus.feedbacktool.entity.Survey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyRepository extends JpaRepository<Survey, Long> {

    // Backs the "filtering on at least one list view" requirement:
    // GET /api/surveys?title=xyz does a case-insensitive partial match.
    Page<Survey> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
