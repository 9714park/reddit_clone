package com.rsmp.redditclone.service;

import com.rsmp.redditclone.model.dto.RegisterRequest;
import com.rsmp.redditclone.model.entity.User;
import com.rsmp.redditclone.model.entity.VerificationToken;
import com.rsmp.redditclone.repository.UserRepository;
import com.rsmp.redditclone.repository.VerificationTokenRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Transactional
    public void signup(RegisterRequest registerRequest) {
        log.debug("Signing up user {}", registerRequest.getUsername());
        User user = User.builder().username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .created(Instant.now())
                .enabled(false)
                .build();


        try {
            User save = userRepository.save(user);
        } catch (Exception e) {
            log.error("Registering user {} to DB failed", user.getUsername());
        }

        generateVerificationToken(user);
    }

    private String generateVerificationToken(User user) {
        log.debug("Creating token for user {}", user.getUsername());
        String tokenStr = UUID.randomUUID().toString();

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(tokenStr);
        verificationToken.setUser(user);

        try {
        verificationTokenRepository.save(verificationToken);
        } catch (Exception e) {
            log.error("Registering verification token for {} to DB failed", user.getUsername());
        }

        return tokenStr;
    }
}
