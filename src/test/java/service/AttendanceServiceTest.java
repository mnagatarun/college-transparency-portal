package portal.service;

import portal.model.Attendance;
import portal.repository.AttendanceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {

    @Mock
    private AttendanceRepository attendanceRepository;

    @InjectMocks
    private AttendanceService attendanceService;

    @Test
    void calculatesCorrectPercentage() {
        Attendance present1 = new Attendance();
        present1.setStatus("PRESENT");
        Attendance present2 = new Attendance();
        present2.setStatus("PRESENT");
        Attendance absent = new Attendance();
        absent.setStatus("ABSENT");

        List<Attendance> records = Arrays.asList(present1, present2, absent);
        when(attendanceRepository.findByStudentId(1L)).thenReturn(records);

        double result = attendanceService.calculateAttendancePercentage(1L);

        assertEquals(66.67, result, 0.01);
    }

    @Test
    void returnsZeroWhenNoRecords() {
        when(attendanceRepository.findByStudentId(2L)).thenReturn(List.of());
        double result = attendanceService.calculateAttendancePercentage(2L);
        assertEquals(0.0, result);
    }
}