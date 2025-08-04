package com.decisionlens.assignment.repo;

import com.decisionlens.assignment.model.WorkflowExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WorkflowExecutionRepository extends JpaRepository<WorkflowExecution, Long> {
    
    List<WorkflowExecution> findByWorkflowId(Long workflowId);
    
    List<WorkflowExecution> findByStatus(WorkflowExecution.ExecutionStatus status);
    
    List<WorkflowExecution> findByStartedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT we FROM WorkflowExecution we WHERE we.workflow.id = :workflowId ORDER BY we.startedAt DESC")
    List<WorkflowExecution> findRecentExecutionsByWorkflowId(@Param("workflowId") Long workflowId);
    
    @Query("SELECT we FROM WorkflowExecution we WHERE we.status = 'COMPLETED' ORDER BY we.startedAt DESC")
    List<WorkflowExecution> findCompletedExecutions();
}