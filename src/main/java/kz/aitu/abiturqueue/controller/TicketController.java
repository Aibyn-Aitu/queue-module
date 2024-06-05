package kz.aitu.abiturqueue.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import kz.aitu.abiturqueue.model.dto.CreatedTicketDTO;
import kz.aitu.abiturqueue.model.dto.TicketStatisticDTO;
import kz.aitu.abiturqueue.model.entity.Ticket;
import kz.aitu.abiturqueue.model.entity.User;
import kz.aitu.abiturqueue.repository.TicketRepository;
import kz.aitu.abiturqueue.service.TicketService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;


@Data
@AllArgsConstructor
@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketRepository ticketRepository;
    private final TicketService ticketService;

    @GetMapping("/wait/10")
    public List<Ticket> getFirst10WaitingTickets() {
        return ticketRepository.findByStatusOrderByStartWaitingTimestampAsc("WAIT", PageRequest.of(0, 10));
    }

    @GetMapping("/created")
    public CreatedTicketDTO getCreatedTickets() {

        var ticketBasic = ticketRepository.findFirstByStatusAndTypeOrderByNumberAsc("CREATED", "BASIC").orElse(null);
        var ticketBenefit = ticketRepository.findFirstByStatusAndTypeOrderByNumberAsc("CREATED", "BENEFIT").orElse(null);

        return new CreatedTicketDTO(ticketBasic, ticketBenefit);
    }

    @GetMapping("/wait")
    public List<Ticket> getWaitTickets() {
        return ticketRepository.findByStatusOrderByStartWaitingTimestampAsc("WAIT");
    }

    @GetMapping("/served")
    public List<Ticket> getServedTickets() {
        return ticketRepository.findByStatusOrderByStartWaitingTimestampAsc("SERVED");
    }

    @GetMapping("/served/{type}")
    public List<Ticket> getServedTickets(@PathVariable String type) {
        return ticketRepository.findByStatusAndTypeOrderByStartWaitingTimestampAsc("SERVED", type);
    }

    @GetMapping("/progress")
    public List<Ticket> getProgressTickets() {
        return ticketRepository.findByStatusOrderByStartWaitingTimestampAsc("PROGRESS");
    }

    @GetMapping("/admin")
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
                .build();

        return stats;
    }

    @GetMapping("/statistics/wait")
    public double getStatisticsWait() {
        return ticketService.calculateClientWaitTime();
    }

    @GetMapping("/find-by-id")
    public ResponseEntity<Ticket> findTicketById(@RequestParam Long id){
        Optional<Ticket> ticket = ticketService.getTicketById(id);
        return ticket.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }
}


