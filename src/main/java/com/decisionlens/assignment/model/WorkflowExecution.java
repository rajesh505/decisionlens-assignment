package com.decisionlens.assignment.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "workflow_executions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowExecution {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id", nullable = false)
    private Workflow workflow;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExecutionStatus status;
    
    @Column(name = "total_budget", precision = 15, scale = 2)
    private BigDecimal totalBudget;
    
    @Column(name = "allocated_budget", precision = 15, scale = 2)
    private BigDecimal allocatedBudget;
    
    @Column(name = "remaining_budget", precision = 15, scale = 2)
    private BigDecimal remainingBudget;
    
    @Column(name = "projects_processed")
    private Integer projectsProcessed;
    
    @Column(name = "projects_funded")
    private Integer projectsFunded;
    
    @Column(name = "projects_rejected")
    private Integer projectsRejected;
    
    @Column(name = "projects_deprioritized")
    private Integer projectsDeprioritized;
    
    @Column(name = "execution_time_ms")
    private Long executionTimeMs;
    
    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "error_message", length = 2000)
    private String errorMessage;
    
    @OneToMany(mappedBy = "workflowExecution", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WorkflowExecutionResult> results;
    
    @PrePersist
    protected void onCreate() {
        startedAt = LocalDateTime.now();
        if (status == null) {
            status = ExecutionStatus.RUNNING;
        }
    }
    
    public enum ExecutionStatus {
        RUNNING, COMPLETED, FAILED, CANCELLED
    }
}