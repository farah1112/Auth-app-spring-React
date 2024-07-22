package com.example.eventappbackend.services;

import com.example.eventappbackend.Controllers.RegistrationRequest;
import com.example.eventappbackend.Repositories.RoleRepository;
import com.example.eventappbackend.Repositories.TokenRepository;
import com.example.eventappbackend.Repositories.UserRepository;
import com.example.eventappbackend.user.EmailTemplateName;
import com.example.eventappbackend.user.Token;
import com.example.eventappbackend.user.User;
import jakarta.mail.MessagingException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Data
public class UserService {
    private  final  UserRepository userRepository;
    private final RoleRepository roleRepository;
    private  final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final  EmailService emailService;

    @Value("${application.mailing.frontend.activationUrl}")
    private String activateUrl;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, TokenRepository tokenRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
    }

    public void register(RegistrationRequest request) throws MessagingException {
        var userRole = roleRepository.findByName("USER")
                .orElseThrow(() ->new IllegalStateException("ROLE USER  was not initialized"));
        var user = User.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountlocked(false)
                .enabled(false)
                .roles(List.of(userRole))
                .build();
        userRepository.save(user);
        sendValidationEmail(user);

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
}
