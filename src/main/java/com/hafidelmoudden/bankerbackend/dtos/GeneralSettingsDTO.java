package com.hafidelmoudden.bankerbackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeneralSettingsDTO {
    private String bankName;
    private String bankCode;
    private String defaultCurrency;
    private String supportEmail;
    private String supportPhone;
    private boolean maintenanceMode;
} 