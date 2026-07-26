package portal.controller;

import portal.model.Student;
import portal.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping("/register")
    public Student register(@RequestBody Student student) {
        return studentService.registerStudent(student);
    }

    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> credentials, HttpSession session) {
        Student student = studentService.loginStudent(
                credentials.get("email"),
                credentials.get("password")
        );
        session.setAttribute("loggedInStudentId", student.getId());
        session.setAttribute("role", student.getRole());
        return "Login successful. Welcome, " + student.getName();
    }

    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/{id}")
    public Student getStudent(@PathVariable Long id) {
        return studentService.getStudentById(id);
    }
}