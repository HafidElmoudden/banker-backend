package com.hafidelmoudden.bankerbackend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "system_settings")
public class SystemSettings {
    @Id
    private Long id;
    
    // General settings
    private String bankName;
    private String bankCode;
    private String defaultCurrency;
    private String supportEmail;
    private String supportPhone;
    private boolean maintenanceMode;
    
    // Security settings
    private int sessionTimeoutMinutes;
    private int maxLoginAttempts;
    private int passwordExpiryDays;
    private boolean twoFactorAuthRequired;
    private int minimumPasswordLength;
    private boolean requireSpecialCharacters;
    private boolean requireNumbers;
} 