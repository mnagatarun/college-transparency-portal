package portal.controller;

import portal.model.Marks;
import portal.model.MarksAuditLog;
import portal.service.MarksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/marks")
public class MarksController {

    @Autowired
    private MarksService marksService;

    @PostMapping("/add")
    public Marks addOrUpdateMarks(
            @RequestParam Long studentId,
            @RequestParam String subject,
            @RequestParam String examType,
            @RequestParam Double marksObtained,
            @RequestParam Double maxMarks,
            @RequestParam String changedBy) {
        return marksService.addOrUpdateMarks(studentId, subject, examType, marksObtained, maxMarks, changedBy);
    }

    @GetMapping("/student/{studentId}")
    public List<Marks> getMarksForStudent(@PathVariable Long studentId) {
        return marksService.getMarksForStudent(studentId);
    }

    @GetMapping("/audit/{marksId}")
    public List<MarksAuditLog> getAuditHistory(@PathVariable Long marksId) {
        return marksService.getAuditHistory(marksId);
    }
}