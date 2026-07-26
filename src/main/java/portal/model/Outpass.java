package portal.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "outpass")
@Data
public class Outpass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    private String reason;

    private LocalDateTime requestedAt;

    private LocalDateTime outTime;

    private String status = "PENDING";

    private String approvedBy;

    private LocalDateTime approvedAt;
}