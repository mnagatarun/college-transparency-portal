package portal.service;

import portal.model.Marks;
import portal.model.MarksAuditLog;
import portal.model.Student;
import portal.repository.MarksRepository;
import portal.repository.MarksAuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MarksService {

    @Autowired
    private MarksRepository marksRepository;

    @Autowired
    private MarksAuditLogRepository auditLogRepository;

    @Autowired
    private StudentService studentService;

    public Marks addOrUpdateMarks(Long studentId, String subject, String examType,
                                  Double marksObtained, Double maxMarks, String changedBy) {

        Optional<Marks> existing = marksRepository
                .findByStudentIdAndSubjectAndExamType(studentId, subject, examType);

        if (existing.isPresent()) {
            Marks marks = existing.get();
            Double oldValue = marks.getMarksObtained();

            MarksAuditLog log = new MarksAuditLog();
            log.setMarksId(marks.getId());
            log.setOldValue(oldValue);
            log.setNewValue(marksObtained);
            log.setChangedBy(changedBy);
            log.setChangedAt(LocalDateTime.now());
            auditLogRepository.save(log);

            marks.setMarksObtained(marksObtained);
            marks.setMaxMarks(maxMarks);
            return marksRepository.save(marks);

        } else {
            Student student = studentService.getStudentById(studentId);

            Marks marks = new Marks();
            marks.setStudent(student);
            marks.setSubject(subject);
            marks.setExamType(examType);
            marks.setMarksObtained(marksObtained);
            marks.setMaxMarks(maxMarks);
            Marks saved = marksRepository.save(marks);

            MarksAuditLog log = new MarksAuditLog();
            log.setMarksId(saved.getId());
            log.setOldValue(null);
            log.setNewValue(marksObtained);
            log.setChangedBy(changedBy);
            log.setChangedAt(LocalDateTime.now());
            auditLogRepository.save(log);

            return saved;
        }
    }

    public List<Marks> getMarksForStudent(Long studentId) {
        return marksRepository.findByStudentId(studentId);
    }

    public List<MarksAuditLog> getAuditHistory(Long marksId) {
        return auditLogRepository.findByMarksId(marksId);
    }
}