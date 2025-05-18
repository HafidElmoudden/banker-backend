package com.hafidelmoudden.bankerbackend.services;

import com.hafidelmoudden.bankerbackend.dtos.GeneralSettingsDTO;
import com.hafidelmoudden.bankerbackend.dtos.SecuritySettingsDTO;
import com.hafidelmoudden.bankerbackend.dtos.SystemSettingsDTO;

public interface SystemSettingsService {
    SystemSettingsDTO getAllSettings();
    GeneralSettingsDTO updateGeneralSettings(GeneralSettingsDTO generalSettings);
    SecuritySettingsDTO updateSecuritySettings(SecuritySettingsDTO securitySettings);
} 