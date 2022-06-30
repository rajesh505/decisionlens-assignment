package com.decisionlens.assignment.errorhandling;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

@Data
@Builder
public class ErrorResponse {

    private String message;

    @Singular
    private List<String> details;

}
