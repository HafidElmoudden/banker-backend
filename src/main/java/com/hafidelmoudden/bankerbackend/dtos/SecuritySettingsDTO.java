package com.hafidelmoudden.bankerbackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecuritySettingsDTO {
    private int sessionTimeoutMinutes;
    private int maxLoginAttempts;
    private int passwordExpiryDays;
    private boolean twoFactorAuthRequired;
    private int minimumPasswordLength;
    private boolean requireSpecialCharacters;
    private boolean requireNumbers;
} 