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
import java.util.*;
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

    public List<Ticket> getTodayTicketsExcludingStatus(String status) {
        LocalDate date = LocalDate.now();
        ZoneId zoneId = ZoneId.systemDefault();
        Long startOfDay = date.atStartOfDay(zoneId).toEpochSecond() * 1000 - 68400000;
        System.out.println(startOfDay);

        return ticketRepository.findByStatusNotAndCreatedTodayOrderByNumberAsc(status, startOfDay);
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
        for (int i = 700; i < 900; i++) {
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

    public Ticket inviteNextTicketToTable(String type, Integer table) {
        log.info("Getting first waiting ticket");
        var nextTicket = ticketRepository.findFirstByStatusAndTypeOrderByStartReadyTimestampAsc("READY", type)
                .orElseGet(() -> ticketRepository.findFirstByStatusAndTypeOrderByStartReadyTimestampAsc("READY", "BASIC")
                        .orElseThrow(() -> new ResourceNotFoundException("No waiting tickets found")));


        log.info("Retrieved ticket: {}", nextTicket);
        nextTicket.setStatus("SERVED");
        nextTicket.setTableNumber(table);
        nextTicket.setStartServedTimestamp(System.currentTimeMillis());
        nextTicket.setType(type);
        return ticketRepository.save(nextTicket);
    }

    public Ticket inviteNextBenefitTicketToTable(String type, Integer table) {
        log.info("Getting first waiting ticket");
        var nextTicket = ticketRepository.findFirstByStatusAndTypeOrderByStartOnlineTimestampAsc("ONLINE", type)
                .orElseGet(() -> ticketRepository.findFirstByStatusAndTypeOrderByStartOnlineTimestampAsc("ONLINE", "BENEFIT")
                        .orElseThrow(() -> new ResourceNotFoundException("No waiting tickets found")));


        log.info("Retrieved ticket: {}", nextTicket);
        nextTicket.setStatus("SERVED");
        nextTicket.setTableNumber(table);
        nextTicket.setStartServedTimestamp(System.currentTimeMillis());
        nextTicket.setType(type);
        return ticketRepository.save(nextTicket);
    }

    public Ticket inviteNextTicketToCoworking(String type) {
        log.info("Getting first waiting ticket");
        var nextTicket = ticketRepository.findFirstByStatusAndTypeOrderByStartWaitingTimestampAsc("WAIT", type)
                .orElseGet(() -> ticketRepository.findFirstByStatusAndTypeOrderByStartWaitingTimestampAsc("WAIT", "BASIC")
                        .orElseThrow(() -> new ResourceNotFoundException("No waiting tickets found")));


        log.info("Retrieved ticket: {}", nextTicket);
        nextTicket.setStatus("COWORKING");
        nextTicket.setStartCoworkingTimestamp(System.currentTimeMillis());
        nextTicket.setType(type);
        return ticketRepository.save(nextTicket);
    }

    public Ticket inviteNextTicketToCheck(String type) {
        log.info("Getting first waiting ticket");
        var nextTicket = ticketRepository.findFirstByStatusAndTypeOrderByStartAddedTimestampAsc("ADDED", type)
                .orElseGet(() -> ticketRepository.findFirstByStatusAndTypeOrderByStartAddedTimestampAsc("ADDED", "BASIC")
                        .orElseThrow(() -> new ResourceNotFoundException("No waiting tickets found")));


        log.info("Retrieved ticket: {}", nextTicket);
        nextTicket.setStatus("CHECK");
        nextTicket.setStartCheckTimestamp(System.currentTimeMillis());
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

    public Ticket toReadyTicket(Long id) {
        log.info("Moving ticket with id {} to progress", id);
        var ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id " + id));

        if(!ticket.getStatus().equals("COWORKING")) {
            throw new IllegalStateException("Ticket with id " + id + " is not in COWORKING status");
        }

        ticket.setStatus("READY");
        ticket.setStartReadyTimestamp(System.currentTimeMillis());

        var updatedTicket = ticketRepository.save(ticket);
        log.info("Moved ticket to progress: {}", updatedTicket);
        return updatedTicket;
    }

    public Ticket toWaitTicket(Long id) {
        log.info("Moving ticket with id {} to wait", id);
        var ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id " + id));

        if(!ticket.getStatus().equals("CHECK")) {
            throw new IllegalStateException("Ticket with id " + id + " is not in CHECK status");
        }

        ticket.setStatus("WAIT");
        ticket.setStartWaitingTimestamp(System.currentTimeMillis());

        var updatedTicket = ticketRepository.save(ticket);
        log.info("Moved ticket to wait: {}", updatedTicket);
        return updatedTicket;
    }

    public Ticket toAddedTicket(Long id) {
        log.info("Moving ticket with id {} to added", id);
        var ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id " + id));

        if(!ticket.getStatus().equals("CREATED")) {
            throw new IllegalStateException("Ticket with id " + id + " is not in CREATED status");
        }

        ticket.setStatus("ADDED");
        ticket.setStartAddedTimestamp(System.currentTimeMillis());

        var updatedTicket = ticketRepository.save(ticket);
        log.info("Moved ticket to added: {}", updatedTicket);
        return updatedTicket;
    }
    public Ticket toCancelTicket(Long id) {
        log.info("Moving ticket with id {} to cancel", id);
        var ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id " + id));

        if(!ticket.getStatus().equals("COWORKING")) {
            throw new IllegalStateException("Ticket with id " + id + " is not in CREATED status");
        }

        ticket.setStatus("CANCEL");
        ticket.setStartCancelTimestamp(System.currentTimeMillis());

        var updatedTicket = ticketRepository.save(ticket);
        log.info("Moved ticket to wait: {}", updatedTicket);
        return updatedTicket;
    }

    public Ticket toDeleteFromCheck(Long id) {
        log.info("Moving ticket with id {} to cancel", id);
        var ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id " + id));

        if(!ticket.getStatus().equals("CHECK")) {
            throw new IllegalStateException("Ticket with id " + id + " is not in CHECK status");
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

    public Optional<Ticket> getTicketById(Long id){
        Optional<Ticket> ticket = ticketRepository.findById(id);
        return ticket;

    }
}
