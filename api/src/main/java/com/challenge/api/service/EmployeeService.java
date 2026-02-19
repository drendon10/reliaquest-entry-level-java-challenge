package com.challenge.api.service;

import com.challenge.api.dto.CreateEmployeeRequest;
import com.challenge.api.dto.UpdateEmployeeRequest;
import com.challenge.api.model.Employee;
import com.challenge.api.model.EmployeeImplementation;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service layer responsible for handling the logic of employee operations.
 *
 * Design Decisions:
 * - Uses in-memory store to satisfy the assignment requirements while avoiding unnecessary complexity.
 * - Uses a concurrent hashmap that ensures multiple requests can go through at the same time.
 * - Handles all logic to keep controller focused on HTTP endpoints.
 * - Throws HTTP exceptions to provide clear API responses when things go wrong.
 */
@Service
public class EmployeeService {

    // A concurrent store that acts as a small database of employees
    private final Map<UUID, Employee> store = new ConcurrentHashMap<>();

    public EmployeeService() {
        // Create employees to have data to work with
        EmployeeImplementation e1 = new EmployeeImplementation();
        e1.setUuid(UUID.randomUUID());
        e1.setFirstName("Daniel");
        e1.setLastName("Rendon");
        e1.setFullName("Daniel Rendon");
        e1.setAge(23);
        e1.setSalary(90000);
        e1.setJobTitle("Backend (Java) Associate Software Engineer");
        e1.setEmail("drendon@example.com");
        e1.setContractHireDate(Instant.now());

        EmployeeImplementation e2 = new EmployeeImplementation();
        e2.setUuid(UUID.randomUUID());
        e2.setFirstName("Destiny");
        e2.setLastName("Esquivel");
        e2.setFullName("Destiny Esquivel");
        e2.setAge(22);
        e2.setSalary(80000);
        e2.setJobTitle("Sales Manager");
        e2.setEmail("destiny.esquivel@example.com");
        e2.setContractHireDate(Instant.now());

        store.put(e1.getUuid(), e1);
        store.put(e2.getUuid(), e2);
    }

    /**
     * Get a list of all employees.
     * @return ArrayList of all employees
     */
    public List<Employee> getAllEmployees() {
        // Return a copy to avoid escaping references
        return new ArrayList<>(store.values());
    }

    /**
     * Get an employee by uuid.
     *
     * @param uuid Employee UUID
     * @return Employee
     */
    public Employee getEmployeeByUuid(UUID uuid) {
        Employee e = store.get(uuid);
        // Return a clear HTTP response if employee is not found
        if (e == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found: " + uuid);
        }
        return e;
    }

    /**
     * Create an employee.
     *
     * @param req CreateEmployeeRequest
     * @return Created employee
     */
    public Employee createEmployee(CreateEmployeeRequest req) {
        // Validate the CreateEmployeeRequest
        validateCreateRequest(req);

        EmployeeImplementation e = new EmployeeImplementation();
        e.setUuid(UUID.randomUUID());

        String firstName = req.getFirstName().trim();
        String lastName = req.getLastName().trim();
        e.setFirstName(firstName);
        e.setLastName(lastName);
        e.setFullName(firstName + " " + lastName);

        e.setSalary(req.getSalary());
        e.setAge(req.getAge());
        e.setJobTitle(req.getJobTitle() != null ? req.getJobTitle().trim() : null);
        e.setEmail(req.getEmail() != null ? req.getEmail().trim() : null);
        e.setContractHireDate(req.getContractHireDate() != null ? req.getContractHireDate() : Instant.now());

        store.put(e.getUuid(), e);
        return e;
    }

    /**
     * Update an employee's fields.
     *
     * @param uuid Employee UUID
     * @param req UpdateEmployeeRequest
     * @return Updated Employee
     */
    public Employee updateEmployee(UUID uuid, UpdateEmployeeRequest req) {
        if (req == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request body is required");
        }

        Employee existing = store.get(uuid);
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found: " + uuid);
        }

        // We store EmployeeImplementation instances, so we can safely update fields.
        EmployeeImplementation e = (EmployeeImplementation) existing;

        // Validate and apply updates (only if provided)
        if (req.getFirstName() != null) {
            requireField(req.getFirstName(), "firstName");
            e.setFirstName(req.getFirstName().trim());
        }

        if (req.getLastName() != null) {
            requireField(req.getLastName(), "lastName");
            e.setLastName(req.getLastName().trim());
        }

        // Recompute full name if either name changed
        if (req.getFirstName() != null || req.getLastName() != null) {
            String fn = e.getFirstName() != null ? e.getFirstName().trim() : "";
            String ln = e.getLastName() != null ? e.getLastName().trim() : "";
            e.setFullName((fn + " " + ln).trim());
        }

        if (req.getSalary() != null) {
            if (req.getSalary() < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "salary must be greater than 0");
            }
            e.setSalary(req.getSalary());
        }

        if (req.getAge() != null) {
            if (req.getAge() < 0 || req.getAge() > 100) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "age must be between 0 and 100");
            }
            e.setAge(req.getAge());
        }

        if (req.getJobTitle() != null) {
            if (req.getJobTitle().trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "jobTitle must not be blank");
            }
            e.setJobTitle(req.getJobTitle().trim());
        }

        if (req.getEmail() != null) {
            if (!isValidEmail(req.getEmail())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email must be a valid email address");
            }
            e.setEmail(req.getEmail().trim());
        }

        if (req.getContractHireDate() != null) {
            e.setContractHireDate(req.getContractHireDate());
        }

        // Store already contains this object reference, but we'll ensure to overwrite with updated data
        store.put(uuid, e);
        return e;
    }

    /**
     * Remove an employee by uuid.
     *
     * @param uuid Employee UUID
     */
    public void deleteEmployee(UUID uuid) {
        Employee removed = store.remove(uuid);
        if (removed == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found: " + uuid);
        }
    }

    /**
     * A function to make a field required.
     *
     * @param value A value to validate
     * @param fieldName The field name to be validated
     */
    private static void requireField(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " is required");
        }
    }

    /**
     * A function that performs very simple validation on a CreateEmployeeRequest.
     *
     * First and last name are required fields (can't be null or empty).
     * All other fields are optional.
     *
     * @param req CreateEmployeeRequest
     */
    private static void validateCreateRequest(CreateEmployeeRequest req) {
        if (req == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request body is required");
        }

        // Make first name and last name required
        requireField(req.getFirstName(), "firstName");
        requireField(req.getLastName(), "lastName");

        // If these fields are provided, then validate them
        if (req.getSalary() != null && req.getSalary() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "salary must be greater than 0");
        }

        if (req.getAge() != null && (req.getAge() < 0 || req.getAge() > 100)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "age must be between 0 and 100");
        }

        if (req.getJobTitle() != null && req.getJobTitle().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "jobTitle must not be blank");
        }

        if (req.getEmail() != null && !isValidEmail(req.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email must be a valid email address");
        }
    }

    /**
     * Perform minimal email validation.
     *
     * @param email An email as a string
     * @return true or false whether the email is valid or not
     */
    private static boolean isValidEmail(String email) {
        String e = email.trim();
        int atSign = e.indexOf('@');
        int lastAtSign = e.lastIndexOf('@');

        // There must be only 1 "@"
        if (atSign <= 0 || atSign != lastAtSign) {
            return false;
        }
        String domain = e.substring(atSign + 1);
        // Check that domain has a period
        return !domain.isEmpty() && domain.contains(".");
    }
}
