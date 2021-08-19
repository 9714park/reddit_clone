package com.rsmp.redditclone.controller;

import com.rsmp.redditclone.model.dto.AuthenticationToken;
import com.rsmp.redditclone.model.dto.LoginRequest;
import com.rsmp.redditclone.model.dto.RegisterRequest;
import com.rsmp.redditclone.security.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("signup")
    public ResponseEntity<String> signup(@RequestBody RegisterRequest registerRequest) {
        authService.signup(registerRequest);
        return new ResponseEntity<>("User Registration Successful", HttpStatus.OK);
    }

    @GetMapping("accountVerification/{token}")
    public ResponseEntity<String> verifyAccount(@PathVariable String token) {
        authService.verifyAccount(token);
        return new ResponseEntity<>("Account Activated Successfully", HttpStatus.OK);
    }

    @PostMapping("/login")
    public AuthenticationToken login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);

    }


}
