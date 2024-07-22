package com.example.eventappbackend.Controllers;

import com.example.eventappbackend.services.UserService;
import com.example.eventappbackend.user.User;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@Validated
@RequiredArgsConstructor
@RequestMapping("User")
@RestController
@CrossOrigin(origins = "*")
public class UserController {
    private  final UserService userService;
    @PostMapping("/Register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> register(
            @RequestBody @Valid RegistrationRequest request
    ) throws MessagingException {
        userService.register(request);
        return ResponseEntity.accepted().build();
    }
}

