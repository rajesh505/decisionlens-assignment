package com.decisionlens.assignment.service;

import com.decisionlens.assignment.dto.WorkflowExecutionRequest;
import com.decisionlens.assignment.dto.WorkflowExecutionResponse;
import com.decisionlens.assignment.model.*;
import com.decisionlens.assignment.repo.ProjectRepository;
import com.decisionlens.assignment.repo.WorkflowExecutionRepository;
import com.decisionlens.assignment.repo.WorkflowExecutionResultRepository;
import com.decisionlens.assignment.repo.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkflowEngineService {
    
    private final WorkflowRepository workflowRepository;
    private final ProjectRepository projectRepository;
    private final WorkflowExecutionRepository workflowExecutionRepository;
    private final WorkflowExecutionResultRepository workflowExecutionResultRepository;
    
    public WorkflowExecutionResponse executeWorkflow(WorkflowExecutionRequest request) {
        long startTime = System.currentTimeMillis();
        
        Workflow workflow = workflowRepository.findById(request.getWorkflowId())
                .orElseThrow(() -> new RuntimeException("Workflow not found"));
        
        WorkflowExecution execution = WorkflowExecution.builder()
                .workflow(workflow)
                .status(WorkflowExecution.ExecutionStatus.RUNNING)
                .totalBudget(request.getTotalBudget())
                .startedAt(LocalDateTime.now())
                .build();
        
        execution = workflowExecutionRepository.save(execution);
        
        try {
            List<Project> projects = getProjectsForExecution(request);
            List<WorkflowExecutionResult> results = new ArrayList<>();
            
            BigDecimal remainingBudget = request.getTotalBudget();
            int projectsFunded = 0;
            int projectsRejected = 0;
            int projectsDeprioritized = 0;
            
            switch (workflow.getType()) {
                case FUNDING_PRIORITIZATION:
                    results = executeFundingPrioritization(projects, remainingBudget, request);
                    break;
                case MISSION_CRITICAL_ACCOMMODATION:
                    results = executeMissionCriticalAccommodation(projects, remainingBudget, request);
                    break;
                case BUDGET_REALLOCATION:
                    results = executeBudgetReallocation(projects, remainingBudget, request);
                    break;
                case PROJECT_DEPRIORITIZATION:
                    results = executeProjectDeprioritization(projects, remainingBudget, request);
                    break;
                case EMERGENCY_FUNDING:
                    results = executeEmergencyFunding(projects, remainingBudget, request);
                    break;
            }
            
            // Calculate statistics
            for (WorkflowExecutionResult result : results) {
                switch (result.getAction()) {
                    case FUND:
                        projectsFunded++;
                        remainingBudget = remainingBudget.subtract(result.getAllocatedAmount());
                        break;
                    case REJECT:
                        projectsRejected++;
                        break;
                    case DEPRIORITIZE:
                        projectsDeprioritized++;
                        break;
                }
            }
            
            // Update execution
            execution.setStatus(WorkflowExecution.ExecutionStatus.COMPLETED);
            execution.setAllocatedBudget(request.getTotalBudget().subtract(remainingBudget));
            execution.setRemainingBudget(remainingBudget);
            execution.setProjectsProcessed(projects.size());
            execution.setProjectsFunded(projectsFunded);
            execution.setProjectsRejected(projectsRejected);
            execution.setProjectsDeprioritized(projectsDeprioritized);
            execution.setCompletedAt(LocalDateTime.now());
            execution.setExecutionTimeMs(System.currentTimeMillis() - startTime);
            
            workflowExecutionRepository.save(execution);
            
            return buildResponse(execution, results);
            
        } catch (Exception e) {
            log.error("Workflow execution failed", e);
            execution.setStatus(WorkflowExecution.ExecutionStatus.FAILED);
            execution.setErrorMessage(e.getMessage());
            execution.setCompletedAt(LocalDateTime.now());
            execution.setExecutionTimeMs(System.currentTimeMillis() - startTime);
            workflowExecutionRepository.save(execution);
            throw new RuntimeException("Workflow execution failed: " + e.getMessage());
        }
    }
    
    private List<Project> getProjectsForExecution(WorkflowExecutionRequest request) {
        if (request.getProjectIds() != null && !request.getProjectIds().isEmpty()) {
            return projectRepository.findAllById(request.getProjectIds());
        } else {
            return projectRepository.findByStatus(Project.ProjectStatus.PENDING);
        }
    }
    
    private List<WorkflowExecutionResult> executeFundingPrioritization(
            List<Project> projects, BigDecimal budget, WorkflowExecutionRequest request) {
        
        List<WorkflowExecutionResult> results = new ArrayList<>();
        BigDecimal remainingBudget = budget;
        
        // Sort by priority (desc) and score (desc)
        projects.sort((p1, p2) -> {
            int priorityCompare = Integer.compare(p2.getPriority(), p1.getPriority());
            return priorityCompare != 0 ? priorityCompare : p2.getScore().compareTo(p1.getScore());
        });
        
        for (Project project : projects) {
            WorkflowExecutionResult result = WorkflowExecutionResult.builder()
                    .project(project)
                    .originalStatus(project.getStatus())
                    .decisionReason("Priority-based funding decision")
                    .appliedRules("Priority: " + project.getPriority() + ", Score: " + project.getScore())
                    .build();
            
            if (project.getCost().compareTo(remainingBudget) <= 0) {
                result.setAction(WorkflowExecutionResult.DecisionAction.FUND);
                result.setNewStatus(Project.ProjectStatus.FUNDED);
                result.setAllocatedAmount(project.getCost());
                remainingBudget = remainingBudget.subtract(project.getCost());
                
                // Update project status
                project.setStatus(Project.ProjectStatus.FUNDED);
                project.setFundedAt(LocalDateTime.now());
                projectRepository.save(project);
            } else {
                result.setAction(WorkflowExecutionResult.DecisionAction.REJECT);
                result.setNewStatus(Project.ProjectStatus.REJECTED);
                result.setDecisionReason("Insufficient budget");
            }
            
            results.add(result);
        }
        
        return results;
    }
    
    private List<WorkflowExecutionResult> executeMissionCriticalAccommodation(
            List<Project> projects, BigDecimal budget, WorkflowExecutionRequest request) {
        
        List<WorkflowExecutionResult> results = new ArrayList<>();
        BigDecimal remainingBudget = budget;
        
        // First, handle mission critical projects
        List<Project> missionCriticalProjects = projects.stream()
                .filter(Project::getIsMissionCritical)
                .sorted((p1, p2) -> Integer.compare(p2.getPriority(), p1.getPriority()))
                .collect(Collectors.toList());
        
        for (Project project : missionCriticalProjects) {
            WorkflowExecutionResult result = WorkflowExecutionResult.builder()
                    .project(project)
                    .originalStatus(project.getStatus())
                    .decisionReason("Mission critical project - automatic funding")
                    .appliedRules("Mission Critical Override")
                    .build();
            
            if (project.getCost().compareTo(remainingBudget) <= 0) {
                result.setAction(WorkflowExecutionResult.DecisionAction.FUND);
                result.setNewStatus(Project.ProjectStatus.FUNDED);
                result.setAllocatedAmount(project.getCost());
                remainingBudget = remainingBudget.subtract(project.getCost());
                
                project.setStatus(Project.ProjectStatus.FUNDED);
                project.setFundedAt(LocalDateTime.now());
                projectRepository.save(project);
            } else {
                result.setAction(WorkflowExecutionResult.DecisionAction.REJECT);
                result.setNewStatus(Project.ProjectStatus.REJECTED);
                result.setDecisionReason("Insufficient budget for mission critical project");
            }
            
            results.add(result);
        }
        
        // Then handle remaining projects with available budget
        List<Project> remainingProjects = projects.stream()
                .filter(p -> !p.getIsMissionCritical())
                .sorted((p1, p2) -> {
                    int priorityCompare = Integer.compare(p2.getPriority(), p1.getPriority());
                    return priorityCompare != 0 ? priorityCompare : p2.getScore().compareTo(p1.getScore());
                })
                .collect(Collectors.toList());
        
        for (Project project : remainingProjects) {
            WorkflowExecutionResult result = WorkflowExecutionResult.builder()
                    .project(project)
                    .originalStatus(project.getStatus())
                    .decisionReason("Standard funding decision")
                    .appliedRules("Priority: " + project.getPriority() + ", Score: " + project.getScore())
                    .build();
            
            if (project.getCost().compareTo(remainingBudget) <= 0) {
                result.setAction(WorkflowExecutionResult.DecisionAction.FUND);
                result.setNewStatus(Project.ProjectStatus.FUNDED);
                result.setAllocatedAmount(project.getCost());
                remainingBudget = remainingBudget.subtract(project.getCost());
                
                project.setStatus(Project.ProjectStatus.FUNDED);
                project.setFundedAt(LocalDateTime.now());
                projectRepository.save(project);
            } else {
                result.setAction(WorkflowExecutionResult.DecisionAction.REJECT);
                result.setNewStatus(Project.ProjectStatus.REJECTED);
                result.setDecisionReason("Insufficient remaining budget");
            }
            
            results.add(result);
        }
        
        return results;
    }
    
    private List<WorkflowExecutionResult> executeBudgetReallocation(
            List<Project> projects, BigDecimal budget, WorkflowExecutionRequest request) {
        
        List<WorkflowExecutionResult> results = new ArrayList<>();
        BigDecimal remainingBudget = budget;
        
        // Get currently funded projects that might be deprioritized
        List<Project> fundedProjects = projectRepository.findByStatus(Project.ProjectStatus.FUNDED);
        
        // Sort by priority (asc) and score (asc) for potential deprioritization
        fundedProjects.sort((p1, p2) -> {
            int priorityCompare = Integer.compare(p1.getPriority(), p2.getPriority());
            return priorityCompare != 0 ? priorityCompare : p1.getScore().compareTo(p2.getScore());
        });
        
        // Deprioritize lower priority funded projects if needed
        for (Project fundedProject : fundedProjects) {
            if (remainingBudget.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }
            
            WorkflowExecutionResult result = WorkflowExecutionResult.builder()
                    .project(fundedProject)
                    .originalStatus(fundedProject.getStatus())
                    .decisionReason("Budget reallocation - deprioritizing lower priority project")
                    .appliedRules("Priority: " + fundedProject.getPriority() + ", Score: " + fundedProject.getScore())
                    .build();
            
            result.setAction(WorkflowExecutionResult.DecisionAction.DEPRIORITIZE);
            result.setNewStatus(Project.ProjectStatus.DEPRIORITIZED);
            result.setPriorityChange(-1);
            
            fundedProject.setStatus(Project.ProjectStatus.DEPRIORITIZED);
            projectRepository.save(fundedProject);
            
            remainingBudget = remainingBudget.add(fundedProject.getCost());
            results.add(result);
        }
        
        // Now fund new projects with available budget
        projects.sort((p1, p2) -> {
            int priorityCompare = Integer.compare(p2.getPriority(), p1.getPriority());
            return priorityCompare != 0 ? priorityCompare : p2.getScore().compareTo(p1.getScore());
        });
        
        for (Project project : projects) {
            if (project.getCost().compareTo(remainingBudget) <= 0) {
                WorkflowExecutionResult result = WorkflowExecutionResult.builder()
                        .project(project)
                        .originalStatus(project.getStatus())
                        .action(WorkflowExecutionResult.DecisionAction.FUND)
                        .newStatus(Project.ProjectStatus.FUNDED)
                        .allocatedAmount(project.getCost())
                        .decisionReason("Funded with reallocated budget")
                        .appliedRules("Priority: " + project.getPriority() + ", Score: " + project.getScore())
                        .build();
                
                remainingBudget = remainingBudget.subtract(project.getCost());
                
                project.setStatus(Project.ProjectStatus.FUNDED);
                project.setFundedAt(LocalDateTime.now());
                projectRepository.save(project);
                
                results.add(result);
            }
        }
        
        return results;
    }
    
    private List<WorkflowExecutionResult> executeProjectDeprioritization(
            List<Project> projects, BigDecimal budget, WorkflowExecutionRequest request) {
        
        List<WorkflowExecutionResult> results = new ArrayList<>();
        
        // Sort projects by priority (asc) and score (asc) for deprioritization
        projects.sort((p1, p2) -> {
            int priorityCompare = Integer.compare(p1.getPriority(), p2.getPriority());
            return priorityCompare != 0 ? priorityCompare : p1.getScore().compareTo(p2.getScore());
        });
        
        for (Project project : projects) {
            WorkflowExecutionResult result = WorkflowExecutionResult.builder()
                    .project(project)
                    .originalStatus(project.getStatus())
                    .action(WorkflowExecutionResult.DecisionAction.DEPRIORITIZE)
                    .newStatus(Project.ProjectStatus.DEPRIORITIZED)
                    .priorityChange(-1)
                    .decisionReason("Project deprioritization workflow")
                    .appliedRules("Priority: " + project.getPriority() + ", Score: " + project.getScore())
                    .build();
            
            project.setStatus(Project.ProjectStatus.DEPRIORITIZED);
            projectRepository.save(project);
            
            results.add(result);
        }
        
        return results;
    }
    
    private List<WorkflowExecutionResult> executeEmergencyFunding(
            List<Project> projects, BigDecimal budget, WorkflowExecutionRequest request) {
        
        List<WorkflowExecutionResult> results = new ArrayList<>();
        BigDecimal remainingBudget = budget;
        
        // Emergency funding prioritizes mission critical projects and high priority projects
        projects.sort((p1, p2) -> {
            // Mission critical projects first
            if (p1.getIsMissionCritical() && !p2.getIsMissionCritical()) return -1;
            if (!p1.getIsMissionCritical() && p2.getIsMissionCritical()) return 1;
            
            // Then by priority and score
            int priorityCompare = Integer.compare(p2.getPriority(), p1.getPriority());
            return priorityCompare != 0 ? priorityCompare : p2.getScore().compareTo(p1.getScore());
        });
        
        for (Project project : projects) {
            WorkflowExecutionResult result = WorkflowExecutionResult.builder()
                    .project(project)
                    .originalStatus(project.getStatus())
                    .decisionReason("Emergency funding decision")
                    .appliedRules("Emergency: " + (project.getIsMissionCritical() ? "Mission Critical" : "Priority: " + project.getPriority()))
                    .build();
            
            if (project.getCost().compareTo(remainingBudget) <= 0) {
                result.setAction(WorkflowExecutionResult.DecisionAction.FUND);
                result.setNewStatus(Project.ProjectStatus.FUNDED);
                result.setAllocatedAmount(project.getCost());
                remainingBudget = remainingBudget.subtract(project.getCost());
                
                project.setStatus(Project.ProjectStatus.FUNDED);
                project.setFundedAt(LocalDateTime.now());
                projectRepository.save(project);
            } else {
                result.setAction(WorkflowExecutionResult.DecisionAction.REJECT);
                result.setNewStatus(Project.ProjectStatus.REJECTED);
                result.setDecisionReason("Insufficient emergency budget");
            }
            
            results.add(result);
        }
        
        return results;
    }
    
    private WorkflowExecutionResponse buildResponse(WorkflowExecution execution, List<WorkflowExecutionResult> results) {
        List<WorkflowExecutionResponse.ProjectDecisionDto> decisions = results.stream()
                .map(result -> WorkflowExecutionResponse.ProjectDecisionDto.builder()
                        .projectId(result.getProject().getId())
                        .projectName(result.getProject().getName())
                        .action(result.getAction().name())
                        .originalStatus(result.getOriginalStatus().name())
                        .newStatus(result.getNewStatus() != null ? result.getNewStatus().name() : null)
                        .allocatedAmount(result.getAllocatedAmount())
                        .priorityChange(result.getPriorityChange())
                        .scoreChange(result.getScoreChange())
                        .decisionReason(result.getDecisionReason())
                        .appliedRules(result.getAppliedRules())
                        .build())
                .collect(Collectors.toList());
        
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
                .decisions(decisions)
                .build();
    }
}