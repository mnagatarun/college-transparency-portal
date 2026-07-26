package portal.controller;

import portal.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;

@Controller
@RequestMapping("/faculty")
public class FacultyController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private MarksService marksService;

    @Autowired
    private OutpassService outpassService;

    @Autowired
    private SubmissionService submissionService;

    private boolean isFaculty(HttpSession session) {
        String role = (String) session.getAttribute("role");
        return "FACULTY".equals(role);
    }

    @GetMapping
    public String facultyDashboard(HttpSession session, Model model) {
        if (!isFaculty(session)) {
            return "redirect:/login";
        }

        model.addAttribute("facultyName", session.getAttribute("studentName"));
        model.addAttribute("studentList", studentService.getAllStudents());
        model.addAttribute("pendingOutpass", outpassService.getAllPendingRequests());
        model.addAttribute("submissionDashboard", submissionService.getAllWithOverdueCheck());
        return "faculty";
    }

    @PostMapping("/attendance/mark")
    public String markAttendance(HttpSession session,
                                 @RequestParam Long studentId,
                                 @RequestParam String subject,
                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                 @RequestParam String status) {
        if (!isFaculty(session)) {
            return "redirect:/login";
        }
        attendanceService.markAttendance(studentId, subject, date, status);
        return "redirect:/faculty";
    }

    @PostMapping("/marks/add")
    public String addMarks(HttpSession session,
                           @RequestParam Long studentId,
                           @RequestParam String subject,
                           @RequestParam String examType,
                           @RequestParam Double marksObtained,
                           @RequestParam Double maxMarks,
                           @RequestParam String changedBy) {
        if (!isFaculty(session)) {
            return "redirect:/login";
        }
        marksService.addOrUpdateMarks(studentId, subject, examType, marksObtained, maxMarks, changedBy);
        return "redirect:/faculty";
    }

    @PostMapping("/outpass/approve/{id}")
    public String approveOutpass(HttpSession session, @PathVariable Long id, @RequestParam String approvedBy) {
        if (!isFaculty(session)) {
            return "redirect:/login";
        }
        outpassService.approveOutpass(id, approvedBy);
        return "redirect:/faculty";
    }

    @PostMapping("/outpass/reject/{id}")
    public String rejectOutpass(HttpSession session, @PathVariable Long id, @RequestParam String approvedBy) {
        if (!isFaculty(session)) {
            return "redirect:/login";
        }
        outpassService.rejectOutpass(id, approvedBy);
        return "redirect:/faculty";
    }

    @PostMapping("/submissions/verify/{id}")
    public String verifySubmission(HttpSession session, @PathVariable Long id, @RequestParam String verifiedBy) {
        if (!isFaculty(session)) {
            return "redirect:/login";
        }
        submissionService.verifySubmission(id, verifiedBy);
        return "redirect:/faculty";
    }
}