package com.rsmp.redditclone.service;

import com.rsmp.redditclone.exception.SpringRedditException;
import com.rsmp.redditclone.model.NotificationEmail;
import com.rsmp.redditclone.model.dto.RegisterRequest;
import com.rsmp.redditclone.model.entity.User;
import com.rsmp.redditclone.model.entity.VerificationToken;
import com.rsmp.redditclone.repository.UserRepository;
import com.rsmp.redditclone.repository.VerificationTokenRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;

    @Transactional
    public void signup(RegisterRequest registerRequest) {
        log.info("Signing up user {}", registerRequest.getUsername());

        // Create new user
        User user = User.builder().username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .created(Instant.now())
                .enabled(false)
                .build();


        try {
            // Save user to DB
            User save = userRepository.save(user);
        } catch (Exception e) {
            throw new SpringRedditException("Failed to register user " +user.getUsername() + " to DB");
        }

        // Create verification token
        String token = generateVerificationToken(user);

        // Send email with token to activate account
        mailService.sendMail(new NotificationEmail("Please activate your account",
                user.getEmail(), "Thank you for signing up to Spring Reddit, " +
                "please click on the below url to activate your account : " +
                "http://localhost:8080/api/auth/accountVerification/" + token));
    }

    private String generateVerificationToken(User user) {
        log.info("Creating token for user {}", user.getUsername());
        String tokenStr = UUID.randomUUID().toString();

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(tokenStr);
        verificationToken.setUser(user);

        try {
            verificationTokenRepository.save(verificationToken);
        } catch (Exception e) {
            throw new SpringRedditException("Registering verification token for {} to DB failed" + user.getUsername(), e);
        }

        return tokenStr;
    }

    public void verifyAccount(String token) {
        log.info("Verifying account with token {}", token);
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new SpringRedditException("Invalid token"));

        activateUser(verificationToken);
    }

    @Transactional
    public void activateUser(VerificationToken verificationToken) {
        String username = verificationToken.getUser().getUsername();
        log.info("Activating account {}", username );

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new SpringRedditException("User not found with name " + username));

        user.setEnabled(true);
        userRepository.save(user);
    }
}
