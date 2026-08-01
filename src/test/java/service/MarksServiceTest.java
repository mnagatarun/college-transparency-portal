package portal.service;

import portal.model.Marks;
import portal.model.Student;
import portal.repository.MarksRepository;
import portal.repository.MarksAuditLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarksServiceTest {

    @Mock private MarksRepository marksRepository;
    @Mock private MarksAuditLogRepository auditLogRepository;
    @Mock private StudentService studentService;

    @InjectMocks
    private MarksService marksService;

    @Test
    void firstTimeMarksEntryLogsNullOldValue() {
        when(marksRepository.findByStudentIdAndSubjectAndExamType(1L, "Maths", "MID1"))
                .thenReturn(Optional.empty());
        when(studentService.getStudentById(1L)).thenReturn(new Student());
        when(marksRepository.save(any(Marks.class))).thenAnswer(i -> {
            Marks m = i.getArgument(0);
            m.setId(1L);
            return m;
        });

        marksService.addOrUpdateMarks(1L, "Maths", "MID1", 15.0, 20.0, "faculty1@college.edu");

        verify(auditLogRepository, times(1)).save(argThat(log ->
                log.getOldValue() == null && log.getNewValue().equals(15.0)));
    }

    @Test
    void updatingExistingMarksLogsOldAndNewValue() {
        Marks existing = new Marks();
        existing.setId(1L);
        existing.setMarksObtained(15.0);

        when(marksRepository.findByStudentIdAndSubjectAndExamType(1L, "Maths", "MID1"))
                .thenReturn(Optional.of(existing));
        when(marksRepository.save(any(Marks.class))).thenAnswer(i -> i.getArgument(0));

        marksService.addOrUpdateMarks(1L, "Maths", "MID1", 18.0, 20.0, "faculty1@college.edu");

        verify(auditLogRepository, times(1)).save(argThat(log ->
                log.getOldValue().equals(15.0) && log.getNewValue().equals(18.0)));
    }
}