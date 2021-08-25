package com.rsmp.redditclone.auth.service;

import com.rsmp.redditclone.auth.model.dto.AuthenticationResponse;
import com.rsmp.redditclone.auth.model.dto.LoginRequest;
import com.rsmp.redditclone.auth.model.dto.RefreshTokenRequest;
import com.rsmp.redditclone.auth.model.dto.RegisterRequest;
import com.rsmp.redditclone.auth.model.entity.VerificationToken;
import com.rsmp.redditclone.auth.repository.VerificationTokenRepository;
import com.rsmp.redditclone.exception.SpringRedditException;
import com.rsmp.redditclone.model.NotificationEmail;
import com.rsmp.redditclone.model.entity.User;
import com.rsmp.redditclone.repository.UserRepository;
import com.rsmp.redditclone.service.MailService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final MailService mailService;

    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;

    // Sign up new user and send activation email
    @Transactional
    public void signup(RegisterRequest registerRequest) {
        log.info("Signing up user {}", registerRequest.getUsername());

        // Create new user
        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .created(Instant.now())
                .enabled(false)
                .build();


        try {
            // Save user to DB
            User save = userRepository.save(user);
        } catch (Exception e) {
            throw new SpringRedditException("Failed to register user " + user
                    .getUsername() + " to DB");
        }

        // Create verification token
        String token = generateVerificationToken(user);

        // Send email with token to activate account
        mailService.sendMail(new NotificationEmail("Please activate your account",
                user.getEmail(), "Thank you for signing up to Spring Reddit, " +
                "please click on the below url to activate your account : " +
                "http://localhost:8080/api/auth/accountVerification/" + token));
    }

    // Create verification token to for account activation
    private String generateVerificationToken(User user) {
        log.info("Creating token for user {}", user.getUsername());
        String tokenStr = UUID.randomUUID()
                .toString();

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(tokenStr);
        verificationToken.setUser(user);

        try {
            verificationTokenRepository.save(verificationToken);
        } catch (Exception e) {
            throw new SpringRedditException("Registering verification token for {} to DB failed" + user
                    .getUsername(), e);
        }

        return tokenStr;
    }

    // Verify account via token
    public void verifyAccount(String token) {
        log.info("Verifying account with token {}", token);
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new SpringRedditException("Invalid token"));

        activateUser(verificationToken);
    }


    // Activate user
    @Transactional
    public void activateUser(VerificationToken verificationToken) {
        String username = verificationToken.getUser().getUsername();
        log.info("Activating account {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new SpringRedditException("User not found with name " + username));

        user.setEnabled(true);
        userRepository.save(user);
    }

    // Authenticate user and provide JWT token
    public AuthenticationResponse login(LoginRequest loginRequest) {
        log.info("Authenticating account {}", loginRequest.getUsername());
        Authentication authenticate = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest
                        .getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authenticate);
        String token = jwtProvider.generateToken(authenticate);

        return AuthenticationResponse.builder().token(token)
                .refreshToken(refreshTokenService.generateRefreshToken().getToken())
                .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                .username(loginRequest.getUsername())
                .build();
    }

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new SpringRedditException("Failed to find user " + principal
                        .getUsername()));
    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
        String token = jwtProvider.generateTokenWithUsername(refreshTokenRequest.getUsername());
        return AuthenticationResponse.builder()
                .token(token)
                .refreshToken(refreshTokenRequest.getRefreshToken())
                .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                .username(refreshTokenRequest.getUsername())
                .build();
    }
}
