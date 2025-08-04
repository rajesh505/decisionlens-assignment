package com.decisionlens.assignment.repo;

import com.decisionlens.assignment.model.WorkflowExecutionResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkflowExecutionResultRepository extends JpaRepository<WorkflowExecutionResult, Long> {
    
    List<WorkflowExecutionResult> findByWorkflowExecutionId(Long workflowExecutionId);
    
    List<WorkflowExecutionResult> findByProjectId(Long projectId);
    
    List<WorkflowExecutionResult> findByAction(WorkflowExecutionResult.DecisionAction action);
}