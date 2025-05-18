package com.hafidelmoudden.bankerbackend.dtos;

import lombok.Data;
import com.hafidelmoudden.bankerbackend.enums.AccountStatus;
import java.util.Date;

@Data
public class BankAccountDTO {
    private String type;
    private String accountType;
    private Long customerId;
    private String customerName;
    private String accountId; // Alias for id to maintain frontend compatibility
    private Date createdAt;
    private String createdBy;
    private AccountStatus status;
}
