package com.decisionlens.assignment.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowExecutionRequest {
    
    @NotNull(message = "Workflow ID is required")
    private Long workflowId;
    
    @NotNull(message = "Total budget is required")
    @DecimalMin(value = "0.0", message = "Total budget must be at least 0")
    private BigDecimal totalBudget;
    
    private List<Long> projectIds;
    
    @Builder.Default
    private Boolean includeMissionCritical = true;
    
    @Builder.Default
    private Boolean allowDeprioritization = true;
    
    @Builder.Default
    private Boolean allowPartialFunding = false;
    
    private String executionNotes;
}