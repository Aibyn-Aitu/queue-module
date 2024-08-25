package kz.aitu.abiturqueue.service;

import jakarta.transaction.Transactional;
import kz.aitu.abiturqueue.exception.CustomNotFoundException;
import kz.aitu.abiturqueue.exception.ExceptionDescription;
import kz.aitu.abiturqueue.model.entity.Ticket;
import kz.aitu.abiturqueue.model.entity.TimeSlot;
import kz.aitu.abiturqueue.model.entity.User;
import kz.aitu.abiturqueue.repository.TicketRepository;
import kz.aitu.abiturqueue.repository.TimeSlotRepository;
import kz.aitu.abiturqueue.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;
    public final UserRepository userRepository;
    public final UserService userService;
    public final TicketRepository ticketRepository;

    public TimeSlotService(TimeSlotRepository timeSlotRepository, UserRepository userRepository, UserService userService, TicketRepository ticketRepository) {
        this.timeSlotRepository = timeSlotRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.ticketRepository = ticketRepository;
    }

    private Optional<TimeSlot> getById(Long id) {
        return this.timeSlotRepository.findById(id);
    }

    private Optional<TimeSlot> getByTicketId(Long id) {
        return this.timeSlotRepository.findByTicketId(id);
    }

    public TimeSlot getByTicketIdThrowException(Long id) {
        return this.getByTicketId(id).orElseThrow(() -> new CustomNotFoundException(String.format
                (ExceptionDescription.CustomNotFoundException, "TimeSlot", "id", id)));
    }


    public TimeSlot getByIdThrowException(Long id) {
        return this.getById(id).orElseThrow(() -> new CustomNotFoundException(String.format
                (ExceptionDescription.CustomNotFoundException, "TimeSlot", "id", id)));
    }

    public List<TimeSlot> getAll() {
        return this.timeSlotRepository.findAll();
    }

    public List<TimeSlot> getAllOnline(Long tableNumber) {
        return this.timeSlotRepository.findAllByStatusAndTableNumberAndTicketIdIsNotNullOrderByTimeAsc("TABLE", tableNumber);
    }

    public List<TimeSlot> getAllTable(Long tableNumber) {
        return this.timeSlotRepository.findAllByStatusAndTableNumberAndTicketIdIsNotNullOrderByTimeAsc("TABLE", tableNumber);
    }

    @Transactional
    public User getFirstOnlineByStatusOrderByTime(Long table) {
        // Используем Optional для безопасного получения TimeSlot
        Optional<TimeSlot> optionalTimeSlot = timeSlotRepository.findFirstByStatusAndTicketIdIsNotNullOrderByTimeAsc("ONLINE");

        if (!optionalTimeSlot.isPresent()) {
            throw new NoSuchElementException("No available time slot found with status: ONLINE");
        }

        TimeSlot timeSlot = optionalTimeSlot.get();

        // Изменяем статус и номер стола
        timeSlot.setStatus("TABLE");
        timeSlot.setTableNumber(table);

        // Сохраняем изменения в TimeSlot
        timeSlotRepository.save(timeSlot);

        // Используем метод orElseThrow для безопасного получения User
        User user = userRepository.findUserByTicketId(timeSlot.getTicketId())
                .orElseThrow(() -> new NoSuchElementException("No user found with ticketId: " + timeSlot.getTicketId()));

        return user;
    }

    public boolean isUserSelectTheTimeSlot(Long userId) {
        User user = this.userService.getByIdThrowException(userId);
        Ticket ticket = this.ticketRepository.findById(user.getTicketId())
                .orElseThrow(() -> new CustomNotFoundException(String.format(
                        ExceptionDescription.CustomNotFoundException, "Ticket", "id", user.getTicketId())));

        Optional<TimeSlot> timeSlot = this.getByTicketId(ticket.getId());
        return timeSlot.isPresent();
    }


//    public Integer addUser(Long userId, Long timeSlotId){
//        TimeSlot timeSlot = this.getByIdThrowException(timeSlotId);
//
//        timeSlot.setUserId();
//    }
}
