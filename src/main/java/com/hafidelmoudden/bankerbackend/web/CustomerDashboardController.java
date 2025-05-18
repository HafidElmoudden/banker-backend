package com.hafidelmoudden.bankerbackend.web;

import com.hafidelmoudden.bankerbackend.entities.Customer;
import com.hafidelmoudden.bankerbackend.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class CustomerDashboardController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerDashboardController.class);
    
    private final CustomerService customerService;

    @GetMapping("/dashboard/customers/top")
    public ResponseEntity<List<Map<String, Object>>> getTopCustomers(@RequestParam(defaultValue = "5") int limit) {
        logger.debug("Fetching top {} customers", limit);
        
        List<Customer> customers = customerService.getTopCustomers(limit);
        logger.debug("Found {} customers", customers.size());
        
        List<Map<String, Object>> topCustomers = customers.stream()
                .map(customer -> {
                    Map<String, Object> customerData = new HashMap<>();
                    customerData.put("id", customer.getId());
                    customerData.put("name", customer.getName());
                    customerData.put("email", customer.getEmail());
                    return customerData;
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(topCustomers);
    }
    
    @GetMapping("/api/dashboard/customers/top")
    public ResponseEntity<List<Map<String, Object>>> getApiTopCustomers(@RequestParam(defaultValue = "5") int limit) {
        return getTopCustomers(limit);
    }
} 