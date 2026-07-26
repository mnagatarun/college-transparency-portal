package portal.controller;

import portal.model.Attendance;
import portal.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @PostMapping("/mark")
    public Attendance markAttendance(
            @RequestParam Long studentId,
            @RequestParam String subject,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String status) {
        return attendanceService.markAttendance(studentId, subject, date, status);
    }

    @GetMapping("/student/{studentId}")
    public List<Attendance> getAttendanceForStudent(@PathVariable Long studentId) {
        return attendanceService.getAttendanceForStudent(studentId);
    }

    @GetMapping("/percentage/{studentId}")
    public double getAttendancePercentage(@PathVariable Long studentId) {
        return attendanceService.calculateAttendancePercentage(studentId);
    }
}