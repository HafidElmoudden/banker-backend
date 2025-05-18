package com.hafidelmoudden.bankerbackend.web;

import com.hafidelmoudden.bankerbackend.entities.User;
import com.hafidelmoudden.bankerbackend.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin("*")
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Page<User>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Fetching users page {} with size {}", page, size);
        return ResponseEntity.ok(userService.getAllUsers(page, size));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        log.info("Fetching user with id: {}", id);
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        log.info("Creating new user: {}", user.getUsername());
        return ResponseEntity.ok(userService.createUser(user));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        log.info("Updating user with id: {}", id);
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Deleting user with id: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<User> changeUserStatus(@PathVariable Long id, @RequestBody Map<String, Boolean> status) {
        boolean active = status.get("active");
        log.info("Changing user status with id: {} to active: {}", id, active);
        return ResponseEntity.ok(userService.changeUserStatus(id, active));
    }

    @PatchMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<User> updateUserRoles(@PathVariable Long id, @RequestBody Map<String, List<String>> rolesMap) {
        List<String> roles = rolesMap.get("roles");
        log.info("Updating roles for user with id: {} to: {}", id, roles);
        return ResponseEntity.ok(userService.updateUserRoles(id, roles));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Page<User>> searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Searching users with keyword: {} on page {} with size {}", keyword, page, size);
        return ResponseEntity.ok(userService.searchUsers(keyword, page, size));
    }
} 