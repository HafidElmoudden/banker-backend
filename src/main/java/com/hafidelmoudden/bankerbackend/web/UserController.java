package com.hafidelmoudden.bankerbackend.web;

import com.hafidelmoudden.bankerbackend.dtos.AccountHistoryDTO;
import com.hafidelmoudden.bankerbackend.dtos.BankAccountDTO;
import com.hafidelmoudden.bankerbackend.dtos.CustomerDTO;
import com.hafidelmoudden.bankerbackend.entities.User;
import com.hafidelmoudden.bankerbackend.exceptions.BankAccountNotFoundException;
import com.hafidelmoudden.bankerbackend.exceptions.CustomerNotFoundException;
import com.hafidelmoudden.bankerbackend.services.BankAccountService;
import com.hafidelmoudden.bankerbackend.services.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin("*")
public class UserController {

    private final UserProfileService userProfileService;
    private final BankAccountService bankAccountService;

    @GetMapping("/customers/profile")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<CustomerDTO> getUserProfile() throws CustomerNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.debug("Fetching profile for user: {}", username);
        
        CustomerDTO customerDTO = userProfileService.getCustomerProfileByUsername(username);
        return ResponseEntity.ok(customerDTO);
    }
    
    @GetMapping("/accounts/user")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<BankAccountDTO>> getUserAccounts() throws CustomerNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.debug("Fetching accounts for user: {}", username);
        
        List<BankAccountDTO> accounts = userProfileService.getUserBankAccounts(username);
        return ResponseEntity.ok(accounts);
    }
    
    @GetMapping("/operations/user/account/{accountId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<AccountHistoryDTO> getUserAccountOperations(
            @PathVariable String accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) throws BankAccountNotFoundException, CustomerNotFoundException {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.debug("Fetching operations for account: {} of user: {}", accountId, username);
        
        // Verify the account belongs to the user
        if (!userProfileService.isAccountOwnedByUser(accountId, username)) {
            return ResponseEntity.status(403).build();
        }
        
        AccountHistoryDTO accountHistory = bankAccountService.getAccountHistory(accountId, page, size);
        return ResponseEntity.ok(accountHistory);
    }
    
    @GetMapping("/dashboard/user/statistics")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getUserDashboardStatistics() throws CustomerNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.debug("Fetching dashboard statistics for user: {}", username);
        
        Map<String, Object> statistics = userProfileService.getUserDashboardStatistics(username);
        return ResponseEntity.ok(statistics);
    }
} 