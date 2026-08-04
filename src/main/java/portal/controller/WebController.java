package portal.controller;
import portal.model.Student;
import portal.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import portal.service.AttendanceService;
import portal.service.MarksService;
import portal.service.OutpassService;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import portal.service.PasswordResetService;
import portal.service.OutpassService;

@Controller
public class WebController {
    @Autowired
    private PasswordResetService passwordResetService;

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, Model model) {
        try {
            passwordResetService.sendResetLink(email);
            model.addAttribute("message", "Reset link sent to your email.");
        } catch (RuntimeException e) {
            e.printStackTrace();
            model.addAttribute("message", "If that email exists, a reset link was sent.");
        }
        return "forgot-password";
    }
    @PostMapping("/outpass/request")
    public String requestOutpass(HttpSession session,
                                 @RequestParam String reason,
                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime outTime,
                                 Model model) {
        Long studentId = (Long) session.getAttribute("loggedInStudentId");
        if (studentId == null) {
            return "redirect:/login";
        }
        try {
            outpassService.requestOutpass(studentId, reason, outTime);
        } catch (RuntimeException e) {
            model.addAttribute("outpassError", e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam String token, @RequestParam String newPassword, Model model) {
        try {
            passwordResetService.resetPassword(token, newPassword);
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("token", token);
            return "reset-password";
        }
    }

    @Autowired
    private StudentService studentService;

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String email,
                               @RequestParam String password,
                               HttpSession session,
                               Model model) {
        try {
            Student student = studentService.loginStudent(email, password);
            session.setAttribute("loggedInStudentId", student.getId());
            session.setAttribute("studentName", student.getName());
            session.setAttribute("role", student.getRole());

            if ("ADMIN".equals(student.getRole())) {
                return "redirect:/admin";
            }
            if ("FACULTY".equals(student.getRole())) {
                return "redirect:/faculty";
            }
            return "redirect:/dashboard";

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }
    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private MarksService marksService;
    @Autowired
    private OutpassService outpassService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Long studentId = (Long) session.getAttribute("loggedInStudentId");

        if (studentId == null) {
            return "redirect:/login";
        }

        String studentName = (String) session.getAttribute("studentName");
        double attendancePercentage = attendanceService.calculateAttendancePercentage(studentId);

        model.addAttribute("studentName", studentName);
        model.addAttribute("attendancePercentage", attendancePercentage);
        model.addAttribute("marksList", marksService.getMarksForStudent(studentId));
        model.addAttribute("outpassList", outpassService.getOutpassForStudent(studentId));

        return "dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
    @PostMapping("/register")
    public String processRegister(@RequestParam String name,
                                  @RequestParam String rollNumber,
                                  @RequestParam String department,
                                  @RequestParam String email,
                                  @RequestParam String password,
                                  Model model) {
        try {
            Student student = new Student();
            student.setName(name);
            student.setRollNumber(rollNumber);
            student.setDepartment(department);
            student.setEmail(email);
            student.setPassword(password);
            studentService.registerStudent(student);
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", "Registration failed. Email or roll number may already be in use.");
            return "register";
        }
    }
}