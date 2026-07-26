package portal.controller;

import portal.model.Submission;
import portal.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

    @Autowired
    private SubmissionService submissionService;

    @PostMapping("/create")
    public Submission createRequirement(
            @RequestParam Long studentId,
            @RequestParam String subject,
            @RequestParam String title,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate) {
        return submissionService.createRequirement(studentId, subject, title, dueDate);
    }

    @PostMapping("/submit/{submissionId}")
    public Submission markSubmitted(@PathVariable Long submissionId) {
        return submissionService.markSubmitted(submissionId);
    }

    @PostMapping("/verify/{submissionId}")
    public Submission verifySubmission(@PathVariable Long submissionId, @RequestParam String verifiedBy) {
        return submissionService.verifySubmission(submissionId, verifiedBy);
    }

    @GetMapping("/dashboard")
    public List<Submission> getAllWithOverdueCheck() {
        return submissionService.getAllWithOverdueCheck();
    }

    @GetMapping("/student/{studentId}")
    public List<Submission> getSubmissionsForStudent(@PathVariable Long studentId) {
        return submissionService.getSubmissionsForStudent(studentId);
    }
}