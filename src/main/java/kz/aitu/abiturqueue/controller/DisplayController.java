package kz.aitu.abiturqueue.controller;

import kz.aitu.abiturqueue.model.dto.CreatedTicketDTO;
import kz.aitu.abiturqueue.model.dto.TicketStatisticDTO;
import kz.aitu.abiturqueue.model.entity.Ticket;
import kz.aitu.abiturqueue.repository.TicketRepository;
import kz.aitu.abiturqueue.service.TicketService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@RestController
@RequestMapping("/api/display")
public class DisplayController {

    private final TicketRepository ticketRepository;
    private final TicketService ticketService;

    @GetMapping("/tickets/wait/10")
    public List<Ticket> getFirst10WaitingTickets() {
        return ticketRepository.findByStatusOrderByStartWaitingTimestampAsc("WAIT", PageRequest.of(0, 10));
    }

    @GetMapping("/tickets/created")
    public CreatedTicketDTO getCreatedTickets() {

        var ticketBasic = ticketRepository.findFirstByStatusAndTypeOrderByNumberAsc("CREATED", "BASIC").orElse(null);
        var ticketBenefit = ticketRepository.findFirstByStatusAndTypeOrderByNumberAsc("CREATED", "BENEFIT").orElse(null);

        return new CreatedTicketDTO(ticketBasic, ticketBenefit);
    }

    @GetMapping("/tickets/wait")
    public List<Ticket> getWaitTickets() {
        return ticketRepository.findByStatusOrderByStartWaitingTimestampAsc("WAIT");
    }

    @GetMapping("/tickets/served/table/{tableNum}")
    public List<Ticket> getServedTickets(@PathVariable Integer tableNum) {
        return ticketRepository.findByStatusAndTableNumberOrderByStartWaitingTimestampAsc("SERVED", tableNum);
    }

    @GetMapping("/tickets/coworking")
    public List<Ticket> getCoworkingTickets() {
        return ticketRepository.findByStatusOrderByStartWaitingTimestampAsc("COWORKING");
    }

    @GetMapping("/tickets/served")
    public List<Ticket> getServedTickets() {
        return ticketRepository.findByStatusOrderByStartWaitingTimestampAsc("SERVED");
    }

    @GetMapping("/tickets/served/{type}")
    public List<Ticket> getServedTickets(@PathVariable String type) {
        return ticketRepository.findByStatusAndTypeOrderByStartWaitingTimestampAsc("SERVED", type);
    }

    @GetMapping("/tickets/progress")
    public List<Ticket> getProgressTickets() {
        return ticketRepository.findByStatusOrderByStartWaitingTimestampAsc("PROGRESS");
    }

    @GetMapping("/tickets/admin")
    public List<Ticket> getTickets() {
        LocalDate today = LocalDate.now();
        long startOfToday = today.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000;

        return ticketRepository.findAll()
                .stream()
                .filter(ticket -> !"CREATED".equals(ticket.getStatus()))
                .filter(ticket -> ticket.getStartWaitingTimestamp() >= startOfToday)
                //.filter(ticket -> ticket.getCreatedTimestamp() >= startOfToday)
                .collect(Collectors.toList());
    }

    @GetMapping("/tickets/statistics")
    public TicketStatisticDTO getStatistics() {
        LocalDate date = LocalDate.now();
        ZoneId zoneId = ZoneId.systemDefault(); // or: ZoneId.of("Asia/Almaty");
        var startOfToday = date.atStartOfDay(zoneId).toEpochSecond() * 1000;

        var stats = TicketStatisticDTO.builder()
                .progressCount(ticketRepository.countAllByStatusAndStartInProgressTimestampAfter("PROGRESS", startOfToday))
                .servedCount(ticketRepository.countAllByStatus("SERVED"))
                .waitCount(ticketRepository.countAllByStatus("WAIT"))
                .createdCount(ticketRepository.countAllByStatus("CREATED"))
                .build();

        return stats;
    }

    @GetMapping("/tickets/statistics/wait")
    public double getStatisticsWait() {
        return ticketService.calculateClientWaitTime();
    }

    @GetMapping("/start")
    public String cleanAndCreateTickets() {
        ticketService.cleanAndCreateTickets();
        return null;
    }
}
