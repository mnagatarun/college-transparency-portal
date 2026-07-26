package portal.controller;

import portal.model.Outpass;
import portal.service.OutpassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/outpass")
public class OutpassController {

    @Autowired
    private OutpassService outpassService;

    @PostMapping("/request")
    public Outpass requestOutpass(
            @RequestParam Long studentId,
            @RequestParam String reason,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime outTime) {
        return outpassService.requestOutpass(studentId, reason, outTime);
    }

    @PostMapping("/approve/{outpassId}")
    public Outpass approveOutpass(@PathVariable Long outpassId, @RequestParam String approvedBy) {
        return outpassService.approveOutpass(outpassId, approvedBy);
    }

    @PostMapping("/reject/{outpassId}")
    public Outpass rejectOutpass(@PathVariable Long outpassId, @RequestParam String approvedBy) {
        return outpassService.rejectOutpass(outpassId, approvedBy);
    }

    @GetMapping("/pending")
    public List<Outpass> getAllPendingRequests() {
        return outpassService.getAllPendingRequests();
    }

    @GetMapping("/student/{studentId}")
    public List<Outpass> getOutpassForStudent(@PathVariable Long studentId) {
        return outpassService.getOutpassForStudent(studentId);
    }
}