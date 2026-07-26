package portal.service;

import portal.model.Attendance;
import portal.model.Student;
import portal.repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private StudentService studentService;

    public Attendance markAttendance(Long studentId, String subject, java.time.LocalDate date, String status) {
        Student student = studentService.getStudentById(studentId);

        Attendance attendance = new Attendance();
        attendance.setStudent(student);
        attendance.setSubject(subject);
        attendance.setDate(date);
        attendance.setStatus(status);

        return attendanceRepository.save(attendance);
    }

    public List<Attendance> getAttendanceForStudent(Long studentId) {
        return attendanceRepository.findByStudentId(studentId);
    }

    public double calculateAttendancePercentage(Long studentId) {
        List<Attendance> records = attendanceRepository.findByStudentId(studentId);

        if (records.isEmpty()) {
            return 0.0;
        }

        long presentCount = records.stream()
                .filter(r -> r.getStatus().equalsIgnoreCase("PRESENT"))
                .count();

        return (presentCount * 100.0) / records.size();
    }
}