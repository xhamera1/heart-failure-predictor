package com.xhamera1.heart_failure_predictor.repository;

import com.xhamera1.heart_failure_predictor.model.PredictionRecord;
import com.xhamera1.heart_failure_predictor.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PredictionRecordRepository extends JpaRepository<PredictionRecord, Long> {

    List<PredictionRecord> findByUserOrderByIdDesc(User user);

}
