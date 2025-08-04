package com.decisionlens.assignment.repo;

import com.decisionlens.assignment.model.Workflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkflowRepository extends JpaRepository<Workflow, Long> {
    
    List<Workflow> findByIsActive(Boolean isActive);
    
    List<Workflow> findByType(Workflow.WorkflowType type);
    
    Optional<Workflow> findByName(String name);
    
    List<Workflow> findByTypeAndIsActive(Workflow.WorkflowType type, Boolean isActive);
}