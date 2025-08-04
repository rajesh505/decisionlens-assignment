package com.decisionlens.assignment.controller;

import com.decisionlens.assignment.dto.WorkflowExecutionRequest;
import com.decisionlens.assignment.dto.WorkflowExecutionResponse;
import com.decisionlens.assignment.model.Workflow;
import com.decisionlens.assignment.repo.WorkflowExecutionRepository;
import com.decisionlens.assignment.repo.WorkflowRepository;
import com.decisionlens.assignment.service.WorkflowEngineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/workflows")
@RequiredArgsConstructor
public class WorkflowController {
    
    private final WorkflowRepository workflowRepository;
    private final WorkflowExecutionRepository workflowExecutionRepository;
    private final WorkflowEngineService workflowEngineService;
    
    @GetMapping
    public ResponseEntity<List<Workflow>> getAllWorkflows() {
        List<Workflow> workflows = workflowRepository.findAll();
        return ResponseEntity.ok(workflows);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Workflow> getWorkflow(@PathVariable Long id) {
        Workflow workflow = workflowRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workflow not found"));
        return ResponseEntity.ok(workflow);
    }
    
    @PostMapping
    public ResponseEntity<Workflow> createWorkflow(@Valid @RequestBody Workflow workflow) {
        workflow = workflowRepository.save(workflow);
        return ResponseEntity.ok(workflow);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Workflow> updateWorkflow(@PathVariable Long id, @Valid @RequestBody Workflow workflow) {
        Workflow existingWorkflow = workflowRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workflow not found"));
        
        existingWorkflow.setName(workflow.getName());
        existingWorkflow.setDescription(workflow.getDescription());
        existingWorkflow.setType(workflow.getType());
        existingWorkflow.setIsActive(workflow.getIsActive());
        
        existingWorkflow = workflowRepository.save(existingWorkflow);
        return ResponseEntity.ok(existingWorkflow);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkflow(@PathVariable Long id) {
        workflowRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Workflow>> getWorkflowsByType(@PathVariable String type) {
        Workflow.WorkflowType workflowType = Workflow.WorkflowType.valueOf(type.toUpperCase());
        List<Workflow> workflows = workflowRepository.findByType(workflowType);
        return ResponseEntity.ok(workflows);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<Workflow>> getActiveWorkflows() {
        List<Workflow> workflows = workflowRepository.findByIsActive(true);
        return ResponseEntity.ok(workflows);
    }
    
    @PostMapping("/execute")
    public ResponseEntity<WorkflowExecutionResponse> executeWorkflow(@Valid @RequestBody WorkflowExecutionRequest request) {
        WorkflowExecutionResponse response = workflowEngineService.executeWorkflow(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/executions")
    public ResponseEntity<List<WorkflowExecutionResponse>> getWorkflowExecutions() {
        List<WorkflowExecutionResponse> executions = workflowExecutionRepository.findCompletedExecutions().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(executions);
    }
    
    @GetMapping("/{workflowId}/executions")
    public ResponseEntity<List<WorkflowExecutionResponse>> getWorkflowExecutions(@PathVariable Long workflowId) {
        List<WorkflowExecutionResponse> executions = workflowExecutionRepository.findRecentExecutionsByWorkflowId(workflowId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(executions);
    }
    
    private WorkflowExecutionResponse convertToResponse(com.decisionlens.assignment.model.WorkflowExecution execution) {
        return WorkflowExecutionResponse.builder()
                .executionId(execution.getId())
                .workflowName(execution.getWorkflow().getName())
                .status(execution.getStatus().name())
                .totalBudget(execution.getTotalBudget())
                .allocatedBudget(execution.getAllocatedBudget())
                .remainingBudget(execution.getRemainingBudget())
                .projectsProcessed(execution.getProjectsProcessed())
                .projectsFunded(execution.getProjectsFunded())
                .projectsRejected(execution.getProjectsRejected())
                .projectsDeprioritized(execution.getProjectsDeprioritized())
                .executionTimeMs(execution.getExecutionTimeMs())
                .startedAt(execution.getStartedAt())
                .completedAt(execution.getCompletedAt())
                .errorMessage(execution.getErrorMessage())
                .build();
    }
}