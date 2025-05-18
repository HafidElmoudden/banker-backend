package com.hafidelmoudden.bankerbackend.web;

import com.hafidelmoudden.bankerbackend.entities.AccountOperation;
import com.hafidelmoudden.bankerbackend.entities.BankAccount;
import com.hafidelmoudden.bankerbackend.entities.CurrentAccount;
import com.hafidelmoudden.bankerbackend.entities.SavingAccount;
import com.hafidelmoudden.bankerbackend.enums.OperationType;
import com.hafidelmoudden.bankerbackend.repositories.AccountOperationRepository;
import com.hafidelmoudden.bankerbackend.repositories.BankAccountRepository;
import com.hafidelmoudden.bankerbackend.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class DashboardController {

    private final CustomerRepository customerRepository;
    private final BankAccountRepository bankAccountRepository;
    private final AccountOperationRepository accountOperationRepository;

    @GetMapping("/dashboard/statistics")
    public ResponseEntity<Map<String, Object>> getDashboardStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // Count of customers
        long customerCount = customerRepository.count();
        statistics.put("totalCustomers", customerCount);
        
        // Count of bank accounts
        long accountCount = bankAccountRepository.count();
        statistics.put("totalAccounts", accountCount);
        
        // Count of operations
        long operationCount = accountOperationRepository.count();
        statistics.put("totalTransactions", operationCount);
        
        // Total balance
        double totalBalance = bankAccountRepository.findAll().stream()
                .mapToDouble(BankAccount::getBalance)
                .sum();
        statistics.put("totalBalance", totalBalance);
        
        return ResponseEntity.ok(statistics);
    }
    
    @GetMapping("/dashboard/operations/monthly")
    public ResponseEntity<List<Map<String, Object>>> getMonthlyOperations(@RequestParam(defaultValue = "0") int requestedYear) {
        List<Map<String, Object>> monthlyData = new ArrayList<>();
        
        // Use current year if not specified
        final int year = requestedYear == 0 ? Calendar.getInstance().get(Calendar.YEAR) : requestedYear;
        
        System.out.println("Getting monthly operations for year: " + year);
        
        // Get all operations for the year
        List<AccountOperation> operations = accountOperationRepository.findAll().stream()
                .filter(op -> {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(op.getOperationDate());
                    return cal.get(Calendar.YEAR) == year;
                })
                .collect(Collectors.toList());
        
        System.out.println("Found " + operations.size() + " operations for year " + year);
        
        // Group by month
        Map<Integer, List<AccountOperation>> operationsByMonth = operations.stream()
                .collect(Collectors.groupingBy(op -> {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(op.getOperationDate());
                    return cal.get(Calendar.MONTH);
                }));
        
        // Create monthly data
        for (int month = 0; month < 12; month++) {
            Map<String, Object> monthData = new HashMap<>();
            List<AccountOperation> monthOperations = operationsByMonth.getOrDefault(month, Collections.emptyList());
            
            double credits = monthOperations.stream()
                    .filter(op -> op.getType() == OperationType.CREDIT)
                    .mapToDouble(AccountOperation::getAmount)
                    .sum();
            
            double debits = monthOperations.stream()
                    .filter(op -> op.getType() == OperationType.DEBIT)
                    .mapToDouble(AccountOperation::getAmount)
                    .sum();
            
            monthData.put("month", month + 1);
            monthData.put("credits", credits);
            monthData.put("debits", debits);
            monthData.put("count", monthOperations.size());
            
            monthlyData.add(monthData);
        }
        
        return ResponseEntity.ok(monthlyData);
    }
    
    @GetMapping("/dashboard/accounts/distribution")
    public ResponseEntity<Map<String, Object>> getAccountTypeDistribution() {
        Map<String, Object> distribution = new HashMap<>();
        
        List<BankAccount> accounts = bankAccountRepository.findAll();
        System.out.println("Found " + accounts.size() + " total accounts");
        
        long savingAccounts = accounts.stream()
                .filter(acc -> acc instanceof SavingAccount)
                .count();
        
        long currentAccounts = accounts.stream()
                .filter(acc -> acc instanceof CurrentAccount)
                .count();
        
        System.out.println("Account distribution - Saving: " + savingAccounts + ", Current: " + currentAccounts);
        
        distribution.put("savingAccounts", savingAccounts);
        distribution.put("currentAccounts", currentAccounts);
        
        return ResponseEntity.ok(distribution);
    }
    
    @GetMapping("/dashboard/transactions/recent")
    public ResponseEntity<List<Map<String, Object>>> getRecentTransactions(@RequestParam(defaultValue = "10") int limit) {
        List<AccountOperation> recentOps = accountOperationRepository.findAll().stream()
                .sorted(Comparator.comparing(AccountOperation::getOperationDate).reversed())
                .limit(limit)
                .collect(Collectors.toList());
        
        List<Map<String, Object>> result = recentOps.stream().map(op -> {
            Map<String, Object> transaction = new HashMap<>();
            transaction.put("id", op.getId());
            transaction.put("date", op.getOperationDate());
            transaction.put("amount", op.getAmount());
            transaction.put("type", op.getType());
            transaction.put("description", op.getDescription());
            transaction.put("accountId", op.getBankAccount().getId());
            return transaction;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(result);
    }
    
    // Map other endpoints to the main statistics endpoint for now
    @GetMapping("/api/dashboard/statistics")
    public ResponseEntity<Map<String, Object>> getApiDashboardStatistics() {
        return getDashboardStatistics();
    }
    
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        return getDashboardStatistics();
    }
    
    @GetMapping("/api/statistics")
    public ResponseEntity<Map<String, Object>> getApiStatistics() {
        return getDashboardStatistics();
    }
    
    @GetMapping("/api/dashboard/operations/monthly")
    public ResponseEntity<List<Map<String, Object>>> getApiMonthlyOperations(@RequestParam(defaultValue = "0") int requestedYear) {
        return getMonthlyOperations(requestedYear);
    }
    
    @GetMapping("/api/dashboard/accounts/distribution")
    public ResponseEntity<Map<String, Object>> getApiAccountTypeDistribution() {
        return getAccountTypeDistribution();
    }
    
    @GetMapping("/api/dashboard/transactions/recent")
    public ResponseEntity<List<Map<String, Object>>> getApiRecentTransactions(@RequestParam(defaultValue = "10") int limit) {
        return getRecentTransactions(limit);
    }
} 