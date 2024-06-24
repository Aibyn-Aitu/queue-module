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

    @Column(name = "number")
    private Integer number;

    @Column(name = "status")
    private String status;

    @Column(name = "createdTimestamp")
    private Long createdTimestamp;

    @Column(name = "startWaitingTimestamp")
    private Long startWaitingTimestamp;

    @Column(name = "startServedTimestamp")
    private Long startServedTimestamp;

    @Column(name = "startInPgrogressTimestamp")
    private Long startInProgressTimestamp;

    @Column(name = "startCancelTimestamp")
    private Long startCancelTimestamp;

    @Column(name = "type")
    private String type;

    @Column(name = "tableNumber")
    private Integer tableNumber;
}
