package kz.aitu.abiturqueue.model.dto;

import kz.aitu.abiturqueue.model.entity.Ticket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatedTicketDTO {
    private Ticket ticketBasic;
    private Ticket ticketBenefit;
}
