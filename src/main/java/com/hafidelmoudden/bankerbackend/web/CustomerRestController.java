package com.hafidelmoudden.bankerbackend.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.hafidelmoudden.bankerbackend.dtos.CustomerDTO;
import com.hafidelmoudden.bankerbackend.entities.Customer;
import com.hafidelmoudden.bankerbackend.exceptions.CustomerNotFoundException;
import com.hafidelmoudden.bankerbackend.services.BankAccountService;
import com.hafidelmoudden.bankerbackend.services.CustomerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@Slf4j
@CrossOrigin("*")
public class CustomerRestController {
    private BankAccountService bankAccountService;
    private CustomerService customerService;
    
    @GetMapping("/customers")
    public Page<CustomerDTO> customers(
            @RequestParam(name="page", defaultValue = "0") int page,
            @RequestParam(name="size", defaultValue = "10") int size){
        log.debug("Fetching customers page {} with size {}", page, size);
        return customerService.listCustomersWithPagination(PageRequest.of(page, size));
    }
    
    @GetMapping("/customers/all")
    public List<CustomerDTO> allCustomers(){
        log.debug("Fetching all customers without pagination");
        List<CustomerDTO> customers = bankAccountService.listCustomers();
        log.debug("Found {} customers", customers.size());
        return customers;
    }
    
    @GetMapping("/customers/search")
    public Page<CustomerDTO> searchCustomers(
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @RequestParam(name="page", defaultValue = "0") int page,
            @RequestParam(name="size", defaultValue = "10") int size){
        log.debug("Searching customers with keyword: '{}', page {}, size {}", keyword, page, size);
        return customerService.searchCustomersWithPagination("%" + keyword + "%", PageRequest.of(page, size));
    }
    
    @GetMapping("/customers/{id}")
    public CustomerDTO getCustomer(@PathVariable(name = "id") Long customerId) throws CustomerNotFoundException {
        log.debug("Fetching customer with ID: {}", customerId);
        CustomerDTO customer = bankAccountService.getCustomer(customerId);
        log.debug("Found customer: {}", customer);
        return customer;
    }
    
    @PostMapping("/customers")
    public CustomerDTO saveCustomer(@RequestBody CustomerDTO customerDTO){
        log.debug("Saving new customer: {}", customerDTO);
        CustomerDTO savedCustomer = bankAccountService.saveCustomer(customerDTO);
        log.debug("Customer saved with ID: {}", savedCustomer.getId());
        return savedCustomer;
    }
    
    @PutMapping("/customers/{customerId}")
    public CustomerDTO updateCustomer(@PathVariable Long customerId, @RequestBody CustomerDTO customerDTO){
        log.debug("Updating customer with ID: {}", customerId);
        customerDTO.setId(customerId);
        CustomerDTO updatedCustomer = bankAccountService.updateCustomer(customerDTO);
        log.debug("Customer updated: {}", updatedCustomer);
        return updatedCustomer;
    }
    
    @DeleteMapping("/customers/{id}")
    public void deleteCustomer(@PathVariable Long id){
        log.debug("Deleting customer with ID: {}", id);
        bankAccountService.deleteCustomer(id);
        log.debug("Customer deleted with ID: {}", id);
    }
    
    @GetMapping("/customers/top")
    public List<Map<String, Object>> getTopCustomers(@RequestParam(defaultValue = "5") int limit) {
        log.debug("Fetching top {} customers", limit);
        return customerService.getTopCustomersWithDetails(limit);
    }
}
