package com.hafidelmoudden.bankerbackend.web;

import com.hafidelmoudden.bankerbackend.dtos.GeneralSettingsDTO;
import com.hafidelmoudden.bankerbackend.dtos.SecuritySettingsDTO;
import com.hafidelmoudden.bankerbackend.dtos.SystemSettingsDTO;
import com.hafidelmoudden.bankerbackend.services.SystemSettingsService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/settings")
@AllArgsConstructor
@CrossOrigin("*")
public class SystemSettingsController {

    private final SystemSettingsService systemSettingsService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<SystemSettingsDTO> getAllSettings() {
        return ResponseEntity.ok(systemSettingsService.getAllSettings());
    }

    @PutMapping("/general")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<GeneralSettingsDTO> updateGeneralSettings(@RequestBody GeneralSettingsDTO generalSettings) {
        return ResponseEntity.ok(systemSettingsService.updateGeneralSettings(generalSettings));
    }

    @PutMapping("/security")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<SecuritySettingsDTO> updateSecuritySettings(@RequestBody SecuritySettingsDTO securitySettings) {
        return ResponseEntity.ok(systemSettingsService.updateSecuritySettings(securitySettings));
    }
} 