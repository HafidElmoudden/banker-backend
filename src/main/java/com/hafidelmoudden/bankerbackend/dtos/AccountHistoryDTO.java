package com.hafidelmoudden.bankerbackend.dtos;

import com.hafidelmoudden.bankerbackend.enums.AccountStatus;
import lombok.Data;

import java.util.Date;
import java.util.List;
@Data
public class AccountHistoryDTO {
    private String accountId;
    private double balance;
    private int currentPage;
    private int totalPages;
    private int pageSize;
    private List<AccountOperationDTO> accountOperationDTOS;
    private Long customerId;
    private String customerName;
    private String accountType;
    private Date createdAt;
    private String createdBy;
    private AccountStatus status;
}
