package com.xhamera1.heart_failure_predictor.controller;

import com.xhamera1.heart_failure_predictor.dto.PredictionFormDto; // Zaimportuj DTO
import com.xhamera1.heart_failure_predictor.dto.PredictionRequestDto;
import com.xhamera1.heart_failure_predictor.dto.PredictionResponseDto;
import com.xhamera1.heart_failure_predictor.dto.UserDto;
import com.xhamera1.heart_failure_predictor.exceptions.PredictionApiException;
import com.xhamera1.heart_failure_predictor.exceptions.ResourceNotFoundException;
import com.xhamera1.heart_failure_predictor.model.User;
import com.xhamera1.heart_failure_predictor.model.enums.*;
import com.xhamera1.heart_failure_predictor.repository.PredictionRecordRepository;
import com.xhamera1.heart_failure_predictor.repository.UserRepository;
import com.xhamera1.heart_failure_predictor.service.PredictionService;
import com.xhamera1.heart_failure_predictor.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;                                         // Import Logger
import org.slf4j.LoggerFactory;                                  // Import LoggerFactory
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;                               // Zaimportuj Model
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.security.core.Authentication;

import java.util.Optional;

@Controller
public class PredictionController {

    private static final Logger log = LoggerFactory.getLogger(PredictionController.class);
    private final PredictionService predictionService;
    private final UserService userService;

    @Autowired
    public PredictionController(PredictionService predictionService, UserService userService) {
        this.predictionService = predictionService;
        this.userService = userService;
    }


    @GetMapping("/predict")
    public String showPredictionForm(Model model) {
        log.debug("Displaying prediction form.");
        if (!model.containsAttribute("predictionFormData")) {
            model.addAttribute("predictionFormData", new PredictionFormDto());
        }
        return "prediction-form";
    }

    @PostMapping("/predict")
    public String handlePrediction(
            @Valid @ModelAttribute("predictionFormData") PredictionFormDto formData,
            BindingResult bindingResult,
            Authentication authentication,
            Model model
    ) {
        log.info("Processing prediction request for user: {}", authentication.getName());
        String username = authentication.getName();
        if (bindingResult.hasErrors()) {
            log.warn("Prediction form validation failed for user {}: {}", authentication.getName(), bindingResult.getAllErrors());
            return "prediction-form";
        }

        if (!authentication.isAuthenticated()) {
            log.warn("Unauthenticated user tried to access POST /predict");
            model.addAttribute("predictionError", "You must be logged to process prediction");
            return "prediction-form";
        }

        User currentUserEntity;
        try {
            currentUserEntity = userService.findUserEntityByUsername(username);
        }
        catch (ResourceNotFoundException e) {
            log.error("Authenticated user '{}' not found in database during prediction.", username, e);
            model.addAttribute("predictionError", "Błąd: Nie można znaleźć danych zalogowanego użytkownika.");
            return "prediction-form";
        }

        PredictionRequestDto requestDto;
        PredictionResponseDto predictionResult;

        try {
            requestDto = mapFormToRequestDto(formData);
            predictionResult = predictionService.getPredictionFromApi(requestDto);
            predictionService.savePredictionRecord(requestDto, predictionResult, currentUserEntity);

            model.addAttribute("predictionResult", predictionResult);
            log.info("Prediction successful for user {}. Result: {}", username, predictionResult.getPrediction());

            return "prediction-form";
        }
        catch (IllegalArgumentException e) {
            log.warn("Invalid form input value for user {}: {}", username, e.getMessage());
            return "prediction-form";
        } catch (PredictionApiException e) {
            log.error("Prediction API call failed for user {}: {}", username, e.getMessage(), e);
            return "prediction-form";
        } catch (Exception e) {
            log.error("Unexpected error during prediction processing for user {}: {}", username, e.getMessage(), e);
            return "prediction-form";
        }
    }


    private PredictionRequestDto mapFormToRequestDto(PredictionFormDto formData) {
        PredictionRequestDto requestDto = new PredictionRequestDto();

        requestDto.setAge(formData.getAge());
        requestDto.setRestingBP(formData.getRestingBP());
        requestDto.setCholesterol(formData.getCholesterol());
        requestDto.setFastingBS(formData.getFastingBS());
        requestDto.setMaxHR(formData.getMaxHR());
        requestDto.setOldPeak(formData.getOldpeak());

        try {
            requestDto.setSex(SexEnum.fromCode(formData.getSex()).getNumericValue());
            requestDto.setChestPainType(ChestPainTypeEnum.fromCode(formData.getChestPainType()).getNumericValue());
            requestDto.setRestingECG(RestingECGEnum.fromCode(formData.getRestingECG()).getNumericValue());
            requestDto.setExerciseAngina(formData.getExerciseAngina());
            requestDto.setStSlope(STSlopeEnum.fromCode(formData.getStSlope()).getNumericValue());
        } catch (NullPointerException | IllegalArgumentException e) {
            log.error("Error mapping enum value from form DTO: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid category value provided in the form.", e);
        }

        return requestDto;
    }


}