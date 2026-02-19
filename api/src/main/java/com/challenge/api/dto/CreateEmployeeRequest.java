package com.challenge.api.dto;

import java.time.Instant;
import lombok.Data;

/**
 * Request Data Transfer Object (DTO) used when creating a new employee.
 *
 * Defines the API input contract and includes only client-provided fields.
 * Fields managed by the server such as UUID and termination date are intentionally excluded.
 */
@Data
public class CreateEmployeeRequest {
    private String firstName;
    private String lastName;
    private Integer salary;
    private Integer age;
    private String jobTitle;
    private String email;
    private Instant contractHireDate;
}
