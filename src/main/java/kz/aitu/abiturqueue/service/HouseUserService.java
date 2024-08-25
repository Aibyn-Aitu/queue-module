package kz.aitu.abiturqueue.service;

import kz.aitu.abiturqueue.exception.CustomNotFoundException;
import kz.aitu.abiturqueue.exception.ExceptionDescription;
import kz.aitu.abiturqueue.exception.InvalidVerificationCodeException;
import kz.aitu.abiturqueue.model.dto.HouseUserDtoRequest;
import kz.aitu.abiturqueue.model.dto.UserDtoRequest;
import kz.aitu.abiturqueue.model.entity.HouseUser;
import kz.aitu.abiturqueue.model.entity.TicketHouse;
import kz.aitu.abiturqueue.repository.HouseUserRepository;
import kz.aitu.abiturqueue.repository.TicketHouseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class HouseUserService {

    private final HouseUserRepository userRepository;
    private final TicketHouseRepository ticketRepository;
    private final EmailSenderService emailSenderService;

    public Optional<HouseUser> getById(Long id){
        return this.userRepository.findById(id);
    }

    public HouseUser getByIdThrowException(Long id) {
        return this.getById(id).orElseThrow(() -> new CustomNotFoundException(String.format
                (ExceptionDescription.CustomNotFoundException, "User", "id", id)));
    }

    public Optional<HouseUser> getByIin(String iin){
        return this.userRepository.findUserByIin(iin);
    }

    public Optional<HouseUser> getByTicket(Long ticketId){
        return this.userRepository.findUserByTicketId(ticketId);
    }

    public Long getTicketIdByIin(String iin){
        Optional<HouseUser> user = this.getByIin(iin);
        return user.map(HouseUser::getTicketId).orElse(null);
    }

    public Optional<HouseUser> getHouseUserByIin(String iin){
        Optional<HouseUser> user = this.getByIin(iin);
        return user;
    }

    public Long create(HouseUserDtoRequest userDtoRequest){
        HouseUser user = new HouseUser();
        user.setEmail(userDtoRequest.getEmail());
        user.setFirstname(userDtoRequest.getFirstname());
        user.setLastname(userDtoRequest.getLastname());
        user.setIin(userDtoRequest.getIin());
        user.setCode(emailSenderService.sendEmail(user.getEmail()));

        this.userRepository.save(user);
        return user.getId();
    }

    public Integer verification(Long userId, Integer code){
        Optional<HouseUser> userOptional = this.getById(userId);

        if(userOptional.isPresent()){
            HouseUser user = userOptional.get();
            if(user.getCode().equals(code)){
                user.setIsVerified(true);
                TicketHouse ticket = ticketRepository.findFirstByStatusAndTypeOrderByNumberAsc("CREATED", "BASIC").orElse(null);
                if (ticket != null) {
                    ticket.setStatus("ADDED");
                    ticket.setStartAddedTimestamp(System.currentTimeMillis());
                    user.setTicketId(ticket.getId());
                    userRepository.save(user);
                    return ticket.getNumber();
                }
            } else {
                throw new InvalidVerificationCodeException("Invalid verification code");
            }
        }
        throw new InvalidVerificationCodeException("HouseUser not found");
    }

    public Integer verificationGirls(Long userId, Integer code){
        Optional<HouseUser> userOptional = this.getById(userId);

        if(userOptional.isPresent()){
            HouseUser user = userOptional.get();
            if(user.getCode().equals(code)){
                user.setIsVerified(true);
                TicketHouse ticket = ticketRepository.findFirstByStatusAndTypeOrderByNumberAsc("CREATED", "BENEFIT").orElse(null);
                if (ticket != null) {
                    ticket.setStatus("ADDED");
                    ticket.setStartAddedTimestamp(System.currentTimeMillis());
                    user.setTicketId(ticket.getId());
                    userRepository.save(user);
                    return ticket.getNumber();
                }
            } else {
                throw new InvalidVerificationCodeException("Invalid verification code");
            }
        }
        throw new InvalidVerificationCodeException("HouseUser not found");
    }

    public Integer onlineVerification(Long userId, Integer code){
        Optional<HouseUser> userOptional = this.getById(userId);

        if(userOptional.isPresent()){
            HouseUser user = userOptional.get();
            if(user.getCode().equals(code)){
                user.setIsVerified(true);
                TicketHouse ticket = ticketRepository.findFirstByStatusAndTypeOrderByNumberAsc("CREATED", "BENEFIT").orElse(null);
                if (ticket != null) {
                    ticket.setStatus("ONLINE");
                    ticket.setStartOnlineTimestamp(System.currentTimeMillis());
                    user.setTicketId(ticket.getId());
                    userRepository.save(user);
                    return ticket.getNumber();
                }
            } else {
                throw new InvalidVerificationCodeException("Invalid verification code");
            }
        }
        throw new InvalidVerificationCodeException("HouseUser not found");
    }
}
