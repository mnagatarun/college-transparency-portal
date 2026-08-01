package portal.service;
import org.springframework.scheduling.annotation.Scheduled;
import portal.model.Outpass;
import lombok.extern.slf4j.Slf4j;
import portal.model.Student;
import portal.repository.OutpassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
public class OutpassService {
    @Scheduled(fixedRate = 3600000)
    public void autoEscalatePendingOutpass() {
        List<Outpass> pending = outpassRepository.findByStatus("PENDING");

        for (Outpass o : pending) {
            long hoursWaiting = ChronoUnit.HOURS.between(o.getRequestedAt(), LocalDateTime.now());
            if (hoursWaiting >= ESCALATION_HOURS) {
                o.setStatus("PENDING_URGENT");
                outpassRepository.save(o);
                log.warn("Outpass auto-escalated to urgent - id: {}, student: {}, hoursWaiting: {}",
                        o.getId(), o.getStudent().getName(), hoursWaiting);
            }
        }
    }
    @Autowired
    private OutpassRepository outpassRepository;

    @Autowired
    private StudentService studentService;

    private static final long ESCALATION_HOURS = 2;

    public Outpass requestOutpass(Long studentId, String reason, LocalDateTime outTime) {
        Student student = studentService.getStudentById(studentId);

        Outpass outpass = new Outpass();
        outpass.setStudent(student);
        outpass.setReason(reason);
        outpass.setOutTime(outTime);
        outpass.setRequestedAt(LocalDateTime.now());
        outpass.setStatus("PENDING");

        return outpassRepository.save(outpass);
    }

    public Outpass approveOutpass(Long outpassId, String approvedBy) {
        Outpass outpass = outpassRepository.findById(outpassId)
                .orElseThrow(() -> new RuntimeException("Outpass request not found"));

        outpass.setStatus("APPROVED");
        outpass.setApprovedBy(approvedBy);
        outpass.setApprovedAt(LocalDateTime.now());

        log.info("Outpass approved - id: {}, student: {}, approvedBy: {}",
                outpassId, outpass.getStudent().getName(), approvedBy);

        return outpassRepository.save(outpass);
    }

    public Outpass rejectOutpass(Long outpassId, String approvedBy) {
        Outpass outpass = outpassRepository.findById(outpassId)
                .orElseThrow(() -> new RuntimeException("Outpass request not found"));

        outpass.setStatus("REJECTED");
        outpass.setApprovedBy(approvedBy);
        outpass.setApprovedAt(LocalDateTime.now());

        log.info("Outpass rejected - id: {}, student: {}, rejectedBy: {}",
                outpassId, outpass.getStudent().getName(), approvedBy);

        return outpassRepository.save(outpass);
    }

    public List<Outpass> getAllPendingRequests() {
        List<Outpass> pending = outpassRepository.findByStatus("PENDING");

        for (Outpass o : pending) {
            long hoursWaiting = ChronoUnit.HOURS.between(o.getRequestedAt(), LocalDateTime.now());
            if (hoursWaiting >= ESCALATION_HOURS) {
                o.setStatus("PENDING_URGENT");
                outpassRepository.save(o);
            }
        }

        List<Outpass> stillPending = outpassRepository.findByStatus("PENDING");
        List<Outpass> urgent = outpassRepository.findByStatus("PENDING_URGENT");
        stillPending.addAll(urgent);
        return stillPending;
    }

    public List<Outpass> getOutpassForStudent(Long studentId) {
        return outpassRepository.findByStudentId(studentId);
    }
}