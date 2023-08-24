package kz.aitu.abiturqueue.service;

import kz.aitu.abiturqueue.exception.ResourceNotFoundException;
import kz.aitu.abiturqueue.model.entity.Ticket;
import kz.aitu.abiturqueue.repository.TicketRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Data
@AllArgsConstructor
public class TicketService {

    private TicketRepository ticketRepository;
    private Environment environment;
    private static final Logger log = LoggerFactory.getLogger(TicketService.class);



    //Status created
    public List<Ticket> addNewTickets(Integer count) {
        var now = new Date();
        var ticketList = new ArrayList<Ticket>(count);
        for (int i = 0; i < count; i++) {
            var ticket = Ticket.builder()
                    .createdTimestamp(now.getTime())
                    .status("CREATED")
                    .build();
            ticketList.add(ticket);
        }
        return ticketRepository.saveAll(ticketList);
    }


    //Status WAIT auto
    public List<Ticket> addNewTicketsV2() {
        var now = new Date();
        var ticketList = new ArrayList<Ticket>(700);
        for (int i = 0; i < 500; i++) {
            var ticket = Ticket.builder()
                    .createdTimestamp(now.getTime() + i)
                    .status("CREATED")
                    //.startWaitingTimestamp(now.getTime() + i)
                    .number(i + 1)
                    .type("BASIC")
                    .build();
            ticketList.add(ticket);
        }
        for (int i = 500; i < 700; i++) {
            var ticket = Ticket.builder()
                    .createdTimestamp(now.getTime() + i)
                    .status("CREATED")
                    //.startWaitingTimestamp(now.getTime() + i)
                    .number(i + 1)
                    .type("BENEFIT")
                    .build();
            ticketList.add(ticket);
        }
        return ticketRepository.saveAll(ticketList);
    }

    public void clearAllTickets() {
        LocalDate date = LocalDate.now();
        ZoneId zoneId = ZoneId.systemDefault(); // or: ZoneId.of("Asia/Almaty");
        var startOfToday = date.atStartOfDay(zoneId).toEpochSecond() * 1000;
        log.info("Tickets cleaning from {}", startOfToday);

        var ticketIdList = ticketRepository.findAll()
                .stream()
                .filter(ticket -> !"PROGRESS".equals(ticket.getStatus()))
                .filter(ticket -> !"CANCEL".equals(ticket.getStatus()))
                .filter(ticket -> ticket.getCreatedTimestamp() <= startOfToday)
                .map(Ticket::getId)
                .toList();

        ticketRepository.deleteAllById(ticketIdList);
    }

    @Scheduled(cron = "0 01 0 * * ?")
    public void cleanAndCreateTickets() {
        clearAllTickets();
        addNewTicketsV2();
        /*String[] activeProfiles = environment.getActiveProfiles();
        for (String profile : activeProfiles) {
            //if (profile.equalsIgnoreCase("prod")) {
                log.info("Cleaning tickets started...");
                clearAllTickets();
                addNewTicketsV2();
            //}
        }*/
    }

    public Ticket inviteNextTicket(String type) {
        log.info("Getting first waiting ticket");
        var nextTicket = ticketRepository.findFirstByStatusAndTypeOrderByStartWaitingTimestampAsc("WAIT", type)
                .orElseGet(() -> ticketRepository.findFirstByStatusAndTypeOrderByStartWaitingTimestampAsc("WAIT", "BASIC")
                        .orElseThrow(() -> new ResourceNotFoundException("No waiting tickets found")));


        log.info("Retrieved ticket: {}", nextTicket);
        nextTicket.setStatus("SERVED");
        nextTicket.setStartServedTimestamp(System.currentTimeMillis());
        nextTicket.setType(type);
        return ticketRepository.save(nextTicket);
    }

    public Ticket toProgressTicket(Long id) {
        log.info("Moving ticket with id {} to progress", id);
        var ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id " + id));

        if(!ticket.getStatus().equals("SERVED")) {
            throw new IllegalStateException("Ticket with id " + id + " is not in WAIT status");
        }

        ticket.setStatus("PROGRESS");
        ticket.setStartInProgressTimestamp(System.currentTimeMillis());

        var updatedTicket = ticketRepository.save(ticket);
        log.info("Moved ticket to progress: {}", updatedTicket);
        return updatedTicket;
    }
    public Ticket toWaitTicket(Long id) {
        log.info("Moving ticket with id {} to wait", id);
        var ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id " + id));

        if(!ticket.getStatus().equals("CREATED")) {
            throw new IllegalStateException("Ticket with id " + id + " is not in CREATED status");
        }

        ticket.setStatus("WAIT");
        ticket.setStartWaitingTimestamp(System.currentTimeMillis());

        var updatedTicket = ticketRepository.save(ticket);
        log.info("Moved ticket to wait: {}", updatedTicket);
        return updatedTicket;
    }
    public Ticket toCancelTicket(Long id) {
        log.info("Moving ticket with id {} to cancel", id);
        var ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id " + id));

        if(!ticket.getStatus().equals("SERVED")) {
            throw new IllegalStateException("Ticket with id " + id + " is not in CREATED status");
        }

        ticket.setStatus("CANCEL");
        ticket.setStartCancelTimestamp(System.currentTimeMillis());

        var updatedTicket = ticketRepository.save(ticket);
        log.info("Moved ticket to wait: {}", updatedTicket);
        return updatedTicket;
    }
    public void toDeleteTicket(Long id) {
        log.info("Moving ticket with id {} to delete", id);
        var ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id " + id));

        ticketRepository.delete(ticket);
    }

    public double calculateClientWaitTime() {
        LocalDate date = LocalDate.now();
        ZoneId zoneId = ZoneId.systemDefault(); // or: ZoneId.of("Asia/Almaty");
        var startOfToday = date.atStartOfDay(zoneId).toEpochSecond() * 1000;
        log.info("Tickets cleaning from {}", startOfToday);

        double average = ticketRepository.findByStatusAndStartInProgressTimestampAfter("PROGRESS", startOfToday)
                .stream()
                .mapToLong(ticket -> (ticket.getStartInProgressTimestamp() - ticket.getStartWaitingTimestamp()) / 1000)
                .average()
                .orElse(0.0);
        return average;
    }
}
