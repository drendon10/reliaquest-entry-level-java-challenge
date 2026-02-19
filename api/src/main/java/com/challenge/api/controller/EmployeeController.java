package com.challenge.api.controller;

import com.challenge.api.dto.CreateEmployeeRequest;
import com.challenge.api.dto.UpdateEmployeeRequest;
import com.challenge.api.model.Employee;
import com.challenge.api.service.EmployeeService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller responsible for exposing Employee resources.
 *
 * Design Decisions:
 * - Maps HTTP requests to employee endpoints.
 * - Uses EmployeeService to perform operations.
 * - Keeps business logic and request handling separate.
 * - Returns RESTful responses with appropriate HTTP status codes.
 */
@RestController
@RequestMapping("/api/v1/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    // Pass the service through the constructor to make the dependency clear and easy to test.
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * Returns all employees.
     *
     * Data is retrieved from an in-memory store, which satisfies the assignment requirement while avoiding unnecessary
     * complexity.
     *
     * @return One or more Employees.
     */
    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    /**
     * Returns a single employee by UUID.
     *
     * If the employee does not exist, the service layer returns a corresponding HTTP 404 status.
     *
     * @param uuid Employee UUID
     * @return Requested Employee if exists
     */
    @GetMapping("/{uuid}")
    public Employee getEmployeeByUuid(@PathVariable UUID uuid) {
        return employeeService.getEmployeeByUuid(uuid);
    }

    /**
     * Creates a new employee.
     *
     * Accepts a request Data Transfer Object (DTO) to control the API contract and prevent clients from
     * setting fields managed by the server. Returns HTTP 201 to indicate successful creation
     *
     * @param requestBody CreateEmployeeRequest
     * @return Newly created Employee
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Employee createEmployee(@RequestBody CreateEmployeeRequest requestBody) {
        return employeeService.createEmployee(requestBody);
    }

    /**
     * Partially updates an employee. Only fields that are not null from the request body are applied.
     *
     * @param uuid Employee UUID
     * @param requestBody
     * @return Updated employee
     */
    @PatchMapping("/{uuid}")
    public Employee updateEmployee(@PathVariable UUID uuid, @RequestBody UpdateEmployeeRequest requestBody) {
        return employeeService.updateEmployee(uuid, requestBody);
    }

    /**
     * Deletes an employee by UUID.
     *
     * @param uuid Employee UUID
     */
    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEmployee(@PathVariable UUID uuid) {
        employeeService.deleteEmployee(uuid);
    }
}
