package com.example.eventappbackend.services;

import com.example.eventappbackend.Config.JwtService;
import com.example.eventappbackend.Controllers.RegistrationRequest;
import com.example.eventappbackend.DTO.UserUpdateDTO;
import com.example.eventappbackend.Repositories.RoleRepository;
import com.example.eventappbackend.Repositories.TokenRepository;
import com.example.eventappbackend.Repositories.UserRepository;
import com.example.eventappbackend.user.*;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Data
public class UserService {
    private  final  UserRepository userRepository;
    private final RoleRepository roleRepository;
    private  final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final  EmailService emailService;
    private  final AuthenticationManager authenticationManager;
    private  final JwtService jwtService;
    @Value("${application.mailing.frontend.activationUrl}")
    private String activateUrl;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, TokenRepository tokenRepository, EmailService emailService, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public void register(RegistrationRequest request) throws MessagingException {
        try {
            System.out.println("Received registration request: " + request);
            if (request.getRole() == null || request.getRole().isEmpty()) {
                throw new IllegalArgumentException("Role is missing");
            }

            var userRole = roleRepository.findByName(request.getRole())
                    .orElseThrow(() -> new IllegalStateException("ROLE " + request.getRole() + " was not initialized"));

            var user = User.builder()
                    .firstName(request.getFirstname())
                    .lastName(request.getLastname())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .accountlocked(false)
                    .enabled(false)
                    .build();

            userRepository.save(user);

            user.setRoles(List.of(userRole));
            userRepository.save(user);

            sendValidationEmail(user);
        } catch (Exception e) {
            // Log the error
            System.err.println("Error during registration: " + e.getMessage());
            // Rethrow the exception or handle it accordingly
            throw e;
        }
    }



    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);
        //send email
        emailService.sendEmail(
                user.getEmail(),
                user.fullname(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activateUrl,
                newToken,
                "Account activation"
        );

    }

    private String generateAndSaveActivationToken(User user) {
        //generate a token
        String generatedToken = generateActivationCode(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdat(LocalDateTime.now())
                .expiredat(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }

    //we will generate a token based on a length
    private String generateActivationCode(int length) {
        String characters ="0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length()); // 0..9
            codeBuilder.append(characters.charAt(randomIndex));
        }

        return codeBuilder.toString();
      }

    public AuthenticateResponse authenticate(AuthenticationRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var claims = new HashMap<String,Object>();
        var user =  ((User)  auth.getPrincipal());
        claims.put("username",user.fullname());
        var jwtToken = jwtService.generateToken( claims,user);

        // Get user roles
        String role = user.getRoles().stream()
                .map(Role::getName)
                .findFirst()
                .orElse("USER");

        return AuthenticateResponse.builder()
                .token(jwtToken)
                .build();
    }

    //@Transactional
    public void activateAccount(String token) throws MessagingException {
        Token savedToken =  tokenRepository.findByToken(token)
                //exception has to be defined here
                .orElseThrow(() ->new RuntimeException("Invalid Token"));
        if ( LocalDateTime.now().isAfter(savedToken.getExpiredat())) {
            sendValidationEmail(savedToken.getUser());
            throw  new RuntimeException("Activation Token has Expired.A new Token has been send to the same email adresse ");
        }
        var user = userRepository.findById(savedToken.getUser().getId()).orElseThrow(()-> new RuntimeException("user not found"));
        user.setEnabled(true);
        userRepository.save(user);
        savedToken.setValidatedat(LocalDateTime.now());
        tokenRepository.save(savedToken);

    }
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public void updateUser(Long id, UserUpdateDTO userUpdateDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        existingUser.setFirstName(userUpdateDTO.getFirstName());
        existingUser.setLastName(userUpdateDTO.getLastName());
        existingUser.setEmail(userUpdateDTO.getEmail());

        if (userUpdateDTO.getPassword() != null && !userUpdateDTO.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userUpdateDTO.getPassword()));
        }

        existingUser.setDateNaissance(userUpdateDTO.getDateNaissance());
        existingUser.setAccountlocked(userUpdateDTO.isAccountlocked());
        existingUser.setEnabled(userUpdateDTO.isEnabled());

        List<Role> roles = userUpdateDTO.getRoles().stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                .collect(Collectors.toList());
        existingUser.setRoles(roles);

        userRepository.save(existingUser);
    }



    @Transactional
    public void deleteUser(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // Remove related tokens
            tokenRepository.deleteByUserId(user.getId());

            // Remove user from all roles
            for (Role role : user.getRoles()) {
                role.getUsers().remove(user);
            }
            user.getRoles().clear();

            // Now delete the user
            userRepository.delete(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }
    }


