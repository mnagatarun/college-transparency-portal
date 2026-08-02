package portal.repository;

import portal.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByEmail(String email);
    Optional<Student> findByRollNumber(String rollNumber);
    Optional<Student> findByResetToken(String resetToken);
}