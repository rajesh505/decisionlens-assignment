package com.decisionlens.assignment.repo;

import com.decisionlens.assignment.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    
    List<Project> findByStatus(Project.ProjectStatus status);
    
    List<Project> findByCategory(Project.ProjectCategory category);
    
    List<Project> findByIsMissionCritical(Boolean isMissionCritical);
    
    List<Project> findByPriorityGreaterThanEqual(Integer priority);
    
    List<Project> findByScoreGreaterThanEqual(BigDecimal score);
    
    List<Project> findByCostLessThanEqual(BigDecimal cost);
    
    @Query("SELECT p FROM Project p WHERE p.status = :status ORDER BY p.priority DESC, p.score DESC")
    List<Project> findByStatusOrderByPriorityAndScore(@Param("status") Project.ProjectStatus status);
    
    @Query("SELECT p FROM Project p WHERE p.isMissionCritical = true AND p.status = :status")
    List<Project> findMissionCriticalByStatus(@Param("status") Project.ProjectStatus status);
    
    @Query("SELECT SUM(p.cost) FROM Project p WHERE p.status = :status")
    BigDecimal getTotalCostByStatus(@Param("status") Project.ProjectStatus status);
    
    @Query("SELECT p FROM Project p WHERE p.status = :status AND p.cost <= :maxCost ORDER BY p.priority DESC, p.score DESC")
    List<Project> findAffordableProjects(@Param("status") Project.ProjectStatus status, @Param("maxCost") BigDecimal maxCost);
    
    @Query("SELECT p FROM Project p WHERE p.status = :status ORDER BY p.priority DESC, p.score DESC")
    List<Project> findProjectsForFunding(@Param("status") Project.ProjectStatus status);
    
    @Query("SELECT p FROM Project p WHERE p.status = :status AND p.isMissionCritical = false ORDER BY p.priority ASC, p.score ASC")
    List<Project> findProjectsForDeprioritization(@Param("status") Project.ProjectStatus status);
}