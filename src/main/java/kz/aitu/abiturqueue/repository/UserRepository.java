package kz.aitu.abiturqueue.repository;

import kz.aitu.abiturqueue.model.entity.Ticket;
import kz.aitu.abiturqueue.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByIin(String iin);
    Optional<User> findUserByTicketId(Long ticketId);
}
