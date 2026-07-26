package portal.service;

import portal.model.Submission;
import portal.model.Student;
import portal.repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class SubmissionService {

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private StudentService studentService;

    public Submission createRequirement(Long studentId, String subject, String title, LocalDate dueDate) {
        Student student = studentService.getStudentById(studentId);

        Submission submission = new Submission();
        submission.setStudent(student);
        submission.setSubject(subject);
        submission.setTitle(title);
        submission.setDueDate(dueDate);
        submission.setStatus("PENDING");

        return submissionRepository.save(submission);
    }

    public Submission markSubmitted(Long submissionId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        submission.setStatus("SUBMITTED");
        submission.setSubmittedDate(LocalDate.now());

        return submissionRepository.save(submission);
    }

    public Submission verifySubmission(Long submissionId, String verifiedBy) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        submission.setStatus("VERIFIED");
        submission.setVerifiedBy(verifiedBy);

        return submissionRepository.save(submission);
    }

    public List<Submission> getAllWithOverdueCheck() {
        List<Submission> pending = submissionRepository.findByStatus("PENDING");

        for (Submission s : pending) {
            if (s.getDueDate().isBefore(LocalDate.now())) {
                s.setStatus("OVERDUE");
                submissionRepository.save(s);
            }
        }

        List<Submission> stillPending = submissionRepository.findByStatus("PENDING");
        List<Submission> overdue = submissionRepository.findByStatus("OVERDUE");
        stillPending.addAll(overdue);
        return stillPending;
    }

    public List<Submission> getSubmissionsForStudent(Long studentId) {
        return submissionRepository.findByStudentId(studentId);
    }
}