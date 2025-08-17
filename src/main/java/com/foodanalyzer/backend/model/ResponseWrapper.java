package com.foodanalyzer.backend.model;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseWrapper {
    private boolean success;
    private String message;
    private String accessToken;

    public ResponseWrapper(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ResponseWrapper(boolean success, String message, String accessToken) {
        this.success = success;
        this.message = message;
        this.accessToken = accessToken;
    }
}