package kz.aitu.abiturqueue.controller;

import kz.aitu.abiturqueue.exception.InvalidVerificationCodeException;
import kz.aitu.abiturqueue.model.dto.HouseUserDtoRequest;
import kz.aitu.abiturqueue.model.dto.VerificationDtoRequest;
import kz.aitu.abiturqueue.model.entity.HouseUser;
import kz.aitu.abiturqueue.service.HouseUserService;
import kz.aitu.abiturqueue.service.TicketHouseService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Data
@AllArgsConstructor
@RestController
@RequestMapping("/api/house-user")
public class HouseUserController {

    private final HouseUserService userService;
    private final TicketHouseService ticketService;

    @PostMapping("/create")
    public ResponseEntity<Long> createUser(@RequestBody HouseUserDtoRequest userDtoRequest){
        return ResponseEntity.ok(userService.create(userDtoRequest));
    }

    @PostMapping("/verification")
    public ResponseEntity<Integer> verification(@RequestBody VerificationDtoRequest verificationDtoRequest){
        try {
            return ResponseEntity.ok(userService.verification(verificationDtoRequest.getUserId(), verificationDtoRequest.getCode()));
        } catch (InvalidVerificationCodeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/verification-girls")
    public ResponseEntity<Integer> verificationGirls(@RequestBody VerificationDtoRequest verificationDtoRequest){
        try {
            return ResponseEntity.ok(userService.verificationGirls(verificationDtoRequest.getUserId(), verificationDtoRequest.getCode()));
        } catch (InvalidVerificationCodeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/online-verification")
    public ResponseEntity<Integer> onlineVerification(@RequestBody VerificationDtoRequest verificationDtoRequest){
        try {
            return ResponseEntity.ok(userService.onlineVerification(verificationDtoRequest.getUserId(), verificationDtoRequest.getCode()));
        } catch (InvalidVerificationCodeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/find/iin")
    public ResponseEntity<HouseUser> findUserByIin(@RequestParam String iin){
        Optional<HouseUser> user = userService.getHouseUserByIin(iin);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @GetMapping("/find/id")
    public ResponseEntity<HouseUser> findUserById(@RequestParam Long id){
        Optional<HouseUser> user = userService.getById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }
}
