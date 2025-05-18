package com.hafidelmoudden.bankerbackend.services;

import com.hafidelmoudden.bankerbackend.dtos.CustomerDTO;
import com.hafidelmoudden.bankerbackend.entities.BankAccount;
import com.hafidelmoudden.bankerbackend.entities.Customer;
import com.hafidelmoudden.bankerbackend.mappers.BankAccountMapperImpl;
import com.hafidelmoudden.bankerbackend.repositories.BankAccountRepository;
import com.hafidelmoudden.bankerbackend.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final BankAccountRepository bankAccountRepository;
    private final BankAccountMapperImpl dtoMapper;

    public List<Customer> getTopCustomers(int limit) {
        List<Customer> customers = customerRepository.findAll();
        
        // For now, just return the first 'limit' customers
        // In a real application, you would calculate this based on account balances or activity
        return customers.stream()
                .limit(limit)
                .toList();
    }
    
    public List<Map<String, Object>> getTopCustomersWithDetails(int limit) {
        List<Customer> customers = customerRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (Customer customer : customers) {
            // Get all accounts for this customer
            List<BankAccount> accounts = bankAccountRepository.findByCustomerId(customer.getId());
            
            // Calculate total balance
            double totalBalance = accounts.stream()
                    .mapToDouble(BankAccount::getBalance)
                    .sum();
            
            // Create result map with all required details
            Map<String, Object> customerData = new HashMap<>();
            customerData.put("id", customer.getId());
            customerData.put("name", customer.getName());
            customerData.put("email", customer.getEmail());
            customerData.put("accountCount", accounts.size());
            customerData.put("totalBalance", totalBalance);
            
            result.add(customerData);
        }
        
        // Sort by total balance (descending) and limit results
        return result.stream()
                .sorted((c1, c2) -> Double.compare((Double)c2.get("totalBalance"), (Double)c1.get("totalBalance")))
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    public Page<CustomerDTO> listCustomersWithPagination(Pageable pageable) {
        Page<Customer> customersPage = customerRepository.findAll(pageable);
        return customersPage.map(customer -> dtoMapper.fromCustomer(customer));
    }
    
    public Page<CustomerDTO> searchCustomersWithPagination(String keyword, Pageable pageable) {
        Page<Customer> customersPage = customerRepository.searchCustomersPaged(keyword, pageable);
        return customersPage.map(customer -> dtoMapper.fromCustomer(customer));
    }
    
    public List<CustomerDTO> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream()
                .map(customer -> dtoMapper.fromCustomer(customer))
                .collect(Collectors.toList());
    }
} 