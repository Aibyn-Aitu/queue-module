package kz.aitu.abiturqueue.repository;

import kz.aitu.abiturqueue.model.entity.TicketHouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable; // ensure correct import here

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketHouseRepository extends JpaRepository<TicketHouse, Long> {
    // Custom queries if needed
    List<TicketHouse> findByStatusOrderByStartWaitingTimestampAsc(String status, Pageable pageable);
    List<TicketHouse> findByStatusAndTypeOrderByStartWaitingTimestampAsc(String status, String type, Pageable pageable);
    List<TicketHouse> findByStatusAndIdIsAfterOrderByIdAsc(String status, Long minId, Pageable pageable);
    List<TicketHouse> findByStatusOrderByStartWaitingTimestampAsc(String status);
    List<TicketHouse> findByStatusOrderByStartCoworkingTimestampAsc(String status);
    List<TicketHouse> findByStatusOrderByStartReadyTimestampAsc(String status);
    List<TicketHouse> findByStatusNotOrderByStartAddedTimestampAsc(String status);

    @Query("SELECT t FROM TicketHouse t WHERE t.status NOT LIKE :status AND t.createdTimestamp > :startOfDay ORDER BY t.number ASC")
    List<TicketHouse> findByStatusNotAndCreatedTodayOrderByNumberAsc(@Param("status") String status, @Param("startOfDay") Long startOfDay);

    List<TicketHouse> findByStatusOrderByStartAddedTimestampAsc(String status);
    List<TicketHouse> findByStatusOrderByStartCheckTimestampAsc(String status);
    List<TicketHouse> findByStatusAndTableNumberOrderByStartWaitingTimestampAsc(String status, Integer tableNum);
    List<TicketHouse> findByStatusAndTableNumberOrderByStartReadyTimestampAsc(String status, Integer tableNum);

    List<TicketHouse> findByStatusAndTableNumberAndTypeOrderByStartWaitingTimestampAsc(String status, Integer tableNum, String type);
    List<TicketHouse> findByStatusAndStartInProgressTimestampAfter(String status, Long timestamp);
    List<TicketHouse> findByStatusAndTypeOrderByStartWaitingTimestampAsc(String status, String type);

    Optional<TicketHouse> findFirstByStatusAndTypeOrderByStartWaitingTimestampAsc(String status, String type);
    Optional<TicketHouse> findFirstByStatusAndTypeOrderByStartReadyTimestampAsc(String status, String type);
    Optional<TicketHouse> findFirstByStatusAndTypeOrderByStartAddedTimestampAsc(String status, String type);

    Optional<TicketHouse> findFirstByStatusAndTypeOrderByStartOnlineTimestampAsc(String status, String type);
    Optional<TicketHouse> findFirstByStatusAndTypeOrderByStartCoworkingTimestampAsc(String status, String type);

    Optional<TicketHouse> findFirstByStatusAndTypeOrderByNumberAsc(String status, String type);

    Long countAllByStatus(String status);
    Long countAllByStatusAndStartInProgressTimestampAfter(String status, Long timestamp);

}
