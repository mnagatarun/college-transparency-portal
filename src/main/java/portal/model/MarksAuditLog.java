package portal.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "marks_audit_log")
@Data
public class MarksAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long marksId;

    private Double oldValue;

    private Double newValue;

    private String changedBy;

    private LocalDateTime changedAt;
}