# ReliaQuest's Entry-Level Java Challenge

## Overview

This project exposes employee data through a secure REST API that can be consumed by the Employees-R-US SaaS platform via webhooks.

Because the company’s existing employee management system is tightly coupled with other internal services, replacing it outright is not feasible. Instead, this API acts as a secure integration layer between the internal system and the external SaaS platform while migration occurs.

With this implementation, we prioritize:

- Practical and maintainable design
- Clear separation of concerns
- Clean and readable code

## Design and Architecture:

The application follows a layered architecture.

Controller -> Service Layer -> Data Store

### Controller Layer

**EmployeeController**
Responsible for:

- Mapping HTTP requests to endpoints
- Returning appropriate HTTP responses

This layer is simple and keeps logic in the service layer.

### Service Layer

**EmployeeService**

Responsible for:

- Logic and validation
- Managing employee data
- Handling data creation and retrieval
- Ensuring thread-safe operations

This layer isolates the main logic of the application, making the system easier to maintain and test.

### Data Store

An in-memory `ConcurrentHashMap<UUID, Employee>` is used as the employee storage.

Why this approach:

- Concurrent request and responses
- Simple enough to demonstrate how the API works while avoiding complexity
- Easy to replace with an actual database if needed.

In an actual production system, a database would be used instead of a hashmap.

## Implemented Endpoints

### Get All Employees

**GET** `/employees`

Returns a list of all employees.

**Response:** `200 OK`

### Get Employee by UUID

**GET** `/employees/{uuid}`

Returns a single employee based on the provided UUID.
**Responses:**

- 200 OK — employee found
- 404 NOT FOUND — employee does not exist

### Create Employee

**POST** `/employees`

Creates a new employee.
**Responses:**

- 201 CREATED — employee successfully created
- 400 BAD REQUEST — invalid request body

### Additional EndPoints Beyond Original Specification

To make the API more practical and demonstrate extensibility, I added additional endpoints that went beyond the original requirements.

### Update Employee

**PATCH** `/employees/{uuid}`

Updates one or more employee fields.
**Responses:**

- 200 OK — employee updated
- 404 NOT FOUND — employee does not exist
- 400 BAD REQUEST — invalid update data

### Delete Employee

**DELETE** `/employees/{uuid}`

Deletes an employee.

**Responses:**

- 204 NO CONTENT — employee deleted
- 404 NOT FOUND — employee does not exist

### Data Validation & Error Handling

I added input validation and error handling to ensure predictable API behavior.

### Validation

- Required fields must be present when creating employees
- Request bodies must match expected structure
- Invalid UUID formats are rejected

### Error Handling

The API returns meaningful HTTP status codes:

- 400 BAD REQUEST for invalid input
- 404 NOT FOUND when a resource does not exist
- 201 CREATED when a resource is successfully created
- 204 NO CONTENT when a resource is deleted
