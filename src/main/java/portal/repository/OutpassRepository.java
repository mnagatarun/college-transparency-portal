package portal.repository;

import portal.model.Outpass;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OutpassRepository extends JpaRepository<Outpass, Long> {
    List<Outpass> findByStudentId(Long studentId);
    List<Outpass> findByStatus(String status);
}