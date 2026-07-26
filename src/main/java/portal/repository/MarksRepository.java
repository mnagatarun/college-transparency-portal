package portal.repository;

import portal.model.Marks;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MarksRepository extends JpaRepository<Marks, Long> {
    List<Marks> findByStudentId(Long studentId);
    Optional<Marks> findByStudentIdAndSubjectAndExamType(Long studentId, String subject, String examType);
}