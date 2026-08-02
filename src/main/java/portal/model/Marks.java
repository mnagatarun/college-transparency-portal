package portal.model;

import jakarta.persistence.*;
import lombok.Data;
import jakarta.validation.constraints.PositiveOrZero;

@Entity
@Table(name = "marks")
@Data
public class Marks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    private String subject;

    private String examType;

    @PositiveOrZero(message = "Marks obtained cannot be negative")
    private Double marksObtained;

    @PositiveOrZero(message = "Max marks cannot be negative")
    private Double maxMarks;
}