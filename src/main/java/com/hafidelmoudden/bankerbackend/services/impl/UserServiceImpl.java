package com.hafidelmoudden.bankerbackend.services.impl;

import com.hafidelmoudden.bankerbackend.entities.User;
import com.hafidelmoudden.bankerbackend.exceptions.UserAlreadyExistsException;
import com.hafidelmoudden.bankerbackend.exceptions.UserNotFoundException;
import com.hafidelmoudden.bankerbackend.repositories.UserRepository;
import com.hafidelmoudden.bankerbackend.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<User> getAllUsers(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size));
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    @Override
    public User createUser(User user) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists: " + user.getUsername());
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists: " + user.getEmail());
        }

        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Ensure roles are set
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(List.of("USER"));
        }
        
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User updatedUser) {
        User existingUser = getUserById(id);
        
        // Update fields
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());
        
        // Only update password if it's provided
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        
        // Update roles if provided
        if (updatedUser.getRoles() != null && !updatedUser.getRoles().isEmpty()) {
            existingUser.setRoles(updatedUser.getRoles());
        }
        
        existingUser.setActive(updatedUser.isActive());
        
        return userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public User changeUserStatus(Long id, boolean active) {
        User user = getUserById(id);
        user.setActive(active);
        return userRepository.save(user);
    }

    @Override
    public User updateUserRoles(Long id, List<String> roles) {
        User user = getUserById(id);
        user.setRoles(roles);
        return userRepository.save(user);
    }

    @Override
    public Page<User> searchUsers(String keyword, int page, int size) {
        return userRepository.searchUsers(keyword, PageRequest.of(page, size));
    }
} 