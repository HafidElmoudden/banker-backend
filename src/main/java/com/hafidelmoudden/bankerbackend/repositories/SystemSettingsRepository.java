package com.hafidelmoudden.bankerbackend.repositories;

import com.hafidelmoudden.bankerbackend.entities.SystemSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemSettingsRepository extends JpaRepository<SystemSettings, Long> {
} 