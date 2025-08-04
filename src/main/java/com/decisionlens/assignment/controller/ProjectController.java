package com.decisionlens.assignment.controller;

import com.decisionlens.assignment.dto.ProjectDto;
import com.decisionlens.assignment.model.Project;
import com.decisionlens.assignment.repo.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {
    
    private final ProjectRepository projectRepository;
    
    @GetMapping
    public ResponseEntity<List<ProjectDto>> getAllProjects() {
        List<ProjectDto> projects = projectRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(projects);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto> getProject(@PathVariable Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        return ResponseEntity.ok(convertToDto(project));
    }
    
    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@Valid @RequestBody ProjectDto projectDto) {
        Project project = convertToEntity(projectDto);
        project = projectRepository.save(project);
        return ResponseEntity.ok(convertToDto(project));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ProjectDto> updateProject(@PathVariable Long id, @Valid @RequestBody ProjectDto projectDto) {
        Project existingProject = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        updateProjectFromDto(existingProject, projectDto);
        existingProject = projectRepository.save(existingProject);
        return ResponseEntity.ok(convertToDto(existingProject));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ProjectDto>> getProjectsByStatus(@PathVariable String status) {
        Project.ProjectStatus projectStatus = Project.ProjectStatus.valueOf(status.toUpperCase());
        List<ProjectDto> projects = projectRepository.findByStatus(projectStatus).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(projects);
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProjectDto>> getProjectsByCategory(@PathVariable String category) {
        Project.ProjectCategory projectCategory = Project.ProjectCategory.valueOf(category.toUpperCase());
        List<ProjectDto> projects = projectRepository.findByCategory(projectCategory).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(projects);
    }
    
    @GetMapping("/mission-critical")
    public ResponseEntity<List<ProjectDto>> getMissionCriticalProjects() {
        List<ProjectDto> projects = projectRepository.findByIsMissionCritical(true).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(projects);
    }
    
    @GetMapping("/affordable")
    public ResponseEntity<List<ProjectDto>> getAffordableProjects(@RequestParam BigDecimal maxCost) {
        List<ProjectDto> projects = projectRepository.findAffordableProjects(Project.ProjectStatus.PENDING, maxCost).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(projects);
    }
    
    @GetMapping("/for-funding")
    public ResponseEntity<List<ProjectDto>> getProjectsForFunding() {
        List<ProjectDto> projects = projectRepository.findProjectsForFunding(Project.ProjectStatus.PENDING).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(projects);
    }
    
    private ProjectDto convertToDto(Project project) {
        return ProjectDto.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .score(project.getScore())
                .cost(project.getCost())
                .priority(project.getPriority())
                .status(project.getStatus().name())
                .category(project.getCategory().name())
                .isMissionCritical(project.getIsMissionCritical())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .fundedAt(project.getFundedAt())
                .build();
    }
    
    private Project convertToEntity(ProjectDto dto) {
        return Project.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .score(dto.getScore())
                .cost(dto.getCost())
                .priority(dto.getPriority())
                .status(dto.getStatus() != null ? Project.ProjectStatus.valueOf(dto.getStatus()) : null)
                .category(dto.getCategory() != null ? Project.ProjectCategory.valueOf(dto.getCategory()) : null)
                .isMissionCritical(dto.getIsMissionCritical())
                .build();
    }
    
    private void updateProjectFromDto(Project project, ProjectDto dto) {
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setScore(dto.getScore());
        project.setCost(dto.getCost());
        project.setPriority(dto.getPriority());
        if (dto.getStatus() != null) {
            project.setStatus(Project.ProjectStatus.valueOf(dto.getStatus()));
        }
        if (dto.getCategory() != null) {
            project.setCategory(Project.ProjectCategory.valueOf(dto.getCategory()));
        }
        project.setIsMissionCritical(dto.getIsMissionCritical());
    }
}