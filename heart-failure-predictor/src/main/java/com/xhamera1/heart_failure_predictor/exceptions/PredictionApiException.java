package com.xhamera1.heart_failure_predictor.exceptions;

public class PredictionApiException extends RuntimeException{

    public PredictionApiException(String message) {
        super(message);
    }
}
