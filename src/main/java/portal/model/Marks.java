package portal.model;

import jakarta.persistence.*;
import lombok.Data;

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

    private Double marksObtained;

    private Double maxMarks;
}