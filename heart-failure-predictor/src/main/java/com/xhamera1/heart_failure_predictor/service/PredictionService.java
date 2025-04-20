package com.xhamera1.heart_failure_predictor.service;

import com.xhamera1.heart_failure_predictor.dto.PredictionRecordDto;
import com.xhamera1.heart_failure_predictor.dto.PredictionRequestDto;
import com.xhamera1.heart_failure_predictor.dto.PredictionResponseDto;
import com.xhamera1.heart_failure_predictor.exceptions.PredictionApiException;
import com.xhamera1.heart_failure_predictor.exceptions.ResourceAlreadyExistsException;
import com.xhamera1.heart_failure_predictor.model.PredictionRecord;
import com.xhamera1.heart_failure_predictor.model.User;
import com.xhamera1.heart_failure_predictor.model.enums.*;
import com.xhamera1.heart_failure_predictor.repository.PredictionRecordRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PredictionService {

    private static final Logger log = LoggerFactory.getLogger(PredictionService.class);
    private final PredictionRecordRepository predictionRecordRepository;
    private final RestTemplate restTemplate;
    private final String predictionApiUrl;


    @Autowired
    public PredictionService(PredictionRecordRepository predictionRecordRepository, RestTemplate restTemplate, @Value("${prediction.api.url}") String predictionApiUrl) {
        this.predictionRecordRepository = predictionRecordRepository;
        this.restTemplate = restTemplate;
        this.predictionApiUrl = predictionApiUrl;
    }

    public PredictionResponseDto getPredictionFromApi(PredictionRequestDto predictionRequestDto) {
        log.debug("Sending prediction request to API {}", predictionApiUrl);
        log.debug("Request DTO {}", predictionRequestDto);
        try {
            PredictionResponseDto responseDto = restTemplate.postForObject(predictionApiUrl, predictionRequestDto, PredictionResponseDto.class);
            if (responseDto == null) {
                throw new PredictionApiException("Received null response from prediction API.");
            }
            log.info("Received prediction response from API.");
            log.debug("Response DTO: {}", responseDto);
            return responseDto;
        } catch (RestClientException e) {
            log.error("Error calling prediction API at {}: {}", predictionApiUrl, e.getMessage());
            throw new PredictionApiException("Failed to get prediction from external API : " + e.getMessage());
        }
    }


    @Transactional
    public PredictionRecord savePredictionRecord(PredictionRequestDto requestData, PredictionResponseDto predictionResult, User user) {
        PredictionRecord newRecord = new PredictionRecord();

        newRecord.setUser(user);
        newRecord.setPredictionTimestamp(LocalDateTime.now());

        newRecord.setAge(requestData.getAge());
        newRecord.setSex(SexEnum.fromNumericValue(requestData.getSex()));
        newRecord.setChestPainType(ChestPainTypeEnum.fromNumericValue(requestData.getChestPainType()));
        newRecord.setRestingBP(requestData.getRestingBP());
        newRecord.setCholesterol(requestData.getCholesterol());
        newRecord.setFastingBS(requestData.getFastingBS() != null && requestData.getFastingBS() == 1);
        newRecord.setRestingECG(RestingECGEnum.fromNumericValue(requestData.getRestingECG()));
        newRecord.setMaxHR(requestData.getMaxHR());
        newRecord.setExerciseAngina(ExerciseAnginaEnum.fromNumericValue(requestData.getExerciseAngina()));
        newRecord.setOldpeak(requestData.getOldPeak());
        newRecord.setStSlope(STSlopeEnum.fromNumericValue(requestData.getStSlope()));

        newRecord.setPredictedHeartDisease(predictionResult.getPrediction() != null && predictionResult.getPrediction() == 1);

        PredictionRecord savedRecord = predictionRecordRepository.save(newRecord);
        log.info("Prediction record saved with ID: {}", savedRecord.getId());
        return savedRecord;
    }

    @Transactional(readOnly = true)
    public List<PredictionRecordDto> getPredictionRecordHistoryForUser(User user) {
        log.debug("Retrieving prediction history for user ID: {}", user.getId());

        List<PredictionRecord> predictionRecords = predictionRecordRepository.findByUserOrderByIdDesc(user);

        log.info("Found {} prediction records for user ID: {}", predictionRecords.size(), user.getId());

        List<PredictionRecordDto> predictionRecordDtoList = predictionRecords.stream()
                .map(this::mapPredictionRecordToDto)
                .collect(Collectors.toList());

        return predictionRecordDtoList;
    }


    private PredictionRecordDto mapPredictionRecordToDto(PredictionRecord predictionRecord) {
        if (predictionRecord == null) {
            return null;
        }
        PredictionRecordDto dto = new PredictionRecordDto();

        dto.setId(predictionRecord.getId());
        dto.setPredictionTimestamp(predictionRecord.getPredictionTimestamp());
        dto.setPredictedOutcome(predictionRecord.getPredictedHeartDisease());
        dto.setAge(predictionRecord.getAge());
        dto.setRestingBP(predictionRecord.getRestingBP());
        dto.setCholesterol(predictionRecord.getCholesterol());
        dto.setFastingBS(predictionRecord.getFastingBS());
        dto.setMaxHR(predictionRecord.getMaxHR());
        dto.setOldpeak(predictionRecord.getOldpeak());

        if (predictionRecord.getSex() != null) {
            dto.setSex(predictionRecord.getSex().getDisplayName());
        }
        if (predictionRecord.getChestPainType() != null) {
            dto.setChestPainType(predictionRecord.getChestPainType().getDisplayName());
        }
        if (predictionRecord.getRestingECG() != null) {
            dto.setRestingECG(predictionRecord.getRestingECG().getDisplayName());
        }
        if (predictionRecord.getExerciseAngina() != null) {
            dto.setExerciseAngina(predictionRecord.getExerciseAngina().getDisplayName());
        }
        if (predictionRecord.getStSlope() != null) {
            dto.setStSlope(predictionRecord.getStSlope().getDisplayName());
        }

        return dto;
    }


}
