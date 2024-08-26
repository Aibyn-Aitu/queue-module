package kz.aitu.abiturqueue.service;

import kz.aitu.abiturqueue.exception.ResourceNotFoundException;
import kz.aitu.abiturqueue.model.entity.TicketHouse;
import kz.aitu.abiturqueue.repository.TicketHouseRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;

@Service
@Data
@AllArgsConstructor
public class TicketHouseService {

    private TicketHouseRepository ticketRepository;
    private Environment environment;
    private static final Logger log = LoggerFactory.getLogger(TicketHouseService.class);



    //Status created
    public List<TicketHouse> addNewTicketHouses(Integer count) {
        var now = new Date();
        var ticketList = new ArrayList<TicketHouse>(count);
        for (int i = 0; i < count; i++) {
            var ticket = TicketHouse.builder()
                    .createdTimestamp(now.getTime())
                    .status("CREATED")
                    .build();
            ticketList.add(ticket);
        }
        return ticketRepository.saveAll(ticketList);
    }

    public List<TicketHouse> getTodayTicketHousesExcludingStatus(String status) {
        LocalDate date = LocalDate.now();
        ZoneId zoneId = ZoneId.systemDefault();
        Long startOfDay = date.atStartOfDay(zoneId).toEpochSecond() * 1000 - 68400000;
        System.out.println(startOfDay);

        return ticketRepository.findByStatusNotAndCreatedTodayOrderByNumberAsc(status, startOfDay);
    }


