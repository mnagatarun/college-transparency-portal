package portal.service;

import portal.model.Student;
import portal.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Map<String, Integer> failedAttempts = new ConcurrentHashMap<>();

    public Student registerStudent(Student student) {
        String hashedPassword = passwordEncoder.encode(student.getPassword());
        student.setPassword(hashedPassword);
        Student saved = studentRepository.save(student);
        log.info("New student registered: {} (roll number: {})", saved.getEmail(), saved.getRollNumber());
        return saved;
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
    }

    public Student loginStudent(String email, String rawPassword) {

        int attempts = failedAttempts.getOrDefault(email, 0);
        if (attempts >= 5) {
            throw new RuntimeException("Too many failed attempts. Try again later.");
        }

        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Login attempt failed - email not found: {}", email);
                    return new RuntimeException("Invalid email or password");
                });

        if (!passwordEncoder.matches(rawPassword, student.getPassword())) {
            log.warn("Login attempt failed - incorrect password for email: {}", email);
            failedAttempts.merge(email, 1, Integer::sum);
            throw new RuntimeException("Invalid email or password");
        }

        if (!student.isEnabled()) {
            log.warn("Login attempt blocked - disabled account: {}", email);
            throw new RuntimeException("This account has been disabled. Contact admin.");
        }

        failedAttempts.remove(email);
        log.info("Successful login: {} (role: {})", email, student.getRole());
        return student;
    }
}