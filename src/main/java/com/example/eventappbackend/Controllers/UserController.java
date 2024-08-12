package com.example.eventappbackend.Controllers;

import com.example.eventappbackend.DTO.UserUpdateDTO;
import com.example.eventappbackend.services.UserService;
import com.example.eventappbackend.user.AuthenticateResponse;
import com.example.eventappbackend.user.AuthenticationRequest;
import com.example.eventappbackend.user.User;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@Validated
@RequiredArgsConstructor
@RequestMapping("/User")
@RestController
@CrossOrigin(origins = " * ")
public class UserController {
    @Autowired
    private UserService userService;
    @PostMapping("/Register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> register(@RequestBody @Valid RegistrationRequest request) {
        try {
            userService.register(request);
            return ResponseEntity.accepted().build();
        } catch (Exception e) {
            // Log the error
            System.err.println("Error during registration: " + e.getMessage());
            // Return a meaningful error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticateResponse> authenticate(
            @RequestBody @Valid AuthenticationRequest request
    ) {
        return  ResponseEntity.ok(userService.authenticate(request));
    }
   @GetMapping("/activate-account")
    public void confirm(
            @RequestParam String token
   ) throws MessagingException {
        userService.activateAccount(token);
   }

    @GetMapping("/all-users")
    public ResponseEntity<List<User>> getAllUsers() {
        System.out.println("Received request to get all users");
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }


    @GetMapping("get-user/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/update-user/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserUpdateDTO userUpdateDTO) {
        userService.updateUser(id, userUpdateDTO);
        return ResponseEntity.ok("User updated successfully");
    }

    @DeleteMapping("/delete-user/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }
}

