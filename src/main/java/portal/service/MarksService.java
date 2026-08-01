package portal.service;

import portal.model.Marks;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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

            MarksAuditLog log2 = new MarksAuditLog();
            log2.setMarksId(marks.getId());
            log2.setOldValue(oldValue);
            log2.setNewValue(marksObtained);
            log2.setChangedBy(changedBy);
            log2.setChangedAt(LocalDateTime.now());
            auditLogRepository.save(log2);

            log.info("Marks updated - studentId: {}, subject: {}, examType: {}, {} -> {}, changedBy: {}",
                    studentId, subject, examType, oldValue, marksObtained, changedBy);

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

            MarksAuditLog log2 = new MarksAuditLog();
            log2.setMarksId(saved.getId());
            log2.setOldValue(null);
            log2.setNewValue(marksObtained);
            log2.setChangedBy(changedBy);
            log2.setChangedAt(LocalDateTime.now());
            auditLogRepository.save(log2);

            log.info("New marks entry created - studentId: {}, subject: {}, examType: {}, value: {}, changedBy: {}",
                    studentId, subject, examType, marksObtained, changedBy);

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