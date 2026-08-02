package portal.service;

import portal.model.Student;
import portal.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired private StudentRepository studentRepository;
    @Autowired private JavaMailSender mailSender;
    @Autowired private PasswordEncoder passwordEncoder;

    @Value("${app.base-url}")
    private String baseUrl;

    public void sendResetLink(String email) {
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No account with that email"));

        String token = UUID.randomUUID().toString();
        student.setResetToken(token);
        student.setResetTokenExpiry(LocalDateTime.now().plusMinutes(30));
        studentRepository.save(student);

        String link = baseUrl + "/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset - College Portal");
        message.setText("Click to reset your password (valid 30 minutes): " + link);
        mailSender.send(message);
    }

    public void resetPassword(String token, String newPassword) {
        Student student = studentRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired reset link"));

        if (student.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset link expired");
        }

        student.setPassword(passwordEncoder.encode(newPassword));
        student.setResetToken(null);
        student.setResetTokenExpiry(null);
        studentRepository.save(student);
    }
}