    //Status WAIT auto
    public List<TicketHouse> addNewTicketHousesV2() {
        var now = new Date();
        var ticketList = new ArrayList<TicketHouse>(700);
        for (int i = 0; i < 500; i++) {
            var ticket = TicketHouse.builder()
                    .createdTimestamp(now.getTime() + i)
                    .status("CREATED")
                    //.startWaitingTimestamp(now.getTime() + i)
                    .number(i + 1)
                    .type("BASIC")
                    .build();
            ticketList.add(ticket);
        }
        for (int i = 700; i < 900; i++) {
            var ticket = TicketHouse.builder()
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

    public void clearAllTicketHouses() {
        LocalDate date = LocalDate.now();
        ZoneId zoneId = ZoneId.systemDefault(); // or: ZoneId.of("Asia/Almaty");
        var startOfToday = date.atStartOfDay(zoneId).toEpochSecond() * 1000;
        log.info("TicketHouses cleaning from {}", startOfToday);

        var ticketIdList = ticketRepository.findAll()
                .stream()
                .filter(ticket -> !"PROGRESS".equals(ticket.getStatus()))
                .filter(ticket -> !"CANCEL".equals(ticket.getStatus()))
                .filter(ticket -> ticket.getCreatedTimestamp() <= startOfToday)
                .map(TicketHouse::getId)
                .toList();

        ticketRepository.deleteAllById(ticketIdList);
    }

    @Scheduled(cron = "0 01 0 * * ?")
    public void cleanAndCreateTicketHouses() {
        clearAllTicketHouses();
        addNewTicketHousesV2();
        /*String[] activeProfiles = environment.getActiveProfiles();
        for (String profile : activeProfiles) {
            //if (profile.equalsIgnoreCase("prod")) {
                log.info("Cleaning tickets started...");
                clearAllTicketHouses();
                addNewTicketHousesV2();
            //}
        }*/
    }

    public TicketHouse inviteNextTicketHouseToTable(String type, Integer table) {
        log.info("Getting first waiting ticket");
        var nextTicketHouse = ticketRepository.findFirstByStatusAndTypeOrderByStartAddedTimestampAsc("ADDED", type)
                .orElseGet(() -> ticketRepository.findFirstByStatusAndTypeOrderByStartAddedTimestampAsc("ADDED", "BASIC")
                        .orElseThrow(() -> new ResourceNotFoundException("No waiting tickets found")));


        log.info("Retrieved ticket: {}", nextTicketHouse);
        nextTicketHouse.setStatus("SERVED");
        nextTicketHouse.setTableNumber(table);
        nextTicketHouse.setStartServedTimestamp(System.currentTimeMillis());
        nextTicketHouse.setType(type);
        return ticketRepository.save(nextTicketHouse);
    }

    public TicketHouse inviteNextBenefitTicketHouseToTable(String type, Integer table) {
        log.info("Getting first waiting ticket");
        var nextTicketHouse = ticketRepository.findFirstByStatusAndTypeOrderByStartOnlineTimestampAsc("ONLINE", type)
                .orElseGet(() -> ticketRepository.findFirstByStatusAndTypeOrderByStartOnlineTimestampAsc("ONLINE", "BENEFIT")
                        .orElseThrow(() -> new ResourceNotFoundException("No waiting tickets found")));


        log.info("Retrieved ticket: {}", nextTicketHouse);
        nextTicketHouse.setStatus("SERVED");
        nextTicketHouse.setTableNumber(table);
        nextTicketHouse.setStartServedTimestamp(System.currentTimeMillis());
        nextTicketHouse.setType(type);
        return ticketRepository.save(nextTicketHouse);
    }

    public TicketHouse inviteNextTicketHouseToCoworking(String type) {
        log.info("Getting first waiting ticket");
        var nextTicketHouse = ticketRepository.findFirstByStatusAndTypeOrderByStartWaitingTimestampAsc("WAIT", type)
                .orElseGet(() -> ticketRepository.findFirstByStatusAndTypeOrderByStartWaitingTimestampAsc("WAIT", "BASIC")
                        .orElseThrow(() -> new ResourceNotFoundException("No waiting tickets found")));


        log.info("Retrieved ticket: {}", nextTicketHouse);
        nextTicketHouse.setStatus("COWORKING");
        nextTicketHouse.setStartCoworkingTimestamp(System.currentTimeMillis());
        nextTicketHouse.setType(type);
        return ticketRepository.save(nextTicketHouse);
    }

    public TicketHouse inviteNextTicketHouseToCheck(String type) {
        log.info("Getting first waiting ticket");
        var nextTicketHouse = ticketRepository.findFirstByStatusAndTypeOrderByStartAddedTimestampAsc("ADDED", type)
                .orElseGet(() -> ticketRepository.findFirstByStatusAndTypeOrderByStartAddedTimestampAsc("ADDED", "BASIC")
                        .orElseThrow(() -> new ResourceNotFoundException("No waiting tickets found")));


        log.info("Retrieved ticket: {}", nextTicketHouse);
        nextTicketHouse.setStatus("CHECK");
        nextTicketHouse.setStartCheckTimestamp(System.currentTimeMillis());
        nextTicketHouse.setType(type);
        return ticketRepository.save(nextTicketHouse);
    }

    public TicketHouse toProgressTicketHouse(Long id) {
        log.info("Moving ticket with id {} to progress", id);
        var ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TicketHouse not found with id " + id));

        if(!ticket.getStatus().equals("SERVED")) {
            throw new IllegalStateException("TicketHouse with id " + id + " is not in WAIT status");
        }

        ticket.setStatus("PROGRESS");
        ticket.setStartInProgressTimestamp(System.currentTimeMillis());

        var updatedTicketHouse = ticketRepository.save(ticket);
        log.info("Moved ticket to progress: {}", updatedTicketHouse);
        return updatedTicketHouse;
    }

    public TicketHouse toReadyTicketHouse(Long id) {
        log.info("Moving ticket with id {} to progress", id);
        var ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TicketHouse not found with id " + id));

        if(!ticket.getStatus().equals("COWORKING")) {
            throw new IllegalStateException("TicketHouse with id " + id + " is not in COWORKING status");
        }

        ticket.setStatus("READY");
        ticket.setStartReadyTimestamp(System.currentTimeMillis());

        var updatedTicketHouse = ticketRepository.save(ticket);
        log.info("Moved ticket to progress: {}", updatedTicketHouse);
        return updatedTicketHouse;
    }

    public TicketHouse toWaitTicketHouse(Long id) {
        log.info("Moving ticket with id {} to wait", id);
        var ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TicketHouse not found with id " + id));

        if(!ticket.getStatus().equals("CHECK")) {
            throw new IllegalStateException("TicketHouse with id " + id + " is not in CHECK status");
        }

        ticket.setStatus("WAIT");
        ticket.setStartWaitingTimestamp(System.currentTimeMillis());

        var updatedTicketHouse = ticketRepository.save(ticket);
        log.info("Moved ticket to wait: {}", updatedTicketHouse);
        return updatedTicketHouse;
    }

    public TicketHouse toAddedTicketHouse(Long id) {
        log.info("Moving ticket with id {} to added", id);
        var ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TicketHouse not found with id " + id));

        if(!ticket.getStatus().equals("CREATED")) {
            throw new IllegalStateException("TicketHouse with id " + id + " is not in CREATED status");
        }

        ticket.setStatus("ADDED");
        ticket.setStartAddedTimestamp(System.currentTimeMillis());

        var updatedTicketHouse = ticketRepository.save(ticket);
        log.info("Moved ticket to added: {}", updatedTicketHouse);
        return updatedTicketHouse;
    }
    public TicketHouse toCancelTicketHouse(Long id) {
        log.info("Moving ticket with id {} to cancel", id);
        var ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TicketHouse not found with id " + id));

        if(!ticket.getStatus().equals("COWORKING")) {
            throw new IllegalStateException("TicketHouse with id " + id + " is not in CREATED status");
        }

        ticket.setStatus("CANCEL");
        ticket.setStartCancelTimestamp(System.currentTimeMillis());

        var updatedTicketHouse = ticketRepository.save(ticket);
        log.info("Moved ticket to wait: {}", updatedTicketHouse);
        return updatedTicketHouse;
    }

    public TicketHouse toCancelAddedTicket(Long id) {
        log.info("Moving ticket with id {} to cancel", id);
        var ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TicketHouse not found with id " + id));

        if(!ticket.getStatus().equals("ADDED")) {
            throw new IllegalStateException("TicketHouse with id " + id + " is not in CREATED status");
        }

        ticket.setStatus("CANCEL");
        ticket.setStartCancelTimestamp(System.currentTimeMillis());

        var updatedTicketHouse = ticketRepository.save(ticket);
        log.info("Moved ticket to wait: {}", updatedTicketHouse);
        return updatedTicketHouse;
    }

    public TicketHouse toCancelTicketHouseFromAdmin(Long id) {
        log.info("Moving ticket with id {} to cancel", id);
        var ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TicketHouse not found with id " + id));

        ticket.setStatus("CANCEL");
        ticket.setStartCancelTimestamp(System.currentTimeMillis());

        var updatedTicketHouse = ticketRepository.save(ticket);
        log.info("Moved ticket to wait: {}", updatedTicketHouse);
        return updatedTicketHouse;
    }

    public TicketHouse toDeleteFromCheck(Long id) {
        log.info("Moving ticket with id {} to cancel", id);
        var ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TicketHouse not found with id " + id));

        if(!ticket.getStatus().equals("CHECK")) {
            throw new IllegalStateException("TicketHouse with id " + id + " is not in CHECK status");
        }

        ticket.setStatus("CANCEL");
        ticket.setStartCancelTimestamp(System.currentTimeMillis());

        var updatedTicketHouse = ticketRepository.save(ticket);
        log.info("Moved ticket to wait: {}", updatedTicketHouse);
        return updatedTicketHouse;
    }
    public void toDeleteTicketHouse(Long id) {
        log.info("Moving ticket with id {} to delete", id);
        var ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TicketHouse not found with id " + id));

        ticketRepository.delete(ticket);
    }

    public double calculateClientWaitTime() {
        LocalDate date = LocalDate.now();
        ZoneId zoneId = ZoneId.systemDefault(); // or: ZoneId.of("Asia/Almaty");
        var startOfToday = date.atStartOfDay(zoneId).toEpochSecond() * 1000;
        log.info("TicketHouses cleaning from {}", startOfToday);

        double average = ticketRepository.findByStatusAndStartInProgressTimestampAfter("PROGRESS", startOfToday)
                .stream()
                .mapToLong(ticket -> (ticket.getStartInProgressTimestamp() - ticket.getStartWaitingTimestamp()) / 1000)
                .average()
                .orElse(0.0);
        return average;
    }

    public Optional<TicketHouse> getTicketHouseById(Long id){
        Optional<TicketHouse> ticket = ticketRepository.findById(id);
        return ticket;

    }
}
