package kz.aitu.abiturqueue.controller;

import kz.aitu.abiturqueue.model.dto.CreatedTicketHouseDto;
import kz.aitu.abiturqueue.model.dto.TicketStatisticDTO;
import kz.aitu.abiturqueue.model.entity.TicketHouse;
import kz.aitu.abiturqueue.repository.TicketHouseRepository;
import kz.aitu.abiturqueue.service.TicketHouseService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Data
@AllArgsConstructor
@RestController
@RequestMapping("/api/tickets-house")
public class TicketHouseController {

    private final TicketHouseRepository ticketRepository;
    private final TicketHouseService ticketService;

    @GetMapping("/wait/10")
    public List<TicketHouse> getFirst10WaitingTickets() {
        return ticketRepository.findByStatusOrderByStartWaitingTimestampAsc("WAIT", PageRequest.of(0, 10));
    }

    @GetMapping("/created")
    public CreatedTicketHouseDto getCreatedTickets() {

        var ticketBasic = ticketRepository.findFirstByStatusAndTypeOrderByNumberAsc("CREATED", "BASIC").orElse(null);
        var ticketBenefit = ticketRepository.findFirstByStatusAndTypeOrderByNumberAsc("CREATED", "BENEFIT").orElse(null);

        return new CreatedTicketHouseDto(ticketBasic, ticketBenefit);
    }

    @GetMapping("/wait")
    public List<TicketHouse> getWaitTickets() {
        return ticketRepository.findByStatusOrderByStartWaitingTimestampAsc("WAIT");
    }

    @GetMapping("/served")
    public List<TicketHouse> getServedTickets() {
        return ticketRepository.findByStatusOrderByStartWaitingTimestampAsc("SERVED");
    }
    @GetMapping("/coworking")
    public List<TicketHouse> getCoworkingTickets() {
        return ticketRepository.findByStatusOrderByStartWaitingTimestampAsc("COWORKING");
    }

    @GetMapping("/check")
    public List<TicketHouse> getCheckTickets() {
        return ticketRepository.findByStatusOrderByStartWaitingTimestampAsc("CHECK");
    }

    @GetMapping("/served/{type}")
    public List<TicketHouse> getServedTickets(@PathVariable String type) {
        return ticketRepository.findByStatusAndTypeOrderByStartWaitingTimestampAsc("SERVED", type);
    }

    @GetMapping("/progress")
    public List<TicketHouse> getProgressTickets() {
        return ticketRepository.findByStatusOrderByStartWaitingTimestampAsc("PROGRESS");
    }

    @GetMapping("/admin")
    public List<TicketHouse> getTickets() {
        LocalDate today = LocalDate.now();
        long startOfToday = today.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000;

        return ticketRepository.findAll()
                .stream()
                .filter(ticket -> !"CREATED".equals(ticket.getStatus()))
                .filter(ticket -> ticket.getStartWaitingTimestamp() >= startOfToday)
                //.filter(ticket -> ticket.getCreatedTimestamp() >= startOfToday)
                .collect(Collectors.toList());
    }

    @GetMapping("/statistics")
    public TicketStatisticDTO getStatistics() {
        LocalDate date = LocalDate.now();
        ZoneId zoneId = ZoneId.systemDefault(); // or: ZoneId.of("Asia/Almaty");
        var startOfToday = date.atStartOfDay(zoneId).toEpochSecond() * 1000;

        var stats = TicketStatisticDTO.builder()
                .progressCount(ticketRepository.countAllByStatusAndStartInProgressTimestampAfter("PROGRESS", startOfToday))
                .servedCount(ticketRepository.countAllByStatus("SERVED"))
                .waitCount(ticketRepository.countAllByStatus("WAIT"))
                .createdCount(ticketRepository.countAllByStatus("CREATED"))
                .coworkingCount(ticketRepository.countAllByStatus("COWORKING"))
                .build();

        return stats;
    }

    @GetMapping("/statistics/wait")
    public double getStatisticsWait() {
        return ticketService.calculateClientWaitTime();
    }

    @GetMapping("/find-by-id")
    public ResponseEntity<TicketHouse> findTicketById(@RequestParam Long id){
        Optional<TicketHouse> ticket = ticketService.getTicketHouseById(id);
        return ticket.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }
}


