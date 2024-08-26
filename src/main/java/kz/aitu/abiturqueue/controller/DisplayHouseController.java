package kz.aitu.abiturqueue.controller;

import kz.aitu.abiturqueue.model.dto.CreatedTicketHouseDto;
import kz.aitu.abiturqueue.model.dto.TicketStatisticDTO;
import kz.aitu.abiturqueue.model.entity.Ticket;
import kz.aitu.abiturqueue.model.entity.TicketHouse;
import kz.aitu.abiturqueue.repository.TicketHouseRepository;
import kz.aitu.abiturqueue.service.TicketHouseService;
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
@RequestMapping("/api/display-house")
public class DisplayHouseController {

    private final TicketHouseRepository ticketRepository;
    private final TicketHouseService ticketService;

    @GetMapping("/tickets/wait/10")
    public List<TicketHouse> getFirst10WaitingTickets() {
        return ticketRepository.findByStatusOrderByStartWaitingTimestampAsc("WAIT", PageRequest.of(0, 10));
    }

    @GetMapping("/tickets/created")
    public CreatedTicketHouseDto getCreatedTickets() {

        var ticketBasic = ticketRepository.findFirstByStatusAndTypeOrderByNumberAsc("CREATED", "BASIC").orElse(null);
        var ticketBenefit = ticketRepository.findFirstByStatusAndTypeOrderByNumberAsc("CREATED", "BENEFIT").orElse(null);

        return new CreatedTicketHouseDto(ticketBasic, ticketBenefit);
    }

    @GetMapping("/tickets/wait")
    public List<TicketHouse> getWaitTickets() {
        return ticketRepository.findByStatusOrderByStartWaitingTimestampAsc("WAIT");
    }

    @GetMapping("/tickets/served/table/{tableNum}")
    public List<TicketHouse> getServedTickets(@PathVariable Integer tableNum) {
        return ticketRepository.findByStatusAndTableNumberOrderByStartWaitingTimestampAsc("SERVED", tableNum);
    }

    @GetMapping("/tickets/served/benefit/table/{tableNum}")
    public List<TicketHouse> getServedBenefitTickets(@PathVariable Integer tableNum) {
        return ticketRepository.findByStatusAndTableNumberAndTypeOrderByStartWaitingTimestampAsc("SERVED", tableNum, "BENEFIT");
    }

    @GetMapping("/tickets/coworking")
    public List<TicketHouse> getCoworkingTickets() {
        return ticketRepository.findByStatusOrderByStartCoworkingTimestampAsc("COWORKING");
    }

    @GetMapping("/tickets/ready")
    public List<TicketHouse> getReadyTickets() {
        return ticketRepository.findByStatusOrderByStartReadyTimestampAsc("READY");
    }

    @GetMapping("/tickets/not-created")
    public List<TicketHouse> getNotCreatedTickets() {
        return ticketService.getTodayTicketHousesExcludingStatus("CREATED");
    }

    @GetMapping("/tickets/check")
    public List<TicketHouse> getCheckTickets() {
        return ticketRepository.findByStatusOrderByStartCheckTimestampAsc("CHECK");
    }

    @GetMapping("/tickets/served")
    public List<TicketHouse> getServedTickets() {
        return ticketRepository.findByStatusOrderByStartWaitingTimestampAsc("SERVED");
    }

    @GetMapping("/tickets/served/boys")
    public List<TicketHouse> getServedBoysTickets() {
        return ticketRepository.findByStatusAndTypeOrderByStartAddedTimestampAsc("SERVED", "BASIC");
    }

    @GetMapping("/tickets/served/girls")
    public List<TicketHouse> getServedGirlsTickets() {
        return ticketRepository.findByStatusAndTypeOrderByStartAddedTimestampAsc("SERVED", "BENEFIT");
    }

    @GetMapping("/tickets/added")
    public List<TicketHouse> getAddedTickets() {
        return ticketRepository.findByStatusOrderByStartAddedTimestampAsc("ADDED");
    }

    @GetMapping("/tickets/served/{type}")
    public List<TicketHouse> getServedTickets(@PathVariable String type) {
        return ticketRepository.findByStatusAndTypeOrderByStartWaitingTimestampAsc("SERVED", type);
    }

    @GetMapping("/tickets/progress")
    public List<TicketHouse> getProgressTickets() {
        return ticketRepository.findByStatusOrderByStartWaitingTimestampAsc("PROGRESS");
    }

    @GetMapping("/tickets/admin")
    public List<TicketHouse> getTickets() {
        LocalDate today = LocalDate.now();
        long startOfToday = today.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000;

        return ticketRepository.findAll()
                .stream()
                .filter(ticket -> !"CREATED".equals(ticket.getStatus()))
                .filter(ticket -> ticket.getStartAddedTimestamp() >= startOfToday)
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
                .coworkingCount(ticketRepository.countAllByStatus("COWORKING"))
                .checkCount(ticketRepository.countAllByStatus("CHECK"))
                .addedCount(ticketRepository.countAllByStatus("ADDED"))
                .readyCount(ticketRepository.countAllByStatus("READY"))
                .build();

        return stats;
    }

    @GetMapping("/tickets/statistics/wait")
    public double getStatisticsWait() {
        return ticketService.calculateClientWaitTime();
    }

    @GetMapping("/start")
    public String cleanAndCreateTickets() {
        ticketService.cleanAndCreateTicketHouses();
        return null;
    }
}
