package kz.aitu.abiturqueue.repository;

import kz.aitu.abiturqueue.model.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    List<Ticket> findByStatusOrderByStartCoworkingTimestampAsc(String status);
    List<Ticket> findByStatusOrderByStartReadyTimestampAsc(String status);
    List<Ticket> findByStatusNotOrderByStartAddedTimestampAsc(String status);

    @Query("SELECT t FROM Ticket t WHERE t.status NOT LIKE :status AND t.createdTimestamp > :startOfDay ORDER BY t.number ASC")
    List<Ticket> findByStatusNotAndCreatedTodayOrderByNumberAsc(@Param("status") String status, @Param("startOfDay") Long startOfDay);

    List<Ticket> findByStatusOrderByStartAddedTimestampAsc(String status);
    List<Ticket> findByStatusOrderByStartCheckTimestampAsc(String status);
    List<Ticket> findByStatusAndVolunteerNumberOrderByStartCheckTimestampAsc(String status, Integer volunteerNumber);
    List<Ticket> findByStatusAndTableNumberOrderByStartWaitingTimestampAsc(String status, Integer tableNum);
    List<Ticket> findByStatusAndTableNumberOrderByStartReadyTimestampAsc(String status, Integer tableNum);

    List<Ticket> findByStatusAndTableNumberAndTypeOrderByStartWaitingTimestampAsc(String status, Integer tableNum, String type);
    List<Ticket> findByStatusAndStartInProgressTimestampAfter(String status, Long timestamp);
    List<Ticket> findByStatusAndTypeOrderByStartWaitingTimestampAsc(String status, String type);

    Optional<Ticket> findFirstByStatusAndTypeOrderByStartWaitingTimestampAsc(String status, String type);
    Optional<Ticket> findFirstByStatusAndTypeOrderByStartReadyTimestampAsc(String status, String type);
    Optional<Ticket> findFirstByStatusAndTypeOrderByStartAddedTimestampAsc(String status, String type);

    Optional<Ticket> findFirstByStatusAndTypeOrderByStartOnlineTimestampAsc(String status, String type);
    Optional<Ticket> findFirstByStatusAndTypeOrderByStartCoworkingTimestampAsc(String status, String type);

    Optional<Ticket> findFirstByStatusAndTypeOrderByNumberAsc(String status, String type);

    Long countAllByStatus(String status);
    Long countAllByStatusAndStartInProgressTimestampAfter(String status, Long timestamp);

}
