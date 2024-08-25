package kz.aitu.abiturqueue.controller;

import kz.aitu.abiturqueue.model.entity.Ticket;
import kz.aitu.abiturqueue.model.entity.TimeSlot;
import kz.aitu.abiturqueue.model.entity.User;
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

    @GetMapping("/get-all-online/{tableNumber}")
    public List<TimeSlot> getAllOnline(@PathVariable Long tableNumber) {
        return timeSlotService.getAllOnline(tableNumber);
    }

    @GetMapping("/get-all-table/{tableNumber}")
    public List<TimeSlot> getAllTable(@PathVariable Long tableNumber) {
        return timeSlotService.getAllOnline(tableNumber);
    }


    @GetMapping("/{id}")
    public ResponseEntity<TimeSlot> getById(@PathVariable(value = "id") Long id){
        TimeSlot timeSlot = this.timeSlotService.getByIdThrowException(id);
        return ResponseEntity.ok().body(timeSlot);
    }

    @GetMapping("/get-all-null-ticket")
    public List<TimeSlot> getAllNullTickets() {
        return timeSlotRepository.findByTicketIdIsNullOrderByIdAsc();
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

    @GetMapping("/get-next-online-ticket/{table}")
    public User getFirstOnlineUser(@PathVariable Long table){
        return this.timeSlotService.getFirstOnlineByStatusOrderByTime(table);
    }

    @GetMapping("/is-user-select/{userId}")
    public boolean isUserSelect(@PathVariable Long userId){
        return timeSlotService.isUserSelectTheTimeSlot(userId);
    }

    @PutMapping("/complete/{slotId}")
    public ResponseEntity<TimeSlot> completeSlot(@PathVariable Long slotId) {
        TimeSlot timeSlot = timeSlotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Time Slot not found"));

        timeSlot.setStatus("COMPLETED");
        TimeSlot updatedTimeSlot = timeSlotRepository.save(timeSlot);
        return ResponseEntity.ok(updatedTimeSlot);
    }

    @PostMapping("/invite-next-user/{table}")
    public ResponseEntity<User> inviteNextUser(@PathVariable Long table) {
        User invitedUser = timeSlotService.getFirstOnlineByStatusOrderByTime(table);
        if (invitedUser == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(invitedUser);
    }




}
