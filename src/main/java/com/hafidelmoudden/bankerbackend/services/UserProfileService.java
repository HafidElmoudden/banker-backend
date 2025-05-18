package com.hafidelmoudden.bankerbackend.services;

import com.hafidelmoudden.bankerbackend.dtos.BankAccountDTO;
import com.hafidelmoudden.bankerbackend.dtos.CustomerDTO;
import com.hafidelmoudden.bankerbackend.entities.BankAccount;
import com.hafidelmoudden.bankerbackend.entities.Customer;
import com.hafidelmoudden.bankerbackend.entities.User;
import com.hafidelmoudden.bankerbackend.exceptions.CustomerNotFoundException;
import com.hafidelmoudden.bankerbackend.exceptions.UserNotFoundException;
import com.hafidelmoudden.bankerbackend.mappers.BankAccountMapperImpl;
import com.hafidelmoudden.bankerbackend.repositories.BankAccountRepository;
import com.hafidelmoudden.bankerbackend.repositories.CustomerRepository;
import com.hafidelmoudden.bankerbackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserProfileService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final BankAccountRepository bankAccountRepository;
    private final BankAccountMapperImpl bankAccountMapper;

    /**
     * Get customer profile associated with a user
     */
    public CustomerDTO getCustomerProfileByUsername(String username) throws CustomerNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));
        
        // Find customer associated with the user
        // In a real application, there would be a direct relationship between User and Customer
        // For now, we'll assume the customer has the same name as the user
        Customer customer = customerRepository.findByName(username)
                .orElseThrow(() -> new CustomerNotFoundException("Customer profile not found for user: " + username));
        
        return bankAccountMapper.fromCustomer(customer);
    }

    /**
     * Get all bank accounts owned by a user
     */
    public List<BankAccountDTO> getUserBankAccounts(String username) throws CustomerNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));
        
        // Find customer associated with the user
        Customer customer = customerRepository.findByName(username)
                .orElseThrow(() -> new CustomerNotFoundException("Customer profile not found for user: " + username));
        
        // Get all accounts for this customer
        List<BankAccount> accounts = bankAccountRepository.findByCustomerId(customer.getId());
        
        return accounts.stream()
                .map(bankAccountMapper::toBankAccountDTO)
                .collect(Collectors.toList());
    }

    /**
     * Check if a bank account is owned by the specified user
     */
    public boolean isAccountOwnedByUser(String accountId, String username) throws CustomerNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));
        
        // Find customer associated with the user
        Customer customer = customerRepository.findByName(username)
                .orElseThrow(() -> new CustomerNotFoundException("Customer profile not found for user: " + username));
        
        // Check if the account belongs to this customer
        BankAccount account = bankAccountRepository.findById(accountId).orElse(null);
        if (account == null) {
            return false;
        }
        
        return account.getCustomer().getId().equals(customer.getId());
    }

    /**
     * Get dashboard statistics for a specific user
     */
    public Map<String, Object> getUserDashboardStatistics(String username) throws CustomerNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));
        
        // Find customer associated with the user
        Customer customer = customerRepository.findByName(username)
                .orElseThrow(() -> new CustomerNotFoundException("Customer profile not found for user: " + username));
        
        // Get all accounts for this customer
        List<BankAccount> accounts = bankAccountRepository.findByCustomerId(customer.getId());
        
        Map<String, Object> statistics = new HashMap<>();
        
        // Count of accounts
        statistics.put("totalAccounts", accounts.size());
        
        // Total balance
        double totalBalance = accounts.stream()
                .mapToDouble(BankAccount::getBalance)
                .sum();
        statistics.put("totalBalance", totalBalance);
        
        // Count of operations
        long operationCount = accounts.stream()
                .flatMap(account -> account.getAccountOperations().stream())
                .count();
        statistics.put("totalTransactions", operationCount);
        
        return statistics;
    }
} 