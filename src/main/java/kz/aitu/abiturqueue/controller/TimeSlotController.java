package kz.aitu.abiturqueue.controller;

import kz.aitu.abiturqueue.model.entity.Ticket;
import kz.aitu.abiturqueue.model.entity.TimeSlot;
import kz.aitu.abiturqueue.repository.TicketRepository;
import kz.aitu.abiturqueue.repository.TimeSlotRepository;
import kz.aitu.abiturqueue.service.TimeSlotService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Data
@AllArgsConstructor
@RestController
@RequestMapping("/api/time-slot")
public class TimeSlotController {

    private final TimeSlotRepository timeSlotRepository;
    private final TimeSlotService timeSlotService;
    private final TicketRepository ticketRepository;

    @GetMapping("/get-all")
    public List<TimeSlot> getAll() {
        return timeSlotService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimeSlot> getById(@PathVariable(value = "id") Long id){
        TimeSlot timeSlot = this.timeSlotService.getByIdThrowException(id);
        return ResponseEntity.ok().body(timeSlot);
    }

    @GetMapping("/get-all-null-ticket")
    public List<TimeSlot> getAllNullTickets() {
        return timeSlotRepository.findByTicketIdIsNull();
    }

    @PostMapping("/assign-ticket")
    public TimeSlot assignTicketToTimeSlot(@RequestParam Long ticketId, @RequestParam Long timeSlotId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        TimeSlot timeSlot = timeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new RuntimeException("Time Slot not found"));

        timeSlot.setTicketId(ticket.getId());
        return timeSlotRepository.save(timeSlot);
    }

    @GetMapping("/is-user-select/{userId}")
    public boolean isUserSelect(@PathVariable Long userId){
        return timeSlotService.isUserSelectTheTimeSlot(userId);
    }
}
