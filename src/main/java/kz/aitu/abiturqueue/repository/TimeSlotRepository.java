package kz.aitu.abiturqueue.repository;

import kz.aitu.abiturqueue.model.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {

    List<TimeSlot> findByTicketIdIsNull();

    Optional<TimeSlot> findByTicketId(Long id);
}
