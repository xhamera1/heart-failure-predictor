package com.xhamera1.heart_failure_predictor.controller;

import com.xhamera1.heart_failure_predictor.dto.PredictionRecordDto;
import com.xhamera1.heart_failure_predictor.exceptions.ResourceNotFoundException;
import com.xhamera1.heart_failure_predictor.service.PredictionService;
import com.xhamera1.heart_failure_predictor.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.List;

@Controller
public class HistoryController {

    private static final Logger log = LoggerFactory.getLogger(HistoryController.class);
    private final PredictionService predictionService;

    @Autowired
    public HistoryController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @GetMapping("/history")
    public String showHistory(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Unauthenticated access attempt to /history");
            return "redirect:/auth/login";
        }

        String username = authentication.getName();
        log.debug("Showing prediction history for user: {}", username);
        List<PredictionRecordDto> predictionHistoryList = Collections.emptyList();

        try {
            predictionHistoryList = predictionService.getPredictionRecordHistoryForUserByUsername(username);
            model.addAttribute("predictionHistory", predictionHistoryList);
        } catch (ResourceNotFoundException e) {
            log.error("Authenticated user '{}' not found when retrieving history.", username, e);
            model.addAttribute("historyError", "Error: not able to find user");
            model.addAttribute("predictionHistory", predictionHistoryList);
        } catch (Exception e) {
            log.error("Error retrieving prediction history for user {}: {}", username, e.getMessage(), e);
            model.addAttribute("historyError", "Unexpected error while trying to get user history");
            model.addAttribute("predictionHistory", predictionHistoryList);
        }
        return "history";
    }
}