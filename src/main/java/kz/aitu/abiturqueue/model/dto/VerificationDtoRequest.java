package kz.aitu.abiturqueue.model.dto;

import lombok.Data;

@Data
public class VerificationDtoRequest {
    private Long userId;
    private Integer code;
}
