package com.decisionlens.assignment.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "workflow_rules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowRule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id", nullable = false)
    private Workflow workflow;
    
    @NotBlank(message = "Rule name is required")
    @Column(nullable = false)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RuleType type;
    
    @Column(name = "rule_condition", length = 2000)
    private String condition;
    
    @Column(name = "rule_action", length = 2000)
    private String action;
    
    @Column(name = "execution_order", nullable = false)
    private Integer executionOrder;
    
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum RuleType {
        SCORE_THRESHOLD,
        COST_LIMIT,
        PRIORITY_BASED,
        MISSION_CRITICAL_OVERRIDE,
        BUDGET_CONSTRAINT,
        CATEGORY_PREFERENCE,
        TIME_BASED,
        CUSTOM_LOGIC
    }
}