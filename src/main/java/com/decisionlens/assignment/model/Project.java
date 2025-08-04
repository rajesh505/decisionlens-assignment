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
@Table(name = "projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Project name is required")
    @Column(nullable = false)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @NotNull(message = "Project score is required")
    @DecimalMin(value = "0.0", message = "Score must be at least 0")
    @DecimalMax(value = "100.0", message = "Score must be at most 100")
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal score;
    
    @NotNull(message = "Project cost is required")
    @DecimalMin(value = "0.0", message = "Cost must be at least 0")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal cost;
    
    @NotNull(message = "Project priority is required")
    @Min(value = 1, message = "Priority must be at least 1")
    @Max(value = 10, message = "Priority must be at most 10")
    @Column(nullable = false)
    private Integer priority;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectCategory category;
    
    @Column(name = "is_mission_critical")
    @Builder.Default
    private Boolean isMissionCritical = false;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "funded_at")
    private LocalDateTime fundedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = ProjectStatus.PENDING;
        }
        if (category == null) {
            category = ProjectCategory.GENERAL;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum ProjectStatus {
        PENDING, FUNDED, REJECTED, ON_HOLD, DEPRIORITIZED
    }
    
    public enum ProjectCategory {
        GENERAL, INFRASTRUCTURE, RESEARCH, DEVELOPMENT, MAINTENANCE, INNOVATION
    }
}