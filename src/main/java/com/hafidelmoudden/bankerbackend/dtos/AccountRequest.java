package com.hafidelmoudden.bankerbackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequest {
    private Long customerId;
    private double initialBalance;
    private String accountType;
    private Double overDraft;
    private Double interestRate;
    private String createdBy;
} 