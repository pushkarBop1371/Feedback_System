package com.campus.feedbacktool.config;

import com.campus.feedbacktool.entity.AppUser;
import com.campus.feedbacktool.entity.Response;
import com.campus.feedbacktool.entity.Survey;
import com.campus.feedbacktool.repository.AppUserRepository;
import com.campus.feedbacktool.repository.ResponseRepository;
import com.campus.feedbacktool.repository.SurveyRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seeds one admin login plus a couple of sample surveys/responses on
 * startup purely so the app is easy to demo / walk through. Safe to delete -
 * each block only runs when its table is empty.
 *
 * Demo admin login (CHANGE/REMOVE before any real deployment):
 *   admin / admin123
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final SurveyRepository surveyRepository;
    private final ResponseRepository responseRepository;
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(SurveyRepository surveyRepository, ResponseRepository responseRepository,
                       AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.surveyRepository = surveyRepository;
        this.responseRepository = responseRepository;
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedAdmin();
        seedSurveys();
    }

    private void seedAdmin() {
        if (appUserRepository.count() > 0) {
            return;
        }
        appUserRepository.save(new AppUser("admin", passwordEncoder.encode("admin123")));
    }

    private void seedSurveys() {
        if (surveyRepository.count() > 0) {
            return;
        }

        Survey onboarding = surveyRepository.save(
                new Survey("Onboarding Experience", "On a scale of 1-10, how smooth was your onboarding?"));
        responseRepository.save(new Response("Asha Rao", "9", onboarding));
        responseRepository.save(new Response("Vikram Shah", "7", onboarding));
        responseRepository.save(new Response("Meera Iyer", "Pretty smooth, loved the buddy system", onboarding));

        Survey cafeteria = surveyRepository.save(
                new Survey("Cafeteria Feedback", "How would you rate today's lunch out of 5?"));
        responseRepository.save(new Response("Karan Mehta", "4", cafeteria));
        responseRepository.save(new Response("Divya Nair", "3", cafeteria));
    }
}
