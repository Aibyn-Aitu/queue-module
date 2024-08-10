package kz.aitu.abiturqueue.service;

import kz.aitu.abiturqueue.exception.CustomNotFoundException;
import kz.aitu.abiturqueue.exception.ExceptionDescription;
import kz.aitu.abiturqueue.model.entity.TimeSlot;
import kz.aitu.abiturqueue.repository.TicketRepository;
import kz.aitu.abiturqueue.repository.TimeSlotRepository;
import kz.aitu.abiturqueue.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.List;
import java.util.Optional;

@Service
public class TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;
    public final UserRepository userRepository;
    public final TicketRepository ticketRepository;

    public TimeSlotService(TimeSlotRepository timeSlotRepository, UserRepository userRepository, TicketRepository ticketRepository) {
        this.timeSlotRepository = timeSlotRepository;
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
    }

    private Optional<TimeSlot> getById(Long id){
        return this.timeSlotRepository.findById(id);
    }

    public TimeSlot getByIdThrowException(Long id) {
        return this.getById(id).orElseThrow(() -> new CustomNotFoundException(String.format
                (ExceptionDescription.CustomNotFoundException, "TimeSlot", "id", id)));
    }

    public List<TimeSlot> getAll(){
        return this.timeSlotRepository.findAll();
    }

//    public Integer addUser(Long userId, Long timeSlotId){
//        TimeSlot timeSlot = this.getByIdThrowException(timeSlotId);
//
//        timeSlot.setUserId();
//    }
}
