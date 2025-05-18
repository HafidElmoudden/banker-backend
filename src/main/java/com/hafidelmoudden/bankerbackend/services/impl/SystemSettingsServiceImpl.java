package com.hafidelmoudden.bankerbackend.services.impl;

import com.hafidelmoudden.bankerbackend.dtos.GeneralSettingsDTO;
import com.hafidelmoudden.bankerbackend.dtos.SecuritySettingsDTO;
import com.hafidelmoudden.bankerbackend.dtos.SystemSettingsDTO;
import com.hafidelmoudden.bankerbackend.entities.SystemSettings;
import com.hafidelmoudden.bankerbackend.repositories.SystemSettingsRepository;
import com.hafidelmoudden.bankerbackend.services.SystemSettingsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class SystemSettingsServiceImpl implements SystemSettingsService {

    private final SystemSettingsRepository systemSettingsRepository;

    @Override
    public SystemSettingsDTO getAllSettings() {
        SystemSettings settings = getOrCreateSettings();
        
        return SystemSettingsDTO.builder()
                .general(GeneralSettingsDTO.builder()
                        .bankName(settings.getBankName())
                        .bankCode(settings.getBankCode())
                        .defaultCurrency(settings.getDefaultCurrency())
                        .supportEmail(settings.getSupportEmail())
                        .supportPhone(settings.getSupportPhone())
                        .maintenanceMode(settings.isMaintenanceMode())
                        .build())
                .security(SecuritySettingsDTO.builder()
                        .sessionTimeoutMinutes(settings.getSessionTimeoutMinutes())
                        .maxLoginAttempts(settings.getMaxLoginAttempts())
                        .passwordExpiryDays(settings.getPasswordExpiryDays())
                        .twoFactorAuthRequired(settings.isTwoFactorAuthRequired())
                        .minimumPasswordLength(settings.getMinimumPasswordLength())
                        .requireSpecialCharacters(settings.isRequireSpecialCharacters())
                        .requireNumbers(settings.isRequireNumbers())
                        .build())
                .build();
    }

    @Override
    public GeneralSettingsDTO updateGeneralSettings(GeneralSettingsDTO generalSettings) {
        SystemSettings settings = getOrCreateSettings();
        
        settings.setBankName(generalSettings.getBankName());
        settings.setBankCode(generalSettings.getBankCode());
        settings.setDefaultCurrency(generalSettings.getDefaultCurrency());
        settings.setSupportEmail(generalSettings.getSupportEmail());
        settings.setSupportPhone(generalSettings.getSupportPhone());
        settings.setMaintenanceMode(generalSettings.isMaintenanceMode());
        
        systemSettingsRepository.save(settings);
        
        return generalSettings;
    }

    @Override
    public SecuritySettingsDTO updateSecuritySettings(SecuritySettingsDTO securitySettings) {
        SystemSettings settings = getOrCreateSettings();
        
        settings.setSessionTimeoutMinutes(securitySettings.getSessionTimeoutMinutes());
        settings.setMaxLoginAttempts(securitySettings.getMaxLoginAttempts());
        settings.setPasswordExpiryDays(securitySettings.getPasswordExpiryDays());
        settings.setTwoFactorAuthRequired(securitySettings.isTwoFactorAuthRequired());
        settings.setMinimumPasswordLength(securitySettings.getMinimumPasswordLength());
        settings.setRequireSpecialCharacters(securitySettings.isRequireSpecialCharacters());
        settings.setRequireNumbers(securitySettings.isRequireNumbers());
        
        systemSettingsRepository.save(settings);
        
        return securitySettings;
    }
    
    private SystemSettings getOrCreateSettings() {
        return systemSettingsRepository.findById(1L)
                .orElseGet(() -> {
                    SystemSettings defaultSettings = new SystemSettings();
                    defaultSettings.setId(1L);
                    defaultSettings.setBankName("Default Bank");
                    defaultSettings.setBankCode("DEFBANK");
                    defaultSettings.setDefaultCurrency("USD");
                    defaultSettings.setSupportEmail("support@defaultbank.com");
                    defaultSettings.setSupportPhone("+1234567890");
                    defaultSettings.setMaintenanceMode(false);
                    defaultSettings.setSessionTimeoutMinutes(30);
                    defaultSettings.setMaxLoginAttempts(5);
                    defaultSettings.setPasswordExpiryDays(90);
                    defaultSettings.setTwoFactorAuthRequired(false);
                    defaultSettings.setMinimumPasswordLength(8);
                    defaultSettings.setRequireSpecialCharacters(true);
                    defaultSettings.setRequireNumbers(true);
                    return systemSettingsRepository.save(defaultSettings);
                });
    }
} 