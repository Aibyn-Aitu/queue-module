package kz.aitu.abiturqueue.model.dto;

import kz.aitu.abiturqueue.model.entity.Ticket;
import kz.aitu.abiturqueue.model.entity.TicketHouse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatedTicketHouseDto {
    private TicketHouse ticketBasic;
    private TicketHouse ticketBenefit;
}
