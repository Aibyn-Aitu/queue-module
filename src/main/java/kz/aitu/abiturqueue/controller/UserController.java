package kz.aitu.abiturqueue.controller;

import kz.aitu.abiturqueue.model.dto.UserDtoRequest;
import kz.aitu.abiturqueue.model.dto.VerificationDtoRequest;
import kz.aitu.abiturqueue.model.entity.User;
import kz.aitu.abiturqueue.service.TicketService;
import kz.aitu.abiturqueue.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Data
@AllArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final TicketService ticketService;

    @PostMapping("/create")
    public ResponseEntity<Long> createUser(@RequestBody UserDtoRequest userDtoRequest){
        return ResponseEntity.ok(userService.create(userDtoRequest));
    }

    @PostMapping("/verification")
    public ResponseEntity<Integer> verification(@RequestBody VerificationDtoRequest verificationDtoRequest){
        return ResponseEntity.ok(userService.verification(verificationDtoRequest.getUserId(), verificationDtoRequest.getCode()));
    }

    @GetMapping("/find/iin")
    public ResponseEntity<User> findUserByIin(@RequestParam String iin){
        Optional<User> user = userService.getUserByIin(iin);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @GetMapping("/find/id")
    public ResponseEntity<User> findUserById(@RequestParam Long id){
        Optional<User> user = userService.getById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }
}
