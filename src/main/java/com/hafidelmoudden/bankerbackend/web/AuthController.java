package com.hafidelmoudden.bankerbackend.web;

import com.hafidelmoudden.bankerbackend.dtos.*;
import com.hafidelmoudden.bankerbackend.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody RegisterRequestDTO request) {
        return ResponseEntity.ok(authService.register(request));
    }
    
    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody PasswordChangeDTO request) {
        authService.changePassword(request);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponseDTO> refreshToken(@RequestBody RefreshTokenRequestDTO request) {
        return ResponseEntity.ok(authService.refreshToken(request.getRefreshToken()));
    }
} 