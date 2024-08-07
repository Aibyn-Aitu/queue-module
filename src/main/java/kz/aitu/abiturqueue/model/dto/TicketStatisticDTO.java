package kz.aitu.abiturqueue.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketStatisticDTO {
    private Long createdCount;
    private Long waitCount;
    private Long servedCount;
    private Long progressCount;
    private Long coworkingCount;
    private Long addedCount;
    private Long checkCount;

    private List<String> dates;
    private List<Integer> ticketsInProcess;
    private List<Double> avgProcessTime;

    public TicketStatisticDTO(List<String> dates, List<Integer> ticketsInProcess, List<Double> avgProcessTime) {
        this.dates = dates;
        this.ticketsInProcess = ticketsInProcess;
        this.avgProcessTime = avgProcessTime;
    }
}
