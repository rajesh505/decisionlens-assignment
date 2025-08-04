package com.decisionlens.assignment.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "workflow_execution_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowExecutionResult {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_execution_id", nullable = true)
    private WorkflowExecution workflowExecution;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DecisionAction action;
    
    @Column(name = "original_status")
    @Enumerated(EnumType.STRING)
    private Project.ProjectStatus originalStatus;
    
    @Column(name = "new_status")
    @Enumerated(EnumType.STRING)
    private Project.ProjectStatus newStatus;
    
    @Column(name = "allocated_amount", precision = 15, scale = 2)
    private BigDecimal allocatedAmount;
    
    @Column(name = "priority_change")
    private Integer priorityChange;
    
    @Column(name = "score_change", precision = 5, scale = 2)
    private BigDecimal scoreChange;
    
    @Column(name = "decision_reason", length = 1000)
    private String decisionReason;
    
    @Column(name = "applied_rules", length = 2000)
    private String appliedRules;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public enum DecisionAction {
        FUND, REJECT, DEPRIORITIZE, HOLD, MODIFY_PRIORITY, MODIFY_SCORE, ALLOCATE_PARTIAL
    }
}