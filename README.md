# Project Funding Decision Workflow System

A comprehensive workflow system for project funding decision-making, built with Spring Boot. This system supports various decision-making scenarios including project prioritization, mission-critical accommodation, budget reallocation, and emergency funding.

## Architecture Overview

### Core Components

1. **Project Management**: Projects with scores, costs, priorities, and categories
2. **Workflow Engine**: Configurable workflows for different decision scenarios
3. **Decision Rules**: Flexible rule-based decision making
4. **Execution Tracking**: Complete audit trail of workflow executions
5. **Scenario Analysis**: What-if analysis for funding decisions

### Workflow Types

- **FUNDING_PRIORITIZATION**: Standard project funding based on priority and score
- **MISSION_CRITICAL_ACCOMMODATION**: Handles mission-critical projects with priority
- **BUDGET_REALLOCATION**: Reallocates budget from lower priority to higher priority projects
- **PROJECT_DEPRIORITIZATION**: Deprioritizes projects to make room for new ones
- **EMERGENCY_FUNDING**: Emergency funding for urgent projects

## Technology Stack

- **Spring Boot 2.7.1**
- **Spring Data JPA**
- **H2 Database**
- **Gradle**
- **Lombok**
- **Swagger/OpenAPI**

## Getting Started

### Prerequisites
- Java 17
- Gradle

### Running the Application

```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`

### API Documentation
Swagger UI is available at: `http://localhost:8080/swagger-ui/index.html`

## API Endpoints

### Projects

#### Get All Projects
```http
GET /api/projects
```

#### Get Project by ID
```http
GET /api/projects/{id}
```

#### Create Project
```http
POST /api/projects
Content-Type: application/json

{
  "name": "New Project",
  "description": "Project description",
  "score": 85.5,
  "cost": 250000,
  "priority": 7,
  "category": "DEVELOPMENT",
  "isMissionCritical": false
}
```

#### Get Projects by Status
```http
GET /api/projects/status/PENDING
```

#### Get Mission Critical Projects
```http
GET /api/projects/mission-critical
```

### Workflows

#### Get All Workflows
```http
GET /api/workflows
```

#### Execute Workflow
```http
POST /api/workflows/execute
Content-Type: application/json

{
  "workflowId": 1,
  "totalBudget": 1000000,
  "projectIds": [1, 2, 3],
  "includeMissionCritical": true,
  "allowDeprioritization": true,
  "allowPartialFunding": false
}
```

#### Get Workflow Executions
```http
GET /api/workflows/executions
```

## Usage Examples

### Scenario 1: Standard Funding Prioritization

**Problem**: Which projects should be funded first with a limited budget?

**Solution**: Use the FUNDING_PRIORITIZATION workflow

```bash
curl -X POST http://localhost:8080/api/workflows/execute \
  -H "Content-Type: application/json" \
  -d '{
    "workflowId": 1,
    "totalBudget": 1000000,
    "includeMissionCritical": true,
    "allowDeprioritization": false
  }'
```

### Scenario 2: Mission Critical Project Accommodation

**Problem**: A new mission-critical project suddenly comes up. How can we accommodate it?

**Solution**: Use the MISSION_CRITICAL_ACCOMMODATION workflow

```bash
curl -X POST http://localhost:8080/api/workflows/execute \
  -H "Content-Type: application/json" \
  -d '{
    "workflowId": 2,
    "totalBudget": 800000,
    "includeMissionCritical": true,
    "allowDeprioritization": true
  }'
```

### Scenario 3: Budget Reallocation

**Problem**: Need to reallocate budget from lower priority funded projects to fund new high-priority projects.

**Solution**: Use the BUDGET_REALLOCATION workflow

```bash
curl -X POST http://localhost:8080/api/workflows/execute \
  -H "Content-Type: application/json" \
  -d '{
    "workflowId": 3,
    "totalBudget": 500000,
    "allowDeprioritization": true
  }'
```

### Scenario 4: Emergency Funding

**Problem**: Need emergency funding for urgent projects.

**Solution**: Use the EMERGENCY_FUNDING workflow

```bash
curl -X POST http://localhost:8080/api/workflows/execute \
  -H "Content-Type: application/json" \
  -d '{
    "workflowId": 5,
    "totalBudget": 300000,
    "includeMissionCritical": true
  }'
```

## Data Model

### Project Entity
- **id**: Unique identifier
- **name**: Project name
- **description**: Project description
- **score**: Project score (0-100)
- **cost**: Project cost
- **priority**: Priority level (1-10)
- **status**: Project status (PENDING, FUNDED, REJECTED, etc.)
- **category**: Project category (INFRASTRUCTURE, DEVELOPMENT, etc.)
- **isMissionCritical**: Whether project is mission critical

### Workflow Entity
- **id**: Unique identifier
- **name**: Workflow name
- **description**: Workflow description
- **type**: Workflow type
- **isActive**: Whether workflow is active

### WorkflowExecution Entity
- **id**: Unique identifier
- **workflow**: Associated workflow
- **status**: Execution status
- **totalBudget**: Total available budget
- **allocatedBudget**: Budget allocated
- **remainingBudget**: Remaining budget
- **executionTimeMs**: Execution time in milliseconds

## Decision Logic

### Funding Prioritization
1. Sort projects by priority (descending) and score (descending)
2. Fund projects in order until budget is exhausted
3. Reject remaining projects

### Mission Critical Accommodation
1. Fund all mission-critical projects first
2. Fund remaining projects by priority and score
3. Reject projects that exceed budget

### Budget Reallocation
1. Deprioritize lower priority funded projects
2. Reallocate freed budget to new high-priority projects
3. Fund new projects in priority order

### Emergency Funding
1. Prioritize mission-critical projects
2. Fund by priority and score
3. Handle urgent situations with limited budget

## Testing

Run the tests with:

```bash
./gradlew test
```

## Sample Data

The application initializes with sample data including:

- 5 predefined workflows
- 10 sample projects across different categories
- Projects with varying priorities, scores, and costs

## Extensibility

The system is designed to be easily extensible:

1. **New Workflow Types**: Add new workflow types to the `WorkflowType` enum
2. **Custom Decision Logic**: Implement new decision logic in `WorkflowEngineService`
3. **Additional Rules**: Extend the rule system with new rule types
4. **New Project Categories**: Add new categories to the `ProjectCategory` enum

## Monitoring and Analytics

- Track workflow execution performance
- Monitor budget allocation efficiency
- Analyze decision patterns
- Audit trail for all decisions

## Security Considerations

- Input validation on all endpoints
- Transaction management for data consistency
- Audit logging for all workflow executions
- Error handling and graceful degradation