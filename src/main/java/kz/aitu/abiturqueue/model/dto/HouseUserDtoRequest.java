package kz.aitu.abiturqueue.model.dto;

import lombok.Data;

@Data
public class HouseUserDtoRequest {
    String email;
    String firstname;
    String lastname;
    String iin;
}
