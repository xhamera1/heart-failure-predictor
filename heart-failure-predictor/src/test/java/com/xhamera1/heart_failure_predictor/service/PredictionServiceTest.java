package com.xhamera1.heart_failure_predictor.service;

import com.xhamera1.heart_failure_predictor.dto.PredictionRecordDto;
import com.xhamera1.heart_failure_predictor.dto.PredictionRequestDto;
import com.xhamera1.heart_failure_predictor.dto.PredictionResponseDto;
import com.xhamera1.heart_failure_predictor.exceptions.PredictionApiException;
import com.xhamera1.heart_failure_predictor.model.PredictionRecord;
import com.xhamera1.heart_failure_predictor.model.User;
import com.xhamera1.heart_failure_predictor.model.enums.*;
import com.xhamera1.heart_failure_predictor.repository.PredictionRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PredictionServiceTest {

    @Mock
    private PredictionRecordRepository predictionRecordRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PredictionService predictionService;

    private PredictionRequestDto requestDto;
    private PredictionResponseDto responseDto;
    private User user;
    private PredictionRecord predictionRecord;
    private final String testApiUrl = "http://test.url/predict";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(predictionService, "predictionApiUrl", testApiUrl);

        requestDto = new PredictionRequestDto(
                55, 1, 0, 140, 250, 0, 1, 150, 1, 1.5, 1
        );

        responseDto = new PredictionResponseDto(
                1, 0.08, 0.92
        );

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        predictionRecord = new PredictionRecord();
        predictionRecord.setId(101L);
        predictionRecord.setUser(user);
        predictionRecord.setAge(requestDto.getAge());
        predictionRecord.setSex(SexEnum.fromNumericValue(requestDto.getSex()));
        predictionRecord.setChestPainType(ChestPainTypeEnum.fromNumericValue(requestDto.getChestPainType()));
        predictionRecord.setRestingBP(requestDto.getRestingBP());
        predictionRecord.setCholesterol(requestDto.getCholesterol());
        predictionRecord.setFastingBS(requestDto.getFastingBS() == 1);
        predictionRecord.setRestingECG(RestingECGEnum.fromNumericValue(requestDto.getRestingECG()));
        predictionRecord.setMaxHR(requestDto.getMaxHR());
        predictionRecord.setExerciseAngina(ExerciseAnginaEnum.fromNumericValue(requestDto.getExerciseAngina()));
        predictionRecord.setOldpeak(requestDto.getOldPeak());
        predictionRecord.setStSlope(STSlopeEnum.fromNumericValue(requestDto.getStSlope()));
        predictionRecord.setPredictedHeartDisease(responseDto.getPrediction() == 1);
        predictionRecord.setPredictionTimestamp(LocalDateTime.now());
    }

    @Test
    @DisplayName("getPredictionFromApi should return PredictionResponseDto on success")
    void getPredictionFromApi_Success() {
        when(restTemplate.postForObject(eq(testApiUrl), eq(requestDto), eq(PredictionResponseDto.class)))
                .thenReturn(responseDto);

        PredictionResponseDto actualResponse = predictionService.getPredictionFromApi(requestDto);

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getPrediction()).isEqualTo(responseDto.getPrediction());
        assertThat(actualResponse.getProbability_1()).isEqualTo(responseDto.getProbability_1());
        verify(restTemplate, times(1)).postForObject(testApiUrl, requestDto, PredictionResponseDto.class);
    }

    @Test
    @DisplayName("getPredictionFromApi should throw PredictionApiException when API returns null")
    void getPredictionFromApi_ApiReturnsNull_ThrowsException() {
        when(restTemplate.postForObject(eq(testApiUrl), eq(requestDto), eq(PredictionResponseDto.class)))
                .thenReturn(null);

        assertThatThrownBy(() -> predictionService.getPredictionFromApi(requestDto))
                .isInstanceOf(PredictionApiException.class)
                .hasMessageContaining("Received null response");
    }


    @Test
    @DisplayName("savePredictionRecord should map data correctly and save")
    void savePredictionRecord_ValidData_SavesRecord() {
        ArgumentCaptor<PredictionRecord> recordCaptor = ArgumentCaptor.forClass(PredictionRecord.class);
        when(predictionRecordRepository.save(recordCaptor.capture())).thenAnswer(invocation -> {
            PredictionRecord captured = invocation.getArgument(0);
            captured.setId(101L);
            captured.setPredictionTimestamp(LocalDateTime.now());
            return captured;
        });

        PredictionRecord savedRecord = predictionService.savePredictionRecord(requestDto, responseDto, user);

        assertThat(savedRecord).isNotNull();
        assertThat(savedRecord.getId()).isEqualTo(101L);

        verify(predictionRecordRepository, times(1)).save(any(PredictionRecord.class));

        PredictionRecord capturedRecord = recordCaptor.getValue();
        assertThat(capturedRecord.getUser()).isEqualTo(user);
        assertThat(capturedRecord.getAge()).isEqualTo(requestDto.getAge());
        assertThat(capturedRecord.getSex()).isEqualTo(SexEnum.fromNumericValue(requestDto.getSex()));
        assertThat(capturedRecord.getChestPainType()).isEqualTo(ChestPainTypeEnum.fromNumericValue(requestDto.getChestPainType()));
        assertThat(capturedRecord.getRestingBP()).isEqualTo(requestDto.getRestingBP());
        assertThat(capturedRecord.getCholesterol()).isEqualTo(requestDto.getCholesterol());
        assertThat(capturedRecord.getFastingBS()).isEqualTo(requestDto.getFastingBS() == 1);
        assertThat(capturedRecord.getRestingECG()).isEqualTo(RestingECGEnum.fromNumericValue(requestDto.getRestingECG()));
        assertThat(capturedRecord.getMaxHR()).isEqualTo(requestDto.getMaxHR());
        assertThat(capturedRecord.getExerciseAngina()).isEqualTo(ExerciseAnginaEnum.fromNumericValue(requestDto.getExerciseAngina()));
        assertThat(capturedRecord.getOldpeak()).isEqualTo(requestDto.getOldPeak());
        assertThat(capturedRecord.getStSlope()).isEqualTo(STSlopeEnum.fromNumericValue(requestDto.getStSlope()));
        assertThat(capturedRecord.getPredictedHeartDisease()).isEqualTo(responseDto.getPrediction() == 1);
        assertThat(capturedRecord.getPredictionTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("getPredictionRecordHistoryForUser should return mapped DTO list when records exist")
    void getPredictionRecordHistoryForUser_RecordsExist_ReturnsDtoList() {
        when(predictionRecordRepository.findByUserOrderByIdDesc(user)).thenReturn(List.of(predictionRecord));

        List<PredictionRecordDto> resultList = predictionService.getPredictionRecordHistoryForUser(user);

        assertThat(resultList).isNotNull().hasSize(1);
        PredictionRecordDto resultDto = resultList.get(0);
        assertThat(resultDto.getId()).isEqualTo(predictionRecord.getId());
        assertThat(resultDto.getAge()).isEqualTo(predictionRecord.getAge());
        assertThat(resultDto.getPredictedOutcome()).isEqualTo(predictionRecord.getPredictedHeartDisease());
        assertThat(resultDto.getSex()).isEqualTo(predictionRecord.getSex().getDisplayName());
        assertThat(resultDto.getChestPainType()).isEqualTo(predictionRecord.getChestPainType().getDisplayName());
        assertThat(resultDto.getRestingECG()).isEqualTo(predictionRecord.getRestingECG().getDisplayName());
        assertThat(resultDto.getExerciseAngina()).isEqualTo(predictionRecord.getExerciseAngina().getDisplayName());
        assertThat(resultDto.getStSlope()).isEqualTo(predictionRecord.getStSlope().getDisplayName());

        verify(predictionRecordRepository, times(1)).findByUserOrderByIdDesc(user);
    }

    @Test
    @DisplayName("getPredictionRecordHistoryForUser should return empty list when no records exist")
    void getPredictionRecordHistoryForUser_NoRecords_ReturnsEmptyList() {
        when(predictionRecordRepository.findByUserOrderByIdDesc(user)).thenReturn(Collections.emptyList());

        List<PredictionRecordDto> resultList = predictionService.getPredictionRecordHistoryForUser(user);

        assertThat(resultList).isNotNull().isEmpty();
        verify(predictionRecordRepository, times(1)).findByUserOrderByIdDesc(user);
    }
}