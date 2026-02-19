package com.challenge.api.model;

import java.time.Instant;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Implementation of the Employee interface.
 *
 * This class represents the employee data model that is used in this assignment.
 * It contains only data fields, leaving the logic to the service layer (EmployeeService).
 */
@Data
@NoArgsConstructor
public class EmployeeImplementation implements Employee {
    private UUID uuid;
    private String firstName;
    private String lastName;
    private String fullName;
    private Integer salary;
    private Integer age;
    private String jobTitle;
    private String email;
    private Instant contractHireDate;
    private Instant contractTerminationDate;
}
