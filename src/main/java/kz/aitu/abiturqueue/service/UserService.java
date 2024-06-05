package kz.aitu.abiturqueue.service;

import kz.aitu.abiturqueue.model.dto.UserDtoRequest;
import kz.aitu.abiturqueue.model.entity.Ticket;
import kz.aitu.abiturqueue.model.entity.User;
import kz.aitu.abiturqueue.repository.TicketRepository;
import kz.aitu.abiturqueue.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final EmailSenderService emailSenderService;

    public Optional<User> getById(Long id){
        return this.userRepository.findById(id);
    }

    public Optional<User> getByIin(String iin){
        return this.userRepository.findUserByIin(iin);
    }

    public Optional<User> getByTicket(Long ticketId){
        return this.userRepository.findUserByTicketId(ticketId);
    }

    public Long getTicketIdByIin(String iin){
        Optional<User> user = this.getByIin(iin);
        return user.map(User::getTicketId).orElse(null);
    }

    public Optional<User> getUserByIin(String iin){
        Optional<User> user = this.getByIin(iin);
        return user;
    }

    public Long create(UserDtoRequest userDtoRequest){
        User user = new User();
        user.setEmail(userDtoRequest.getEmail());
        user.setFirstname(userDtoRequest.getFirstname());
        user.setLastname(userDtoRequest.getLastname());
        user.setIin(userDtoRequest.getIin());
        user.setCode(emailSenderService.sendEmail(user.getEmail()));

        this.userRepository.save(user);
        return user.getId();
    }

    public Integer verification(Long userId, Integer code){
        Optional<User> userOptional = this.getById(userId);

        if(userOptional.isPresent()){
            User user = userOptional.get();
            if(user.getCode().equals(code)){
                user.setIsVerified(true);
                Ticket ticket = ticketRepository.findFirstByStatusAndTypeOrderByNumberAsc("CREATED", "BASIC").orElse(null);
                ticket.setStatus("WAIT");
                ticket.setStartWaitingTimestamp(System.currentTimeMillis());
                if (ticket != null) {
                    user.setTicketId(ticket.getId());
                    userRepository.save(user);
                    return ticket.getNumber();
                }
            }
        }
        return null;
    }

//    public Long create(UserDtoRequest userDtoRequest){
//        User user = new User();
//        Ticket ticket = ticketRepository.findFirstByStatusAndTypeOrderByNumberAsc("CREATED", "BASIC").orElse(null);
//
//        if(getByIin(userDtoRequest.getIin()).isEmpty()){
//            user.setIin(userDtoRequest.getIin());
//            user.setTicketId(ticket.getId());
//        }else {
//            return 0L;
//        }
//
//        this.userRepository.save(user);
//        return user.getTicketId();
//    }
}
