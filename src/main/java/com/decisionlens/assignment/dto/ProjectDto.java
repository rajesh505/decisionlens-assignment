package com.decisionlens.assignment.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectDto {
    
    private Long id;
    
    @NotBlank(message = "Project name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Project score is required")
    @DecimalMin(value = "0.0", message = "Score must be at least 0")
    @DecimalMax(value = "100.0", message = "Score must be at most 100")
    private BigDecimal score;
    
    @NotNull(message = "Project cost is required")
    @DecimalMin(value = "0.0", message = "Cost must be at least 0")
    private BigDecimal cost;
    
    @NotNull(message = "Project priority is required")
    @Min(value = 1, message = "Priority must be at least 1")
    @Max(value = 10, message = "Priority must be at most 10")
    private Integer priority;
    
    private String status;
    private String category;
    private Boolean isMissionCritical;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime fundedAt;
}