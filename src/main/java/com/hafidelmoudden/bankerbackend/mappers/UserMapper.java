package com.hafidelmoudden.bankerbackend.mappers;

import com.hafidelmoudden.bankerbackend.dtos.UserDTO;
import com.hafidelmoudden.bankerbackend.entities.User;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {
    
    public UserDTO fromUser(User user) {
        if (user == null) return null;
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles(),
                user.isActive(),
                user.getCreatedAt()
        );
    }
} 