package portal.service;

import portal.model.Outpass;
import portal.model.Student;
import portal.repository.OutpassRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutpassServiceTest {

    @Mock
    private OutpassRepository outpassRepository;

    @InjectMocks
    private OutpassService outpassService;

    @Test
    void approvingOutpassSetsCorrectStatus() {
        Outpass outpass = new Outpass();
        outpass.setId(1L);
        outpass.setStudent(new Student());
        outpass.setStatus("PENDING");

        when(outpassRepository.findById(1L)).thenReturn(java.util.Optional.of(outpass));
        when(outpassRepository.save(any(Outpass.class))).thenAnswer(i -> i.getArgument(0));

        Outpass result = outpassService.approveOutpass(1L, "hod@college.edu");

        assertEquals("APPROVED", result.getStatus());
        assertEquals("hod@college.edu", result.getApprovedBy());
    }

    @Test
    void rejectingOutpassSetsCorrectStatus() {
        Outpass outpass = new Outpass();
        outpass.setId(2L);
        outpass.setStudent(new Student());
        outpass.setStatus("PENDING");

        when(outpassRepository.findById(2L)).thenReturn(java.util.Optional.of(outpass));
        when(outpassRepository.save(any(Outpass.class))).thenAnswer(i -> i.getArgument(0));

        Outpass result = outpassService.rejectOutpass(2L, "hod@college.edu");

        assertEquals("REJECTED", result.getStatus());
    }
}