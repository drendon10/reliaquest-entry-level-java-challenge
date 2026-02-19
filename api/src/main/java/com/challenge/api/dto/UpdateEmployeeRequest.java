package com.challenge.api.dto;

import java.time.Instant;
import lombok.Data;

/**
 * Request Data Transfer Object (DTO) for making partial updates to an employee
 *
 * All fields are optional. If a field is null, it will not be changed.
 * Clients should not be allowed to change termination date so that field was left out.
 */
@Data
public class UpdateEmployeeRequest {
    private String firstName;
    private String lastName;
    private Integer salary;
    private Integer age;
    private String jobTitle;
    private String email;
    private Instant contractHireDate;
}
