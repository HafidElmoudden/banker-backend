package com.hafidelmoudden.bankerbackend.services;

import com.hafidelmoudden.bankerbackend.entities.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {
    Page<User> getAllUsers(int page, int size);
    User getUserById(Long id);
    User createUser(User user);
    User updateUser(Long id, User user);
    void deleteUser(Long id);
    User changeUserStatus(Long id, boolean active);
    User updateUserRoles(Long id, List<String> roles);
    Page<User> searchUsers(String keyword, int page, int size);
} 