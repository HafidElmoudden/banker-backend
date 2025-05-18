package com.hafidelmoudden.bankerbackend;

import com.hafidelmoudden.bankerbackend.dtos.CustomerDTO;
import com.hafidelmoudden.bankerbackend.dtos.BankAccountDTO;
import com.hafidelmoudden.bankerbackend.dtos.CurrentBankAccountDTO;
import com.hafidelmoudden.bankerbackend.dtos.SavingBankAccountDTO;
import com.hafidelmoudden.bankerbackend.entities.*;
import com.hafidelmoudden.bankerbackend.enums.AccountStatus;
import com.hafidelmoudden.bankerbackend.enums.OperationType;
import com.hafidelmoudden.bankerbackend.exceptions.CustomerNotFoundException;
import com.hafidelmoudden.bankerbackend.repositories.AccountOperationRepository;
import com.hafidelmoudden.bankerbackend.repositories.BankAccountRepository;
import com.hafidelmoudden.bankerbackend.repositories.CustomerRepository;
import com.hafidelmoudden.bankerbackend.repositories.UserRepository;
import com.hafidelmoudden.bankerbackend.services.BankAccountService;
import com.hafidelmoudden.bankerbackend.services.BankService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
public class BankerbackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankerbackendApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(BankAccountService bankAccountService, 
                                      UserRepository userRepository, 
                                      PasswordEncoder passwordEncoder,
                                      AccountOperationRepository accountOperationRepository) {
        return args -> {
            // Create admin user if it doesn't exist
            if (!userRepository.existsByUsername("admin")) {
                User adminUser = new User();
                adminUser.setUsername("admin");
                adminUser.setEmail("admin@banker.com");
                adminUser.setPassword(passwordEncoder.encode("admin123"));
                adminUser.setRoles(Arrays.asList("ADMIN", "USER"));
                userRepository.save(adminUser);
                System.out.println("Admin user created");
            }
            
            // Create a regular user if it doesn't exist
            if (!userRepository.existsByUsername("user")) {
                User regularUser = new User();
                regularUser.setUsername("user");
                regularUser.setEmail("user@banker.com");
                regularUser.setPassword(passwordEncoder.encode("user123"));
                regularUser.setRoles(new ArrayList<>(Arrays.asList("USER")));
                userRepository.save(regularUser);
                System.out.println("Regular user created");
            }

            Stream.of("Hassan", "Imane", "Mohamed").forEach(name -> {
                CustomerDTO customer = new CustomerDTO();
                customer.setName(name);
                customer.setEmail(name + "@gmail.com");
                bankAccountService.saveCustomer(customer);
            });
            bankAccountService.listCustomers().forEach(customer -> {
                try {
                    bankAccountService.saveCurrentBankAccount(Math.random() * 90000, 9000, customer.getId());
                    bankAccountService.saveSavingBankAccount(Math.random() * 120000, 5.5, customer.getId());
                } catch (CustomerNotFoundException e) {
                    e.printStackTrace();
                }
            });
            List<BankAccountDTO> bankAccounts = bankAccountService.bankAccountList();
            for (BankAccountDTO bankAccount : bankAccounts) {
                for (int i = 0; i < 10; i++) {
                    String accountId;
                    if (bankAccount instanceof SavingBankAccountDTO) {
                        accountId = ((SavingBankAccountDTO) bankAccount).getId();
                    } else {
                        accountId = ((CurrentBankAccountDTO) bankAccount).getId();
                    }
                    bankAccountService.credit(accountId, 10000 + Math.random() * 120000, "Credit");
                    bankAccountService.debit(accountId, 1000 + Math.random() * 9000, "Debit");
                }
            }
            
            // Ensure we have transactions for the current year
            Calendar currentDate = Calendar.getInstance();
            int currentYear = currentDate.get(Calendar.YEAR);
            int currentMonth = currentDate.get(Calendar.MONTH);
            
            // Check if we have operations for the current year
            List<AccountOperation> currentYearOps = accountOperationRepository.findAll().stream()
                .filter(op -> {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(op.getOperationDate());
                    return cal.get(Calendar.YEAR) == currentYear;
                })
                .collect(Collectors.toList());
                
            System.out.println("Found " + currentYearOps.size() + " operations for current year " + currentYear);
            
            // If we don't have enough operations for the current year, add some
            if (currentYearOps.size() < 12) {
                System.out.println("Adding sample operations for current year charts");
                
                // Get a bank account to add operations to
                if (!bankAccounts.isEmpty()) {
                    String accountId;
                    if (bankAccounts.get(0) instanceof SavingBankAccountDTO) {
                        accountId = ((SavingBankAccountDTO) bankAccounts.get(0)).getId();
                    } else {
                        accountId = ((CurrentBankAccountDTO) bankAccounts.get(0)).getId();
                    }
                    
                    // Add some operations for each month
                    for (int month = 0; month <= currentMonth; month++) {
                        for (int i = 0; i < 3; i++) {
                            // Create a date for this month
                            Calendar cal = Calendar.getInstance();
                            cal.set(Calendar.YEAR, currentYear);
                            cal.set(Calendar.MONTH, month);
                            cal.set(Calendar.DAY_OF_MONTH, 15);
                            
                            // Add credit and debit operations
                            bankAccountService.credit(accountId, 5000 + Math.random() * 15000, 
                                "Sample Credit for " + (month + 1) + "/" + currentYear);
                            bankAccountService.debit(accountId, 1000 + Math.random() * 3000, 
                                "Sample Debit for " + (month + 1) + "/" + currentYear);
                        }
                    }
                    
                    System.out.println("Added sample operations for current year charts");
                }
            }
        };
    }

    //@Bean
    CommandLineRunner start(CustomerRepository customerRepository,
                            BankAccountRepository bankAccountRepository,
                            AccountOperationRepository accountOperationRepository) {
        return args -> {
            Stream.of("Hassan", "Yassine", "Aicha").forEach(name -> {
                Customer customer = new Customer();
                customer.setName(name);
                customer.setEmail(name + "@gmail.com");
                customerRepository.save(customer);
            });
            customerRepository.findAll().forEach(cust -> {
                CurrentAccount currentAccount = new CurrentAccount();
                currentAccount.setId(UUID.randomUUID().toString());
                currentAccount.setBalance(Math.random() * 90000);
                currentAccount.setCreatedAt(new Date());
                currentAccount.setStatus(AccountStatus.CREATED);
                currentAccount.setCustomer(cust);
                currentAccount.setOverDraft(9000);
                bankAccountRepository.save(currentAccount);

                SavingAccount savingAccount = new SavingAccount();
                savingAccount.setId(UUID.randomUUID().toString());
                savingAccount.setBalance(Math.random() * 90000);
                savingAccount.setCreatedAt(new Date());
                savingAccount.setStatus(AccountStatus.CREATED);
                savingAccount.setCustomer(cust);
                savingAccount.setInterestRate(5.5);
                bankAccountRepository.save(savingAccount);

            });
            bankAccountRepository.findAll().forEach(acc -> {
                for (int i = 0; i < 10; i++) {
                    AccountOperation accountOperation = new AccountOperation();
                    accountOperation.setOperationDate(new Date());
                    accountOperation.setAmount(Math.random() * 12000);
                    accountOperation.setType(Math.random() > 0.5 ? OperationType.DEBIT : OperationType.CREDIT);
                    accountOperation.setBankAccount(acc);
                    accountOperationRepository.save(accountOperation);
                }

            });
        };

    }

}
