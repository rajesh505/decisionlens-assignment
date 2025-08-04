package com.decisionlens.assignment.service;

import com.decisionlens.assignment.dto.WorkflowExecutionRequest;
import com.decisionlens.assignment.dto.WorkflowExecutionResponse;
import com.decisionlens.assignment.model.Project;
import com.decisionlens.assignment.model.Workflow;
import com.decisionlens.assignment.repo.ProjectRepository;
import com.decisionlens.assignment.repo.WorkflowExecutionRepository;
import com.decisionlens.assignment.repo.WorkflowExecutionResultRepository;
import com.decisionlens.assignment.repo.WorkflowRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkflowEngineServiceTest {

    @Mock
    private WorkflowRepository workflowRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private WorkflowExecutionRepository workflowExecutionRepository;

    @Mock
    private WorkflowExecutionResultRepository workflowExecutionResultRepository;

    @InjectMocks
    private WorkflowEngineService workflowEngineService;

    private Workflow testWorkflow;
    private Project testProject1;
    private Project testProject2;

    @BeforeEach
    void setUp() {
        // Create test workflow
        testWorkflow = Workflow.builder()
                .id(1L)
                .name("Test Workflow")
                .description("Test workflow for funding prioritization")
                .type(Workflow.WorkflowType.FUNDING_PRIORITIZATION)
                .isActive(true)
                .build();

        // Create test projects
        testProject1 = Project.builder()
                .id(1L)
                .name("High Priority Project")
                .description("A high priority project")
                .score(new BigDecimal("90.0"))
                .cost(new BigDecimal("100000"))
                .priority(9)
                .status(Project.ProjectStatus.PENDING)
                .category(Project.ProjectCategory.DEVELOPMENT)
                .isMissionCritical(false)
                .build();

        testProject2 = Project.builder()
                .id(2L)
                .name("Low Priority Project")
                .description("A low priority project")
                .score(new BigDecimal("70.0"))
                .cost(new BigDecimal("50000"))
                .priority(5)
                .status(Project.ProjectStatus.PENDING)
                .category(Project.ProjectCategory.MAINTENANCE)
                .isMissionCritical(false)
                .build();
    }

    @Test
    void testExecuteWorkflow_FundingPrioritization() {
        // Arrange
        WorkflowExecutionRequest request = WorkflowExecutionRequest.builder()
                .workflowId(1L)
                .totalBudget(new BigDecimal("150000"))
                .build();

        when(workflowRepository.findById(1L)).thenReturn(Optional.of(testWorkflow));
        when(projectRepository.findByStatus(Project.ProjectStatus.PENDING))
                .thenReturn(Arrays.asList(testProject1, testProject2));
        when(workflowExecutionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(workflowExecutionResultRepository.saveAll(any())).thenReturn(Arrays.asList());

        // Act
        WorkflowExecutionResponse response = workflowEngineService.executeWorkflow(request);

        // Assert
        assertNotNull(response);
        assertEquals("Test Workflow", response.getWorkflowName());
        assertEquals("COMPLETED", response.getStatus());
        assertEquals(new BigDecimal("150000"), response.getTotalBudget());
        assertEquals(new BigDecimal("150000"), response.getAllocatedBudget());
        assertEquals(new BigDecimal("0"), response.getRemainingBudget());
        assertEquals(2, response.getProjectsProcessed());
        assertEquals(2, response.getProjectsFunded());
        assertEquals(0, response.getProjectsRejected());
        assertEquals(0, response.getProjectsDeprioritized());
        assertNotNull(response.getDecisions());
        assertEquals(2, response.getDecisions().size());
    }

    @Test
    void testExecuteWorkflow_InsufficientBudget() {
        // Arrange
        WorkflowExecutionRequest request = WorkflowExecutionRequest.builder()
                .workflowId(1L)
                .totalBudget(new BigDecimal("50000"))
                .build();

        when(workflowRepository.findById(1L)).thenReturn(Optional.of(testWorkflow));
        when(projectRepository.findByStatus(Project.ProjectStatus.PENDING))
                .thenReturn(Arrays.asList(testProject1, testProject2));
        when(workflowExecutionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(workflowExecutionResultRepository.saveAll(any())).thenReturn(Arrays.asList());

        // Act
        WorkflowExecutionResponse response = workflowEngineService.executeWorkflow(request);

        // Assert
        assertNotNull(response);
        assertEquals("Test Workflow", response.getWorkflowName());
        assertEquals("COMPLETED", response.getStatus());
        assertEquals(new BigDecimal("50000"), response.getTotalBudget());
        assertEquals(new BigDecimal("50000"), response.getAllocatedBudget());
        assertEquals(new BigDecimal("0"), response.getRemainingBudget());
        assertEquals(2, response.getProjectsProcessed());
        assertEquals(1, response.getProjectsFunded());
        assertEquals(1, response.getProjectsRejected());
        assertEquals(0, response.getProjectsDeprioritized());
        assertNotNull(response.getDecisions());
        assertEquals(2, response.getDecisions().size());
    }

    @Test
    void testExecuteWorkflow_WorkflowNotFound() {
        // Arrange
        WorkflowExecutionRequest request = WorkflowExecutionRequest.builder()
                .workflowId(999L)
                .totalBudget(new BigDecimal("100000"))
                .build();

        when(workflowRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            workflowEngineService.executeWorkflow(request);
        });
    }
}