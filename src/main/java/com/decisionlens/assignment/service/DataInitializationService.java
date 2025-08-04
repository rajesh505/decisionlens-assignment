package com.decisionlens.assignment.service;

import com.decisionlens.assignment.model.Project;
import com.decisionlens.assignment.model.Workflow;
import com.decisionlens.assignment.repo.ProjectRepository;
import com.decisionlens.assignment.repo.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataInitializationService implements CommandLineRunner {
    
    private final WorkflowRepository workflowRepository;
    private final ProjectRepository projectRepository;
    
    @Override
    public void run(String... args) throws Exception {
        initializeWorkflows();
        initializeSampleProjects();
        log.info("Data initialization completed");
    }
    
    private void initializeWorkflows() {
        if (workflowRepository.count() == 0) {
            // Funding Prioritization Workflow
            Workflow fundingPrioritization = Workflow.builder()
                    .name("Standard Funding Prioritization")
                    .description("Prioritizes projects based on priority and score for funding decisions")
                    .type(Workflow.WorkflowType.FUNDING_PRIORITIZATION)
                    .isActive(true)
                    .build();
            workflowRepository.save(fundingPrioritization);
            
            // Mission Critical Accommodation Workflow
            Workflow missionCritical = Workflow.builder()
                    .name("Mission Critical Accommodation")
                    .description("Handles mission critical projects with priority over regular projects")
                    .type(Workflow.WorkflowType.MISSION_CRITICAL_ACCOMMODATION)
                    .isActive(true)
                    .build();
            workflowRepository.save(missionCritical);
            
            // Budget Reallocation Workflow
            Workflow budgetReallocation = Workflow.builder()
                    .name("Budget Reallocation")
                    .description("Reallocates budget from lower priority funded projects to higher priority new projects")
                    .type(Workflow.WorkflowType.BUDGET_REALLOCATION)
                    .isActive(true)
                    .build();
            workflowRepository.save(budgetReallocation);
            
            // Project Deprioritization Workflow
            Workflow deprioritization = Workflow.builder()
                    .name("Project Deprioritization")
                    .description("Deprioritizes projects to make room for higher priority projects")
                    .type(Workflow.WorkflowType.PROJECT_DEPRIORITIZATION)
                    .isActive(true)
                    .build();
            workflowRepository.save(deprioritization);
            
            // Emergency Funding Workflow
            Workflow emergencyFunding = Workflow.builder()
                    .name("Emergency Funding")
                    .description("Emergency funding workflow for urgent projects")
                    .type(Workflow.WorkflowType.EMERGENCY_FUNDING)
                    .isActive(true)
                    .build();
            workflowRepository.save(emergencyFunding);
            
            log.info("Initialized {} workflows", workflowRepository.count());
        }
    }
    
    private void initializeSampleProjects() {
        if (projectRepository.count() == 0) {
            // Infrastructure Projects
            Project infrastructure1 = Project.builder()
                    .name("Data Center Upgrade")
                    .description("Upgrade data center infrastructure for improved performance")
                    .score(new BigDecimal("85.5"))
                    .cost(new BigDecimal("500000"))
                    .priority(8)
                    .category(Project.ProjectCategory.INFRASTRUCTURE)
                    .isMissionCritical(true)
                    .build();
            projectRepository.save(infrastructure1);
            
            Project infrastructure2 = Project.builder()
                    .name("Network Security Enhancement")
                    .description("Enhance network security protocols and monitoring")
                    .score(new BigDecimal("92.0"))
                    .cost(new BigDecimal("300000"))
                    .priority(9)
                    .category(Project.ProjectCategory.INFRASTRUCTURE)
                    .isMissionCritical(true)
                    .build();
            projectRepository.save(infrastructure2);
            
            // Development Projects
            Project development1 = Project.builder()
                    .name("Mobile App Development")
                    .description("Develop new mobile application for customer engagement")
                    .score(new BigDecimal("78.0"))
                    .cost(new BigDecimal("250000"))
                    .priority(6)
                    .category(Project.ProjectCategory.DEVELOPMENT)
                    .isMissionCritical(false)
                    .build();
            projectRepository.save(development1);
            
            Project development2 = Project.builder()
                    .name("API Gateway Implementation")
                    .description("Implement API gateway for microservices architecture")
                    .score(new BigDecimal("88.5"))
                    .cost(new BigDecimal("400000"))
                    .priority(7)
                    .category(Project.ProjectCategory.DEVELOPMENT)
                    .isMissionCritical(false)
                    .build();
            projectRepository.save(development2);
            
            // Research Projects
            Project research1 = Project.builder()
                    .name("AI/ML Research Initiative")
                    .description("Research and development of AI/ML capabilities")
                    .score(new BigDecimal("95.0"))
                    .cost(new BigDecimal("750000"))
                    .priority(10)
                    .category(Project.ProjectCategory.RESEARCH)
                    .isMissionCritical(false)
                    .build();
            projectRepository.save(research1);
            
            Project research2 = Project.builder()
                    .name("Blockchain Technology Study")
                    .description("Study and evaluate blockchain technology applications")
                    .score(new BigDecimal("72.5"))
                    .cost(new BigDecimal("200000"))
                    .priority(5)
                    .category(Project.ProjectCategory.RESEARCH)
                    .isMissionCritical(false)
                    .build();
            projectRepository.save(research2);
            
            // Maintenance Projects
            Project maintenance1 = Project.builder()
                    .name("System Maintenance")
                    .description("Regular system maintenance and updates")
                    .score(new BigDecimal("65.0"))
                    .cost(new BigDecimal("100000"))
                    .priority(4)
                    .category(Project.ProjectCategory.MAINTENANCE)
                    .isMissionCritical(false)
                    .build();
            projectRepository.save(maintenance1);
            
            Project maintenance2 = Project.builder()
                    .name("Database Optimization")
                    .description("Optimize database performance and queries")
                    .score(new BigDecimal("82.0"))
                    .cost(new BigDecimal("150000"))
                    .priority(6)
                    .category(Project.ProjectCategory.MAINTENANCE)
                    .isMissionCritical(false)
                    .build();
            projectRepository.save(maintenance2);
            
            // Innovation Projects
            Project innovation1 = Project.builder()
                    .name("Virtual Reality Platform")
                    .description("Develop VR platform for immersive experiences")
                    .score(new BigDecimal("90.0"))
                    .cost(new BigDecimal("600000"))
                    .priority(8)
                    .category(Project.ProjectCategory.INNOVATION)
                    .isMissionCritical(false)
                    .build();
            projectRepository.save(innovation1);
            
            Project innovation2 = Project.builder()
                    .name("IoT Integration Project")
                    .description("Integrate IoT devices for smart monitoring")
                    .score(new BigDecimal("87.5"))
                    .cost(new BigDecimal("350000"))
                    .priority(7)
                    .category(Project.ProjectCategory.INNOVATION)
                    .isMissionCritical(false)
                    .build();
            projectRepository.save(innovation2);
            
            log.info("Initialized {} sample projects", projectRepository.count());
        }
    }
}