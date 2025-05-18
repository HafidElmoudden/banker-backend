package com.hafidelmoudden.bankerbackend.services;

import com.hafidelmoudden.bankerbackend.dtos.*;
import com.hafidelmoudden.bankerbackend.entities.User;
import com.hafidelmoudden.bankerbackend.exceptions.InvalidCredentialsException;
import com.hafidelmoudden.bankerbackend.exceptions.PasswordMismatchException;
import com.hafidelmoudden.bankerbackend.exceptions.UserAlreadyExistsException;
import com.hafidelmoudden.bankerbackend.exceptions.UserNotFoundException;
import com.hafidelmoudden.bankerbackend.mappers.UserMapper;
import com.hafidelmoudden.bankerbackend.repositories.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    public AuthResponseDTO login(AuthRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        
        User user = (User) authentication.getPrincipal();
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        
        return AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userMapper.fromUser(user))
                .build();
    }

    public AuthResponseDTO register(RegisterRequestDTO request) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }
        
        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        // By default, assign USER role
        List<String> roles = new ArrayList<>();
        roles.add("USER");
        user.setRoles(roles);
        
        User savedUser = userRepository.save(user);
        
        // Generate tokens
        String accessToken = jwtService.generateToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);
        
        return AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userMapper.fromUser(savedUser))
                .build();
    }
    
    public void changePassword(PasswordChangeDTO request) {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }
        
        // Check if new password and confirm password match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException("New password and confirm password do not match");
        }
        
        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
    
    public AuthResponseDTO refreshToken(String refreshToken) {
        try {
            // Extract username from token
            String username = jwtService.extractUsername(refreshToken);
            
            // Find the user
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException("User not found"));
            
            // Validate token
            if (!jwtService.isTokenValid(refreshToken, user)) {
                throw new InvalidCredentialsException("Invalid refresh token");
            }
            
            // Generate new tokens
            String newAccessToken = jwtService.generateToken(user);
            String newRefreshToken = jwtService.generateRefreshToken(user);
            
            return AuthResponseDTO.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .user(userMapper.fromUser(user))
                    .build();
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            throw new InvalidCredentialsException("Invalid refresh token: " + e.getMessage());
        }
    }
} 