package portal.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Entity
@Table(name = "students")
@Data
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Roll number is required")
    @Column(unique = true)
    private String rollNumber;

    @NotBlank(message = "Department is required")
    private String department;

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private String role = "STUDENT";
    private String resetToken;
    private java.time.LocalDateTime resetTokenExpiry;
    private boolean enabled = true;
}
