package com.decisionlens.assignment.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowExecutionResponse {
    
    private Long executionId;
    private String workflowName;
    private String status;
    private BigDecimal totalBudget;
    private BigDecimal allocatedBudget;
    private BigDecimal remainingBudget;
    private Integer projectsProcessed;
    private Integer projectsFunded;
    private Integer projectsRejected;
    private Integer projectsDeprioritized;
    private Long executionTimeMs;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String errorMessage;
    private List<ProjectDecisionDto> decisions;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProjectDecisionDto {
        private Long projectId;
        private String projectName;
        private String action;
        private String originalStatus;
        private String newStatus;
        private BigDecimal allocatedAmount;
        private Integer priorityChange;
        private BigDecimal scoreChange;
        private String decisionReason;
        private String appliedRules;
    }
}