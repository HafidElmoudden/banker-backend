package com.hafidelmoudden.bankerbackend.web;

import com.hafidelmoudden.bankerbackend.dtos.CustomerDTO;
import com.hafidelmoudden.bankerbackend.entities.Customer;
import com.hafidelmoudden.bankerbackend.exceptions.CustomerNotFoundException;
import com.hafidelmoudden.bankerbackend.services.BankAccountService;
import com.hafidelmoudden.bankerbackend.services.CustomerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This controller handles customer-related endpoints with the /api prefix
 */
@RestController
@RequestMapping("/api")
@AllArgsConstructor
@Slf4j
@CrossOrigin("*")
public class ApiCustomerController {
    
    private final BankAccountService bankAccountService;
    private final CustomerService customerService;
    
    @GetMapping("/customers")
    public List<CustomerDTO> customers(){
        log.debug("API: Fetching all customers");
        List<CustomerDTO> customers = bankAccountService.listCustomers();
        log.debug("API: Found {} customers", customers.size());
        return customers;
    }
    
    @GetMapping("/customers/search")
    public List<CustomerDTO> searchCustomers(@RequestParam(name = "keyword",defaultValue = "") String keyword){
        log.debug("API: Searching customers with keyword: '{}'", keyword);
        List<CustomerDTO> customers = bankAccountService.searchCustomers("%"+keyword+"%");
        log.debug("API: Found {} customers matching keyword '{}'", customers.size(), keyword);
        return customers;
    }
    
    @GetMapping("/customers/{id}")
    public CustomerDTO getCustomer(@PathVariable(name = "id") Long customerId) throws CustomerNotFoundException {
        log.debug("API: Fetching customer with ID: {}", customerId);
        CustomerDTO customer = bankAccountService.getCustomer(customerId);
        log.debug("API: Found customer: {}", customer);
        return customer;
    }
    
    @PostMapping("/customers")
    public CustomerDTO saveCustomer(@RequestBody CustomerDTO customerDTO){
        log.debug("API: Saving new customer: {}", customerDTO);
        CustomerDTO savedCustomer = bankAccountService.saveCustomer(customerDTO);
        log.debug("API: Customer saved with ID: {}", savedCustomer.getId());
        return savedCustomer;
    }
    
    @PutMapping("/customers/{customerId}")
    public CustomerDTO updateCustomer(@PathVariable Long customerId, @RequestBody CustomerDTO customerDTO){
        log.debug("API: Updating customer with ID: {}", customerId);
        customerDTO.setId(customerId);
        CustomerDTO updatedCustomer = bankAccountService.updateCustomer(customerDTO);
        log.debug("API: Customer updated: {}", updatedCustomer);
        return updatedCustomer;
    }
    
    @DeleteMapping("/customers/{id}")
    public void deleteCustomer(@PathVariable Long id){
        log.debug("API: Deleting customer with ID: {}", id);
        bankAccountService.deleteCustomer(id);
        log.debug("API: Customer deleted with ID: {}", id);
    }
    
    @GetMapping("/customers/top")
    public List<CustomerDTO> getTopCustomers(@RequestParam(defaultValue = "5") int limit) {
        log.debug("API: Fetching top {} customers", limit);
        List<Customer> topCustomers = customerService.getTopCustomers(limit);
        
        List<CustomerDTO> result = topCustomers.stream()
                .map(customer -> {
                    CustomerDTO dto = new CustomerDTO();
                    dto.setId(customer.getId());
                    dto.setName(customer.getName());
                    dto.setEmail(customer.getEmail());
                    return dto;
                })
                .collect(Collectors.toList());
        
        log.debug("API: Returning {} top customers", result.size());
        return result;
    }
} 