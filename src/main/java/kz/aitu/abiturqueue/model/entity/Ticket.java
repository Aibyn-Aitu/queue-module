package kz.aitu.abiturqueue.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private Integer number;
    // Status:
    // CREATED Билет создан
    // 'WAIT' Билет получил абитуриент
    // 'served' Билет в ожидании
    // 'in progress' билет у менеджера
    private String status;
    private Long createdTimestamp;
    private Long startWaitingTimestamp;
    private Long startServedTimestamp;
    private Long startInProgressTimestamp;
    private Long startCancelTimestamp;
    private String type;
}
