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

    @GetMapping
    public String facultyDashboard(HttpSession session, Model model) {
        model.addAttribute("facultyName", session.getAttribute("studentName"));
        model.addAttribute("studentList", studentService.getAllStudents());
        model.addAttribute("pendingOutpass", outpassService.getAllPendingRequests());
        model.addAttribute("submissionDashboard", submissionService.getAllWithOverdueCheck());
        return "faculty";
    }

    @PostMapping("/attendance/mark")
    public String markAttendance(@RequestParam Long studentId,
                                 @RequestParam String subject,
                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                 @RequestParam String status) {
        attendanceService.markAttendance(studentId, subject, date, status);
        return "redirect:/faculty";
    }

    @PostMapping("/marks/add")
    public String addMarks(@RequestParam Long studentId,
                           @RequestParam String subject,
                           @RequestParam String examType,
                           @RequestParam Double marksObtained,
                           @RequestParam Double maxMarks,
                           @RequestParam String changedBy) {
        marksService.addOrUpdateMarks(studentId, subject, examType, marksObtained, maxMarks, changedBy);
        return "redirect:/faculty";
    }

    @PostMapping("/outpass/approve/{id}")
    public String approveOutpass(@PathVariable Long id, @RequestParam String approvedBy) {
        outpassService.approveOutpass(id, approvedBy);
        return "redirect:/faculty";
    }

    @PostMapping("/outpass/reject/{id}")
    public String rejectOutpass(@PathVariable Long id, @RequestParam String approvedBy) {
        outpassService.rejectOutpass(id, approvedBy);
        return "redirect:/faculty";
    }

    @PostMapping("/submissions/verify/{id}")
    public String verifySubmission(@PathVariable Long id, @RequestParam String verifiedBy) {
        submissionService.verifySubmission(id, verifiedBy);
        return "redirect:/faculty";
    }
}