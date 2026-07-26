package portal.repository;

import portal.model.MarksAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MarksAuditLogRepository extends JpaRepository<MarksAuditLog, Long> {
    List<MarksAuditLog> findByMarksId(Long marksId);
}