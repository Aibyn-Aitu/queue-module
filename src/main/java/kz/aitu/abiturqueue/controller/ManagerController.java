package kz.aitu.abiturqueue.controller;

import kz.aitu.abiturqueue.model.entity.Ticket;
import kz.aitu.abiturqueue.service.TicketService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Data
@AllArgsConstructor
@RestController
@RequestMapping("/api/manager")
public class ManagerController {

    private final TicketService ticketService;

    // ... ваш код ...

    @PostMapping("/invite/{type}")
    public ResponseEntity<Ticket> inviteNextTicket(@PathVariable String type) {

        return ResponseEntity.ok(ticketService.inviteNextTicket(type));
    }

    @PutMapping("/{id}/toProgress")
    public ResponseEntity<Ticket> toProgressTicket(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.toProgressTicket(id));
    }

    @PutMapping("/{id}/toWait")
    public ResponseEntity<Ticket> toWaitTicket(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.toWaitTicket(id));
    }

    @PutMapping("/{id}/toCancel")
    public ResponseEntity<Ticket> toCancelTicket(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.toCancelTicket(id));
    }

    @PutMapping("/{id}/toDelete")
    public ResponseEntity<Ticket> toDeleteTicket(@PathVariable Long id) {
        ticketService.toDeleteTicket(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/createTicket/{count}")
    public ResponseEntity<List<Ticket>> addNewTickets(@PathVariable("count") Integer count) {
        return ResponseEntity.ok(ticketService.addNewTicketsV2());
    }
}
