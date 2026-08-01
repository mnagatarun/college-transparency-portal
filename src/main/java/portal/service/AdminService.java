package portal.service;

import portal.model.Student;
import portal.model.MarksAuditLog;
import portal.repository.StudentRepository;
import portal.repository.MarksAuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AdminService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private MarksAuditLogRepository marksAuditLogRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Student> getAllUsers() {
        return studentRepository.findAll();
    }

    public Student createFaculty(String name, String facultyId, String department, String email, String password) {
        Student faculty = new Student();
        faculty.setName(name);
        faculty.setRollNumber(facultyId);
        faculty.setDepartment(department);
        faculty.setEmail(email);
        faculty.setPassword(passwordEncoder.encode(password));
        faculty.setRole("FACULTY");
        return studentRepository.save(faculty);
    }

    public Student toggleAccountStatus(Long userId) {
        Student user = studentRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(!user.isEnabled());
        return studentRepository.save(user);
    }

    public Student resetPassword(Long userId, String newPassword) {
        Student user = studentRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        return studentRepository.save(user);
    }

    public List<MarksAuditLog> getAllAuditLogs() {
        return marksAuditLogRepository.findAll();
    }
}