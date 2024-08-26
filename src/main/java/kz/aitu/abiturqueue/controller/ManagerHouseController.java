package kz.aitu.abiturqueue.controller;

import kz.aitu.abiturqueue.model.entity.Ticket;
import kz.aitu.abiturqueue.model.entity.TicketHouse;
import kz.aitu.abiturqueue.service.TicketHouseService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Data
@AllArgsConstructor
@RestController
@RequestMapping("/api/manager-house")
public class ManagerHouseController {

    private final TicketHouseService ticketService;

    // ... ваш код ...

    @PostMapping("/invite/{type}/{table}")
    public ResponseEntity<TicketHouse> inviteNextTicket(@PathVariable String type, @PathVariable Integer table) {

        return ResponseEntity.ok(ticketService.inviteNextTicketHouseToTable(type, table));
    }

    @PostMapping("/invite/{type}")
    public ResponseEntity<TicketHouse> inviteNextTicketToCoworking(@PathVariable String type) {

        return ResponseEntity.ok(ticketService.inviteNextTicketHouseToCoworking(type));
    }

    @PostMapping("/invite/check/{type}")
    public ResponseEntity<TicketHouse> inviteNextTicketToCheck(@PathVariable String type) {

        return ResponseEntity.ok(ticketService.inviteNextTicketHouseToCheck(type));
    }

    @PostMapping("/invite/benefit/{type}/{table}")
    public ResponseEntity<TicketHouse> inviteNextBenefitTicketToTable(@PathVariable String type, @PathVariable Integer table) {

        return ResponseEntity.ok(ticketService.inviteNextBenefitTicketHouseToTable(type, table));
    }

    @PutMapping("/{id}/toProgress")
    public ResponseEntity<TicketHouse> toProgressTicket(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.toProgressTicketHouse(id));
    }

    @PutMapping("/{id}/toWait")
    public ResponseEntity<TicketHouse> toWaitTicket(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.toWaitTicketHouse(id));
    }

    @PutMapping("/{id}/toAdded")
    public ResponseEntity<TicketHouse> toWaitAdded(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.toAddedTicketHouse(id));
    }

    @PutMapping("/{id}/toReady")
    public ResponseEntity<TicketHouse> toReadyTicket(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.toReadyTicketHouse(id));
    }

    @PutMapping("/{id}/toCancel")
    public ResponseEntity<TicketHouse> toCancelTicket(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.toCancelTicketHouse(id));
    }
    @PutMapping("/{id}/toAddedCancel")
    public ResponseEntity<TicketHouse> toCancelAddedTicket(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.toCancelAddedTicket(id));
    }


    @PutMapping("/{id}/toCancelFromAdmin")
    public ResponseEntity<TicketHouse> toCancelTicketFromAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.toCancelTicketHouseFromAdmin(id));
    }

    @PutMapping("/{id}/toDeleteCheck")
    public ResponseEntity<TicketHouse> toDeleteFromCheck(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.toDeleteFromCheck(id));
    }

    @PutMapping("/{id}/toDelete")
    public ResponseEntity<TicketHouse> toDeleteTicket(@PathVariable Long id) {
        ticketService.toDeleteTicketHouse(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/createTicket/{count}")
    public ResponseEntity<List<TicketHouse>> addNewTickets(@PathVariable("count") Integer count) {
        return ResponseEntity.ok(ticketService.addNewTicketHousesV2());
    }
}
