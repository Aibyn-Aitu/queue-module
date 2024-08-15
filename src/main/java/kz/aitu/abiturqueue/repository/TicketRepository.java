package kz.aitu.abiturqueue.repository;

import kz.aitu.abiturqueue.model.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable; // ensure correct import here

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    // Custom queries if needed
    List<Ticket> findByStatusOrderByStartWaitingTimestampAsc(String status, Pageable pageable);
    List<Ticket> findByStatusAndTypeOrderByStartWaitingTimestampAsc(String status, String type, Pageable pageable);
    List<Ticket> findByStatusAndIdIsAfterOrderByIdAsc(String status, Long minId, Pageable pageable);
    List<Ticket> findByStatusOrderByStartWaitingTimestampAsc(String status);
    List<Ticket> findByStatusAndTableNumberOrderByStartWaitingTimestampAsc(String status, Integer tableNum);

    List<Ticket> findByStatusAndTableNumberAndTypeOrderByStartWaitingTimestampAsc(String status, Integer tableNum, String type);
    List<Ticket> findByStatusAndStartInProgressTimestampAfter(String status, Long timestamp);
    List<Ticket> findByStatusAndTypeOrderByStartWaitingTimestampAsc(String status, String type);

    Optional<Ticket> findFirstByStatusAndTypeOrderByStartWaitingTimestampAsc(String status, String type);

    Optional<Ticket> findFirstByStatusAndTypeOrderByNumberAsc(String status, String type);

    Long countAllByStatus(String status);
    Long countAllByStatusAndStartInProgressTimestampAfter(String status, Long timestamp);

}
